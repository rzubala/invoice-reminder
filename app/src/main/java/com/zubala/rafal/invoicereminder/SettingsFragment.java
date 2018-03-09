package com.zubala.rafal.invoicereminder;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.widget.Toast;

import com.zubala.rafal.invoicereminder.utils.AlarmUtils;


/**
 * Created by rzubala on 03.03.18.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_invoice);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen prefScreen = getPreferenceScreen();
        int count = prefScreen.getPreferenceCount();

        for (int i = 0; i < count; i++) {
            Preference preference = prefScreen.getPreference(i);
            if (preference instanceof TimePreference) {
                TimePreference tpreference = (TimePreference) preference;
                int timestamp = tpreference.getTime();
                setTimePreference(tpreference, timestamp);
            } else if (preference.getKey().equals(getString(R.string.pref_days_before_notification_key))) {
                String value = sharedPreferences.getString(preference.getKey(), "");
                setDaysPreference(preference, value);
            }
        }

        Preference preference = findPreference(getString(R.string.pref_days_before_notification_key));
        preference.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (null != preference) {
            if (preference instanceof TimePreference) {
                TimePreference tpreference = (TimePreference) preference;
                int timestamp = tpreference.getTime();
                setTimePreference(tpreference, timestamp);
                preference.setOnPreferenceChangeListener(this);
                AlarmUtils.startAlarm(getContext());
            } else if (key.equals(getString(R.string.pref_show_notification_key))) {
                AlarmUtils.startAlarm(getContext());
            } else if (key.equals(getString(R.string.pref_days_before_notification_key))) {
                String value = sharedPreferences.getString(preference.getKey(), "");
                setDaysPreference(preference, value);
                AlarmUtils.startAlarm(getContext());
            }
        }
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        DialogFragment dialogFragment = null;
        if (preference instanceof TimePreference) {
            dialogFragment = TimePreferenceDialogFragmentCompat.newInstance(preference.getKey());
        }

        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(this.getFragmentManager(),"android.support.v7.preference.PreferenceFragment.DIALOG");
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Toast error = Toast.makeText(getContext(), getString(R.string.newInvoiceError), Toast.LENGTH_SHORT);

        if (preference.getKey().equals(getString(R.string.pref_days_before_notification_key))) {
            String stringDays = (String) newValue;
            try {
                int days = Integer.parseInt(stringDays);
                return true;
            } catch (NumberFormatException nfe) {
                error.show();
                return false;
            }
        }
        return true;
    }

    private void setTimePreference(TimePreference preference, int value) {
        if (value <= 0) {
            return;
        }
        int hours = value / 60;
        int minutes = value % 60;
        String valueStr = String.format("%02d", hours)+":"+String.format("%02d", minutes);
        preference.setSummary(valueStr);
    }

    private void setDaysPreference(Preference preference, String value) {
        preference.setSummary(value);
    }
}
