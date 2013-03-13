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
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface to handle
 * interaction events. Use the {@link SettingsFragment#newInstance} factory
 * method to create an instance of this fragment.
 * 
 */
public class SettingsFragment extends Fragment implements
		CompoundButton.OnCheckedChangeListener, OnClickListener {

	Switch s;
	SeekBar seekBar;
	EditText editText;
	RadioGroup mRadioGroupOs;
	boolean mChecked;
	int mMinute = 2;
	Button button;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());

		final View rootView = inflater.inflate(R.layout.fragment_settings,
				container, false);

		// ON/OFFボタンのリスナー
		mChecked = sharedPreferences.getBoolean("notification_switch", false);
		s = (Switch) rootView.findViewById(R.id.switch1);
		s.setChecked(mChecked);
		s.setOnCheckedChangeListener(this);

		seekBar = (SeekBar) rootView.findViewById(R.id.seekBar1);
		seekBar.setMax(4);
		seekBar.setProgress(sharedPreferences
				.getInt("notification_freqescy", 2));

		editText = (EditText) rootView.findViewById(R.id.editText1);

		// シークバーの初期値をTextViewに表示
		frequencySet(seekBar.getProgress());

		// シークバーのリスナー
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// ツマミをドラッグしたときに呼ばれる
				frequencySet(progress);

			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// ツマミに触れたときに呼ばれる
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				// ツマミを離したときに呼ばれる

				if (mChecked) {

					mMinute = seekBar.getProgress();
					NotificationServiceStop();
					NotificationServiceSet();
					NotificationServiceStart();
				}

				SharedPreferences sharedPreferences = PreferenceManager
						.getDefaultSharedPreferences(getActivity());
				Editor editor = sharedPreferences.edit();
				editor.putInt("notification_freqescy", seekBar.getProgress());
				editor.commit();
			}
		});

		mRadioGroupOs = (RadioGroup) rootView.findViewById(R.id.radioGroup1);

		// ラジオボタンの初期値
		String theme_preference = sharedPreferences.getString(
				"theme_preference", "Dark");
		if (theme_preference.equals("Light"))
			mRadioGroupOs.check(R.id.radio0);
		else if (theme_preference.equals("Dark"))
			mRadioGroupOs.check(R.id.radio1);
		else if (theme_preference.equals("Transparent"))
			mRadioGroupOs.check(R.id.radio2);

		// ラジオボタンのリスナー
		mRadioGroupOs.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int id) {
				SharedPreferences sharedPreferences = PreferenceManager
						.getDefaultSharedPreferences(getActivity());
				Editor editor = sharedPreferences.edit();
				editor.putString("theme_preference",
						(String) ((RadioButton) rootView.findViewById(id))
								.getText());
				editor.commit();

			}

		});

		// Add RSS Feedボタンのリスナー
		button = (Button) rootView.findViewById(R.id.button1);
		button.setOnClickListener(this);

		// SharedPreferenceの設定
		SharedPreferences.Editor editor = sharedPreferences.edit();
		int vCode = sharedPreferences.getInt("VersionCode", 1);
		String vName = sharedPreferences.getString("VersionName", "1.0");

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

	// ON/OFFボタン
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		mChecked = isChecked;
		if (isChecked) {
			NotificationServiceStop();
			NotificationServiceSet();
			NotificationServiceStart();
		} else {
			NotificationServiceStop();
		}

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("notification_switch", isChecked);
		editor.commit();
	}

	// Add RSS Feedボタン
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.button1) {
			Intent intent = new Intent(getActivity(),
					(Class<?>) EntryActivity.class);
			intent.putExtra("ADD", "add");
			startActivity(intent);
			getActivity().finish();
		}

	}

	public void frequencySet(int position) {
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

		Intent intent = new Intent(getActivity(), NotificationService.class);
		PendingIntent pendingIntent = PendingIntent.getService(getActivity(),
				-1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) getActivity()
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setInexactRepeating(AlarmManager.RTC,
				System.currentTimeMillis(), time, pendingIntent);
	}

	// NotificationServiceのAlarmManager登録解除
	protected void NotificationServiceStop() {
		getActivity().stopService(
				new Intent(getActivity(), NotificationService.class));
		Intent intent = new Intent(getActivity(), NotificationService.class);
		PendingIntent pendingIntent = PendingIntent.getService(getActivity(),
				-1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) getActivity()
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);

		RssMessageNotification.cancel(getActivity(), 100);
	}

}
