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
 * ����������
 * 
 * @ClassName: MessageChatAdapter
 * @Description: TODO
 * @author smile
 * @date 2014-5-28 ����5:34:07
 */
public class MessageChatAdapter extends BaseListAdapter<BmobMsg> {

	// 8��Item������
	// �ı�
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
		// �ı�����
		ImageView iv_avatar = ViewHolder.get(convertView, R.id.iv_avatar);
		final TextView tv_send_status = ViewHolder.get(convertView,
				R.id.tv_send_status);// ����״̬
		TextView tv_time = ViewHolder.get(convertView, R.id.tv_time);
		TextView tv_message = ViewHolder.get(convertView, R.id.tv_message);
		final ProgressBar progress_load = ViewHolder.get(convertView,
				R.id.progress_load);// ������

		// ���ͷ������������
		String avatar = item.getBelongAvatar();
		if (avatar != null && !avatar.equals("")) {// ����ͷ��-Ϊ�˲�ÿ�ζ�����ͷ��
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
		// ||getItemViewType(position)==TYPE_SEND_IMAGE//ͼƬ��������
		) {// ֻ���Լ����͵���Ϣ�����ط�����
			// ״̬����
			if (item.getStatus() == BmobConfig.STATUS_SEND_SUCCESS) {// ���ͳɹ�
				progress_load.setVisibility(View.INVISIBLE);

				tv_send_status.setVisibility(View.VISIBLE);
				tv_send_status.setText("�ѷ���");

			} else if (item.getStatus() == BmobConfig.STATUS_SEND_FAIL) {// ����������Ӧ���߲�ѯʧ�ܵ�ԭ����ɵķ���ʧ�ܣ�����Ҫ�ط�
				progress_load.setVisibility(View.INVISIBLE);
				tv_send_status.setVisibility(View.INVISIBLE);

			} else if (item.getStatus() == BmobConfig.STATUS_SEND_RECEIVERED) {// �Է��ѽ��յ�
				progress_load.setVisibility(View.INVISIBLE);

				tv_send_status.setVisibility(View.VISIBLE);
				tv_send_status.setText("���Ķ�");

			}
		}
		// ����������ʾ����
		final String text = item.getContent();
		if (item.getMsgType() == BmobConfig.TYPE_TEXT) {

			tv_message.setText(text);
		}

		return convertView;
	}

	/**
	 * ��ȡͼƬ�ĵ�ַ--
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
//		} else {// ������յ�����Ϣ������Ҫ����������
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
