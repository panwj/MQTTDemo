package com.mavl.im.main;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.mavl.im.BaseFragment;
import com.mavl.im.R;

public class SettingFragment extends BaseFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
