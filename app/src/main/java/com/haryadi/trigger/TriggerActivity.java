package com.haryadi.trigger;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.haryadi.trigger.adapter.ScreenSlidePagerAdapter;

/**
 * Created by aharyadi on 1/23/17.
 */

public class TriggerActivity extends AppCompatActivity {

    private ViewPager mPager;
    private ScreenSlidePagerAdapter mAdapter;

    FloatingActionButton wifiEnable;
    FloatingActionButton bluetoothEnable;
    FloatingActionButton locationEnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPager = (ViewPager) findViewById(R.id.viewPager);
        mAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);

        wifiEnable = (FloatingActionButton)findViewById(R.id.wifi_enable);
        wifiEnable.setOnClickListener(getOnClick());
        bluetoothEnable = (FloatingActionButton)findViewById(R.id.bluetooth_enable);
        bluetoothEnable.setOnClickListener(getOnClick());
        locationEnable = (FloatingActionButton)findViewById(R.id.location_enable);
        locationEnable.setOnClickListener(getOnClick());
    }

    public View.OnClickListener getOnClick(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.wifi_enable){
                    Toast t = Toast.makeText(getApplicationContext(),"Wifi Clicked",Toast.LENGTH_SHORT);
                    t.show();
                }
                else if(v.getId() == R.id.bluetooth_enable){
                    Toast t = Toast.makeText(getApplicationContext(),"bluetooth Clicked",Toast.LENGTH_SHORT);
                    t.show();
                }
                else if(v.getId() == R.id.location_enable){
                    Toast t = Toast.makeText(getApplicationContext(),"location Clicked",Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        };
    }


    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

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
}
