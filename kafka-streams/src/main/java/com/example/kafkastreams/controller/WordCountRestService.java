package com.example.kafkastreams.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WordCountRestService {

    private final StreamsBuilderFactoryBean factoryBean;

    /**
     * REST service to get the number of occurrences of a word in the streaming messages
     *
     * @param word
     * @return The number of occurrences of the word
     */
    @GetMapping("/count/{word}")
    public Long getCount(@PathVariable String word) {
        final KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();

        final ReadOnlyKeyValueStore<String, Long> counts = kafkaStreams
                .store(
                        StoreQueryParameters
                                .fromNameAndType("counts", QueryableStoreTypes.keyValueStore()));
        return counts.get(word);
    }
}
