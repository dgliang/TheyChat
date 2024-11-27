package com.example.theychat;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.theychat.adapter.TheyChatAdapter;

import io.socket.client.Socket;

public class TheyChatActivity extends AppCompatActivity {
    private ViewPager vp_content;
    private RadioGroup rg_bar;
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_they_chat);

        initView();

        socket = MainApplication.getInstance().getSocket();
        socket.connect(); // 建立 Socket 连接
    }

    // 初始化视图
    private void initView() {
        vp_content = findViewById(R.id.vp_content);

        // 构建一个翻页适配器
        TheyChatAdapter adapter = new TheyChatAdapter(getSupportFragmentManager());
        vp_content.setAdapter(adapter);

        // 给翻页视图添加页面变更监听器
        vp_content.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // 选中指定位置的单选按钮
                rg_bar.check(rg_bar.getChildAt(position).getId());
            }
        });

        // 设置单选组的选中监听器
        rg_bar = findViewById(R.id.rg_bar);
        rg_bar.setOnCheckedChangeListener((group, checkedId) -> {
            for (int pos = 0; pos < rg_bar.getChildCount(); pos++) {
                RadioButton tab = (RadioButton) rg_bar.getChildAt(pos);
                if (tab.getId() == checkedId) {
                    vp_content.setCurrentItem(pos);
                }
            }
        });
    }

    // 取消接收传来的文本消息, 关闭 Socket 连接
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (socket.connected()) {
            socket.disconnect();
        }
    }
}
