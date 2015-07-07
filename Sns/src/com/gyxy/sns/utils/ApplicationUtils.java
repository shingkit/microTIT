package com.gyxy.sns.utils;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

public class ApplicationUtils {
	public static String getVersion(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
