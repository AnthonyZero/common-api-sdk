package com.anthonyzero.sdk.client.exception;

/**
 * @author : jin.ping
 * @date : 2024/2/26
 */
public class OpenApiHttpMethodException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public OpenApiHttpMethodException(String message) {
        super(message);
    }

    public OpenApiHttpMethodException(String message, Throwable cause) {
        super(message, cause);
    }
}
