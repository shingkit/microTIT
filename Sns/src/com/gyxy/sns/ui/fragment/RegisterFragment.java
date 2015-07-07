package com.gyxy.sns.ui.fragment;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.gyxy.sns.R;
import com.gyxy.sns.model.MoreInfo;
import com.gyxy.sns.model.User;
import com.gyxy.sns.ui.activity.MainActivity;
import com.gyxy.sns.utils.LogUtils;
import com.gyxy.sns.utils.ToastUtils;

public class RegisterFragment extends Fragment implements OnClickListener {
	protected static final String TAG = "RegisterFragment";
	private EditText et_username;
	private EditText et_pwd;
	private EditText et_pwd_repeat;
	private EditText et_email;
	private Button btn_register;

	private Context mContext;
	private CheckBox cb_agreement;
	private User user;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();

		View rootView = inflater.inflate(R.layout.fragment_register, container,
				false);
		et_username = (EditText) rootView.findViewById(R.id.et_username);
		et_pwd = (EditText) rootView.findViewById(R.id.et_pwd);
		et_pwd_repeat = (EditText) rootView.findViewById(R.id.et_pwd_repeat);
		et_email = (EditText) rootView.findViewById(R.id.et_email);
		btn_register = (Button) rootView.findViewById(R.id.btn_register);
		cb_agreement = (CheckBox) rootView.findViewById(R.id.cb_agreement);
		cb_agreement.setOnClickListener(this);
		btn_register.setOnClickListener(this);
		return rootView;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_register:
			// register
			String username = et_username.getText().toString();
			String pwd = et_pwd.getText().toString();
			String pwd_repeat = et_pwd_repeat.getText().toString();
			String email = et_email.getText().toString();

			if (TextUtils.isEmpty(username)) {
				ToastUtils.makeText(mContext, "�������û�����");
				return;
			}
			if (TextUtils.isEmpty(pwd)) {
				ToastUtils.makeText(mContext, "���������룡");
				return;
			}
			if (TextUtils.isEmpty(pwd_repeat)) {
				ToastUtils.makeText(mContext, "���ظ��������룡");
				return;
			}
			if (!pwd.equals(pwd_repeat)) {
				ToastUtils.makeText(mContext, "������������벻һ�£�");
				return;
			}
			if (TextUtils.isEmpty(email)) {
				ToastUtils.makeText(mContext, "��������֤���䣡");
				return;
			}
			if (!cb_agreement.isChecked()) {
				ToastUtils.makeText(mContext, "ע�����ͬ��Э�飡");
				return;
			}

			user = new User();
			user.setUsername(username);
			user.setPassword(pwd);
			user.setEmail(email);

			// ���û����豸�����
			user.setDeviceType("android");
			user.setInstallId(BmobInstallation.getInstallationId(getActivity()));
			user.signUp(mContext, new SaveListener() {

				private MoreInfo info;

				@Override
				public void onSuccess() {
					Toast.makeText(mContext, "ע��ɹ�", Toast.LENGTH_SHORT).show();
					// ���豸��username���а�
					BmobUserManager userManager = BmobUserManager
							.getInstance(mContext);
					userManager.bindInstallationForRegister(user.getUsername());
					
					addRole();
					Intent intent = new Intent(mContext, MainActivity.class);
					startActivity(intent);
					getActivity().finish();
				}

				private void addRole() {
					//MoreInfo�ϴ�
					info = new MoreInfo();
					info.setUser(user);
					info.setUserRole(MoreInfo.MEMBER);
					info.save(getActivity(), new SaveListener() {
						
						@Override
						public void onSuccess() {
							//�� ����
							user.setMoreInfo(info);
							user.update(getActivity(), new UpdateListener() {
								
								@Override
								public void onSuccess() {
									LogUtils.i(TAG, "�û��͸������ݰ󶨳ɹ�");
								}
								
								@Override
								public void onFailure(int arg0, String arg1) {
									LogUtils.E(TAG, "�û��͸������ݰ�ʧ��"+arg1);
								}
							});
						}
						
						@Override
						public void onFailure(int arg0, String arg1) {
							// TODO Auto-generated method stub
							
						}
					});
				}

				@Override
				public void onFailure(int code, String arg1) {
					ToastUtils.makeErrorText(mContext, code, arg1);
					Log.i(TAG, "��½ʧ��,code:" + code + "������Ϣ:" + arg1);
				}
			});
			break;
		case R.id.cb_agreement:
			showDialog();
			break;
		default:
			break;
		}

	}

 

	/**
	 * ��ʾ�Ի��� ��ʾע����֪
	 */
	public void showDialog() {
		View view_dialog = View.inflate(mContext, R.layout.dialog_register,
				null);
		WebView webview = (WebView) view_dialog.findViewById(R.id.webview);
		webview.loadUrl("file:///android_res/raw/register.html");
		AlertDialog.Builder builder = new Builder(getActivity())
				.setTitle(R.string.register_need_know)
				.setView(view_dialog)
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						})
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								cb_agreement.setChecked(true);
								dialog.dismiss();

							}
						});
		AlertDialog dialog = builder.create();
		dialog.show();

	}

}
