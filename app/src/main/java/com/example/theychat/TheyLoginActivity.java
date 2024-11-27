package com.example.theychat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.theychat.constant.NetConst;
import com.example.theychat.util.SocketUtil;

public class TheyLoginActivity extends AppCompatActivity {
    private EditText et_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_they_login);

        et_name = findViewById(R.id.et_name);

        findViewById(R.id.btn_login).setOnClickListener(view -> Login());

        // 检查能否连上 Socket 服务器
        SocketUtil.checkSocketAvailable(this, NetConst.CHAT_IP, NetConst.CHAT_PORT);
    }

    // 登录
    private void Login() {
        String name = et_name.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "请输入您的昵称", Toast.LENGTH_SHORT).show();
            return;
        }

        // 打开主界面，聊天界面
        MainApplication.getInstance().wechatName = name;
        startActivity(new Intent(this, TheyChatActivity.class));
    }
}
