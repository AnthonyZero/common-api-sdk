package com.anthonyzero.sdk.client.config;

import com.anthonyzero.sdk.client.exception.OpenApiHttpMethodException;
import com.anthonyzero.sdk.client.exception.OpenApiInvokeException;
import com.anthonyzero.sdk.client.model.HttpMethodEnum;
import com.anthonyzero.sdk.client.utils.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author : jin.ping
 * @date : 2024/2/26
 */
@Slf4j
public class OkHttp3Client {
    private OkHttpClient okHttpClient;
    private int connectTimeOut = 60;
    private int readTimeOut = 60;
    private int writeTimeOut = 60;
    private final ConnectionPool CONNECTION_POOL;

    public OkHttp3Client(int connectTimeOut, int readTimeOut, int writeTimeOut) {
        this.CONNECTION_POOL = new ConnectionPool(200, 5L, TimeUnit.MINUTES);
        this.connectTimeOut = connectTimeOut;
        this.readTimeOut = readTimeOut;
        this.writeTimeOut = writeTimeOut;
        this.init();
    }

    public OkHttp3Client() {
        this.CONNECTION_POOL = new ConnectionPool(200, 5L, TimeUnit.MINUTES);
        this.init();
    }

    private void init() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }};
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init((KeyManager[])null, trustAllCerts, new SecureRandom());
            this.okHttpClient = (new OkHttpClient.Builder()).connectionPool(this.CONNECTION_POOL).sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager)trustAllCerts[0]).hostnameVerifier((hostname, session) -> {
                return true;
            }).connectTimeout(this.connectTimeOut, TimeUnit.SECONDS).readTimeout(this.readTimeOut, TimeUnit.SECONDS).writeTimeout(this.writeTimeOut, TimeUnit.SECONDS).build();
        } catch (KeyManagementException | NoSuchAlgorithmException ex) {
            throw new RuntimeException("Failed to create OkHttpClient", ex);
        }
    }

    public Response execute(String url, HttpMethodEnum method, Map<String, String> headerMap, String bodyJson, String mediaType) {
        log.info("execute url = {}", url);
        if (method == HttpMethodEnum.GET) {
            bodyJson = null;
        }

        Request.Builder builder = (new Request.Builder()).url(url);
        if (headerMap != null && !headerMap.isEmpty()) {
            Objects.requireNonNull(builder);
            headerMap.forEach(builder::addHeader);
            log.info("execute url of headers = {}", GsonUtil.toString(headerMap));
        }

        Request request;
        if (method == HttpMethodEnum.GET) {
            request = builder.build();
        } else {
            RequestBody requestBody = RequestBody.create(MediaType.parse(mediaType), bodyJson);
            if (method == HttpMethodEnum.POST) {
                request = builder.post(requestBody).build();
            } else if (method == HttpMethodEnum.PUT) {
                request = builder.put(requestBody).build();
            } else {
                if (method != HttpMethodEnum.DELETE) {
                    throw new OpenApiHttpMethodException("Http请求方法不支持");
                }
                request = builder.delete(requestBody).build();
            }
        }

        try {
            return this.okHttpClient.newCall(request).execute();
        } catch (Exception ex) {
            throw new OpenApiInvokeException("服务调用异常", ex);
        }
    }
}
