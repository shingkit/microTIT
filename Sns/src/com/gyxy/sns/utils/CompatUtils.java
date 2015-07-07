package com.gyxy.sns.utils;

import android.content.Context;
import android.os.Build;

public class CompatUtils {
	/**
	 * �жϵ�ǰ�汾�Ƿ���ڻ����4.4
	 * 
	 * @return
	 */
	public static boolean isAboveKitkat() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
	}

	public static boolean isL() {
		return Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT;
	}

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * �����ֻ��ķֱ��ʴ� px(����) �ĵ�λ ת��Ϊ dp��
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f) - 15;
	}
}
