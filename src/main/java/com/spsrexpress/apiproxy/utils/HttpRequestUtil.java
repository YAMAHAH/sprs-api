package com.spsrexpress.apiproxy.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class HttpRequestUtil {

    @Value("${okhttp.client.connectTimeout}")
    private long connectTimeout;

    @Value("${okhttp.client.readTimeout}")
    private long readTimeout;

    private final okhttp3.MediaType JSON
            = okhttp3.MediaType.parse("application/json; charset=utf-8");
    private final okhttp3.MediaType xmlType
            = okhttp3.MediaType.parse("application/xml; charset=utf-8");

    public String postRequest(String targetURL, String urlParameters) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .build();
        okhttp3.RequestBody body = okhttp3.RequestBody.create(xmlType, urlParameters);
        Request request = new Request.Builder()
                .url(targetURL)
                .post(body)
                .addHeader("content-language", "ru-RU")
                .addHeader("content-type", "application/xml")
                .addHeader("content-length", Integer.toString(urlParameters.getBytes().length))
                .addHeader("cache-control", "no-cache")
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
