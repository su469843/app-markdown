package io.qzz.studyhard.markdown.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VersionUtils {
    private static final String TAG = "VersionUtils";
    private static final int TIMEOUT = 5000;
    
    // 版本信息获取地址
    private static final String[] VERSION_URLS = {
        "https://raw.githubusercontent.com/your-username/your-repo/main/version.json",
        "https://cdn.jsdelivr.net/gh/your-username/your-repo@main/version.json",
        "https://gitee.com/your-username/your-repo/raw/main/version.json"
    };
    
    public interface VersionCallback {
        void onSuccess(VersionInfo versionInfo);
        void onError(Exception e);
    }
    
    public static class VersionInfo {
        public String latestVersion;
        public String updateMessage;
        public String downloadUrl;
        
        public VersionInfo(String latestVersion, String updateMessage, String downloadUrl) {
            this.latestVersion = latestVersion;
            this.updateMessage = updateMessage;
            this.downloadUrl = downloadUrl;
        }
    }
    
    private static final ExecutorService executor = Executors.newFixedThreadPool(3);
    
    /**
     * 获取版本信息
     */
    public static void getVersionInfo(Context context, VersionCallback callback) {
        executor.execute(() -> {
            Exception lastException = null;
            
            // 首先尝试从网络获取
            for (String url : VERSION_URLS) {
                try {
                    String json = fetchJsonFromUrl(url);
                    VersionInfo info = parseVersionInfo(json);
                    if (info != null) {
                        // 切换到主线程回调
                        new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                            callback.onSuccess(info);
                        });
                        return;
                    }
                } catch (Exception e) {
                    lastException = e;
                    Log.w(TAG, "Failed to fetch version info from " + url, e);
                }
            }
            
            // 如果网络获取失败，尝试从本地资源获取
            try {
                String json = fetchJsonFromLocal(context);
                VersionInfo info = parseVersionInfo(json);
                if (info != null) {
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                        callback.onSuccess(info);
                    });
                    return;
                }
            } catch (Exception e) {
                lastException = e;
                Log.w(TAG, "Failed to fetch version info from local resource", e);
            }
            
            // 所有尝试都失败
            final Exception finalException = lastException;
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                callback.onError(finalException != null ? finalException : new Exception("Failed to get version info"));
            });
        });
    }
    
    /**
     * 从URL获取JSON
     */
    private static String fetchJsonFromUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(TIMEOUT);
        connection.setReadTimeout(TIMEOUT);
        
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("HTTP error code: " + responseCode);
        }
        
        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder content = new StringBuilder();
        String line;
        
        while ((line = reader.readLine()) != null) {
            content.append(line);
        }
        
        reader.close();
        inputStream.close();
        connection.disconnect();
        
        return content.toString();
    }
    
    /**
     * 从本地资源获取JSON
     */
    private static String fetchJsonFromLocal(Context context) throws IOException {
        InputStream inputStream = context.getResources().openRawResource(io.qzz.studyhard.markdown.R.raw.update_config);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder content = new StringBuilder();
        String line;
        
        while ((line = reader.readLine()) != null) {
            content.append(line);
        }
        
        reader.close();
        inputStream.close();
        
        return content.toString();
    }
    
    /**
     * 解析版本信息
     */
    private static VersionInfo parseVersionInfo(String json) {
        try {
            // 简化的JSON解析
            String latestVersion = extractJsonValue(json, "latestVersion");
            String updateMessage = extractJsonValue(json, "updateMessage");
            String downloadUrl = extractJsonValue(json, "downloadUrl");
            
            if (latestVersion != null && !latestVersion.isEmpty()) {
                return new VersionInfo(latestVersion, updateMessage, downloadUrl);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse version info", e);
        }
        return null;
    }
    
    /**
     * 提取JSON值
     */
    private static String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int startIndex = json.indexOf(searchKey);
        if (startIndex != -1) {
            startIndex += searchKey.length();
            int endIndex = json.indexOf("\"", startIndex);
            if (endIndex != -1) {
                return json.substring(startIndex, endIndex);
            }
        }
        return "";
    }
    
    /**
     * 比较版本号
     */
    public static boolean isVersionNewer(String latestVersion, String currentVersion) {
        try {
            String[] latestParts = latestVersion.split("\\.");
            String[] currentParts = currentVersion.split("\\.");
            
            int length = Math.max(latestParts.length, currentParts.length);
            
            for (int i = 0; i < length; i++) {
                int latestPart = i < latestParts.length ? Integer.parseInt(latestParts[i]) : 0;
                int currentPart = i < currentParts.length ? Integer.parseInt(currentParts[i]) : 0;
                
                if (latestPart > currentPart) {
                    return true;
                } else if (latestPart < currentPart) {
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            // 如果解析失败，进行字符串比较
            return latestVersion.compareTo(currentVersion) > 0;
        }
        
        return false;
    }
}