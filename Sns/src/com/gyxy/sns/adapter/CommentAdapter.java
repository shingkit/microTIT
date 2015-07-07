package com.gyxy.sns.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gyxy.sns.MyApplication;
import com.gyxy.sns.R;
import com.gyxy.sns.model.Comment;
import com.gyxy.sns.model.User;
import com.gyxy.sns.ui.widget.BezelImageView;
import com.gyxy.sns.utils.TimeUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CommentAdapter extends BaseListAdapter<Comment> {

	public CommentAdapter(Context context, List<Comment> list) {
		super(context, list);
	}

	@Override
	public View bindView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_comment, parent,
					false);
		}
		BezelImageView biv_head = ViewHolder.get(convertView, R.id.biv_head);
		TextView tv_username = ViewHolder.get(convertView, R.id.tv_username);
		TextView tv_createdAt = ViewHolder.get(convertView, R.id.tv_createdAt);
		TextView tv_content = ViewHolder.get(convertView, R.id.tv_content);

		Comment comment = list.get(position);
		User user = comment.getUser();
		tv_createdAt.setText(TimeUtil.getDescriptionTimeFromString(comment
				.getCreatedAt()));
		tv_content.setText(comment.getContent());
		tv_username.setText("" + user.getUsername());
		if (!TextUtils.isEmpty(user.getAvatar())) {
			ImageLoader.getInstance().displayImage(user.getAvatar(), biv_head,
					MyApplication.getHeadOptions());
		} else {
			biv_head.setImageResource(R.drawable.person_image_empty);
		}

		return convertView;
	}

}
