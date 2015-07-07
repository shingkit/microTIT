package com.gyxy.sns.ui.activity;

import android.os.Bundle;
import android.util.Log;

import com.gyxy.sns.R;
import com.gyxy.sns.ui.fragment.SettingsFragment;

public class SettingsActivity extends SwipeBackActivity {

	private static final String TAG = "SettingsActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "oncreate");
		setContentView(R.layout.activity_settings);
		// …Ë÷√±ÍÃ‚
		getToolbar().setTitle(R.string.settings);

		getFragmentManager().beginTransaction()
				.replace(R.id.fl_container, new SettingsFragment()).commit();
	}

}
