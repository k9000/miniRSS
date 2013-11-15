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

import java.util.ArrayList;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * 通知をピン留めするサービス
 * 
 * @author k9000
 * 
 */
public class NotificationChangeService extends IntentService {

	public NotificationChangeService() {
		super("NotificationChangeService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		if (intent.getBooleanExtra("TITLE", true)){
			final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			if( sharedPreferences.getBoolean("notification_switch", false)) {
				RssMessageNotification.titlenotify(getApplicationContext(),
						"minimaruRSS", "タップして更新", "", -1);
		}
				
		} else {
			final ArrayList<RssItem> arraylist = (ArrayList<RssItem>) intent
					.getSerializableExtra("LIST");
			final int count = intent.getIntExtra("COUNT", 0) + 1;
			if (count < arraylist.size())
				RssMessageNotification.noti(getApplicationContext(), arraylist,
						count, intent.getIntExtra("ID", 1));
		}

	}
}
