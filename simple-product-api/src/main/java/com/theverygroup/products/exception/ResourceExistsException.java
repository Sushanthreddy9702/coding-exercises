package com.theverygroup.products.exception;

public class ResourceExistsException extends RuntimeException {
    public ResourceExistsException(String msg) {
        super(msg);
    }
}
