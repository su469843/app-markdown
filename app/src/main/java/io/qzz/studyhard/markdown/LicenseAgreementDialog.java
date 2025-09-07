package io.qzz.studyhard.markdown;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class LicenseAgreementDialog extends Dialog {
    private Context context;
    private OnAgreeListener onAgreeListener;
    
    public interface OnAgreeListener {
        void onAgree();
    }
    
    public LicenseAgreementDialog(Context context) {
        super(context);
        this.context = context;
    }
    
    public void setOnAgreeListener(OnAgreeListener listener) {
        this.onAgreeListener = listener;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_license_agreement);
        
        // 设置对话框属性
        setTitle("用户协议");
        setCancelable(false);
        
        // 初始化视图
        TextView licenseText = findViewById(R.id.licenseText);
        CheckBox agreeCheckbox = findViewById(R.id.agreeCheckbox);
        Button agreeButton = findViewById(R.id.agreeButton);
        Button cancelButton = findViewById(R.id.cancelButton);
        
        // 设置协议文本
        licenseText.setText(getLicenseText());
        
        // 设置按钮点击事件
        agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (agreeCheckbox.isChecked()) {
                    if (onAgreeListener != null) {
                        onAgreeListener.onAgree();
                    }
                    dismiss();
                }
            }
        });
        
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
    
    private String getLicenseText() {
        return "文件管理器用户协议\n\n" +
               "1. 使用须知\n" +
               "本应用是一个开源的文件编辑器，主要用于查看、编辑和管理设备上的文本文件。\n\n" +
               "2. 功能说明\n" +
               "- 支持多种文本格式文件的编辑（.txt, .js, .html, .css, .md, .markdown）\n" +
               "- 支持从网络下载文件\n" +
               "- 支持从设备选择文件\n" +
               "- 支持Markdown格式预览\n\n" +
               "3. 权限说明\n" +
               "为实现文件管理功能，应用需要以下权限：\n" +
               "- 网络访问权限：用于下载网络文件\n" +
               "- 存储读写权限：用于保存和读取本地文件\n\n" +
               "4. 免责声明\n" +
               "本应用按\"现状\"提供，不提供任何形式的担保。用户需自行承担使用风险。\n\n" +
               "5. 协议变更\n" +
               "我们保留随时修改本协议的权利，修改后的协议在应用内公布即生效。\n\n" +
               "点击\"同意\"表示您已阅读并接受本协议的所有条款。";
    }
}