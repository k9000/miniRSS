package com.tlulybluemonochrome.minimarurss;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
		CompoundButton.OnCheckedChangeListener,OnClickListener {

	Switch s;
	SeekBar seekBar;
	EditText editText;
	RadioGroup mRadioGroupOs;
	boolean mChecked;
	long mMinute = 600000;
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

		mChecked = sharedPreferences.getBoolean("notification_switch", false);
		s = (Switch) rootView.findViewById(R.id.switch1);
		s.setChecked(mChecked);
		s.setOnCheckedChangeListener(this);

		seekBar = (SeekBar) rootView.findViewById(R.id.seekBar1);
		seekBar.setMax(59);
		seekBar.setProgress(sharedPreferences.getInt("notification_freqescy",
				10) - 1);

		editText = (EditText) rootView.findViewById(R.id.editText1);

		// シークバーの初期値をTextViewに表示
		editText.setText(String.valueOf(seekBar.getProgress() + 1));

		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// ツマミをドラッグしたときに呼ばれる
				editText.setText(String.valueOf(progress + 1));
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// ツマミに触れたときに呼ばれる
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				// ツマミを離したときに呼ばれる

				if (mChecked) {
					mMinute = (seekBar.getProgress() + 1) * 60000;
					// NotificationServiceStop();
					// NotificationServiceStart();
				}

				SharedPreferences sharedPreferences = PreferenceManager
						.getDefaultSharedPreferences(getActivity());
				Editor editor = sharedPreferences.edit();
				editor.putInt("notification_freqescy",
						seekBar.getProgress() + 1);
				editor.commit();
			}
		});

		mRadioGroupOs = (RadioGroup) rootView.findViewById(R.id.radioGroup1);

		String theme_preference = sharedPreferences.getString(
				"theme_preference", "Light");
		if (theme_preference.equals("Light"))
			mRadioGroupOs.check(R.id.radio0);
		else if (theme_preference.equals("Dark"))
			mRadioGroupOs.check(R.id.radio1);
		else if (theme_preference.equals("Transparent"))
			mRadioGroupOs.check(R.id.radio2);

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
		
		button = (Button)rootView.findViewById(R.id.button1);
		button.setOnClickListener(this);

		// Inflate the layout for this fragment
		return rootView;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		mChecked = isChecked;
		if (isChecked) {
			// NotificationServiceStop();
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
	
	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.button1){
			Intent intent = new Intent(getActivity(), (Class<?>) EntryActivity.class);
			intent.putExtra("ADD", "add");
			startActivity(intent);
			getActivity().finish();
		}
		
	}
	

	protected void NotificationServiceStart() {

		Intent intent = new Intent(getActivity(), NotificationService.class);
		PendingIntent pendingIntent = PendingIntent.getService(getActivity(),
				-1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) getActivity()
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setInexactRepeating(AlarmManager.RTC,
				System.currentTimeMillis(), mMinute, pendingIntent);
	}

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
