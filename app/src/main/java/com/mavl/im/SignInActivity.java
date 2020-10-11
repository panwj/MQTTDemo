package com.mavl.im;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.mavl.im.event.ConnectEvent;
import com.mavl.im.sdk.IMConnectionClient;
import com.mavl.im.sdk.Logger;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SignInActivity extends BaseActivity {

    private TextView mSignInTv, mErrorTv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_layout);
        registerEventBus();

        mSignInTv = findViewById(R.id.tv_signin);
        mErrorTv = findViewById(R.id.tv_error);

        mSignInTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    IMConnectionClient client = IMManager.getInstance(getApplicationContext()).createDefaultAccount1();
                    client.doConnect(null);
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.e("client1 do connect exception : " + e.toString());
                    hideProgressDialog();
                    if (mErrorTv != null) {
                        mErrorTv.setText(e.toString());
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterEventBus();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ConnectEvent(ConnectEvent connectEvent) {
        switch (connectEvent.status) {
            case NONE:
                break;
            case ERROR:
                hideProgressDialog();
                if (mErrorTv != null) {
                    mErrorTv.setText(connectEvent.error);
                }
                break;
            case CONNECTING:
                showProgressDialog();
                break;
            case CONNECTED:
                hideProgressDialog();
                if (mErrorTv != null) {
                    mErrorTv.setText("登陆成功, 点击进入主页");
                    mErrorTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            launchMainActivity();
                            finish();
                        }
                    });
                }
                break;
            case DISCONNECTING:
                break;
            case DISCONNECTED:
                break;
        }
    }

    private void registerEventBus() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    private void unregisterEventBus() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
