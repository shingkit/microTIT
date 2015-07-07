package com.gyxy.sns.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gyxy.sns.R;
import com.gyxy.sns.ui.widget.LineTabIndicator;
import com.gyxy.sns.utils.BmobUtils;
import com.gyxy.sns.utils.LogUtils;

/**
 * 用户管理fragment
 * 
 * @author sj
 * 
 */
public class UserManageFragment extends Fragment {

	public static final String TAG = "UserManageFragment";
	private ManagerFragment managerFragment;
	private AuthorizationFragment authorizationFragment;
	private boolean FLAG_IS_SUPER_ADMIN;
	private List<Fragment> fragments = new ArrayList<Fragment>();
	private AdminApplyManageFragment adminApplyManageFragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_user_manage,
				container, false);
		initviews(rootView);
		return rootView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FLAG_IS_SUPER_ADMIN = BmobUtils.isSuperAdmin(getActivity());
		authorizationFragment = new AuthorizationFragment();
		adminApplyManageFragment = new AdminApplyManageFragment();
		managerFragment = new ManagerFragment();

		fragments.add(authorizationFragment);
		fragments.add(adminApplyManageFragment);
		fragments.add(managerFragment);
	}

	private void initviews(View rootView) {
		LineTabIndicator indicator = (LineTabIndicator) rootView
				.findViewById(R.id.tabindicator);
		ViewPager viewpager = (ViewPager) rootView.findViewById(R.id.viewpager);
		viewpager.setAdapter(new MyFragmentPagerAdapter(getActivity()
				.getSupportFragmentManager()));
		indicator.setViewPager(viewpager);
	}

	private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

		private String[] tab_titles = { "实名认证", "管理员申请", "管理员" };

		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			LogUtils.i(TAG, "postion" + position);
			return fragments.get(position);
		}

		@Override
		public int getCount() {
			if (FLAG_IS_SUPER_ADMIN) {
				return 3;
			} else {
				return 1;
			}
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return tab_titles[position];
		}

	}

}
