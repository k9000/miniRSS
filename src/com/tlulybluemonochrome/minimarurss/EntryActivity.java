package com.tlulybluemonochrome.minimarurss;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Build;
import android.os.Bundle;
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
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */
	private static final String[] DUMMY_CREDENTIALS = new String[] {
			"foo@example.com:hello", "bar@example.com:world" };

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
	private String mEmail;
	private String mPassword;
	private int mPosition;
	private boolean mPass = false;
	private RssFeed mItem;
	private boolean noti = false;

	int selectColor = 0xff00aeef;

	private int mflag;

	ArrayList<RssFeed> items;

	// UI references.
	private EditText mTitleView;
	private EditText mUriView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mPageTitle;
	private Button button;
	private ListView mListview;
	ImageButton imageButton;

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
			Toast.makeText(this, "error1", Toast.LENGTH_SHORT).show();
		}

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mPageTitle = (TextView) findViewById(R.id.textView1);
		mListview = (ListView) findViewById(R.id.listView1);

		button = (Button) findViewById(R.id.regist_button);

		mTitleView = (EditText) findViewById(R.id.title);

		mUriView = (EditText) findViewById(R.id.uri);

		imageButton = (ImageButton) findViewById(R.id.imageButton1);
		
		CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox1);


		Bundle args = new Bundle();
		if (getIntent().getDataString() != null) {
			mflag = 2;
			mUri = getIntent().getDataString();
			args.putString(ItemDetailFragment.ARG_ITEM_ID, getIntent()
					.getDataString());

		} else if (getIntent().getStringExtra(Intent.EXTRA_TEXT) != null) {
			mflag = 1;
			args.putString(ItemDetailFragment.ARG_ITEM_ID, getIntent()
					.getExtras().getString(Intent.EXTRA_TEXT));

		} else if (getIntent().getBooleanExtra("EDIT", false)) {
			mflag = 3;
			// mItem = getIntent().getParcelableExtra("Parcelable");
			mPosition = getIntent().getExtras().getInt("POSITION");
			mTitle = items.get(mPosition).getTitle();
			mUri = items.get(mPosition).getUrl();
			noti = items.get(mPosition).getNoti();
			button.setText("Edit");
			mPageTitle.setText("RSS Feed 編集");
			args.putString(ItemDetailFragment.ARG_ITEM_ID, mUri);
			// CustomDataのデータをViewの各Widgetにセットする

			selectColor = items.get(mPosition).getTag();

		}

		if (getIntent().getExtras().getString("ADD") != null) {
			button.setText("Check");
		} else {
			showProgress(true);
			getLoaderManager().initLoader(mflag, args, this);
		}

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

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {

		mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);

	}

	public void clickButton_Regist(View v) {
		if (mPass) {
			if (getIntent().getBooleanExtra("EDIT", false)) {
				items.set(mPosition,
						new RssFeed(mTitleView.getText().toString(), mUriView
								.getText().toString(), selectColor,noti));

			} else {
				items.add(new RssFeed(mTitleView.getText().toString(), mUriView
						.getText().toString(), selectColor,noti));
				Toast.makeText(this,
						mTitleView.getText().toString() + " を追加しました",
						Toast.LENGTH_SHORT).show();
			}

			Intent intent = new Intent(this, (Class<?>) ItemListActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(intent);

			try {
				FileOutputStream fos = this.openFileOutput("SaveData.txt",
						Context.MODE_PRIVATE);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(items);
				oos.close();
			} catch (Exception e1) {
				Toast.makeText(this, "error2", Toast.LENGTH_SHORT).show();
			}

			this.finish();
		} else {
			Bundle args = new Bundle();
			args.putString(ItemDetailFragment.ARG_ITEM_ID, mUriView.getText()
					.toString());
			mflag = 3;
			showProgress(true);
			getLoaderManager().initLoader(mflag, args, this);
		}
	}

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

	public void clickButton_Color(View v) {

		ColorPickerDialog mColorPickerDialog;

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

	@Override
	public Loader<ArrayList<RssItem>> onCreateLoader(int flag, Bundle args) {
		String url = args.getString(ItemDetailFragment.ARG_ITEM_ID);
		RssParserTaskLoader appLoader = new RssParserTaskLoader(this, flag, url);

		appLoader.forceLoad();
		return appLoader;
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<RssItem>> arg0,
			ArrayList<RssItem> arg1) {
		if (arg1 == null) {
			return;
		}
		if (getIntent().getExtras().getString("ADD") != null) {
			button.setText("Add");
		}

		switch (mflag) {
		case 1:
			mUriView.setText(arg1.get(0).getUrl());
			break;
		case 2:
			mTitleView.setText(arg1.get(0).getTitle());
			break;
		case 3:
			ArrayAdapter<RssItem> adapter = new ArrayAdapter<RssItem>(this,
					android.R.layout.simple_list_item_activated_1,
					android.R.id.text1, arg1);
			mListview.setAdapter(adapter);
			mPass = true;
			showProgress(false);
			return;
		}
		mflag++;

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
