package com.example.kafkastreams.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafkaStreams
public class KafkaConfig {

    @Value(value = "${spring.kafka.topics.hobbit}")
    private String hobbitTopic;

    @Value(value = "${spring.kafka.topics.wordcount-output}")
    private String wordCountOutputTopic;

    @Bean
    NewTopic hobbit() {

        return TopicBuilder
                .name(hobbitTopic)
                .partitions(15)
                .replicas(3)
                .build();
    }

    @Bean
    NewTopic counts() {

        return TopicBuilder
                .name(wordCountOutputTopic)
                .partitions(6)
                .replicas(3)
                .build();
    }
}
