package com.gyxy.sns.utils;

import android.content.Context;
import cn.bmob.v3.BmobUser;

import com.gyxy.sns.Constants;
import com.gyxy.sns.model.MoreInfo;
import com.gyxy.sns.model.User;

public class BmobUtils {
	/**
	 * 判断当前用户是否为超级管理员
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isSuperAdmin(Context context) {
		if (MoreInfo.SUPER_ADMIN.equals(Constants.userRole)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断当前用户是否为超级管理员
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isAdmin(Context context) {
		if (MoreInfo.SUPER_ADMIN.equals(Constants.userRole)
				|| MoreInfo.ADMIN.equals(Constants.userRole)) {
			return true;
		}
		return false;
	}
	
	

}
