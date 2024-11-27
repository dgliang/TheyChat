package com.example.theychat.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.theychat.GroupChatActivity;
import com.example.theychat.MainApplication;
import com.example.theychat.R;
import com.example.theychat.adapter.EntityListAdapter;
import com.example.theychat.bean.EntityInfo;
import com.example.theychat.widget.NoScrollListView;

import java.util.ArrayList;
import java.util.List;

public class GroupListFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String TAG = "GroupListFragment";
    protected View mView;
    protected Context mContext;
    private NoScrollListView nslv_group;
    private List<EntityInfo> mGroupList = new ArrayList<>(); // 群聊列表

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        EntityInfo group = mGroupList.get(i);

        Intent intent = new Intent(mContext, GroupChatActivity.class);
        intent.putExtra("self_name", MainApplication.getInstance().theyChatName);
        intent.putExtra("group_name", group.name);
        startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 简单起见硬编码几个群组
        mGroupList.add(new EntityInfo("Android开发技术交流群", ""));
        mGroupList.add(new EntityInfo("摄影爱好者", ""));
        mGroupList.add(new EntityInfo("人工智能学习讨论群", ""));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();

        // 获取活动页面的上下文
        mView = inflater.inflate(R.layout.fragment_group_list, container, false);
        TextView tv_title = mView.findViewById(R.id.tv_title);
        tv_title.setText(String.format("群聊（%d）", mGroupList.size()));
        mView.findViewById(R.id.iv_back).setOnClickListener(view -> getActivity().finish());
        nslv_group = mView.findViewById(R.id.nslv_group);
        EntityListAdapter adapter = new EntityListAdapter(mContext, mGroupList);
        nslv_group.setAdapter(adapter);
        nslv_group.setOnItemClickListener(this);
        return mView;
    }
}
