package com.harikrishnan.finflow.transaction.event;

import com.harikrishnan.finflow.transaction.dto.TransactionRecordedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionEventConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "transaction-recorded", groupId = "finflow-group")
    public void handleTransactionEventRecorded (String message) {
        try {
            TransactionRecordedEvent event = objectMapper.readValue(
                    message, TransactionRecordedEvent.class);
            log.info("Received TransactionRecordedEvent — transactionId: {}, " +
                            "userId: {}, amount: {}, type: {}",
                    event.getTransactionId(),
                    event.getUserId(),
                    event.getAmount(),
                    event.getTransactionType());
            log.info("Analytics processed for transaction: {}",
                    event.getTransactionId());
        } catch (Exception e) {
            log.error("Failed to process TransactionRecordedEvent: {}",
                    e.getMessage());
        }
    }

}
