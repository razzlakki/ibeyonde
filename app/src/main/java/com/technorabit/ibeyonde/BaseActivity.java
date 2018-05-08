package com.technorabit.ibeyonde;

import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/**
 * Created by raja on 12/02/18.
 */

public class BaseActivity extends AppCompatActivity {
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
