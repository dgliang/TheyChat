package com.example.theychat.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.theychat.R;
import com.example.theychat.bean.EntityInfo;
import com.example.theychat.util.ChatUtil;

import java.util.List;

// 用户适配器
public class EntityListAdapter extends BaseAdapter {
    private Context context;
    private List<EntityInfo> userList; // 用户信息列表

    public EntityListAdapter(Context context, List<EntityInfo> user_list) {
        this.context = context;
        userList = user_list;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int i) {
        return userList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = LayoutInflater.from(context).inflate(R.layout.item_user, null);

            holder.iv_portrait = convertView.findViewById(R.id.iv_portrait);
            holder.tv_name = convertView.findViewById(R.id.tv_name);
            holder.tv_relation = convertView.findViewById(R.id.tv_relation);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        EntityInfo user = userList.get(position);
        holder.tv_name.setText(user.name);
        holder.tv_relation.setText(user.relation);
        Drawable drawable = ChatUtil.getPortraitByName(context, user.name);
        holder.iv_portrait.setImageDrawable(drawable);
        return convertView;
    }

    // 定义一个视图持有者，以便重用列表项的视图资源
    public final class ViewHolder {
        public ImageView iv_portrait;   // 用户头像
        public TextView tv_name;        // 用户名称
        public TextView tv_relation;    // 用户关系
    }
}
