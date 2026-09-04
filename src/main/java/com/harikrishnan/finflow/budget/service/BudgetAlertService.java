package com.harikrishnan.finflow.budget.service;

import com.harikrishnan.finflow.budget.domain.BudgetAlert;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class BudgetAlertService {

    @Async("taskExecutor")
    public CompletableFuture<Void> sendAlertToUserExceedingBudgetLimit (String emailId, Long budgetId, String categoryName, BigDecimal limitAmount, Map<String, String> mdc) {
        if(mdc !=  null) MDC.setContextMap(mdc);
        try {
            log.info("Sending budget exceeded alert to {} for budget id: {}",
                    emailId, budgetId);
            Thread.sleep(1000);
            log.info("Email sent to : {}", emailId);
            return CompletableFuture.completedFuture(null);
        }
        catch (Exception exception) {
            log.info("Unable to sent Email to : {}", emailId);
            return CompletableFuture.failedFuture(exception);
        }
       finally {
            MDC.clear();
        }
    }

}
