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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Xml;

/**
 * 更新確認
 * 
 * @author k9000
 * 
 */
public class RssParserTaskLoader extends AsyncTaskLoader<ArrayList<RssItem>> {

	private URL url;
	private int wait;
	private int flag;
	private Activity activity;
	private int colorTag = 0x33b5e5;

	/**
	 * ItewDetailFragment用コンストラクタ
	 * 
	 * @param context
	 *            context
	 * @param url
	 *            URL
	 * @param wait
	 *            待ち時間
	 * @param color
	 *            通知色
	 * @param activity
	 *            多重起動防止用
	 */
	public RssParserTaskLoader(Context context, String url, int wait,
			int color, Activity activity) {
		super(context);

		this.colorTag = color;

		this.activity = activity;

		this.wait = wait;// 引っ張って更新の機嫌取り

		flag = 3;

		try {
			this.url = new URL(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * EntryActivityｃ用コンストラクタ
	 * 
	 * @param context
	 *            context
	 * @param flag
	 *            識別用
	 * @param url
	 *            URL
	 * @param activity
	 *            多重起動防止用
	 */
	public RssParserTaskLoader(EntryActivity context, int flag, String url,
			Activity activity) {
		super(context);

		this.activity = activity;

		wait = 100;// 雰囲気用

		this.flag = flag;

		try {
			this.url = new URL(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	synchronized public ArrayList<RssItem> loadInBackground() {
		synchronized (activity) {

			ArrayList<RssItem> result = new ArrayList<RssItem>();

			switch (flag) {
			case 1:// RSSのURI獲得を目指す
				try {
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.addRequestProperty("User-Agent", "desktop");
					conn.setDoInput(true);
					conn.connect();
					InputStream is = conn.getInputStream();
					result = parseHtml(is);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
				break;

			case 2:// RSSのタイトル獲得を目指す
				try {
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.addRequestProperty("User-Agent", "desktop");
					conn.setDoInput(true);
					conn.connect();
					InputStream is = conn.getInputStream();
					result = parseRSS(is);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
				break;

			case 3:// RSSの記事一覧の獲得を目指す
				try {
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.addRequestProperty("User-Agent", "desktop");
					conn.setDoInput(true);
					conn.connect();
					InputStream is = conn.getInputStream();
					result = parseXml(is, colorTag);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
				break;
			default:
				return null;
			}

			try {
				Thread.sleep(wait);
			} catch (InterruptedException e) {
			}
			return result;
		}
	}

	/**
	 * XMLをパースする
	 * 
	 * @param is
	 *            InputStream
	 * @param color
	 *            色
	 * @return 更新ページリスト
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public ArrayList<RssItem> parseXml(InputStream is, int color)
			throws IOException, XmlPullParserException {
		ArrayList<RssItem> list = new ArrayList<RssItem>();
		XmlPullParser parser = Xml.newPullParser();
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
						currentItem.setTag(color);
						currentItem.setText("");
					} else if (currentItem != null) {
						if (tag.equals("title")) {

							currentItem.setTitle(parser.nextText());
						} else if (tag.equals("link")) {
							currentItem.setUrl(parser.nextText());
						} else if (tag.equals("description")) {
							String buf = parser.nextText();
							currentItem.setImage(StripImageTags(buf));
							currentItem.setText(buf.replaceAll(
									"(<.+?>|\r\n|\n\r|\n|\r)", "").replaceAll("&nbsp;|&amp;", " "));// タグと改行除去
						}
					}
					break;
				case XmlPullParser.END_TAG:
					tag = parser.getName();
					if (tag.equals("item") && removePR(currentItem)) {
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
	
	private static String StripImageTags(String str) {
		Pattern o = Pattern.compile("<img.*jpg.*?>");
		Pattern p = Pattern.compile("//.*jpg");
		String matchstr = null;
		Matcher n = o.matcher(str);
		if (n.find()){
			  str = n.group();
			}
		Matcher m = p.matcher(str);
		if (m.find()){
		  matchstr = "http:"+m.group();
		}
		return matchstr;
	}

	/**
	 * PR削除判定
	 * 
	 * @param currentItem
	 * @return 見つからなかったらtrue
	 */
	private boolean removePR(RssItem currentItem) {
		String title = currentItem.getTitle();
		String regex = "^PR";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(title);

		return !m.find();
	}

	/**
	 * HTMLをパースする
	 * 
	 * @param is
	 *            InputStream
	 * @return RSS用URI
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public ArrayList<RssItem> parseHtml(InputStream is) throws IOException,
			XmlPullParserException {

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setValidating(false);
		factory.setFeature(Xml.FEATURE_RELAXED, true);
		factory.setNamespaceAware(true);
		XmlPullParser parser = factory.newPullParser();

		ArrayList<RssItem> list = new ArrayList<RssItem>();
		try {
			parser.setInput(is, "UTF-8");
			int eventType = parser.getEventType();
			RssItem currentItem = null;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String tag = null;
				switch (eventType) {
				case XmlPullParser.START_TAG:
					tag = parser.getName();
					if (tag.equals("head")) {
						currentItem = new RssItem();
					} else if (currentItem != null) {
						if (tag.equals("link")) {
							String rel = parser.getAttributeValue(null, "rel");
							String type = parser
									.getAttributeValue(null, "type");
							String herf = parser
									.getAttributeValue(null, "href");
							if (rel.equals("alternate")
									&& type.equals("application/rss+xml")) {
								currentItem.setUrl(herf);
								list.add(currentItem);
								return list;
							}
						}
					}
					break;
				case XmlPullParser.END_TAG:
					tag = parser.getName();
					if (tag.equals("head")) {
						return null;
					}
					break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * RSSをパースする
	 * 
	 * @param is
	 *            InputStream
	 * @return ページタイトル
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public ArrayList<RssItem> parseRSS(InputStream is) throws IOException,
			XmlPullParserException {
		ArrayList<RssItem> list = new ArrayList<RssItem>();
		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(is, null);
			int eventType = parser.getEventType();
			RssItem currentItem = null;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String tag = null;
				switch (eventType) {
				case XmlPullParser.START_TAG:
					tag = parser.getName();
					if (tag.equals("channel")) {
						currentItem = new RssItem();
					} else if (currentItem != null) {
						if (tag.equals("title")) {
							currentItem.setTitle(parser.nextText());
							list.add(currentItem);
							return list;
						}
					}
					break;
				case XmlPullParser.END_TAG:
					tag = parser.getName();
					if (tag.equals("head")) {
						return null;
					}
					break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

}
