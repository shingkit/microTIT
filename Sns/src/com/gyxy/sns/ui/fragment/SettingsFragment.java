package com.gyxy.sns.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.gc.materialdesign.widgets.ColorSelector;
import com.gc.materialdesign.widgets.ColorSelector.OnColorSelectedListener;
import com.gyxy.sns.R;
import com.gyxy.sns.ui.activity.AboutActivity;
import com.gyxy.sns.utils.ApplicationUtils;
import com.gyxy.sns.utils.PrefUtils;

public class SettingsFragment extends PreferenceFragment implements
		OnPreferenceClickListener {

	private ColorSelector colorSelector;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);

		init();

	}

	private void init() {
		PreferenceManager pm = getPreferenceManager();
		// 版本号
		pm.findPreference("version").setSummary(
				ApplicationUtils.getVersion(getActivity()));
		// 关于
		Intent aboutIntent = new Intent(getActivity(), AboutActivity.class);
		pm.findPreference("about").setIntent(aboutIntent);

		// 主题

		pm.findPreference("color").setOnPreferenceClickListener(this);

		colorSelector = new ColorSelector(getActivity(), R.color.BLUE_TIT,
				new OnColorSelectedListener() {

					@Override
					public void onColorSelected(int color) {
						PrefUtils.setColor(getActivity(), color);
					}
				});

	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		Log.i(getTag(), "onPreference");
		switch (preference.getKey()) {
		case PrefUtils.PREF_THEME_COLOR:
			showDialog();
			break;
		case "notify":
			break;
		default:
			break;
		}
		return true;
	}

	private void showDialog() {

		colorSelector.show();
	}

}
