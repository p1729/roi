package com.pankaj.roi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class APIClientException extends Exception {
    public APIClientException(String message) {
        super(message);
    }
}
