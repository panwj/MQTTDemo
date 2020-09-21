package com.mavl.im.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.LinkedList;

import com.mavl.im.BaseFragment;

public class MainViewPagerAdapter extends FragmentPagerAdapter {

    private LinkedList<BaseFragment> mFragments = new LinkedList<>();

    public MainViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setFragments(LinkedList<BaseFragment> items) {
        mFragments.clear();
        if (items != null) mFragments.addAll(items);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments != null ? mFragments.get(position) : null;
    }

    @Override
    public int getCount() {
        return mFragments != null ? mFragments.size() : 0;
    }
}
