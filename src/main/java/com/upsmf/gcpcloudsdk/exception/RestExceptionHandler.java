package com.upsmf.gcpcloudsdk.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception ex) {
        log.debug("RestExceptionHandler::handleException::" + ex);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = null;

        if (ex instanceof FileStorageException) {
            FileStorageException serviceLocatorException = (FileStorageException) ex;
            status = HttpStatus.BAD_REQUEST;
            errorResponse = ErrorResponse.builder()
                    .code(serviceLocatorException.getCode())
                    .message(serviceLocatorException.getMessage())
                    .httpStatusCode(serviceLocatorException.getHttpStatusCode() != null
                            ? serviceLocatorException.getHttpStatusCode()
                            : String.valueOf(status.value()))
                    .build();
            if (StringUtils.isNotBlank(serviceLocatorException.getMessage())) {
                log.error(serviceLocatorException.getMessage());
            }

            return new ResponseEntity<>(errorResponse, status);
        }

        errorResponse = ErrorResponse.builder()
                .code(ex.getMessage()).build();
        return new ResponseEntity<>(errorResponse, status);
    }

}
