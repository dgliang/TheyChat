package com.example.theychat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;

import com.example.theychat.util.PermissionUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 启用边缘到边缘模式，支持应用内容扩展到屏幕的整个区域
        EdgeToEdge.enable(this);

        // 设置活动的布局文件为 activity_main.xml
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_handler_message).setOnClickListener(this);    // 为 "处理消息" 按钮设置点击事件监听器
        findViewById(R.id.btn_work_manager).setOnClickListener(this);       // 为 "工作管理" 按钮设置点击事件监听器
        findViewById(R.id.btn_thread_ui).setOnClickListener(this);          // 为 "线程与UI" 按钮设置点击事件监听器
        findViewById(R.id.btn_json_convert).setOnClickListener(this);       // 为 "JSON 转换" 按钮设置点击事件监听器
        findViewById(R.id.btn_okhttp_call).setOnClickListener(this);        // 为 "OkHttp 调用" 按钮设置点击事件监听器
        findViewById(R.id.btn_okhttp_upload).setOnClickListener(this);      // 为 "OkHttp 上传" 按钮设置点击事件监听器
        findViewById(R.id.btn_okhttp_download).setOnClickListener(this);    // 为 "OkHttp 下载" 按钮设置点击事件监听器
        findViewById(R.id.btn_glide_cache).setOnClickListener(this);        // 为 "Glide 缓存" 按钮设置点击事件监听器
        findViewById(R.id.btn_glide_simple).setOnClickListener(this);       // 为 "Glide 简单示例" 按钮设置点击事件监听器
        findViewById(R.id.btn_glide_special).setOnClickListener(this);      // 为 "Glide 特殊功能" 按钮设置点击事件监听器
        findViewById(R.id.btn_socketio_text).setOnClickListener(this);      // 为 "SocketIO 文本" 按钮设置点击事件监听器
        findViewById(R.id.btn_socketio_image).setOnClickListener(this);     // 为 "SocketIO 图片" 按钮设置点击事件监听器
        findViewById(R.id.btn_we_login).setOnClickListener(this);           // 为 "微信登录" 按钮设置点击事件监听器
        findViewById(R.id.btn_web_socket).setOnClickListener(this);         // 为 "WebSocket" 按钮设置点击事件监听器
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_handler_message) {
            startActivity(new Intent(this, HandlerMessageActivity.class));
        } else if (view.getId() == R.id.btn_work_manager) {
            startActivity(new Intent(this, WorkManagerActivity.class));
        } else if (view.getId() == R.id.btn_thread_ui) {
            startActivity(new Intent(this, ThreadUiActivity.class));
        } else if (view.getId() == R.id.btn_json_convert) {
            startActivity(new Intent(this, JsonConvertActivity.class));
        } else if (view.getId() == R.id.btn_okhttp_call) {
            startActivity(new Intent(this, OkhttpCallActivity.class));
        } else if (view.getId() == R.id.btn_okhttp_upload) {
            startActivity(new Intent(this, OkhttpUploadActivity.class));
        } else if (view.getId() == R.id.btn_okhttp_download) {
            startActivity(new Intent(this, OkhttpDownloadActivity.class));
        } else if (view.getId() == R.id.btn_glide_cache) {
            startActivity(new Intent(this, GlideCacheActivity.class));
        } else if (view.getId() == R.id.btn_glide_simple) {
            startActivity(new Intent(this, GlideSimpleActivity.class));
        } else if (view.getId() == R.id.btn_glide_special) {
            if (PermissionUtil.checkPermission(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, (int) view.getId() % 65536)) {
                startActivity(new Intent(this, GlideSpecialActivity.class));
            }
        } else if (view.getId() == R.id.btn_socketio_text) {
            startActivity(new Intent(this, SocketIOTextActivity.class));
        } else if (view.getId() == R.id.btn_socketio_image) {
            startActivity(new Intent(this, SocketIOImageActivity.class));
        } else if (view.getId() == R.id.btn_we_login) {
            startActivity(new Intent(this, TheyLoginActivity.class));
        } else if (view.getId() == R.id.btn_web_socket) {
            startActivity(new Intent(this, WebSocketActivity.class));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // requestCode 不能小于 0，也不能大于 65536
        if (requestCode == R.id.btn_glide_special % 65536) {
            startActivity(new Intent(this, GlideSpecialActivity.class));
        } else {
            Toast.makeText(this, "需要存储卡权限才能浏览相册图片", Toast.LENGTH_SHORT).show();
        }
    }
}