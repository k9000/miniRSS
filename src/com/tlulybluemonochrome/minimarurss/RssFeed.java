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

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

/**
 * RSSフィードリストを1行毎に保存するクラス
 * 
 * @author k9000
 * 
 */
public class RssFeed implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2407226097890754936L;
	private String title;
	private String url;
	private int tag;
	private boolean noti = false;
	private byte[] imageArray;

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

	public final String getTitle() {
		return title;
	}

	public final String getUrl() {
		return url;
	}

	public final int getTag() {
		return tag;
	}

	public final boolean getNoti() {
		return noti;
	}

	public final void setTitle(final String title) {
		this.title = title;
	}

	public final void setUrl(final String url) {
		this.url = url;
	}

	public final void setTag(final int tag) {
		this.tag = tag;
	}

	public final void setNoti(final boolean noti) {
		this.noti = noti;
	}

	public final void setImage(final Bitmap image) {
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		image.compress(CompressFormat.PNG, 100, os);
		this.imageArray = os.toByteArray();

	}

	public final Bitmap getImageData() {
		final Bitmap bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
		bmp.eraseColor(tag);
		return bmp;
	}

	public final Bitmap getImage() {
		if (imageArray != null)
			return BitmapFactory.decodeByteArray(imageArray, 0,
					imageArray.length);
		else
			return null;
	}

}
