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
import java.util.HashMap;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;
import android.view.Window;

/**
 * メインのActivity
 * 
 * @author k9000
 * 
 */
public class ItemListActivity extends Activity implements
		ItemListFragment.Callbacks, LoaderCallbacks<ArrayList<RssItem>> {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	SharedPreferences sharedPreferences;

	ArrayList<RssFeed> items;

	HashMap<String, ArrayList<RssItem>> hp;

	String url;

	int i = 0;

	SectionsPagerAdapter mSectionsPagerAdapter;

	private EfectViewPager efectViewPager;

	private int set = 0;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {

		/* Preferencesからテーマ設定 */
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		final String thme_preference = sharedPreferences.getString(
				"theme_preference", "Light");
		int theme = R.style.LightMetal;
		if (thme_preference.equals("Light"))
			theme = R.style.LightMetal;
		else if (thme_preference.equals("White"))
			theme = R.style.WhiteGlass;
		else if (thme_preference.equals("Dark"))
			theme = R.style.DarkGlass;
		else if (thme_preference.equals("Transparent"))
			theme = R.style.Glass;
		else if (thme_preference.equals("Gray"))
			theme = R.style.NoiseGray;
		setTheme(theme);

		// タイトルバーにプログレスアイコンを表示可能にする
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_list);

		if (findViewById(R.id.item_detail_container) != null) {// タブレット用
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((ItemListFragment) getFragmentManager().findFragmentById(
					R.id.item_list)).setActivateOnItemClick(true);
		}

		// セーブデータオープン
		try {
			FileInputStream fis = openFileInput("SaveData.txt");
			ObjectInputStream ois = new ObjectInputStream(fis);
			items = ((ArrayList<RssFeed>) ois.readObject());
			ois.close();
		} catch (Exception e) {
			items = new ArrayList<RssFeed>();
		}

		if (items.isEmpty() || sharedPreferences.getInt("save_version", 0) != 1) {// セーブ空のとき
			items = new ArrayList<RssFeed>();
			items.add(new RssFeed(
					"Googleニュース",
					"http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss",
					0xff00aeef, true));
			items.add(new RssFeed("4Gamer.net",
					"http://www.4gamer.net/rss/index.xml", 0xffffc0cb, true));
			items.add(new RssFeed("ORICON ニュース",
					"http://rss.rssad.jp/rss/oricon/news/total", 0xff99cc00,
					true));
			items.add(new RssFeed("映画.com", "http://feeds.eiga.com/eiga_news",
					0xffcc0000, true));
			items.add(new RssFeed("ASCII.jp",
					"http://rss.rssad.jp/rss/ascii/rss.xml", 0xfff9f903, true));
			items.add(new RssFeed("ガジェット通信", "http://getnews.jp/feed/ext/orig",
					0xfffcb414, true));
			items.add(new RssFeed("Impress Watch",
					"http://rss.rssad.jp/rss/headline/headline.rdf",
					0xffda31e5, true));
			items.add(new RssFeed("Engadget",
					"http://feed.rssad.jp/rss/engadget/rss", 0xff0000cd, true));
			items.add(new RssFeed("GIGAZINE",
					"http://feed.rssad.jp/rss/gigazine/rss_2.0", 0xff2f4f4f,
					true));
			items.add(new RssFeed("lifehacker",
					"http://feeds.lifehacker.jp/rss/lifehacker/index.xml",
					0xff808000, true));
			items.add(new RssFeed("痛いニュース(ﾉ∀`)",
					"http://blog.livedoor.jp/dqnplus/index.rdf", 0xff8b4513,
					true));
			items.add(new RssFeed("アルファルファモザイク",
					"http://alfalfalfa.com/index.rdf", 0xff808080, true));
			items.add(new RssFeed("andronavi", "http://andronavi.com/feed",
					0xffadd8e6, true));
			items.add(new RssFeed("オクトバ", "http://octoba.net/feed", 0xff9370db,
					true));

			try {// セーブ書き込み
				FileOutputStream fos = this.openFileOutput("SaveData.txt",
						Context.MODE_PRIVATE);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(items);
				oos.close();
			} catch (Exception e1) {
			}
			final Editor editor = sharedPreferences.edit();
			editor.putInt("save_version", 1);
			editor.commit();
		}

		hp = new HashMap<String, ArrayList<RssItem>>();
		try {// 既読セーブデータオープン
			FileInputStream fis = openFileInput("RssData.dat");
			ObjectInputStream ois = new ObjectInputStream(fis);
			hp = (HashMap<String, ArrayList<RssItem>>) ois.readObject();
			ois.close();
		} catch (Exception e) {
		}
		
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		final String animation = sharedPreferences.getString("animation", "Cube");
		int effect = 3;
		if (animation.equals("Tablet"))
			effect = 1;
		else if (animation.equals("Cube"))
			effect = 3;
		else if (animation.equals("Flip"))
			effect = 5;
		else if (animation.equals("Zoom"))
			effect = 7;
		else if (animation.equals("Rotate"))
			effect = 9;

		setupJazziness(effect);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		// View

		efectViewPager.setCurrentItem(getIntent().getIntExtra(
				ItemDetailFragment.ARG_ITEM_ID, 1));

		//getLoaderManager().initLoader(0, null, this);

		// タイトルバーのプログレスアイコンを表示する
		setProgressBarIndeterminateVisibility(true);
	}

	private void setupJazziness(final int effect) {
		efectViewPager = (EfectViewPager) findViewById(R.id.jazzy_pager);
		EfectViewPager.setTransitionEffect(effect);
		efectViewPager.setAdapter(mSectionsPagerAdapter);
		// mJazzy.setPageMargin(30);

	}

	/**
	 * Callback method from {@link ItemListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	// ItemLsitFragmentのリスナー
	@Override
	public void onItemSelected(final int tag,final String url,final int position) {
		efectViewPager.setCurrentItem(position + 2);

	}

	// 並べ替え用
	@Override
	public void onSetItems(final ArrayList<RssFeed> items) {
		this.items = items;
		try {
			FileOutputStream fos = this.openFileOutput("SaveData.txt",
					Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(items);
			oos.close();
		} catch (Exception e1) {
		}
		set = 3;
		mSectionsPagerAdapter.notifyDataSetChanged();
		

	}

	/*
	 * // 右上のメニュー作成
	 * 
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * super.onCreateOptionsMenu(menu);
	 * 
	 * MenuInflater inflater = getMenuInflater();
	 * inflater.inflate(R.menu.my_menu, menu); // ON/OFFボタンのリスナー s = (Switch)
	 * menu.findItem(R.id.item_switch).getActionView();
	 * s.setOnCheckedChangeListener(null);
	 * s.setChecked(sharedPreferences.getBoolean("notification_switch", false));
	 * s.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	 * 
	 * @Override public void onCheckedChanged(CompoundButton buttonView, boolean
	 * isChecked) { if (!mChecked) {
	 * 
	 * SharedPreferences sharedPreferences = PreferenceManager
	 * .getDefaultSharedPreferences(ItemListActivity.this); Editor editor =
	 * sharedPreferences.edit(); editor.putBoolean("notification_switch",
	 * isChecked); editor.commit();
	 * //mSectionsPagerAdapter.notifyDataSetChanged();
	 * 
	 * } mChecked = false;
	 * 
	 * } }); s.setChecked(sharedPreferences.getBoolean("notification_switch",
	 * false)); return true; }
	 */

	// メニューボタンクリック
	/*
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { boolean
	 * ret = true; switch (item.getItemId()) { case R.id.item_setting:
	 * mJazzy.setCurrentItem(0); break; case R.id.item_list:
	 * mJazzy.setCurrentItem(1); break; default: ret =
	 * super.onOptionsItemSelected(item); }
	 * 
	 * return ret; }
	 */

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */

	public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

		public SectionsPagerAdapter(final FragmentManager fm) {
			super(fm);

		}

		@Override
		public Object instantiateItem(final ViewGroup container, int position) {
			final Object obj = super.instantiateItem(container, position);
			EfectViewPager.setObjectForPosition(obj, position);
			return obj;
		}

		// ページ生成
		@Override
		public Fragment getItem(final int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.

			// getIntent().getStringExtra(ItemDetailFragment.ARG_ITEM_ID));
			Fragment fragment;

			if (position == 0)// 設定画面
				fragment = new SettingsFragment();
			else if (position == 1 && mTwoPane)// トップページ(タブレット用)
				fragment = new TopPageFragment();
			else if (position == 1)// フィードリスト
				fragment = new ItemListFragment();
			else {// 記事一覧
				final Bundle arguments = new Bundle();
				arguments.putString(ItemDetailFragment.ARG_ITEM_ID,
						items.get(position - 2).getUrl());
				arguments.putInt("COLOR", items.get(position - 2).getTag());
				arguments.putSerializable("LIST",
						hp.get(items.get(position - 2).getUrl()));
				fragment = new ItemDetailFragment();
				fragment.setArguments(arguments);
			}
			// fragment.getView();
			// mJazzy.setFragmentForPosition(getItemId(position), position);

			return fragment;
		}

		// 全ページ数
		@Override
		public int getCount() {
			final int count = items.size() + 2;
			return count;
		}

		// ページタイトル
		@Override
		public CharSequence getPageTitle(final int position) {
			if (position == 0)
				return "Setting";
			else if (position == 1 && mTwoPane)
				return "TopPage";
			else if (position == 1)
				return "LIST";
			else
				return items.get(position - 2).getTitle();

		}

		@Override
		public int getItemPosition(final Object object) {
			if (set>0) {
				set--;
				return POSITION_NONE;
			}
			return POSITION_UNCHANGED;
		}

	}

	@Override
	public Loader<ArrayList<RssItem>> onCreateLoader(final int id,final Bundle args) {
		// ArrayList<RssItem> rsslist = args.get;
		url = items.get(i).getUrl();
		final int color = items.get(i).getTag();
		final RssParserTaskLoader appLoader = new RssParserTaskLoader(this, url, 0,
				color, this);

		appLoader.forceLoad();
		return appLoader;
	}

	@Override
	public void onLoadFinished(final Loader<ArrayList<RssItem>> arg0,
			final ArrayList<RssItem> arg1) {
		if (arg1 == null) {// 失敗時(意味ないかも)
			return;
		}
		hp.put(url, arg1);
		i++;
		if (i < items.size()) {
			getLoaderManager().restartLoader(0, null, this);
		} else {
			try {// セーブ書き込み
				FileOutputStream fos = this.openFileOutput("RssData.dat",
						Context.MODE_PRIVATE);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(hp);
				oos.close();
			} catch (Exception e1) {
			}
			final Editor editor = sharedPreferences.edit();
			editor.putInt("save_version", 1);
			editor.commit();

			set = 3;
			mSectionsPagerAdapter.notifyDataSetChanged();
			setProgressBarIndeterminateVisibility(false);
		}

	}

	@Override
	public void onLoaderReset(final Loader<ArrayList<RssItem>> arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onDestroy() {
		//ImageCache.deleteAll(getCacheDir());
		getLoaderManager().destroyLoader(0);
		//efectViewPager.setAdapter(null);
		efectViewPager = null;
		mSectionsPagerAdapter = null;
		sharedPreferences = null;
		items = null;
		hp = null;
		super.onDestroy();

	}

}
