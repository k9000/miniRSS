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

public class RssParserTaskLoader extends AsyncTaskLoader<ArrayList<RssItem>> {

	private URL url;
	private int wait;
	private int flag;
	private Activity activity;
	private int colorTag = 0x33b5e5;

	// ItewDetailFragmentから
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

	// EntryActivityｃから
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

	// XMLをパースする
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

	// PR削除
	private boolean removePR(RssItem currentItem) {
		String title = currentItem.getTitle();
		String regex = "^PR";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(title);

		return !m.find();
	}

	// HTMLをパースする
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

	// RSSをパースする
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
