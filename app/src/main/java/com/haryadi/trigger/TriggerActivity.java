package com.haryadi.trigger;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.haryadi.trigger.adapter.ScreenSlidePagerAdapter;
import com.haryadi.trigger.data.TriggerContract;
import com.haryadi.trigger.fragment.EditCreateLocationFragment;
import com.haryadi.trigger.fragment.EditCreateProfileFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TriggerActivity extends AppCompatActivity {

    @BindView(R.id.viewPager) ViewPager mPager;

    public static final String ACTION_DATA_UPDATED = "com.haryadi.trigger.ACTION_DATA_UPDATED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pager);
        ButterKnife.bind(this);

        getWindow().getDecorView().setFitsSystemWindows(true);
        Intent intent = getIntent();
        String frag = getIntent().getStringExtra("Trigger");
        if(frag!=null){
            if(intent.hasExtra("IdValue") && intent.hasExtra("triggerPoint")) {
                openFrag(getIntent());
            }
        }
      //  Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.slide_right);
        //getWindow().setEnterTransition(transition);

        //  setSupportActionBar(toolbar);

        ScreenSlidePagerAdapter mAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mPager.setCurrentItem(1);
    }

    public void openFrag(Intent intent){
        long id = intent.getLongExtra("IdValue",-1);
        String trigger = intent.getStringExtra("triggerPoint");
        Uri uri = TriggerContract.TriggerEntry.buildTaskUri(id);
        if(trigger.equals("LOCATION")){
            FragmentManager fm = getSupportFragmentManager();
            EditCreateLocationFragment editLocationDialogFragment = EditCreateLocationFragment.newInstance("Edit", true, uri,null);
            editLocationDialogFragment.show(fm, "Edit");
        }
        else {
            FragmentManager fm = getSupportFragmentManager();
            EditCreateProfileFragment editNameDialogFragment = EditCreateProfileFragment.newInstance("Edit", true, uri);
            editNameDialogFragment.show(fm, "Edit");
        }
    }

 /*   @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public static void notifyWidgets(Context mContext) {
        Intent intent = new Intent(ACTION_DATA_UPDATED);
        mContext.sendBroadcast(intent);
    }
}
