package com.gyxy.sns.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.gc.materialdesign.views.ButtonFloat;
import com.gyxy.sns.Constants;
import com.gyxy.sns.MyApplication;
import com.gyxy.sns.R;
import com.gyxy.sns.adapter.CommentAdapter;
import com.gyxy.sns.model.Comment;
import com.gyxy.sns.model.Post;
import com.gyxy.sns.model.User;
import com.gyxy.sns.ui.widget.BezelImageView;
import com.gyxy.sns.utils.BmobUtils;
import com.gyxy.sns.utils.TimeUtil;
import com.gyxy.sns.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PostActivity extends SwipeBackActivity implements OnClickListener,
		OnTouchListener {

	protected static final int FETCH_SUCCESS = 1;
	protected static final int FETCH_FAILED = 0;
	private SwipeRefreshLayout swiperefresh;
	private ListView listview;
	private ImageView iv_image;
	private BezelImageView biv_head;
	private TextView tv_username;
	private TextView tv_content;
	private TextView tv_title;
	private TextView tv_empty;
	private Post currentPost;
	private RelativeLayout rl_post;

	private CommentAdapter adapter;

	private static final int COMMENTS_PER_PAGE = 20;
	protected static final String TAG = "PostActivity";

	private static int COMMENTS_SKIP = 0;

	private List<Comment> comments = new ArrayList<Comment>();
	private View view_up;
	private EditText et_content;
	private ImageButton ib_send;
	private ButtonFloat btn_more;
	private GestureDetector mDetector;
	private PopupWindow popupwindow;
	private View view_popup;

	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case FETCH_SUCCESS:
				// 获取回帖成功
				if (comments != null && comments.size() > 0) {
					Log.i(TAG, "COUNT:" + comments.size());
					if (adapter == null) {
						adapter = new CommentAdapter(getApplicationContext(),comments);
						
						listview.setAdapter(adapter);
					} else {
						adapter.notifyDataSetChanged();
					}
					tv_empty.setVisibility(View.GONE);
				} else {
					tv_empty.setVisibility(View.VISIBLE);
				}
				break;
			default:
				break;
			}
			swiperefresh.setRefreshing(false);
		};
	};
	private ButtonFloat btn_previous;
	private ButtonFloat btn_next;
	private TextView tv_createdAt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 初始化popupwindow的view
		view_popup = View.inflate(this, R.layout.popup_comment, null);
		btn_previous = (ButtonFloat) view_popup.findViewById(R.id.btn_previous);
		btn_previous.setOnClickListener(this);
		btn_next = (ButtonFloat) view_popup.findViewById(R.id.btn_next);
		btn_next.setOnClickListener(this);

		currentPost = (Post) getIntent().getSerializableExtra("post");
		setContentView(R.layout.activity_post);
		getToolbar().setTitle(R.string.post);
		initViews();
		fetchComments(0);
		setupGesture();
	}

	/**
	 * 手势识别
	 */
	private void setupGesture() {
		mDetector = new GestureDetector(this,
				new GestureDetector.SimpleOnGestureListener() {

					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						Log.i(TAG, "手势识别");
						if (Math.abs(velocityY) < 40) {
							// 无效动作
							Log.i(TAG, "无效动作");
							return true;
						}
						if (e2.getRawY() - e1.getRawY() > 40) {
							// 下滑
							tv_content.setVisibility(View.VISIBLE);
							iv_image.setVisibility(View.VISIBLE);
							return true;
						}
						if (e1.getRawY() - e2.getRawY() > 40) {
							// 上滑

							tv_content.setVisibility(View.GONE);
							iv_image.setVisibility(View.GONE);
							return true;
						}
						return super.onFling(e1, e2, velocityX, velocityY);
					}

				});
	}

	private void initViews() {
		et_content = (EditText) findViewById(R.id.et_content);
		ib_send = (ImageButton) findViewById(R.id.ib_send);
		btn_more = (ButtonFloat) findViewById(R.id.btn_more);
		rl_post = (RelativeLayout) findViewById(R.id.rl_post);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_content = (TextView) findViewById(R.id.tv_content);
		tv_username = (TextView) findViewById(R.id.tv_username);
		biv_head = (BezelImageView) findViewById(R.id.biv_head);
		tv_createdAt = (TextView) findViewById(R.id.tv_createdAt);
		biv_head.setOnClickListener(this);
		iv_image = (ImageView) findViewById(R.id.iv_image);
		swiperefresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
		listview = (ListView) findViewById(android.R.id.list);
		view_up = findViewById(R.id.view_up);
		tv_empty = (TextView) findViewById(R.id.tv_empty);
		rl_post.setOnTouchListener(this);
		ib_send.setOnClickListener(this);
		btn_more.setOnClickListener(this);

		swiperefresh.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				fetchComments(COMMENTS_SKIP);
			}
		});

		User user = currentPost.getUser();
		String title = currentPost.getTitle();
		String content = currentPost.getContent();
		BmobFile image = currentPost.getImage();
		String createdAt = currentPost.getCreatedAt();
		if (user != null) {
			tv_username.setText(user.getUsername());
			if (!TextUtils.isEmpty(user.getAvatar())) {
				// if(user.getHead()!=null&&user.getHead().getFileUrl(getApplicationContext())!=null){

				ImageLoader.getInstance().displayImage(user.getAvatar(),
						biv_head, MyApplication.getHeadOptions());
			}
		}
		if (!TextUtils.isEmpty(title)) {
			tv_title.setText(title);
			tv_title.setVisibility(View.VISIBLE);
		}
		if (!TextUtils.isEmpty(content)) {
			tv_content.setText(content);
			tv_content.setVisibility(View.VISIBLE);
		}
		tv_createdAt.setText(TimeUtil.getDescriptionTimeFromString(createdAt));
		if (image != null) {
			ImageLoader.getInstance().displayImage(image.getFileUrl(this),
					iv_image, MyApplication.getDefaultOptions());
			iv_image.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 获取跟贴
	 */
	private void fetchComments(int skip) {
		swiperefresh.setRefreshing(true);
		BmobQuery<Comment> query = new BmobQuery<Comment>();
		query.addWhereEqualTo("post", currentPost);
		query.include("user");
		query.setLimit(COMMENTS_PER_PAGE);
		query.setSkip(skip);
		query.findObjects(getApplicationContext(), new FindListener<Comment>() {

			@Override
			public void onError(int code, String msg) {
				handler.sendEmptyMessage(FETCH_FAILED);
				ToastUtils.makeText(getApplicationContext(), "获取回帖失败..错误码："
						+ code + " 错误信息：" + msg);
			}

			@Override
			public void onSuccess(List<Comment> list) {
				comments = list;
				handler.sendEmptyMessage(FETCH_SUCCESS);
			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (BmobUtils.isAdmin(this)) {
			getMenuInflater().inflate(R.menu.post, menu);
			if (currentPost.isTop()) {
				menu.add(0, R.id.menu_item_cancel_top, 1, "取消置顶");
			} else {
				menu.add(0, R.id.menu_item_set_top, 1, "设置置顶");
			}
			if (currentPost.isJp()) {
				menu.add(0, R.id.menu_item_cancel_jp, 1, "取消精品");
			} else {
				menu.add(0, R.id.menu_item_set_jp, 1, "设置精品");
			}
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_item_delete:
			currentPost.delete(getApplicationContext(), new DeleteListener() {

				@Override
				public void onSuccess() {
					ToastUtils.makeText(getApplicationContext(), "删除成功");
					finish();
				}

				@Override
				public void onFailure(int code, String msg) {
					ToastUtils
							.makeErrorText(getApplicationContext(), code, msg);
				}
			});
			break;
		case R.id.menu_item_set_top:
			setTop(true);
			break;
		case R.id.menu_item_cancel_top:
			setTop(false);
			break;
		case R.id.menu_item_set_jp:
			setJp(true);
			break;
		case R.id.menu_item_cancel_jp:
			setJp(false);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setJp(boolean flag) {
		currentPost.setJp(flag);
		currentPost.update(getApplicationContext(), new UpdateListener() {

			@Override
			public void onSuccess() {
				ToastUtils.makeText(getApplicationContext(), "操作成功");
			}

			@Override
			public void onFailure(int code, String msg) {
				ToastUtils.makeErrorText(getApplicationContext(), code, msg);
			}
		});
	}

	private void setTop(boolean flag) {
		currentPost.setTop(flag);
		currentPost.update(getApplicationContext(), new UpdateListener() {

			@Override
			public void onSuccess() {
				ToastUtils.makeText(getApplicationContext(), "置顶成功");
			}

			@Override
			public void onFailure(int code, String msg) {
				ToastUtils.makeErrorText(getApplicationContext(), code, msg);
			}
		});
	}

	@Override
	protected void setThemeColor(int color) {
		super.setThemeColor(color);
		rl_post.setBackgroundColor(color);
		view_up.setBackgroundColor(color);
		btn_more.setBackgroundColor(color);
		et_content.setTextColor(color);
		ib_send.setColorFilter(color);
		btn_previous.setBackgroundColor(color);
		btn_next.setBackgroundColor(color);
	}
//
//	private class CommentAdapter extends BaseAdapter {
//
//		@Override
//		public int getCount() {
//			return comments.size();
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			ViewHolder holder = null;
//			if (convertView == null) {
//				holder = new ViewHolder();
//				convertView = getLayoutInflater().inflate(
//						R.layout.item_comment, parent, false);
//				holder.biv_head = (BezelImageView) convertView
//						.findViewById(R.id.biv_head);
//				holder.tv_username = (TextView) convertView
//						.findViewById(R.id.tv_username);
//				holder.tv_content = (TextView) convertView
//						.findViewById(R.id.tv_content);
//				convertView.setTag(holder);
//			} else {
//				holder = (ViewHolder) convertView.getTag();
//			}
//			Comment comment = comments.get(position);
//			User user = comment.getUser();
//			holder.tv_content.setText(comment.getContent());
//			holder.tv_username.setText("" + user.getUsername());
//			if (!TextUtils.isEmpty(user.getAvatar())) {
//				ImageLoader.getInstance().displayImage(user.getAvatar(),
//						biv_head, MyApplication.getHeadOptions());
//			}
//			return convertView;
//		}
//
//		@Override
//		public Object getItem(int position) {
//			return null;
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return position;
//		}
//	}

//	private static class ViewHolder {
//		BezelImageView biv_head;
//		TextView tv_username;
//		TextView tv_content;
//
//	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.biv_head:
			Intent intent = new Intent(this, PersonInfoActivity.class);
			intent.putExtra(Constants.user, currentPost.getUser());
			startActivity(intent);
			break;
		case R.id.ib_send:
			// 回帖
			newComment();
			break;
		case R.id.btn_more:
			if (popupwindow != null && popupwindow.isShowing()) {
				dismissPopup();
			} else {
				showPopupWindow();
			}
			break;
		case R.id.btn_previous:
			COMMENTS_SKIP -= COMMENTS_PER_PAGE;
			if (COMMENTS_SKIP <= 0) {
				ToastUtils.makeText(this, "已经是第一页了！");
			} else {
				fetchComments(COMMENTS_SKIP);
			}
			break;
		case R.id.btn_next:
			COMMENTS_SKIP += COMMENTS_PER_PAGE;
			fetchComments(COMMENTS_SKIP);
			break;
		default:
			break;
		}
	}

	private void newComment() {

		String content = et_content.getText().toString().trim();
		if (TextUtils.isEmpty(content)) {
			ToastUtils.makeText(getApplicationContext(), "不能回复空的消息");
			return;
		}
		final Comment comment = new Comment();
		comment.setContent(content);
		comment.setPost(currentPost);
		comment.setUser(BmobUser.getCurrentUser(getApplicationContext(),
				User.class));
		comment.save(getApplicationContext(), new SaveListener() {

			@Override
			public void onSuccess() {
				BmobRelation relation = new BmobRelation();
				relation.add(comment);

				currentPost.setComments(relation);
				currentPost.update(getApplicationContext(),
						new UpdateListener() {

							@Override
							public void onSuccess() {
								ToastUtils.makeText(getApplicationContext(),
										"回帖成功！");
								fetchComments(0);
								et_content.setText("");
							}

							@Override
							public void onFailure(int code, String msg) {
								ToastUtils.makeErrorText(
										getApplicationContext(), code, msg);
							}
						});
			}

			@Override
			public void onFailure(int code, String msg) {
				ToastUtils.makeErrorText(getApplicationContext(), code, msg);
			}
		});

	}

	private void showPopupWindow() {
		popupwindow = new PopupWindow(view_popup, -2, -2);
		// 动画播放有一个前提条件： 窗体必须要有背景资源。 如果窗体没有背景，动画就播放不出来。
		popupwindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		int[] location = new int[2];
		btn_more.getLocationInWindow(location);
		popupwindow.showAtLocation(btn_more, Gravity.END + Gravity.TOP, 120,
				location[1] + 200);
		ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f,
				Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f);
		sa.setDuration(200);
		AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
		aa.setDuration(200);
		AnimationSet set = new AnimationSet(false);
		set.addAnimation(aa);
		set.addAnimation(sa);
		view_popup.startAnimation(set);
	}

	private void dismissPopup() {
		if (popupwindow != null && popupwindow.isShowing()) {
			popupwindow.dismiss();
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (v == rl_post) {
			Log.i(TAG, "ontouchevent");
			mDetector.onTouchEvent(event);
			return true;
		}
		return false;
	}
}
