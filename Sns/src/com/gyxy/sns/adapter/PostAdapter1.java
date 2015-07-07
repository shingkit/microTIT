package com.gyxy.sns.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gyxy.sns.Constants;
import com.gyxy.sns.MyApplication;
import com.gyxy.sns.R;
import com.gyxy.sns.model.Post;
import com.gyxy.sns.ui.activity.PersonInfoActivity;
import com.gyxy.sns.ui.widget.BezelImageView;
import com.gyxy.sns.utils.TimeUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PostAdapter1 extends BaseAdapter {
	private Context context;

	private List<Post> list;

	public PostAdapter1(Context context, List<Post> posts) {
		this.context = context;
		this.list = posts;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {

			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_post, parent, false);
			holder = new ViewHolder();
			holder.biv_head = (BezelImageView) convertView
					.findViewById(R.id.biv_head);
			holder.tv_updateAt = (TextView) convertView
					.findViewById(R.id.tv_updateAt);
			holder.tv_username = (TextView) convertView
					.findViewById(R.id.tv_username);
			holder.tv_title = (TextView) convertView
					.findViewById(R.id.tv_title);
			holder.tv_content = (TextView) convertView
					.findViewById(R.id.tv_content);
			holder.iv_image = (ImageView) convertView
					.findViewById(R.id.iv_image);
			holder.tv_top = (TextView) convertView.findViewById(R.id.tv_top);
			holder.tv_jp = (TextView) convertView.findViewById(R.id.tv_jp);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final Post post = list.get(position);

		if (!TextUtils.isEmpty(post.getTitle())) {
			holder.tv_title.setText(post.getTitle());
			holder.tv_title.setVisibility(View.VISIBLE);
		}
		if (!TextUtils.isEmpty(post.getContent())) {
			holder.tv_content.setText(post.getContent());
			holder.tv_content.setVisibility(View.VISIBLE);
		}
		if (post.getImage() != null
				&& post.getImage().getFileUrl(context) != null) {
			ImageLoader.getInstance().displayImage(
					post.getImage().getFileUrl(context), holder.iv_image,
					MyApplication.getThumbnailOptions());
			holder.iv_image.setVisibility(View.VISIBLE);
		} else {// ∑¿÷πconvertView÷ÿ”√µº÷¬Õº∆¨º”‘ÿ¥ÌŒÛ
			holder.iv_image.setVisibility(View.GONE);
		}

		holder.tv_updateAt.setText(TimeUtil.getDescriptionTimeFromString(post
				.getUpdatedAt()));
		if (post.isTop()) {
			holder.tv_top.setVisibility(View.VISIBLE);
		} else {
			holder.tv_top.setVisibility(View.GONE);
		}
		if (post.isJp()) {
			holder.tv_jp.setVisibility(View.VISIBLE);
		} else {
			holder.tv_jp.setVisibility(View.GONE);
		}
		holder.tv_username.setText(post.getUser().getUsername());
		if (!TextUtils.isEmpty(post.getUser().getAvatar())) {
			ImageLoader.getInstance().displayImage(post.getUser().getAvatar(),
					holder.biv_head, MyApplication.getHeadOptions());
		}
		holder.biv_head.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, PersonInfoActivity.class);
				intent.putExtra(Constants.user, post.getUser());
				context.startActivity(intent);
			}
		});
		return convertView;
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class ViewHolder {
		BezelImageView biv_head;
		TextView tv_title;
		TextView tv_updateAt;
		TextView tv_username;
		TextView tv_top;
		TextView tv_jp;
		TextView tv_content;
		ImageView iv_image;

	}
}
