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

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.origamilabs.library.views.StaggeredGridView;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Bitmap;

/**
 * タブレット用トップページ
 * 
 * @author k9000
 * 
 */
public class TopPageFragment extends Fragment  {

	private ArrayList<RssItem> list;

	private final ImageLoader imageLoader = ImageLoader.getInstance();

	// private ImageLoaderConfiguration config;

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	private DisplayImageOptions options;

	private PullToRefreshAttacher mPullToRefreshAttacher;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 * 
		 * @param position
		 */
		public void onAdapterSelected(final int tag, final String url,
				final int position);

	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onAdapterSelected(final int tag, final String url,
				final int position) {
		}

	};

	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	public TopPageFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		final View rootView = inflater.inflate(R.layout.fragment_top_page,
				container, false);

		final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getActivity()).memoryCacheExtraOptions(320, 320)
				.threadPoolSize(5)
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new LruMemoryCache(8 * 1024 * 1024))
				.memoryCacheSize(8 * 1024 * 1024).build();
		ImageLoader.getInstance().init(config);

		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new FadeInBitmapDisplayer(500)).build();

		final StaggeredGridView gridView = (StaggeredGridView) rootView
				.findViewById(R.id.googlecards_gridview);

		list = (ArrayList<RssItem>) getArguments().getSerializable("LIST");

		final GoogleCardsAdapter adapter = new GoogleCardsAdapter(
				getActivity(), 0, list);

		gridView.setAdapter(adapter);

		// 引っ張って更新
		mPullToRefreshAttacher = ((ItemListActivity) getActivity())
				.getPullToRefreshAttacher();
		final PullToRefreshLayout ptrLayout = (PullToRefreshLayout) rootView
				.findViewById(R.id.ptr_layout);
		ptrLayout.setPullToRefreshAttacher(mPullToRefreshAttacher, (ItemListActivity)getActivity());

		adapter.notifyDataSetChanged();

		return rootView;

	}

	private static class ViewHolder {
		ImageView image;
		TextView title, text;
		LinearLayout content;
	}

	private class GoogleCardsAdapter extends ArrayAdapter<RssItem> {

		private LayoutInflater layoutInflater_;

		public GoogleCardsAdapter(Context context, int textViewResourceId,
				List<RssItem> objects) {
			super(context, textViewResourceId, objects);
			layoutInflater_ = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
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
				holder.content = (LinearLayout) view.findViewById(R.id.card);
				view.setTag(holder);

			} else {
				holder = (ViewHolder) view.getTag();
				holder.image.setImageBitmap(null);

			}

			final RssItem item = (RssItem) getItem(position);

			if (item.getImage() != null) {
				holder.image.setVisibility(View.VISIBLE);
				imageLoader
						.displayImage(item.getImage(), holder.image, options);
				// makeImage(item.getImage(), holder.image);
			} else {
				holder.image.setVisibility(View.GONE);
			}

			holder.title.setText(item.getTitle());
			holder.text.setText(item.getPage() + "   "
					+ String.valueOf(item.getDate()));
			holder.content.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mCallbacks.onAdapterSelected(position, list.get(position)
							.getUrl(), position);
				}

			});

			return view;
		}
	}

}
