package com.technorabit.ibeyonde;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.applandeo.rcalender.CalendarView;
import com.applandeo.rcalender.EventDay;
import com.applandeo.rcalender.listeners.EventClickLister;
import com.applandeo.rcalender.listeners.OnDayClickListener;
import com.applandeo.rcalender.listeners.OnSelectDateListener;

import java.util.Calendar;

public class HistoryActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("History");
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        initGUI();
    }

    private void initGUI() {
        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnSelectDateListener(new OnSelectDateListener() {
            @Override
            public void onSelect(Calendar calendar) {
                Log.e("Date", calendar.toString());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
