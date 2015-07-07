package com.gyxy.sns.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gyxy.sns.db.PushDBHelper;
import com.gyxy.sns.model.Push;

public class PushDao {

	private PushDBHelper helper;
	
	public PushDao(Context context){
		helper=new PushDBHelper(context);
	}
	/**
	 * 添加推送数据到数据库
	 * @param push
	 * @return
	 */
	public boolean add(Push push){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("title", push.getTitle());
		values.put("content", push.getContent());
		long insert = db.insert(PushDBHelper.TB_NAME, null, values);
		db.close();
		if(insert!=-1){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 添加推送数据到数据库
	 * @param title
	 * @param content
	 * @return
	 */
	public boolean add(String title,String content){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("title", title);
		values.put("content", content);
		long insert = db.insert(PushDBHelper.TB_NAME, null, values);
		db.close();
		if(insert!=-1){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 查询所有的推送消息
	 * @return
	 */
	public List<Push> queryAll(){
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(PushDBHelper.TB_NAME,new String[]{"title","content"}, null, null, null, null, null);
		List<Push>list=new ArrayList<Push>();
		if(cursor.moveToNext()){
			String title = cursor.getString(0);
			String content=cursor.getString(1);
			list.add(new Push(title,content));
		}
		db.close();
		return list;
	}
}
