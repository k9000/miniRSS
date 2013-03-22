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

import jp.sharakova.android.urlimageview.UrlImageView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

	private boolean[] _first;

	static class ViewHolder {
		ImageView imageView;
		TextView textView;
		TextView textView2;
		ImageButton btn;
		LinearLayout content;
		UrlImageView urlImageView;
	}

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
		// accordionSet.deleteAccordion();
		// accordionSet = null;

		ViewHolder holder;

		// ビューを受け取る
		View view = convertView;

		// 特定の行(position)のデータを得る
		final RssItem item = (RssItem) getItem(position);

		// convertViewは使い回しされている可能性があるのでnullの時だけ新しく作る
		if (null == view) {

			view = layoutInflater_.inflate(R.layout.custom_detail_layout, null);

			ImageView imageView = (ImageView) view.findViewById(R.id.image);
			TextView textView = (TextView) view.findViewById(R.id.text);
			TextView textView2 = (TextView) view.findViewById(R.id.text2);
			ImageButton btn = (ImageButton) view.findViewById(R.id.btn1);
			LinearLayout content = (LinearLayout) view
					.findViewById(R.id.content1);
			UrlImageView urlImageView = (UrlImageView) view
					.findViewById(R.id.urlImageView);
			
			

			holder = new ViewHolder();
			holder.imageView = imageView;
			holder.textView = textView;
			holder.textView2 = textView2;
			holder.btn = btn;
			holder.content = content;
			holder.urlImageView = urlImageView;

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		holder.content.setVisibility(View.GONE);//convertView使い回しを誤魔化す

		// クリックしてブラウザ起動
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item
						.getUrl()));
				v.getContext().startActivity(intent);
			}
		});

		// CustomDataのデータをViewの各Widgetにセットする
		holder.imageView.setImageBitmap(item.getImageData());
		holder.textView.setText(item.getTitle());
		holder.textView2.setText(item.getText());

		if (item.getImage() == null) {
			holder.urlImageView.setVisibility(View.GONE);
		}
		
		new AccordionSet(holder.btn, holder.content, holder.urlImageView,
				item.getImage(), (getContext().getResources()
						.getDisplayMetrics().density) * 100);

		return view;
	}

}
