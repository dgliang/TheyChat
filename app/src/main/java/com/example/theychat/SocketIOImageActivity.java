package com.example.theychat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.theychat.bean.ImagePart;
import com.example.theychat.constant.NetConst;
import com.example.theychat.util.BitmapUtil;
import com.example.theychat.util.DateUtil;
import com.example.theychat.util.SocketUtil;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketIOImageActivity extends AppCompatActivity {
    private static final String TAG = "SocketIOImageActivity";
    private ImageView iv_req;
    private ImageView iv_res;
    private TextView tv_res;
    private int CHOOSE_CODE = 3;
    private String fileName;
    private Bitmap bitmap;
    private Socket socket;
    private int block = 50 * 1024; // 每段的数据包大小
    private String lastFile; // 上次的文件名
    private int receiveCount; // 接收包的数量
    private byte[] receiveData; // 收到的字节数组

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_socket_io_image);

        iv_req = findViewById(R.id.iv_req);
        iv_res = findViewById(R.id.iv_res);
        tv_res = findViewById(R.id.tv_res);

        // 选择图片按钮点击事件
        findViewById(R.id.btn_choose).setOnClickListener(v -> {
            Intent albumIntent = new Intent(Intent.ACTION_GET_CONTENT);
            albumIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
            albumIntent.setType("image/*");
            startActivityForResult(albumIntent, CHOOSE_CODE);
        });

        // 发送图片按钮点击事件
        findViewById(R.id.btn_send).setOnClickListener(view -> {
            if (bitmap == null) {
                Toast.makeText(this, "请先选择图片文件", Toast.LENGTH_SHORT).show();
                return;
            }
            sendImage();
        });

        initSocket();
    }

    // 初始化套接字
    private void initSocket() {
        // 检查能否连上 Socket 服务器
        SocketUtil.checkSocketAvailable(this, NetConst.BASE_IP, NetConst.BASE_PORT);
        try {
            String uri = String.format("http://%s:%d/", NetConst.BASE_IP, NetConst.BASE_PORT);
            socket = IO.socket(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        socket.connect();

        // 等待接收传来的图片数据
        socket.on("receive_image", this::receiveImage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // 图片选择结果处理
        if (resultCode == RESULT_OK && requestCode == CHOOSE_CODE) {
            if (intent.getData() != null) {
                Uri uri = intent.getData();
                String path = uri.toString();
                fileName = path.substring(path.lastIndexOf("/") + 1);

                bitmap = BitmapUtil.getAutoZoomImage(this, uri);
                iv_req.setImageBitmap(bitmap);
            }
        }
    }

    // 分段传输图片数据
    private void sendImage() {
        Log.d(TAG, "sendImage");

        ByteArrayOutputStream pak = new ByteArrayOutputStream();

        // 把位图数据压缩到字节数组输出流
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, pak);
        byte[] bytes = pak.toByteArray();
        int count = bytes.length / block + 1;
        Log.d(TAG, "sendImage length=" + bytes.length + ", count=" + count);

        for (int i = 0; i < count; i++) {
            Log.d(TAG, "sendImage i=" + i);
            String encodeData = "";
            if (i == count - 1) {
                // 如果是最后一段，处理剩余的字节
                int remain = bytes.length % block;
                byte[] temp = new byte[remain];
                System.arraycopy(bytes, i * block, temp, 0, remain);
                encodeData = Base64.encodeToString(temp, Base64.DEFAULT);
            } else {
                // 处理完整的 50KB 数据块
                byte[] temp = new byte[block];
                System.arraycopy(bytes, i * block, temp, 0, block);
                encodeData = Base64.encodeToString(temp, Base64.DEFAULT);
            }

            // 创建 ImagePart 对象，包含当前图片数据段的信息
            ImagePart part = new ImagePart(fileName, encodeData, i, bytes.length);
            SocketUtil.emit(socket, "send_image", part); // 向服务器提交图像数据
        }
    }

    // 接收对方传来的图片数据
    private void receiveImage(Object... args) {
        JSONObject json = (JSONObject) args[0];
        ImagePart part = new Gson().fromJson(json.toString(), ImagePart.class);

        // 与上次文件名不同，表示开始接收新文件
        if (!part.getName().equals(lastFile)) {
            lastFile = part.getName();
            receiveCount = 0;
            receiveData = new byte[part.getLength()];
        }
        receiveCount++;

        byte[] temp = Base64.decode(part.getData(), Base64.DEFAULT);
        System.arraycopy(temp, 0, receiveData, part.getSeq() * block, temp.length);

        // 所有数据包都接收完毕，拼接完整的图片数据
        if (receiveCount >= part.getLength() / block + 1) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(receiveData, 0, receiveData.length);
            String desc = String.format("%s 收到服务端消息：%s", DateUtil.getNowTime(), part.getName());

            // 回到主线程展示图片与描述文字
            runOnUiThread(() -> {
                tv_res.setText(desc);
                iv_res.setImageBitmap(bitmap);
            });
        }
    }

    // 取消接收传来的图片消息, 关闭 Socket 连接
    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.off("receive_image");
        if (socket.connected()) {
            socket.disconnect();
        }
        socket.close();
    }
}
