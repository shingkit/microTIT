package com.gyxy.sns.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.gyxy.sns.R;

public class AboutActivity extends SwipeBackActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		getToolbar().setTitle(R.string.about);
	}

	public void send_email(View v) {

	}
}
