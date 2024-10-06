package com.anthonyzero.sdk.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author : jin.ping
 * @date : 2024/2/26
 */
@Data
@ConfigurationProperties(prefix = OpenApiProperties.PREFIX)
public class OpenApiProperties {

    public static final String PREFIX = "common.open-api";
    private String appId;
    private String appSecret;
    private String openapiAddress;
    private int maxTotalConnect;
    private int maxConnectPerRoute;
    private int connectTimeout = 30;
    private int readTimeout = 30;
    private int writeTimeout = 30;
}
