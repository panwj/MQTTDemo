package com.mavl.im;


import android.os.Bundle;

import com.mavl.im.main.MainFragment;

public class MainActivity extends BaseActivity {

    private MainFragment mMainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MqttManager.release();
    }

    private void init() {
        mMainFragment = new MainFragment();
        Bundle args = new Bundle();
        mMainFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mMainFragment)
                .commitAllowingStateLoss();
    }
}