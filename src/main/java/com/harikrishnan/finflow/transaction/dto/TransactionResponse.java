package com.harikrishnan.finflow.transaction.dto;
import com.harikrishnan.finflow.transaction.domain.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class TransactionResponse {

    private final Long id;

    private final Long accountId;

    private final TransactionType transactionType;

    private final BigDecimal amount;

    private final String description;

    private final Long categoryId;

    private final Long toAccountId;

    private final LocalDate date;
}
