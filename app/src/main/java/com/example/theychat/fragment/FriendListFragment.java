package com.example.theychat.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.theychat.FriendChatActivity;
import com.example.theychat.MainApplication;
import com.example.theychat.R;
import com.example.theychat.adapter.EntityListAdapter;
import com.example.theychat.bean.EntityInfo;
import com.example.theychat.widget.NoScrollListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.socket.client.Socket;

public class FriendListFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String TAG = "FriendListFragment";
    protected View view;
    protected Context context;
    private TextView tv_title;
    private NoScrollListView nslv_friend;
    private Map<String, EntityInfo> friendsMap = new HashMap<>();
    private List<EntityInfo> friendsList = new ArrayList<>(); // 好友列表
    private EntityListAdapter adapter;
    private Socket socket;
    private Handler handler = new Handler(Looper.myLooper());
    private Runnable refresh = () -> refreshFriends(); // 好友列表的刷新任务

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        EntityInfo friend = friendsList.get(i);

        // 跳转到与指定好友聊天的界面
        Intent intent = new Intent(context, FriendChatActivity.class);
        intent.putExtra("self_name", MainApplication.getInstance().theyChatName);
        intent.putExtra("friend_name", friend.name);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 获取活动页面的上下文
        context = getActivity();
        view = inflater.inflate(R.layout.fragment_friend_list, container, false);
        initView();
        initSocket();
        return view;
    }

    // 初始化视图
    private void initView() {
        tv_title = view.findViewById(R.id.tv_title);

        tv_title.setText(String.format("好友（%d）", friendsList.size()));

        view.findViewById(R.id.iv_back).setOnClickListener(view -> getActivity().finish());
        nslv_friend = view.findViewById(R.id.nslv_friend);

        adapter = new EntityListAdapter(context, friendsList);
        nslv_friend.setAdapter(adapter);
        nslv_friend.setOnItemClickListener(this);
    }

    // 初始化套接字
    private void initSocket() {
        socket = MainApplication.getInstance().getSocket();

        // 开始监听好友上线事件
        socket.on("friend_online", (args) -> {
            String friend_name = (String) args[0];
            Log.d(TAG, "friend_name=" + friend_name);

            if (friend_name != null) {
                // 把刚上线的好友加入好友列表
                friendsMap.put(friend_name, new EntityInfo(friend_name, "好友"));
                friendsList.clear();
                friendsList.addAll(friendsMap.values());
                handler.postDelayed(refresh, 200);
            }
        });

        // 开始监听好友下线事件
        socket.on("friend_offline", (args) -> {
            String friend_name = (String) args[0];

            // 从好友列表移除已下线的好友
            friendsMap.remove(friend_name);
            friendsList.clear();
            friendsList.addAll(friendsMap.values());
            handler.postDelayed(refresh, 200);
        });

        // 通知服务器“我已上线”
        socket.emit("self_online", MainApplication.getInstance().theyChatName);
    }

    // 刷新好友列表
    private void refreshFriends() {
        // 防止频繁刷新造成列表视图崩溃
        handler.removeCallbacks(refresh);
        tv_title.setText(String.format("好友（%d）", friendsList.size()));
        adapter.notifyDataSetChanged();
    }

    // 通知服务器 “我已下线”，取消监听好友上线和下线事件
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        socket.emit("self_offline", MainApplication.getInstance().theyChatName);
        socket.off("friend_online");
        socket.off("friend_offline");
    }
}
