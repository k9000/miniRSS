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
import android.widget.ArrayAdapter;

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

		item = new ArrayList<RssItem>();

		mListView = (RefreshableListView) rootView.findViewById(R.id.listview);

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

	@Override
	public Loader<ArrayList<RssItem>> onCreateLoader(int wait, Bundle args) {
		String url = args.getString(ItemDetailFragment.ARG_ITEM_ID);
		RssParserTaskLoader appLoader = new RssParserTaskLoader(getActivity(),
				url, wait);

		appLoader.forceLoad();
		return appLoader;
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<RssItem>> arg0,
			ArrayList<RssItem> arg1) {
		if(arg1 == null){
			return;
		}
		item = arg1;
		ArrayAdapter<RssItem> adapter = new ArrayAdapter<RssItem>(
				getActivity(), android.R.layout.simple_list_item_activated_1,
				android.R.id.text1, item);
		mListView.setAdapter(adapter);
		if (mFlag) {
			mListView.completeRefreshing();
			mFlag = false;
		}

	}

	@Override
	public void onLoaderReset(Loader<ArrayList<RssItem>> arg0) {

	}

}
