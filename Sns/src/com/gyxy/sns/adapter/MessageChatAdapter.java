package com.gyxy.sns.adapter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;

import com.gyxy.sns.Constants;
import com.gyxy.sns.MyApplication;
import com.gyxy.sns.R;
import com.gyxy.sns.model.User;
import com.gyxy.sns.ui.activity.PersonInfoActivity;
import com.gyxy.sns.utils.TimeUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 聊天适配器
 * 
 * @ClassName: MessageChatAdapter
 * @Description: TODO
 * @author smile
 * @date 2014-5-28 下午5:34:07
 */
public class MessageChatAdapter extends BaseListAdapter<BmobMsg> {

	// 8种Item的类型
	// 文本
	private final int TYPE_RECEIVER_TXT = 0;
	private final int TYPE_SEND_TXT = 1;

	String currentObjectId = "";

	DisplayImageOptions options;

	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

	public MessageChatAdapter(Context context, List<BmobMsg> msgList) {
		// TODO Auto-generated constructor stub
		super(context, msgList);
		currentObjectId = BmobUserManager.getInstance(context)
				.getCurrentUserObjectId();

	}

	@Override
	public int getItemViewType(int position) {
		BmobMsg msg = list.get(position);

		return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_TXT
				: TYPE_RECEIVER_TXT;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	private View createViewByType(BmobMsg message, int position) {
		int type = message.getMsgType();

		return getItemViewType(position) == TYPE_RECEIVER_TXT ? mInflater
				.inflate(R.layout.item_chat_received_message, null) : mInflater
				.inflate(R.layout.item_chat_sent_message, null);
	}

	@Override
	public View bindView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final BmobMsg item = list.get(position);
		if (convertView == null) {
			convertView = createViewByType(item, position);
		}
		// 文本类型
		ImageView iv_avatar = ViewHolder.get(convertView, R.id.iv_avatar);
		final TextView tv_send_status = ViewHolder.get(convertView,
				R.id.tv_send_status);// 发送状态
		TextView tv_time = ViewHolder.get(convertView, R.id.tv_time);
		TextView tv_message = ViewHolder.get(convertView, R.id.tv_message);
		final ProgressBar progress_load = ViewHolder.get(convertView,
				R.id.progress_load);// 进度条

		// 点击头像进入个人资料
		String avatar = item.getBelongAvatar();
		if (avatar != null && !avatar.equals("")) {// 加载头像-为了不每次都加载头像
			ImageLoader.getInstance().displayImage(avatar, iv_avatar,
					MyApplication.getDefaultOptions(), animateFirstListener);
		} else {
			iv_avatar.setImageResource(R.drawable.person_image_empty);
		}

		iv_avatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, PersonInfoActivity.class);
				if (getItemViewType(position) == TYPE_RECEIVER_TXT) {
					intent.putExtra("from", "other");
					intent.putExtra("username", item.getBelongUsername());
				} else {
					intent.putExtra(Constants.user, BmobUserManager
							.getInstance(mContext).getCurrentUser(User.class));
				}
				mContext.startActivity(intent);
			}
		});

		tv_time.setText(TimeUtil.getChatTime(Long.parseLong(item.getMsgTime())));

		if (getItemViewType(position) == TYPE_SEND_TXT
		// ||getItemViewType(position)==TYPE_SEND_IMAGE//图片单独处理
		) {// 只有自己发送的消息才有重发机制
			// 状态描述
			if (item.getStatus() == BmobConfig.STATUS_SEND_SUCCESS) {// 发送成功
				progress_load.setVisibility(View.INVISIBLE);

				tv_send_status.setVisibility(View.VISIBLE);
				tv_send_status.setText("已发送");

			} else if (item.getStatus() == BmobConfig.STATUS_SEND_FAIL) {// 服务器无响应或者查询失败等原因造成的发送失败，均需要重发
				progress_load.setVisibility(View.INVISIBLE);
				tv_send_status.setVisibility(View.INVISIBLE);

			} else if (item.getStatus() == BmobConfig.STATUS_SEND_RECEIVERED) {// 对方已接收到
				progress_load.setVisibility(View.INVISIBLE);

				tv_send_status.setVisibility(View.VISIBLE);
				tv_send_status.setText("已阅读");

			}
		}
		// 根据类型显示内容
		final String text = item.getContent();
		if (item.getMsgType() == BmobConfig.TYPE_TEXT) {

			tv_message.setText(text);
		}

		return convertView;
	}

	/**
	 * 获取图片的地址--
	 * 
	 * @Description: TODO
	 * @param @param item
	 * @param @return
	 * @return String
	 * @throws
	 */
//	private String getImageUrl(BmobMsg item) {
//		String showUrl = "";
//		String text = item.getContent();
//		if (item.getBelongId().equals(currentObjectId)) {//
//			if (text.contains("&")) {
//				showUrl = text.split("&")[0];
//			} else {
//				showUrl = text;
//			}
//		} else {// 如果是收到的消息，则需要从网络下载
//			showUrl = text;
//		}
//		return showUrl;
//	}

	private static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

}
