package com.example.springKafkaTopicsJoin.streams;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class KafkaConsumer {

    @KafkaListener(topics = "${spring.kafka.topic.input}")
    public void listner(@Payload String record) {
        if (record != null && !record.isEmpty()) {
            try {
                //DO ADDITIONAL PROCESSING WITH THIS FILTERED STREAM OF TEXAS SALES. FOR NOW JUST PRINTING IT OUT
                log.info("Message received from topic => {} ", record);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }
}
