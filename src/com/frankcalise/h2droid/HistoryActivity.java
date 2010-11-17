package com.frankcalise.h2droid;

import java.util.ArrayList;
import java.util.List;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class HistoryActivity extends ListActivity {
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set up main layout
        setContentView(R.layout.activity_history);
        
        try {
        	List<Entry> entryList = getEntries();
        
        
        	final ListView listView = getListView();
        	ListAdapter adapter = new EntryListAdapter(entryList, this);
        	listView.setAdapter(adapter);
        	
        	// set on item click listener for the ListView
        	listView.setOnItemClickListener(new OnItemClickListener() {
        		public void onItemClick(AdapterView<?> parent, View view,
        				int position, long id) {
        			// When clicked, go to new activity to display
        			// selected day's entries
        			TextView text = (TextView)view.findViewById(R.id.entry_date_textview);
        			Log.d("ITEM_CLICK", String.format("item id = %d, %s", position, text.getText()));
        		}
        	});
        } catch (NullPointerException npe) {
        	Log.e("HISTORY", "null pointer exception, could not getEntries()");
        }
    }
    
    private List<Entry> getEntries() {
    	List<Entry> entries = new ArrayList<Entry>();    	 // List of entries to return
    	String sortOrder = WaterProvider.KEY_DATE + " DESC"; // Date descending
    	
    	ContentResolver cr = getContentResolver();
    	
    	// Return all saved entries, grouped by date
    	Cursor c = cr.query(Uri.withAppendedPath(WaterProvider.CONTENT_URI, "group_date"),
    						null, null, null, sortOrder);
    	
    	
    	if (c.moveToFirst()) {
    		do {
    			String date = c.getString(WaterProvider.DATE_COLUMN);
    			double metricAmount = c.getDouble(WaterProvider.AMOUNT_COLUMN);
    			boolean isNonMetric = false;    			
    			
    			Entry e = new Entry(date, metricAmount, isNonMetric);
    			entries.add(e);
    			
    		} while (c.moveToNext());
    	}
    	
    	c.close();
    	
    	return entries;
    }
}
