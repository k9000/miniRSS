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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ListView;
import android.widget.TextView;

/**
 * 更新ページリストを1行毎に格納するアダプタ
 * 
 * @author k9000
 * 
 */
public class CustomDetailAdapter extends ArrayAdapter<RssItem> {
	private LayoutInflater layoutInflater_;

	private static final Handler handler = new Handler();
	private static int height;
	private static int current = 0;
	private static int rotation = -90;

	static class ViewHolder {
		ImageView imageView;
		TextView textView;
		TextView textView2;
		ImageButton btn;
		LinearLayout content;
		ImageView urlImageView;
		boolean bound;
	}

	public CustomDetailAdapter(final Context context,
			final int textViewResourceId, final List<RssItem> objects) {
		super(context, textViewResourceId, objects);
		layoutInflater_ = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		height = (int) ((getContext().getResources().getDisplayMetrics().density) * 100);

	}

	@Override
	public View getView(final int position, final View convertView,
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
			holder.urlImageView = (ImageView) view
					.findViewById(R.id.urlImageView);
			holder.bound = false;

			final Bitmap bmp = Bitmap.createBitmap(1, 1,
					Bitmap.Config.ARGB_8888);
			bmp.eraseColor(getItem(0).getTag());
			holder.imageView.setImageBitmap(bmp);// 高速化のため

			view.setTag(holder);
		} else {
			// convertView.setOnClickListener(null);
			// convertView = null;

			holder = (ViewHolder) view.getTag();
			if (holder.bound) {
				holder.content.setLayoutParams(new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT, 0));
				holder.btn.setRotation(-90);
				holder.bound = false;
				holder.content.setVisibility(View.GONE);
			}

		}

		// クリックしてブラウザ起動
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				((ListView) parent).performItemClick(v, position,
						(long) v.getId());
			}
		});

		// CustomDataのデータをViewの各Widgetにセットする

		holder.textView.setText(item.getTitle());
		holder.textView2.setText(item.getText());

		if (item.getImage() == null) {
			holder.urlImageView.setVisibility(View.GONE);
		}

		holder.btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {

				if (holder.bound) {
					holder.bound = false;
				} else {
					holder.content.setVisibility(View.VISIBLE);
					if (item.getImage() != null) {

						// holder.urlImageView.setImageUrl(item.getImage());
					}
					holder.bound = true;
				}
				makeThread(holder.bound, holder.btn, holder.content);
				if (item.getImage() != null) {
					makeImage(item.getImage(), holder.urlImageView);
				}
			}
		});

		return view;
	}

	private static void makeThread(final boolean bound, final ImageButton btn,
			final LinearLayout content) {
		new Thread(new Runnable() {
			public void run() {
				final int startTime = (int) System.currentTimeMillis();
				final int easeTime = 400;
				while (easeTime > (int) System.currentTimeMillis() - startTime) {
					final int diff = (int) System.currentTimeMillis()
							- startTime;
					final DecelerateInterpolator mInterpolator = new DecelerateInterpolator();
					if (bound) {
						current = (int) (height * mInterpolator
								.getInterpolation((float) diff
										/ (float) easeTime));
						rotation = (int) (90 * mInterpolator
								.getInterpolation((float) diff
										/ (float) easeTime) - 90);
					} else {
						current = (int) (height - height
								* mInterpolator.getInterpolation((float) diff
										/ (float) easeTime));
						rotation = (int) (0 - 90 * mInterpolator
								.getInterpolation((float) diff
										/ (float) easeTime));
					}
					threadFunc(btn, content);
				}
			}
		}).start();
	}

	private static void threadFunc(final ImageButton btn,
			final LinearLayout content) {
		handler.post(new Runnable() {

			public void run() {
				content.setLayoutParams(new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT, current));
				btn.setRotation(rotation);
			}
		});
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
		}
	}

	private static void makeImage(final String image,
			final ImageView urlImageView) {
		new Thread(new Runnable() {

			public void run() {
				try {
					final URL image_url = new URL(image);
					final InputStream is = (InputStream) image_url.getContent();
					imageFunc(BitmapFactory.decodeStream(is), urlImageView);
					is.close();

				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}).start();
	}

	private static void imageFunc(final Bitmap bitmap,
			final ImageView urlImageView) {
		handler.post(new Runnable() {
			public void run() {
				urlImageView.setImageBitmap(bitmap);
			}
		});
	}

}
