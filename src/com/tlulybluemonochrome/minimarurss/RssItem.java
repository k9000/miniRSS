package com.tlulybluemonochrome.minimarurss;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class RssItem implements Parcelable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8557521632862260470L;
	private String title;
	private String url;
	private String text;
	private int tag;

	public RssItem(String title, String url, String text, int tag) {
		this.title = title;
		this.url = url;
		this.text = text;
		this.tag = tag;
	}

	public RssItem(Parcel in) {
		// writeToParcelで保存した順番で読み出す必要がある
		title = in.readString();
		url = in.readString();
		text = in.readString();
		tag = in.readInt();

	}
	
	public RssItem(){
		
	}

	@Override
	public String toString() {
		return title;
	}

	public String getTitle() {
		return title;
	}

	public String getUrl() {
		return url;
	}

	public String getText() {
		return text;
	}

	public int getTag() {
		return tag;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	@Override
	public int describeContents() {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO 自動生成されたメソッド・スタブ
		dest.writeString(title);
		dest.writeString(url);
		dest.writeString(text);
		dest.writeInt(tag);
	}

}
