package com.tlulybluemonochrome.minimarurss;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.util.Xml;

public class NotificationService extends IntentService {

	public NotificationService(String name) {
		super(name);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public NotificationService() {
		super("NotificationService");
	}

	final static String TAG = "test";

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO 自動生成されたメソッド・スタブ

		RssMessageNotification.titlenotify(getApplicationContext(), "minimaruRSS",
				"更新中", "更新中", 99);

		ArrayList<RssItem> arraylist = new ArrayList<RssItem>();

		try {
			FileInputStream fis = openFileInput("SaveData.dat");
			ObjectInputStream ois = new ObjectInputStream(fis);
			arraylist = (ArrayList<RssItem>) ois.readObject();
			ois.close();
		} catch (Exception e) {
			Log.d(TAG, "Error");
		}

		try {
			URL url = new URL(
					"http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss");
			InputStream is = url.openConnection().getInputStream();
			arraylist = parseXml(is, arraylist);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int i = 0; i < arraylist.size(); i++) {
			if (arraylist.get(i).getTag() == 0) {
				RssMessageNotification.notify(getApplicationContext(),
						arraylist.get(i).getTitle(),
						arraylist.get(i).getText(), arraylist.get(i).getUrl(),
						i);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
				}
			}
		}

		try {
			FileOutputStream fos = openFileOutput("SaveData.dat", MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(arraylist);
			oos.close();
		} catch (Exception e) {
			Log.d(TAG, "Error");
		}
	}

	// XMLをパースする
	public ArrayList<RssItem> parseXml(InputStream is,
			ArrayList<RssItem> oldlist) throws IOException,
			XmlPullParserException {
		XmlPullParser parser = Xml.newPullParser();
		ArrayList<RssItem> list = new ArrayList<RssItem>();
		try {
			parser.setInput(is, null);
			int eventType = parser.getEventType();
			RssItem currentItem = null;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String tag = null;
				switch (eventType) {
				case XmlPullParser.START_TAG:
					tag = parser.getName();
					if (tag.equals("item")) {
						currentItem = new RssItem();
						// currentItem.setTag("URL");
					} else if (currentItem != null) {
						if (tag.equals("title")) {
							currentItem.setTitle(parser.nextText());
						} else if (tag.equals("link")) {
							currentItem.setUrl(parser.nextText());
						} else if (tag.equals("description")) {
							currentItem.setText(parser.nextText().replaceAll(
									"<.+?>", ""));
						}
					}
					break;
				case XmlPullParser.END_TAG:
					tag = parser.getName();
					if (tag.equals("item")) {
						if (Serch(currentItem, oldlist))
							currentItem.setTag(0);
						else
							currentItem.setTag(1);
						list.add(currentItem);
					}
					break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	}

	public boolean Serch(RssItem item, ArrayList<RssItem> oldlist) {

		for (int i = 0; i < oldlist.size(); i++) {
			if (item.getUrl().equals(oldlist.get(i).getUrl()))
				return false;
		}

		return true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		RssMessageNotification.cancel(getApplicationContext(), 99);
		RssMessageNotification.titlenotify(getApplicationContext(),
				"minimaruRSS", "タップして更新", "更新完了", 100);
	}

}
