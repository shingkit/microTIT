package com.gyxy.sns.utils;

import com.gyxy.sns.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefUtils {

	public final static String PREF_THEME_COLOR = "color";

	public static int getColor(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getInt(
				PREF_THEME_COLOR, R.color.BLUE_TIT);
	}

	public static boolean isSoundOpen(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean("sound", true);
	}

	public static boolean isViberateOpen(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean("viberate", true);
	}

	public static void setColor(Context context, int color) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putInt("color", color).commit();
	}

	/**
	 * ×¢²á¼àÌýÆ÷
	 * 
	 * @param context
	 * @param listener
	 */
	public static void registerOnSharedPreferenceChangeListener(
			final Context context,
			SharedPreferences.OnSharedPreferenceChangeListener listener) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.registerOnSharedPreferenceChangeListener(listener);
	}

	public static void unregisterOnSharedPreferenceChangeListener(
			final Context context,
			SharedPreferences.OnSharedPreferenceChangeListener listener) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.unregisterOnSharedPreferenceChangeListener(listener);
	}

}
