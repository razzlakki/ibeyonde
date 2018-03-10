package com.technorabit.ibeyonde;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabItem;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.applandeo.rcalender.CalendarView;
import com.applandeo.rcalender.listeners.OnSelectDateListener;

import java.util.Calendar;

public class HistorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initGUI();
    }

    private void initGUI() {
        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnSelectDateListener(new OnSelectDateListener() {
            @Override
            public void onSelect(Calendar calendar) {
                Toast.makeText(HistorActivity.this, "SignatureDoesNotMatch", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
