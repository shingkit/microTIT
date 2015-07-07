package com.gyxy.sns.ui.fragment;

import android.support.v4.app.Fragment;

import com.gyxy.sns.utils.PrefUtils;

public class BaseFragment extends Fragment {

	@Override
	public void onStart() {
		super.onStart();
		int color = PrefUtils.getColor(getActivity());
		setThemeColor(color);
	}

	protected void setThemeColor(int color) {

	}
}
