package com.example.theychat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.theychat.bean.UserInfo;
import com.google.gson.Gson;

@SuppressLint(value = {"DefaultLocale", "SetTextI18n"})
public class JsonConvertActivity extends AppCompatActivity {
    private TextView tv_json;
    private UserInfo user;
    private String jsonStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_json_convert);

        user = new UserInfo("阿四", 25, 165L, 50.0f);
        jsonStr = new Gson().toJson(user);
        tv_json = findViewById(R.id.tv_json);

        findViewById(R.id.btn_origin_json).setOnClickListener(view -> {
            jsonStr = new Gson().toJson(user);
            tv_json.setText("JSON串内容如下：\n" + jsonStr);
        });

        findViewById(R.id.btn_convert_json).setOnClickListener(view -> {
            UserInfo newUser = new Gson().fromJson(jsonStr, UserInfo.class);
            String desc = String.format("\n\t姓名=%s\n\t年龄=%d\n\t身高=%d\n\t体重=%f",
                    newUser.name, newUser.age, newUser.height, newUser.weight);
            tv_json.setText("从JSON串解析而来的用户信息如下：" + desc);
        });
    }
}
