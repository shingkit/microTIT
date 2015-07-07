package com.gyxy.sns.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.gyxy.sns.Constants;
import com.gyxy.sns.MyApplication;
import com.gyxy.sns.R;
import com.gyxy.sns.model.Post;
import com.gyxy.sns.model.User;
import com.gyxy.sns.ui.activity.PersonInfoActivity;
import com.gyxy.sns.ui.widget.BezelImageView;
import com.gyxy.sns.utils.TimeUtil;
import com.gyxy.sns.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PostAdapter extends BaseListAdapter<Post> {

	public PostAdapter(Context context, List<Post> list) {
		super(context, list);
	}

	@Override
	public View bindView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_post, parent, false);
		}
		final Post post = list.get(position);
		final User user = post.getUser();

		BezelImageView biv_head = ViewHolder.get(convertView, R.id.biv_head);
		TextView tv_username = ViewHolder.get(convertView, R.id.tv_username);
		TextView tv_updateAt = ViewHolder.get(convertView, R.id.tv_updateAt);
		TextView tv_title = ViewHolder.get(convertView, R.id.tv_title);
		TextView tv_content = ViewHolder.get(convertView, R.id.tv_content);
		TextView tv_top = ViewHolder.get(convertView, R.id.tv_top);
		TextView tv_jp = ViewHolder.get(convertView, R.id.tv_jp);
		ImageView iv_image = ViewHolder.get(convertView, R.id.iv_image);

		if (!TextUtils.isEmpty(post.getTitle())) {
			tv_title.setText(post.getTitle());
			tv_title.setVisibility(View.VISIBLE);
		}
		if (!TextUtils.isEmpty(post.getContent())) {
			tv_content.setText(post.getContent());
			tv_content.setVisibility(View.VISIBLE);
		}
		if (post.getImage() != null
				&& post.getImage().getFileUrl(mContext) != null) {
			ImageLoader.getInstance().displayImage(
					post.getImage().getFileUrl(mContext), iv_image,
					MyApplication.getThumbnailOptions());
			iv_image.setVisibility(View.VISIBLE);
		} else {// 防止convertView重用导致图片加载错误
			iv_image.setVisibility(View.GONE);
		}

		tv_updateAt.setText(TimeUtil.getDescriptionTimeFromString(post
				.getUpdatedAt()));
		if (post.isTop()) {
			tv_top.setVisibility(View.VISIBLE);
		} else {
			tv_top.setVisibility(View.GONE);
		}
		if (post.isJp()) {
			tv_jp.setVisibility(View.VISIBLE);
		} else {
			tv_jp.setVisibility(View.GONE);
		}
		tv_username.setText(user.getUsername());
		if (!TextUtils.isEmpty(user.getAvatar())) {
			ImageLoader.getInstance().displayImage(user.getAvatar(),
					biv_head, MyApplication.getHeadOptions());
		}else{
			biv_head.setImageResource(R.drawable.person_image_empty);
		}
		biv_head.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (user.getObjectId() == null) {
					ToastUtils.makeText(mContext, "该用户不存在！");
				} else {

					Intent intent = new Intent(mContext,
							PersonInfoActivity.class);
					intent.putExtra(Constants.user, user);
					mContext.startActivity(intent);
				}
			}
		});

		return convertView;
	}

}
