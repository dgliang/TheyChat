package com.example.theychat;

import android.app.Application;

import com.example.theychat.constant.NetConst;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class MainApplication extends Application {
    private static MainApplication app;
    public String theyChatName; // 昵称
    private Socket socket;

    @Override
    public void onCreate() {
        super.onCreate();

        app = this;
        try {
            String uri = String.format("http://%s:%d/", NetConst.CHAT_IP, NetConst.CHAT_PORT);
            socket = IO.socket(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    // 利用单例模式获取当前应用的唯一实例
    public static MainApplication getInstance() {
        return app;
    }

    // 获取套接字对象的唯一实例
    public Socket getSocket() {
        return socket;
    }
}
