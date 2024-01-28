package com.example.kafkastreams.streams;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class Consumer {

    @KafkaListener(topics = {"${spring.kafka.topics.hobbit}"}, groupId = "qwerty")
    public void consumerOne(ConsumerRecord<Integer, String> message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.debug("topic: {}, key: {} - message: {}", topic, message.key(), message.value());
    }

    @KafkaListener(topics = {"${spring.kafka.topics.wordcount-output}"}, groupId = "asdfg")
    public void consumerTwo(ConsumerRecord<Integer, String> message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.debug("topic: {}, key: {} - message: {}", topic, message.key(), message.value());
    }
}