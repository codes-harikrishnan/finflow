package com.harikrishnan.finflow.budget.service;

import com.harikrishnan.finflow.budget.domain.Budget;
import com.harikrishnan.finflow.budget.domain.BudgetStatus;
import com.harikrishnan.finflow.budget.dto.BudgetRequest;
import com.harikrishnan.finflow.budget.dto.BudgetResponse;
import com.harikrishnan.finflow.budget.dto.GetBudgetRequest;
import com.harikrishnan.finflow.budget.repository.BudgetRepository;
import com.harikrishnan.finflow.category.domain.Category;
import com.harikrishnan.finflow.category.repository.CategoryRepository;
import com.harikrishnan.finflow.exceptions.ConflictException;
import com.harikrishnan.finflow.exceptions.ResourceNotFoundException;
import com.harikrishnan.finflow.utils.SecurityUtils;
import jakarta.persistence.Cacheable;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetService {

    private final BudgetRepository budgetRepository;

    private final CategoryRepository categoryRepository;

    private final SecurityUtils securityUtils;

    public BudgetResponse createBudget (BudgetRequest budgetRequest) {
        log.info("Creating budget");
        Category category = categoryRepository.findById(budgetRequest.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("Unable to find a category with id:"+ budgetRequest.getCategoryId()));

            if(budgetRepository.existsByUserAndCategoryAndMonthAndYear(securityUtils.getCurrentUser(),category, budgetRequest.getMonth(), budgetRequest.getYear())) {
                throw new ConflictException("A budget already exists for this category in " + budgetRequest.getMonth() + "/" + budgetRequest.getYear());
            }

        Budget newBudget = Budget.builder()
                .year(budgetRequest.getYear())
                .month(budgetRequest.getMonth())
                .budgetStatus(BudgetStatus.ACTIVE)
                .category(category)
                .limitAmount(budgetRequest.getLimitAmount())
                .spentAmount(BigDecimal.ZERO)
                .build();

       Budget budget = budgetRepository.save(newBudget);

       return BudgetResponse.builder()
               .id(budget.getId())
               .categoryId(budget.getCategory().getId())
               .year(budget.getYear())
               .month(budget.getMonth())
               .budgetStatus(budget.getBudgetStatus())
               .limitAmount(budget.getLimitAmount())
               .spentAmount(budget.getSpentAmount())
               .build();
    }

    @Transactional(readOnly = true)
    public Page<BudgetResponse> getAllBudgets (GetBudgetRequest getBudgetRequest, Pageable pageable) {

        log.info("Get all budgets for page: {}", pageable.getPageNumber());

        Specification specification = ((root, query, criteriaBuilder) ->  {
            root.fetch("category", JoinType.LEFT);
            return criteriaBuilder.equal(root.get("user"),securityUtils.getCurrentUser());
        });

        if(getBudgetRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(getBudgetRequest.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("Unable to find a category with id:"+ getBudgetRequest.getCategoryId()));
            specification = specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("category"),category)));
        }

        if(getBudgetRequest.getBudgetStatus() != null) {
            specification = specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("budgetStatus"),getBudgetRequest.getBudgetStatus())));
        }

        if(getBudgetRequest.getYear() != null) {
            specification = specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("year"),getBudgetRequest.getYear())));
        }

        if(getBudgetRequest.getMonth() != null) {
            specification = specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("month"),getBudgetRequest.getMonth())));
        }

      Page <Budget> budgets = budgetRepository.findAll(specification,pageable);

        List<BudgetResponse> budgetResponses = budgets.getContent().stream().map(budget -> {
            return BudgetResponse.builder()
                    .month(budget.getMonth())
                    .year(budget.getYear())
                    .id(budget.getId())
                    .budgetStatus(budget.getBudgetStatus())
                    .limitAmount(budget.getLimitAmount())
                    .spentAmount(budget.getSpentAmount())
                    .categoryId(budget.getCategory().getId())
                    .build();
        }).toList();

        return new PageImpl<>(budgetResponses,pageable,budgets.getTotalElements());
    }

    @Transactional(readOnly = true)
    public BudgetResponse getBudget (Long id) {
        log.info("Get the budget with id: {}", id);

        Budget budget = budgetRepository.findByIdAndUser(id,securityUtils.getCurrentUser()).orElseThrow(() -> new ResourceNotFoundException("Unable to find budget with id:"+ id));

        return BudgetResponse.builder()
                .month(budget.getMonth())
                .year(budget.getYear())
                .id(budget.getId())
                .budgetStatus(budget.getBudgetStatus())
                .limitAmount(budget.getLimitAmount())
                .spentAmount(budget.getSpentAmount())
                .categoryId(budget.getCategory().getId())
                .build();
    }

}
