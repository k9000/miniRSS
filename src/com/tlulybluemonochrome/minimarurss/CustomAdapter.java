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

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * RSSフィードリストを1行毎に格納するアダプタ
 * 
 * @author k9000
 * 
 */
public class CustomAdapter extends ArrayAdapter<RssFeed> {
	private LayoutInflater layoutInflater_;
	
	

	/**
	 * コンストラクタ
	 * 
	 * @param context
	 * @param textViewResourceId
	 * @param objects
	 */
	public CustomAdapter(Context context, int textViewResourceId,
			List<RssFeed> objects) {
		super(context, textViewResourceId, objects);
		layoutInflater_ = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		
		// 特定の行(position)のデータを得る
		RssFeed item = (RssFeed) getItem(position);

		// convertViewは使い回しされている可能性があるのでnullの時だけ新しく作る
		if (null == convertView) {
			convertView = layoutInflater_.inflate(R.layout.custom_layout, null);
		} else {
			// s = (Switch) convertView.findViewById(R.id.switch1);
			//Log.d("test", String.valueOf(position));
		}
		
		//

		// CustomDataのデータをViewの各Widgetにセットする
		ImageView imageView;
		imageView = (ImageView) convertView.findViewById(R.id.image);
		if (item.getNoti()) {
			imageView.setImageBitmap(item.getImageData());
		} else {
			imageView.setImageBitmap(null);
		}

		TextView textView;
		textView = (TextView) convertView.findViewById(R.id.text);
		textView.setText(item.getTitle());


		return convertView;
	}



}
