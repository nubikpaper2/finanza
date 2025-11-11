package com.finanza.service;

import com.finanza.dto.ImportResponse;
import com.finanza.dto.ImportTransactionRequest;
import com.finanza.dto.TransactionResponse;
import com.finanza.model.*;
import com.finanza.repository.AccountRepository;
import com.finanza.repository.CategoryRepository;
import com.finanza.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ImportService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryRuleService categoryRuleService;

    private static final DateTimeFormatter[] DATE_FORMATTERS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd")
    };

    @Transactional
    public ImportResponse importCSV(MultipartFile file, Long accountId, User user) throws Exception {
        List<ImportTransactionRequest> requests = parseCSV(file);
        return processImport(requests, accountId, user);
    }

    @Transactional
    public ImportResponse importExcel(MultipartFile file, Long accountId, User user) throws Exception {
        List<ImportTransactionRequest> requests = parseExcel(file);
        return processImport(requests, accountId, user);
    }

    @Transactional
    public ImportResponse importTransactions(List<ImportTransactionRequest> requests, User user) throws Exception {
        if (requests.isEmpty()) {
            throw new RuntimeException("No hay transacciones para importar");
        }

        // Usar la cuenta de la primera transacción o la cuenta por defecto del usuario
        Long accountId = requests.get(0).getAccountId();
        if (accountId == null) {
            // Buscar la primera cuenta del usuario
            Account account = accountRepository.findByOrganization(user.getOrganization())
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No se encontró una cuenta para importar"));
            accountId = account.getId();
        }

        return processImport(requests, accountId, user);
    }

    private List<ImportTransactionRequest> parseCSV(MultipartFile file) throws Exception {
        List<ImportTransactionRequest> requests = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build().parse(reader)) {

            for (CSVRecord record : csvParser) {
                try {
                    ImportTransactionRequest request = new ImportTransactionRequest();
                    
                    // Fecha
                    request.setDate(parseDate(record.get("fecha")));
                    
                    // Descripción
                    request.setDescription(record.get("descripcion"));
                    
                    // Monto
                    String amountStr = record.get("monto").replace(",", ".");
                    request.setAmount(new BigDecimal(amountStr));
                    
                    // Tipo (INCOME o EXPENSE)
                    String type = record.get("tipo").toUpperCase();
                    request.setType(type);
                    
                    // Categoría (opcional)
                    if (csvParser.getHeaderMap().containsKey("categoria")) {
                        request.setCategory(record.get("categoria"));
                    }
                    
                    // Notas (opcional)
                    if (csvParser.getHeaderMap().containsKey("notas")) {
                        request.setNotes(record.get("notas"));
                    }

                    requests.add(request);
                } catch (Exception e) {
                    // Ignorar filas con errores
                    System.err.println("Error parseando fila: " + e.getMessage());
                }
            }
        }

        return requests;
    }

    private List<ImportTransactionRequest> parseExcel(MultipartFile file) throws Exception {
        List<ImportTransactionRequest> requests = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            // Leer encabezados
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> columnMap = new HashMap<>();
            for (Cell cell : headerRow) {
                String header = cell.getStringCellValue().toLowerCase();
                columnMap.put(header, cell.getColumnIndex());
            }

            // Leer datos
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    ImportTransactionRequest request = new ImportTransactionRequest();
                    
                    // Fecha
                    Cell dateCell = row.getCell(columnMap.get("fecha"));
                    if (dateCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(dateCell)) {
                        request.setDate(dateCell.getLocalDateTimeCellValue().toLocalDate());
                    } else {
                        request.setDate(parseDate(dateCell.getStringCellValue()));
                    }
                    
                    // Descripción
                    request.setDescription(getCellValueAsString(row.getCell(columnMap.get("descripcion"))));
                    
                    // Monto
                    Cell amountCell = row.getCell(columnMap.get("monto"));
                    BigDecimal amount;
                    if (amountCell.getCellType() == CellType.NUMERIC) {
                        amount = BigDecimal.valueOf(amountCell.getNumericCellValue());
                    } else {
                        String amountStr = amountCell.getStringCellValue().replace(",", ".");
                        amount = new BigDecimal(amountStr);
                    }
                    request.setAmount(amount);
                    
                    // Tipo
                    String type = getCellValueAsString(row.getCell(columnMap.get("tipo"))).toUpperCase();
                    request.setType(type);
                    
                    // Categoría (opcional)
                    if (columnMap.containsKey("categoria")) {
                        request.setCategory(getCellValueAsString(row.getCell(columnMap.get("categoria"))));
                    }
                    
                    // Notas (opcional)
                    if (columnMap.containsKey("notas")) {
                        request.setNotes(getCellValueAsString(row.getCell(columnMap.get("notas"))));
                    }

                    requests.add(request);
                } catch (Exception e) {
                    // Ignorar filas con errores
                    System.err.println("Error parseando fila " + i + ": " + e.getMessage());
                }
            }
        }

        return requests;
    }

    private ImportResponse processImport(List<ImportTransactionRequest> requests, Long accountId, User user) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        if (!account.getOrganization().equals(user.getOrganization())) {
            throw new RuntimeException("No tienes permiso para esta cuenta");
        }

        ImportResponse response = new ImportResponse();
        response.setTotal(requests.size());
        response.setImported(0);
        response.setFailed(0);
        response.setErrors(new ArrayList<>());
        response.setTransactions(new ArrayList<>());

        for (ImportTransactionRequest request : requests) {
            try {
                Transaction transaction = new Transaction();
                transaction.setTransactionDate(request.getDate());
                transaction.setDescription(request.getDescription());
                transaction.setAmount(request.getAmount());
                transaction.setNotes(request.getNotes());
                transaction.setAccount(account);
                transaction.setOrganization(user.getOrganization());
                transaction.setCreatedBy(user);

                // Tipo de transacción
                if ("INCOME".equalsIgnoreCase(request.getType())) {
                    transaction.setType(Transaction.TransactionType.INCOME);
                } else if ("EXPENSE".equalsIgnoreCase(request.getType())) {
                    transaction.setType(Transaction.TransactionType.EXPENSE);
                } else {
                    throw new RuntimeException("Tipo de transacción inválido: " + request.getType());
                }

                // Categoría: primero buscar por nombre, luego por reglas
                Category category = null;
                if (request.getCategory() != null && !request.getCategory().trim().isEmpty()) {
                    category = findCategoryByName(request.getCategory(), user.getOrganization(), transaction.getType());
                }
                
                if (category == null) {
                    // Intentar auto-categorizar usando reglas
                    category = categoryRuleService.findCategoryByRules(
                            request.getDescription(), request.getAmount(), user.getOrganization());
                }

                transaction.setCategory(category);

                // Guardar transacción
                transaction = transactionRepository.save(transaction);

                // Agregar a respuesta
                response.setImported(response.getImported() + 1);
                response.getTransactions().add(toTransactionResponse(transaction));

            } catch (Exception e) {
                response.setFailed(response.getFailed() + 1);
                response.getErrors().add("Error en línea " + (requests.indexOf(request) + 1) + ": " + e.getMessage());
            }
        }

        return response;
    }

    private Category findCategoryByName(String name, Organization organization, Transaction.TransactionType type) {
        Category.TransactionType categoryType = type == Transaction.TransactionType.INCOME 
                ? Category.TransactionType.INCOME 
                : Category.TransactionType.EXPENSE;

        return categoryRepository.findByNameAndOrganization(name, organization)
                .stream()
                .filter(c -> c.getType() == categoryType)
                .findFirst()
                .orElse(null);
    }

    private LocalDate parseDate(String dateStr) {
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException e) {
                // Intentar con el siguiente formato
            }
        }
        throw new RuntimeException("No se pudo parsear la fecha: " + dateStr);
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private TransactionResponse toTransactionResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setType(transaction.getType());
        response.setAmount(transaction.getAmount());
        response.setTransactionDate(transaction.getTransactionDate());
        response.setDescription(transaction.getDescription());
        response.setNotes(transaction.getNotes());
        response.setAccountId(transaction.getAccount().getId());
        response.setAccountName(transaction.getAccount().getName());
        
        if (transaction.getCategory() != null) {
            response.setCategoryId(transaction.getCategory().getId());
            response.setCategoryName(transaction.getCategory().getName());
        }
        
        response.setCreatedAt(transaction.getCreatedAt());
        response.setUpdatedAt(transaction.getUpdatedAt());
        
        return response;
    }
}
