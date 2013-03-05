package com.tlulybluemonochrome.minimarurss;

import android.app.Activity;
import android.app.ListFragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
public class ItemListFragment extends ListFragment {

	DummyContent dummycontent;

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
		 * 
		 * @param position
		 */
		public void onItemSelected(String tag, String url, int position);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(String tag, String url, int position) {
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

		dummycontent = new DummyContent();

		// TODO: replace with a real list adapter.

		ArrayAdapter<DummyContent.DummyItem> arrayadapter = new ArrayAdapter<DummyContent.DummyItem>(
				getActivity(), android.R.layout.simple_list_item_activated_1,
				android.R.id.text1, dummycontent.ITEMS);

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		int count = (sharedPreferences.getInt("COUNT", 0));
		for (int i = 0; i < count; i++) {
			arrayadapter.add(new DummyItem(sharedPreferences.getString(
					"ID" + i, ""),
					sharedPreferences.getString("TITLE" + i, ""),
					sharedPreferences.getString("TAG" + i, ""),
					sharedPreferences.getString("URL" + i, ""), ""));
		}

		if (count == 0) {
			arrayadapter
					.add(new DummyItem(
							"1",
							"ピックアップ",
							"RSS",
							"http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=ir",
							""));
			arrayadapter
					.add(new DummyItem(
							"2",
							"社会",
							"RSS",
							"http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=y",
							""));
			arrayadapter
					.add(new DummyItem(
							"3",
							"国際",
							"RSS",
							"http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=w",
							""));
			arrayadapter
					.add(new DummyItem(
							"4",
							"ビジネス",
							"RSS",
							"http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=b",
							""));
			arrayadapter
					.add(new DummyItem(
							"5",
							"政治",
							"RSS",
							"http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=p",
							""));
			arrayadapter
					.add(new DummyItem(
							"6",
							"エンタメ",
							"RSS",
							"http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=e",
							""));
			arrayadapter
					.add(new DummyItem(
							"7",
							"スポーツ",
							"RSS",
							"http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=s",
							""));
			arrayadapter
					.add(new DummyItem(
							"8",
							"テクノロジー",
							"RSS",
							"http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=t",
							""));
		}

		Editor editor = sharedPreferences.edit();
		count = arrayadapter.getCount();
		editor.putInt("COUNT", count);
		for (int i = 0; i < count; i++) {
			editor.putString("ID" + i, arrayadapter.getItem(i).getId());
			editor.putString("TITLE" + i, arrayadapter.getItem(i).getTitle());
			editor.putString("IAG" + i, arrayadapter.getItem(i).getTag());
			editor.putString("URL" + i, arrayadapter.getItem(i).getUrl());
		}
		editor.commit();

		setListAdapter(arrayadapter);

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
		mCallbacks.onItemSelected(dummycontent.ITEMS.get(position).tag,
				dummycontent.ITEMS.get(position).url, position);
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

	public void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

}
