package com.tlulybluemonochrome.minimarurss;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.tlulybluemonochrome.minimarurss.dummy.DummyContent;

import android.app.Service;
import android.content.Intent;
import android.os.ConditionVariable;
import android.os.IBinder;
import android.util.Log;
import android.util.Xml;

public class NotificationService extends Service {

	final static String TAG = "MyService";
	final int INTERVAL_PERIOD = 5000;
	Timer timer = new Timer();
	ConditionVariable mCondition;
	
	ArrayList<DummyContent.DummyItem> arraylist;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");
		
		Thread thread = new Thread(null, mTask, "NotifyingService");
	    mCondition = new ConditionVariable(false);
	    thread.start();

		timer.scheduleAtFixedRate(new TimerTask() {
			int i = 0;
			@Override
			public void run() {
				if(arraylist!= null){
				RssMessageNotification.notify(getApplicationContext(), arraylist.get(i).getTitle(),arraylist.get(i).getTag(),arraylist.get(i).getUrl(),0);
				if(i==arraylist.size()-1)
					i=0;
				i++;
				}			}
		}, 0, INTERVAL_PERIOD);

		return START_STICKY;
	}
	
	private Runnable mTask = new Runnable() {
	    public void run() {
	    	arraylist = null;

		try {
			URL url = new URL("http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss");
			InputStream is = url.openConnection().getInputStream();
			arraylist = parseXml(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
	        
	    }
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (timer != null) {
			timer.cancel();
		}
		RssMessageNotification.cancel(this);
		Log.d(TAG, "onDestroy");
	}

	
	// XMLをパースする
		public ArrayList<DummyContent.DummyItem> parseXml(InputStream is)
				throws IOException, XmlPullParserException {
			XmlPullParser parser = Xml.newPullParser();
			ArrayList<DummyContent.DummyItem> mAdapter = new ArrayList<DummyContent.DummyItem>();
			try {
				parser.setInput(is, null);
				int eventType = parser.getEventType();
				DummyContent.DummyItem currentItem = null;
				while (eventType != XmlPullParser.END_DOCUMENT) {
					String tag = null;
					switch (eventType) {
					case XmlPullParser.START_TAG:
						tag = parser.getName();
						if (tag.equals("item")) {
							currentItem = new DummyContent.DummyItem();
							//currentItem.setTag("URL");
						} else if (currentItem != null) {
							if (tag.equals("title")) {
								currentItem.setTitle(parser.nextText());
							} else if (tag.equals("link")) {
								currentItem.setLink(parser.nextText());
							} else if (tag.equals("description")) {
								currentItem.setTag(parser.nextText().replaceAll("<.+?>", ""));
							}
						}
						break;
					case XmlPullParser.END_TAG:
						tag = parser.getName();
						if (tag.equals("item")) {
							mAdapter.add(currentItem);
						}
						break;
					}
					eventType = parser.next();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return mAdapter;

		}

}
