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

    @KafkaListener(topics = {"hobbit"}, groupId = "demo-hobbit")
    public void consumerOne(ConsumerRecord<Integer, String> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.debug("topic: {}, key: {} - message: {}", topic, record.key(), record.value());
    }

    @KafkaListener(topics = {"wordcount-output"}, groupId = "demo-wordcount")
    public void consumerTwo(ConsumerRecord<Integer, String> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.debug("topic: {}, key: {} - message: {}", topic, record.key(), record.value());
    }
}