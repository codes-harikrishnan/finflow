package com.harikrishnan.finflow.budget.dto;

import com.harikrishnan.finflow.budget.domain.BudgetStatus;
import com.harikrishnan.finflow.category.domain.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class BudgetResponse implements Serializable {

    private final Long id;

    private final Long categoryId;

    private final BigDecimal limitAmount;

    private final BigDecimal spentAmount;

    private final BudgetStatus budgetStatus;

    private final Integer year;

    private final Integer month;
}
