package com.anthonyzero.sdk.client;

import com.anthonyzero.sdk.client.config.OpenApiProperties;
import com.anthonyzero.sdk.client.config.OkHttp3Client;
import com.anthonyzero.sdk.client.exception.OpenApiCreationException;
import com.anthonyzero.sdk.client.model.*;
import com.anthonyzero.sdk.client.utils.GsonUtil;
import com.anthonyzero.sdk.client.utils.Md5Util;
import com.anthonyzero.sdk.client.utils.SignatureUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author : jin.ping
 * @date : 2024/2/27
 */
@Slf4j
public class OpenApiClient {

    private final String appId;
    private final String appSecret;
    private final String openapiAddress;
    private static final String DEFAULT_MEDIA_TYPE = "application/json; charset=utf-8";
    private static final Integer SUCCESS = 0;
    private final OkHttp3Client okHttp3Client;

    public OpenApiClient(OpenApiProperties openApiProperties) {
        this.appId = openApiProperties.getAppId();
        this.appSecret = openApiProperties.getAppSecret();
        this.openapiAddress = openApiProperties.getOpenapiAddress();
        this.okHttp3Client = new OkHttp3Client(openApiProperties.getConnectTimeout(), openApiProperties.getReadTimeout(), openApiProperties.getWriteTimeout());
    }


    public OpenApiResult invokePost(String apiUrl) {
        return this.invokePost(apiUrl, "{}");
    }

    public OpenApiResult invokePost(String apiUrl, String bodyJson) {
        return this.invokeApi(apiUrl, HttpMethodEnum.POST, null, null, bodyJson, DEFAULT_MEDIA_TYPE);
    }

    public OpenApiResult invokeGet(String apiUrl) {
        return this.invokeGet(apiUrl, null);
    }

    public OpenApiResult invokeGet(String apiUrl, Map<String, String> params) {
        return this.invokeApi(apiUrl, HttpMethodEnum.GET, null, params, null, DEFAULT_MEDIA_TYPE);
    }

    public OpenApiResult invokeGet(String apiUrl, Map<String, String> params, String bodyJson) {
        return this.invokeApi(apiUrl, HttpMethodEnum.GET, null, params, bodyJson, DEFAULT_MEDIA_TYPE);
    }

    public OpenApiResult invokeApi(String apiUrl, HttpMethodEnum httpMethodEnum, Map<String, String> headerMap, Map<String, String> params, String bodyJson) {
        return this.invokeApi(apiUrl, httpMethodEnum, headerMap, params, bodyJson, DEFAULT_MEDIA_TYPE);
    }

    public OpenApiResult invokeApi(String apiUrl, HttpMethodEnum httpMethodEnum, Map<String, String> headerMap, Map<String, String> params, String bodyJson, String mediaType) {
        okhttp3.Response response = this.invoke(apiUrl, httpMethodEnum, headerMap, params, bodyJson, mediaType);
        String result = resolver(response.body());
        log.debug("execute result = {}", result);
        Response openResponse = GsonUtil.fromJson(result, Response.class);
        return covertResult(openResponse);
    }

    public <T> T invokeResult(String apiUrl, HttpMethodEnum httpMethodEnum, Map<String, String> headerMap, Map<String, String> params, String bodyJson, String mediaType, Type type) {
        okhttp3.Response response = this.invoke(apiUrl, httpMethodEnum, headerMap, params, bodyJson, mediaType);
        String result = resolver(response.body());
        log.debug("execute result = {}", result);
        return GsonUtil.fromJson(result, type);
    }


