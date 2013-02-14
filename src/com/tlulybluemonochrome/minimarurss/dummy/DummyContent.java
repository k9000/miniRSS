package com.tlulybluemonochrome.minimarurss.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

	/**
	 * An array of sample (dummy) items.
	 */
	public List<DummyItem> ITEMS = new ArrayList<DummyItem>();
	public static List<DummyItem> RSSITEMS = new ArrayList<DummyItem>();

	/**
	 * A map of sample (dummy) items, by ID.
	 */
	public static Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();


	/**
	 * A dummy item representing a piece of content.
	 */
	public static class DummyItem {
		public String id;
		public String content;
		public String title;
		public String url;
		int i = 1;

		public DummyItem(String id, String content, String title, String url) {
			this.id = id;
			this.content = content;
			this.title = title;
			this.url = url;
		}

		public DummyItem() {
			this.id = String.valueOf(i);
			i++;

		}

		@Override
		public String toString() {
			return content;
		}

		public void setTitle(String title) {
			this.title = title;
			this.content = title;

		}

		public void setLink(String link) {
			this.url = link;

		}

	}
}
