package com.example.kafkastreams.streams;

import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Component
public class Producer {

    private final KafkaTemplate<String, String> template;

    @Value(value = "${spring.kafka.topics.wordcount-input}")
    private String wordCountInputTopic;

    Faker faker;

    @EventListener(ApplicationStartedEvent.class)
    public void generate() {

        faker = Faker.instance();
        final Flux<Long> interval = Flux.interval(Duration.ofMillis(1_000));

        final Flux<String> quotes = Flux.fromStream(Stream.generate(() -> faker.hobbit().quote()));

        Flux.zip(interval, quotes)
                .map(it -> template.send(
                        new ProducerRecord<>(wordCountInputTopic, String.valueOf(faker.random().nextInt(42)), it.getT2())
                ))
                .doOnError(error -> log.error("Error: ", error))
                .blockLast();
    }
}
