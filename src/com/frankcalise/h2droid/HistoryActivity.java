package com.frankcalise.h2droid;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;

public class HistoryActivity extends ListActivity {
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set up main layout
        setContentView(R.layout.activity_history);
        
        List<Entry> entryList = getEntries();
        
        final ListView listView = getListView();
        ListAdapter adapter = new EntryListAdapter(entryList, this);
        listView.setAdapter(adapter);
    }
    
    private List<Entry> getEntries() {
    	List<Entry> entries = new ArrayList<Entry>();
    	
    	//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	//Date now = new Date();
    	//String where = "'" + sdf.format(now) + "' = date(" + WaterProvider.KEY_DATE + ")";
    	String sortOrder = WaterProvider.KEY_DATE + " DESC";
    	
    	ContentResolver cr = getContentResolver();
    	
    	// Return all saved entries
    	Cursor c = cr.query(WaterProvider.CONTENT_URI,
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
