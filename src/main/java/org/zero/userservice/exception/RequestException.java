package org.zero.userservice.exception;

import lombok.Getter;

@Getter
public class RequestException extends RuntimeException {
    public RequestException(String message) {
        super(message);
    }
}
