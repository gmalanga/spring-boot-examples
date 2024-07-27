package com.example.kafkareplying.controller;

import com.example.kafkareplying.exception.ProductNotFoundException;
import com.example.kafkareplying.model.Product;
import com.example.kafkareplying.repository.ProductRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = { "*" })
@RequestMapping(value = "/", produces = "application/json; charset=utf-8")
class ProductController {

    private final ProductRepository repository;

    ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/products")
    List<Product> all() {
        return repository.findAll();
    }

    @GetMapping({ "/product/{id}" })
    Product one(@PathVariable String id) throws Throwable {
        return (Product) repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }

}
