package com.example.kafkastreams;

import com.example.kafkastreams.streams.Processor;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;

import java.util.Properties;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@AllArgsConstructor
class ProcessorTest {

    private final String wordCountOutputTopic;

    private final String wordCountInputTopic;

    private Processor wordCountProcessor;

    @BeforeEach
    void setUp() {
        wordCountProcessor = new Processor();
    }

    @Test
    void givenInputMessages_whenProcessed_thenWordCountIsProduced() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        wordCountProcessor.process(streamsBuilder);
        Topology topology = streamsBuilder.build();

        try (TopologyTestDriver topologyTestDriver = new TopologyTestDriver(topology, new Properties())) {

            TestInputTopic<String, String> inputTopic = topologyTestDriver
                    .createInputTopic(wordCountInputTopic, new StringSerializer(), new StringSerializer());

            TestOutputTopic<String, Long> outputTopic = topologyTestDriver
                    .createOutputTopic(wordCountOutputTopic, new StringDeserializer(), new LongDeserializer());

            inputTopic.pipeInput("key1", "hello world");
            inputTopic.pipeInput("key2", "hello");
            inputTopic.pipeInput("key3", "hello world");

            assertThat(outputTopic.readKeyValuesToList())
                    .containsExactly(
                            KeyValue.pair("hello", 1L),
                            KeyValue.pair("world", 1L),
                            KeyValue.pair("hello", 2L),
                            KeyValue.pair("hello", 3L),
                            KeyValue.pair("world", 2L)
                    );
        }
    }
}
