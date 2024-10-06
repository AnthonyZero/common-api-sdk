package com.anthonyzero.sdk.client.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author : jin.ping
 * @date : 2024/2/26
 */
public class Md5Util {
    private static final Logger log = LoggerFactory.getLogger(Md5Util.class);
    protected static final char[] HEXBYTES = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public Md5Util() {
    }

    protected static byte[] md5(String s) {
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(s.getBytes("UTF-8"));
            byte[] messageDigest = algorithm.digest();
            return messageDigest;
        } catch (Exception var3) {
            log.error("MD5 Error...", var3);
            return null;
        }
    }

    protected static String toHex(byte[] hash) {
        if (hash == null) {
            return null;
        } else {
            StringBuffer buf = new StringBuffer(hash.length * 2);

            for(int i = 0; i < hash.length; ++i) {
                if ((hash[i] & 255) < 16) {
                    buf.append("0");
                }

                buf.append(Long.toString((long)(hash[i] & 255), 16));
            }

            return buf.toString();
        }
    }

    public static String hash(String s) {
        try {
            String hex = toHex(md5(s));
            return hex == null ? null : new String(hex.getBytes("UTF-8"), "UTF-8");
        } catch (Exception e) {
            log.error("not supported charset...{}", e);
            return s;
        }
    }

    public static String calc(String input) {
        try {
            byte[] plainText = input.getBytes("UTF8");
            byte[] result = calc(plainText);
            return result == null ? null : byteToHex(result);
        } catch (UnsupportedEncodingException var3) {
            return null;
        }
    }

    public static byte[] calc(byte[] content) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            return messageDigest.digest(content);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String byteToHex(byte[] b) {
        int len = b.length;
        char[] s = new char[len * 2];
        int i = 0;

        for(int j = 0; i < len; ++i) {
            int c = b[i] & 255;
            s[j++] = HEXBYTES[c >> 4 & 15];
            s[j++] = HEXBYTES[c & 15];
        }

        return new String(s);
    }
}
