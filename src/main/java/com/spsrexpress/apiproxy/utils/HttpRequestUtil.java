package com.spsrexpress.apiproxy.utils;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class HttpRequestUtil {

    @Autowired
    private OkHttpClient client;

    private final okhttp3.MediaType xmlType
            = okhttp3.MediaType.parse("application/xml; charset=utf-8");

    public String postRequest(String targetURL, String urlParameters) throws IOException {
        RequestBody body = RequestBody.create(xmlType, urlParameters);
        Request request = new Request.Builder()
                .url(targetURL)
                .post(body)
                .addHeader("content-type", "application/xml")
                .addHeader("cache-control", "no-cache")
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            ResponseBody resBody = response.body();
            String result = "";
            if (resBody != null) {
                result = resBody.string();
                resBody.close();
            }
            return result;
        } else {
            throw new IOException("Unexpected code: " + response);
        }
    }
}
