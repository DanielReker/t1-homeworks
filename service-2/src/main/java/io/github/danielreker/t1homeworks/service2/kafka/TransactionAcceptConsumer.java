package io.github.danielreker.t1homeworks.service2.kafka;

import io.github.danielreker.t1homeworks.service2.model.dto.TransactionAcceptDto;
import io.github.danielreker.t1homeworks.service2.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TransactionAcceptConsumer {
    private final TransactionService service;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "t1_demo_transaction_accept",
            containerFactory = "kafkaJsonContainerListenerFactory"
    )
    public void transactionAcceptListener(@Payload TransactionAcceptDto dto, Acknowledgment ack) {
        try {
            service.processTransaction(dto);
        } finally {
            ack.acknowledge();
        }
    }
}
