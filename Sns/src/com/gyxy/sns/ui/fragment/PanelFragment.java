package com.gyxy.sns.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.gc.materialdesign.views.ButtonFloat;
import com.gyxy.sns.Constants;
import com.gyxy.sns.R;
import com.gyxy.sns.adapter.PostAdapter;
import com.gyxy.sns.model.Panel;
import com.gyxy.sns.model.Post;
import com.gyxy.sns.ui.activity.NewPostActivity;
import com.gyxy.sns.ui.activity.PostActivity;
import com.gyxy.sns.utils.CompatUtils;
import com.gyxy.sns.utils.LogUtils;
import com.gyxy.sns.utils.ToastUtils;

public class PanelFragment extends BaseFragment implements OnClickListener {

	public static final String CURRENT_PANEL = "current_panel";

	public static boolean fetchAll = true;
	public static Panel current_panel;

	private SwipeRefreshLayout mSwipeRefreshLayout;
	private ListView mListView;

	private static final int FETCH_POST_SUCCESS = 100;

	public static final int number_per_page = 20;

	private static int skip = 0;

	private static final String TAG = "PanelFragment";

	private ButtonFloat btn_more;

	private PopupWindow popupwindow;

	private View view_popup;
	/**
	 * 数据列表
	 */
	private List<Post> list = new ArrayList<Post>();
	private PostAdapter adapter;

	public PanelFragment() {

	}

	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case FETCH_POST_SUCCESS:
				if (adapter == null) {
					adapter = new PostAdapter(getActivity(), list);
					mListView.setAdapter(adapter);
				} else {
					if (list.size() == 0) {
						ToastUtils.makeText(getActivity(), "没有更多");
					} else {
						adapter = new PostAdapter(getActivity(), list);
						mListView.setAdapter(adapter);
						// adapter.notifyDataSetChanged();
						// LogUtils.i(TAG, "adapter notifyDataSetChanged");

					}
				}
				break;

