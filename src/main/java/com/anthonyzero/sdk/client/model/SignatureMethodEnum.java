package com.anthonyzero.sdk.client.model;

import lombok.Getter;

/**
 * @author : jin.ping
 * @date : 2024/2/26
 */
@Getter
public enum SignatureMethodEnum {
    HMACMD5("HmacMD5"),
    HMACSHA1("HmacSHA1"),
    HMACSHA256("HmacSHA256");

    private String method;

    SignatureMethodEnum(String method) {
        this.method = method;
    }
}
