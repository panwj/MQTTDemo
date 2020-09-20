package test.com.mqttdemo.main;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import test.com.mqttdemo.BaseFragment;
import test.com.mqttdemo.R;

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
