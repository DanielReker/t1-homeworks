package io.github.danielreker.t1homeworks.service1.kafka;

import io.github.danielreker.t1homeworks.service1.model.dto.CreateTransactionRequest;
import io.github.danielreker.t1homeworks.service1.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionConsumer {
    private final TransactionService transactionService;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "t1_demo_transactions",
            containerFactory = "kafkaJsonContainerListenerFactory"
    )
    public void transactionListener(@Payload CreateTransactionRequest dto, Acknowledgment ack) {
        try {
            transactionService.create(dto);
        } finally {
            ack.acknowledge();
        }
    }
}
