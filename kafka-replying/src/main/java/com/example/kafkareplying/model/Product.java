package com.example.kafkareplying.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;


@Data
@Entity
public class Product {
    private @Id String id;
    private String name;
    private String type;
    private String version;
    private String event;

    Product() {}
    public Product(String id, String name, String type, String version, String event) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.version = version;
        this.event = event;
    }
}
