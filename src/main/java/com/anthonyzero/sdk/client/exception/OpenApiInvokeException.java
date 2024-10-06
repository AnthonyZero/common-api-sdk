package com.anthonyzero.sdk.client.exception;

/**
 * @author : jin.ping
 * @date : 2024/2/26
 */
public class OpenApiInvokeException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public OpenApiInvokeException(String message) {
        super(message);
    }

    public OpenApiInvokeException(String message, Throwable cause) {
        super(message, cause);
    }
}
