package com.tlulybluemonochrome.minimarurss;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * An activity representing a list of Items. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link ItemDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ItemListFragment} and the item details (if present) is a
 * {@link ItemDetailFragment}.
 * <p>
 * This activity also implements the required {@link ItemListFragment.Callbacks}
 * interface to listen for item selections.
 */
public class ItemListActivity extends Activity implements
		ItemListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	SharedPreferences sharedPreferences;

	ArrayList<RssFeed> items;

	SectionsPagerAdapter mSectionsPagerAdapter;

	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		/* Preferencesからテーマ設定 */
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String thme_preference = sharedPreferences.getString(
				"theme_preference", "Light");
		int theme = android.R.style.Theme_Holo_Light;
		if (thme_preference.equals("Light"))
			theme = android.R.style.Theme_Holo_Light;
		else if (thme_preference.equals("Dark"))
			theme = android.R.style.Theme_Holo;
		else if (thme_preference.equals("Transparent"))
			theme = android.R.style.Theme_DeviceDefault_Wallpaper;
		setTheme(theme);

		try {
			FileInputStream fis = openFileInput("SaveData.txt");
			ObjectInputStream ois = new ObjectInputStream(fis);
			items = (ArrayList<RssFeed>) ois.readObject();
			ois.close();
		} catch (Exception e) {
			items = new ArrayList<RssFeed>();

			items.add(new RssFeed(
					"ピックアップ",
					"http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=ir",
					1));
			items.add(new RssFeed(
					"社会",
					"http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=y",
					1));
			items.add(new RssFeed(
					"国際",
					"http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=w",
					1));
			items.add(new RssFeed(
					"ビジネス",
					"http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=b",
					1));
			items.add(new RssFeed(
					"政治",
					"http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=p",
					1));
			items.add(new RssFeed(
					"エンタメ",
					"http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=e",
					1));
			items.add(new RssFeed(
					"スポーツ",
					"http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=s",
					1));
			items.add(new RssFeed(
					"テクノロジー",
					"http://news.google.com/news?hl=ja&ned=us&ie=UTF-8&oe=UTF-8&output=rss&topic=t",
					1));

			try {
				FileOutputStream fos = this.openFileOutput("SaveData.txt",
						Context.MODE_PRIVATE);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(items);
				oos.close();
			} catch (Exception e1) {
			}

		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_list);

		if (findViewById(R.id.item_detail_container) != null) {
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

		// sharedPreferences =
		// PreferenceManager.getDefaultSharedPreferences(this);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
			}

			// スクロール時
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				if (mTwoPane)
					((ItemListFragment) getFragmentManager().findFragmentById(
							R.id.item_list)).setActivatedPosition(arg0 - 2);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setCurrentItem(getIntent().getIntExtra(
				ItemDetailFragment.ARG_ITEM_ID, 1));

		// TODO: If exposing deep links into your app, handle intents here.
	}

	/**
	 * Callback method from {@link ItemListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(int tag, String url, int position) {
		mViewPager.setCurrentItem(position + 2);

	}

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
		
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.my_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean ret = true;
		switch (item.getItemId()) {
		case R.id.item_setting:
			mViewPager.setCurrentItem(0);
			break;
		case R.id.item_list:
			mViewPager.setCurrentItem(1);
			break;
		default:
			ret = super.onOptionsItemSelected(item);
		}

		return ret;
	}



	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);

		}

		// ページ生成
		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.

			// getIntent().getStringExtra(ItemDetailFragment.ARG_ITEM_ID));
			Fragment fragment;

			if (position == 0)
				fragment = new SettingsFragment();
			else if (position == 1 && mTwoPane)
				fragment = new TopPageFragment();
			else if (position == 1)
				fragment = new ItemListFragment();
			else {
				Bundle arguments = new Bundle();
				arguments.putString(ItemDetailFragment.ARG_ITEM_ID,
						items.get(position - 2).getUrl());
				fragment = new ItemDetailFragment();
				fragment.setArguments(arguments);
			}

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

	}




}
