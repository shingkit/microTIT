package com.gyxy.sns.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

import com.gyxy.sns.Constants;
import com.gyxy.sns.MyApplication;
import com.gyxy.sns.R;
import com.gyxy.sns.model.MoreInfo;
import com.gyxy.sns.model.Panel;
import com.gyxy.sns.model.User;
import com.gyxy.sns.ui.fragment.PanelFragment;
import com.gyxy.sns.utils.BmobUtils;
import com.gyxy.sns.utils.LogUtils;
import com.gyxy.sns.utils.NetWorkUtils;
import com.gyxy.sns.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 主界面 drawer抽屉+fragment
 * 
 * @author sj
 * 
 */
public class MainActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "MainActivity";

	private static boolean has_panel_list = false;

	protected static final int FETCH_PANEL_SUCCESS = 1;
	protected static ListView lv_panel;
	private PanelAdapter panelAdapter;
	private RelativeLayout rl_settings;

	private Panel current_panel = null;

	private List<Panel> panels = new ArrayList<Panel>();
	private List<Panel> panels_preset = new ArrayList<Panel>();
	private List<Panel> panels_not_preset = new ArrayList<Panel>();

	private User currentUser;

	private LinearLayout ll_loading_tag;

	private Spinner spinner;

	private TextView tv_role;

	private int panel_position;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			if (msg.what == FETCH_PANEL_SUCCESS) {
				if (panelAdapter == null) {
					panelAdapter = new PanelAdapter();

					lv_panel.setAdapter(panelAdapter);
					LayoutAnimationController lac = new LayoutAnimationController(
							AnimationUtils.loadAnimation(MainActivity.this,
									R.anim.right_to_left));
					lac.setOrder(LayoutAnimationController.ORDER_REVERSE);
					lac.setInterpolator(getApplicationContext(),
							android.R.interpolator.decelerate_quad);
					lv_panel.setLayoutAnimation(lac);
				} else {
					panelAdapter.notifyDataSetChanged();
				}
				has_panel_list = true;
				spinner.setVisibility(View.VISIBLE);
				lv_panel.startLayoutAnimation();
				ll_loading_tag.setVisibility(View.INVISIBLE);

			}
		}
	};

	private RelativeLayout rl_manage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initSpinner();
		initPanel();

		initCurrentUserInfo();
		initSettingsAndManageInfo();

		new Thread() {
			public void run() {
				fetchPanels();
			};
		}.start();

		if (!NetWorkUtils.isNetAvailable(this)) {
			findViewById(R.id.tv_net_tag).setVisibility(View.VISIBLE);
		}

	}

	private void initSpinner() {
		spinner = (Spinner) findViewById(R.id.spinner);
		String[] array = new String[] { "全部", "精华" };
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, array);
		spinner.setAdapter(spinnerAdapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (position == 0)
					PanelFragment.fetchAll = true;
				if (position == 1)
					PanelFragment.fetchAll = false;
				LogUtils.i(TAG, "spinner pos " + position + " fetchAll"
						+ PanelFragment.fetchAll);
				if (has_panel_list)
					selectPanel(panel_position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}

		});
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		has_panel_list = false;
	}

	private void initPanel() {
		lv_panel = (ListView) findViewById(R.id.lv_panel);
		ll_loading_tag = (LinearLayout) findViewById(R.id.ll_loading_tag);
		ll_loading_tag.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		lv_panel.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lv_panel.setItemChecked(0, true);
		lv_panel.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				lv_panel.setItemChecked(position, true);
				if (position == panels_preset.size())
					return;
				if (position < panels_preset.size()) {
					selectPanel(position);
				} else if (position > panels_preset.size()) {
					selectPanel(position - 1);
				}
			}

		});

	}

	/**
	 * “设置”相对布局和管理布局
	 */
	private void initSettingsAndManageInfo() {
		rl_settings = (RelativeLayout) findViewById(R.id.rl_settings);
		rl_settings.setOnClickListener(this);
		rl_manage = (RelativeLayout) findViewById(R.id.rl_manage);
		rl_manage.setOnClickListener(this);
		if (currentUser != null && BmobUtils.isAdmin(this)) {
			rl_manage.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 个人信息布局 包括head和username role
	 */
	private void initCurrentUserInfo() {
		currentUser = BmobUser.getCurrentUser(getApplicationContext(),
				User.class);
		findViewById(R.id.fl_user_info).setOnClickListener(this);
		System.out.println(currentUser.toString());
		// findViews
		ImageView iv_head = (ImageView) findViewById(R.id.iv_head);
		TextView tv_username = (TextView) findViewById(R.id.tv_username);
		tv_role = (TextView) findViewById(R.id.tv_role);

		// 设置头像 用户名
		if (!TextUtils.isEmpty(currentUser.getAvatar())) {
			ImageLoader.getInstance().displayImage(currentUser.getAvatar(),
					iv_head, MyApplication.getHeadOptions());
		}
		tv_username.setText(currentUser.getUsername());
		getUserMoreInfo();
	}

	private void getUserMoreInfo() {
		BmobQuery<MoreInfo> query = new BmobQuery<MoreInfo>();
		query.addWhereEqualTo("user", currentUser);
		query.findObjects(getApplicationContext(),
				new FindListener<MoreInfo>() {
					@Override
					public void onError(int code, String msg) {
						ToastUtils.makeErrorText(getApplicationContext(), code,
								msg);
					}

					@Override
					public void onSuccess(List<MoreInfo> arg0) {
						if (arg0 != null && arg0.size() > 0) {
							MoreInfo moreInfo = arg0.get(0);
							Constants.moreInfoObjectId = moreInfo.getObjectId();
							Constants.userRole = moreInfo.getUserRole();
							Constants.vip_info = moreInfo.getVip_info();

							tv_role.setText(Constants.userRole);
							if (BmobUtils.isAdmin(getApplicationContext()))
								rl_manage.setVisibility(View.VISIBLE);
						}

					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.menu_item_main_msg:
			Intent intent = new Intent(this, MessageAndPushActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_item_main_logout:
			BmobUserManager.getInstance(getApplicationContext()).logout();
			// BmobUser.logOut(getApplicationContext());
			ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
			am.killBackgroundProcesses(getPackageName());

			intent = new Intent(this, SplashActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.menu_item_main_about:
			startActivity(new Intent(this, AboutActivity.class));
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void selectPanel(int position) {
		this.panel_position = position;
		current_panel = panels.get(position);
		PanelFragment fr = PanelFragment.newInstance(panels.get(position));
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.fl_main, fr);
		ft.commit();
		getSupportActionBar().setTitle(panels.get(position).getName());
		mDrawerLayout.closeDrawers();
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.rl_settings:// 打开设置
			Log.i(TAG, "打开设置");
			intent = new Intent(getApplicationContext(), SettingsActivity.class);
			startActivity(intent);
			break;
		case R.id.fl_user_info:// 帧布局抽屉上部的头像和用户名
			Log.i(TAG, "打开个人信息页");
			if (currentUser != null) {
				// 打开个人信息页
				intent = new Intent(MainActivity.this, PersonInfoActivity.class);
				intent.putExtra(Constants.user, currentUser);
				startActivity(intent);
			}
			break;
		case R.id.rl_manage:
			// 管理界面
			intent = new Intent(MainActivity.this, ManageActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	private void fetchPanels() {
		BmobQuery<Panel> query = new BmobQuery<Panel>();
		query.findObjects(getApplicationContext(), new FindListener<Panel>() {

			@Override
			public void onError(int arg0, String arg1) {
				// ToastUtils.makeText(getApplicationContext(), "获取版块列表失败，错误码为："
				// + arg0);
			}

			@Override
			public void onSuccess(List<Panel> list) {
				panels.addAll(list);
				orderPanels(list);
				handler.sendEmptyMessage(FETCH_PANEL_SUCCESS);
			}

			/**
			 * 按照是否是预支版块进行排序
			 * 
			 * @param list
			 *            版块列表
			 */
			private void orderPanels(List<Panel> list) {
				for (Panel panel : panels) {
					if (panel.isPreset()) {
						panels_preset.add(panel);
					} else {
						panels_not_preset.add(panel);
					}
				}
				panels.clear();
				panels.addAll(panels_preset);
				panels.addAll(panels_not_preset);
				for (Panel panel : panels) {
					Log.i(TAG, panel.toString());
				}
			}
		});
	}

	/**
	 * 版块适配器
	 * 
	 * @author sj
	 * 
	 */
	private class PanelAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return panels_preset.size()+panels_not_preset.size() + 1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Panel currentPanel = null;
			if (position == panels_preset.size()) {
				TextView textview = new TextView(MainActivity.this);
				textview.setText("更多版块");
				textview.setBackgroundColor(getResources().getColor(
						R.color.gray));
				return textview;
			}
			if (position < panels_preset.size()) {
				currentPanel = panels_preset.get(position);
			} else {
				currentPanel = panels_not_preset.get(position
						- panels_preset.size() - 1);
			}
			// R.layout.item_drawer_panel
			ViewHolder holder;
			if(convertView!=null&&convertView instanceof RelativeLayout){
				holder=(ViewHolder) convertView.getTag();
			}else{
				holder = new ViewHolder();
				convertView = View.inflate(MainActivity.this,
						R.layout.item_drawer_panel,null);
//						android.R.layout.simple_list_item_activated_1, null);
				holder.tv_panel = (TextView) convertView
						.findViewById(R.id.tv_panel);
				convertView.setTag(holder);
			}
			
			holder.tv_panel.setText(currentPanel.getName());
			return convertView;
		}

		@Override
		public Object getItem(int position) {
			Panel currentPanel = null;
			if (position == panels_preset.size())
				return null;
			if (position < panels_preset.size()) {
				currentPanel = panels_preset.get(position);
			} else {
				currentPanel = panels_not_preset.get(position
						- panels_preset.size() - 1);
			}
			return currentPanel;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

	}

	static class ViewHolder {
		private TextView tv_panel;
	}

}
