package com.tlulybluemonochrome.minimarurss;

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
import com.tlulybluemonochrome.minimarurss.dummy.DummyContent;
import com.tlulybluemonochrome.minimarurss.dummy.DummyContent.DummyItem;

import com.tlulybluemonochrome.minimarurss.RefreshableListView.OnRefreshListener;
import com.tlulybluemonochrome.minimarurss.RefreshableListView;

/**
 * A fragment representing a single Item detail screen. This fragment is either
 * contained in a {@link ItemListActivity} in two-pane mode (on tablets) or a
 * {@link ItemDetailActivity} on handsets.
 */
public class ItemDetailFragment extends Fragment implements
		LoaderCallbacks<ArrayAdapter<DummyContent.DummyItem>> {

	// ProgressDialog progressDialog;
	DummyContent dummycontent;
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

		// dummycontent = new DummyContent();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.main, container, false);

		mListView = (RefreshableListView) rootView.findViewById(R.id.listview);

		if (savedInstanceState == null) {
			getLoaderManager().initLoader(0, getArguments(), this);
		}

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
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri
						.parse(dummycontent.ITEMS.get(position).url));
				startActivity(intent);
			}
		});

		return rootView;
	}

	@Override
	public Loader<ArrayAdapter<DummyItem>> onCreateLoader(int wait, Bundle args) {
		dummycontent = new DummyContent();
		String url = args.getString(ItemDetailFragment.ARG_ITEM_ID);
		RssParserTaskLoader appLoader = new RssParserTaskLoader(getActivity(),
				new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
						android.R.layout.simple_list_item_activated_1,
						android.R.id.text1, dummycontent.ITEMS), url, wait);

		appLoader.forceLoad();
		return appLoader;
	}

	@Override
	public void onLoadFinished(Loader<ArrayAdapter<DummyItem>> arg0,
			ArrayAdapter<DummyItem> arg1) {
		mListView.setAdapter(arg1);
		if (mFlag) {
			mListView.completeRefreshing();
			mFlag = false;
		}

	}

	@Override
	public void onLoaderReset(Loader<ArrayAdapter<DummyItem>> arg0) {

	}

}