			default:
				break;
			}
			mSwipeRefreshLayout.setRefreshing(false);
		};
	};

	private ButtonFloat btn_previous;

	private ButtonFloat btn_new_post;

	private ButtonFloat btn_next;
 
	public static PanelFragment newInstance(Panel panel) {
		PanelFragment fragment = new PanelFragment();
		Bundle args = new Bundle();
		args.putSerializable(CURRENT_PANEL, panel);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_panel, container,
				false);
		btn_more = (ButtonFloat) rootView.findViewById(R.id.btn_more);
		btn_more.setOnClickListener(this);
		current_panel = (Panel) getArguments().getSerializable(
				PanelFragment.CURRENT_PANEL);
		// SwipeRefreshLayout
		mSwipeRefreshLayout = (SwipeRefreshLayout) rootView
				.findViewById(R.id.swiperefresh);
		// 设置下拉圆圈渐变颜色
		mSwipeRefreshLayout.setColorSchemeColors(R.color.swipe_color_1,
				R.color.swipe_color_2, R.color.swipe_color_3,
				R.color.swipe_color_4);
		// listview
		mListView = (ListView) rootView.findViewById(android.R.id.list);
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				dismissPopup();
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Post clickedPost = (Post) mListView.getItemAtPosition(position);
				Intent intent = new Intent(getActivity(), PostActivity.class);
				intent.putExtra("post", clickedPost);
				startActivity(intent);
			}

		});
		return rootView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view_popup = View.inflate(getActivity(), R.layout.popup_post, null);

		btn_new_post = (ButtonFloat) view_popup.findViewById(R.id.btn_new_post);
		btn_new_post.setOnClickListener(this);
		btn_previous = (ButtonFloat) view_popup.findViewById(R.id.btn_previous);
		btn_previous.setOnClickListener(this);
		btn_next = (ButtonFloat) view_popup.findViewById(R.id.btn_next);
		btn_next.setOnClickListener(this);

		IntentFilter filter = new IntentFilter();
		filter.addAction(getActivity().getPackageName()
				+ Constants.dismissPopup);
		filter.addAction(getActivity().getPackageName() + Constants.all);
		filter.addAction(getActivity().getPackageName() + Constants.jp);
		// receiver = new MyBroadcastReceiver();
		// getActivity().registerReceiver(receiver, filter);
	}
 
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// 设置适配器
		mListView.setAdapter(adapter);
		// 设置刷新事件
		mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				dismissPopup();
				fetchPost(0);
			}

		});
	}

	@Override
	public void onStart() {
		super.onStart();
		fetchPost(0);
	}

	/**
	 * 获取当前版块帖子列表
	 * 
	 * @param skip
	 */
	private  void fetchPost(int skip) {
		mSwipeRefreshLayout.setRefreshing(true);
		if (fetchAll) {
			// 置顶贴
			list.clear();
			BmobQuery<Post> queryTop = new BmobQuery<Post>();
			queryTop.setLimit(20);
			queryTop.order("-updatedAt");
			queryTop.include("user");
			queryTop.addWhereEqualTo("top", true);
			queryTop.addWhereEqualTo("panel", current_panel);
			queryTop.findObjects(getActivity(), new FindListener<Post>() {

				@Override
				public void onSuccess(List<Post> arg0) {
					list.addAll(0, arg0);
					for (Post post : arg0) {
						System.out.println(post.toString());
					}
					// handler.sendEmptyMessage(FETCH_POST_SUCCESS);
				}

				@Override
				public void onError(int code, String arg1) {
					ToastUtils.makeText(getActivity(), "刷新错误：" + code);
				}

			});
		}

		BmobQuery<Post> query = new BmobQuery<Post>();
		query.setLimit(20);
		if (!fetchAll) {
			query.addWhereEqualTo("jp", true);
		}
		query.order("-updatedAt");
		query.setSkip(skip);
		query.include("user");
		query.addWhereNotEqualTo("top", true);
		query.addWhereEqualTo("panel", current_panel);
		query.findObjects(getActivity(), new FindListener<Post>() {

			@Override
			public void onSuccess(List<Post> arg0) {
				if (!fetchAll) {
					list.clear();
				}
				list.addAll(arg0);

				for (Post post : arg0) {
					System.out.println(post.toString());
				}
				handler.sendEmptyMessage(FETCH_POST_SUCCESS);
			}

			@Override
			public void onError(int code, String msg) {
				ToastUtils.makeErrorText(getActivity(), code, msg);
			}

		});
	}

	private void showPopupWindow() {
		// 更多按钮的旋转动画
		Animation ani = AnimationUtils.loadAnimation(getActivity(),
				R.anim.rotate_right);
		btn_more.startAnimation(ani);

		popupwindow = new PopupWindow(view_popup, -2, -2);// -2=wrap content
		// 动画播放有一个前提条件： 窗体必须要有背景资源。 如果窗体没有背景，动画就播放不出来。
		popupwindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		int[] location = new int[2];
		btn_more.getLocationInWindow(location);
		LogUtils.i(TAG, "btn_more 的位置坐标：" + location[0] + " ," + location[1]);
		popupwindow.showAtLocation(btn_more, Gravity.END + Gravity.BOTTOM,
				CompatUtils.dip2px(getActivity(), 24),
				CompatUtils.dip2px(getActivity(), btn_more.getHeight() + 8));

		AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
		aa.setDuration(200);
		view_popup.startAnimation(aa);

	}

	private void dismissPopup() {
		if (popupwindow != null && popupwindow.isShowing()) {
			AlphaAnimation aa = new AlphaAnimation(1.0f, 0.5f);
			aa.setDuration(200);
			view_popup.startAnimation(aa);
			popupwindow.dismiss();

			Animation ani = AnimationUtils.loadAnimation(getActivity(),
					R.anim.rotate_left);
			btn_more.startAnimation(ani);

		}
	}

	@Override
	public void onStop() {
		dismissPopup();
		super.onStop();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_more:
			// 显示更多按钮 ：发帖 上一页 下一页
			if (popupwindow != null && popupwindow.isShowing()) {
				dismissPopup();
			} else {
				showPopupWindow();
			}
			break;
		case R.id.btn_new_post:
			Intent intent = new Intent(getActivity(), NewPostActivity.class);
			intent.putExtra("current_panel", current_panel);
			startActivity(intent);
			break;
		case R.id.btn_previous:
			skip -= number_per_page;
			if (skip <= 0) {
				ToastUtils.makeText(getActivity(), "已经是第一页了！");
			}
			fetchPost(skip);
			break;
		case R.id.btn_next:
			skip += number_per_page;
			fetchPost(skip);
			break;
		default:
			break;
		}
	}

	@Override
	protected void setThemeColor(int color) {
		super.setThemeColor(color);

		btn_more.setBackgroundColor(color);
		btn_new_post.setBackgroundColor(color);
		btn_previous.setBackgroundColor(color);
		btn_next.setBackgroundColor(color);
	}

}
