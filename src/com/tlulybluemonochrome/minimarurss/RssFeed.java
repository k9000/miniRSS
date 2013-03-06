package com.tlulybluemonochrome.minimarurss;

import java.io.Serializable;

public class RssFeed implements Serializable {
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

}
