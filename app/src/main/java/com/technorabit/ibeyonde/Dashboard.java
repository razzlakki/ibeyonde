package com.technorabit.ibeyonde;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.dms.datalayerapi.network.Http;
import com.dms.datalayerapi.util.CommonPoolExecutor;
import com.dms.datalayerapi.util.GetUrlMaker;
import com.technorabit.ibeyonde.adaptor.TabViewPagerAdapter;
import com.technorabit.ibeyonde.connection.HttpClientManager;
import com.technorabit.ibeyonde.constants.AppConstants;
import com.technorabit.ibeyonde.fragment.TabFragment;
import com.technorabit.ibeyonde.fragment.dailog.BaseFragmentDialog;
import com.technorabit.ibeyonde.util.SharedUtil;
import com.technorabit.ibeyonde.util.Util;

public class Dashboard extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabViewPagerAdapter adapter;

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
        View headerLayout = navigationView.getHeaderView(0);
        ((TextView) headerLayout.findViewById(R.id.username_side_nav)).setText(SharedUtil.get(this).getString("username"));
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        CommonPoolExecutor.get().clean();
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    private void initGUI() {
        viewPager = findViewById(R.id.viewpager);
        setupViewPager();
        ((CheckBox) findViewById(R.id.checkBox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (adapter != null) {
                    for (int i = 0; i < adapter.getCount(); i++) {
                        ((TabFragment) adapter.getItem(i)).updateGrid(isChecked);
                    }
                }
            }
        });
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager() {
        updateView();
    }

    private void updateView() {
        adapter = new TabViewPagerAdapter(getSupportFragmentManager());
        if (!(SharedUtil.get(this).hasKey("DefaultView")) || SharedUtil.get(this).getString("DefaultView").equalsIgnoreCase("Motion")) {
            adapter.addFragment(TabFragment.getInstance(TabFragment.Type.MOTION), "Motion");
            adapter.addFragment(TabFragment.getInstance(TabFragment.Type.LIVE), "Live");
        } else {
            adapter.addFragment(TabFragment.getInstance(TabFragment.Type.LIVE), "Live");
            adapter.addFragment(TabFragment.getInstance(TabFragment.Type.MOTION), "Motion");
        }
        viewPager.setAdapter(adapter);
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, 101);
        } else if (id == R.id.nav_logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
        startActivity(new Intent(this, Dashboard.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                ((TabFragment) adapter.getItem(i)).isDestroyed();
            }
        }
    }
}
