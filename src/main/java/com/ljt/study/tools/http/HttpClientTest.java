package com.ljt.study.tools.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.ljt.study.tools.http.OkHttpTest.CHECK_VALUE;

/**
 * @author LiJingTang
 * @date 2021-09-24 17:58
 */
class HttpClientTest {

    public static void main(String[] args) throws IOException {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setSocketTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();

        long start = System.currentTimeMillis();

        for (int i = 0; i < 50; i++) {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("mer_cust_id", "6666000107422135"));
            params.add(new BasicNameValuePair("version", "10"));
            params.add(new BasicNameValuePair("check_value", CHECK_VALUE));
            HttpEntity reqEntity = new UrlEncodedFormEntity(params, "utf-8");

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(OkHttpTest.URL);
            post.setEntity(reqEntity);
            post.setConfig(config);
            HttpResponse response = httpClient.execute(post);

            System.out.println(i + "-" + response.getStatusLine().getStatusCode());
        }

        System.out.println(System.currentTimeMillis() - start);
    }

}
