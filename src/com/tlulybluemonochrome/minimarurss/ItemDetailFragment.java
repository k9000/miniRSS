package com.tlulybluemonochrome.minimarurss;

import java.util.ArrayList;

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.tlulybluemonochrome.minimarurss.RefreshableListView.OnRefreshListener;
import com.tlulybluemonochrome.minimarurss.RefreshableListView;

/**
 * A fragment representing a single Item detail screen. This fragment is either
 * contained in a {@link ItemListActivity} in two-pane mode (on tablets) or a
 * {@link ItemDetailActivity} on handsets.
 */
public class ItemDetailFragment extends Fragment implements
		LoaderCallbacks<ArrayList<RssItem>> {

	ArrayList<RssItem> item;
	
	CustomDetailAdapter adapter;

	boolean mFlag = false;

	private RefreshableListView mListView;

	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.main, container, false);

		mListView = (RefreshableListView) rootView.findViewById(R.id.listview);

		item = new ArrayList<RssItem>();

		adapter = new CustomDetailAdapter(getActivity(), 0,
				item);
		mListView.setAdapter(adapter);

		getLoaderManager().initLoader(0, getArguments(), this);

		// 引っ張って更新
		mListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh(RefreshableListView listView) {
				// TODO 自動生成されたメソッド・スタブ
				mFlag = true;
				getLoaderManager().restartLoader(500,
						ItemDetailFragment.this.getArguments(),
						ItemDetailFragment.this);
			}
		});

		// クリックしてブラウザ起動
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item
						.get(position).getUrl()));
				startActivity(intent);
			}
		});

		return rootView;
	}

	// ASyncTaskLoader始動
	@Override
	public Loader<ArrayList<RssItem>> onCreateLoader(int wait, Bundle args) {
		String url = args.getString(ItemDetailFragment.ARG_ITEM_ID);
		int color = args.getInt("COLOR");
		RssParserTaskLoader appLoader = new RssParserTaskLoader(getActivity(),
				url, wait,color,  getActivity());

		appLoader.forceLoad();
		return appLoader;
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<RssItem>> arg0,
			ArrayList<RssItem> arg1) {
		if (arg1 == null) {// 失敗時(意味ないかも)
			return;
		}
		// リスト更新
		item = arg1;
		adapter = new CustomDetailAdapter(getActivity(), 0,
				item);
		mListView.setAdapter(adapter);
		if (mFlag) {// 引っ張って更新したとき
			mListView.completeRefreshing();
			mFlag = false;
		}

	}

	@Override
	public void onLoaderReset(Loader<ArrayList<RssItem>> arg0) {

	}

}
