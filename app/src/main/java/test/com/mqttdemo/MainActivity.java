package test.com.mqttdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import test.com.mqttdemo.main.MainFragment;

public class MainActivity extends AppCompatActivity {

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