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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import shared.ui.actionscontentview.ActionsContentView;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;

/**
 * メインのActivity
 * 
 * @author k9000
 * 
 */
public class ItemListActivity extends Activity implements
		ItemListFragment.Callbacks, ItemDetailFragment.Callbacks,
		TopPageFragment.Callbacks, LoaderCallbacks<ArrayList<RssItem>> {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	private SharedPreferences sharedPreferences;

	private ArrayList<RssFeed> items;

	private final ArrayList<RssItem> alllist = new ArrayList<RssItem>();

	private final ArrayList<RssItem> nalllist = new ArrayList<RssItem>();

	private final HashMap<String, ArrayList<RssItem>> hp = new HashMap<String, ArrayList<RssItem>>();

	private final HashMap<String, ArrayList<RssItem>> nhp = new HashMap<String, ArrayList<RssItem>>();

	private String url;

	private int i = 0;

	private final SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(
			getFragmentManager());

	private EfectViewPager efectViewPager;

	private int set = 0;

	private MenuItem ref;

	// private SlidingMenu menu;
	private ActionsContentView viewActionsContentView;

	private PullToRefreshAttacher mPullToRefreshAttacher;

	// private int slide = 0;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		/* Preferencesからテーマ設定 */
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

		getActionBar().setHomeButtonEnabled(true);

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
			items.add(new RssFeed("ガジェット速報", "http://ggsoku.com/feed/",
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
			items.add(new RssFeed("ドロイドバンク", "http://androidnavi.net/feed/",
					0xfffcb414, true));
			items.add(new RssFeed("アンドロイダー", "https://androider.jp/rss/home/",
					0xff33b5e5, true));

			saveItem(items);// セーブ書き込み
			final Editor editor = sharedPreferences.edit();
			editor.putInt("save_version", 1);
			editor.commit();
		}

		try {// 既読セーブデータオープン
			FileInputStream fis = openFileInput("RssData.dat");
			ObjectInputStream ois = new ObjectInputStream(fis);
			hp.clear();
			hp.putAll((HashMap<String, ArrayList<RssItem>>) ois.readObject());
			ois.close();
			alllist.clear();
			for (String key : hp.keySet()) {
				alllist.addAll(hp.get(key));
			}
			Collections.sort(alllist, new Comparator<RssItem>() {
				@Override
				public int compare(RssItem lhs, RssItem rhs) {
					if (lhs.getDate() == null)
						return 1;
					else if (rhs.getDate() == null)
						return -1;
					else if (lhs.getDate().before(rhs.getDate()))
						return 1;
					else
						return -1;

				}
			});

		} catch (Exception e) {
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_action_content);
		
		// The attacher should always be created in the Activity's onCreate
        mPullToRefreshAttacher = PullToRefreshAttacher.get(this);
        
     // Retrieve the PullToRefreshLayout from the content view
        PullToRefreshLayout ptrLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);

		final String animation = sharedPreferences.getString("animation",
				"Cube");
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

		efectViewPager = (EfectViewPager) findViewById(R.id.jazzy_pager);
		EfectViewPager.setTransitionEffect(effect);
		efectViewPager.setAdapter(mSectionsPagerAdapter);
		efectViewPager.setOffscreenPageLimit(3);
		// efectViewPager.setPageMargin( (int)
		// (getResources().getDisplayMetrics().density * -8) );

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		// View

		efectViewPager.setCurrentItem(
				getIntent().getIntExtra(ItemDetailFragment.ARG_ITEM_ID, 1),
				false);
		efectViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				if (position == 0) {
					getActionBar().setTitle(" Setting");
					getActionBar().setDisplayHomeAsUpEnabled(true);

				} else if (position == 1) {
					getActionBar().setTitle("minimaruRSS");
					getActionBar().setDisplayHomeAsUpEnabled(false);
				} else {
					getActionBar().setTitle(" " + items.get(position - 2).getTitle());
					getActionBar().setDisplayHomeAsUpEnabled(true);
				}

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

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

		viewActionsContentView = (ActionsContentView) findViewById(R.id.actionsContentView);
		viewActionsContentView.setSwipingType(ActionsContentView.SWIPING_EDGE);

		if (sharedPreferences.getBoolean("ref_switch", true)
				&& savedInstanceState == null) {
			getLoaderManager().initLoader(0, null, this);
			// ref.setVisible(false);
			// タイトルバーのプログレスアイコンを表示する
			setProgressBarIndeterminateVisibility(true);
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("page", efectViewPager.getCurrentItem());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		efectViewPager.setCurrentItem(savedInstanceState.getInt("page", 1),
				false);
	}

	/**
	 * Callback method from {@link ItemListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	// ItemLsitFragmentのリスナー
	@Override
	public void onItemSelected(final int tag, final String url,
			final int position) {
		viewActionsContentView.showContent();
		efectViewPager.setCurrentItem(position + 2, true);
	}

	// 並べ替え用
	@Override
	public void onSetItems(final ArrayList<RssFeed> items) {
		this.items = items;
		saveItem(items);
		set = 3;
		mSectionsPagerAdapter.notifyDataSetChanged();

	}

	private void saveItem(final ArrayList<RssFeed> items2) {

		try {
			final FileOutputStream fos;
			fos = this.openFileOutput("SaveData.txt", Context.MODE_PRIVATE);
			final ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(items2);
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onAdapterSelected(int tag, String url, int position) {
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
	}

	// 右上のメニュー作成
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.my_menu, menu);
		// ref = menu.findItem(R.id.reflesh);
		if (sharedPreferences.getBoolean("ref_switch", true)) {
			//ref.setVisible(false);
		}
		return true;
	}

	// メニューボタンクリック
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean ret = true;
		switch (item.getItemId()) {
		case android.R.id.home:
			viewActionsContentView.showContent();
			efectViewPager.setCurrentItem(1);
			break;
		case R.id.item_setting:
			viewActionsContentView.showContent();
			efectViewPager.setCurrentItem(0);
			break;
		/*
		 * case R.id.reflesh: i = 0; getLoaderManager().initLoader(0, null,
		 * this); ref.setVisible(false); // タイトルバーのプログレスアイコンを表示する
		 * setProgressBarIndeterminateVisibility(true); break;
		 */
		default:
			ret = super.onOptionsItemSelected(item);
			break;
		}
		return ret;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */

	public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

		public SectionsPagerAdapter(final FragmentManager fm) {
			super(fm);

		}

		@Override
		public Object instantiateItem(final ViewGroup container,
				final int position) {
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
			// Fragment fragment;

			if (position == 0) {// 設定画面

				return new SettingsFragment();
			} else if (position == 1) {// top画面
				final Bundle arguments = new Bundle();
				arguments.putSerializable("LIST", alllist);
				final TopPageFragment fragment = new TopPageFragment();
				fragment.setArguments(arguments);
				return fragment;
			}

			else {// 記事一覧
				final Bundle arguments = new Bundle();
				arguments.putString(ItemDetailFragment.ARG_ITEM_ID,
						items.get(position - 2).getUrl());
				arguments
						.putString("TITLE", items.get(position - 2).getTitle());
				arguments.putInt("COLOR", items.get(position - 2).getTag());
				arguments.putSerializable("LIST",
						hp.get(items.get(position - 2).getUrl()));
				final ItemDetailFragment fragment = new ItemDetailFragment();
				fragment.setArguments(arguments);
				return fragment;
			}

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
			else if (position == 1)
				return "TopPage";
			// else if (position == 1)
			// return "LIST";
			else
				return items.get(position - 2).getTitle();

		}

		@Override
		public int getItemPosition(final Object object) {
			if (set > 0) {
				set--;
				return POSITION_NONE;
			}
			return POSITION_UNCHANGED;
		}

	}

	@Override
	public Loader<ArrayList<RssItem>> onCreateLoader(final int id,
			final Bundle args) {
		// ArrayList<RssItem> rsslist = args.get;
		url = items.get(i).getUrl();
		final int color = items.get(i).getTag();
		final RssParserTaskLoader appLoader = new RssParserTaskLoader(this,
				url, 0, color, items.get(i).getTitle());
		appLoader.forceLoad();
		return appLoader;
	}

	@Override
	public void onLoadFinished(final Loader<ArrayList<RssItem>> arg0,
			final ArrayList<RssItem> arg1) {
		if (arg1 == null) {// 失敗時
			// Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
		} else {
			nhp.put(url, arg1);
			nalllist.addAll(arg1);
			((ItemListFragment) getFragmentManager()
					.findFragmentById(R.id.menu)).setIcon(arg1.get(0).getUrl(),
					i);

		}
		i++;
		if (i < items.size()) {
			getLoaderManager().restartLoader(0, null, this);
		} else {
			if (!nalllist.isEmpty()) {
				Collections.sort(nalllist, new Comparator<RssItem>() {
					@Override
					public int compare(final RssItem lhs, final RssItem rhs) {
						if (lhs.getDate() == null)
							return 1;
						else if (rhs.getDate() == null)
							return -1;
						else if (lhs.getDate().before(rhs.getDate()))
							return 1;
						else
							return -1;

					}
				});

				try {// セーブ書き込み
					alllist.clear();
					alllist.addAll(nalllist);
					nalllist.clear();
					hp.clear();
					hp.putAll(nhp);
					nhp.clear();
					final FileOutputStream fos = this.openFileOutput(
							"RssData.dat", Context.MODE_PRIVATE);
					final ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.writeObject(hp);
					oos.close();
				} catch (Exception e1) {
				}
			}
			set = 3;
			mSectionsPagerAdapter.notifyDataSetChanged();
			setProgressBarIndeterminateVisibility(false);
			//ref.setVisible(true);
		}

	}

	@Override
	public void onLoaderReset(final Loader<ArrayList<RssItem>> arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

	// 戻るボタン
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (viewActionsContentView.isActionsShown())
				viewActionsContentView.showContent();
			else if (mTwoPane == false && efectViewPager.getCurrentItem() != 1
					&& sharedPreferences.getBoolean("back_switch", false))
				efectViewPager.setCurrentItem(1, true);
			else
				this.finish();
			return true;
		}
		return false;
	}

	@Override
	public void onDestroy() {
		// ImageCache.deleteAll(getCacheDir());
		getLoaderManager().destroyLoader(0);
		// efectViewPager.setAdapter(null);
		alllist.clear();
		nalllist.clear();
		hp.clear();
		nhp.clear();
		efectViewPager = null;
		items = null;
		// hp = null;
		super.onDestroy();

	}
	
	PullToRefreshAttacher getPullToRefreshAttacher() {
        return mPullToRefreshAttacher;
    }

	@Override
	public void onRefreshList(String string, ArrayList<RssItem> arg1) {
		hp.put(string, arg1);
		try {// セーブ書き込み
			final FileOutputStream fos = this.openFileOutput("RssData.dat",
					Context.MODE_PRIVATE);
			final ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(hp);
			oos.close();
			sharedPreferences.edit().putBoolean("card", true).commit();
		} catch (Exception e1) {
		}
	}

}
