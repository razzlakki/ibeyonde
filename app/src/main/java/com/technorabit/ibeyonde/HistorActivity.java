package com.technorabit.ibeyonde;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dms.collapsiblecalendarview.data.Day;
import com.dms.collapsiblecalendarview.widget.CollapsibleCalendar;
import com.dms.datalayerapi.network.Http;
import com.dms.datalayerapi.util.CommonPoolExecutor;
import com.dms.datalayerapi.util.GetUrlMaker;
import com.technorabit.ibeyonde.connection.HttpClientManager;
import com.technorabit.ibeyonde.constants.AppConstants;
import com.technorabit.ibeyonde.util.SharedUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HistorActivity extends BaseActivity {

    private String date;
    private String UDUID;
    private ImageView history_video_view;
    private View progresss_bar;
    private boolean isDestoyed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        isDestoyed = false;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initGUI();
        initData();
        Calendar c = Calendar.getInstance();
        setDate(getDateFromCal(new Day(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))));
        updateOnUI();
    }

    private void initData() {
        setUDUID(getIntent().getStringExtra("udid"));
    }

    private void initGUI() {
        history_video_view = findViewById(R.id.history_video_view);
        progresss_bar = findViewById(R.id.progresss_bar);
        final CollapsibleCalendar calendarView = findViewById(R.id.calendarView);

        calendarView.setCalendarListener(new CollapsibleCalendar.CalendarListener() {
            @Override
            public void onDaySelect() {
                progresss_bar.setVisibility(View.VISIBLE);
                setDate(getDateFromCal(calendarView.getSelectedDay()));
                updateOnUI();
            }

            @Override
            public void onItemClick(View v) {

            }

            @Override
            public void onDataUpdate() {

            }

            @Override
            public void onMonthChange() {

            }

            @Override
            public void onWeekChange(int position) {

            }
        });
    }

    private String getDateFromCal(Day calendar) {
        Calendar cal = Calendar.getInstance();
        cal.set(calendar.getYear(), calendar.getMonth(), calendar.getDay());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String dateString = dateFormat.format(cal.getTime());
        Log.e("Date", "Final DAte :::::" + dateString);
        return dateString;
    }

    private void updateOnUI() {
        GetUrlMaker getUrlMaker = GetUrlMaker.getMaker();
        HttpClientManager client = HttpClientManager.get(this);
        client.setUsername(SharedUtil.get(this).getString("username"));
        client.setPassword(SharedUtil.get(this).getString("password"));
        client.diskCacheEnable(false);
        String url = AppConstants.GET_HISTORY.replace(AppConstants.REPLACER, "");
        url = url + "&uuid=" + getUDUID() + "&date=" + getDate();
        client.new NetworkTask<Void, String>(null, Http.GET) {
            @Override
            protected void onPostExecute(final String liveUrl) {
                super.onPostExecute(liveUrl);
                progresss_bar.setVisibility(View.GONE);
                if (liveUrl != null) {
                    parseData(liveUrl);
                } else {

                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getUrlMaker.getPathForGetUrl(url));
    }

    private void parseData(String liveUrl) {
        try {
            final JSONArray jsonArray = new JSONArray(liveUrl);

            CommonPoolExecutor.get().clean().startDownload(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Message msg = new Message();
                        try {
                            msg.obj = (jsonArray.getJSONObject(i).getString("url")).replace("https", "http");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (!isDestoyed)
                            handler.sendMessage(msg);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.placeholder);
            requestOptions.error(R.drawable.error);
            Glide.with(HistorActivity.this).setDefaultRequestOptions(requestOptions).load((String) msg.obj).thumbnail(1).into(history_video_view);
            super.handleMessage(msg);
        }
    };


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUDUID() {
        return UDUID;
    }

    public void setUDUID(String UDUID) {
        this.UDUID = UDUID;
    }


    @Override
    protected void onDestroy() {
        isDestoyed = true;
        super.onDestroy();
    }
}
