package com.gyxy.sns.utils;

import android.content.Context;
import android.net.ConnectivityManager;

public class NetWorkUtils {
	public static boolean isNetAvailable(Context context) {

		boolean netStat = false;
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (cm.getActiveNetworkInfo() != null) {
			netStat = cm.getActiveNetworkInfo().isAvailable();
		}
		return netStat;
	}
}
