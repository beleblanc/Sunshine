package com.caultive.benoit.sunshine.app;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Created by benoit on 2014-11-01.
 */
public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }


    /**
     * Attaches a listener so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)
     */
    private void bindPreferenceSummaryToValue(Preference preference){
        preference.setOnPreferenceChangeListener(this);

        onPreferenceChange(preference, PreferenceManager
                                        .getDefaultSharedPreferences(preference.getContext())
                                        .getString(preference.getKey(),""));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.city_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.country_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.unit_key)));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value){
        String stringValue = value.toString();

        if(preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if(prefIndex > 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }

        }else{
            preference.setSummary(stringValue);
        }


        return true;
    }
}
