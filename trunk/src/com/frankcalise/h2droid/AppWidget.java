package com.frankcalise.h2droid;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

public class AppWidget extends AppWidgetProvider {
	
	// Intent to listen for to update Widget UI
	public static String FORCE_WIDGET_UPDATE = 
		"com.frankcalise.h2droid.FORCE_WIDGET_UPDATE";
	
	@Override
	public void onUpdate(Context context,
						 AppWidgetManager appWidgetManager,
						 int[] appWidgetIds) {
		updateAmount(context, appWidgetManager, appWidgetIds);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		
		// Check if Intent received matches widget update string
		if (FORCE_WIDGET_UPDATE.equals(intent.getAction())) {
			updateAmount(context);
		}
	}
	
	public void updateAmount(Context context,
							 AppWidgetManager appWidgetManager,
							 int[] appWidgetIds) {
		// Grab the data from today's entries
		double amount = 0;
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	Date now = new Date();
    	String where = "'" + sdf.format(now) + "' = date(" + WaterProvider.KEY_DATE + ")";
    	
    	ContentResolver cr = context.getContentResolver();
    	
    	// Return all saved entries
    	Cursor c = cr.query(WaterProvider.CONTENT_URI,
    					    null, where, null, null);
    	
    	if (c.moveToFirst()) {
    		do {
    			String date = c.getString(WaterProvider.DATE_COLUMN);
    			double metricAmount = c.getDouble(WaterProvider.AMOUNT_COLUMN);
    			boolean isNonMetric = false;
    			Entry e = new Entry(date, metricAmount, isNonMetric);
    			
    			amount += e.getNonMetricAmount();
    		} while (c.moveToNext());
    	}
    	
    	c.close();
    	
    	// Create new RemoteViews to set the text displayed
    	// by the widget's TextView
    	final int N = appWidgetIds.length;
    	for (int i = 0; i < N; i++) {
    		int appWidgetId = appWidgetIds[i];
    		RemoteViews views = new RemoteViews(context.getPackageName(),
    											R.layout.one_cell_widget);
    		views.setTextViewText(R.id.widget_amount_text, String.format("%.1f fl oz", amount));
    		appWidgetManager.updateAppWidget(appWidgetId, views);
    	}
	}
	
	// Obtain an instance of AppWidgetManager from the context
	// and use it to find widget IDs of active Hydrate widgets.
	// Then pass to updateAmount(Context, AppWidgetManager, int[])
	public void updateAmount(Context context) {
		ComponentName thisWidget = new ComponentName(context, AppWidget.class);
		
		AppWidgetManager appWidgetManager = 
			AppWidgetManager.getInstance(context);
		
		int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		updateAmount(context, appWidgetManager, appWidgetIds);
	}
}
