package com.harikrishnan.finflow.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionRecordedEvent implements Serializable {
    private Long transactionId;
    private Long userId;
    private Long accountId;
    private Long categoryId;
    private String transactionType;
    private BigDecimal amount;
    private LocalDate transactionDate;
}
