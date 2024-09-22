package com.example.kafkareplying.kafka.producer;

import com.example.kafkareplying.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProducer {

    private final KafkaTemplate<String, Product> template;
    private String topic = "products";

    public void send(Product product) {
        template.send(new ProducerRecord<>(topic, "test", product));
        log.info("Sent payload {} to topic {}", product, topic);
    }

    public void sendTwo(Product product) {
        MessageBuilder<Product> messageBuilder = MessageBuilder
                .withPayload(product)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.KEY, "test");
        template.send(messageBuilder.build());
        log.info("Sent payload {} to topic {}", product, topic);
    }
}
