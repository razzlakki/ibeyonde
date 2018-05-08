package com.technorabit.ibeyonde;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.technorabit.ibeyonde.util.SharedUtil;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Settings");
        }
        if (!SharedUtil.get(SettingsActivity.this).hasKey("DefaultView") || SharedUtil.get(SettingsActivity.this).getString("DefaultView").equalsIgnoreCase("Motion")) {
            ((RadioButton) findViewById(R.id.defaultMotion)).setChecked(true);
            ((RadioButton) findViewById(R.id.live)).setChecked(false);
        } else {
            ((RadioButton) findViewById(R.id.defaultMotion)).setChecked(false);
            ((RadioButton) findViewById(R.id.live)).setChecked(true);
        }
        ((RadioGroup) findViewById(R.id.radio_parrent)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.defaultMotion)
                    SharedUtil.get(SettingsActivity.this).store("DefaultView", "Motion");
                else
                    SharedUtil.get(SettingsActivity.this).store("DefaultView", "Live");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        setResult(RESULT_OK);
        finish();
        return super.onOptionsItemSelected(item);
    }
}
