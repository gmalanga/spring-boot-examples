package com.example.kafkareplying.config;

import com.example.kafkareplying.model.MyNumber;
import com.example.kafkareplying.model.Product;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.topic.requestreply-topic}")
    private String requestReplyTopic;
    @Value("${spring.kafka.consumergroup}")
    private String consumerGroup;

    @Bean
    public ConsumerFactory<String, Product> productConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "products_group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(Product.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Product> concurrentKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, Product> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(productConsumerFactory());

        return factory;
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        // list of host:port pairs used for establishing the initial connections to the Kakfa cluster
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "helloworld");
        return props;
    }

    @Bean
    public ProducerFactory<String, MyNumber> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, MyNumber> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ReplyingKafkaTemplate<String, MyNumber, MyNumber> replyKafkaTemplate(ProducerFactory<String, MyNumber> pf,
                                                                                KafkaMessageListenerContainer<String,
                                                                                MyNumber> container) {
        return new ReplyingKafkaTemplate<>(pf, container);
    }

    @Bean
    public KafkaMessageListenerContainer<String, MyNumber> replyContainer(ConsumerFactory<String, MyNumber> cf) {
        ContainerProperties containerProperties = new ContainerProperties(requestReplyTopic);
        return new KafkaMessageListenerContainer<>(cf, containerProperties);
    }

    @Bean
    public ConsumerFactory<String, MyNumber> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(), new JsonDeserializer<>(MyNumber.class));
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, MyNumber>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MyNumber> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setReplyTemplate(kafkaTemplate());
        return factory;
    }
}
