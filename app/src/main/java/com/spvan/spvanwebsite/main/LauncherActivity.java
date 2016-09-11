package com.spvan.spvanwebsite.main;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.spvan.spvanwebsite.ActivityLauncherBinding;
import com.spvan.spvanwebsite.R;

/**
 * Created by apple on 16/8/6.
 */

public class LauncherActivity extends AppCompatActivity{
    private ActivityLauncherBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_launcher);
        Toast.makeText(getApplicationContext(),"sdf",Toast.LENGTH_LONG).show();
        InitEvent();
    }

    public void InitEvent()
    {
        CountDownTimer timer = new CountDownTimer(3000,1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
//                startActivity(new Intent(LauncherActivity.this,MainActivity.class));
//                finish();
            }
        };
        timer.start();
    }
}
