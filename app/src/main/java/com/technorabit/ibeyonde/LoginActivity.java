package com.technorabit.ibeyonde;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.dms.datalayerapi.network.Http;
import com.dms.datalayerapi.util.GetUrlMaker;
import com.technorabit.ibeyonde.connection.HttpClientManager;
import com.technorabit.ibeyonde.constants.AppConstants;
import com.technorabit.ibeyonde.fragment.dailog.BaseFragmentDialog;
import com.technorabit.ibeyonde.model.LoginRes;
import com.technorabit.ibeyonde.util.SharedUtil;
import com.technorabit.ibeyonde.util.Util;

public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initGUI();
    }

    private void initGUI() {
        findViewById(R.id.loginBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin(((EditText) findViewById(R.id.username)).getText().toString(), ((EditText) findViewById(R.id.password)).getText().toString());
            }
        });

        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });
        findViewById(R.id.forgot_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });
    }

    private void doLogin(final String username, final String password) {

        GetUrlMaker getUrlMaker = GetUrlMaker.getMaker();
        HttpClientManager client = HttpClientManager.get(LoginActivity.this);
        client.setUsername(username);
        client.setPassword(password);
        client.diskCacheEnable(false);
        final BaseFragmentDialog dialog = Util.showBaseLoading(LoginActivity.this);
        client.new NetworkTask<Void, LoginRes>(LoginRes.class, Http.GET) {
            @Override
            protected void onPostExecute(LoginRes loginData) {
                super.onPostExecute(loginData);
                dialog.dismiss();
                if (loginData != null) {
                    if (loginData.code == 200) {
                        SharedUtil.get(LoginActivity.this).addToSet("username", username).addToSet("password", password).addToSet(SharedUtil.IS_LOGIN, true).commitSet();
                        startActivity(new Intent(LoginActivity.this, Dashboard.class));
                        finish();
                    }
                    Snackbar.make(getWindow().getDecorView().getRootView(), loginData.message, Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(getWindow().getDecorView().getRootView(), "invalid inputs", Snackbar.LENGTH_LONG).show();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getUrlMaker.getPathForGetUrl(AppConstants.LOGIN.replace(AppConstants.REPLACER, "")));
    }
}
