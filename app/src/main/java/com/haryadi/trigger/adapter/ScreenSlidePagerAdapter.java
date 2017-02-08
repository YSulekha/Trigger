package com.haryadi.trigger.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.haryadi.trigger.fragment.MainFragment_bac;
import com.haryadi.trigger.fragment.ProfileFragment;

/**
 * Created by aharyadi on 1/23/17.
 */

public class ScreenSlidePagerAdapter extends FragmentPagerAdapter {

    public static final int NUM_PAGES = 2;

    public ScreenSlidePagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new ProfileFragment();
            case 1:
                return new MainFragment_bac();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
