package com.tlulybluemonochrome.minimarurss;

import java.io.Serializable;

import android.graphics.Bitmap;

public class RssFeed implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2265423572274338985L;
	private String title;
	private String url;
	private int tag;
	private boolean noti = false;

	public RssFeed(String title, String url, int tag, boolean noti) {
		this.title = title;
		this.url = url;
		this.tag = tag;
		this.noti = noti;
	}

	public RssFeed() {

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

	public int getTag() {
		return tag;
	}

	public boolean getNoti() {
		return noti;
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

	public void setNoti(boolean noti) {
		this.noti = noti;
	}

	public Bitmap getImageData() {
		Bitmap bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
		bmp.eraseColor(getTag());
		return bmp;
	}

}
