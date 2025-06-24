package io.github.danielreker.t1homeworks.service1.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.utils.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class MetricsProducer<T> {
    @Value("${spring.application.kafka.metrics-topic}")
    private String topic;

    private final KafkaTemplate<String, T> kafkaTemplate;


    public void sendMetricsError(T metricsErrorDto, String errorType) throws Exception {
        try {
            var header = new RecordHeader("errorType", Utils.utf8(errorType));
            ProducerRecord<String, T> record = new ProducerRecord<>(topic, metricsErrorDto);
            record.headers().add(header);
            kafkaTemplate.send(record).get();
        } finally {
            kafkaTemplate.flush();
        }
    }
}
