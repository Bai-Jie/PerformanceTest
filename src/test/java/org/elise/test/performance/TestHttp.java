package org.elise.test.performance;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import org.elise.test.framework.stack.http.HttpClient;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

public class TestHttp {

    public static void main(String args[]) throws InterruptedException, UnsupportedEncodingException, URISyntaxException {
        String host = "www.baidu.com";
        String url = "https://www.baidu.com";
        HttpClient client = new HttpClient();
        DefaultHttpHeaders headers = new DefaultHttpHeaders();
        headers.set(HttpHeaderNames.HOST, host);
        headers.set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        client.invoke(host,80, url, HttpMethod.GET,headers);


    }
}