package com.finanza.dto;

import com.finanza.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private Transaction.TransactionType type;
    private BigDecimal amount;
    private String currency;
    private BigDecimal exchangeRate;
    private BigDecimal amountInLocalCurrency;
    private LocalDate transactionDate;
    private String description;
    private String notes;
    
    private Long accountId;
    private String accountName;
    
    private Long categoryId;
    private String categoryName;
    
    private Long destinationAccountId;
    private String destinationAccountName;
    
    private Long creditCardId;
    private String creditCardName;
    private Integer installments;
    
    private java.util.List<ArgentinaTaxResponse> taxes;
    private java.util.List<InstallmentResponse> installmentDetails;
    
    private Set<String> tags;
    private Set<String> attachments;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TransactionResponse fromEntity(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setType(transaction.getType());
        response.setAmount(transaction.getAmount());
        response.setCurrency(transaction.getCurrency());
        response.setExchangeRate(transaction.getExchangeRate());
        response.setAmountInLocalCurrency(transaction.getAmountInLocalCurrency());
        response.setTransactionDate(transaction.getTransactionDate());
        response.setDescription(transaction.getDescription());
        response.setNotes(transaction.getNotes());
        
        if (transaction.getAccount() != null) {
            response.setAccountId(transaction.getAccount().getId());
            response.setAccountName(transaction.getAccount().getName());
        }
        
        if (transaction.getCategory() != null) {
            response.setCategoryId(transaction.getCategory().getId());
            response.setCategoryName(transaction.getCategory().getName());
        }
        
        if (transaction.getDestinationAccount() != null) {
            response.setDestinationAccountId(transaction.getDestinationAccount().getId());
            response.setDestinationAccountName(transaction.getDestinationAccount().getName());
        }
        
        if (transaction.getCreditCard() != null) {
            response.setCreditCardId(transaction.getCreditCard().getId());
            response.setCreditCardName(transaction.getCreditCard().getName());
        }
        
        response.setInstallments(transaction.getInstallments());
        
        response.setTags(transaction.getTags());
        response.setAttachments(transaction.getAttachments());
        response.setCreatedAt(transaction.getCreatedAt());
        response.setUpdatedAt(transaction.getUpdatedAt());
        
        return response;
    }
}
