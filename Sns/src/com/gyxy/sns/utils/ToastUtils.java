package com.gyxy.sns.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
	public static void makeText(Context context, String str) {
		Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
	}

	public static void makeErrorText(Context context, int code, String msg) {
		Toast.makeText(context, "����ʧ�ܣ�������:" + code + " ������Ϣ��" + msg,
				Toast.LENGTH_LONG).show();
	}
}
