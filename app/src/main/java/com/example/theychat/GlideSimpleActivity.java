package com.example.theychat;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class GlideSimpleActivity extends AppCompatActivity {
    private ImageView iv_network;
    private String imageURL = "http://b247.photo.store.qq.com/psb?/V11ZojBI312o2K/63aY8a4M5quhi.78*krOo7k3Gu3cknuclBJHS3g1fpc!/b/dDXWPZMlBgAA";
    private String[] modeArray = {"默认", "容纳居中fitCenter", "居中剪裁centerCrop", "居中入内centerInside", "圆形剪裁circleCrop"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_glide_simple);

        iv_network = findViewById(R.id.iv_network);
        CheckBox ck_fitxy = findViewById(R.id.ck_fitxy);

        ck_fitxy.setOnCheckedChangeListener((buttonView, isChecked) -> {
            iv_network.setScaleType(isChecked ? ImageView.ScaleType.FIT_XY : ImageView.ScaleType.FIT_CENTER);
        });
        initModeSpinner();
    }

    // 初始化显示方式的下拉框
    private void initModeSpinner() {
        ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this, R.layout.item_select, modeArray);
        Spinner sp_show_mode = findViewById(R.id.sp_show_mode);
        sp_show_mode.setPrompt("请选择显示方式");
        sp_show_mode.setAdapter(modeAdapter);
        sp_show_mode.setSelection(0);
        sp_show_mode.setOnItemSelectedListener(new ModeSelectedListener());
    }

    class ModeSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            showNetworkImage(arg2); // 加载并显示网络图片
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    private void showNetworkImage(int mode) {
        if (mode == 0) {
            // 使用图像视图默认的显示方式
            Glide.with(this).load(imageURL).into(iv_network);
        } else if (mode == 1) {
            // 显示方式为容纳居中fitCenter
            Glide.with(this).load(imageURL).fitCenter().into(iv_network);
        } else if (mode == 2) {
            // 显示方式为居中剪裁centerCrop
            Glide.with(this).load(imageURL).centerCrop().into(iv_network);
        } else if (mode == 3) {
            // 显示方式为居中入内centerInside
            Glide.with(this).load(imageURL).centerInside().into(iv_network);
        } else if (mode == 4) {
            // 显示方式为圆形剪裁circleCrop
            Glide.with(this).load(imageURL).circleCrop().into(iv_network);
        }
    }
}
