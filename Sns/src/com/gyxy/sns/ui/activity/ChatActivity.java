package com.gyxy.sns.ui.activity;

import java.util.ArrayList;
import java.util.List;

import com.gyxy.sns.Constants;
import com.gyxy.sns.R;
import com.gyxy.sns.adapter.MessageChatAdapter;
import com.gyxy.sns.model.User;
import com.gyxy.sns.utils.ToastUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.PushListener;

public class ChatActivity extends SwipeBackActivity implements OnClickListener {

	private static final int MsgPagerNum = 20;
	private static BmobChatManager manager;
	private User targetUser;
	private String targetId;
	private NewBroadcastReceiver receiver;
	private List<BmobMsg> list = new ArrayList<BmobMsg>();
	private EditText et_content;
	private ImageButton ib_send;
	private MessageChatAdapter adapter;
	private ListView listview;
	private BmobMsg message;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		manager = BmobChatManager.getInstance(this);
		targetUser = (User) getIntent().getSerializableExtra(Constants.user);
		targetId = targetUser.getObjectId();
		getToolbar().setTitle(targetUser.getUsername());
		initNewMessageBroadCast();
		initView();
	}

	private void initView() {
		et_content = (EditText) findViewById(R.id.et_content);
		listview = (ListView) findViewById(R.id.listview);
		adapter = new MessageChatAdapter(getApplicationContext(), initMsgData());
		listview.setAdapter(adapter);
		ib_send = (ImageButton) findViewById(R.id.ib_send);
		ib_send.setOnClickListener(this);
	}

	private void initNewMessageBroadCast() {
		receiver = new NewBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(
				BmobConfig.BROADCAST_NEW_MESSAGE);
		// ���ù㲥�����ȼ������Mainacitivity,���������Ϣ����ʱ��������chatҳ�棬ֱ����ʾ��Ϣ����������ʾ��Ϣδ��
		intentFilter.setPriority(5);
		registerReceiver(receiver, intentFilter);
	}

	private void send() {
		String msg = et_content.getText().toString().trim();
		if (TextUtils.isEmpty(msg)) {
			ToastUtils.makeText(getApplicationContext(), "���ܷ�������Ϣ");
			return;
		}

		message = BmobMsg.createTextSendMsg(this, targetId, msg);
		// ���������ص���Ĭ�Ϸ�����ɣ������ݱ��浽������Ϣ�������Ự����
		// manager.sendTextMessage(targetUser, message);
		// �������ص�
		manager.sendTextMessage(targetUser, message, new PushListener() {

			@Override
			public void onFailure(int arg0, String arg1) {

			}

			@Override
			public void onSuccess() {

			}

		});
	}
	
	public static  void send(Context context,User user,String msg) {
		if (TextUtils.isEmpty(msg)) {
			ToastUtils.makeText(context, "���ܷ�������Ϣ");
			return;
		}

		BmobMsg message = BmobMsg.createTextSendMsg(context, user.getObjectId(), msg);
		// ���������ص���Ĭ�Ϸ�����ɣ������ݱ��浽������Ϣ�������Ự����
		// manager.sendTextMessage(targetUser, message);
		// �������ص�
		manager.sendTextMessage(user, message, new PushListener() {

			@Override
			public void onFailure(int arg0, String arg1) {

			}

			@Override
			public void onSuccess() {

			}

		});
	}

	/**
	 * ������Ϣ��ʷ�������ݿ��ж���
	 */
	private List<BmobMsg> initMsgData() {
		List<BmobMsg> list = BmobDB.create(this).queryMessages(targetId,
				MsgPagerNum);
		return list;
	}

	/**
	 * ����Ϣ�㲥������
	 * 
	 */
	private class NewBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String from = intent.getStringExtra("fromId");
			String msgId = intent.getStringExtra("msgId");
			String msgTime = intent.getStringExtra("msgTime");
			// �յ�����㲥��ʱ��message�Ѿ�����Ϣ���У���ֱ�ӻ�ȡ
			if (TextUtils.isEmpty(from) && TextUtils.isEmpty(msgId)
					&& TextUtils.isEmpty(msgTime)) {
				BmobMsg msg = BmobChatManager.getInstance(ChatActivity.this)
						.getMessage(msgId, msgTime);
				if (!from.equals(targetId))// ������ǵ�ǰ��������������Ϣ��������
					return;
				// ��ӵ���ǰҳ��
				// mAdapter.add(msg);
				// // ��λ
				// mListView.setSelection(mAdapter.getCount() - 1);
				// ȡ����ǰ��������δ����ʾ
				BmobDB.create(ChatActivity.this).resetUnread(targetId);
			}
			// �ǵðѹ㲥���ս��
			abortBroadcast();
		}
	}

	/**
	 * ˢ�½���
	 * 
	 * @Title: refreshMessage
	 * @Description: TODO
	 * @param @param message
	 * @return void
	 * @throws
	 */
	private void refreshMessage(BmobMsg msg) {
		// ���½���
		adapter.add(msg);
		listview.setSelection(adapter.getCount() > 1 ? adapter.getCount() - 1
				: 0);
		et_content.setText("");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_send:
			send();
			refreshMessage(message);
			message = null;
			break;

		default:
			break;
		}
	}

	@Override
	protected void setThemeColor(int color) {
		super.setThemeColor(color);
		et_content.setHintTextColor(color);
		ib_send.setColorFilter(color);
	}
}
