package com.zubala.rafal.invoicereminder;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;


/**
 * Created by rzubala on 03.03.18.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_invoice);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }
}
