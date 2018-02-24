package com.technorabit.ibeyonde;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.RadioGroup;

import com.dms.datalayerapi.network.Http;
import com.dms.datalayerapi.util.GetUrlMaker;
import com.technorabit.ibeyonde.adaptor.VideoListAdaptor;
import com.technorabit.ibeyonde.amin.AnimUtil;
import com.technorabit.ibeyonde.connection.HttpClientManager;
import com.technorabit.ibeyonde.constants.AppConstants;
import com.technorabit.ibeyonde.fragment.dailog.BaseFragmentDialog;
import com.technorabit.ibeyonde.util.Util;

public class Dashboard extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RadioGroup toggelButton;
    private View tab1View, tab2View;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        initGUI();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        initRecycleView();
//        initData(0);
        initToggelView();
    }

    private void initGUI() {
        toggelButton = findViewById(R.id.radio_group);
        tab1View = findViewById(R.id.motion_layout);
        tab2View = findViewById(R.id.live_layout);
    }

    private void initToggelView() {
        toggelButton.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio1) {
                    tab1View.setVisibility(View.VISIBLE);
                    tab2View.setVisibility(View.VISIBLE);
                    tab1View.startAnimation(AnimUtil.inFromLeftAnimation(new AnimUtil.AnimationListner() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            super.onAnimationEnd(animation);
//                            tab1View.setVisibility(View.GONE);
                        }
                    }));
                    tab2View.startAnimation(AnimUtil.outToRightAnimation(new AnimUtil.AnimationListner() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            super.onAnimationEnd(animation);
                            tab2View.setVisibility(View.GONE);
                        }
                    }));
                } else {
                    tab1View.setVisibility(View.VISIBLE);
                    tab2View.setVisibility(View.VISIBLE);
                    Animation anim = AnimUtil.outToLeftAnimation(new AnimUtil.AnimationListner() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            super.onAnimationEnd(animation);
                            tab1View.setVisibility(View.GONE);
                        }
                    });
                    tab1View.startAnimation(anim);
                    Animation anim1 = AnimUtil.inFromRightAnimation(new AnimUtil.AnimationListner() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            super.onAnimationEnd(animation);
//                            tab1View.setVisibility(View.GONE);
                        }
                    });
                    tab2View.startAnimation(anim1);
                }
            }
        });
    }

    private void initRecycleView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        VideoListAdaptor videoListAdaptor = new VideoListAdaptor();
        recyclerView.setAdapter(videoListAdaptor);
    }

    private void initData(int menuId) {
        GetUrlMaker getUrlMaker = GetUrlMaker.getMaker();

        HttpClientManager client = HttpClientManager.get(this);
        client.diskCacheEnable(false);
        final BaseFragmentDialog dialog = Util.showBaseLoading(this);
        client.new NetworkTask<Void, String>(String.class, Http.POST) {
            @Override
            protected void onPostExecute(String loginData) {
                super.onPostExecute(loginData);
                dialog.dismiss();
                if (loginData != null) {
                    Log.e("DeviceList Res **", loginData);
                } else {
                    Snackbar.make(getWindow().getDecorView().getRootView(), "invalid inputs", Snackbar.LENGTH_LONG).show();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getUrlMaker.getPathForGetUrl(AppConstants.GET_DEVICE_LIST));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_motion) {
//            // Handle the camera action
//        } else if (id == R.id.nav_live) {
//
//        } else
        if (id == R.id.nav_alerts) {

        } else if (id == R.id.nav_events) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
