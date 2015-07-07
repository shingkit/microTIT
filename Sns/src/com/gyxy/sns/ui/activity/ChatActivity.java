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
		// 设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
		intentFilter.setPriority(5);
		registerReceiver(receiver, intentFilter);
	}

	private void send() {
		String msg = et_content.getText().toString().trim();
		if (TextUtils.isEmpty(msg)) {
			ToastUtils.makeText(getApplicationContext(), "不能发生空消息");
			return;
		}

		message = BmobMsg.createTextSendMsg(this, targetId, msg);
		// 不带监听回调，默认发送完成，将数据保存到本地消息表和最近会话表中
		// manager.sendTextMessage(targetUser, message);
		// 带监听回调
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
			ToastUtils.makeText(context, "不能发生空消息");
			return;
		}

		BmobMsg message = BmobMsg.createTextSendMsg(context, user.getObjectId(), msg);
		// 不带监听回调，默认发送完成，将数据保存到本地消息表和最近会话表中
		// manager.sendTextMessage(targetUser, message);
		// 带监听回调
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
	 * 加载消息历史，从数据库中读出
	 */
	private List<BmobMsg> initMsgData() {
		List<BmobMsg> list = BmobDB.create(this).queryMessages(targetId,
				MsgPagerNum);
		return list;
	}

	/**
	 * 新消息广播接收者
	 * 
	 */
	private class NewBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String from = intent.getStringExtra("fromId");
			String msgId = intent.getStringExtra("msgId");
			String msgTime = intent.getStringExtra("msgTime");
			// 收到这个广播的时候，message已经在消息表中，可直接获取
			if (TextUtils.isEmpty(from) && TextUtils.isEmpty(msgId)
					&& TextUtils.isEmpty(msgTime)) {
				BmobMsg msg = BmobChatManager.getInstance(ChatActivity.this)
						.getMessage(msgId, msgTime);
				if (!from.equals(targetId))// 如果不是当前正在聊天对象的消息，不处理
					return;
				// 添加到当前页面
				// mAdapter.add(msg);
				// // 定位
				// mListView.setSelection(mAdapter.getCount() - 1);
				// 取消当前聊天对象的未读标示
				BmobDB.create(ChatActivity.this).resetUnread(targetId);
			}
			// 记得把广播给终结掉
			abortBroadcast();
		}
	}

	/**
	 * 刷新界面
	 * 
	 * @Title: refreshMessage
	 * @Description: TODO
	 * @param @param message
	 * @return void
	 * @throws
	 */
	private void refreshMessage(BmobMsg msg) {
		// 更新界面
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
