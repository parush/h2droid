package com.frankcalise.h2droid;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class h2droid extends Activity {
	private double mConsumption = 0;
	private static final String LOCAL_DATA = "hydrate_data";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set up main layout
        setContentView(R.layout.main);
    }
    
    /** Called when activity returns to foreground */
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	loadTodaysEntriesFromProvider();
    }
    
    /** Set up menu for main activity */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.main_menu, menu);
    	
    	return true;
    }
    
    /** Handle menu selection */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    		case R.id.menu_settings:
    			startActivity(new Intent(this, Settings.class));
    			return true;
    		case R.id.menu_facts:
    			startActivity(new Intent(this, FactsActivity.class));
    			return true;
    		case R.id.menu_reset:
    			Log.d("RESET", "reset all of today's data - launch asynctask with delete uri");
    			resetTodaysEntries();
    			return true;
    		case R.id.menu_add:
    			startActivity(new Intent(this, CustomEntryActivity.class));
    			return true;
    		case R.id.menu_history:
    			startActivity(new Intent(this, HistoryActivity.class));
    			return true;
    		default: break;
    	}
    	
    	return false;
    }
    
    /** Handle "add one serving" action */
    public void onOneServingClick(View v) {
		Log.d("ADD", "One serving");
		Entry oneServing = new Entry(8, true);
		addNewEntry(oneServing);
    }
    
    /** Handle "add two servings" action */
    public void onFavServingsClick(View v) {
    	String[] itemsArr = getFavoriteAmounts();
		Log.d("ADD", "Favorite amount");
		new AlertDialog.Builder(this)
			.setTitle("Add favorite amount")
			.setItems(itemsArr, 
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						double favAmount = Settings.getFavoriteAmountDouble(which, getApplicationContext());
						Log.d("FAVORITES", "option = " + which + " settings amount = " + favAmount);
						Entry favServing = new Entry(favAmount, true);
						addNewEntry(favServing);
					}
				})
		.show();
    }
    
    /** Handle "add custom serving" action */
    public void onCustomServingClick(View v) {
    	Log.d("ADD", "Custom serving");
    	// adding some amount of water other than
    	// one or two servings
    	startActivity(new Intent(this, CustomEntryActivity.class));
    }
    
    /** Handle "undo last serving" action */
    public void onUndoClick(View v) {
    	Log.d("UNDO", "Last serving");
		// remove last entry from today
		undoTodaysLastEntry();
    }
    
    private void addNewEntry(Entry _entry) {
    	Log.d("CONTENT", "in addNewEntry");
    	    	
    	ContentResolver cr = getContentResolver();
    	
    	// Insert the new entry into the provider
    	ContentValues values = new ContentValues();
    	
    	values.put(WaterProvider.KEY_DATE, _entry.getDate());
    	values.put(WaterProvider.KEY_AMOUNT, _entry.getMetricAmount());
    	
    	cr.insert(WaterProvider.CONTENT_URI, values);
    	
    	mConsumption += _entry.getNonMetricAmount();
    	
    	// Make a toast displaying add complete
    	String toastMsg = String.format("Added %.1f fl oz", _entry.getNonMetricAmount());
    	Toast toast = Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT);
    	toast.setGravity(Gravity.BOTTOM, 0, 0);
    	toast.show();
    	
    	// Update the amount of consumption on UI
    	updateConsumptionTextView();
    }
    
    private void undoTodaysLastEntry() {
    	Log.d("UNDO", "in undo");
    	Date now = new Date();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	
    	String sortOrder = WaterProvider.KEY_DATE + " DESC LIMIT 1";
    	String[] projection = {WaterProvider.KEY_ID};
    	String where = "'" + sdf.format(now) + "' = date(" + WaterProvider.KEY_DATE + ")";
    	
    	ContentResolver cr = getContentResolver();
    	
    	Cursor c = cr.query(WaterProvider.CONTENT_URI, projection, where, null, sortOrder);
    	int results = 0;
    	if (c.moveToFirst()) {
    		Log.d("UNDO", "id = " + c.getInt(0));
    		final Uri uri = Uri.parse("content://com.frankcalise.provider.h2droid/entries/" + c.getInt(0));
    		results = cr.delete(uri, null, null);
    	} else {
    		Log.d("UNDO", "no entries from today!");
    	}
    	
    	c.close();
    	
    	String toastMsg;
    	if (results > 0) {
    		loadTodaysEntriesFromProvider();
    		toastMsg = "Undoing last entry...";
    	} else {
    		toastMsg = "No entries from today!";
    	}
    	
    	Toast toast = Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT);
    	toast.setGravity(Gravity.BOTTOM, 0, 0);
    	toast.show();
    }
    
    private void resetTodaysEntries() {
    	Date now = new Date();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	
    	String where = "'" + sdf.format(now) + "' = date(" + WaterProvider.KEY_DATE + ")";
    	
    	ContentResolver cr = getContentResolver();
    	
    	int results = cr.delete(WaterProvider.CONTENT_URI, where, null);
    	
    	String toastMsg;
    	if (results > 0) {
    		Log.d("RESET", "deleted some rows");
    		toastMsg = "Deleting today's entries...";
    	} else {
    		Log.d("RESET", "there were no entries for today");
    		toastMsg = "No entries from today!";
    	}
    	
    	Toast toast = Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT);
    	toast.setGravity(Gravity.BOTTOM, 0, 0);
    	toast.show();
    	
    	mConsumption = 0;
    	updateConsumptionTextView();
    }
    
    private void loadTodaysEntriesFromProvider() {
    	mConsumption = 0;
    	
    	Log.d("CONTENT", "in loadTodaysEntriesFromProvider()");
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	Date now = new Date();
    	String where = "'" + sdf.format(now) + "' = date(" + WaterProvider.KEY_DATE + ")";
    	
    	ContentResolver cr = getContentResolver();
    	
    	// Return all saved entries
    	Cursor c = cr.query(WaterProvider.CONTENT_URI,
    					    null, where, null, null);
    	
    	if (c.moveToFirst()) {
    		do {
    			String date = c.getString(WaterProvider.DATE_COLUMN);
    			double metricAmount = c.getDouble(WaterProvider.AMOUNT_COLUMN);
    			boolean isNonMetric = false;
    			Entry e = new Entry(date, metricAmount, isNonMetric);
    			
    			mConsumption += e.getNonMetricAmount();
    			
    			Log.d("CONTENT", e.toString());
    		} while (c.moveToNext());
    	}
    	
    	c.close();
    	
    	updateConsumptionTextView();
    }
    
    /** Update the today's consumption TextView */
    private void updateConsumptionTextView() {
    	double prefsGoal = Settings.getAmount(getApplicationContext());
    	Log.d("GOAL", "read from prefs = " + prefsGoal);
    	double percentGoal = (mConsumption / prefsGoal) * 100.0;
    	double delta = mConsumption - prefsGoal;

    	if (percentGoal > 100.0) {
    		percentGoal = 100.0;
    	}
    	
    	// Show consumption amount
    	int unitsPref = Settings.getUnitSystem(getApplicationContext());
    	double displayAmount = mConsumption;
    	String displayUnits = "fl oz";
    	if (unitsPref == Settings.UNITS_METRIC) {
    		displayAmount = mConsumption / Entry.ouncePerMililiter;
    		displayUnits = "ml";
    	} 
    	final TextView amountTextView = (TextView)findViewById(R.id.consumption_textview);
    	String dailyTotal = String.format("%.1f %s\n", displayAmount, displayUnits);
    	amountTextView.setText(dailyTotal);
    	
    	// Show delta from goal
    	final TextView overUnderTextView = (TextView)findViewById(R.id.over_under_textview);
    	double displayDelta = delta;
    	if (unitsPref == Settings.UNITS_METRIC) {
    		displayDelta /= Entry.ouncePerMililiter;
    	}
    	String overUnder = String.format("%+.1f %s (%.1f%%)", displayDelta, displayUnits, percentGoal);
    	overUnderTextView.setText(overUnder);
    	
    	if (delta >= 0) {
    		overUnderTextView.setTextColor(getResources().getColor(R.color.positive_delta));
    	} else {
    		overUnderTextView.setTextColor(getResources().getColor(R.color.negative_delta));
    	}
 
    	// Show current goal setting
    	final TextView goalTextView = (TextView)findViewById(R.id.goal_textview);
    	double displayPrefsGoal = prefsGoal;
    	if (unitsPref == Settings.UNITS_METRIC) {
    		displayPrefsGoal /= Entry.ouncePerMililiter;
    	}
    	String goalText = String.format("Daily goal: %.1f %s", displayPrefsGoal, displayUnits);
    	goalTextView.setText(goalText);	
    	
    	// Broadcast an Intent to update Widget
    	// Use putExtra so AppWidget class does not need
    	// to do ContentProvider pull
    	Intent widgetIntent = new Intent(AppWidget.FORCE_WIDGET_UPDATE);
    	widgetIntent.putExtra("AMOUNT", mConsumption);
    	widgetIntent.putExtra("PERCENT", percentGoal);
    	widgetIntent.putExtra("UNITS", unitsPref);
    	this.sendBroadcast(widgetIntent);
    	
    	// Save off current amount, needed if user 
    	// changes unit system settings to update
    	// widget later on
    	SharedPreferences localData = getSharedPreferences(LOCAL_DATA, 0);
    	SharedPreferences.Editor editor = localData.edit();
    	editor.putString("amount", String.valueOf(mConsumption));
    	editor.putString("percent", String.valueOf(percentGoal));
    	
    	// Commit changes
    	editor.commit();
    }
    
    private String[] getFavoriteAmounts() {
    	String[] favAmounts = new String[5];
    	final int max = 5;
    	Context context = getApplicationContext();
    	
    	for (int i = 0; i < max; i++) {
    		favAmounts[i] = Settings.getFavoriteAmountString(i, context);
    	}
    	
    	return favAmounts;
    }
}