package com.harikrishnan.finflow.transaction.event;

import com.harikrishnan.finflow.transaction.dto.TransactionRecordedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionEventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishTransactionRecorded(TransactionRecordedEvent transactionRecordedEvent) {
        try {
            log.info("Publishing TransactionRecordedEvent for transaction: {}",transactionRecordedEvent.getTransactionId());
            String json = objectMapper.writeValueAsString(transactionRecordedEvent);
            kafkaTemplate.send("transaction-recorded",transactionRecordedEvent.getTransactionId().toString(),json);
        } catch (Exception e) {
            log.error("Failed to publish TransactionRecordedEvent: {}", e.getMessage());
        }
        }
}
