package com.gyxy.sns.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.db.BmobDB;

import com.gyxy.sns.MyApplication;
import com.gyxy.sns.R;
import com.gyxy.sns.ui.widget.BezelImageView;
import com.gyxy.sns.utils.TimeUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 会话适配器
 * 
 * @ClassName: MessageRecentAdapter
 * @Description: TODO
 * @author smile
 * @date 2014-6-7 下午2:34:10
 */
public class MessageRecentAdapter extends ArrayAdapter<BmobRecent> implements
		Filterable {

	private LayoutInflater inflater;
	private List<BmobRecent> mData;
	private Context mContext;

	public MessageRecentAdapter(Context context, int textViewResourceId,
			List<BmobRecent> objects) {
		super(context, textViewResourceId, objects);
		inflater = LayoutInflater.from(context);
		this.mContext = context;
		mData = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final BmobRecent item = mData.get(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_conversation, parent,
					false);
		}
		BezelImageView biv_head = ViewHolder.get(convertView, R.id.biv_head);
		TextView tv_username = ViewHolder.get(convertView, R.id.tv_username);
		TextView tv_recent_msg = ViewHolder
				.get(convertView, R.id.tv_recent_msg);
		TextView tv_recent_time = ViewHolder.get(convertView,
				R.id.tv_recent_time);
		TextView tv_recent_unread = ViewHolder.get(convertView,
				R.id.tv_recent_unread);

		// 填充数据
		String avatar = item.getAvatar();
		if (!TextUtils.isEmpty(avatar)) {
			ImageLoader.getInstance().displayImage(avatar, biv_head,
					MyApplication.getHeadOptions());
		} else {
			biv_head.setImageResource(R.drawable.person_image_empty);
		}

		tv_username.setText(item.getUserName());
		tv_recent_time.setText(TimeUtil.getChatTime(item.getTime()));
		// 显示内容
		tv_recent_msg.setText(item.getMessage());

		int num = BmobDB.create(mContext).getUnreadCount(item.getTargetid());
		if (num > 0) {
			tv_recent_unread.setVisibility(View.VISIBLE);
			tv_recent_unread.setText(num + "");
		} else {
			tv_recent_unread.setVisibility(View.GONE);
		}
		return convertView;
	}

}
