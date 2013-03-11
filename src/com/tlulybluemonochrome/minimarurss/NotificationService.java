package com.tlulybluemonochrome.minimarurss;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

public class NotificationService extends IntentService {

	ArrayList<RssItem> oldlist;

	int mColor;

	Bitmap mBmp;

	public NotificationService(String name) {
		super(name);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	// アラームマネージャ用コンストラクタ
	public NotificationService() {
		super("NotificationService");
	}

	final static String TAG = "test";

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO 自動生成されたメソッド・スタブ

		RssMessageNotification.titlenotify(getApplicationContext(),
				"minimaruRSS", "更新中", "更新中", 99);

		ArrayList<RssItem> arraylist = new ArrayList<RssItem>();

		ArrayList<RssFeed> urilist = new ArrayList<RssFeed>();

		try {// URIセーブデータオープン
			FileInputStream fis = openFileInput("SaveData.txt");
			ObjectInputStream ois = new ObjectInputStream(fis);
			urilist = (ArrayList<RssFeed>) ois.readObject();
			ois.close();
		} catch (Exception e) {
			Toast.makeText(this, "error1", Toast.LENGTH_SHORT).show();
		}

		try {// 既読セーブデータオープン
			FileInputStream fis = openFileInput("SaveData.dat");
			ObjectInputStream ois = new ObjectInputStream(fis);
			oldlist = (ArrayList<RssItem>) ois.readObject();
			ois.close();
		} catch (Exception e) {
			Log.d(TAG, "Error");
		}

		// 全URLチェック
		for (int i = 0; i < urilist.size(); i++) {

			if (urilist.get(i).getNoti()) {// Notifications設定確認
				try {// 記事取得
					URL url = new URL(urilist.get(i).getUrl());
					InputStream is = url.openConnection().getInputStream();
					arraylist.addAll(parseXml(is, urilist.get(i).getTag(),
							urilist.get(i).getTitle()));
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}

		// 未読記事通知
		for (int i = 0; i < arraylist.size(); i++) {
			if (arraylist.get(i).getTag() != 0) {
				RssMessageNotification.notify(getApplicationContext(),
						arraylist.get(i).getTitle(),
						arraylist.get(i).getText(), arraylist.get(i).getUrl(),
						i, Picuture(arraylist.get(i).getTag()), arraylist
								.get(i).getPage());
				try {// 通知の間を置く
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		}

		try {// 既読判定書き込み
			FileOutputStream fos = openFileOutput("SaveData.dat", MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(arraylist);
			oos.close();
		} catch (Exception e) {
			Log.d(TAG, "Error");
		}

	}

	// XMLをパースする
	public ArrayList<RssItem> parseXml(InputStream is, int color, String page)
			throws IOException, XmlPullParserException {
		XmlPullParser parser = Xml.newPullParser();
		ArrayList<RssItem> list = new ArrayList<RssItem>();
		try {
			parser.setInput(is, null);
			int eventType = parser.getEventType();
			RssItem currentItem = null;
			int i = 0;
			while (eventType != XmlPullParser.END_DOCUMENT && i < 5) {// 通知数制限
				String tag = null;
				switch (eventType) {
				case XmlPullParser.START_TAG:
					tag = parser.getName();
					if (tag.equals("item")) {
						currentItem = new RssItem();
						currentItem.setTag(color);
					} else if (currentItem != null) {
						if (tag.equals("title")) {
							currentItem.setTitle(parser.nextText());
						} else if (tag.equals("link")) {
							currentItem.setUrl(parser.nextText());
						} else if (tag.equals("description")) {
							currentItem.setText(parser.nextText().replaceAll(
									"<.+?>", ""));// タグ除去
						}
					}
					break;
				case XmlPullParser.END_TAG:
					tag = parser.getName();
					if (tag.equals("item") && removePR(currentItem)) {
						if (Serch(currentItem)) {
							currentItem.setTag(0);
						}
						currentItem.setPage(page);
						list.add(currentItem);
						i++;
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

	// PR削除
	private boolean removePR(RssItem currentItem) {
		String title = currentItem.getTitle();
		String regex = "^PR";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(title);

		return !m.find();
	}

	// 未読チェック
	public boolean Serch(RssItem item) {

		for (int i = 0; i < oldlist.size(); i++) {
			if (item.getUrl().equals(oldlist.get(i).getUrl()))
				return true;
		}

		return false;
	}

	// アイコン生成
	public Bitmap Picuture(int color) {
		if (color == mColor) {
			return mBmp;
		}
		mColor = color;

		final Bitmap picture = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);
		mBmp = picture.copy(picture.getConfig(), true);

		int width = mBmp.getWidth();
		int height = mBmp.getHeight();
		int[] pixels = new int[width * height];
		mBmp.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (pixels[x + y * width] > 0xFFFFFFFF) {
					pixels[x + y * width] = 0;
				} else if (pixels[x + y * width] >= 0xFF33B5E5) {
					pixels[x + y * width] = color;
				} else if (pixels[x + y * width] >= 0xCC33B5E5) {
					pixels[x + y * width] = color - 0x33000000;
				} else if (pixels[x + y * width] >= 0x9933B5E5) {
					pixels[x + y * width] = color - 0x66000000;
				} else if (pixels[x + y * width] >= 0x6633B5E5) {
					pixels[x + y * width] = color - 0x99000000;
				} else if (pixels[x + y * width] >= 0x3333B5E5) {
					pixels[x + y * width] = color - 0xCC000000;
				} else {
					pixels[x + y * width] = 0;
				}
			}
		}
		mBmp.setPixels(pixels, 0, width, 0, 0, width, height);
		return mBmp;
	}

	// Service終了
	@Override
	public void onDestroy() {
		super.onDestroy();
		RssMessageNotification.cancel(getApplicationContext(), 99);
		RssMessageNotification.titlenotify(getApplicationContext(),
				"minimaruRSS", "タップして更新", "更新完了", 100);
	}

}
