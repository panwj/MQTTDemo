package com.mavl.im;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class GroupActivity extends BaseActivity {

    public static void launchGroupActivity(Activity activity, String clientId) {
        try {
            Intent intent = new Intent(activity, GroupActivity.class);
            intent.putExtra("client_id", clientId);
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
