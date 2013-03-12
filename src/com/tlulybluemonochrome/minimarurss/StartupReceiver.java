package com.tlulybluemonochrome.minimarurss;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class StartupReceiver extends BroadcastReceiver {
	public StartupReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO: This method is called when the BroadcastReceiver is receiving
		// an Intent broadcast.
		// throw new UnsupportedOperationException("Not yet implemented");
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		if (sharedPreferences.getBoolean("notification_switch", false)) {
			long time = AlarmManager.INTERVAL_HOUR;
			switch (sharedPreferences.getInt("notification_freqescy", 2)) {
			case 0:
				time = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
				break;
			case 1:
				time = AlarmManager.INTERVAL_HALF_HOUR;
				break;
			case 2:
				time = AlarmManager.INTERVAL_HOUR;
				break;
			case 3:
				time = AlarmManager.INTERVAL_HALF_DAY;
				break;
			case 4:
				time = AlarmManager.INTERVAL_DAY;
				break;
			}

			Intent serviceIntent = new Intent(context,
					NotificationService.class);
			PendingIntent pendingIntent = PendingIntent.getService(context, -1,
					serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager alarmManager = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			alarmManager.setInexactRepeating(AlarmManager.RTC,
					System.currentTimeMillis(), time, pendingIntent);
			context.startService(new Intent(context, NotificationService.class));
		}

	}
}
