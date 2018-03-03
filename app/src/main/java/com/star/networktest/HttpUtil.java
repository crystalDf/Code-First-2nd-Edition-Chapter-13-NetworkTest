package com.star.networktest;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtil {

    public static void sendHttpRequest(final String address,
                                       final HttpCallbackListener listener) {

        new Thread(() -> {
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;

            try {
                URL url = new URL(address);

                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setConnectTimeout(8000);
                httpURLConnection.setReadTimeout(8000);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                InputStream inputStream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder response = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }

                if (listener != null) {
                    listener.onFinish(response.toString());
                }

            } catch (IOException e) {
                e.printStackTrace();

                if (listener != null) {
                    listener.onError(e);
                }
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
        }).start();
    }

    public static void sendOkHttpRequest(String address, Callback callback) {

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();

        okHttpClient.newCall(request).enqueue(callback);
    }
}
