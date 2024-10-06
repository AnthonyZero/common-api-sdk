package com.anthonyzero.sdk.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author : jin.ping
 * @date : 2024/2/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> implements Serializable {

    private Integer code = 0;
    private String message = "成功";
    private T data;

    public static <T> Response<T> ok(String message) {
        return new Response<>(0, message, null);
    }

    public static <T> Response<T> ok() {
        return new Response<>(0, "操作成功", null);
    }

    public static <T> Response<T> data(T data) {
        return new Response<>(0, "操作成功", data);
    }

    public static <T> Response<T> error(Integer code, String message) {
        return new Response<>(code, message, null);
    }

    public static <T> Response<T> error(String message) {
        return new Response<>(500, message, null);
    }

    public static <T> Response<T> error() {
        return new Response<>(500, "操作失败", null);
    }
}
