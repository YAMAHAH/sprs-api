package com.spsrexpress.apiproxy.utils;

import com.spsrexpress.apiproxy.exception.SpsrException;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

@Component
public class HttpRequestUtil {

    @Value("${okhttp.client.connectTimeout}")
    private long connectTimeout;

    @Value("${okhttp.client.readTimeout}")
    private long readTimeout;
    private long serversLoadTimes = 0L;
    private long maxLoadTimes = 3L;

    private final okhttp3.MediaType JSON
            = okhttp3.MediaType.parse("application/json; charset=utf-8");
    private final okhttp3.MediaType xmlType
            = okhttp3.MediaType.parse("application/xml; charset=utf-8");

    public String postRequest(String targetURL, String urlParameters) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .build();
        serversLoadTimes = 0;
        okhttp3.RequestBody body = okhttp3.RequestBody.create(xmlType, urlParameters);
        Request request = new Request.Builder()
                .url(targetURL)
                .post(body)
                .addHeader("content-language", "ru-RU")
                .addHeader("content-type", "application/xml")
                .addHeader("content-length", Integer.toString(urlParameters.getBytes().length))
                .addHeader("cache-control", "no-cache")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (e.getCause().equals(SocketTimeoutException.class) && serversLoadTimes < maxLoadTimes)//如果超时并未超过指定次数，则重新连接
                {
                    serversLoadTimes++;
                    client.newCall(call.request()).enqueue(this);
                } else {
                    e.printStackTrace();
                }
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("连接服务器超时.");
//                try {
//                    throw new SpsrException("连接服务器超时,请检查网络.");
//                } catch (SpsrException e) {
//                    e.printStackTrace();
//                }
            }
        });
        Response response = call.execute();
        return response.body().string();
    }
}
