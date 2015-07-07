package com.gyxy.sns.ui.fragment;

import java.lang.ref.WeakReference;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

import com.gyxy.sns.R;
import com.gyxy.sns.db.dao.PushDao;
import com.gyxy.sns.model.Push;
import com.gyxy.sns.adapter.*;

public class PushFragment extends ListFragment {
	private static class MyHandler extends Handler {
		WeakReference<PushFragment> reference;

		public MyHandler(PushFragment fragment) {
			this.reference = new WeakReference<PushFragment>(fragment);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			PushFragment fragment = reference.get();
			switch (msg.what) {
			case FETCH_DATA_SUCCESS:
				PushAdapter adapter = new PushAdapter(fragment.getActivity(),
						 fragment.list);
				fragment.setListAdapter(adapter);
				break;
			case FETCH_DATA_FAILED_OR_NULL:
				//fragment.setEmptyText("没有消息");
				break;
			default:
				break;
			}
		}
	}

	private static List<Push> list;
	protected static final int FETCH_DATA_SUCCESS = 100;
	protected static final int FETCH_DATA_FAILED_OR_NULL = 101;

	private final MyHandler handler = new MyHandler(this);

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_push, container,
				false);
		initData();
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		initData();
	}
	
	private void initData() {
		new Thread() {

			public void run() {
				PushDao dao = new PushDao(getActivity());
				list = dao.queryAll();
				if (list != null && list.size() > 0) {
					handler.sendEmptyMessage(FETCH_DATA_SUCCESS);
				} else {
					handler.sendEmptyMessage(FETCH_DATA_FAILED_OR_NULL);
				}
			};
		}.start();

	}

	 
}
