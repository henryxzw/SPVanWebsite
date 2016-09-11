package com.spvan.spvanwebsite.main;

import android.app.Application;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/9/11 0011.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this,"sdf",Toast.LENGTH_LONG).show();
    }
}
