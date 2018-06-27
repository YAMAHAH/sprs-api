package com.spsrexpress.apiproxy.utils;

import com.spsrexpress.apiproxy.exception.SpsrException;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class HttpRequestUtil {

    @Value("${okhttp.client.connectTimeout}")
    private long connectTimeout;

    @Value("${okhttp.client.readTimeout}")
    private long readTimeout;
    private long serversLoadTimes = 0L;
    private long maxLoadTimes = 3L;

    @Autowired
    private OkHttpClient client;

    private final okhttp3.MediaType JSON
            = okhttp3.MediaType.parse("application/json; charset=utf-8");
    private final okhttp3.MediaType xmlType
            = okhttp3.MediaType.parse("application/xml; charset=utf-8");

    public String postRequest(String targetURL, String urlParameters) throws IOException {
        long startTime = System.currentTimeMillis();    //获取开始时间
        serversLoadTimes = 0;
        RequestBody body = RequestBody.create(xmlType, urlParameters);
        Request request = new Request.Builder()
                .url(targetURL)
                .post(body)
//                .addHeader("content-language", "ru-RU")
                .addHeader("content-type", "application/xml")
                .addHeader("content-length", Integer.toString(urlParameters.getBytes().length))
                .addHeader("cache-control", "no-cache")
                .build();
        Call call = client.newCall(request);
        long endTime = System.currentTimeMillis();    //获取结束时间
        Response response = call.execute();
        System.out.println("请求时间：" + (endTime - startTime) + "ms");
        return response.body().string();
    }
}
