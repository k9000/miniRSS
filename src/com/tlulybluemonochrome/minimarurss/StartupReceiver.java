/*
 * Copyright (C) 2013 k9000
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tlulybluemonochrome.minimarurss;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * スタートアップ
 * 
 * @author k9000
 * 
 */
public class StartupReceiver extends BroadcastReceiver {
	public StartupReceiver() {
	}

	@Override
	public void onReceive(final Context context,final Intent intent) {
		// TODO: This method is called when the BroadcastReceiver is receiving
		// an Intent broadcast.
		// throw new UnsupportedOperationException("Not yet implemented");
		final SharedPreferences sharedPreferences = PreferenceManager
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

			final Intent serviceIntent = new Intent(context,
					NotificationService.class);
			final PendingIntent pendingIntent = PendingIntent.getService(context, -2,
					serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			final AlarmManager alarmManager = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			alarmManager.cancel(pendingIntent);
			alarmManager.setInexactRepeating(AlarmManager.RTC,
					System.currentTimeMillis(), time, pendingIntent);
			context.startService(new Intent(context, NotificationService.class));
		}

	}
}
