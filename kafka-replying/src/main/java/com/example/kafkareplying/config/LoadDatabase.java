package com.example.kafkareplying.config;

import com.example.kafkareplying.model.Product;
import com.example.kafkareplying.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class LoadDatabase {

    @Bean
    CommandLineRunner initDatabase(ProductRepository repository) {
        return args -> {
            log.info("Preloading {}", repository.save(
                    new Product("1", "Burger", "Aussie with avocade, egg, bacon and beetroot", "v2", "CREATED")));
            log.info("Preloading {}", repository.save(
                    new Product("2", "Chips", "Sweet potato fries", "v1", "UPDATED")));
        };
    }
}
