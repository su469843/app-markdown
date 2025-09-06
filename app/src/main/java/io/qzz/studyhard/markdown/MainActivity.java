package io.qzz.studyhard.markdown;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.noties.markwon.Markwon;
import io.qzz.studyhard.markdown.network.MarkdownDownloader;

public class MainActivity extends Activity {
    private EditText urlEditText;
    private EditText editText;
    private TextView previewText;
    private Button downloadButton;
    private Button previewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化视图
        urlEditText = findViewById(R.id.urlEditText);
        editText = findViewById(R.id.editText);
        previewText = findViewById(R.id.previewText);
        downloadButton = findViewById(R.id.downloadButton);
        previewButton = findViewById(R.id.previewButton);

        // 设置下载按钮点击事件
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadMarkdown();
            }
        });

        // 设置预览按钮点击事件
        previewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewMarkdown();
            }
        });
    }

    private void downloadMarkdown() {
        String url = urlEditText.getText().toString().trim();
        if (url.isEmpty()) {
            Toast.makeText(this, "请输入URL", Toast.LENGTH_SHORT).show();
            return;
        }

        // 显示下载中提示
        Toast.makeText(this, "下载中...", Toast.LENGTH_SHORT).show();

        // 使用MarkdownDownloader下载内容
        MarkdownDownloader.downloadMarkdown(url, new MarkdownDownloader.DownloadCallback() {
            @Override
            public void onSuccess(String content) {
                // 将下载的内容显示在编辑器中
                editText.setText(content);
                Toast.makeText(MainActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(MainActivity.this, "下载失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void previewMarkdown() {
        // 获取输入的Markdown文本
        String markdownText = editText.getText().toString();

        // 使用Markwon解析并显示Markdown
        Markwon markwon = Markwon.create(this);
        markwon.setMarkdown(previewText, markdownText);
    }
}