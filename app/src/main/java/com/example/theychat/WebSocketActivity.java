package com.example.theychat;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.theychat.constant.NetConst;
import com.example.theychat.task.AppClientEndpoint;
import com.example.theychat.util.DateUtil;

import java.net.URI;

import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

public class WebSocketActivity extends AppCompatActivity {
    private static final String TAG = "WebSocketActivity";
    private static final String SERVER_URL = NetConst.WEBSOCKET_PREFIX + "testWebSocket";
    private EditText et_input;
    private TextView tv_res;
    private AppClientEndpoint appTask; // WebSocket 客户端任务对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web_socket);

        et_input = findViewById(R.id.et_input);
        tv_res = findViewById(R.id.tv_response);

        findViewById(R.id.btn_send).setOnClickListener(v -> {
            String content = et_input.getText().toString();
            if (TextUtils.isEmpty(content)) {
                Toast.makeText(this, "请输入消息文本", Toast.LENGTH_SHORT).show();
                return;
            }
            new Thread(() -> appTask.sendRequest(content)).start();
        });

        // 启动线程初始化 WebSocket 客户端
        new Thread(this::initWebSocket).start();
    }

    // 初始化WebSocket的客户端任务
    private void initWebSocket() {
        appTask = new AppClientEndpoint(this, resp -> {
            String desc = String.format("%s 收到服务端返回：%s", DateUtil.getNowTime(), resp);
            tv_res.setText(desc);
        });

        // 获取 WebSocket 容器
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            URI uri = new URI(SERVER_URL);

            // 连接 WebSocket 服务器，并关联文本传输任务获得连接会话
            Session session = container.connectToServer(appTask, uri);

            // 设置文本消息的最大缓存大小
            session.setMaxTextMessageBufferSize(1024 * 1024 * 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
