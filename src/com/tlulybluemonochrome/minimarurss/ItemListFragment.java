package com.tlulybluemonochrome.minimarurss;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.Loader;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.tlulybluemonochrome.minimarurss.dummy.DummyContent;
import com.tlulybluemonochrome.minimarurss.dummy.DummyContent.DummyItem;

/**
 * A list fragment representing a list of Items. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link ItemDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ItemListFragment extends ListFragment implements
		LoaderCallbacks<ArrayAdapter<DummyContent.DummyItem>> {

	DummyContent dummycontent;

	ProgressDialog progressDialog;

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(String id, String url);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(String id, String url) {
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ItemListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		dummycontent = new DummyContent();

		// TODO: replace with a real list adapter.
		if (getActivity().getIntent().getStringExtra(
				ItemDetailFragment.ARG_ITEM_ID) == null) {
			ArrayAdapter<DummyContent.DummyItem> arrayadapter = new ArrayAdapter<DummyContent.DummyItem>(
					getActivity(),
					android.R.layout.simple_list_item_activated_1,
					android.R.id.text1, dummycontent.ITEMS);
			arrayadapter
					.add(new DummyItem(
							"1",
							"ピックアップ",
							"",
							"http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=ir"));
			arrayadapter
					.add(new DummyItem("2", "社会", "",
							"http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=y"));
			arrayadapter
					.add(new DummyItem("3", "国際", "",
							"http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=w"));
			arrayadapter
					.add(new DummyItem("4", "ビジネス", "",
							"http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=b"));
			arrayadapter
					.add(new DummyItem("5", "政治", "",
							"http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=p"));
			arrayadapter
					.add(new DummyItem("6", "エンタメ", "",
							"http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=e"));
			arrayadapter
					.add(new DummyItem("7", "スポーツ", "",
							"http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=s"));
			arrayadapter
					.add(new DummyItem("8", "テクノロジー", "",
							"http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=t"));

			setListAdapter(arrayadapter);
		}

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));

		}

		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("取得中");
		progressDialog.setCancelable(true);
		progressDialog.show();

		if (getActivity().getIntent().getStringExtra(
				ItemDetailFragment.ARG_ITEM_ID) != null)
			getLoaderManager().initLoader(0,
					getActivity().getIntent().getExtras(), this);
		else
			progressDialog.dismiss();
	}

	@Override
	public void onAttach(Activity activity) {
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

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		mCallbacks.onItemSelected(dummycontent.ITEMS.get(position).id,
				dummycontent.ITEMS.get(position).url);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	@Override
	public Loader<ArrayAdapter<DummyItem>> onCreateLoader(int id, Bundle args) {
		String url = args.getString(ItemDetailFragment.ARG_ITEM_ID);
		RssParserTaskLoader appLoader = new RssParserTaskLoader(getActivity(),
				new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
						android.R.layout.simple_list_item_activated_1,
						android.R.id.text1, dummycontent.ITEMS), url);

		appLoader.forceLoad();
		return appLoader;
	}

	@Override
	public void onLoadFinished(Loader<ArrayAdapter<DummyItem>> arg0,
			ArrayAdapter<DummyItem> arg1) {
		setListAdapter(arg1);
		progressDialog.dismiss();

	}

	@Override
	public void onLoaderReset(Loader<ArrayAdapter<DummyItem>> arg0) {

	}
}
