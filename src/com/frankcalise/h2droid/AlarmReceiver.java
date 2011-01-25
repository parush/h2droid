package com.frankcalise.h2droid;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
        // Setup NotificationManager
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification reminder = Settings.getReminderNotification(context);
    	if (reminder != null) {
    		Intent reminderIntent = new Intent(context, h2droid.class);
    		reminder.setLatestEventInfo(context,
    				"Hydrate","Reminder to drink more water!",
    				PendingIntent.getActivity(context, 0, reminderIntent,
    				PendingIntent.FLAG_CANCEL_CURRENT));
    		nm.notify(0, reminder);
    	}
	}

}
