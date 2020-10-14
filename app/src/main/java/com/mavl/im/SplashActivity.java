package com.mavl.im;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.mavl.im.sdk.IMMessageBroker;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IMMessageBroker client1 = IMManager.getInstance(getApplicationContext()).getClient("client1");
        if (client1 == null || !client1.isConnect()) {
            launchSignInActivity();
        } else {
            launchMainActivity();
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void launchSignInActivity() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
