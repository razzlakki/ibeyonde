package com.technorabit.ibeyonde;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.EditText;

import com.dms.datalayerapi.network.Http;
import com.dms.datalayerapi.util.GetUrlMaker;
import com.technorabit.ibeyonde.connection.HttpClientManager;
import com.technorabit.ibeyonde.constants.AppConstants;
import com.technorabit.ibeyonde.fragment.dailog.BaseFragmentDialog;
import com.technorabit.ibeyonde.model.LoginRes;
import com.technorabit.ibeyonde.util.Util;

public class RegistrationActivity extends BaseActivity {

    private EditText username, email, password, re_password;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z_-]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initGui();
    }

    private void initGui() {
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        re_password = findViewById(R.id.re_password);
        findViewById(R.id.registerBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerOnClick();
            }
        });
    }

    private void registerOnClick() {
        if (validate()) {
            doRegistration();
        }
    }

    private void doRegistration() {
        GetUrlMaker getUrlMaker = GetUrlMaker.getMaker();
        getUrlMaker.addParams("user_name", username.getText().toString());
        getUrlMaker.addParams("user_email", email.getText().toString());
        getUrlMaker.addParams("user_password_new", password.getText().toString());
        getUrlMaker.addParams("user_password_repeat", re_password.getText().toString());
        HttpClientManager client = HttpClientManager.get(RegistrationActivity.this);
        client.diskCacheEnable(false);
        final BaseFragmentDialog dialog = Util.showBaseLoading(RegistrationActivity.this);
        client.new NetworkTask<Void, LoginRes>(LoginRes.class, Http.POST) {
            @Override
            protected void onPostExecute(LoginRes loginData) {
                super.onPostExecute(loginData);
                dialog.dismiss();
                if (loginData != null) {
                    if (loginData.code == 200) {
                        startActivity(new Intent(RegistrationActivity.this, Dashboard.class));
                        finish();
                    }
                    Snackbar.make(getWindow().getDecorView().getRootView(), loginData.message, Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(getWindow().getDecorView().getRootView(), "invalid inputs", Snackbar.LENGTH_LONG).show();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getUrlMaker.getPathForGetUrl(AppConstants.REGISTER.replace(AppConstants.REPLACER, "")), getUrlMaker.getAsPostPerms());
    }

    private boolean validate() {
        if (username.getText().toString().length() <= 0) {
            username.setError("Please check username and try again");
            return false;
        }
        if (email.getText().toString().length() <= 0 || !email.getText().toString().matches(emailPattern)) {
            email.setError("Please check email and try again");
            return false;
        }
        if (password.getText().toString().length() <= 0) {
            password.setError("Please check password and try again");
            return false;
        }
        if (!re_password.getText().toString().equals(password.getText().toString())) {
            re_password.setError("password not matching");
            return false;
        }
        return true;
    }
}
