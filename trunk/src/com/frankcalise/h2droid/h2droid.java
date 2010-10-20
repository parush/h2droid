package com.frankcalise.h2droid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class h2droid extends Activity {
	private int mConsumption = 0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set up main layout
        setContentView(R.layout.main);
        
        // Set up click listeners for
        // add serving and undo buttons
        Button oneServingButton = (Button)findViewById(R.id.add_one_serving_button);
        oneServingButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View arg0) {
        		Log.d("ADD", "One serving");
        		addWater(8);
        	}
        });
        
        Button twoServingsButton = (Button)findViewById(R.id.add_two_servings_button);
        twoServingsButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View arg0) {
        		Log.d("ADD", "Two servings");
        		addWater(16);
        	}
        });
        
        Button customServingButton = (Button)findViewById(R.id.add_custom_serving_button);
        customServingButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View arg0) {
        		Log.d("ADD", "Custom serving");
        		addWater(13);
        	}
        });
        
        Button undoButton = (Button)findViewById(R.id.undo_last_serving_button);
        undoButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View arg0) {
        		Log.d("UNDO", "Last serving");
        	}
        });
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
    	}
    	
    	return false;
    }
    
    /** 
     * addWater : Add water to today's amount
     * 
     * @param numUnits -1 indicates undo
     * 				   >0 indicates positive units to add
     */
    private void addWater(int numUnits) {
    	mConsumption += numUnits;
    	
    	// update the amount of consumption on UI
    	updateConsumptionTextView();
    }
    
    /** Update the today's consumption TextView */
    private void updateConsumptionTextView() {
    	TextView tv = (TextView)findViewById(R.id.consumption_textview);
    	tv.setText("Today's water consumption: " + mConsumption + " oz");
    }
}