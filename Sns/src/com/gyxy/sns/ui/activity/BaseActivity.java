package com.gyxy.sns.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;

import com.gyxy.sns.Constants;
import com.gyxy.sns.R;
import com.gyxy.sns.utils.CompatUtils;
import com.gyxy.sns.utils.PrefUtils;

@SuppressLint("NewApi")
public class BaseActivity extends ActionBarActivity implements
		OnSharedPreferenceChangeListener {
	protected Toolbar toolBar;

	protected DrawerLayout mDrawerLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		PrefUtils.registerOnSharedPreferenceChangeListener(this, this);

		ActionBar ab = getSupportActionBar();
		if (ab != null) {
			ab.setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		int color = PrefUtils.getColor(getApplicationContext());
		setThemeColor(color);
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		getToolbar();
		setupNavDrawer();
	}

	private void setupNavDrawer() {
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (mDrawerLayout != null) {

			mDrawerLayout.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					sendBroadcast(new Intent(getPackageName()
							+ Constants.dismissPopup));
					Log.i("baseactivity", "onFocusChange，发送广播");

				}
			});
			mDrawerLayout.openDrawer(Gravity.START);
		}

		if (toolBar != null) {
			toolBar.setNavigationIcon(R.drawable.ic_drawer);
			toolBar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					mDrawerLayout.openDrawer(Gravity.START);
				}
			});
		}
	}

	protected Toolbar getToolbar() {
		toolBar = (Toolbar) findViewById(R.id.toolbar);
		if (toolBar != null) {
			setSupportActionBar(toolBar);
		}
		return toolBar;
	}

	@Override
	protected void onDestroy() {
		PrefUtils.unregisterOnSharedPreferenceChangeListener(this, this);
		super.onDestroy();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (PrefUtils.PREF_THEME_COLOR.equals(key)) {
			int color = PrefUtils.getColor(getApplicationContext());
			setThemeColor(color);

		}
	}

	/**
	 * 初始化主题颜色
	 */
	protected void setThemeColor(int color) {
		if (toolBar != null) {
			toolBar.setBackgroundColor(color);
		}
		if (CompatUtils.isL()) {
			getWindow().setNavigationBarColor(color);
		}

		if (mDrawerLayout != null) {
			mDrawerLayout.setStatusBarBackgroundColor(color);
		} else {
			if (CompatUtils.isL()) {
				getWindow().setStatusBarColor(color);
			}
		}
	}

}
