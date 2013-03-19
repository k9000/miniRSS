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

import com.jfeinstein.jazzyviewpager.JazzyViewPager;
import com.jfeinstein.jazzyviewpager.JazzyViewPager.TransitionEffect;

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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

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

	// ViewPager mViewPager;

	private JazzyViewPager mJazzy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		/* Preferencesからテーマ設定 */
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String thme_preference = sharedPreferences.getString(
				"theme_preference", "Light");
		int theme = R.style.LightMetal;
		if (thme_preference.equals("Light"))
			theme = R.style.LightMetal;
		else if (thme_preference.equals("Dark"))
			theme = R.style.DarkGlass;
		else if (thme_preference.equals("Transparent"))
			theme = R.style.Glass;
		setTheme(theme);

		items = new ArrayList<RssFeed>();

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

		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		String animation = sharedPreferences.getString("animation", "Cube");
		TransitionEffect effect = TransitionEffect.CubeOut;
		if (animation.equals("Tablet"))
			effect = TransitionEffect.Tablet;
		else if (animation.equals("Cube"))
			effect = TransitionEffect.CubeOut;
		else if (animation.equals("Flip"))
			effect = TransitionEffect.FlipHorizontal;
		else if (animation.equals("Zoom"))
			effect = TransitionEffect.ZoomIn;
		else if (animation.equals("Rotate"))
			effect = TransitionEffect.RotateUp;

		setupJazziness(effect);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		// View

		// Set up the ViewPager with the sections adapter.
		// mViewPager = (ViewPager) findViewById(R.id.pager);

		/*
		mJazzy.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
			}

			// スクロール時処理(タブレット用)
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				if (mTwoPane)
					((ItemListFragment) getFragmentManager().findFragmentById(
							R.id.item_list)).setActivatedPosition(arg0 - 2);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});*/

		// mViewPager.setAdapter(mSectionsPagerAdapter);
		mJazzy.setCurrentItem(getIntent().getIntExtra(
				ItemDetailFragment.ARG_ITEM_ID, 1));

		// セーブデータオープン
		try {
			FileInputStream fis = openFileInput("SaveData.txt");
			ObjectInputStream ois = new ObjectInputStream(fis);
			items.addAll((ArrayList<RssFeed>) ois.readObject());
			ois.close();
		} catch (Exception e) {
		}

		if (items.isEmpty() || sharedPreferences.getInt("save_version", 0) != 1) {// セーブ空のとき
			// items = new ArrayList<RssFeed>();
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
			Editor editor = sharedPreferences.edit();
			editor.putInt("save_version", 1);
			editor.commit();
		}

		hp = new HashMap<String, ArrayList<RssItem>>();
		try {// 既読セーブデータオープン
			FileInputStream fis = openFileInput("RssData.dat");
			ObjectInputStream ois = new ObjectInputStream(fis);
			hp = (HashMap<String, ArrayList<RssItem>>) ois.readObject();
			ois.close();
			Toast.makeText(this, "読み込み成功", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
		}

		Bundle arguments = new Bundle();
		// arguments.putSerializable("RSS", items);
		getLoaderManager().initLoader(0, arguments, this);

		// タイトルバーのプログレスアイコンを表示する
		setProgressBarIndeterminateVisibility(true);
	}

	private void setupJazziness(TransitionEffect effect) {
		mJazzy = (JazzyViewPager) findViewById(R.id.jazzy_pager);
		mJazzy.setTransitionEffect(effect);
		mJazzy.setAdapter(mSectionsPagerAdapter);
		// mJazzy.setPageMargin(30);

	}

	/**
	 * Callback method from {@link ItemListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	// ItemLsitFragmentのリスナー
	@Override
	public void onItemSelected(int tag, String url, int position) {
		mJazzy.setCurrentItem(position + 2);

	}

	// 並べ替え用
	@Override
	public void onSetItems(ArrayList<RssFeed> items) {
		this.items = items;
		try {
			FileOutputStream fos = this.openFileOutput("SaveData.txt",
					Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(items);
			oos.close();
		} catch (Exception e1) {
		}
		mSectionsPagerAdapter.notifyDataSetChanged();

	}

	// 右上のメニュー作成
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.my_menu, menu);
		return true;
	}

	// メニューボタンクリック
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean ret = true;
		switch (item.getItemId()) {
		case R.id.item_setting:
			mJazzy.setCurrentItem(0);
			break;
		case R.id.item_list:
			mJazzy.setCurrentItem(1);
			break;
		default:
			ret = super.onOptionsItemSelected(item);
		}

		return ret;
	}

	// 戻るボタン
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mTwoPane == false && mJazzy.getCurrentItem() != 1)
				mJazzy.setCurrentItem(1);
			else
				this.finish();
			return true;
		}
		return false;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */

	public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);

		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Object obj = super.instantiateItem(container, position);
			mJazzy.setObjectForPosition(obj, position);
			return obj;
		}

		// ページ生成
		@Override
		public Fragment getItem(int position) {
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
				Bundle arguments = new Bundle();
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
			int count = items.size() + 2;
			return count;
		}

		// ページタイトル
		@Override
		public CharSequence getPageTitle(int position) {
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
		public int getItemPosition(Object object) {
		    return POSITION_NONE;
		}

	}

	@Override
	public Loader<ArrayList<RssItem>> onCreateLoader(int id, Bundle args) {
		// ArrayList<RssItem> rsslist = args.get;
		url = items.get(i).getUrl();
		int color = items.get(i).getTag();
		RssParserTaskLoader appLoader = new RssParserTaskLoader(this, url, 0,
				color, this);

		appLoader.forceLoad();
		return appLoader;
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<RssItem>> arg0,
			ArrayList<RssItem> arg1) {
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
			Editor editor = sharedPreferences.edit();
			editor.putInt("save_version", 1);
			editor.commit();
		}
		mSectionsPagerAdapter.notifyDataSetChanged();
		setProgressBarIndeterminateVisibility(false);

		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onLoaderReset(Loader<ArrayList<RssItem>> arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
