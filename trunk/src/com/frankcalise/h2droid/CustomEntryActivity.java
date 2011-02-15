package com.frankcalise.h2droid;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class CustomEntryActivity extends Activity implements OnGestureListener {
	
	private EditText mAmountEditText;
	private GestureDetector mGestureScanner;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Inflate the layout
        setContentView(R.layout.activity_custom_entry);
        
        // Enable default metric or non-metric via Settings
        final RadioButton metricRadioButton = (RadioButton)findViewById(R.id.radio_metric);
        final RadioButton imperialRadioButton = (RadioButton)findViewById(R.id.radio_non_metric);
        int unitsPref = Settings.getUnitSystem(getApplicationContext());
        
        // Toggle the correct radio button according to user's prefs
        if (unitsPref == Settings.UNITS_METRIC) {
        	metricRadioButton.setChecked(true);
        	imperialRadioButton.setChecked(false);
        } else {
        	metricRadioButton.setChecked(false);
        	imperialRadioButton.setChecked(true);
        }

        
        // Set up TextWatcher for the amount EditText
        mAmountEditText = (EditText)findViewById(R.id.amount_edittext);
        
        mAmountEditText.addTextChangedListener(new TextWatcher() {
        	@Override
        	public void onTextChanged(CharSequence s, int start, int before,
        			int count) {
        		
        	}

			@Override
			public void afterTextChanged(Editable s) {
				updateConversionTextView();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			
				
			}
        });
        
        mGestureScanner = new GestureDetector(this);
    }
    
    private void updateConversionTextView() {
    	final TextView tv = (TextView)findViewById(R.id.conversion_textview);
    	
    	try {
    		double amount = Double.valueOf(mAmountEditText.getText().toString()).doubleValue();
    	
    		final RadioButton radioNonMetric = (RadioButton)findViewById(R.id.radio_non_metric);

    		String conversionText;
    		if (radioNonMetric.isChecked()) {
    			conversionText = String.format("%.1f fl oz =  %.1f ml", amount, (amount / Entry.ouncePerMililiter));
    		} else {
    			conversionText = String.format("%.1f fl oz =  %.1f ml", (amount * Entry.ouncePerMililiter), amount);
    		}
    	
    		tv.setText(conversionText);
    	} catch (NumberFormatException nfe) {
    		tv.setText("");
    	}
    }
    
    public void onSaveClick(View v) {
    	Log.d("ADD", "add new entry here");
    	
    	try {
    		double amount = Double.valueOf(mAmountEditText.getText().toString()).doubleValue();
    		
    		final RadioButton radioNonMetric = (RadioButton)findViewById(R.id.radio_non_metric);
    		boolean isNonMetric;
    		
    		if (radioNonMetric.isChecked()) {
    			isNonMetric = true;
    		} else {
    			isNonMetric = false;
    		}
    		
    		
    		Entry e = new Entry(amount, isNonMetric);
    		addNewEntry(e);
    	} catch (NumberFormatException nfe) {
    		// show some error toast here?
    	}

    	finish();
    }
    
    public void onCancelClick(View v) {
    	Log.d("CANCEL", "user cancelled this add");
    	finish();
    }
    
    public void onRadioClick(View v) {
    	updateConversionTextView();
    }
    
    private void addNewEntry(Entry _entry) {
    	Log.d("CONTENT", "in addNewEntry");
    	    	
    	// Check to see if user wants Toast message
    	boolean showToasts = Settings.getToastsSetting(getApplicationContext());
    	
    	ContentResolver cr = getContentResolver();
    	
    	// Insert the new entry into the provider
    	ContentValues values = new ContentValues();
    	
    	values.put(WaterProvider.KEY_DATE, _entry.getDate());
    	values.put(WaterProvider.KEY_AMOUNT, _entry.getMetricAmount());
    	
    	cr.insert(WaterProvider.CONTENT_URI, values);
    	
    	// Make a toast displaying add complete
    	int unitsPref = Settings.getUnitSystem(this);
    	double displayAmount = _entry.getNonMetricAmount();
    	String displayUnits = "fl oz";
    	if (unitsPref == Settings.UNITS_METRIC) {
    		displayUnits = "ml";
    		displayAmount = _entry.getMetricAmount();
    	}
    	String toastMsg = String.format("Added %.1f %s", displayAmount, displayUnits);
    	Toast toast = Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT);
    	toast.setGravity(Gravity.BOTTOM, 0, 0);
    	if (showToasts)
    		toast.show();
    	
    	// If user wants a reminder when to drink next,
    	// setup a notification X minutes away from this entry
    	// where X is also a setting
    	if (Settings.getReminderEnabled(this)) {
    		// Get the AlarmManager service
			AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
			
			// create the calendar object
			Calendar cal = Calendar.getInstance();
			// add X minutes to the calendar object
			cal.add(Calendar.MINUTE, Settings.getReminderInterval(this));
			
			// cancel existing alarm if any, this way latest
			// alarm will be the only one to notify user
			Intent cancelIntent = new Intent(this, AlarmReceiver.class);
			PendingIntent cancelSender = PendingIntent.getBroadcast(this, 0, cancelIntent, 0);
			am.cancel(cancelSender);
			
			// set up the new alarm
			Intent intent = new Intent(this, AlarmReceiver.class);
			PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
    	}
    }

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// Dismiss the soft keyboard when
		// user taps main view
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mAmountEditText.getWindowToken(), 0);
		
		return true;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return mGestureScanner.onTouchEvent(ev);
	}
}
