package com.example.springKafkaTopicsJoin.controller;

import com.example.springKafkaTopicsJoin.streams.KafkaProducer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Slf4j
@RestController
@AllArgsConstructor
public class WordCountRestService {

    private final StreamsBuilderFactoryBean factoryBean;

    private final KafkaProducer kafkaProducer;

    @GetMapping("/count/{word}")
    public Long getWordCount(@PathVariable String word) {
        log.info("word: {}", word);
        KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();
        ReadOnlyKeyValueStore<String, Long> counts = Objects.requireNonNull(kafkaStreams)
                .store(StoreQueryParameters.fromNameAndType("counts", QueryableStoreTypes.keyValueStore()));
        return counts.get(word);
    }

    @GetMapping("/send/{word}")
    public void getWord(@PathVariable String word) {
        log.info("word: {}", word);
        kafkaProducer.sendMessage(word);
    }

    @PostMapping("/message")
    public void addMessage(@RequestBody String message) {

        kafkaProducer.sendMessage(message);
    }
}
