package io.github.danielreker.t1homeworks.kafka;

import io.github.danielreker.t1homeworks.model.dto.CreateTransactionRequest;
import io.github.danielreker.t1homeworks.service.TransactionService;
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
            id = "${spring.kafka.consumer.group-id}",
            topics = "t1_demo_transactions",
            containerFactory = "kafkaJsonContainerListenerFactory"
    )
    public void transactionListener(@Payload CreateTransactionRequest dto, Acknowledgment ack) {
        transactionService.create(dto);
        ack.acknowledge();
    }
}
