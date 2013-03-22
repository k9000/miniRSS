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

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import com.tlulybluemonochrome.minimarurss.CustomAdapter.CheckedChangedListenerInterface;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
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
 * 目次ページ
 * 
 * @author k9000
 * 
 */
public class ItemListFragment extends Fragment {

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
		//setHasOptionsMenu(true);

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
		
		adapter.setListener(new CheckedChangedListenerInterface() {

			@Override
			public void onCheckedChanged(int position, boolean isChecked) {
				items.get(position).setNoti(isChecked);
				mCallbacks.onSetItems(items);// リスナーでPagerViewer更新

			}

			// リストクリック(コールバックに飛ばす)
			@Override
			public void onClick(int position) {
				// TODO 自動生成されたメソッド・スタブ
				// Notify the active callbacks interface (the activity, if the
				// fragment is attached to one) that an item has been selected.
				mCallbacks.onItemSelected(items.get(position).getTag(), items
						.get(position).getUrl(), position);
			}

		});

		
		
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO 自動生成されたメソッド・スタブ
				mCallbacks.onItemSelected(items.get(position).getTag(), items
						.get(position).getUrl(), position);
			} 
			
		});

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

	// タブレット用
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}
	/*
	// 右上のメニュー作成
		@Override
			public void onCreateOptionsMenu(Menu menu,MenuInflater menuInflater) {
				super.onCreateOptionsMenu(menu, menuInflater);
				SharedPreferences sharedPreferences = PreferenceManager
						.getDefaultSharedPreferences(getActivity());
				menuInflater.inflate(R.menu.my_menu, menu);
				// ON/OFFボタンのリスナー
				Switch s2 = (Switch) menu.findItem(R.id.item_switch).getActionView();
				s2.setOnCheckedChangeListener(null);
				s2.setChecked(sharedPreferences.getBoolean("notification_switch", false));
				s2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						SharedPreferences sharedPreferences = PreferenceManager
								.getDefaultSharedPreferences(getActivity());
						Editor editor = sharedPreferences.edit();
						editor.putBoolean("notification_switch", isChecked);
						editor.commit();
					}
				});
				s2.setChecked(sharedPreferences.getBoolean("notification_switch", false));
			}*/

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		mListView
				.setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	public void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			mListView.setItemChecked(mActivatedPosition, false);
		} else {
			mListView.setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	class DragListener extends SortableListView.SimpleDragListener {
		private boolean mDrag;

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
														+ getString(R.string.removed_rss),
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
			mDrag = true;
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
			mDrag = true;
			return positionTo;
		}

		// ドラッグ終了
		@Override
		public boolean onStopDrag(int positionFrom, int positionTo) {
			mDraggingPosition = -1;
			mListView.invalidateViews();
			if (mDrag) {
				mCallbacks.onSetItems(items);
			}
			return super.onStopDrag(positionFrom, positionTo);
		}
	}
	/*
	 * @Override public void onCheckChanged(int position, boolean isChecked) {
	 * // TODO 自動生成されたメソッド・スタブ items.get(position).setNoti(isChecked);
	 * mCallbacks.onSetItems(items);// リスナーでPagerViewer更新 }
	 */
}
