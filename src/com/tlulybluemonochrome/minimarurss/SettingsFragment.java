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

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;

/**
 * 設定画面
 * 
 * @author k9000
 * 
 */
public class SettingsFragment extends Fragment {

	private boolean mChecked;
	private int mMinute;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {

		final SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		final Editor editor = sharedPreferences.edit();

		final View rootView = inflater.inflate(R.layout.fragment_settings,
				container, false);

		// ON/OFFボタンのリスナー
		mChecked = sharedPreferences.getBoolean("notification_switch", false);
		final Switch s = (Switch) rootView.findViewById(R.id.switch1);
		s.setOnCheckedChangeListener(null);
		s.setChecked(mChecked);
		s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(final CompoundButton buttonView,
					final boolean isChecked) {
				// s2.setChecked(isChecked);

				mChecked = isChecked;
				// mCallbacks.onCheckedChanged(isChecked);
				if (isChecked) {
					NotificationServiceStop();
					NotificationServiceSet();
					NotificationServiceStart();
				} else {
					NotificationServiceStop();
				}

				editor.putBoolean("notification_switch", isChecked);
				editor.commit();

			}
		});
		s.setChecked(mChecked);

		// ON/OFFボタンのリスナー
		final boolean picChecked = sharedPreferences.getBoolean("pic_switch",
				true);
		final Switch s2 = (Switch) rootView.findViewById(R.id.switch2);
		s2.setOnCheckedChangeListener(null);
		s2.setChecked(picChecked);
		s2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(final CompoundButton buttonView,
					final boolean isChecked) {
				editor.putBoolean("pic_switch", isChecked);
				editor.commit();
			}
		});

		SeekBar seekBar = (SeekBar) rootView.findViewById(R.id.seekBar1);
		seekBar.setMax(4);
		seekBar.setProgress(sharedPreferences
				.getInt("notification_freqescy", 2));

		final EditText editText = (EditText) rootView
				.findViewById(R.id.editText1);

		// シークバーの初期値をTextViewに表示
		frequencySet(seekBar.getProgress(), editText);

		// シークバーのリスナー
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onProgressChanged(final SeekBar seekBar,
					final int progress, final boolean fromUser) {
				// ツマミをドラッグしたときに呼ばれる
				frequencySet(progress, editText);

			}

			public void onStartTrackingTouch(final SeekBar seekBar) {
				// ツマミに触れたときに呼ばれる
			}

			public void onStopTrackingTouch(final SeekBar seekBar) {
				// ツマミを離したときに呼ばれる

				if (mChecked) {

					mMinute = seekBar.getProgress();
					NotificationServiceStop();
					NotificationServiceSet();
					NotificationServiceStart();
				}

				editor.putInt("notification_freqescy", seekBar.getProgress());
				editor.commit();
			}
		});

		// ON/OFFボタンのリスナー
		final boolean refChecked = sharedPreferences.getBoolean("ref_switch",
				true);
		final Switch s3 = (Switch) rootView.findViewById(R.id.switch3);
		s3.setOnCheckedChangeListener(null);
		s3.setChecked(refChecked);
		s3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(final CompoundButton buttonView,
					final boolean isChecked) {
				editor.putBoolean("ref_switch", isChecked);
				editor.commit();
			}
		});

		// ON/OFFボタンのリスナー
		final boolean backChecked = sharedPreferences.getBoolean("back_switch",
				false);
		final Switch s4 = (Switch) rootView.findViewById(R.id.switch4);
		s4.setOnCheckedChangeListener(null);
		s4.setChecked(backChecked);
		s4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(final CompoundButton buttonView,
					final boolean isChecked) {
				editor.putBoolean("back_switch", isChecked);
				editor.commit();
			}
		});

		final RadioGroup mRadioGroupOs1 = (RadioGroup) rootView
				.findViewById(R.id.radioGroup1);

		// ラジオボタンの初期値
		final String theme_preference = sharedPreferences.getString(
				"theme_preference", "Metal");
		if (theme_preference.equals("Simple"))
			mRadioGroupOs1.check(R.id.radio0);
		else if (theme_preference.equals("Metal"))
			mRadioGroupOs1.check(R.id.radio1);
		else if (theme_preference.equals("White"))
			mRadioGroupOs1.check(R.id.radio2);
		else if (theme_preference.equals("Gray"))
			mRadioGroupOs1.check(R.id.radio3);
		else if (theme_preference.equals("Dark"))
			mRadioGroupOs1.check(R.id.radio4);

		// ラジオボタンのリスナー
		mRadioGroupOs1
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(final RadioGroup group,
							final int id) {
						editor.putString("theme_preference",
								(String) ((RadioButton) rootView
										.findViewById(id)).getText());
						editor.commit();

					}

				});

		final RadioGroup mRadioGroupOs2 = (RadioGroup) rootView
				.findViewById(R.id.radioGroup2);

		// ラジオボタンの初期値
		final String animation_preference = sharedPreferences.getString(
				"animation", "Cube");
		if (animation_preference.equals("None"))
			mRadioGroupOs2.check(R.id.radio9);
		else if (animation_preference.equals("Cube"))
			mRadioGroupOs2.check(R.id.radio10);
		else if (animation_preference.equals("Tablet"))
			mRadioGroupOs2.check(R.id.radio11);
		else if (animation_preference.equals("Flip"))
			mRadioGroupOs2.check(R.id.radio12);
		else if (animation_preference.equals("Zoom"))
			mRadioGroupOs2.check(R.id.radio13);
		else if (animation_preference.equals("Rotate"))
			mRadioGroupOs2.check(R.id.radio14);

		// ラジオボタンのリスナー
		mRadioGroupOs2
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(final RadioGroup group,
							final int id) {
						editor.putString("animation",
								(String) ((RadioButton) rootView
										.findViewById(id)).getText());
						editor.commit();

					}

				});

		// Add RSS Feedボタンのリスナー
		final Button button = (Button) rootView.findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (v.getId() == R.id.button1) {
					Intent intent = new Intent(getActivity(),
							(Class<?>) EntryActivity.class);
					intent.putExtra("ADD", "add");
					startActivity(intent);
					getActivity().finish();
				}
			}
		});

		// SharedPreferenceの設定
		final int vCode = sharedPreferences.getInt("VersionCode", 1);
		final String vName = sharedPreferences.getString("VersionName", "1.0");

		// 最新のバージョン情報を取得する
		PackageInfo pi = null;
		try {
			pi = getActivity().getPackageManager().getPackageInfo(
					getActivity().getPackageName(),
					PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		// 最新のバージョン情報をPreferenceに保存
		editor.putInt("VersionCode", pi.versionCode);
		editor.putString("VersionName", pi.versionName);
		editor.commit();

		if (pi != null) {
			// VersionCode でVersionUPを判断する
			if (pi.versionCode > vCode) {
				// VersionCodeが上がっている場合
				if (mChecked) {

					NotificationServiceStop();
					NotificationServiceSet();
					NotificationServiceStart();
				}

			}

		}

		// Inflate the layout for this fragment
		return rootView;
	}

	public void frequencySet(final int position, final EditText editText) {
		switch (position) {
		case 0:
			editText.setText(R.string.fifteen_min);
			break;
		case 1:
			editText.setText(R.string.half_hour);
			break;
		case 2:
			editText.setText(R.string.hour);
			break;
		case 3:
			editText.setText(R.string.half_day);
			break;
		case 4:
			editText.setText(R.string.day);
			break;
		}
	}

	private void NotificationServiceStart() {
		getActivity().startService(
				new Intent(getActivity(), NotificationService.class));

	}

	// NotificationServiceをAlarmManagerに登録
	protected void NotificationServiceSet() {
		long time = AlarmManager.INTERVAL_HOUR;
		switch (mMinute) {
		case 0:
			time = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
			break;
		case 1:
			time = AlarmManager.INTERVAL_HALF_HOUR;
			break;
		case 2:
			time = AlarmManager.INTERVAL_HOUR;
			break;
		case 3:
			time = AlarmManager.INTERVAL_HALF_DAY;
			break;
		case 4:
			time = AlarmManager.INTERVAL_DAY;
			break;
		}

		final Intent intent = new Intent(getActivity().getApplicationContext(),
				NotificationService.class);
		final PendingIntent pendingIntent = PendingIntent.getService(
				getActivity().getApplicationContext(), -2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		final AlarmManager alarmManager = (AlarmManager)getActivity().getApplicationContext()
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setInexactRepeating(AlarmManager.RTC,
				System.currentTimeMillis(), time, pendingIntent);
	}

	// NotificationServiceのAlarmManager登録解除
	protected void NotificationServiceStop() {
		getActivity().stopService(
				new Intent(getActivity().getApplicationContext(), NotificationService.class));
		final Intent intent = new Intent(getActivity().getApplicationContext(),
				NotificationService.class);
		final PendingIntent pendingIntent = PendingIntent.getService(
				getActivity().getApplicationContext(), -2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		final AlarmManager alarmManager = (AlarmManager) getActivity().getApplicationContext()
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);

		RssMessageNotification.cancel(getActivity().getApplicationContext(), -1);
	}

}
