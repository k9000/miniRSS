package com.tlulybluemonochrome.minimarurss;

import java.io.Serializable;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class RssFeed implements Serializable, Parcelable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2265423572274338985L;
	private String title;
	private String url;
	private int tag;

	public RssFeed(String title, String url, int tag) {
		this.title = title;
		this.url = url;
		this.tag = tag;
	}

	public RssFeed(Parcel in) {
		// writeToParcelで保存した順番で読み出す必要がある
		title = in.readString();
		url = in.readString();
		tag = in.readInt();

	}

	public RssFeed() {

	}

	public static final Parcelable.Creator<RssFeed> CREATOR = new Parcelable.Creator<RssFeed>() {
		public RssFeed createFromParcel(Parcel in) {
			return new RssFeed(in);
		}

		public RssFeed[] newArray(int size) {
			return new RssFeed[size];
		}
	};

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

	public int getTag() {
		return tag;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public Bitmap getImageData() {
		Bitmap bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
		bmp.eraseColor(getTag());
		return bmp;
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
		dest.writeInt(tag);

	}

}
