package com.example.theychat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.theychat.bean.ImageMessage;
import com.example.theychat.bean.ImagePart;
import com.example.theychat.bean.MessageInfo;
import com.example.theychat.util.BitmapUtil;
import com.example.theychat.util.ChatUtil;
import com.example.theychat.util.DateUtil;
import com.example.theychat.util.SocketUtil;
import com.example.theychat.util.Utils;
import com.example.theychat.util.ViewUtil;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import io.socket.client.Socket;

public class FriendChatActivity extends AppCompatActivity {
    private static final String TAG = "FriendChatActivity";
    private EditText et_input;
    private ScrollView sv_chat;
    private LinearLayout ll_show;
    private int dip_margin;
    private int CHOOSE_CODE = 3;
    private String selfName, friendName; // 自己名称，好友名称
    private Socket socket;
    private String minuteTime = "00:00";
    private int block = 50 * 1024; // 每段的数据包大小
    private String lastFile; // 上次的文件名
    private int receiveCount; // 接收包的数量
    private byte[] receiveData; // 收到的字节数组

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_friend_chat);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 保持屏幕常亮

        selfName = getIntent().getStringExtra("self_name");
        friendName = getIntent().getStringExtra("friend_name");
        initView();
        initSocket();
    }

    // 初始化视图
    private void initView() {
        dip_margin = Utils.dip2px(this, 5);
        TextView tv_title = findViewById(R.id.tv_title);

        et_input = findViewById(R.id.et_input);
        sv_chat = findViewById(R.id.sv_chat);
        ll_show = findViewById(R.id.ll_show);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        findViewById(R.id.ib_img).setOnClickListener(v -> {
            // 准备跳到系统相册
            Intent albumIntent = new Intent(Intent.ACTION_GET_CONTENT);
            albumIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);

            // 类型为图像
            albumIntent.setType("image/*");
            startActivityForResult(albumIntent, CHOOSE_CODE);
        });
        findViewById(R.id.btn_send).setOnClickListener(v -> sendMessage());
        tv_title.setText(friendName);
    }

    // 初始化套接字
    private void initSocket() {
        socket = MainApplication.getInstance().getSocket();

        // 等待接收好友消息
        socket.on("receive_friend_message", (args) -> {
            JSONObject json = (JSONObject) args[0];
            Log.d(TAG, "receive_friend_message:"+json.toString());
            MessageInfo message = new Gson().fromJson(json.toString(), MessageInfo.class);
            runOnUiThread(() -> appendChatMsg(message.from, message.content, false));
        });

        // 等待接收好友图片
        socket.on("receive_friend_image", (args) -> receiveImage(args));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.off("receive_friend_message");
        socket.off("receive_friend_image");
    }

    // 发送聊天消息
    private void sendMessage() {
        String content = et_input.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入聊天消息", Toast.LENGTH_SHORT).show();
            return;
        }

        et_input.setText("");
        ViewUtil.hideOneInputMethod(this, et_input);
        appendChatMsg(selfName, content, true);

        // 向 Socket 服务器发送聊天消息
        MessageInfo message = new MessageInfo(selfName, friendName, content);
        SocketUtil.emit(socket, "send_friend_message", message);
    }

    // 往聊天窗口添加聊天消息
    private void appendChatMsg(String name, String content, boolean isSelf) {
        appendNowMinute();

        // 把单条消息的线性布局添加到聊天窗口上
        ll_show.addView(ChatUtil.getChatView(this, name, content, isSelf));
        new Handler(Looper.myLooper()).postDelayed(() -> {
            sv_chat.fullScroll(ScrollView.FOCUS_DOWN);
        }, 100);
    }

    // 往聊天窗口添加当前时间
    private void appendNowMinute() {
        String nowMinute = DateUtil.getNowMinute();

        if (!minuteTime.substring(0, 4).equals(nowMinute.substring(0, 4))) {
            minuteTime = nowMinute;
            ll_show.addView(ChatUtil.getHintView(this, nowMinute, dip_margin));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK && requestCode == CHOOSE_CODE) {

            // 从相册选择一张照片
            if (intent.getData() != null) {
                Uri uri = intent.getData();
                String path = uri.toString();
                String imageName = path.substring(path.lastIndexOf("/")+1);

                String imagePath = BitmapUtil.getAutoZoomPath(this, uri);

                appendChatImage(selfName, imagePath, true);
                sendImage(imageName, imagePath);
            }
        }
    }

    // 分段传输图片数据
    private void sendImage(String imageName, String imagePath) {
        Log.d(TAG, "sendImage");

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream pkg = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, pkg);
        byte[] bytes = pkg.toByteArray();
        int count = bytes.length/ block + 1;

        Log.d(TAG, "sendImage length="+bytes.length+", count="+count);

        for (int i = 0; i < count; i++) {
            Log.d(TAG, "sendImage i=" + i);

            String encodeData = "";
            if (i == count - 1) {
                int remain = bytes.length % block;
                byte[] temp = new byte[remain];
                System.arraycopy(bytes, i * block, temp, 0, remain);
                encodeData = Base64.encodeToString(temp, Base64.DEFAULT);
            } else {
                byte[] temp = new byte[block];
                System.arraycopy(bytes, i * block, temp, 0, block);
                encodeData = Base64.encodeToString(temp, Base64.DEFAULT);
            }

            ImagePart part = new ImagePart(imageName, encodeData, i, bytes.length);
            ImageMessage message = new ImageMessage(selfName, friendName, part);
            SocketUtil.emit(socket, "send_friend_image", message);
        }
    }

    // 接收对方传来的图片数据
    private void receiveImage(Object... args) {
        JSONObject json = (JSONObject) args[0];
        ImageMessage message = new Gson().fromJson(json.toString(), ImageMessage.class);
        ImagePart part = message.getPart();

        if (!part.getName().equals(lastFile)) {
            // 与上次文件名不同，表示开始接收新文件
            lastFile = part.getName();
            receiveCount = 0;
            receiveData = new byte[part.getLength()];
        }
        receiveCount++;

        byte[] temp = Base64.decode(part.getData(), Base64.DEFAULT);
        System.arraycopy(temp, 0, receiveData, part.getSeq()* block, temp.length);

        // 所有数据包都接收完毕
        if (receiveCount >= part.getLength()/ block +1) {
            // 从字节数组中解码得到位图对象
            Bitmap bitmap = BitmapFactory.decodeByteArray(receiveData, 0, receiveData.length);
            String imagePath = String.format("%s/%s.jpg",
                    getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(),
                    DateUtil.getNowDateTime());

            BitmapUtil.saveImage(imagePath, bitmap);
            runOnUiThread(() -> appendChatImage(message.getFrom(), imagePath, false));
        }
    }

    // 往聊天窗口添加图片消息
    private void appendChatImage(String name, String imagePath, boolean isSelf) {
        appendNowMinute();

        // 把图片消息的线性布局添加到聊天窗口上
        ll_show.addView(ChatUtil.getChatImage(this, name, imagePath, isSelf));
        new Handler(Looper.myLooper()).postDelayed(() -> {
            sv_chat.fullScroll(ScrollView.FOCUS_DOWN);
        }, 100);
    }

}
