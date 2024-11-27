package com.example.theychat;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.theychat.constant.NetConst;
import com.example.theychat.util.DateUtil;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketIOTextActivity extends AppCompatActivity {
    private final static String TAG = "SocketIOTextActivity";
    private EditText et_input;
    private TextView tv_res;
    private Socket socket;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        setContentView(R.layout.activity_socket_io_text);

        et_input = findViewById(R.id.et_input);
        tv_res = findViewById(R.id.tv_res);

        findViewById(R.id.btn_send).setOnClickListener(view -> {
            String content = et_input.getText().toString();

            if (TextUtils.isEmpty(content)) {
                Toast.makeText(this, "请输入聊天消息", Toast.LENGTH_SHORT).show();
                return;
            }
            // 往 Socket 服务器发送文本消息
            socket.emit("send_text", content);
        });

        initSocket();
    }

    // 初始化套接字
    private void initSocket() {
        // 检查能否连上 Socket 服务器
        SocketUtil.checkSocketAvaliable(this, NetConst.BASE_IP, NetConst.BASE_PORT);
        try {
            String uri = String.format("http://%s:%d/", NetConst.BASE_IP, NetConst.BASE_PORT);
            socket = IO.socket(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        socket.connect();

        // 等待接收传来的文本消息
        socket.on("receive_text", (args) -> {
            String desc = String.format("%s 收到服务端消息：%s", DateUtil.getNowTime(), (String) args[0]);
            runOnUiThread(() -> tv_res.setText(desc));
        });
    }

    // 取消接收传来的文本消息, 关闭 Socket 连接
    @Override
    protected void onDestroy() {
        super.onDestroy();

        socket.off("receive_text");
        if (socket.connected()) {
            socket.disconnect();
        }
        socket.close();
    }
}
