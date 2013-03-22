/*
 * Copyright (C) 2013 k9000
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tlulybluemonochrome.minimarurss;

import java.io.Serializable;

import android.graphics.Bitmap;

/**
 * 更新ページリストを1行毎に保存するクラス
 * 
 * @author k9000
 * 
 */
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
	private String image;

	public RssItem(String title, String url, String text, int tag, String page,
			String image) {
		this.title = title;
		this.url = url;
		this.text = text;
		this.tag = tag;
		this.page = page;
		this.image = image;
	}

	public RssItem() {

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

	public String getImage() {
		return image;
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

	public void setImage(String stripImageTags) {
		this.image = stripImageTags;
	}

	public Bitmap getImageData() {
		Bitmap bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
		bmp.eraseColor(tag);
		return bmp;
	}

}
