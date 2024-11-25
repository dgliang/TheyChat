package com.example.theychat;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.theychat.constant.NetConst;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkhttpCallActivity extends AppCompatActivity {
    private final static String TAG = "OkhttpCallActivity";
    private final static String URL_STOCK = "https://hq.sinajs.cn/list=s_sh000001";  // 股票数据接口URL
    private final static String URL_LOGIN = NetConst.HTTP_PREFIX + "login";
    private LinearLayout ll_login;
    private EditText et_username;
    private EditText et_password;
    private TextView tv_result;
    private int checkID = R.id.rb_get;  // 默认选中的单选按钮资源编号，表示GET请求方式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_okhttp_call);

        ll_login = findViewById(R.id.ll_login);
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        tv_result = findViewById(R.id.tv_result);
        RadioGroup rg_method = findViewById(R.id.rg_method);

        // 设置单选按钮组的监听器，动态显示或隐藏登录表单
        rg_method.setOnCheckedChangeListener((group, tmpCheckedId) -> {
            checkID = tmpCheckedId;
            int visibility = checkID == R.id.rb_get ? View.GONE : View.VISIBLE;
            ll_login.setVisibility(visibility);
        });

        // 设置发送请求按钮的点击事件监听
        findViewById(R.id.btn_send).setOnClickListener(v -> {
            if (checkID == R.id.rb_get) {
                Get();  // 发起GET请求
            } else if (checkID == R.id.rb_post_form) {
                PostForm();  // 发起POST表单请求
            } else if (checkID == R.id.rb_post_json) {
                PostJson();  // 发起POST JSON请求
            }
        });
    }

    // 发起GET请求
    private void Get() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .header("Accept-Language", "zh-CN")
                .header("Referer", "https://finance.sina.com.cn")
                .url(URL_STOCK)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                runOnUiThread(() -> tv_result.setText("调用股指接口报错："+e.getMessage()));  // 请求失败时更新UI
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                String resp = response.body().string();  // 获取响应的内容
                runOnUiThread(() -> tv_result.setText("调用股指接口返回：\n"+resp));  // 请求成功时更新UI
            }
        });
    }

    // 发起POST请求（表单格式）
    private void PostForm() {
        String username = et_username.getText().toString();
        String password = et_password.getText().toString();
        FormBody body = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().post(body).url(URL_LOGIN).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> tv_result.setText("调用登录接口报错："+e.getMessage()));  // 请求失败时更新UI
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                String resp = response.body().string();
                runOnUiThread(() -> tv_result.setText("调用登录接口返回：\n"+resp));  // 请求成功时更新UI
            }
        });
    }

    // 发起POST请求（JSON格式）
    private void PostJson() {
        String username = et_username.getText().toString();
        String password = et_password.getText().toString();
        String jsonString = "";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", username);
            jsonObject.put("password", password);
            jsonString = jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(jsonString, MediaType.parse("text/plain;charset=utf-8"));  // 创建请求体
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().post(body).url(URL_LOGIN).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> tv_result.setText("调用登录接口报错："+e.getMessage()));  // 请求失败时更新UI
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                String resp = response.body().string();
                runOnUiThread(() -> tv_result.setText("调用登录接口返回：\n"+resp));  // 请求成功时更新UI
            }
        });
    }
}
