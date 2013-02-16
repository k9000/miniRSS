package com.tlulybluemonochrome.minimarurss.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

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
	public static class DummyItem implements Parcelable{
		public String id;
		public String title;
		public String tag;
		public String url;
		int i = 1;

		public DummyItem(String id, String title, String tag, String url) {
			this.id = id;
			this.title = title;
			this.tag = tag;
			this.url = url;
		}

		public DummyItem() {
			this.id = String.valueOf(i);
			i++;

		}

		public DummyItem(Parcel source) {
			// TODO 自動生成されたコンストラクター・スタブ
			id = source.readString(); 
			title = source.readString(); 
			tag = source.readString(); 
			url = source.readString(); 
		}

		@Override
		public String toString() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;

		}

		public void setTag(String tag) {
			this.tag = tag;

		}

		public void setLink(String link) {
			this.url = link;

		}

		@Override
		public int describeContents() {
			// TODO 自動生成されたメソッド・スタブ
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			// TODO 自動生成されたメソッド・スタブ
			dest.writeString(id);
			dest.writeString(title);
			dest.writeString(tag);
			dest.writeString(url);
			
			
		}
		
		public static final Parcelable.Creator<DummyItem> CREATOR
        = new Parcelable.Creator<DummyItem>() {

			@Override
			public DummyItem createFromParcel(Parcel source) {
				// TODO 自動生成されたメソッド・スタブ
				return new DummyItem(source);
			}

			@Override
			public DummyItem[] newArray(int size) {
				// TODO 自動生成されたメソッド・スタブ
				return new DummyItem[size];
			}
			
		};

	}
}
