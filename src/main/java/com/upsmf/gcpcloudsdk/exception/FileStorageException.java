package com.upsmf.gcpcloudsdk.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Setter
@Component
public class FileStorageException extends RuntimeException {
    private String code;
    private String message;
    private String httpStatusCode;
    private Map<String, String> errors;

    public FileStorageException() {
    }

    public FileStorageException(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public FileStorageException(String code, String message, String httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }

    public FileStorageException(Map<String, String> errors) {
        this.message = errors.toString();
        this.errors = errors;
    }
}
