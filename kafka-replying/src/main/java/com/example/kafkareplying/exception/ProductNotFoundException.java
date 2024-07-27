package com.example.kafkareplying.exception;

public class ProductNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 8087803211710068858L;

    public ProductNotFoundException(String id) {
        super("Could not find product " + id);
    }
}
