package com.technorabit.ibeyonde.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

/**
 * Created by raja on 07/05/18.
 */

public class AutoRotateUtil {

    int currentPos = 0;

    public AutoRotateUtil(ViewPager viewPager) {
        viewPager.addOnAdapterChangeListener(new ViewPager.OnAdapterChangeListener() {
            @Override
            public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {
                if (newAdapter.getCount() > 0) {
                    currentPos = 0;
                    initThread(viewPager, newAdapter);
                }
            }
        });
    }

    private void initThread(final ViewPager viewPager, final PagerAdapter newAdapter) {
        viewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentPos > newAdapter.getCount())
                    currentPos = 0;
                viewPager.setCurrentItem(++currentPos);
                initThread(viewPager, newAdapter);
            }
        }, 5000);
    }
}
