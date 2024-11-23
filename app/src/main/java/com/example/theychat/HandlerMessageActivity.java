package com.example.theychat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

@SuppressLint("HandlerLeak")
public class HandlerMessageActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_message; // 用于显示新闻的播放状态
    private boolean isPlaying = false; // 标记新闻是否正在播放
    private int BEGIN = 0, SCROLL = 1, END = 2; // 新闻播放的状态码
    private String[] mNewsArray = {
            "北斗导航系统正式开通，定位精度媲美GPS",
            "黑人之死引发美国各地反种族主义运动", "印度运营商禁止华为中兴反遭诺基亚催债",
            "贝鲁特发生大爆炸全球紧急救援黎巴嫩", "日本货轮触礁毛里求斯造成严重漏油污染"
    }; // 包含要播放的新闻内容


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_handler_message);

        tv_message = findViewById((R.id.tv_message));

        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_start) {
            if (!isPlaying) {
                isPlaying = true;
                new PlayThread().start();
            }
        } else if (view.getId() == R.id.btn_stop){
            isPlaying = false;
        }
    }

    // 新闻播放线程
    private class PlayThread extends Thread {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(BEGIN);

            // 当 isPlaying 为 true 时，进入循环执行新闻播放
            while (isPlaying) {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Message message = Message.obtain();
                message.what = SCROLL;

                // 随机从新闻数组中选择一条新闻，并发送消息更新 UI
                message.obj = mNewsArray[new Random().nextInt(5)];
                mHandler.sendMessage(message);
            }
            mHandler.sendEmptyMessage(END);
            isPlaying = false;
        }
    }

    // 定义一个 Handler，用于接收子线程传递的消息并更新 UI
    private Handler mHandler = new Handler(Looper.myLooper()) {
        public void handleMessage(Message msg) {
            String desc = tv_message.getText().toString();

            // 根据不同的消息类型，更新 TextView 的内容
            if (msg.what == BEGIN) {
                desc = String.format("%s\n%s %s", desc, DateUtil.getNowTime(), "开始播放新闻");
            } else if (msg.what == SCROLL) {
                desc = String.format("%s\n%s %s", desc, DateUtil.getNowTime(), msg.obj);
            } else if (msg.what == END) { // 结束播放
                desc = String.format("%s\n%s %s", desc, DateUtil.getNowTime(), "新闻播放结束");
            }
            tv_message.setText(desc);
        }
    };
}
