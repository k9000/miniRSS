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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import net.margaritov.preference.colorpicker.ColorPickerDialog;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
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
		public void onItemSelected(final int tag, final String url,
				final int position);

		public void onSetItems(final ArrayList<RssFeed> items);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(final int tag, final String url,
				final int position) {
		}

		@Override
		public void onSetItems(final ArrayList<RssFeed> items) {
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
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setHasOptionsMenu(true);

	}

	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}

		final View rootView = inflater.inflate(R.layout.fragment_item_list,
				container, false);

		// 並べ替えListView登録
		mListView = new SortableListView(getActivity());
		mListView = (SortableListView) rootView.findViewById(R.id.list);
		mListView.setDragListener(new DragListener());
		mListView.setSortable(true);

		// セーブデータオープン
		try {
			FileInputStream fis = getActivity().openFileInput("SaveData.txt");
			ObjectInputStream ois = new ObjectInputStream(fis);
			items = (ArrayList<RssFeed>) ois.readObject();
			ois.close();
			mListView.setAdapter(new CustomAdapter(getActivity(), 0, items));
			
			
		} catch (Exception e) {
		}

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(final AdapterView<?> arg0, final View arg1,
					final int position, final long arg3) {
				// TODO 自動生成されたメソッド・スタブ
				mCallbacks.onItemSelected(items.get(position).getTag(), items
						.get(position).getUrl(), position);
			}

		});
	
		for(int i=0;i<items.size();i++){
			if(items.get(i).getImage() == null){
				makeImage(items.get(i).getUrl(), items.get(i));
			}
		}
		

		return rootView;
	}
	
	private void saveItem(ArrayList<RssFeed> items2) {
		try {
			final FileOutputStream fos;
			fos = getActivity().openFileOutput("SaveData.txt",
					Context.MODE_PRIVATE);
		final ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(items2);
		oos.close();
	} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
		e.printStackTrace();
	}
		
	}

	private void makeImage(final String image,final RssFeed rssFeed) {
		new Thread(new Runnable() {

			public void run() {
				
				try {
					final URL url = new URL(image);
					final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.getResponseCode();
					final String redirectUrl = connection.getURL().toString();
					//final String redirectUrl = connection.getHeaderField();
					connection.disconnect();
					final URL image_url = new URL("http://www.google.com/s2/favicons?domain_url="+redirectUrl);
					final InputStream is = (InputStream) image_url.getContent();
					rssFeed.setImage(BitmapFactory.decodeStream(is));
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				saveItem(items);
			}
		}).start();
	}

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

	// タブレット用
	@Override
	public void onSaveInstanceState(final Bundle outState) {
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
	public void setActivateOnItemClick(final boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		mListView
				.setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	public void setActivatedPosition(final int position) {
		if (position == ListView.INVALID_POSITION) {
			mListView.setItemChecked(mActivatedPosition, false);
		} else {
			mListView.setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	class DragListener extends SortableListView.SimpleDragListener {
		private boolean mDrag;
		// private int mDraggingPosition = -1;
		private PopupMenu popup;

		// リスト長押し
		@Override
		public void onItemLongClick(final AdapterView<?> parent,
				final View view, final int position, final long id) {

			// PopupMenuのインスタンスを作成
			popup = new PopupMenu(getActivity(), view);

			// popup.xmlで設定したメニュー項目をポップアップメニューに割り当てる
			popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
			popup.getMenu().findItem(R.id.item1)
					.setChecked(items.get(position).getNoti());

			// ポップアップメニューを表示
			popup.show();

			// ポップアップメニューのメニュー項目のクリック処理
			popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				public boolean onMenuItemClick(final MenuItem item) {
					switch (item.getItemId()) {
					case R.id.item1:// Notificationクリック
						item.setChecked(!item.isChecked());
						items.get(position).setNoti(
								!items.get(position).getNoti());
						mCallbacks.onSetItems(items);// リスナーでPagerViewer更新
						break;
					case R.id.menu_color:// colorクリック
						// 色選択ダイアログ
						ColorPickerDialog colorPickerDialog = new ColorPickerDialog(
								getActivity(), items.get(position).getTag());
						colorPickerDialog
								.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
									@Override
									public void onColorChanged(int color) {
										items.get(position).setTag(color);
										mCallbacks.onSetItems(items);// リスナーでPagerViewer更新
									}
								});
						colorPickerDialog.show();
						break;
					case R.id.menu_edit:// 編集クリック
						Intent intent = new Intent(getActivity(),
								(Class<?>) EntryActivity.class);
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
									public void onClick(
											final DialogInterface dialog,
											final int which) {
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
									public void onClick(
											final DialogInterface dialog,
											final int which) {
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
		public int onStartDrag(final int position) {
			Vibrator vibrator = (Vibrator) getActivity().getSystemService(
					Context.VIBRATOR_SERVICE);
			vibrator.vibrate(10);
			// mDraggingPosition = position;
			mListView.invalidateViews();
			return position;
		}

		// ドラッグ中
		@Override
		public int onDuringDrag(final int positionFrom, final int positionTo) {
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
			// mDraggingPosition = positionTo;
			mListView.invalidateViews();
			mDrag = true;
			return positionTo;
		}

		// ドラッグ終了
		@Override
		public boolean onStopDrag(final int positionFrom, final int positionTo) {
			// mDraggingPosition = -1;
			mListView.invalidateViews();
			if (mDrag) {
				mCallbacks.onSetItems(items);
			}
			return super.onStopDrag(positionFrom, positionTo);
		}
	}

	@Override
	public void onDestroyView() {
		mListView.setDragListener(null);
		mListView.setAdapter(null);
		mListView.setOnItemClickListener(null);
		mListView = null;
		items = null;
		super.onDestroyView();

	}

	public void setIcon(final String uri,final int i) {
		makeImage(uri, items.get(i));
		mListView.invalidateViews();
	}

}
