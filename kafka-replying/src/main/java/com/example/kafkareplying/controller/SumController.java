package com.example.kafkareplying.controller;

import com.example.kafkareplying.model.MyNumber;
import java.util.Arrays;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SumController {

    ReplyingKafkaTemplate<String, MyNumber, MyNumber> kafkaTemplate;

    @Value("${spring.kafka.topic.request-topic}")
    String requestTopic;

    @Value("${spring.kafka.topic.requestreply-topic}")
    String requestReplyTopic;

    @PostMapping(value = "/sum", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public MyNumber sum(@RequestBody MyNumber request) throws InterruptedException, ExecutionException {

        // create producer record
        ProducerRecord<String, MyNumber> producerRecord = new ProducerRecord<>(requestTopic, request);

        // set reply topic in header
        producerRecord.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, requestReplyTopic.getBytes()));

        // post in kafka topic
        RequestReplyFuture<String, MyNumber, MyNumber> sendAndReceive = kafkaTemplate.sendAndReceive(record);

        // confirm if producer produced successfully
        SendResult<String, MyNumber> sendResult = sendAndReceive.getSendFuture().get();

        //print all headers
        sendResult.getProducerRecord().headers().forEach(header -> log.debug("{}:{}", header.key(), Arrays.toString(header.value())));

        // get consumer record
        ConsumerRecord<String, MyNumber> consumerRecord = sendAndReceive.get();

        // return consumer value
        return consumerRecord.value();
    }

}
