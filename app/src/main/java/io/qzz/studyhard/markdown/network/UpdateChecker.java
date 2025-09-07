package io.qzz.studyhard.markdown.network;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import androidx.core.content.FileProvider;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {
    private static final String TAG = "UpdateChecker";
    private static final String[] VERSION_CHECK_URLS = {
        "https://raw.githubusercontent.com/su469843/app-markdown/main/publish/version.json",
        "https://cdn.jsdelivr.net/gh/su469843/app-markdown/publish/version.json",
        "https://gitee.com/su469843/app-markdown/raw/main/publish/version.json"
    };
    private static final String UPDATE_FILE_NAME = "app-update.apk";
    
    private Context context;
    private long downloadId;
    private VersionCallback callback;
    
    public interface VersionCallback {
        void onNewVersionAvailable(String newVersion, String changelog, String downloadUrl, boolean forceUpdate);
        void onNoUpdateAvailable();
        void onError(String error);
    }

    private int compareVersions(String v1, String v2) {
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");
        int length = Math.max(parts1.length, parts2.length);
        for (int i = 0; i < length; i++) {
            int part1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int part2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;
            if (part1 < part2) return -1;
            if (part1 > part2) return 1;
        }
        return 0;
    }
    
    public UpdateChecker(Context context) {
        this.context = context;
    }
    
    private String getDeviceABI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return Build.SUPPORTED_ABIS[0];
        }
        return Build.CPU_ABI;
    }

    public void checkForUpdates(final VersionCallback callback) {
        this.callback = callback;
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (String versionUrl : VERSION_CHECK_URLS) {
                    try {
                        URL url = new URL(versionUrl);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(5000);
                        connection.setReadTimeout(5000);
                    
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream())
                        );
                        StringBuilder response = new StringBuilder();
                        String line;
                        
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        
                        JSONObject json = new JSONObject(response.toString());
                        String latestVersion = json.getString("version");
                        String currentVersion = context.getPackageManager()
                            .getPackageInfo(context.getPackageName(), 0)
                            .versionName;
                            
                        if (!currentVersion.equals(latestVersion)) {
                            String changelog = json.optString("changelog", "");
                            String downloadUrl = json.getString("downloadUrl");
                            callback.onNewVersionAvailable(latestVersion, changelog, downloadUrl);
                        } else {
                            callback.onNoUpdateAvailable();
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error checking for updates", e);
                    callback.onError("更新检查失败: " + e.getMessage());
                }
            }
        }).start();
    }
    
    public void downloadUpdate(String downloadUrl) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
        request.setTitle("下载更新");
        request.setDescription("正在下载新版本...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, UPDATE_FILE_NAME);
        
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadId = manager.enqueue(request);
        
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (id == downloadId) {
                    installUpdate();
                }
            }
        }, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
    
    private void installUpdate() {
        File file = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS), UPDATE_FILE_NAME);
            
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(context,
                context.getPackageName() + ".provider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        }
        
        context.startActivity(intent);
    }
}
