package com.harikrishnan.finflow.transaction.dto;

import com.harikrishnan.finflow.transaction.domain.TransactionType;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetTransactionRequest {


    private Long accountId;

    private Long categoryId;

    private TransactionType type;

    private LocalDate fromDate;

    private LocalDate toDate;
}
