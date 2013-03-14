package com.tlulybluemonochrome.minimarurss;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

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

	ArrayList<RssFeed> items;

	SortableListView mListView;

	int mDraggingPosition = -1;

	PopupMenu popup;

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
		public void onItemSelected(int tag, String url, int position);

		public void onSetItems(ArrayList<RssFeed> items);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(int tag, String url, int position) {
		}

		@Override
		public void onSetItems(ArrayList<RssFeed> items) {
			// TODO 自動生成されたメソッド・スタブ

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}

		View rootView = inflater.inflate(R.layout.fragment_item_list,
				container, false);

		// 並べ替えListView登録
		mListView = (SortableListView) rootView.findViewById(R.id.list);
		mListView.setDragListener(new DragListener());
		mListView.setSortable(true);

		// セーブデータオープン
		try {
			FileInputStream fis = getActivity().openFileInput("SaveData.txt");
			ObjectInputStream ois = new ObjectInputStream(fis);
			items = (ArrayList<RssFeed>) ois.readObject();
			ois.close();
		} catch (Exception e) {
		}

		CustomAdapter adapter = new CustomAdapter(getActivity(), 0, items);

		mListView.setAdapter(adapter);

		return rootView;
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

	// リストクリック(リスナーに飛ばす)
	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		mCallbacks.onItemSelected(items.get(position).getTag(),
				items.get(position).getUrl(), position);
	}

	// タブレット用
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

	class DragListener extends SortableListView.SimpleDragListener {
		// リスト長押し
		@Override
		public void onItemLongClick(AdapterView<?> parent, View view,
				final int position, long id) {

			// PopupMenuのインスタンスを作成
			popup = new PopupMenu(getActivity(), view);

			// popup.xmlで設定したメニュー項目をポップアップメニューに割り当てる
			popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());

			// ポップアップメニューを表示
			popup.show();

			// ポップアップメニューのメニュー項目のクリック処理
			popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {
					switch (item.getItemId()) {
					case R.id.menu_edit:// 編集クリック
						Intent intent = new Intent(getActivity(),
								(Class<?>) EntryActivity.class);
						// intent.putExtra("Parcelable", (Parcelable)items);
						intent.putExtra("EDIT", true);
						intent.putExtra("POSITION", position);
						startActivity(intent);
						getActivity().finish();
						break;

					case R.id.menu_delete:// 削除クリック
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								getActivity());
						// アラートダイアログのタイトルを設定します
						alertDialogBuilder.setTitle(items.get(position)
								.getTitle());
						// アラートダイアログのメッセージを設定します
						alertDialogBuilder.setMessage(R.string.remove_rss);
						// アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
						alertDialogBuilder.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Toast.makeText(
												getActivity(),
												items.get(position).getTitle()
														+ getString(R.string.removed_rss) ,
												Toast.LENGTH_SHORT).show();
										items.remove(position);
										mListView.invalidateViews();
										mCallbacks.onSetItems(items);// リスナーでPagerViewer更新

									}
								});
						alertDialogBuilder.setNegativeButton(R.string.cancel,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}
								});
						// アラートダイアログのキャンセルが可能かどうかを設定します
						alertDialogBuilder.setCancelable(true);
						AlertDialog alertDialog = alertDialogBuilder.create();
						// アラートダイアログを表示します
						alertDialog.show();

						break;

					}

					return true;
				}
			});

		}

		// ドラッグ開始
		@Override
		public int onStartDrag(int position) {
			Vibrator vibrator = (Vibrator) getActivity().getSystemService(
					Context.VIBRATOR_SERVICE);
			vibrator.vibrate(10);
			mDraggingPosition = position;
			mListView.invalidateViews();
			return position;
		}

		// ドラッグ中
		@Override
		public int onDuringDrag(int positionFrom, int positionTo) {
			if (positionFrom < 0 || positionTo < 0
					|| positionFrom == positionTo) {
				return positionFrom;
			}
			popup.dismiss();
			int i;
			if (positionFrom < positionTo) {
				final int min = positionFrom;
				final int max = positionTo;
				final RssFeed data = items.get(min);
				i = min;
				while (i < max) {
					items.set(i, items.get(++i));
				}
				items.set(max, data);
			} else if (positionFrom > positionTo) {
				final int min = positionTo;
				final int max = positionFrom;
				final RssFeed data = items.get(max);
				i = max;
				while (i > min) {
					items.set(i, items.get(--i));
				}
				items.set(min, data);
			}
			mDraggingPosition = positionTo;
			mListView.invalidateViews();
			return positionTo;
		}

		// ドラッグ終了
		@Override
		public boolean onStopDrag(int positionFrom, int positionTo) {
			mDraggingPosition = -1;
			mListView.invalidateViews();
			mCallbacks.onSetItems(items);
			return super.onStopDrag(positionFrom, positionTo);
		}
	}

}
