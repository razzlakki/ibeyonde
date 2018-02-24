package com.technorabit.ibeyonde;

import android.content.Intent;
import android.os.Bundle;

import com.technorabit.ibeyonde.util.SharedUtil;

public class SplashActivity extends BaseActivity {

    private boolean isDestroy = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        satartThread();
    }

    private void satartThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startActivity();
            }
        }).start();
    }

    private void startActivity() {
        if (!isDestroy) {
            if (SharedUtil.get(this).getBoolean(SharedUtil.IS_LOGIN))
                startActivity(new Intent(this, Dashboard.class));
            else
                startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroy = true;
    }
}
