package com.example.kafkastreams.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafkaStreams
public class KafkaConfig {

    @Bean
    NewTopic hobbit() {
        return TopicBuilder.name("hobbit").partitions(15).replicas(3).build();
    }

    @Bean
    NewTopic counts() {
        return TopicBuilder.name("streams-wordcount-output").partitions(6).replicas(3).build();
    }
}
