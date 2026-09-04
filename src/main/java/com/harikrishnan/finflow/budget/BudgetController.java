package com.harikrishnan.finflow.budget;
import com.harikrishnan.finflow.budget.domain.BudgetStatus;
import com.harikrishnan.finflow.budget.dto.BudgetRequest;
import com.harikrishnan.finflow.budget.dto.BudgetResponse;
import com.harikrishnan.finflow.budget.dto.GetBudgetRequest;
import com.harikrishnan.finflow.budget.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/budget")
@RequiredArgsConstructor
@Slf4j
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(@Valid @RequestBody BudgetRequest budgetRequest) {
        log.info("Received endpoint request to create a budget");
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetService.createBudget(budgetRequest));
    }

    @GetMapping
    public ResponseEntity<Page<BudgetResponse>> getBudgets (@RequestParam(required = false) Integer month, @RequestParam(required = false) Integer year, @RequestParam(required = false)BudgetStatus budgetStatus, @RequestParam(required = false) Long categoryId, @RequestParam(defaultValue = "0") Integer pageNumber, @RequestParam(defaultValue = "10") Integer limit) {
        log.info("Received endpoint request to get budgets for page: {}", pageNumber);
        Pageable pageable = PageRequest.of(pageNumber,limit);
        GetBudgetRequest getBudgetRequest = GetBudgetRequest.builder()
                .categoryId(categoryId)
                .budgetStatus(budgetStatus)
                .month(month)
                .year(year)
                .build();
        return  ResponseEntity.status(HttpStatus.OK).body(budgetService.getAllBudgets(getBudgetRequest,pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponse> getBudgetById(@PathVariable Long id) {
        log.info("Received endpoint request to get budget with id: {}", id);
        return  ResponseEntity.status(HttpStatus.OK).body(budgetService.getBudget(id));
    }

}
