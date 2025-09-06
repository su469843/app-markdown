package io.qzz.studyhard.markdown.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MarkdownDownloader {
    
    public interface DownloadCallback {
        void onSuccess(String content);
        void onError(Exception e);
    }
    
    public static void downloadMarkdown(String url, DownloadCallback callback) {
        new Thread(() -> {
            try {
                String content = downloadContent(url);
                // 切换到主线程回调
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    callback.onSuccess(content);
                });
            } catch (Exception e) {
                // 切换到主线程回调
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    callback.onError(e);
                });
            }
        }).start();
    }
    
    private static String downloadContent(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("HTTP error code: " + responseCode);
        }
        
        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder content = new StringBuilder();
        String line;
        
        while ((line = reader.readLine()) != null) {
            content.append(line).append("\n");
        }
        
        reader.close();
        inputStream.close();
        connection.disconnect();
        
        return content.toString();
    }
}