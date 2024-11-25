package com.example.theychat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.theychat.constant.NetConst;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkhttpUploadActivity extends AppCompatActivity {
    private final static String TAG = "OkhttpUploadActivity";
    private final static String URL_REGISTER = NetConst.HTTP_PREFIX + "register";
    private EditText et_username;
    private EditText et_password;
    private TextView tv_result;
    private ImageView iv_avatar;
    private int CHOOSE_CODE = 3; // 只在相册挑选图片的请求码
    private List<String> pathList = new ArrayList<>(); // 头像文件的路径列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_okhttp_upload);

        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        iv_avatar = findViewById(R.id.iv_avatar);
        tv_result = findViewById(R.id.tv_result);

        // 创建意图，打开系统相册选择图片，只允许选择一张图片
        iv_avatar.setOnClickListener(v -> {
            Intent albumIntent = new Intent(Intent.ACTION_GET_CONTENT);
            albumIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
            albumIntent.setType("image/*");
            startActivityForResult(albumIntent, CHOOSE_CODE);
        });

        // 设置注册按钮的点击事件监听器
        findViewById(R.id.btn_register).setOnClickListener(v -> uploadFile());
    }

    // 上传文件
    private void uploadFile() {
        if (pathList.size() <= 0) {
            Toast.makeText(this, "请选择待上传的用户头像", Toast.LENGTH_SHORT).show();
            return;
        }
        // 创建分段内容的建造器对象
        MultipartBody.Builder builder = new MultipartBody.Builder();
        String username = et_username.getText().toString();
        String password = et_password.getText().toString();
        if (!TextUtils.isEmpty(username)) {
            // 往建造器对象添加文本格式的分段数据
            builder.addFormDataPart("username", username);
            builder.addFormDataPart("password", password);
        }
        for (String path : pathList) { // 添加多个附件
            File file = new File(path); // 根据文件路径创建文件对象
            // 往建造器对象添加图像格式的分段数据
            builder.addFormDataPart("image", file.getName(), RequestBody.create(file, MediaType.parse("image/*"))
            );
        }
        RequestBody body = builder.build(); // 根据建造器生成请求结构
        OkHttpClient client = new OkHttpClient(); // 创建一个okhttp客户端对象
        // 创建一个POST方式的请求结构
        Request request = new Request.Builder().post(body).url(URL_REGISTER).build();
        Call call = client.newCall(request); // 根据请求结构创建调用对象
        // 加入HTTP请求队列。异步调用，并设置接口应答的回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) { // 请求失败
                // 回到主线程操纵界面
                runOnUiThread(() -> tv_result.setText("调用注册接口报错：\n"+e.getMessage()));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException { // 请求成功
                String resp = response.body().string();
                // 回到主线程操纵界面
                runOnUiThread(() -> tv_result.setText("调用注册接口返回：\n"+resp));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // 检查请求码是否为 CHOOSE_CODE 且结果码为 RESULT_OK，表示用户成功选择了文件
        if (resultCode == RESULT_OK && requestCode == CHOOSE_CODE) {
            pathList.clear();

            // 如果 intent 中包含有效的 URI 数据，保存选中的图片，并获取图片路径
            if (intent.getData() != null) {
                String imagePath = saveImage(intent.getData());
                pathList.add(imagePath);
            }
        }
    }

    // 把指定 URI 的图片复制一份到内部存储空间，并返回存储路径
    private String saveImage(Uri data) {
        String uriStr = data.toString();
        String imageName = uriStr.substring(uriStr.lastIndexOf("/")+1);
        String imagePath = String.format("%s/%s.jpg", getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(),
                imageName);

        // 自动缩小后的位图
        Bitmap bitmap = BitmapUtil.getAutoZoomImage(this, data);

        // 把位图数据保存到指定路径的图片文件
        BitmapUtil.saveImage(imagePath, bitmap);
        iv_avatar.setImageBitmap(bitmap);
        return imagePath;
    }
}
