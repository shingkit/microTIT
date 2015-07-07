package com.gyxy.sns.ui.fragment;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.gyxy.sns.MyApplication;
import com.gyxy.sns.R;
import com.gyxy.sns.adapter.BaseListAdapter;
import com.gyxy.sns.adapter.ViewHolder;
import com.gyxy.sns.model.AdminApplication;
import com.gyxy.sns.model.AuthApplication;
import com.gyxy.sns.model.MoreInfo;
import com.gyxy.sns.model.User;
import com.gyxy.sns.ui.activity.ChatActivity;
import com.gyxy.sns.ui.widget.BezelImageView;
import com.gyxy.sns.utils.LogUtils;
import com.gyxy.sns.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class AdminApplyManageFragment extends Fragment {
	protected static final String TAG="AdminApplyManageFragment";

	protected static final int FETCH_SUCCESS = 100;

	private static List<AdminApplication> list;

	private View rootView;
	private ListView lv;

	private static AdminApplyAdapter adapter;

	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == FETCH_SUCCESS) {
				if (adapter == null) {
					adapter = new AdminApplyAdapter(getActivity(), list);
					lv.setAdapter(adapter);
				} else {
					adapter.notifyDataSetChanged();
				}
			}
		}
	};


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_admin_apply_manage,
				container, false);
		init();
		fetch();
		return rootView;
	}

	private void init() {
		lv = (ListView) rootView.findViewById(R.id.listview);
	}

	private void fetch() {
		BmobQuery<AdminApplication> query = new BmobQuery<AdminApplication>();
		query.include("user");
		query.addWhereEqualTo("state", AdminApplication.state_applicating);
		query.findObjects(getActivity(), new FindListener<AdminApplication>() {

			@Override
			public void onError(int code, String msg) {
				ToastUtils.makeErrorText(getActivity(), code, msg);
			}

			@Override
			public void onSuccess(List<AdminApplication> arg0) {
				list = arg0;
				handler.sendEmptyMessage(FETCH_SUCCESS);
			}

		});
	}

	private class AdminApplyAdapter extends BaseListAdapter<AdminApplication> {

		public AdminApplyAdapter(Context context, List<AdminApplication> list) {
			super(context, list);
		}

		@Override
		public View bindView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.item_admin_apply_manage, parent, false);
			}
			TextView tv_username = ViewHolder
					.get(convertView, R.id.tv_username);
			BezelImageView biv_head = ViewHolder
					.get(convertView, R.id.biv_head);

			final AdminApplication adminApplication = list.get(position);
			final User user = adminApplication.getUser();

			tv_username.setText(user.getUsername());
			ImageLoader.getInstance().displayImage(user.getAvatar(), biv_head,
					MyApplication.getHeadOptions());

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showDialog();
				}

				private void showDialog() {
					AlertDialog dialog = new AlertDialog.Builder(getActivity())
							.setTitle("提醒")
							.setMessage("同意" + user.getUsername() + "的个人认证？")
							.setNegativeButton("不同意",
									new AlertDialog.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											setState(
													adminApplication,
													AdminApplication.state_rejected);
										}

										

									})
							.setPositiveButton("同意",
									new AlertDialog.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											setManager(adminApplication);
 
										}

									}).create();
					dialog.show();
				}
			});
			return convertView;
		}

	}
	/**
	 * 设置MoreInfo表中用户角色为管理员，成功后设置AdminApplication状态为已同意
	 * @param adminApplication
	 */
	private void setManager(final AdminApplication adminApplication) {

		BmobQuery<MoreInfo>query=new BmobQuery<MoreInfo>();
		query.addWhereEqualTo("user", adminApplication.getUser());
		query.findObjects(getActivity(), new FindListener<MoreInfo>() {

			@Override
			public void onError(int code, String msg) {
				LogUtils.E(TAG, code, "GET MoreInfo FAILED "+msg);
				ToastUtils.makeErrorText(getActivity(), code, msg);
			}

			@Override
			public void onSuccess(List<MoreInfo> arg0) {
				ToastUtils.makeText(getActivity(), "设置管理员成功，正在同步数据");
				MoreInfo moreInfo = arg0.get(0);
				moreInfo.setUserRole(MoreInfo.ADMIN);
				moreInfo.update(getActivity(), new UpdateListener() {
					
					@Override
					public void onSuccess() {
						fetch();
						setState(adminApplication, AdminApplication.state_agreed);
					}
					
					@Override
					public void onFailure(int code, String msg) {
						LogUtils.E(TAG, code, "SET MoreInfo:USERROLE FAILED "+msg);
						ToastUtils.makeErrorText(getActivity(), code, msg);
					}
				});
			}
		});
	
	}
	private void setState(final AdminApplication adminApplication, int flag) {
		adminApplication.setState(flag);
		adminApplication.update(getActivity(), new UpdateListener() {
			
			@Override
			public void onSuccess() { 
				ToastUtils.makeText(getActivity(), "设置状态成功");
				fetch();
			}
			

			@Override
			public void onFailure(int code, String msg) {
				LogUtils.E(TAG, code,"SET STATE "+ msg);
				ToastUtils.makeErrorText(getActivity(), code, msg);
			}
		});
	}
}
