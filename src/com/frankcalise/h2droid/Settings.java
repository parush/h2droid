package com.frankcalise.h2droid;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Settings extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	
	private static final String OPT_AMOUNT = "SETTING_GOAL";
	private static final String OPT_AMOUNT_DEF = "64";
	private static final String[] OPT_FAV_AMOUNT_DEF = {"8", "16", "16.9", "20", "33.8"};
	private static final double DEFAULT_AMOUNT = 64.0;
	private static final double DEFAULT_FAV_AMOUNT = 8.0;
	private static final String[] OPT_FAV_AMOUNT = {"FAV_AMOUNT_ONE", 
													"FAV_AMOUNT_TWO",
													"FAV_AMOUNT_THREE",
													"FAV_AMOUNT_FOUR",
													"FAV_AMOUNT_FIVE"};
	
	private EditTextPreference mEditTextPref1;
	private EditTextPreference mEditTextPref2;
	private EditTextPreference mEditTextPref3;
	private EditTextPreference mEditTextPref4;
	private EditTextPreference mEditTextPref5;
	
	@Override
	protected void onCreate(Bundle savedInstanceBundle) {
		super.onCreate(savedInstanceBundle);
		
		// Load XML prefs file
		addPreferencesFromResource(R.xml.settings);
		
		// Get reference to preferences to update summaries
		mEditTextPref1 = (EditTextPreference)getPreferenceScreen().findPreference(OPT_FAV_AMOUNT[0]);
		mEditTextPref2 = (EditTextPreference)getPreferenceScreen().findPreference(OPT_FAV_AMOUNT[1]);
		mEditTextPref3 = (EditTextPreference)getPreferenceScreen().findPreference(OPT_FAV_AMOUNT[2]);
		mEditTextPref4 = (EditTextPreference)getPreferenceScreen().findPreference(OPT_FAV_AMOUNT[3]);
		mEditTextPref5 = (EditTextPreference)getPreferenceScreen().findPreference(OPT_FAV_AMOUNT[4]);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// Setup initial values
		mEditTextPref1.setSummary("Current amount is " + getPreferenceScreen().getSharedPreferences().getString(OPT_FAV_AMOUNT[0], OPT_FAV_AMOUNT_DEF[0]));
		mEditTextPref2.setSummary("Current amount is " + getPreferenceScreen().getSharedPreferences().getString(OPT_FAV_AMOUNT[1], OPT_FAV_AMOUNT_DEF[1]));
		mEditTextPref3.setSummary("Current amount is " + getPreferenceScreen().getSharedPreferences().getString(OPT_FAV_AMOUNT[2], OPT_FAV_AMOUNT_DEF[2]));
		mEditTextPref4.setSummary("Current amount is " + getPreferenceScreen().getSharedPreferences().getString(OPT_FAV_AMOUNT[3], OPT_FAV_AMOUNT_DEF[3]));
		mEditTextPref5.setSummary("Current amount is " + getPreferenceScreen().getSharedPreferences().getString(OPT_FAV_AMOUNT[4], OPT_FAV_AMOUNT_DEF[4]));
		
		// Register listener for when a key changes
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		// Unregister listener for when a key changes
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}
	
	/** Get the current value of daily goal amount */
	public static double getAmount(Context context) {
		try {
			String prefString = PreferenceManager.getDefaultSharedPreferences(context)
				.getString(OPT_AMOUNT, OPT_AMOUNT_DEF);
			
			double prefAmount = Double.valueOf(prefString).doubleValue();
			
			return prefAmount;
		} catch (NumberFormatException nfe) {
			return DEFAULT_AMOUNT;
		} 
	}
	
	public static String getFavoriteAmountString(int favIndex, Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
		.getString(OPT_FAV_AMOUNT[favIndex], OPT_FAV_AMOUNT_DEF[favIndex]);
	}
	
	public static double getFavoriteAmountDouble(int favIndex, Context context) {
		try {
			String prefString = PreferenceManager.getDefaultSharedPreferences(context)
				.getString(OPT_FAV_AMOUNT[favIndex], OPT_FAV_AMOUNT_DEF[favIndex]);
			
			double prefAmount = Double.valueOf(prefString).doubleValue();
			
			return prefAmount;
		} catch (NumberFormatException nfe) {
			return DEFAULT_FAV_AMOUNT;
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		
		// Update the preference summaries for favorite amounts
		if (key.equals(OPT_FAV_AMOUNT[0])) {
			mEditTextPref1.setSummary("Current amount is " + sharedPreferences.getString(key, OPT_FAV_AMOUNT_DEF[0]));
		} else if (key.equals(OPT_FAV_AMOUNT[1])) {
			mEditTextPref2.setSummary("Current amount is " + sharedPreferences.getString(key, OPT_FAV_AMOUNT_DEF[1]));
		} else if (key.equals(OPT_FAV_AMOUNT[2])) {
			mEditTextPref3.setSummary("Current amount is " + sharedPreferences.getString(key, OPT_FAV_AMOUNT_DEF[2]));
		} else if (key.equals(OPT_FAV_AMOUNT[3])) {
			mEditTextPref4.setSummary("Current amount is " + sharedPreferences.getString(key, OPT_FAV_AMOUNT_DEF[3]));
		} else if (key.equals(OPT_FAV_AMOUNT[4])) {
			mEditTextPref5.setSummary("Current amount is " + sharedPreferences.getString(key, OPT_FAV_AMOUNT_DEF[4]));
		}
	}
}
