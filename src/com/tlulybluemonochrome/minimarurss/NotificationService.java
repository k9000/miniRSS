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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.preference.PreferenceManager;
import android.util.Xml;

/**
 * 更新確認して通知するサービス
 * 
 * @author k9000
 * 
 */
public class NotificationService extends IntentService {

	static ArrayList<RssItem> oldlist;

	// int count;

	static final int maxSize = 10 * 1024 * 1024;

	public NotificationService(final String name) {
		super(name);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	// アラームマネージャ用コンストラクタ
	public NotificationService() {
		super("NotificationService");
	}

	final static String TAG = "test";

	@Override
	protected void onHandleIntent(final Intent intent) {
		// TODO 自動生成されたメソッド・スタブ

		RssMessageNotification.cancel(getApplicationContext(), -1);
		RssMessageNotification.titlenotify(getApplicationContext(),
				"minimaruRSS", "更新中", "更新中", -1, true);

		final SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		final boolean picChecked = sharedPreferences.getBoolean("pic_switch",
				true);

		final ArrayList<RssItem> arraylist = new ArrayList<RssItem>();

		ArrayList<RssFeed> urilist = new ArrayList<RssFeed>();

		try {// URIセーブデータオープン
			final FileInputStream fis = openFileInput("SaveData.txt");
			final ObjectInputStream ois = new ObjectInputStream(fis);
			urilist = (ArrayList<RssFeed>) ois.readObject();
			fis.close();
			ois.close();
		} catch (Exception e) {
		}

		try {// 既読セーブデータオープン
			final FileInputStream fis = openFileInput("SaveData.dat");
			final ObjectInputStream ois = new ObjectInputStream(fis);
			oldlist = (ArrayList<RssItem>) ois.readObject();
			fis.close();
			ois.close();
		} catch (Exception e) {
			oldlist = null;
		}

		// 全URLチェック
		for (int i = 0; i < urilist.size(); i++) {

			if (urilist.get(i).getNoti()) {// Notifications設定確認
				try {
					final HttpURLConnection conn = (HttpURLConnection) new URL(
							urilist.get(i).getUrl()).openConnection();
					conn.setConnectTimeout(5000);
					conn.setReadTimeout(5000);
					conn.addRequestProperty("User-Agent", "desktop");
					conn.setDoInput(true);
					conn.connect();
					arraylist.addAll(parseXml(conn.getInputStream(), urilist
							.get(i).getTag(), urilist.get(i).getTitle()));

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}

		if (!arraylist.isEmpty()) {
			try {// 既読判定書き込み
				final FileOutputStream fos = openFileOutput("SaveData.dat",
						MODE_PRIVATE);
				final ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(arraylist);
				fos.close();
				oos.close();
			} catch (Exception e) {
			}
		}

		// 既読削除
		Iterator<RssItem> it = arraylist.iterator();
		while (it.hasNext()) {
			RssItem i = (RssItem) it.next();
			if (i.getTag() == 0)
				it.remove();
		}

		// 画像取得
		final int listsize = arraylist.size();
		for (int i = 0; i < listsize; i++) {
			final FileOutputStream fos;
			try {
				fos = openFileOutput(i + ".png", MODE_PRIVATE);
				makeImage(
						arraylist.get(i).getImage(),
						Picuture(arraylist.get(i).getTag(),
								R.drawable.ic_launcher), picChecked).compress(
						Bitmap.CompressFormat.PNG, 100, fos);
				fos.close();
			} catch (FileNotFoundException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}

		}

		// 未読記事通知
		if (!arraylist.isEmpty()) {
			this.startService(new Intent(this, NotificationChangeService.class)
					.putExtra("BROWSE", false).putExtra("TITLE", false)
					.putExtra("REFRESH", true).putExtra("LIST", arraylist)
					.putExtra("COUNT", 0).putExtra("ID", 0));
		}
	}

	// XMLをパースする
	public static ArrayList<RssItem> parseXml(final InputStream is,
			final int color, final String page) throws IOException,
			XmlPullParserException {
		final XmlPullParser parser = Xml.newPullParser();
		final ArrayList<RssItem> list = new ArrayList<RssItem>();
		try {
			parser.setInput(is, null);
			int eventType = parser.getEventType();
			RssItem currentItem = null;
			int i = 0;
			while (eventType != XmlPullParser.END_DOCUMENT && i < 10) {// 通知数制限
				String tag = null;
				switch (eventType) {
				case XmlPullParser.START_TAG:
					tag = parser.getName();
					if (tag.equals("item") || tag.equals("entry")) {
						currentItem = new RssItem();
						currentItem.setTag(color);
					} else if (currentItem != null) {
						if (tag.equals("title")) {
							currentItem.setTitle(parser.nextText().replaceAll(
									"(&#....;|&....;|&...;)", ""));// タグ除去;
						} else if (tag.equals("link")) {
							final String link = parser.nextText();
							if (link != "") {
								currentItem.setUrl(link);
							} else {
								final String rel = parser.getAttributeValue(
										null, "rel");
								final String herf = parser.getAttributeValue(
										null, "href");
								if (rel.equals("alternate")) {
									currentItem.setUrl(herf);
								}
							}
						} else if (tag.equals("description")
								|| tag.equals("summary")) {
							String buf = parser.nextText();
							currentItem.setImage(StripImageTags(buf));
							currentItem
									.setText(buf
											.replaceAll(
													"(<.+?>|\r\n|\n\r|\n|\r|&#....;|&....;|&...;|&..;)",
													""));// タグと改行除去
						} else if (tag.equals("encoded")) {
							currentItem.setImage(StripImageTags(parser
									.nextText()));
						}
					}
					break;
				case XmlPullParser.END_TAG:
					tag = parser.getName();
					if ((tag.equals("item") || tag.equals("entry"))
							&& removePR(currentItem)) {
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

	private static String StripImageTags(String str) {
		final Pattern o = Pattern.compile("<img.*?(jpg|png|images).*?>");
		final Pattern p = Pattern.compile("http.*?(jpg|png)");
		final Pattern q = Pattern.compile("//.*?(jpg|png)");
		final Pattern r = Pattern.compile("//.*?images.*?\"");

		String matchstr = null;

		final Matcher mo = o.matcher(str);
		if (mo.find()) {
			str = mo.group();

			final Matcher mp = p.matcher(str);
			final Matcher mq = q.matcher(str);
			final Matcher mr = r.matcher(str);

			if (mp.find()) {
				matchstr = mp.group();
			} else if (mq.find()) {
				matchstr = "http:" + mq.group();
				// } else if (mr.find()) {
				// matchstr = "http:" + mr.group();
				// matchstr = matchstr.substring(0, matchstr.length() - 1);
			} else {
				matchstr = null;
			}
			return matchstr;
		}

		return null;

	}

	// PR削除
	private static boolean removePR(RssItem currentItem) {
		final String title = currentItem.getTitle();
		final String regex = "^PR";
		final Pattern p = Pattern.compile(regex);
		final Matcher m = p.matcher(title);

		return !m.find();
	}

	// 未読チェック
	public static boolean Serch(RssItem item) {
		if (oldlist == null)
			return false;

		for (int i = 0; i < oldlist.size(); i++) {
			if (item.getUrl().equals(oldlist.get(i).getUrl()))
				return true;
		}

		return false;
	}

	// アイコン生成
	private Bitmap Picuture(final int color, final int resource) {

		final Bitmap picture = BitmapFactory.decodeResource(getResources(),
				resource);
		final Bitmap mBmp = picture.copy(Bitmap.Config.ARGB_8888, true);

		final int width = mBmp.getWidth();
		final int height = mBmp.getHeight();
		final int[] pixels = new int[width * height];
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

	private Bitmap makeImage(final String image, final Bitmap base,
			boolean picChecked) {
		if (picChecked && image != null) {
			// Bitmap bitmap;
			try {
				final URL image_url = new URL(image);
				final InputStream is = (InputStream) image_url.getContent();

				// 画像サイズ情報を取得する
				final BitmapFactory.Options imageOptions = new BitmapFactory.Options();
				imageOptions.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(is, null, imageOptions);
				is.close();

				final Resources res = getBaseContext().getResources();
				final float scale = Math
						.max((float) imageOptions.outWidth
								/ res.getDimension(android.R.dimen.notification_large_icon_width),
								(float) imageOptions.outHeight
										/ res.getDimension(android.R.dimen.notification_large_icon_height));

				final float sscale = ((int) scale != 0 ? (int) scale : 1)
						/ scale;

				imageOptions.inSampleSize = (int) scale;
				// scale = scale/(int)scale;

				// 計算したスケールで画像を読み込む
				imageOptions.inJustDecodeBounds = false;
				final InputStream is2 = (InputStream) image_url.getContent();
				final Bitmap bitmap = BitmapFactory.decodeStream(is2, null,
						imageOptions);
				is2.close();

				final Matrix matrix = new Matrix();
				matrix.postScale(sscale, sscale);
				if (bitmap != null)
					return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
							bitmap.getHeight(), matrix, true);
			} catch (IOException e) {
				e.printStackTrace();
				return base;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return base;
			}
			// return bitmap;
		}
		return base;

	}

	// Service終了
	@Override
	public void onDestroy() {
		super.onDestroy();
		RssMessageNotification.cancel(getApplicationContext(), -1);
		RssMessageNotification.titlenotify(getApplicationContext(),
				"minimaruRSS", "タップして更新", "更新完了", -1, false);
	}

}
