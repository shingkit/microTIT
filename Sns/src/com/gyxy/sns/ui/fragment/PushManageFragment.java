package com.gyxy.sns.ui.fragment;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.listener.PushListener;

import com.gyxy.sns.R;
import com.gyxy.sns.utils.ToastUtils;

public class PushManageFragment extends Fragment implements OnClickListener {

	private EditText et_title;
	private EditText et_content;
	private Button btn_push;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_push_manage, container,
				false);
		init(rootView);

		return rootView;
	}

	private void init(View rootView) {
		et_title = (EditText) rootView.findViewById(R.id.et_title);
		et_content = (EditText) rootView.findViewById(R.id.et_content);
		btn_push = (Button) rootView.findViewById(R.id.btn_push);
		btn_push.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == btn_push) {
			// 推送
			String title = et_title.getText().toString().trim();
			String content = et_content.getText().toString().trim();

			if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
				ToastUtils.makeText(getActivity(), "标题和内容不能为空！");
				return;
			}
			BmobPushManager<String> bmobPush = new BmobPushManager<String>(
					getActivity());
			JSONObject json = new JSONObject();
			try {
				json.put("title", title);
				json.put("content", content);
				bmobPush.pushMessageAll(json, new PushListener() {
					@Override
					public void onSuccess() {
						ToastUtils.makeText(getActivity(), "推送成功");
					}

					@Override
					public void onFailure(int code, String msg) {
						ToastUtils.makeErrorText(getActivity(), code, msg);
					}
				});
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}
}
