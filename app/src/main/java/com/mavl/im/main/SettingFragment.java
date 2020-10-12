package com.mavl.im.main;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;

import com.mavl.im.BaseFragment;
import com.mavl.im.R;
import com.mavl.im.util.SharedPreferencesUtil;

public class SettingFragment extends BaseFragment {

    private CheckBox mClient1Retained, mClient1CleanSession, mClient1AutomaticReconnect;
    private RadioGroup mClient1Qos;

    private CheckBox mClient2Retained, mClient2CleanSession, mClient2AutomaticReconnect;
    private RadioGroup mClient2Qos;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        initView(view);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    void initView(View view) {
        mClient1Retained = view.findViewById(R.id.client1_retained);
        mClient1CleanSession = view.findViewById(R.id.client1_cleanSession);
        mClient1AutomaticReconnect = view.findViewById(R.id.client1_automaticReconnect);
        mClient1Qos = view.findViewById(R.id.client1_rg_qos);

        mClient1Retained.setChecked((boolean) SharedPreferencesUtil.get(getActivity(), SharedPreferencesUtil.PREF_CLIENT1_RETAINED, true));
        mClient1CleanSession.setChecked((boolean) SharedPreferencesUtil.get(getActivity(), SharedPreferencesUtil.PREF_CLIENT1_CLEAN_SESSION, true));
        mClient1AutomaticReconnect.setChecked((boolean) SharedPreferencesUtil.get(getActivity(), SharedPreferencesUtil.PREF_CLIENT1_AUTO_RECONNECT, true));
        int client1_qos = (int) SharedPreferencesUtil.get(getActivity(), SharedPreferencesUtil.PREF_CLIENT1_QOS, 1);
        if (client1_qos == 0) {
            mClient1Qos.check(R.id.client1_0);
        } else if (client1_qos == 1) {
            mClient1Qos.check(R.id.client1_1);
        } else if (client1_qos == 2) {
            mClient1Qos.check(R.id.client1_2);
        }


        mClient2Retained = view.findViewById(R.id.client2_retained);
        mClient2CleanSession = view.findViewById(R.id.client2_cleanSession);
        mClient2AutomaticReconnect = view.findViewById(R.id.client2_automaticReconnect);
        mClient2Qos = view.findViewById(R.id.client2_rg_qos);

        mClient2Retained.setChecked((boolean) SharedPreferencesUtil.get(getActivity(), SharedPreferencesUtil.PREF_CLIENT2_RETAINED, true));
        mClient2CleanSession.setChecked((boolean) SharedPreferencesUtil.get(getActivity(), SharedPreferencesUtil.PREF_CLIENT2_CLEAN_SESSION, true));
        mClient2AutomaticReconnect.setChecked((boolean) SharedPreferencesUtil.get(getActivity(), SharedPreferencesUtil.PREF_CLIENT2_AUTO_RECONNECT, true));
        int client2_qos = (int) SharedPreferencesUtil.get(getActivity(), SharedPreferencesUtil.PREF_CLIENT2_QOS, 1);
        if (client2_qos == 0) {
            mClient2Qos.check(R.id.client2_0);
        } else if (client2_qos == 1) {
            mClient2Qos.check(R.id.client2_1);
        } else if (client2_qos == 2) {
            mClient2Qos.check(R.id.client2_2);
        }

        mClient1Retained.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtil.put(getActivity(), SharedPreferencesUtil.PREF_CLIENT1_RETAINED, isChecked);
            }
        });
        mClient1CleanSession.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtil.put(getActivity(), SharedPreferencesUtil.PREF_CLIENT1_CLEAN_SESSION, isChecked);
            }
        });
        mClient1AutomaticReconnect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtil.put(getActivity(), SharedPreferencesUtil.PREF_CLIENT1_AUTO_RECONNECT, isChecked);
            }
        });
        mClient1Qos.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.client1_0:
                        SharedPreferencesUtil.put(getActivity(), SharedPreferencesUtil.PREF_CLIENT1_QOS, 0);
                        break;
                    case R.id.client1_1:
                        SharedPreferencesUtil.put(getActivity(), SharedPreferencesUtil.PREF_CLIENT1_QOS, 1);
                        break;
                    case R.id.client1_2:
                        SharedPreferencesUtil.put(getActivity(), SharedPreferencesUtil.PREF_CLIENT1_QOS, 2);
                        break;
                }
            }
        });


        mClient2Retained.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtil.put(getActivity(), SharedPreferencesUtil.PREF_CLIENT2_RETAINED, isChecked);
            }
        });
        mClient2CleanSession.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtil.put(getActivity(), SharedPreferencesUtil.PREF_CLIENT2_CLEAN_SESSION, isChecked);
            }
        });
        mClient2AutomaticReconnect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtil.put(getActivity(), SharedPreferencesUtil.PREF_CLIENT2_AUTO_RECONNECT, isChecked);
            }
        });
        mClient2Qos.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.client2_0:
                        SharedPreferencesUtil.put(getActivity(), SharedPreferencesUtil.PREF_CLIENT2_QOS, 0);
                        break;
                    case R.id.client2_1:
                        SharedPreferencesUtil.put(getActivity(), SharedPreferencesUtil.PREF_CLIENT2_QOS, 1);
                        break;
                    case R.id.client2_2:
                        SharedPreferencesUtil.put(getActivity(), SharedPreferencesUtil.PREF_CLIENT2_QOS, 2);
                        break;
                }
            }
        });
    }
}
