package com.gyxy.sns.ui.activity;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import cn.bmob.im.BmobChat;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobUser;

import com.gyxy.sns.Constants;
import com.gyxy.sns.R;
import com.gyxy.sns.ui.fragment.FindPwdFragment;
import com.gyxy.sns.ui.fragment.LoginFragment;
import com.gyxy.sns.ui.fragment.RegisterFragment;

/**
 * 应用程序首界面，展示APP名字和太工徽标
 * 
 * @author sj
 */
public class SplashActivity extends FragmentActivity {

	protected static final int BMOB_USER_DONT_EXIST = 0;
	protected static final int BMOB_USER_EXIST = 1;
	private FrameLayout rl_container;
	private FragmentManager fm;
	private LoginFragment loginFragment;
	private RegisterFragment registerFragment;
	private FindPwdFragment findPwdFragment;
	private TextView tv_find_pwd;
	private TextView tv_register;
	private TextView tv_app_label;

	@SuppressLint("HandlerLeak")
	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			BmobUser user = BmobUser.getCurrentUser(getApplicationContext());
			if (user != null) {
				// 打开主界面
				Intent intent = new Intent(SplashActivity.this,
						MainActivity.class);
				startActivity(intent);
				Log.i("TAG", "user" + user.getUsername());
				SplashActivity.this.finish();
			} else {
				showContainerLayout();
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		initBmob();

		initThread();
		fm = getSupportFragmentManager();
		loginFragment = new LoginFragment();
		registerFragment = new RegisterFragment();
		findPwdFragment = new FindPwdFragment();

	}

	private void initBmob() {
		// Bmob.initialize(this, Constants.app_id);
		// 使用推送服务时的初始化操作
		// BmobPush.startWork(this, Constants.app_id);
		BmobChat.DEBUG_MODE = true;
		BmobChat.getInstance(getApplicationContext()).init(Constants.app_id);
		BmobInstallation.getCurrentInstallation(this).save();
	}

	private void initThread() {
		new Thread() {
			public void run() {
				try {
					Thread.sleep(500);
					handler.sendEmptyMessage(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		}.start();
	}

	public void showContainerLayout() {
		// 显示登录linearlayout
		rl_container = (FrameLayout) findViewById(R.id.fl_container);
		tv_find_pwd = (TextView) findViewById(R.id.tv_find_pwd);
		tv_register = (TextView) findViewById(R.id.tv_register);
		tv_app_label = (TextView) findViewById(R.id.tv_app_label);

		rl_container.setVisibility(View.VISIBLE);
		tv_find_pwd.setVisibility(View.VISIBLE);
		tv_register.setVisibility(View.VISIBLE);

		// 默认打开登陆
		fm.beginTransaction().replace(R.id.fl_container, loginFragment)
				.commit();
	}

	public void show_login_Fragment(View v) {
		fm.beginTransaction().replace(R.id.fl_container, loginFragment)
				.commit();

		tv_app_label.setText(R.string.app_name);
	}

	public void show_register_Fragment(View v) {
		fm.beginTransaction().replace(R.id.fl_container, registerFragment)
				.commit();

		tv_app_label.setText(R.string.login);
	}

	public void show_find_pwd_Fragment(View v) {
		fm.beginTransaction().replace(R.id.fl_container, findPwdFragment)
				.commit();

		tv_app_label.setText(R.string.login);
	}

}
