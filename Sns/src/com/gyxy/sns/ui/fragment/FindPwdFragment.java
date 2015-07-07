package com.gyxy.sns.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.ResetPasswordListener;
import cn.bmob.v3.listener.SaveListener;

import com.gyxy.sns.R;
import com.gyxy.sns.model.User;
import com.gyxy.sns.ui.activity.MainActivity;
import com.gyxy.sns.utils.ToastUtils;

public class FindPwdFragment extends Fragment implements OnClickListener {
	protected static final String TAG = "LoginFragment";
	private EditText et_email;
	private Button btn_find_pwd;

	private Context mContext;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();

		View rootView = inflater.inflate(R.layout.fragment_find_pwd, container,
				false);
		et_email = (EditText) rootView.findViewById(R.id.et_email);
		btn_find_pwd = (Button) rootView.findViewById(R.id.btn_find_pwd);
		btn_find_pwd.setOnClickListener(this);
		return rootView;
	}

	@Override
	public void onClick(View v) {
		String email = et_email.getText().toString();
		if (TextUtils.isEmpty(email)) {
			ToastUtils.makeText(mContext, "请输入注册时输入的验证邮箱");
			return;
		}

		BmobUser.resetPassword(mContext, email, new ResetPasswordListener() {

			@Override
			public void onSuccess() {
				ToastUtils.makeText(mContext, "请前往邮箱查看重置邮件！");
				getActivity().finish();
			}

			@Override
			public void onFailure(int code, String msg) {
				ToastUtils.makeText(mContext, "登陆失败!错误码为" + code);
				Log.i(TAG, "登陆失败,code:" + code + "错误信息:" + msg);
			}
		});

	}
}
