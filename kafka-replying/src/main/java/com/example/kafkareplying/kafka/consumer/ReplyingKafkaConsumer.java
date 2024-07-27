package com.example.kafkareplying.kafka.consumer;

import com.example.kafkareplying.model.MyNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReplyingKafkaConsumer {

    @KafkaListener(topics = "${spring.kafka.topic.request-topic}")
    @SendTo
    public MyNumber listen(MyNumber request) {

        int sum = request.getFirstNumber() + request.getSecondNumber();
        request.setAdditionalProperty("sum", sum);
        return request;
    }

}
