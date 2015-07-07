package com.gyxy.sns.ui.fragment;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.gyxy.sns.Constants;
import com.gyxy.sns.MyApplication;
import com.gyxy.sns.R;
import com.gyxy.sns.model.AdminApplication;
import com.gyxy.sns.model.AuthApplication;
import com.gyxy.sns.model.MoreInfo;
import com.gyxy.sns.model.User;
import com.gyxy.sns.ui.activity.ApplyAuthActivity;
import com.gyxy.sns.ui.activity.ChatActivity;
import com.gyxy.sns.ui.widget.BezelImageView;
import com.gyxy.sns.utils.BmobUtils;
import com.gyxy.sns.utils.CompatUtils;
import com.gyxy.sns.utils.ImageUtils;
import com.gyxy.sns.utils.LogUtils;
import com.gyxy.sns.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class BasicInfoFragment extends Fragment implements OnClickListener {
	private static final int REQUESTCODE_SELECT_PIC = 100;
	private static final int REQUESTCODE_SELECT_PIC_KITKAT = 102;
	private static final String TAG = "BasicInfoFragment";

	private String userRole;
	private String vip_info;

	private BezelImageView biv_head;
	private ImageView iv_sex;
	private ImageView iv_vip;
	private TextView tv_username;
	private TextView tv_stuId;
	private TextView tv_vip_info;
	private TextView tv_role;
	private TextView tv_email;
	private TextView tv_phone;
	private User user;
	private AlertDialog dialog;

	private String dateTime;
	private String targeturl;
	private String fileName;
	private Bitmap bitmap;
	private BmobFile bmobFile;

	// private String files;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_basic_info, container,
				false);
		user = (User) getActivity().getIntent().getSerializableExtra(
				Constants.user);
		initView(view);
		initEvent();
		getUserMoreInfo();
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {// 如果传入的参数不是当前用户，显示私信图标
		super.onCreateOptionsMenu(menu, inflater);
		if (!BmobUtils.isAdmin(getActivity())) {
			// 申请管理员
			MenuItem reqAdminItem = menu.add(0, R.id.menu_req_admin, 0,
					R.string.menu_req_manager);
			MenuItemCompat.setShowAsAction(reqAdminItem,
					MenuItem.SHOW_AS_ACTION_NEVER);
		}

	}

	private void initEvent() {
		// 如果是当前登陆用户，则设置点击事件
		if (isCurrentUser()) {
			tv_vip_info.setClickable(true);
			tv_vip_info.setOnClickListener(this);
			biv_head.setClickable(true);
			biv_head.setOnClickListener(this);
			iv_sex.setClickable(true);
			iv_sex.setOnClickListener(this);
			tv_stuId.setClickable(true);
			tv_stuId.setOnClickListener(this);
		} else {
			tv_vip_info.setVisibility(View.GONE);
		}

	}

	private boolean isCurrentUser() {
		return User.getCurrentUser(getActivity(), User.class).getObjectId()
				.equals(user.getObjectId());
	}

	private void getUserMoreInfo() {
		BmobQuery<MoreInfo> query = new BmobQuery<MoreInfo>();
		query.addWhereEqualTo("user", user);
		query.findObjects(getActivity(), new FindListener<MoreInfo>() {
			@Override
			public void onError(int arg0, String arg1) {

			}

			@Override
			public void onSuccess(List<MoreInfo> arg0) {
				if (arg0 != null && arg0.size() > 0) {
					MoreInfo moreInfo = arg0.get(0);
					userRole = moreInfo.getUserRole();
					vip_info = moreInfo.getVip_info();
					if (!TextUtils.isEmpty(userRole))
						tv_role.setText("权限等级：" + userRole);
					if (!TextUtils.isEmpty(vip_info)) {
						tv_vip_info.setText("认证身份：" + vip_info);
						tv_vip_info.setVisibility(View.VISIBLE);
						iv_vip.setVisibility(View.VISIBLE);
					}
				}
			}
		});
	}

	private void initView(View view) {
		iv_vip = (ImageView) view.findViewById(R.id.iv_vip);
		biv_head = (BezelImageView) view.findViewById(R.id.biv_head);
		iv_sex = (ImageView) view.findViewById(R.id.iv_sex);
		tv_username = (TextView) view.findViewById(R.id.tv_username);
		tv_stuId = (TextView) view.findViewById(R.id.tv_stuId);
		tv_vip_info = (TextView) view.findViewById(R.id.tv_vip_info);
		tv_role = (TextView) view.findViewById(R.id.tv_role);
		tv_email = (TextView) view.findViewById(R.id.tv_email);
		tv_phone = (TextView) view.findViewById(R.id.tv_phone);

		if (!TextUtils.isEmpty(user.getUsername()))
			tv_username.setText(user.getUsername());
		if (isCurrentUser()) {

			if (!TextUtils.isEmpty(Constants.vip_info)) {
				iv_vip.setVisibility(View.VISIBLE);
				tv_vip_info.setText("认证信息：" + Constants.vip_info);
			}
			tv_role.setText("权限等级：" + Constants.userRole);
		}
		if (!TextUtils.isEmpty(user.getStuId()))
			tv_stuId.setText("学号：" + user.getStuId());
		if (!TextUtils.isEmpty(user.getEmail())) {
			tv_email.setText("E-mail：" + user.getEmail());
		}
		if (!TextUtils.isEmpty(user.getPhoneNumber()))
			tv_phone.setText("手机号：" + user.getPhoneNumber());

		if (!user.equals(BmobUser.getCurrentUser(getActivity(), User.class))) {
			tv_email.setVisibility(View.GONE);
		}

		if (user.getSex())
			iv_sex.setImageResource(R.drawable.male);
		else
			iv_sex.setImageResource(R.drawable.female);

		if (!TextUtils.isEmpty(user.getAvatar())) {// 头像
			ImageLoader.getInstance().displayImage(user.getAvatar(), biv_head,
					MyApplication.getHeadOptions());
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_vip_info:
			Intent intent = new Intent(getActivity(), ApplyAuthActivity.class);
			startActivity(intent);
			break;
		case R.id.tv_stuId:
			showSetStuIdDialog();
			break;
		case R.id.iv_sex:
			showSelectSexDialog();
			break;
		case R.id.biv_head:
			selectPic();
			break;
		}

	}

	@Override
	public void onActivityResult(int requestCode, int responseCode, Intent data) {
		if (responseCode == Activity.RESULT_OK) {

			if (requestCode == REQUESTCODE_SELECT_PIC) {

				fileName = null;
				if (data != null) {
					Uri originalUri = data.getData();
					ContentResolver cr = getActivity().getContentResolver();
					Cursor cursor = cr.query(originalUri, null, null, null,
							null);
					if (cursor.moveToNext()) {
						fileName = cursor.getString(cursor
								.getColumnIndex("_data"));
						Log.i(TAG, "get pic:" + fileName);
					}
					cursor.close();
					bitmap = ImageUtils.compressImageFromFile(fileName);
					targeturl = ImageUtils.saveToSdCard(getActivity(), bitmap,
							dateTime);
					biv_head.setImageBitmap(bitmap);
					uploadPic();

				}
			}
			if (requestCode == REQUESTCODE_SELECT_PIC_KITKAT) {
				fileName = null;
				if (data != null) {
					Uri originalUri = data.getData();
					fileName = ImageUtils.getPath(getActivity(), originalUri);
					Log.i(TAG, "filename" + fileName);
				}
				bitmap = ImageUtils.compressImageFromFile(fileName);
				targeturl = ImageUtils.saveToSdCard(getActivity(), bitmap,
						dateTime);

				biv_head.setImageBitmap(bitmap);
				uploadPic();
			}
		}
	}

	private void uploadPic() {
		bmobFile = new BmobFile(new File(targeturl));
		bmobFile.uploadblock(getActivity(), new UploadFileListener() {

			@Override
			public void onSuccess() {
				ToastUtils.makeText(getActivity(), "上传头像成功！");
				setAvator();
				Log.i(TAG, "上传成功,发帖中。。");
			}

			private void setAvator() {
				user.setAvatar(bmobFile.getFileUrl(getActivity()));
				user.update(getActivity(), new UpdateListener() {

					@Override
					public void onSuccess() {
						ToastUtils.makeText(getActivity(), "设置头像成功");
					}

					@Override
					public void onFailure(int code, String msg) {
						LogUtils.E(TAG, code, msg);
					}
				});
			}

			@Override
			public void onFailure(int code, String msg) {
				ToastUtils.makeText(getActivity(), "上传头像失败！");
				LogUtils.E(TAG, code, msg);
			}

		});
	}

	private void selectPic() {
		Intent intent = null;
		if (CompatUtils.isAboveKitkat()) {
			intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
			intent.addCategory(Intent.CATEGORY_OPENABLE);
		} else {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
		}
		intent.setType("image/*");
		if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
			if (CompatUtils.isAboveKitkat()) {
				startActivityForResult(intent, REQUESTCODE_SELECT_PIC_KITKAT);
			} else {
				startActivityForResult(intent, REQUESTCODE_SELECT_PIC);
			}
		}
	}

	private void showSelectSexDialog() {
		String[] sexItems = new String[] { "男", "女" };
		dialog = new AlertDialog.Builder(getActivity())
				.setTitle("修改资料")

				.setSingleChoiceItems(sexItems, 0,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (which == 0) {
									setSex(true);
								} else {
									setSex(false);
								}
							}

							private void setSex(final boolean sex) {
								user.setSex(sex);
								user.update(getActivity(),
										new UpdateListener() {

											@Override
											public void onSuccess() {
												ToastUtils.makeText(
														getActivity(), "修改成功");
												dialog.dismiss();
												if (sex) {
													iv_sex.setImageResource(R.drawable.male);
												} else {
													iv_sex.setImageResource(R.drawable.female);
												}
											}

											@Override
											public void onFailure(int code,
													String msg) {
												ToastUtils.makeErrorText(
														getActivity(), code,
														msg);
											}
										});
							}
						}).create();
		dialog.show();
	}

	private void showSetStuIdDialog() {
		final EditText edittext = new EditText(getActivity());
		edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
		dialog = new AlertDialog.Builder(getActivity())
				.setTitle("设置学号")
				.setView(edittext)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								setStuId(edittext.getText().toString());
								dialog.dismiss();
							}

						})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create();
		dialog.show();
	}

	private void setStuId(final String stuId) {
		if (TextUtils.isEmpty(stuId)) {
			ToastUtils.makeText(getActivity(), "学号不能为空！");
			return;
		}
		user.setStuId(stuId);
		user.update(getActivity(), new UpdateListener() {

			@Override
			public void onSuccess() {
				tv_stuId.setText("学号：" + stuId);
				ToastUtils.makeText(getActivity(), "修改成功");
			}

			@Override
			public void onFailure(int code, String msg) {
				ToastUtils.makeErrorText(getActivity(), code, msg);
				LogUtils.E(TAG, code, msg);
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		LogUtils.i(TAG, "on item selected");
		switch (item.getItemId()) {
		case R.id.menu_item_personalinfo_msg:
			// 打开私信界面
			Intent intent = new Intent(getActivity(), ChatActivity.class);
			intent.putExtra(Constants.user, user);
			startActivity(intent);
			break;
		case R.id.menu_req_admin:

			if (BmobUtils.isAdmin(getActivity())) {
				ToastUtils.makeText(getActivity(), "您已经是管理员了！");
			} else {
				// 不是管理员 先判断是否通过vip验证
				if (TextUtils.isEmpty(vip_info)) {
					ToastUtils.makeText(getActivity(), "请先进行实名认证");
					intent = new Intent(getActivity(), ApplyAuthActivity.class);
					startActivity(intent);
				} else {
					reqAdmin();
				}
			}
			break;

		default:
			break;
		}
		return true;

	}

	private void reqAdmin() {
		AdminApplication application = new AdminApplication();
		application.setUser(User.getCurrentUser(getActivity(), User.class));
		application.setState(AuthApplication.state_applicating);
		application.save(getActivity(), new SaveListener() {

			@Override
			public void onSuccess() {
				ToastUtils.makeText(getActivity(), "已提交申请，请安心等待");
			}

			@Override
			public void onFailure(int code, String msg) {
				ToastUtils.makeErrorText(getActivity(), code, msg);
				LogUtils.E(TAG, code, msg);
			}
		});
	}
}
