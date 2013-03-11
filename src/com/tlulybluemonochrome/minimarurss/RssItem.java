package com.tlulybluemonochrome.minimarurss;

import java.io.Serializable;

import android.graphics.Bitmap;

public class RssItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8557521632862260470L;
	private String title;
	private String url;
	private String text;
	private int tag;
	private String page;

	public RssItem(String title, String url, String text, int tag, String page) {
		this.title = title;
		this.url = url;
		this.text = text;
		this.tag = tag;
		this.page = page;
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
	
	public String getPage() {
		return page;
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
	
	public void setPage(String page) {
		this.page = page;
	}
	
	public Bitmap getImageData() {
		Bitmap bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
		bmp.eraseColor(tag);
		return bmp;
	}



}
