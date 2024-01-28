package com.example.kafkastreams.streams;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class Consumer {

    @KafkaListener(topics = {"hobbit"}, groupId = "spring-boot-streams")
    public void consume(ConsumerRecord<String, String> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("topic: {}, key: {} - message= {}", topic, record.key(), record.value());
    }
}