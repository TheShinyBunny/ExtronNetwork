package com.extron.network.api.utils.tasks;

import com.google.common.net.HttpHeaders;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

public class NetworkUtils {


    public static HttpURLConnection openConnection(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection(Proxy.NO_PROXY);
        connection.setConnectTimeout(3000);
        connection.setReadTimeout(6000);
        connection.setRequestProperty(HttpHeaders.USER_AGENT,"ExtronNetwork");
        return connection;
    }

}
