package com.anthonyzero.sdk.client.utils;

import com.anthonyzero.sdk.client.model.SignatureMethodEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author : jin.ping
 * @date : 2024/2/26
 */
public class SignatureUtil {

    public SignatureUtil() {

    }

    /**
     * 计算签名
     *
     * @param method
     * @param secret
     * @param params
     * @return
     */
    public static String sign(SignatureMethodEnum method, String secret, Map<String, String> params) {
        String content = getSignContent(params);
        switch (method) {
            case HMACMD5:
            case HMACSHA1:
            case HMACSHA256:
                return HmacSHA256Util.hmacSHA256(secret, content);
            default:
                throw new IllegalArgumentException("sign method is error");
        }
    }

    /**
     * 计算签名
     * @param method
     * @param secret
     * @param content
     * @return
     */
    public static String sign(SignatureMethodEnum method, String secret, String content) {
        switch (method) {
            case HMACMD5:
            case HMACSHA1:
            case HMACSHA256:
                return HmacSHA256Util.hmacSHA256(secret, content);
            default:
                throw new IllegalArgumentException("sign method is error");
        }
    }


    /**
     * 计算签名content
     *
     * @param params
     * @return
     */
    private static String getSignContent(Map<String, String> params) {
        StringBuilder content = new StringBuilder();
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        int index = 0;
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            if (areNotEmpty(key, value)) {
                content.append((index == 0 ? "" : "&") + key + "=" + value);
                index++;
            }
        }
        return content.toString();
    }

    /**
     * 检查指定的字符串列表是否不为空。
     */
    private static boolean areNotEmpty(String... values) {
        boolean result = true;
        if (values == null || values.length == 0) {
            result = false;
        } else {
            for (String value : values) {
                result &= !isEmpty(value);
            }
        }
        return result;
    }

    /**
     * 字符串为空
     *
     * @param value
     * @return
     */
    private static boolean isEmpty(String value) {
        return StringUtils.isEmpty(value);
    }
}
