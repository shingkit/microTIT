package com.gyxy.sns.utils;

import android.util.Log;

public class LogUtils {

	private static final boolean isDebug = true;

	public static void i(String tag, String msg) {
		if (isDebug) {
			Log.i(tag, msg);
		}
	}

	public static void E(String tag, int code, String msg) {
		if (isDebug) {
			Log.e(tag, "´íÎóÂëÎª:" + code + "´íÎóĞÅÏ¢£º" + msg);
		}
	}
	

	public static void E(String tag,String msg) {
		if (isDebug) {
			Log.e(tag, msg);
		}
	}
}
