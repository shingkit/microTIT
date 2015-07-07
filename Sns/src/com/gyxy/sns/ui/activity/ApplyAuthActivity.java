package com.gyxy.sns.ui.activity;

import java.io.File;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.gyxy.sns.R;
import com.gyxy.sns.model.AuthApplication;
import com.gyxy.sns.model.User;
import com.gyxy.sns.utils.CompatUtils;
import com.gyxy.sns.utils.ImageUtils;
import com.gyxy.sns.utils.ToastUtils;

public class ApplyAuthActivity extends SwipeBackActivity implements
		OnClickListener {

	private static final int REQUESTCODE_SELECT_PIC_KITKAT = 101;
	private static final int REQUESTCODE_SELECT_PIC = 100;
	private static final String TAG = null;
	private String fileName = null;
	private Bitmap bitmap;
	private ImageView iv_pic;
	private String targeturl;

	private String dateTime;
	private EditText et_vip_info;
	private Button btn_add_pic;
	private Button btn_apply;
	private BmobFile bmobFile;
	private EditText et_real_name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_apply_auth);
		getToolbar().setTitle("实名认证");
		initView();
	}

	private void initView() {
		et_vip_info = (EditText) findViewById(R.id.et_vip_info);
		et_real_name = (EditText) findViewById(R.id.et_real_name);
		iv_pic = (ImageView) findViewById(R.id.iv_pic);
		btn_add_pic = (Button) findViewById(R.id.btn_add_pic);
		btn_apply = (Button) findViewById(R.id.btn_apply);
		
		btn_apply.setOnClickListener(this);
		btn_add_pic.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.btn_add_pic:
			if (CompatUtils.isAboveKitkat()) {
				intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
				intent.addCategory(Intent.CATEGORY_OPENABLE);
			} else {
				intent = new Intent(Intent.ACTION_GET_CONTENT);
			}
			intent.setType("image/*");
			if (intent.resolveActivity(getPackageManager()) != null) {
				if (CompatUtils.isAboveKitkat()) {
					startActivityForResult(intent,
							REQUESTCODE_SELECT_PIC_KITKAT);
				} else {
					startActivityForResult(intent, REQUESTCODE_SELECT_PIC);
				}
			}
			break;
		case R.id.btn_apply:
			uploadPic();
			break;
		default:
			break;
		}
	}

	private void uploadPic() {
		bmobFile = new BmobFile(new File(targeturl));
		bmobFile.uploadblock(getApplicationContext(), new UploadFileListener() {

			@Override
			public void onSuccess() {
				Log.i(TAG, "图片上传成功,上传图片数据成功。。");
				apply();
			}

			@Override
			public void onFailure(int code, String msg) {
				ToastUtils.makeText(getApplicationContext(), "出错啦！请重试");
				Log.i(TAG, "上传失败" + code + ":" + msg);
			}

			@Override
			public void onProgress(Integer arg0) {
				super.onProgress(arg0);
				Log.i(TAG, "progress" + arg0);
			}
		});
	}

	private void apply() {
		String vip_info=et_vip_info.getText().toString().trim();
		String real_name=et_real_name.getText().toString().trim();
		
		AuthApplication application = new AuthApplication();
		application.setState(AuthApplication.state_applicating);
		application.setUser(BmobUser.getCurrentUser(getApplicationContext(),
				User.class));
		
		if(TextUtils.isEmpty(real_name)|TextUtils.isEmpty(vip_info)){
			ToastUtils.makeText(getApplicationContext(), "还没有填写完呢！！");
			return;
		}
		if (TextUtils.isEmpty(targeturl)) {
			ToastUtils.makeText(getApplicationContext(), "没有检测到图片");
			return;
		} else {
			Log.i(TAG, "target" + targeturl);
			application.setImage(bmobFile);
		}
		application.setName(real_name);
		application.setVip_info(vip_info);
		application.save(getApplicationContext(), new SaveListener() {

			@Override
			public void onFailure(int arg0, String arg1) {

			}

			@Override
			public void onSuccess() {
				ToastUtils.makeText(getApplicationContext(), "申请已提交，请耐心等待..");
				new Thread() {
					public void run() {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						ApplyAuthActivity.this.finish();
					};
				}.start();
			}

		});
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	protected void onActivityResult(int requestCode, int responseCode,
			Intent data) {
		if (responseCode == RESULT_OK) {

			if (requestCode == REQUESTCODE_SELECT_PIC) {

				fileName = null;
				if (data != null) {
					Uri originalUri = data.getData();
					ContentResolver cr = getContentResolver();
					Cursor cursor = cr.query(originalUri, null, null, null,
							null);
					if (cursor.moveToNext()) {
						fileName = cursor.getString(cursor
								.getColumnIndex("_data"));
						Log.i(TAG, "get pic:" + fileName);
					}
					cursor.close();
					bitmap = ImageUtils.compressImageFromFile(fileName);
					targeturl = ImageUtils.saveToSdCard(this, bitmap, dateTime);
					iv_pic.setImageBitmap(bitmap);
					iv_pic.setVisibility(View.VISIBLE);
				}
			}
			if (requestCode == REQUESTCODE_SELECT_PIC_KITKAT) {
				fileName = null;
				if (data != null) {
					Uri originalUri = data.getData();
					fileName = ImageUtils.getPath(getApplicationContext(),
							originalUri);
					Log.i(TAG, "filename" + fileName);
				}
				bitmap = ImageUtils.compressImageFromFile(fileName);
				targeturl = ImageUtils.saveToSdCard(this, bitmap, dateTime);
				iv_pic.setImageBitmap(bitmap);
				 
				iv_pic.setVisibility(View.VISIBLE);
			}
			btn_add_pic.setVisibility(View.GONE);
			btn_apply.setVisibility(View.VISIBLE);

		} else {
			Log.i(TAG, "resp code" + responseCode);
		}
	}
 
}
