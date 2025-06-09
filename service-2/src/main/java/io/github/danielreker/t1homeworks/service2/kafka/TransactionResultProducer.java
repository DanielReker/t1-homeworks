package io.github.danielreker.t1homeworks.service2.kafka;

import io.github.danielreker.t1homeworks.service2.model.dto.TransactionResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TransactionResultProducer {
    @Value("${spring.application.kafka.transaction-result-topic}")
    private String topic;

    private final KafkaTemplate<String, TransactionResultDto> kafkaTemplate;

    public void sendTransactionResult(TransactionResultDto dto) {
        try {
            kafkaTemplate.send(topic, dto).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            kafkaTemplate.flush();
        }
    }

}
