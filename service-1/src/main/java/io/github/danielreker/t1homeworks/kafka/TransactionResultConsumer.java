package io.github.danielreker.t1homeworks.kafka;

import io.github.danielreker.t1homeworks.model.dto.TransactionResultDto;
import io.github.danielreker.t1homeworks.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TransactionResultConsumer {
    private final TransactionService transactionService;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "t1_demo_transaction_result",
            containerFactory = "kafkaJsonContainerListenerFactory"
    )
    public void transactionResultListener(TransactionResultDto dto, Acknowledgment ack) {
        try {
            transactionService.processResult(dto);
        } finally {
            ack.acknowledge();
        }
    }
}
