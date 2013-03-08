package com.tlulybluemonochrome.minimarurss;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Xml;

public class RssParserTaskLoader extends AsyncTaskLoader<ArrayList<RssItem>> {

	private URL url;
	private int wait;
	private int flag;

	public RssParserTaskLoader(Context context, String url, int wait) {
		super(context);

		this.wait = wait;

		flag = 3;

		try {
			this.url = new URL(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public RssParserTaskLoader(EntryActivity context, int flag, String url) {
		super(context);

		wait = 100;

		this.flag = flag;

		try {
			this.url = new URL(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public ArrayList<RssItem> loadInBackground() {

		ArrayList<RssItem> result = new ArrayList<RssItem>();

		switch (flag) {
		case 1:
			try {
				InputStream is = url.openConnection().getInputStream();
				result = parseHtml(is);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			break;

		case 2:
			try {
				InputStream is = url.openConnection().getInputStream();
				result = parseRSS(is);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			break;

		case 3:
			try {
				InputStream is = url.openConnection().getInputStream();
				result = parseXml(is);
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

	// XMLをパースする
	public ArrayList<RssItem> parseXml(InputStream is) throws IOException,
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
					if (tag.equals("item")) {
						currentItem = new RssItem();
						currentItem.setTag(0);
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
					if (tag.equals("item")) {
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

	// HTMLをパースする
	public ArrayList<RssItem> parseHtml(InputStream is) throws IOException,
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
					if (tag.equals("head")) {
						currentItem = new RssItem();
					} else if (currentItem != null) {
						if (tag.equals("link")) {
							String rel = parser.getAttributeValue(null, "rel");
							String type = parser
									.getAttributeValue(null, "type");
							String title = parser.getAttributeValue(null,
									"title");
							String herf = parser
									.getAttributeValue(null, "href");
							if (rel.equals("alternate")
									&& type.equals("application/rss+xml")) {
								currentItem.setTitle(title);
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
