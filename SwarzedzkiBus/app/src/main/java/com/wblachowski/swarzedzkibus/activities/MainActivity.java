package com.wblachowski.swarzedzkibus.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.wblachowski.swarzedzkibus.R;
import com.wblachowski.swarzedzkibus.data.DataBaseUpdater;
import com.wblachowski.swarzedzkibus.data.MainDataBaseHelper;
import com.wblachowski.swarzedzkibus.data.SettingsDataBaseHelper;
import com.wblachowski.swarzedzkibus.fragments.AllFragment;
import com.wblachowski.swarzedzkibus.fragments.FavouritesFragment;
import com.wblachowski.swarzedzkibus.fragments.SearchFragment;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    FavouritesFragment favouritesFragment;
    AllFragment allFragment;
    SearchFragment searchFragment;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private MainDataBaseHelper myDbHelper;
    private DataBaseUpdater myDbUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createDataBase();
        createSettingsDataBase();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_refresh:
                        updateDataBase();
                        break;
                    case R.id.action_settings:
                        showSettingsActivity();
                        break;
                    case R.id.action_about:
                        showAboutDialog();
                        break;
                }
                return true;
            }
        });
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    favouritesFragment.refreshStopsList(false);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabLayout.getTabAt(0).setIcon(R.drawable.tab_favourites);
        tabLayout.getTabAt(1).setIcon(R.drawable.tab_all);
        tabLayout.getTabAt(2).setIcon(R.drawable.tab_search);

        myDbUpdater = new DataBaseUpdater(this);
        checkForUpdates();
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

    @Override
    protected void onResume()
    {
        super.onResume();
        if(mViewPager.getCurrentItem()==0) {
            if(favouritesFragment!=null){
                favouritesFragment.refreshStopsList(false);
            }
        }
    }

    public MainDataBaseHelper getDataBaseHelper() {
        return myDbHelper;
    }

    private void createDataBase() {
        myDbHelper = MainDataBaseHelper.getInstance(this);

        try {

            myDbHelper.createDataBase();

        } catch (IOException ioe) {

            System.out.println(ioe.getMessage());

        }

        try {

            myDbHelper.openDataBase();

        } catch (Exception sqle) {

            System.out.println(sqle.getMessage());

        }
    }

    private void createSettingsDataBase() {
        try {
            SettingsDataBaseHelper.getInstance(this).createDataBase();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void showAboutDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_information, null);
        ((TextView) view.findViewById(R.id.about_update_date)).setText(SettingsDataBaseHelper.getInstance(this).getLastUpdateString());
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.about_title).setNegativeButton(R.string.about_close, null);
        dialog.setView(view);
        dialog.show();
    }

    private void showSettingsActivity() {
        try {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void updateDataBase() {
        final ProgressDialog dialog = ProgressDialog.show(
                this, "", "Sprawdzanie aktualizacji");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isUpdateAvailable = myDbUpdater.isUpdateAvailable();

                if (isUpdateAvailable) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.setMessage("Pobieram rozkład");
                        }
                    });
                    boolean successfulUpdate = myDbUpdater.update();
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    displayUpdated(successfulUpdate);
                } else {
                    displayUpToDate();
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }
        }).start();
    }

    private void checkForUpdates() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean val = pref.getBoolean(getString(R.string.key_auto_update), true);
        if (!val) return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (myDbUpdater.isUpdateAvailable()) {
                    displayUpdateAvailable();
                }
            }
        }).start();
    }

    private void displayUpdateAvailable() {
        final Activity activity = this;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(activity.getString(R.string.update_available_title))
                        .setMessage(activity.getString(R.string.update_available_msg))
                        .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                updateDataBase();
                            }
                        })
                        .setNegativeButton("Nie", null).show();
            }
        });
    }

    private void displayUpToDate() {
        final Activity activity = this;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Rozkład jest aktualny")
                        .setPositiveButton("Ok", null).show();
            }
        });
    }

    private void displayUpdated(boolean successful) {
        final String title, msg;
        if (successful) {
            title = "Sukces";
            msg = "Zaktualizowano rozkład";
        } else {
            title = "Błąd";
            msg = "Nie udało się zaktualizować rozkładu";
        }
        final Activity activity = this;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(title)
                        .setMessage(msg)
                        .setPositiveButton("Ok", null).show();
            }
        });
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
            switch (position) {
                case 0:
                    favouritesFragment = new FavouritesFragment();
                    return favouritesFragment;
                case 1:
                    allFragment = new AllFragment();
                    return allFragment;
                case 2:
                    searchFragment = new SearchFragment();
                    return searchFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

    }
}
