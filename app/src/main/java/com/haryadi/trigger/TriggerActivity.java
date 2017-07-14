package com.haryadi.trigger;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.haryadi.trigger.adapter.ScreenSlidePagerAdapter;
import com.haryadi.trigger.data.TriggerContract;
import com.haryadi.trigger.fragment.EditCreateLocationFragment;
import com.haryadi.trigger.fragment.EditCreateProfileFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TriggerActivity extends AppCompatActivity {

    public
    @BindView(R.id.viewPager)
    ViewPager mPager;
    public static TriggerActivity mInstance = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pager);
        ButterKnife.bind(this);

        getWindow().getDecorView().setFitsSystemWindows(true);
        mInstance = this;

        //When clicked on Widget, open edit dialog fragment
        Intent intent = getIntent();
        String frag = getIntent().getStringExtra("Trigger");
        if (frag != null) {
            if (intent.hasExtra("IdValue") && intent.hasExtra("triggerPoint")) {
                openFrag(getIntent());
            }
        }

        ScreenSlidePagerAdapter mAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());

        //Open Map fragment
        mPager.setCurrentItem(1);
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 1) {
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() + 1);
        }
    }

    public void openFrag(Intent intent) {
        long id = intent.getLongExtra("IdValue", -1);
        String trigger = intent.getStringExtra("triggerPoint");
        Uri uri = TriggerContract.TriggerEntry.buildTaskUri(id);
        if (trigger.equals(getString(R.string.location))) {
            FragmentManager fm = getSupportFragmentManager();
            EditCreateLocationFragment editLocationDialogFragment = EditCreateLocationFragment.newInstance("Edit", true, uri,null, null);
            editLocationDialogFragment.show(fm, "Edit");
        } else {
            FragmentManager fm = getSupportFragmentManager();
            EditCreateProfileFragment editNameDialogFragment = EditCreateProfileFragment.newInstance("Edit", true, uri);
            editNameDialogFragment.show(fm, "Edit");
        }
    }

}

