package com.frankcalise.h2droid;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
//import android.view.KeyEvent;
//import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class CustomEntryActivity extends Activity {
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Inflate the layout
        setContentView(R.layout.activity_custom_entry);
        
        // Enable default metric or non-metric via Settings
        //final RadioButton unitsRadioButton = (RadioButton)findViewById(R.id.radio_metric);
        
        // Set up TextWatcher for the amount EditText
        final EditText amountEditText = (EditText)findViewById(R.id.amount_edittext);
        
        amountEditText.addTextChangedListener(new TextWatcher() {
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
    }
    
    private void updateConversionTextView() {
    	final TextView tv = (TextView)findViewById(R.id.conversion_textview);
    	
    	try {
    		final EditText amountEditText = (EditText)findViewById(R.id.amount_edittext);
    		double amount = Double.valueOf(amountEditText.getText().toString()).doubleValue();
    	
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
    		final EditText amountEditText = (EditText)findViewById(R.id.amount_edittext);
    		double amount = Double.valueOf(amountEditText.getText().toString()).doubleValue();
    		
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
    
    public void onNonMetricClick(View v) {
    	
    }
    
    private void addNewEntry(Entry _entry) {
    	Log.d("CONTENT", "in addNewEntry");
    	    	
    	ContentResolver cr = getContentResolver();
    	
    	// Insert the new entry into the provider
    	ContentValues values = new ContentValues();
    	
    	values.put(WaterProvider.KEY_DATE, _entry.getDate());
    	values.put(WaterProvider.KEY_AMOUNT, _entry.getMetricAmount());
    	
    	cr.insert(WaterProvider.CONTENT_URI, values);
    	
    	// Make a toast displaying add complete
    	String toastMsg = String.format("Added %.1f fl oz", _entry.getNonMetricAmount());
    	Toast toast = Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT);
    	toast.setGravity(Gravity.BOTTOM, 0, 0);
    	toast.show();
    }
}
