package com.gyxy.sns.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
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
import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.listener.SaveListener;

import com.gyxy.sns.R;
import com.gyxy.sns.model.User;
import com.gyxy.sns.ui.activity.MainActivity;
import com.gyxy.sns.utils.ToastUtils;

public class LoginFragment extends Fragment implements OnClickListener {
	protected static final String TAG = "LoginFragment";
	private EditText et_username;
	private EditText et_pwd;
	private Button btn_login;

	private Context mContext;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();

		View rootView = inflater.inflate(R.layout.fragment_login, container,
				false);
		et_username = (EditText) rootView.findViewById(R.id.et_username);
		et_pwd = (EditText) rootView.findViewById(R.id.et_pwd);
		btn_login = (Button) rootView.findViewById(R.id.btn_login);
		btn_login.setOnClickListener(this);
		return rootView;
	}

	@Override
	public void onClick(View v) {
		// login
		User user = new User();
		String username = et_username.getText().toString();
		String password = et_pwd.getText().toString();

		if (TextUtils.isEmpty(username)) {
			ToastUtils.makeText(mContext, "«Î ‰»Î”√ªß√˚£°");
			return;
		}
		if (TextUtils.isEmpty(password)) {
			ToastUtils.makeText(mContext, "«Î ‰»Î√‹¬Î£°");
			return;
		}

		user.setUsername(username);
		user.setPassword(password);
		BmobUserManager usermanager = BmobUserManager
				.getInstance(getActivity());
		usermanager.login(user, new SaveListener() {

			@Override
			public void onSuccess() {
				ToastUtils.makeText(mContext, "µ«¬Ω≥…π¶£°");
				Intent intent = new Intent(mContext, MainActivity.class);
				startActivity(intent);
				getActivity().finish();
			}

			@Override
			public void onFailure(int code, String arg1) {
				ToastUtils.makeText(mContext, "µ«¬Ω ß∞‹!¥ÌŒÛ¬ÎŒ™" + code);
				Log.i(TAG, "µ«¬Ω ß∞‹,code:" + code + "¥ÌŒÛ–≈œ¢:" + arg1);
			}
		});

	}
}
