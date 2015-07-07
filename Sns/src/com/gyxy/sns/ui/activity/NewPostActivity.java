package com.gyxy.sns.ui.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.gyxy.sns.R;
import com.gyxy.sns.model.Panel;
import com.gyxy.sns.model.Post;
import com.gyxy.sns.model.User;
import com.gyxy.sns.utils.CacheUtils;
import com.gyxy.sns.utils.CompatUtils;
import com.gyxy.sns.utils.ImageUtils;
import com.gyxy.sns.utils.ToastUtils;

@SuppressLint("NewApi")
public class NewPostActivity extends SwipeBackActivity implements
		OnClickListener {

	private EditText et_title;
	private EditText et_content;
	private LinearLayout ll_take_pic;
	private LinearLayout ll_select_pic;
	private ImageView iv_select_pic;
	private ImageView iv_take_pic;

	private Panel current_panel;
	private static final int REQUESTCODE_SELECT_PIC = 100;
	private static final int REQUESTCODE_SELECT_PIC_KITKAT = 102;
	private static final int REQUESTCODE_TAKE_PIC = 101;

	private static final String TAG = "NewPostActivity";
	private Intent intent;
	private String dateTime;
	private String targeturl;
	private String fileName;
	private Bitmap bitmap;
	private BmobFile bmobFile;
	private String files;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_post);
		getToolbar().setTitle("发帖");
		Bundle extras = getIntent().getExtras();
		current_panel = (Panel) extras.getSerializable("current_panel");

		initView();
	}

	private void initView() {
		et_title = (EditText) findViewById(R.id.et_title);
		et_content = (EditText) findViewById(R.id.et_content);
		ll_select_pic = (LinearLayout) findViewById(R.id.ll_select_pic);
		ll_take_pic = (LinearLayout) findViewById(R.id.ll_take_pic);
		iv_select_pic = (ImageView) findViewById(R.id.iv_select_pic);
		iv_take_pic = (ImageView) findViewById(R.id.iv_take_pic);

		ll_select_pic.setOnClickListener(this);
		ll_take_pic.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_new_post, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_item_post:
			if (targeturl == null) {
				post();
			} else {
				uploadPic();
			}
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void uploadPic() {
		bmobFile = new BmobFile(new File(targeturl));
		bmobFile.uploadblock(getApplicationContext(), new UploadFileListener() {

			@Override
			public void onSuccess() {
				Log.i(TAG, "上传成功,发帖中。。");
				post();
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

	private void post() {

		String title = et_title.getText().toString();
		String content = et_content.getText().toString();
		if (TextUtils.isEmpty(title)) {
			ToastUtils.makeText(getApplicationContext(), "标题不能为空");
			return;
		}
		if (TextUtils.isEmpty(content)) {
			ToastUtils.makeText(getApplicationContext(), "正文不能为空");
			return;
		}

		Post post = new Post();
		post.setPanel(current_panel);
		post.setUser(BmobUser.getCurrentUser(this, User.class));
		post.setTitle(title);
		post.setContent(content);
		if (!TextUtils.isEmpty(targeturl)) {
			Log.i(TAG, "target" + targeturl);
			post.setImage(bmobFile);
		}
		post.save(this, new SaveListener() {

			@Override
			public void onSuccess() {
				ToastUtils.makeText(getApplicationContext(), "发帖成功,返回页面");
				NewPostActivity.this.finish();
			}

			@Override
			public void onFailure(int code, String msg) {
				ToastUtils.makeText(getApplicationContext(), "发帖失败,错误信息："
						+ code + "msg" + msg);
			}
		});
	}

	@Override
	public void onClick(View v) {

		Date date1 = new Date(System.currentTimeMillis());
		dateTime = date1.getTime() + "";
		switch (v.getId()) {
		case R.id.ll_select_pic:

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
		case R.id.ll_take_pic:
			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

			files = CacheUtils.getCacheDirectory(getApplicationContext(), true,
					"pic") + dateTime + ".jpg";
			// 设置保存路径
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(files)));
			if (intent.resolveActivity(getPackageManager()) != null) {
				startActivityForResult(intent, REQUESTCODE_TAKE_PIC);
			}
			break;
		default:
			break;
		}
	}

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

					iv_select_pic.setBackgroundDrawable(new BitmapDrawable(
							getResources(), bitmap));
					ll_take_pic.setVisibility(View.GONE);
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

				iv_select_pic.setBackground(new BitmapDrawable(getResources(),
						bitmap));
				ll_take_pic.setVisibility(View.GONE);
			}
			if (requestCode == REQUESTCODE_TAKE_PIC) {
				File file = new File(files);
				if (file.exists()) {
					bitmap = ImageUtils.compressImageFromFile(files);
					targeturl = ImageUtils.saveToSdCard(this, bitmap, dateTime);
					iv_take_pic.setBackgroundDrawable(new BitmapDrawable(
							getResources(), bitmap));
					ll_select_pic.setVisibility(View.GONE);
				} else {

				}
			}

		} else {
			Log.i(TAG, "resp code" + responseCode);
		}
	}

}
