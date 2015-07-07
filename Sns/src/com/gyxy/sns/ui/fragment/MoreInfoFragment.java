package com.gyxy.sns.ui.fragment;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.gyxy.sns.Constants;
import com.gyxy.sns.R;
import com.gyxy.sns.adapter.PostAdapter;
import com.gyxy.sns.model.Post;
import com.gyxy.sns.model.User;
import com.gyxy.sns.utils.ToastUtils;

public class MoreInfoFragment extends Fragment {

	private ListView listview;
	private User user;
	private List<Post> list;
	private TextView tv_empty;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		user = (User) getActivity().getIntent().getSerializableExtra(
				Constants.user);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_more_info, container,
				false);
		init(view);
		return view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		fetchData();
	}

	private void init(View view) {
		listview = (ListView) view.findViewById(R.id.listview);
		tv_empty = (TextView) view.findViewById(R.id.tv_empty);
	}

	private void fetchData() {
		BmobQuery<Post> query = new BmobQuery<Post>();
		query.addWhereEqualTo(Constants.user, user);
		query.include("user");
		query.findObjects(getActivity(), new FindListener<Post>() {

			@Override
			public void onSuccess(List<Post> posts) {
				if (posts != null) {
					if (posts.size() > 0) {
						list = posts;
						PostAdapter adapter = new PostAdapter(getActivity(),
								list);
						listview.setAdapter(adapter);
					} else {
						tv_empty.setText("这个家伙不积极，最近没发言...");
					}
				}
			}

			@Override
			public void onError(int code, String msg) {
				ToastUtils.makeErrorText(getActivity(), code, msg);
			}
		});
	}

}
