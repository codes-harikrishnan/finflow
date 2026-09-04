package com.harikrishnan.finflow.budget.dto;
import com.harikrishnan.finflow.budget.domain.BudgetStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetBudgetRequest {

    private Long categoryId;

    private BudgetStatus budgetStatus;

    private Integer year;

    private Integer month;
}
