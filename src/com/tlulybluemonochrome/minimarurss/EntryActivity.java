package com.tlulybluemonochrome.minimarurss;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.widget.EditText;
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
		
		showProgress(true);
		Bundle args = new Bundle();
		if (getIntent().getDataString() != null) {
			mflag = 2;
			mUri = getIntent().getDataString();
			args.putString(ItemDetailFragment.ARG_ITEM_ID, getIntent()
					.getDataString());
			
		} else if (getIntent().getExtras().getString(Intent.EXTRA_TEXT) != null) {
			mflag = 1;
			args.putString(ItemDetailFragment.ARG_ITEM_ID, getIntent().getExtras().getString(Intent.EXTRA_TEXT));
			
		} else if (getIntent().getExtras().getString("URI") != null) {
			mflag = 3;
			mTitle = getIntent().getExtras().getString("TITLE");
			mUri = getIntent().getExtras().getString("URI");
			mPosition = getIntent().getExtras().getInt("POSITION");
			button.setText("Edit");
			mPageTitle.setText("RSS Feed 編集");
			args.putString(ItemDetailFragment.ARG_ITEM_ID, mUri);
		}
		getLoaderManager().initLoader(mflag, args, this);

		mTitleView = (EditText) findViewById(R.id.title);

		mUriView = (EditText) findViewById(R.id.uri);

		mTitleView.setText(mTitle);
		mUriView.setText(mUri);

		/*
		 * mPasswordView .setOnEditorActionListener(new
		 * TextView.OnEditorActionListener() {
		 * 
		 * @Override public boolean onEditorAction(TextView textView, int id,
		 * KeyEvent keyEvent) { if (id == R.id.login || id ==
		 * EditorInfo.IME_NULL) { // attemptLogin(); return true; } return
		 * false; } });
		 */

		

		/*
		 * findViewById(R.id.sign_in_button).setOnClickListener( new
		 * View.OnClickListener() {
		 * 
		 * @Override public void onClick(View view) { showProgress(true);
		 * getLoaderManager().initLoader(0, null, this.EntryActivity.class); //
		 * attemptLogin(); } });
		 */

	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * super.onCreateOptionsMenu(menu); getMenuInflater().inflate(R.menu.entry,
	 * menu); return true; }
	 */

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	/*
	 * public void attemptLogin() { if (mAuthTask != null) { return; }
	 * 
	 * // Reset errors. mEmailView.setError(null); mPasswordView.setError(null);
	 * 
	 * // Store values at the time of the login attempt. mEmail =
	 * mEmailView.getText().toString(); mPassword =
	 * mPasswordView.getText().toString();
	 * 
	 * boolean cancel = false; View focusView = null;
	 * 
	 * // Check for a valid password. if (TextUtils.isEmpty(mPassword)) {
	 * mPasswordView.setError(getString(R.string.error_field_required));
	 * focusView = mPasswordView; cancel = true; } else if (mPassword.length() <
	 * 4) { mPasswordView.setError(getString(R.string.error_invalid_password));
	 * focusView = mPasswordView; cancel = true; }
	 * 
	 * // Check for a valid email address. if (TextUtils.isEmpty(mEmail)) {
	 * mEmailView.setError(getString(R.string.error_field_required)); focusView
	 * = mEmailView; cancel = true; } else if (!mEmail.contains("@")) {
	 * mEmailView.setError(getString(R.string.error_invalid_email)); focusView =
	 * mEmailView; cancel = true; }
	 * 
	 * if (cancel) { // There was an error; don't attempt login and focus the
	 * first // form field with an error. focusView.requestFocus(); } else { //
	 * Show a progress spinner, and kick off a background task to // perform the
	 * user login attempt.
	 * mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
	 * showProgress(true); mAuthTask = new UserLoginTask();
	 * mAuthTask.execute((Void) null); } }
	 */

	/**
	 * Shows the progress UI and hides the login form.
	 */
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		/*
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {*/
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			//mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		//}
	}

	public void clickButton_Regist(View v) {
		if (getIntent().getExtras().getString("URI") != null) {
			items.set(mPosition, new RssFeed(mTitleView.getText().toString(),
					mUriView.getText().toString(), 1));
			Intent intent = new Intent(this, (Class<?>) ItemListActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(intent);

		} else {
			items.add(new RssFeed(mTitleView.getText().toString(), mUriView
					.getText().toString(), 1));
			Toast.makeText(this, mTitleView.getText().toString()+" を追加しました", Toast.LENGTH_SHORT).show();
		}

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
	}

	public void clickButton_Cancel(View v) {
		if (getIntent().getExtras().getString("URI") != null) {
			Intent intent = new Intent(this, (Class<?>) ItemListActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(intent);
			}
		
		this.finish();
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
		

		switch (mflag) {
		case 1:
			mUriView.setText(arg1.get(0).getUrl());
			break;
		case 2:
			mTitleView.setText(arg1.get(0).getTitle());
			break;
		case 3:
			ArrayAdapter<RssItem> adapter = new ArrayAdapter<RssItem>(
					this, android.R.layout.simple_list_item_activated_1,
					android.R.id.text1, arg1);
			mListview.setAdapter(adapter);
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

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	/*
	 * public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
	 * 
	 * @Override protected Boolean doInBackground(Void... params) { // TODO:
	 * attempt authentication against a network service.
	 * 
	 * try { // Simulate network access. Thread.sleep(2000); } catch
	 * (InterruptedException e) { return false; }
	 * 
	 * for (String credential : DUMMY_CREDENTIALS) { String[] pieces =
	 * credential.split(":"); if (pieces[0].equals(mEmail)) { // Account exists,
	 * return true if the password matches. return pieces[1].equals(mPassword);
	 * } }
	 * 
	 * // TODO: register the new account here. return true; }
	 * 
	 * @Override protected void onPostExecute(final Boolean success) { mAuthTask
	 * = null; showProgress(false);
	 * 
	 * if (success) { finish(); } else { mPasswordView
	 * .setError(getString(R.string.error_incorrect_password));
	 * mPasswordView.requestFocus(); } }
	 * 
	 * @Override protected void onCancelled() { mAuthTask = null;
	 * showProgress(false); } }
	 */
}
