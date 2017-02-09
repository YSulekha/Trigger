package com.haryadi.trigger;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.haryadi.trigger.adapter.ScreenSlidePagerAdapter;
import com.haryadi.trigger.fragment.EditCreateProfileFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TriggerActivity extends AppCompatActivity {

    @BindView(R.id.viewPager) ViewPager mPager;
    private ScreenSlidePagerAdapter mAdapter;

 /*   @BindView(R.id.wifi_enable)FloatingActionButton wifiEnable;
    @BindView(R.id.wifi_disable) FloatingActionButton wifiDisable;
    @BindView(R.id.bluetooth_enable)FloatingActionButton bluetoothEnable;
    @BindView(R.id.bluetooth_disable)FloatingActionButton bluetoothDisable;
    @BindView(R.id.location_enable)FloatingActionButton locationEnable;
    @BindView(R.id.floatingActionMenu)FloatingActionMenu floatingActionMenu;
    @BindView(R.id.toolbar)Toolbar toolbar;*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pager);
        ButterKnife.bind(this);
      //  setSupportActionBar(toolbar);

        mAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
   /*     wifiEnable.setOnClickListener(getOnClick(floatingActionMenu));
        bluetoothEnable.setOnClickListener(getOnClick(floatingActionMenu));
        locationEnable.setOnClickListener(getOnClick(floatingActionMenu));
        wifiDisable.setOnClickListener(getOnClick(floatingActionMenu));
        bluetoothDisable.setOnClickListener(getOnClick(floatingActionMenu));*/
    }

    public View.OnClickListener getOnClick(final FloatingActionMenu fm){
        FloatingActionButton b;
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.wifi_enable){
                    Toast t = Toast.makeText(getApplicationContext(),"Wifi Clicked",Toast.LENGTH_SHORT);
                    t.show();
                    fm.close(true);
                    showEditDialog("WIFI");
                }
                else if(v.getId() == R.id.bluetooth_enable){
                    Toast t = Toast.makeText(getApplicationContext(),"bluetooth Clicked",Toast.LENGTH_SHORT);
                    t.show();
                    fm.close(true);
                    showEditDialog("BLUETOOTH");
                }
                else if(v.getId() == R.id.location_enable){
                    Toast t = Toast.makeText(getApplicationContext(),"Add a location:By addingPin or Search for a location",Toast.LENGTH_SHORT);
                    t.show();
                    fm.close(true);
                   // showEditDialog("LOCATION");
                }
                if(v.getId() == R.id.wifi_disable){
                    Toast t = Toast.makeText(getApplicationContext(),"Wifi Disable Clicked",Toast.LENGTH_SHORT);
                    t.show();
                    fm.close(true);
                    showEditDialog("WIFI DISABLE");
                }
                if(v.getId() == R.id.bluetooth_disable){
                    Toast t = Toast.makeText(getApplicationContext(),"Bluetooth Disable Clicked",Toast.LENGTH_SHORT);
                    t.show();
                    fm.close(true);
                    showEditDialog("BLUETOOTH DISABLE");
                }
            }
        };
    }

    private void showEditDialog(String title) {
        FragmentManager fm = getSupportFragmentManager();
        EditCreateProfileFragment editNameDialogFragment = EditCreateProfileFragment.newInstance(title,false,null);
        editNameDialogFragment.show(fm, title);
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
