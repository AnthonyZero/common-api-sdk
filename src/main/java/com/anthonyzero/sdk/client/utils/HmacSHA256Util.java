package com.anthonyzero.sdk.client.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author : jin.ping
 * @date : 2024/2/26
 */
@Slf4j
public class HmacSHA256Util {

    public HmacSHA256Util() {
    }

    public static byte[] hmacSHA256(byte[] key, byte[] content) {
        try {
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            hmacSha256.init(new SecretKeySpec(key, 0, key.length, "HmacSHA256"));
            return hmacSha256.doFinal(content);
        } catch (Exception e) {
            throw new RuntimeException("hmacSHA256 error");
        }
    }

    public static String byteArrayToHexString(byte[] b) {
        StringBuilder hs = new StringBuilder();

        for(int n = 0; b != null && n < b.length; ++n) {
            String stmp = Integer.toHexString(b[n] & 255);
            if (stmp.length() == 1) {
                hs.append('0');
            }

            hs.append(stmp);
        }
        return hs.toString().toLowerCase();
    }

    public static String hmacSHA256(String secret, String message) {
        String hash = "";
        Mac hmacSha256 = null;
        try {
            hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            hmacSha256.init(secretKeySpec);
            byte[] bytes = hmacSha256.doFinal(message.getBytes());
            hash = byteArrayToHexString(bytes);
        } catch (Exception e) {
            throw new RuntimeException("hmacSHA256 error");
        }
        return hash;
    }
}
