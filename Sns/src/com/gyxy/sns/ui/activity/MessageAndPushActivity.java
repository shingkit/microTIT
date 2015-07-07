package com.gyxy.sns.ui.activity;

import com.gyxy.sns.R;
import com.gyxy.sns.ui.fragment.AuthorizationFragment;
import com.gyxy.sns.ui.fragment.PushFragment;
import com.gyxy.sns.ui.fragment.RecentChatFragment;
import com.gyxy.sns.ui.widget.LineTabIndicator;
import com.gyxy.sns.utils.BmobUtils;
import com.gyxy.sns.utils.LogUtils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class MessageAndPushActivity extends SwipeBackActivity {

	private RecentChatFragment recentChatFragment;
	private PushFragment pushFragment;
	private LineTabIndicator indicator;
	private ViewPager viewpager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_and_push);
		getToolbar().setTitle("消息通知");
		recentChatFragment = new RecentChatFragment();
		pushFragment = new PushFragment();

		indicator = (LineTabIndicator) findViewById(R.id.tabindicator);
		viewpager = (ViewPager) findViewById(R.id.viewpager);
		MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(
				getSupportFragmentManager());
		viewpager.setAdapter(adapter);

	}

	@Override
	protected void onStart() {
		super.onStart();
		indicator.setViewPager(viewpager);
	}

	private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

		private static final String TAG = "MyFragmentPagerAdapter";
		private String[] tab_titles = { "聊天", "推送" };

		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			LogUtils.i(TAG, "postion" + position);
			if (position == 0) {
				if (recentChatFragment != null)
					return recentChatFragment;
				else
					return new RecentChatFragment();
			}
			if (position == 1) {
				if (pushFragment != null)
					return pushFragment;
				else
					return new PushFragment();
			}

			return null;
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return tab_titles[position];
		}

	}

	@Override
	protected void setThemeColor(int color) {
		super.setThemeColor(color);
		indicator.setColor(color);
	}
}
