package com.example.theychat.util;

import android.app.Activity;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import io.socket.client.Socket;

public class SocketUtil {
    // 把对象数据转换为 json 串发送到 Socket 服务器
    public static void emit(Socket socket, String event, Object obj) {
        try {
            JSONObject json = new JSONObject(new Gson().toJson(obj));
            socket.emit(event, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 判断 Socket 能否连通
    public static void checkSocketAvailable(Activity act, String host, int port) {
        new Thread(() -> {
            try (java.net.Socket socket = new java.net.Socket()) {
                SocketAddress address = new InetSocketAddress(host, port);
                socket.connect(address, 1500);
            } catch (Exception e) {
                e.printStackTrace();
                act.runOnUiThread(() -> {
                    Toast.makeText(act, "无法连接Socket服务器", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}
