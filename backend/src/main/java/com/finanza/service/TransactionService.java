package com.finanza.service;

import com.finanza.dto.TransactionRequest;
import com.finanza.dto.TransactionResponse;
import com.finanza.dto.TransferRequest;
import com.finanza.model.*;
import com.finanza.repository.AccountRepository;
import com.finanza.repository.CategoryRepository;
import com.finanza.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getAllTransactions(Organization organization, Pageable pageable) {
        log.debug("Obteniendo todas las transacciones para la organización: {}", organization.getId());
        return transactionRepository.findByOrganization(organization, pageable)
                .map(TransactionResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getTransactionsByFilters(
            Organization organization,
            LocalDate startDate,
            LocalDate endDate,
            Long categoryId,
            Transaction.TransactionType type,
            Pageable pageable) {
        
        log.debug("Obteniendo transacciones con filtros - Org: {}, Fechas: {} a {}, Categoría: {}, Tipo: {}", 
                  organization.getId(), startDate, endDate, categoryId, type);
        
        return transactionRepository.findByFilters(organization, startDate, endDate, categoryId, type, pageable)
                .map(TransactionResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(Long id, Organization organization) {
        log.debug("Obteniendo transacción con id: {}", id);
        Transaction transaction = transactionRepository.findByIdAndOrganization(id, organization)
                .orElseThrow(() -> new RuntimeException("Transacción no encontrada"));
        return TransactionResponse.fromEntity(transaction);
    }

    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request, Organization organization, User user) {
        log.debug("Creando nueva transacción de tipo: {} por monto: {}", request.getType(), request.getAmount());

        // Validar cuenta
        Account account = accountRepository.findByIdAndOrganization(request.getAccountId(), organization)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        if (!account.getActive()) {
            throw new RuntimeException("La cuenta está inactiva");
        }

        // Validar categoría si se proporciona
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findByIdAndOrganization(request.getCategoryId(), organization)
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        }

        // Validar que no sea transferencia (usar método específico para transferencias)
        if (request.getType() == Transaction.TransactionType.TRANSFER) {
            throw new RuntimeException("Use el endpoint de transferencias para este tipo de transacción");
        }

        // Crear transacción
        Transaction transaction = new Transaction();
        transaction.setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setDescription(request.getDescription());
        transaction.setNotes(request.getNotes());
        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setOrganization(organization);
        transaction.setCreatedBy(user);
        
        if (request.getTags() != null) {
            transaction.getTags().addAll(request.getTags());
        }
        
        if (request.getAttachments() != null) {
            transaction.getAttachments().addAll(request.getAttachments());
        }

        // Actualizar saldo de la cuenta
        updateAccountBalance(account, transaction.getType(), request.getAmount());

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transacción creada exitosamente con id: {}. Nuevo saldo de cuenta {}: {}", 
                 savedTransaction.getId(), account.getName(), account.getBalance());
        
        return TransactionResponse.fromEntity(savedTransaction);
    }

    @Transactional
    public TransactionResponse updateTransaction(Long id, TransactionRequest request, Organization organization) {
        log.debug("Actualizando transacción con id: {}", id);
        
        Transaction transaction = transactionRepository.findByIdAndOrganization(id, organization)
                .orElseThrow(() -> new RuntimeException("Transacción no encontrada"));

        // Si cambió el monto o tipo, revertir el efecto anterior
        if (!transaction.getAmount().equals(request.getAmount()) || 
            !transaction.getType().equals(request.getType())) {
            
            // Revertir efecto anterior
            revertAccountBalance(transaction.getAccount(), transaction.getType(), transaction.getAmount());
            
            // Aplicar nuevo efecto
            updateAccountBalance(transaction.getAccount(), request.getType(), request.getAmount());
        }

        // Si cambió de cuenta
        if (!transaction.getAccount().getId().equals(request.getAccountId())) {
            // Revertir en cuenta anterior
            revertAccountBalance(transaction.getAccount(), transaction.getType(), transaction.getAmount());
            
            // Obtener nueva cuenta
            Account newAccount = accountRepository.findByIdAndOrganization(request.getAccountId(), organization)
                    .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
            
            // Aplicar en nueva cuenta
            updateAccountBalance(newAccount, request.getType(), request.getAmount());
            transaction.setAccount(newAccount);
        }

        // Actualizar categoría si cambió
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findByIdAndOrganization(request.getCategoryId(), organization)
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            transaction.setCategory(category);
        } else {
            transaction.setCategory(null);
        }

        transaction.setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setDescription(request.getDescription());
        transaction.setNotes(request.getNotes());
        
        transaction.getTags().clear();
        if (request.getTags() != null) {
            transaction.getTags().addAll(request.getTags());
        }
        
        transaction.getAttachments().clear();
        if (request.getAttachments() != null) {
            transaction.getAttachments().addAll(request.getAttachments());
        }

        Transaction updatedTransaction = transactionRepository.save(transaction);
        log.info("Transacción actualizada exitosamente con id: {}", updatedTransaction.getId());
        
        return TransactionResponse.fromEntity(updatedTransaction);
    }

    @Transactional
    public void deleteTransaction(Long id, Organization organization) {
        log.debug("Eliminando transacción con id: {}", id);
        
        Transaction transaction = transactionRepository.findByIdAndOrganization(id, organization)
                .orElseThrow(() -> new RuntimeException("Transacción no encontrada"));

        // Revertir el efecto en el saldo de la cuenta
        revertAccountBalance(transaction.getAccount(), transaction.getType(), transaction.getAmount());
        
        // Si es transferencia, revertir también en cuenta destino
        if (transaction.getType() == Transaction.TransactionType.TRANSFER && 
            transaction.getDestinationAccount() != null) {
            revertAccountBalance(transaction.getDestinationAccount(), Transaction.TransactionType.INCOME, transaction.getAmount());
        }

        transactionRepository.delete(transaction);
        log.info("Transacción eliminada exitosamente con id: {}", id);
    }

    @Transactional
    public TransactionResponse createTransfer(TransferRequest request, Organization organization, User user) {
        log.debug("Creando transferencia de cuenta {} a cuenta {} por monto: {}", 
                  request.getFromAccountId(), request.getToAccountId(), request.getAmount());

        // Validar que no sean la misma cuenta
        if (request.getFromAccountId().equals(request.getToAccountId())) {
            throw new RuntimeException("No puede transferir a la misma cuenta");
        }

        // Validar cuentas
        Account fromAccount = accountRepository.findByIdAndOrganization(request.getFromAccountId(), organization)
                .orElseThrow(() -> new RuntimeException("Cuenta origen no encontrada"));
        
        Account toAccount = accountRepository.findByIdAndOrganization(request.getToAccountId(), organization)
                .orElseThrow(() -> new RuntimeException("Cuenta destino no encontrada"));

        if (!fromAccount.getActive() || !toAccount.getActive()) {
            throw new RuntimeException("Una de las cuentas está inactiva");
        }

        // Validar saldo suficiente
        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Saldo insuficiente en la cuenta origen");
        }

        // Crear transacción de transferencia
        Transaction transaction = new Transaction();
        transaction.setType(Transaction.TransactionType.TRANSFER);
        transaction.setAmount(request.getAmount());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setDescription(request.getDescription() != null ? 
                request.getDescription() : 
                "Transferencia de " + fromAccount.getName() + " a " + toAccount.getName());
        transaction.setNotes(request.getNotes());
        transaction.setAccount(fromAccount);
        transaction.setDestinationAccount(toAccount);
        transaction.setOrganization(organization);
        transaction.setCreatedBy(user);

        // Actualizar saldos
        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));
        
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transferencia creada exitosamente con id: {}. Saldo origen: {}, Saldo destino: {}", 
                 savedTransaction.getId(), fromAccount.getBalance(), toAccount.getBalance());
        
        return TransactionResponse.fromEntity(savedTransaction);
    }

    private void updateAccountBalance(Account account, Transaction.TransactionType type, BigDecimal amount) {
        BigDecimal currentBalance = account.getBalance();
        
        switch (type) {
            case INCOME:
                account.setBalance(currentBalance.add(amount));
                break;
            case EXPENSE:
                account.setBalance(currentBalance.subtract(amount));
                break;
            case TRANSFER:
                // Las transferencias se manejan en createTransfer
                break;
        }
        
        accountRepository.save(account);
    }

    private void revertAccountBalance(Account account, Transaction.TransactionType type, BigDecimal amount) {
        BigDecimal currentBalance = account.getBalance();
        
        switch (type) {
            case INCOME:
                account.setBalance(currentBalance.subtract(amount));
                break;
            case EXPENSE:
                account.setBalance(currentBalance.add(amount));
                break;
            case TRANSFER:
                // Las transferencias se revierten en deleteTransaction
                break;
        }
        
        accountRepository.save(account);
    }
}
