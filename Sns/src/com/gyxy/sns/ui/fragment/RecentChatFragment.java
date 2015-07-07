package com.gyxy.sns.ui.fragment;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;

import com.gyxy.sns.Constants;
import com.gyxy.sns.R;
import com.gyxy.sns.adapter.MessageRecentAdapter;
import com.gyxy.sns.model.User;
import com.gyxy.sns.ui.activity.ChatActivity;
import com.gyxy.sns.utils.ToastUtils;

public class RecentChatFragment extends BaseFragment implements
		OnItemClickListener {

	private MessageRecentAdapter adapter;
	private ListView listview;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_recent_chat, container,
				false);
		initView(view);
		return view;
	}

	private void initView(View view) {
		listview = (ListView) view.findViewById(R.id.listview);
		adapter = new MessageRecentAdapter(getActivity(),
				R.layout.item_conversation, fetchRecentChat());
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(this);
	}

	private List<BmobRecent> fetchRecentChat() {
		return BmobDB.create(getActivity()).queryRecents();

	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		BmobRecent recent = (BmobRecent) adapterView
				.getItemAtPosition(position);
		String targetid = recent.getTargetid();
		BmobQuery<User> query = new BmobQuery<User>();
		query.addWhereEqualTo("objectId", targetid);
		query.getObject(getActivity(), targetid, new GetListener<User>() {

			@Override
			public void onSuccess(User arg0) {
				Intent intent = new Intent(getActivity(), ChatActivity.class);
				intent.putExtra(Constants.user, arg0);
				startActivity(intent);
			}

			@Override
			public void onFailure(int code, String msg) {
				ToastUtils.makeErrorText(getActivity(), code, msg);
			}
		});
	}

}
