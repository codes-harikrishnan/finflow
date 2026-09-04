package com.harikrishnan.finflow.budget.dto;


import com.harikrishnan.finflow.budget.domain.BudgetStatus;
import com.harikrishnan.finflow.category.domain.Category;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetRequest {

    @NotNull
    private Long categoryId;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal limitAmount;


    private BigDecimal spentAmount;

    private BudgetStatus budgetStatus;

    @NotNull
    @Min(value = 2020)
    private Integer year;

    @Min(value = 1)
    @Max(value = 12)
    private Integer month;

}
