package com.example.theychat.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.theychat.fragment.FriendListFragment;
import com.example.theychat.fragment.GroupListFragment;
import com.example.theychat.fragment.MyInfoFragment;

public class TheyChatAdapter extends FragmentPagerAdapter {
    public TheyChatAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new FriendListFragment();
        } else if (position == 1) {
            return new GroupListFragment();
        } else if (position == 2) {
            return new MyInfoFragment();
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
