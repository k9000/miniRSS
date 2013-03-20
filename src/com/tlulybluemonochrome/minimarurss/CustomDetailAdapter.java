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
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 更新ページリストを1行毎に格納するアダプタ
 * 
 * @author k9000
 * 
 */
public class CustomDetailAdapter extends ArrayAdapter<RssItem> {
	private LayoutInflater layoutInflater_;

	// private boolean _first = true;

	AccordionSet _as1;

	private boolean[] _first;

	public CustomDetailAdapter(Context context, int textViewResourceId,
			List<RssItem> objects, boolean first) {
		super(context, textViewResourceId, objects);
		layoutInflater_ = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		_first = new boolean[getCount()];
		for (int i = 0; i < getCount(); i++) {
			_first[i] = first;
		}
		// Log.d("test", "const");
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 特定の行(position)のデータを得る
		final RssItem item = (RssItem) getItem(position);

		// convertViewは使い回しされている可能性があるのでnullの時だけ新しく作る
		if (null == convertView) {
			convertView = layoutInflater_.inflate(
					R.layout.custom_detail_layout, null);
		}

		// CustomDataのデータをViewの各Widgetにセットする

		ImageView imageView;
		imageView = (ImageView) convertView.findViewById(R.id.image);
		imageView.setImageBitmap(item.getImageData());

		TextView textView;
		textView = (TextView) convertView.findViewById(R.id.text);
		textView.setText(item.getTitle());

		TextView textView2;
		textView2 = (TextView) convertView.findViewById(R.id.text2);
		textView2.setText(item.getText());

		// final AccordionSet _as1;
		final ImageButton btn = (ImageButton) convertView
				.findViewById(R.id.btn1);
		final LinearLayout content = (LinearLayout) convertView
				.findViewById(R.id.content1);

		final LinearLayout layout = (LinearLayout) convertView
				.findViewById(R.id.layout);

		// Log.d("test", "getview");

		// クリックしてブラウザ起動
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item
						.getUrl()));

				v.getContext().startActivity(intent);

			}
		});

		if (_first[position]) {
			_first[position] = false;
			Handler handler = new Handler();
			handler.post(new Runnable() {
				@Override
				public void run() {
					// Log.d("test", "run");
					new AccordionSet(btn, content);
				}
			});
		}

		return convertView;
	}

}
