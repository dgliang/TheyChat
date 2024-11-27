package com.example.theychat.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.theychat.MainApplication;
import com.example.theychat.R;
import com.example.theychat.util.ChatUtil;
import com.example.theychat.widget.InputDialog;

import io.socket.client.Socket;

public class MyInfoFragment extends Fragment {
    private static final String TAG = "MyInfoFragment";
    protected View mView;
    protected Context mContext;
    private ImageView iv_portrait;
    private TextView tv_nick;
    private Socket mSocket;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();

        mView = inflater.inflate(R.layout.fragment_my_info, container, false);
        initView();
        showPortrait();

        mSocket = MainApplication.getInstance().getSocket();
        return mView;
    }

    // 初始化视图
    private void initView() {
        TextView tv_title = mView.findViewById(R.id.tv_title);
        tv_title.setText("个人信息");

        mView.findViewById(R.id.iv_back).setOnClickListener(v -> getActivity().finish());
        iv_portrait = mView.findViewById(R.id.iv_portrait);
        tv_nick = mView.findViewById(R.id.tv_nick);
        mView.findViewById(R.id.ll_nick).setOnClickListener(v -> modifyNickName());
    }

    // 显示用户昵称和用户头像
    private void showPortrait() {
        String nickName = MainApplication.getInstance().theyChatName;
        tv_nick.setText(nickName);
        Drawable drawable = ChatUtil.getPortraitByName(mContext, nickName);
        iv_portrait.setImageDrawable(drawable);
    }

    // 修改用户昵称
    private void modifyNickName() {
        String nickName = MainApplication.getInstance().theyChatName;

        InputDialog newDialog = new InputDialog(mContext, nickName, 0,
                "请输入新的昵称", (idt, content, seq) -> {
            MainApplication.getInstance().theyChatName = content;
            showPortrait();

            // 完成改名操作
            mSocket.emit("self_offline", nickName);
            mSocket.emit("self_online", MainApplication.getInstance().theyChatName);
        });
        newDialog.show();
    }
}
