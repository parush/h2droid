package com.frankcalise.h2droid;

import android.app.Activity;
import android.os.Bundle;

public class CustomEntryActivity extends Activity {
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Inflate the layout
        setContentView(R.layout.activity_custom_entry);
    }
}
