package com.tlulybluemonochrome.minimarurss;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.tlulybluemonochrome.minimarurss.dummy.DummyContent;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Xml;
import android.widget.ArrayAdapter;

public class RssParserTaskLoader extends
		AsyncTaskLoader<ArrayAdapter<DummyContent.DummyItem>> {

	private ArrayAdapter<DummyContent.DummyItem> mAdapter;

	public RssParserTaskLoader(Context context,
			ArrayAdapter<DummyContent.DummyItem> adapter) {
		super(context);

		mAdapter = adapter;
	}

	@Override
	public ArrayAdapter<DummyContent.DummyItem> loadInBackground() {

		ArrayAdapter<DummyContent.DummyItem> result = null;

		try {
			// HTTP経由でアクセスし、InputStreamを取得する
			URL url = new URL(
					"http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss");
			InputStream is = url.openConnection().getInputStream();
			result = parseXml(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// ここで返した値は、onPostExecuteメソッドの引数として渡される
		return result;
	}

	// XMLをパースする
	public ArrayAdapter<DummyContent.DummyItem> parseXml(InputStream is)
			throws IOException, XmlPullParserException {
		XmlPullParser parser = Xml.newPullParser();
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
					} else if (currentItem != null) {
						if (tag.equals("title")) {
							currentItem.setTitle(parser.nextText());
						} else if (tag.equals("link")) {
							currentItem.setLink(parser.nextText());
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
