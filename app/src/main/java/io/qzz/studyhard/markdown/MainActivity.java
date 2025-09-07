package io.qzz.studyhard.markdown;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.noties.markwon.Markwon;
import io.qzz.studyhard.markdown.network.MarkdownDownloader;

public class MainActivity extends Activity {
    private EditText urlEditText;
    private EditText filenameEditText;
    private EditText editText;
    private TextView previewText;
    private Button downloadButton;
    private Button previewButton;
    private Button openButton;
    private Button saveButton;
    private Button selectFileButton;
    
    private static final String DEFAULT_FILENAME = "markdown_content.txt";
    private static final int PICK_FILE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化视图
        urlEditText = findViewById(R.id.urlEditText);
        filenameEditText = findViewById(R.id.filenameEditText);
        editText = findViewById(R.id.editText);
        previewText = findViewById(R.id.previewText);
        downloadButton = findViewById(R.id.downloadButton);
        previewButton = findViewById(R.id.previewButton);
        openButton = findViewById(R.id.openButton);
        saveButton = findViewById(R.id.saveButton);
        selectFileButton = findViewById(R.id.selectFileButton);

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
        
        // 设置打开按钮点击事件
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFile();
            }
        });
        
        // 设置保存按钮点击事件
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFile();
            }
        });
        
        // 设置文件选择按钮点击事件
        selectFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFileFromDevice();
            }
        });
        
        // 设置默认文件名
        filenameEditText.setText(DEFAULT_FILENAME);
        
        // 加载默认文件
        loadFile(DEFAULT_FILENAME);
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
        
        // 只有当文件是Markdown格式时才进行预览
        String filename = filenameEditText.getText().toString().trim();
        if (filename.isEmpty()) {
            filename = DEFAULT_FILENAME;
        }
        
        if (filename.endsWith(".md") || filename.endsWith(".markdown")) {
            // 使用Markwon解析并显示Markdown
            Markwon markwon = Markwon.create(this);
            markwon.setMarkdown(previewText, markdownText);
        } else {
            previewText.setText("预览功能仅支持Markdown文件(.md, .markdown)");
        }
    }
    
    private void openFile() {
        String filename = filenameEditText.getText().toString().trim();
        if (filename.isEmpty()) {
            filename = DEFAULT_FILENAME;
        }
        loadFile(filename);
        Toast.makeText(this, "文件已加载: " + filename, Toast.LENGTH_SHORT).show();
    }
    
    private void saveFile() {
        String filename = filenameEditText.getText().toString().trim();
        if (filename.isEmpty()) {
            filename = DEFAULT_FILENAME;
        }
        
        // 检查文件扩展名是否为支持的类型
        if (isSupportedFileType(filename)) {
            saveToFile(filename);
        } else {
            Toast.makeText(this, "不支持的文件类型。支持的类型: .txt, .js, .html, .css, .md, .markdown", Toast.LENGTH_LONG).show();
        }
    }
    
    private void selectFileFromDevice() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "选择文件"), PICK_FILE_REQUEST_CODE);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    loadFileFromUri(uri);
                }
            }
        }
    }
    
    private void loadFileFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder content = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                
                reader.close();
                inputStream.close();
                
                editText.setText(content.toString());
                
                // 提取文件名并设置到filenameEditText
                String fileName = getFileNameFromUri(uri);
                if (fileName != null) {
                    filenameEditText.setText(fileName);
                }
                
                Toast.makeText(this, "文件加载成功", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, "读取文件失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private String getFileNameFromUri(Uri uri) {
        // 简单实现，实际项目中可能需要更复杂的逻辑
        String path = uri.getPath();
        if (path != null) {
            int lastSlash = path.lastIndexOf('/');
            if (lastSlash != -1) {
                return path.substring(lastSlash + 1);
            }
        }
        return "selected_file.txt";
    }
    
    private boolean isSupportedFileType(String filename) {
        return filename.endsWith(".txt") || 
               filename.endsWith(".js") || 
               filename.endsWith(".html") || 
               filename.endsWith(".css") || 
               filename.endsWith(".md") || 
               filename.endsWith(".markdown");
    }
    
    private void saveToFile(String filename) {
        try {
            FileOutputStream fos = openFileOutput(filename, MODE_PRIVATE);
            String content = editText.getText().toString();
            fos.write(content.getBytes());
            fos.close();
            Toast.makeText(this, "文件已保存: " + filename, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "保存失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void loadFile(String filename) {
        try {
            FileInputStream fis = openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder content = new StringBuilder();
            String line;
            
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
            
            br.close();
            isr.close();
            fis.close();
            
            editText.setText(content.toString());
        } catch (IOException e) {
            // 文件不存在是正常情况，不需要提示
            editText.setText("");
        }
    }
}