package com.gyxy.sns.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.bmob.v3.BmobUser;

import com.gyxy.sns.Constants;
import com.gyxy.sns.R;
import com.gyxy.sns.model.User;
import com.gyxy.sns.ui.fragment.BasicInfoFragment;
import com.gyxy.sns.ui.fragment.MoreInfoFragment;
import com.gyxy.sns.utils.BmobUtils;

/**
 * 个人信息界面
 * 
 * @author sj
 * 
 */
public class PersonInfoActivity extends SwipeBackActivity implements
		OnClickListener {
	private ViewPager vp_personalinfo;
	private BasicInfoFragment basicInfoFragment;
	private MoreInfoFragment moreInfoFragment;
	private TextView tv_personalinfo_tab_basic;
	private TextView tv_personalinfo_tab_more;
	private LinearLayout ll_tab;
	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personalinfo);

		initView();
		initEvent();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// 如果传入的参数不是当前用户，显示私信图标
		if (!BmobUser.getCurrentUser(this).getObjectId()
				.equals(user.getObjectId()))
			getMenuInflater().inflate(R.menu.menu_personalinfo, menu);
//		else {
//			if (!BmobUtils.isAdmin(getApplicationContext())) {
//				// 申请管理员
//				MenuItem reqAdminItem = menu.add(0, R.id.menu_req_admin, 0,
//						R.string.menu_req_manager);
//				MenuItemCompat.setShowAsAction(reqAdminItem,
//						MenuItem.SHOW_AS_ACTION_NEVER);
//			}
//		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.menu_item_personalinfo_msg:
			// 打开私信界面
			Intent intent = new Intent(this, ChatActivity.class);
			intent.putExtra(Constants.user, user);
			startActivity(intent);

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void initEvent() {
		tv_personalinfo_tab_basic.setOnClickListener(this);
		tv_personalinfo_tab_more.setOnClickListener(this);

		vp_personalinfo.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				switch (position) {
				case 0:
					tv_personalinfo_tab_basic.setTextColor(Color.WHITE);
					tv_personalinfo_tab_more.setTextColor(getResources()
							.getColor(R.color.gray));
					break;
				case 1:
					tv_personalinfo_tab_more.setTextColor(Color.WHITE);
					tv_personalinfo_tab_basic.setTextColor(getResources()
							.getColor(R.color.gray));
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

	}

	private void initView() {
		user = (User) getIntent().getSerializableExtra(Constants.user);

		getToolbar().setTitle(user.getUsername());
		ll_tab = (LinearLayout) findViewById(R.id.ll_tab);
		vp_personalinfo = (ViewPager) findViewById(R.id.vp_personalinfo);
		tv_personalinfo_tab_basic = (TextView) findViewById(R.id.tv_personalinfo_tab_basic);
		tv_personalinfo_tab_more = (TextView) findViewById(R.id.tv_personalinfo_tab_more);
		// fragment
		basicInfoFragment = new BasicInfoFragment();
		moreInfoFragment = new MoreInfoFragment();
		// viewpager
		vp_personalinfo.setAdapter(new FragmentPagerAdapter(
				getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return 2;
			}

			@Override
			public Fragment getItem(int position) {
				switch (position) {
				case 0:
					return basicInfoFragment;
				case 1:
					return moreInfoFragment;
				default:
					return null;
				}

			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_personalinfo_tab_basic:
			tv_personalinfo_tab_basic.setTextColor(Color.WHITE);
			tv_personalinfo_tab_more.setTextColor(getResources().getColor(
					R.color.gray));
			vp_personalinfo.setCurrentItem(0);
			break;
		case R.id.tv_personalinfo_tab_more:
			tv_personalinfo_tab_more.setTextColor(Color.WHITE);
			tv_personalinfo_tab_basic.setTextColor(getResources().getColor(
					R.color.gray));
			vp_personalinfo.setCurrentItem(1);
			break;
		default:
			break;
		}
	}

	@Override
	protected void setThemeColor(int color) {
		super.setThemeColor(color);
		ll_tab.setBackgroundColor(color);
	}
}
