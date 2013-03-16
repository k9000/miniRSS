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
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class EntryActivity extends Activity implements
		LoaderCallbacks<ArrayList<RssItem>> {
	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	// private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mTitle;
	private String mUri;
	private int mPosition;
	private boolean mPass = false;
	private boolean noti = true;

	int selectColor = 0xff00aeef;

	private int mflag;

	ArrayList<RssFeed> items;

	// UI references.
	private EditText mTitleView;
	private EditText mUriView;
	private View mLoginStatusView;
	private TextView mPageTitle;
	private Button button;
	private ListView mListview;
	private ImageButton imageButton;
	private CheckBox checkBox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_entry);

		try {
			FileInputStream fis = openFileInput("SaveData.txt");
			ObjectInputStream ois = new ObjectInputStream(fis);
			items = (ArrayList<RssFeed>) ois.readObject();
			ois.close();
		} catch (Exception e) {
			Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
		}

		// レイアウトID登録
		mLoginStatusView = findViewById(R.id.login_status);
		mPageTitle = (TextView) findViewById(R.id.textView1);
		mListview = (ListView) findViewById(R.id.listView1);
		button = (Button) findViewById(R.id.regist_button);
		mTitleView = (EditText) findViewById(R.id.title);
		mUriView = (EditText) findViewById(R.id.uri);
		imageButton = (ImageButton) findViewById(R.id.imageButton1);
		checkBox = (CheckBox) findViewById(R.id.checkBox1);

		Bundle args = new Bundle();
		if (getIntent().getDataString() != null) {// RSS_Linkクリックから
			mflag = 2;// タイトル取得を目指す
			mUri = getIntent().getDataString();
			args.putString(ItemDetailFragment.ARG_ITEM_ID, getIntent()
					.getDataString());

		} else if (getIntent().getStringExtra(Intent.EXTRA_TEXT) != null) {// ページ共有から
			mflag = 1;// URI取得を目指す
			args.putString(ItemDetailFragment.ARG_ITEM_ID, getIntent()
					.getExtras().getString(Intent.EXTRA_TEXT));

		} else if (getIntent().getBooleanExtra("EDIT", false)) {// 編集クリックから
			mflag = 3;// 記事一覧取得を目指す
			// mItem = getIntent().getParcelableExtra("Parcelable");
			mPosition = getIntent().getExtras().getInt("POSITION");
			mTitle = items.get(mPosition).getTitle();
			mUri = items.get(mPosition).getUrl();
			noti = items.get(mPosition).getNoti();
			button.setText(R.string.edit);
			mPageTitle.setText(R.string.rss_feed_edit);
			args.putString(ItemDetailFragment.ARG_ITEM_ID, mUri);
			selectColor = items.get(mPosition).getTag();

		}

		if (getIntent().getExtras().getString("ADD") != null) {// 設定の追加ボタンから
			button.setText(R.string.check);
		} else {
			showProgress(true);
			getLoaderManager().initLoader(mflag, args, this);
		}

		// 状態に合わせてUIの文字とか変更
		imageButton.setBackgroundColor(selectColor);
		mTitleView.setText(mTitle);
		mUriView.setText(mUri);
		checkBox.setChecked(noti);

		// チェックボックスがクリックされた時に呼び出されるコールバックリスナーを登録します
		checkBox.setOnClickListener(new View.OnClickListener() {
			@Override
			// チェックボックスがクリックされた時に呼び出されます
			public void onClick(View v) {
				CheckBox checkBox = (CheckBox) v;
				// チェックボックスのチェック状態を取得します
				noti = checkBox.isChecked();

			}
		});

	}

	// プログレスバーのON/OFF
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {

		mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);

	}

	// 登録ボタン
	public void clickButton_Regist(View v) {
		if (mPass) {// 認証クリア
			if (getIntent().getBooleanExtra("EDIT", false)) {// 編集モード
				items.set(mPosition, new RssFeed(mTitleView.getText()
						.toString(), mUriView.getText().toString(),
						selectColor, noti));
				// アプリ画面起動
				Intent intent = new Intent(this,
						(Class<?>) ItemListActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);

			} else {// 追加モード
				items.add(new RssFeed(mTitleView.getText().toString(), mUriView
						.getText().toString(), selectColor, noti));
				Toast.makeText(
						this,
						mTitleView.getText().toString()
								+ getString(R.string.was_added),
						Toast.LENGTH_SHORT).show();
			}

			// データーセーブ
			try {
				FileOutputStream fos = this.openFileOutput("SaveData.txt",
						Context.MODE_PRIVATE);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(items);
				oos.close();
			} catch (Exception e1) {
				Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
			}

			// 終了
			this.finish();
		} else if (mTitleView.getText().toString().equals("")) {// タイトル探し
			Bundle args = new Bundle();
			args.putString(ItemDetailFragment.ARG_ITEM_ID, mUriView.getText()
					.toString());
			mflag = 2;
			showProgress(true);
			getLoaderManager().initLoader(mflag, args, this);
		} else {// URIチェック
			Bundle args = new Bundle();
			args.putString(ItemDetailFragment.ARG_ITEM_ID, mUriView.getText()
					.toString());
			mflag = 3;
			showProgress(true);
			getLoaderManager().initLoader(mflag, args, this);
		}
	}

	// キャンセルボタン
	public void clickButton_Cancel(View v) {
		if (getIntent().getBooleanExtra("EDIT", false)
				|| getIntent().getExtras().getString("ADD") != null) {
			Intent intent = new Intent(this, (Class<?>) ItemListActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(intent);
		}

		this.finish();
	}

	// 戻るボタン
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (getIntent().getBooleanExtra("EDIT", false)
					|| getIntent().getExtras().getString("ADD") != null) {// 編集時
				Intent intent = new Intent(this,
						(Class<?>) ItemListActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
			}

			this.finish();
			return true;
		}
		return false;
	}

	// 色選択
	public void clickButton_Color(View v) {

		ColorPickerDialog mColorPickerDialog;

		// 色選択ダイアログ
		mColorPickerDialog = new ColorPickerDialog(this,
				new ColorPickerDialog.OnColorChangedListener() {
					@Override
					public void colorChanged(int color) {
						selectColor = color;
						imageButton.setBackgroundColor(color);
					}
				}, selectColor);

		mColorPickerDialog.show();
	}

	// ASyncTaskLoader始動
	@Override
	public Loader<ArrayList<RssItem>> onCreateLoader(int flag, Bundle args) {
		String url = args.getString(ItemDetailFragment.ARG_ITEM_ID);
		RssParserTaskLoader appLoader = new RssParserTaskLoader(this, flag,
				url, this);

		appLoader.forceLoad();
		return appLoader;
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<RssItem>> arg0,
			ArrayList<RssItem> arg1) {
		if (arg1 == null) {// 失敗時
			if (getIntent().getBooleanExtra("EDIT", false)
					|| getIntent().getExtras().getString("ADD") != null) {// 編集時
				Intent intent = new Intent(this,
						(Class<?>) ItemListActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
			}
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);
			// アラートダイアログのタイトルを設定します
			// alertDialogBuilder.setTitle("フィードを取得出来ませんでした");
			// アラートダイアログのメッセージを設定します
			alertDialogBuilder.setMessage(R.string.can_not_get_feed);
			// アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
			alertDialogBuilder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							EntryActivity.this.finish();
						}
					});
			// アラートダイアログのキャンセルが可能かどうかを設定します
			alertDialogBuilder.setCancelable(true);
			AlertDialog alertDialog = alertDialogBuilder.create();
			// アラートダイアログを表示します
			alertDialog.show();

			return;
		}
		showProgress(false);// プログレスバー消す
		if (getIntent().getExtras().getString("ADD") != null) {// URIチェック通過
			button.setText(R.string.add);
		}

		switch (mflag) {
		case 1:// RSSのURIget
			mUriView.setText(arg1.get(0).getUrl());
			break;
		case 2:// RSSのタイトルget
			mTitleView.setText(arg1.get(0).getTitle());
			break;
		case 3:// 記事一覧get
			ArrayAdapter<RssItem> adapter = new ArrayAdapter<RssItem>(this,
					android.R.layout.simple_list_item_activated_1,
					android.R.id.text1, arg1);
			mListview.setAdapter(adapter);
			mPass = true;
			return;
		}
		mflag++;

		// Loader再始動
		showProgress(true);// プログレスバー再表示
		Bundle args = new Bundle();
		args.putString(ItemDetailFragment.ARG_ITEM_ID, mUriView.getText()
				.toString());
		getLoaderManager().restartLoader(mflag, args, this);

	}

	@Override
	public void onLoaderReset(Loader<ArrayList<RssItem>> arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}
}