    //base invoke
    public okhttp3.Response invoke(String apiUrl, HttpMethodEnum httpMethodEnum, Map<String, String> headerMap, Map<String, String> params, String bodyJson, String mediaType) {
        if (headerMap == null) {
            headerMap = new HashMap();
        }
        this.buildHeaderMap(headerMap, bodyJson);

        Map<String, String> signMap = new HashMap();
        this.handleHeader(signMap, headerMap);
        if (!CollectionUtils.isEmpty(params)) {
            signMap.putAll(params);
        }
        String sign = SignatureUtil.sign(SignatureMethodEnum.HMACSHA256, this.appSecret, signMap);
        headerMap.put(Constants.HEADER_AUTH_SIGN, sign);
        String url = this.buildUrl(params, apiUrl);

        return this.okHttp3Client.execute(url, httpMethodEnum, headerMap, bodyJson, mediaType);
    }


    private void buildHeaderMap(Map<String, String> headerMap, String bodyJson) {
        if (!headerMap.containsKey(Constants.HEADER_APP_ID)) {
            headerMap.put(Constants.HEADER_APP_ID, this.appId);
        }
        if (!headerMap.containsKey(Constants.HEADER_REQUEST_ID)) {
            headerMap.put(Constants.HEADER_REQUEST_ID, UUID.randomUUID().toString().replaceAll("-", ""));
        }
        if (!headerMap.containsKey(Constants.HEADER_TIMESTAMP)) {
            headerMap.put(Constants.HEADER_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        }
        if (StringUtils.hasText(bodyJson)) {
            headerMap.put(Constants.HEADER_BODY_MD5, Md5Util.calc(bodyJson));
        }
    }

    private void handleHeader(Map<String, String> signMap, Map<String, String> headerMap) {
        if (headerMap.containsKey(Constants.HEADER_APP_ID)) {
            signMap.put(Constants.HEADER_APP_ID, headerMap.get(Constants.HEADER_APP_ID));
        }
        if (headerMap.containsKey(Constants.HEADER_REQUEST_ID)) {
            signMap.put(Constants.HEADER_REQUEST_ID, headerMap.get(Constants.HEADER_REQUEST_ID));
        }
        if (headerMap.containsKey(Constants.HEADER_TIMESTAMP)) {
            signMap.put(Constants.HEADER_TIMESTAMP, headerMap.get(Constants.HEADER_TIMESTAMP));
        }
        if (headerMap.containsKey(Constants.HEADER_BODY_MD5)) {
            signMap.put(Constants.HEADER_BODY_MD5, headerMap.get(Constants.HEADER_BODY_MD5));
        }
    }

    private String buildUrl(Map<String, String> queryParams, String url) {
        url = this.openapiAddress + url;
        if (queryParams != null && !queryParams.isEmpty()) {
            StringBuilder sb = new StringBuilder(url);
            sb.append("?");
            if (!ObjectUtils.isEmpty(queryParams)) {
                Iterator iterator = queryParams.entrySet().iterator();

                while(iterator.hasNext()) {
                    Map.Entry<String, String> entry = (Map.Entry)iterator.next();

                    try {
                        String encodedKey = URLEncoder.encode(entry.getKey(), "UTF8");
                        String encodedValue = URLEncoder.encode(entry.getValue(), "UTF8");
                        sb.append(encodedKey).append("=").append(encodedValue).append("&");
                    } catch (UnsupportedEncodingException ex) {
                        throw new OpenApiCreationException("请求参数拼接异常");
                    }
                }
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        } else {
            return url;
        }
    }


    private static String resolver(ResponseBody responseBody) {
        InputStream is = null;
        String result = null;

        try {
            is = responseBody.byteStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String body = null;
            StringBuilder sb = new StringBuilder();

            while((body = br.readLine()) != null) {
                sb.append(body);
            }
            is.close();
            result = sb.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    public static OpenApiResult covertResult(Response response) {
        OpenApiResult apiResult;
        if (Objects.isNull(response)) {
            apiResult = OpenApiResult.error("999", "获取结果失败");
        } else if (SUCCESS.equals(response.getCode())) {
            apiResult = OpenApiResult.success(response.getData());
        } else {
            apiResult = OpenApiResult.error(response.getCode().toString(), response.getMessage());
        }

        return apiResult;
    }
}
