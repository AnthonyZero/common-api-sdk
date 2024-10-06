package com.anthonyzero.sdk.client.exception;

/**
 * @author : jin.ping
 * @date : 2024/2/26
 */
public class OpenApiCreationException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public OpenApiCreationException(String message) {
        super(message);
    }

    public OpenApiCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
