package com.tlulybluemonochrome.minimarurss;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GoogleCardsAdapter extends ArrayAdapter<RssItem> {

	private LayoutInflater layoutInflater_;
	
	private static final Handler handler = new Handler();
	
	static class ViewHolder {
		ImageView image;
		TextView title,text;
	}

	public GoogleCardsAdapter(Context context, int textViewResourceId,
			List<RssItem> objects) {
		super(context, textViewResourceId, objects);
		layoutInflater_ = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		// ビューを受け取る
		View view = convertView;

		// convertViewは使い回しされている可能性があるのでnullの時だけ新しく作る
		if (view == null) {
			view = layoutInflater_.inflate(R.layout.card, null);

			holder = new ViewHolder();
			holder.image = (ImageView) view.findViewById(R.id.card_image);
			holder.title = (TextView) view.findViewById(R.id.card_title);
			holder.text = (TextView) view.findViewById(R.id.card_text);

			view.setTag(holder);

		} else {
			holder = (ViewHolder) view.getTag();
			holder.image.setImageBitmap(null);

		}
		
		final RssItem item = (RssItem) getItem(position);
		
		if (item.getImage() != null) {
			holder.image.setVisibility(View.VISIBLE);
			makeImage(item.getImage(), holder.image);
		}else{
			holder.image.setVisibility(View.GONE);
		}
		
		holder.title.setText(item.getTitle());
		//holder.text.setText(item.getText());
		
		
		
/*
		// 特定の行(position)のデータを得る
		RssFeed item = (RssFeed) getItem(position);

		// CustomDataのデータをViewの各Widgetにセットする
		if (item.getNoti()) {
			holder.image.setImageBitmap(item.getImageData());
		} else {
			holder.image.setImageBitmap(null);
		}

		holder.text.setText(item.getTitle());
*/
		return view;
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
