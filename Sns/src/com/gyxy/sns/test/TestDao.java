package com.gyxy.sns.test;

import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import android.test.AndroidTestCase;

import com.gyxy.sns.MyApplication;
import com.gyxy.sns.db.dao.PushDao;
import com.gyxy.sns.model.Push;
import com.gyxy.sns.utils.LogUtils;
public class TestDao extends AndroidTestCase{
	
	private static final String TAG = "TestDao";

	@Test
	public void testInsert(){
		PushDao dao=new PushDao(MyApplication.getInstance());
		Push push=new Push("title 1","content 1");
		boolean add = dao.add(push);
		Assert.assertEquals(add, true);
	}
	
	public void testQuery(){
		PushDao dao=new PushDao(getContext());
		List<Push> queryAll = dao.queryAll();
		Iterator<Push>iterator=queryAll.iterator();
		while(iterator.hasNext()){
			Push push = iterator.next();
			LogUtils.i(TAG, push.getTitle()+"..."+push.getContent());
		}
	}
}
