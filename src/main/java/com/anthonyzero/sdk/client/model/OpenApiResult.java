package com.anthonyzero.sdk.client.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : jin.ping
 * @date : 2024/2/26
 */
@Data
public class OpenApiResult implements Serializable {

    private static final long serialVersionUID = -1L;
    private boolean status;
    private Object data;
    private String errCode;
    private String errMsg;

    public OpenApiResult(Object data) {
        this.status = true;
        this.data = data;
    }

    public OpenApiResult(String errorCode, String errorMsg) {
        this.status = false;
        this.errCode = errorCode;
        this.errMsg = errorMsg;
    }

    public static OpenApiResult success(Object data) {
        return new OpenApiResult(data);
    }

    public static OpenApiResult error(String errorCode, String errorMsg) {
        return new OpenApiResult(errorCode, errorMsg);
    }
}
