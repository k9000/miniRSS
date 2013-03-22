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

import java.util.EventListener;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

/**
 * RSSフィードリストを1行毎に格納するアダプタ
 * 
 * @author k9000
 * 
 */
public class CustomAdapter extends ArrayAdapter<RssFeed> {
	private LayoutInflater layoutInflater_;
	
	SortableListView list;

	CheckedChangedListenerInterface listener = null;

	private Switch s;

	public interface CheckedChangedListenerInterface extends
	EventListener {

		public void onCheckedChanged(int position, boolean isChecked);

		public void onClick(int position);

	}

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
		
		list = null;
		try{
		      list = (SortableListView)parent;
		    }catch(Exception e){
		      e.printStackTrace();
		    }
		list.requestDisallowInterceptTouchEvent(true);
		
		// 特定の行(position)のデータを得る
		RssFeed item = (RssFeed) getItem(position);

		// convertViewは使い回しされている可能性があるのでnullの時だけ新しく作る
		if (null == convertView) {
			convertView = layoutInflater_.inflate(R.layout.custom_layout, null);
		} else {
			// s = (Switch) convertView.findViewById(R.id.switch1);
			Log.d("test", String.valueOf(position));
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
		/*
		convertView.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO 自動生成されたメソッド・スタブ
				return false;
			}
			
		});*/
		
		/*

		s = (Switch) convertView.findViewById(R.id.switch1);

		s.setOnCheckedChangeListener(null);

		s.setChecked(item.getNoti());

		s.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (listener != null) {
					listener.onCheckedChanged(position, isChecked);
				}

			}
		});
		s.setChecked(item.getNoti());*/

		/*
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//list.performItemClick(v, position, (long)v.getId());
			}
		});*/
		
		//convertView.setTag(position);
		//convertView.setOnLongClickListener(list);
		//convertView.setOnTouchListener(list);

		return convertView;
	}

	/**
	 * リスナーを追加する
	 * 
	 * @param listener
	 */
	public void setListener(CheckedChangedListenerInterface listener) {
		this.listener = listener;
	}

	/**
	 * リスナーを削除する
	 */
	public void removeListener() {
		this.listener = null;
	}

}
