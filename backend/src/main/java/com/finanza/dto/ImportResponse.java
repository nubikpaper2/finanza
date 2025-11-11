package com.finanza.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportResponse {
    private Integer total;
    private Integer imported;
    private Integer failed;
    private List<String> errors;
    private List<TransactionResponse> transactions;
}
