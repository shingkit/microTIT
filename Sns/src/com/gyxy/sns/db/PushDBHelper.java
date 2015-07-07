package com.gyxy.sns.db;

import com.gyxy.sns.utils.LogUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PushDBHelper extends SQLiteOpenHelper {
	private static final String DB_NAME="sns.db";
	private static final int DB_VERSION=1;
	public static final String TB_NAME="push";
	private static final String TAG = "PushDBHelper";
	private String SQL_CREATE_TABLE=
			"CREATE TABLE " + TB_NAME + " (" +
				    "_id INTEGER PRIMARY KEY," +
				    "title TEXT,"+ 
				    "content TEXT )";

	public PushDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_TABLE);
		LogUtils.i(TAG, SQL_CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		 
	}

}
