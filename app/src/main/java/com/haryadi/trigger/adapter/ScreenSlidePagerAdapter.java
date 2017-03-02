package com.haryadi.trigger.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.haryadi.trigger.fragment.MapFragment;
import com.haryadi.trigger.fragment.ProfileFragment;

public class ScreenSlidePagerAdapter extends FragmentPagerAdapter {

    public static final int NUM_PAGES = 2;

    public ScreenSlidePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ProfileFragment();
            case 1:
                return new MapFragment();

        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
