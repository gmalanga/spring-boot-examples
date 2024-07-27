package com.example.kafkareplying.listener;

import com.example.kafkareplying.model.Product;
import com.example.kafkareplying.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductEventListener {

    private final ProductRepository repository;

    @KafkaListener(topics = "products")
    public void listen(Product product) throws Exception {
        log.info("received product event: {}", product);

        repository.save(product);
    }
}
