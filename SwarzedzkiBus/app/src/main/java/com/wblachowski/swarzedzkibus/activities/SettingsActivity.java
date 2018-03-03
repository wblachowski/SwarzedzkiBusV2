package com.wblachowski.swarzedzkibus.activities;

import android.app.Activity;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import com.wblachowski.swarzedzkibus.R;

/**
 * Created by wblachowski on 3/1/2018.
 */

public class SettingsActivity extends AppCompatPreferenceActivity {
    private static final String TAG = SettingsActivity.class.getSimpleName();
    private static Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity=this;
        // load settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
    }

    public static class MainPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);
            Preference[] preferences = new Preference[]{findPreference(getString(R.string.key_auto_update)),findPreference(getString(R.string.key_departure_time))};
            loadPreferencesValues(preferences);
            for(Preference preference : preferences){
                bindPreferenceSummaryToValue(preference);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
    }

    private static void loadPreferencesValues(Preference[] preferences){
        for(Preference preference : preferences){
            String stringValue="";
            if(preference instanceof CheckBoxPreference) {
                if(((CheckBoxPreference) preference).isChecked()){
                    if (preference.getKey().equals(preference.getContext().getString(R.string.key_auto_update))) {
                            stringValue = preference.getContext().getString(R.string.summary_auto_update_enabled);
                    } else if (preference.getKey().equals(preference.getContext().getString(R.string.key_departure_time))) {
                            stringValue = preference.getContext().getString(R.string.summary_departure_time_enabled);
                    }
                }else{
                    if (preference.getKey().equals(preference.getContext().getString(R.string.key_auto_update))) {
                        stringValue = preference.getContext().getString(R.string.summary_auto_update_disabled);
                    } else if (preference.getKey().equals(preference.getContext().getString(R.string.key_departure_time))) {
                        stringValue = preference.getContext().getString(R.string.summary_departure_time_disabled);
                    }
                }
                preference.setSummary(stringValue);
            }
        }
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue="";

            if (preference.getKey().equals(preference.getContext().getString(R.string.key_auto_update))) {
                if ((Boolean)newValue) {
                    stringValue = preference.getContext().getString(R.string.summary_auto_update_enabled);
                } else {
                    stringValue = preference.getContext().getString(R.string.summary_auto_update_disabled);
                }
            } else if (preference.getKey().equals(preference.getContext().getString(R.string.key_departure_time))) {
                if ((Boolean)newValue) {
                    stringValue = preference.getContext().getString(R.string.summary_departure_time_enabled);
                } else {
                    stringValue = preference.getContext().getString(R.string.summary_departure_time_disabled);
                }
            }

            if(!stringValue.equals(""))preference.setSummary(stringValue);

            PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean(preference.getKey(),(Boolean)newValue).apply();
            return true;
        }
    };
}

