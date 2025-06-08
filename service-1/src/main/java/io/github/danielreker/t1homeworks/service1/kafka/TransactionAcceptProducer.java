package io.github.danielreker.t1homeworks.service1.kafka;

import io.github.danielreker.t1homeworks.service1.model.dto.TransactionAcceptDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionAcceptProducer {
    @Value("${spring.application.kafka.transaction-accept-topic}")
    private String topic;

    private final KafkaTemplate<String, TransactionAcceptDto> kafkaTemplate;


    public void sendTransactionAccept(TransactionAcceptDto dto) {
        try {
            kafkaTemplate.send(topic, dto).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            kafkaTemplate.flush();
        }
    }
}
