package com.example.kafkareplying.controller;

import com.example.kafkareplying.exception.ProductNotFoundException;
import com.example.kafkareplying.kafka.producer.KafkaProducer;
import com.example.kafkareplying.model.Product;
import com.example.kafkareplying.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping(value = "/", produces = "application/json; charset=utf-8")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository repository;
    private final KafkaProducer kafkaProducer;

    @GetMapping("/products")
    List<Product> all() {
        return repository.findAll();
    }

    @GetMapping({"/product/{id}"})
    Product one(@PathVariable String id) throws ProductNotFoundException {
        return repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }

    @PostMapping("/product")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody final Product product) {
        kafkaProducer.sendTwo(product);
    }

    @DeleteMapping("/product/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String delete(@PathVariable String id) {
        repository.deleteById(id);
        return id;
    }

}
