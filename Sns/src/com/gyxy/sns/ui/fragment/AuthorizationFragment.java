package com.gyxy.sns.ui.fragment;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.gyxy.sns.MyApplication;
import com.gyxy.sns.R;
import com.gyxy.sns.adapter.BaseListAdapter;
import com.gyxy.sns.adapter.ViewHolder;
import com.gyxy.sns.model.AuthApplication;
import com.gyxy.sns.model.MoreInfo;
import com.gyxy.sns.model.User;
import com.gyxy.sns.ui.widget.BezelImageView;
import com.gyxy.sns.utils.LogUtils;
import com.gyxy.sns.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class AuthorizationFragment extends Fragment {

	protected static final String TAG = null;
	private List<AuthApplication> list;
	private ListView listview;
	private AuthApplication clickedAuthApplication;
	private User clickItemUser;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_authorization,
				container, false);
		initView(rootView);
		fetchData();
		return rootView;
	}

	private void initView(View rootView) {
		listview = (ListView) rootView.findViewById(R.id.listview);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				clickedAuthApplication = (AuthApplication) arg0
						.getItemAtPosition(position);
				clickItemUser = clickedAuthApplication.getUser();
				AlertDialog dialog = new AlertDialog.Builder(getActivity())
						.setTitle("提醒")
						.setMessage(
								"同意" + clickItemUser.getUsername() + "的个人认证？")
						.setNeutralButton(android.R.string.cancel,
								new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								})
						.setNegativeButton("不同意", new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								setState(clickedAuthApplication,
										AuthApplication.state_rejected);
							}
						}).setPositiveButton("同意", new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								setState(clickedAuthApplication,
										AuthApplication.state_agreed);
							}

						}).create();
				dialog.show();
			}

		});
	}

	private void setState(final AuthApplication authApply, final int state) {
		authApply.setState(state);
		authApply.update(getActivity(), new UpdateListener() {

			@Override
			public void onSuccess() {
				ToastUtils.makeText(getActivity(), "正在同步数据，请稍等..");
				if (state == AuthApplication.state_agreed) {
					 setMoreInfo();
				} else {
					fetchData();
				}
			}

			@Override
			public void onFailure(int code, String msg) {
				ToastUtils.makeErrorText(getActivity(), code, msg);
			}
		});
	}

	private void fetchData() {
		BmobQuery<AuthApplication> query = new BmobQuery<AuthApplication>();
		query.addWhereEqualTo("state", AuthApplication.state_applicating);
		query.include("user");
		query.findObjects(getActivity(), new FindListener<AuthApplication>() {

			@Override
			public void onSuccess(List<AuthApplication> arg0) {
				list = arg0;
				AuthAdapter adapter = new AuthAdapter(getActivity(), list);
				listview.setAdapter(adapter);
			}

			@Override
			public void onError(int arg0, String arg1) {

			}
		});
	}

	private class AuthAdapter extends BaseListAdapter<AuthApplication> {

		public AuthAdapter(Context context, List<AuthApplication> list) {
			super(context, list);
		}
 

		@Override
		public View bindView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_auth_manage,
						parent, false);
			}
 
			BezelImageView biv_head = ViewHolder
					.get(convertView, R.id.biv_head);
			TextView tv_username = ViewHolder
					.get(convertView, R.id.tv_username);
			TextView tv_vip_info = ViewHolder
					.get(convertView, R.id.tv_vip_info);
			TextView tv_realname = ViewHolder.get(convertView, R.id.tv_name);
			ImageView iv_image = ViewHolder.get(convertView, R.id.iv_image);

			AuthApplication authApplication = list.get(position);
			//用户名 头像
			tv_username.setText(authApplication.getUser().getUsername());
			if (!TextUtils.isEmpty(authApplication.getUser().getAvatar())) {
				ImageLoader.getInstance().displayImage(
						authApplication.getUser().getAvatar(), biv_head,
						MyApplication.getHeadOptions());
			}
			//真实姓名 身份 图片
			if(!TextUtils.isEmpty(authApplication.getName()))
				tv_realname.setText("真实姓名：" + authApplication.getName());
			
			tv_vip_info.setText("申请认证身份："+authApplication.getVip_info());
			
			if (authApplication.getImage() != null
					&& authApplication.getImage().getFileUrl(getActivity()) != null) {
				ImageLoader.getInstance().displayImage(
						authApplication.getImage().getFileUrl(getActivity()),
						iv_image, MyApplication.getThumbnailOptions());
			}
			return convertView;
		}

	}

	/**
	 * 将AuthApplication 里的数据设置到MoreInfo中
	 */
	private void setMoreInfo() {
		// 设置角色
		BmobQuery<MoreInfo> queryInfo = new BmobQuery<MoreInfo>();
		queryInfo.addWhereEqualTo("user", clickItemUser);
		queryInfo.findObjects(getActivity(), new FindListener<MoreInfo>() {

			@Override
			public void onError(int arg0, String arg1) {
				LogUtils.E(TAG, "无法获取到当前用户的角色：" + arg1);
			}

			@Override
			public void onSuccess(List<MoreInfo> arg0) {
				if (arg0 != null && arg0.size() > 0) {
					String objectId = arg0.get(0).getObjectId();

					MoreInfo info = new MoreInfo();
					info.setName(clickedAuthApplication.getName());
					info.setVip_info(clickedAuthApplication.getVip_info());
					info.setVip_image(clickedAuthApplication.getImage()
							.getFileUrl(getActivity()));
					info.update(getActivity(), objectId, new UpdateListener() {

						@Override
						public void onSuccess() {
							ToastUtils.makeText(getActivity(), "操作成功");
							fetchData();
						}

						@Override
						public void onFailure(int code, String msg) {
							ToastUtils.makeErrorText(getActivity(), code, msg);
						}
					});
					LogUtils.i(TAG, "获取到当前用户的角色：" + arg0.get(0).getUserRole());
				} else {
					LogUtils.i(TAG, "获取到当前用户的角色为  null");
				}
			}
		});
	}
}
