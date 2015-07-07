package com.gyxy.sns.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.gyxy.sns.R;
import com.gyxy.sns.ui.fragment.PanelManageFragment;
import com.gyxy.sns.ui.fragment.PushManageFragment;
import com.gyxy.sns.ui.fragment.UserManageFragment;
import com.gyxy.sns.utils.BmobUtils;

/**
 * 管理界面
 * 
 * @author sj
 * 
 */
public class ManageActivity extends SwipeBackActivity {

	private UserManageFragment userManageFragment;
	private PanelManageFragment panelManageFragment;
	private PushManageFragment pushManageFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manager);
		getToolbar().setTitle("");
		getSupportActionBar().setDisplayShowTitleEnabled(true);

		userManageFragment = new UserManageFragment();
		panelManageFragment = new PanelManageFragment();
		pushManageFragment = new PushManageFragment();
		init();

	}

	private void init() {
		Spinner spinner = (Spinner) findViewById(R.id.spinner);

		String[] items = null;
		if (BmobUtils.isSuperAdmin(getApplicationContext())) {
			items = new String[] { "用户管理", "版块管理", "推送管理" };
		} else {
			items = new String[] { "用户管理" };
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, items);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				switch (position) {
				case 0:
					getSupportFragmentManager().beginTransaction()
							.replace(R.id.framelayout, userManageFragment)
							.commit();
					break;
				case 1:
					getSupportFragmentManager().beginTransaction()
							.replace(R.id.framelayout, panelManageFragment)
							.commit();
					break;
				case 2:
					getSupportFragmentManager().beginTransaction()
							.replace(R.id.framelayout, pushManageFragment)
							.commit();
					break;
				default:
					break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}

		});

	}

}
