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

import java.util.ArrayList;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.tlulybluemonochrome.minimarurss.RefreshableListView.OnRefreshListener;
import com.tlulybluemonochrome.minimarurss.RefreshableListView;

/**
 * 更新ページ表示用フラグメント
 * 
 * @author k9000
 * 
 */
public class ItemDetailFragment extends Fragment implements
		LoaderCallbacks<ArrayList<RssItem>> {

	ArrayList<RssItem> item;

	CustomDetailAdapter adapter;

	boolean mFlag = false;

	private RefreshableListView mListView;

	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.main, container, false);

		mListView = (RefreshableListView) rootView.findViewById(R.id.listview);

		item = new ArrayList<RssItem>();

		if (getArguments().getSerializable("LIST") != null) {
			item = (ArrayList<RssItem>) getArguments().getSerializable("LIST");
		}

		adapter = new CustomDetailAdapter(getActivity(), 0, item);

		mListView.setAdapter(adapter);

		// getLoaderManager().initLoader(0, getArguments(), this);

		// 引っ張って更新
		mListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh(RefreshableListView listView) {
				// TODO 自動生成されたメソッド・スタブ
				mFlag = true;
				getLoaderManager().restartLoader(500,
						ItemDetailFragment.this.getArguments(),
						ItemDetailFragment.this);
			}
		});

		return rootView;
	}

	// ASyncTaskLoader始動
	@Override
	public Loader<ArrayList<RssItem>> onCreateLoader(int wait, Bundle args) {
		RssParserTaskLoader appLoader = new RssParserTaskLoader(getActivity(),
				args.getString(ItemDetailFragment.ARG_ITEM_ID), wait,
				args.getInt("COLOR"), getActivity());
		appLoader.forceLoad();
		return appLoader;
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<RssItem>> arg0,
			ArrayList<RssItem> arg1) {
		if (arg1 == null) {// 失敗時(意味ないかも)
			Toast.makeText(getActivity(), "null", Toast.LENGTH_SHORT).show();
			return;
		}
		// リスト更新
		item = arg1;
		adapter = new CustomDetailAdapter(getActivity(), 0, arg1);
		mListView.setAdapter(adapter);
		if (mFlag) {// 引っ張って更新したとき
			mListView.completeRefreshing();
			LayoutAnimationController anim = getListCascadeAnimation();
			mListView.setLayoutAnimation(anim);
			mFlag = false;
		}

	}

	@Override
	public void onLoaderReset(Loader<ArrayList<RssItem>> arg0) {

	}

	private static LayoutAnimationController getListCascadeAnimation() {
		AnimationSet set = new AnimationSet(true);

		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(50);
		set.addAnimation(animation);

		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				-1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(100);
		set.addAnimation(animation);

		LayoutAnimationController controller = new LayoutAnimationController(
				set, 0.5f);
		return controller;
	}
}
