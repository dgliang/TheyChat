package com.example.theychat.task;

import android.app.Activity;
import android.util.Log;

import javax.websocket.*;

@ClientEndpoint
public class AppClientEndpoint {
    private final static String TAG = "AppClientEndpoint";
    private Activity activity;          // 活动实例
    private OnRespListener listener;    // 消息应答监听器
    private Session session;            // 连接会话

    public AppClientEndpoint(Activity act, OnRespListener listener) {
        activity = act;
        this.listener = listener;
    }

    // 向服务器发送请求报文
    public void sendRequest(String req) {
        Log.d(TAG, "发送请求报文："+req);

        try {
            if (session != null) {
                RemoteEndpoint.Basic remote = session.getBasicRemote();
                remote.sendText(req);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 连接成功后调用
    @OnOpen
    public void onOpen(final Session session) {
        this.session = session;
        Log.d(TAG, "成功创建连接");
    }

    // 收到服务端消息时调用
    @OnMessage
    public void processMessage(Session session, String message) {
        Log.d(TAG, "WebSocket服务端返回：" + message);

        if (listener != null) {
            activity.runOnUiThread(() -> listener.receiveResponse(message));
        }
    }

    // 收到服务端错误时调用
    @OnError
    public void processError(Throwable t) {
        t.printStackTrace();
    }

    // 定义一个 WebSocket 应答的监听器接口
    public interface OnRespListener {
        void receiveResponse(String resp);
    }
}
