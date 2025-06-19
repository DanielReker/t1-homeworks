package io.github.danielreker.t1homeworks.service1.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

@TestConfiguration
public class KafkaTestConfiguration {

    @Value("${spring.application.kafka.transaction-accept-topic}")
    private String transactionAcceptTopic;

    @Bean
    public NewTopic transactionAcceptTopic() {
        return TopicBuilder.name(transactionAcceptTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
}