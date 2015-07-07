package com.gyxy.sns.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gyxy.sns.R;
import com.gyxy.sns.model.Push;

public class PushAdapter extends BaseListAdapter<Push> {

	public PushAdapter(Context context, List<Push> list) {
		super(context, list);
	}

	@Override
	public View bindView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_push, parent, false);
		}
		TextView tv_title=ViewHolder.get(convertView, R.id.tv_title);
		TextView tv_content=ViewHolder.get(convertView, R.id.tv_content);
		Push push = list.get(position);
		tv_title.setText(push.getTitle());
		tv_content.setText(push.getContent());
		return convertView;
	}

}
