package com.example.theychat;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.theychat.util.DateUtil;

import java.util.Random;

public class ThreadUiActivity extends AppCompatActivity {
    private TextView tv_message;
    private boolean isPlaying = false;
    private String[] newsArray = {
            "北斗导航系统正式开通，定位精度媲美GPS",
            "黑人之死引发美国各地反种族主义运动", "印度运营商禁止华为中兴反遭诺基亚催债",
            "贝鲁特发生大爆炸全球紧急救援黎巴嫩", "日本货轮触礁毛里求斯造成严重漏油污染"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_thread_ui);

        tv_message = findViewById(R.id.tv_message);

        findViewById(R.id.btn_start).setOnClickListener(view -> {
            if (!isPlaying) {
                isPlaying = true;
                new Thread(this::broadcastNews).start();
            }
        });
        findViewById(R.id.btn_stop).setOnClickListener(view -> isPlaying = false);
    }

    private void broadcastNews() {
        String startDesc = String.format("%s\n%s %s", tv_message.getText().toString(), DateUtil.getNowTime(), "开始播放新闻");
        runOnUiThread(() -> tv_message.setText(startDesc));
        while (isPlaying) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String runDesc = String.format("%s\n%s %s", tv_message.getText().toString(), DateUtil.getNowTime(),
                    newsArray[new Random().nextInt(5)]);
            runOnUiThread(() -> tv_message.setText(runDesc));
        }
        String endDesc = String.format("%s\n%s %s", tv_message.getText().toString(), DateUtil.getNowTime(), "新闻播放结束，谢谢观看");
        runOnUiThread(() -> tv_message.setText(endDesc));
        isPlaying = false;
    }
}
