package com.frankcalise.h2droid;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Settings extends PreferenceActivity {
	
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
	
	@Override
	protected void onCreate(Bundle savedInstanceBundle) {
		super.onCreate(savedInstanceBundle);
		addPreferencesFromResource(R.xml.settings);
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
}
