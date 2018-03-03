package com.wblachowski.swarzedzkibus.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.wblachowski.swarzedzkibus.R;
import com.wblachowski.swarzedzkibus.data.SettingsDataBaseHelper;
import com.wblachowski.swarzedzkibus.fragments.TimeTableFragment;

import java.util.Calendar;

public class TimeTableActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TimeTableFragment[] fragments;
    private String stopId;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private boolean isFavourite = false;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        setToolbarInfo();
        stopId = getIntent().getStringExtra("id").toString();
        fragments = new TimeTableFragment[3];
        setStartingTab();
    }

    private void setStartingTab() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        //mon-friday = 2-6
        //sunday = 1
        //saturday = 7
        if (dayOfWeek >= 2 && dayOfWeek <= 6) {
            mViewPager.setCurrentItem(0);
        } else if (dayOfWeek == 7) {
            mViewPager.setCurrentItem(1);
        } else {
            mViewPager.setCurrentItem(2);
        }
    }

    public String getStopId() {
        return stopId;
    }

    private void setToolbarInfo() {
        TextView nr = (TextView) findViewById(R.id.time_table_bus_nr);
        TextView stopName = (TextView) findViewById(R.id.time_table_stop_name);
        TextView direction = (TextView) findViewById(R.id.time_table_direction);
        nr.setText(getIntent().getStringExtra("nr").toString());
        stopName.setText(getIntent().getStringExtra("stopName").toString());
        direction.setText(getIntent().getStringExtra("direction").toString());
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        this.menu=menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_table, menu);
        new Thread(new Runnable() {
            @Override
            public void run() {
                isFavourite = SettingsDataBaseHelper.getInstance(null).isStopFavourite(getIntent().getStringExtra("id"), getIntent().getStringExtra("direction"));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MenuItem menuItem = menu.findItem(R.id.action_favourite);
                        menuItem.setTitle(isFavourite ? getString(R.string.action_unfavourite) : getString(R.string.action_favourite));
                    }
                });
            }
        }).start();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        final Activity activity = this;
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_favourite) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final boolean succesful;
                    if(isFavourite){
                        succesful = SettingsDataBaseHelper.getInstance(activity).deleteFromFavourites(getIntent().getStringExtra("id"),getIntent().getStringExtra("direction"));
                        if(succesful)isFavourite=false;
                    }else{
                        succesful = SettingsDataBaseHelper.getInstance(activity).insertIntoFavourites(getIntent().getStringExtra("id"),getIntent().getStringExtra("direction"));
                        if(succesful)isFavourite=true;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(succesful) {
                                Toast.makeText(activity, isFavourite ?  "Dodano do ulubionych" : "Usunięto z ulubionych",
                                        Toast.LENGTH_LONG).show();
                                menu.findItem(R.id.action_favourite).setTitle(isFavourite ? getString(R.string.action_unfavourite) : getString(R.string.action_favourite));
                            }
                        }
                    });
                }
            }).start();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (fragments[position] == null) {
                fragments[position] = new TimeTableFragment();
                fragments[position].setType(position);
                fragments[position].setStopId(stopId);
            }
            return fragments[position];
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "PON - PT";
                case 1:
                    return "SOBOTY";
                case 2:
                    return "NIEDZIELE";
            }
            return null;
        }
    }
}
