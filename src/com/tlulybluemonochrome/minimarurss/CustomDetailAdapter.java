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
import jp.sharakova.android.urlimageview.UrlImageView.OnImageLoadListener;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.DecelerateInterpolator;
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

	private static Handler handler;
	private static float height;
	private static float current = 0.0f;
	private static float rotation = -90.0f;
	private static Thread thread;
	private static int startTime;
	private static DecelerateInterpolator mInterpolator;
	private static final int easeTime = 400;

	static class ViewHolder {
		ImageView imageView;
		TextView textView;
		TextView textView2;
		ImageButton btn;
		LinearLayout content;
		UrlImageView urlImageView;
		boolean bound;
	}

	public CustomDetailAdapter(final Context context,
			final int textViewResourceId, final List<RssItem> objects) {
		super(context, textViewResourceId, objects);
		layoutInflater_ = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(final int position, View convertView,
			final ViewGroup parent) {

		final ViewHolder holder;

		// ビューを受け取る
		View view = convertView;
		

		// 特定の行(position)のデータを得る
		final RssItem item = (RssItem) getItem(position);

		// convertViewは使い回しされている可能性があるのでnullの時だけ新しく作る
		if (view == null) {
			view = layoutInflater_.inflate(R.layout.custom_detail_layout, null);

			holder = new ViewHolder();
			holder.imageView = (ImageView) view.findViewById(R.id.image);
			holder.textView = (TextView) view.findViewById(R.id.text);
			holder.textView2 = (TextView) view.findViewById(R.id.text2);
			holder.btn = (ImageButton) view.findViewById(R.id.btn1);
			holder.content = (LinearLayout) view.findViewById(R.id.content1);
			holder.urlImageView = (UrlImageView) view
					.findViewById(R.id.urlImageView);
			holder.bound = false;

			view.setTag(holder);
		} else {
			convertView.setOnClickListener(null);
			convertView = null;
			
			holder = (ViewHolder) view.getTag();
			if (holder.bound) {
				holder.content.setLayoutParams(new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT, 0));
				holder.btn.setRotation(-90);
				holder.bound = false;
			}

		}

		holder.content.setVisibility(View.GONE);

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

		height = (getContext().getResources().getDisplayMetrics().density) * 100;
		handler = new Handler();
		mInterpolator = new DecelerateInterpolator();
		holder.btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startTime = (int) System.currentTimeMillis();
				if (holder.bound) {
					holder.bound = false;
				} else {
					holder.content.setVisibility(View.VISIBLE);
					if (item.getImage() != null) {

						holder.urlImageView.setImageUrl(item.getImage(),
								imageLoadListener);
					}
					holder.bound = true;
				}

				if (thread == null || !thread.isAlive()) {
					thread = null;
					makeThread(holder.bound, holder.btn, holder.content);
					thread.start();
				}

			}
		});

		return view;
	}

	private static void makeThread(final boolean bound, final ImageButton btn,
			final LinearLayout content) {
		thread = new Thread(new Runnable() {
			public void run() {
				while (easeTime > (int) System.currentTimeMillis() - startTime) {
					int diff = (int) System.currentTimeMillis() - startTime;
					if (bound) {
						current = height
								* mInterpolator.getInterpolation((float) diff
										/ (float) easeTime);
						rotation = 90 * mInterpolator
								.getInterpolation((float) diff
										/ (float) easeTime) - 90;
					} else {
						current = height
								- height
								* mInterpolator.getInterpolation((float) diff
										/ (float) easeTime);
						rotation = 0 - 90 * mInterpolator
								.getInterpolation((float) diff
										/ (float) easeTime);
					}
					threadFunc(btn, content);
				}
			}
		});
	}

	private static void threadFunc(final ImageButton btn,
			final LinearLayout content) {
		handler.post(new Runnable() {

			public void run() {
				content.setLayoutParams(new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT, (int) current));
				btn.setRotation(rotation);
			}
		});
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
		}
	}

	final private OnImageLoadListener imageLoadListener = new OnImageLoadListener() {
		public void onStart(String url) {
		}

		public void onComplete(String url) {
		}
	};
	
	public void destroy(){
		layoutInflater_ = null;
		handler = null;
		thread = null;
		mInterpolator = null;

	}

}
