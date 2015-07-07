package com.gyxy.sns.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;

import com.gyxy.sns.R;
import com.gyxy.sns.ui.widget.SwipeBackLayout;

//public class SwipeBackActivity extends BaseActivity implements
//		SwipeBackLayout.SwipeBackListener  {
//
//	private static final String TAG = "SwipeBackActivity";
//	private SwipeBackLayout swipeBackLayout;
//	private ImageView ivShadow;
//
//	@Override
//	public void setContentView(int layoutResID) {
//		super.setContentView(getContainer());
//		setDragEdge(DragEdge.LEFT); 
//		View view = LayoutInflater.from(this).inflate(layoutResID, null);
//		swipeBackLayout.addView(view);
//	}
//
// 
//	@Override
//	protected void onStart() {
//		super.onStart();
//		Log.i(TAG, "ONSTART");
//		Toolbar toolBar = getToolbar();
//		if (toolBar != null) {
//			toolBar.setNavigationIcon(R.drawable.ic_back);
//			toolBar.setNavigationOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View view) {
//					finish();
//				}
//			});
//		}
//	}
//
//	private View getContainer() {
//		RelativeLayout container = new RelativeLayout(this);
//		swipeBackLayout = new SwipeBackLayout(this);
//		swipeBackLayout.setOnSwipeBackListener(this);
//		ivShadow = new ImageView(this);
//		ivShadow.setBackgroundColor(getResources().getColor(R.color.black_p50));
//		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
//				LayoutParams.MATCH_PARENT);
//		container.addView(ivShadow, params);
//		container.addView(swipeBackLayout);
//		return container;
//	}
//
//	public void setDragEdge(SwipeBackLayout.DragEdge dragEdge) {
//		swipeBackLayout.setDragEdge(dragEdge);
//	}
//
//	public SwipeBackLayout getSwipeBackLayout() {
//		return swipeBackLayout;
//	}
//
//	@Override
//	public void onViewPositionChanged(float fractionAnchor, float fractionScreen) {
//		ivShadow.setAlpha(1 - fractionScreen);
//	}
//
//	@Override
//	public void startActivity(Intent intent) {
//		super.startActivity(intent);
//		overridePendingTransition(R.anim.swipeback_in, 0);
//	}
//
//	@Override
//	public void finish() {
//		super.finish();
//		overridePendingTransition(0, R.anim.swipeback_out);
//	}
//
//	 
//	
//	
//}

public class SwipeBackActivity extends BaseActivity {
	protected SwipeBackLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(
				R.layout.base, null);
		layout.attachToActivity(this);
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.base_slide_right_in, 0);
	}

	protected void onStart() {
		super.onStart();
		Toolbar toolBar = getToolbar();
		if (toolBar != null) {
			toolBar.setNavigationIcon(R.drawable.ic_back);
			toolBar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					finish();
				}
			});
		}
	};

	// Press the back button in mobile phone
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.base_slide_right_out);
	}
}
