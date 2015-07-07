package com.gyxy.sns.receiver;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.config.BmobConstant;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.inteface.OnReceiveListener;
import cn.bmob.im.util.BmobJsonUtil;
import cn.bmob.im.util.BmobLog;

import com.gyxy.sns.R;
import com.gyxy.sns.ui.activity.MessageAndPushActivity;
import com.gyxy.sns.utils.NetWorkUtils;
import com.gyxy.sns.utils.PrefUtils;

public class MyMessageReceiver extends BroadcastReceiver {

	// �¼�����
	public static ArrayList<EventListener> ehList = new ArrayList<EventListener>();

	public static final int NOTIFY_ID = 0x000;
	public static int mNewNum = 0;//
	BmobUserManager userManager;
	BmobChatUser currentUser;

	// ������뷢���Զ����ʽ����Ϣ����ʹ��sendJsonMessage����������Json��ʽ���ַ�����Ȼ���㰴�ո�ʽ�Լ�����������

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String json = intent.getStringExtra("msg");
		BmobLog.i("�յ���message = " + json);

		userManager = BmobUserManager.getInstance(context);
		currentUser = userManager.getCurrentUser();
		boolean isNetConnected = NetWorkUtils.isNetAvailable(context);
		if (isNetConnected) {
			parseMessage(context, json);
		} else {
			for (int i = 0; i < ehList.size(); i++)
				((EventListener) ehList.get(i)).onNetChange(isNetConnected);
		}
	}

	/**
	 * ����Json�ַ���
	 * 
	 * @Title: parseMessage
	 * @Description: TODO
	 * @param @param context
	 * @param @param json
	 * @return void
	 * @throws
	 */
	private void parseMessage(final Context context, String json) {
		JSONObject jo;
		try {
			jo = new JSONObject(json);
			String tag = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TAG);
			if (tag.equals(BmobConfig.TAG_OFFLINE)) {// ����֪ͨ
				if (currentUser != null) {
					if (ehList.size() > 0) {// �м�����ʱ�򣬴�����ȥ
						for (EventListener handler : ehList)
							handler.onOffline();
					} else {
						// �������
						if(userManager!=null)
							userManager.logout();
					}
				}
			} else {
				String fromId = BmobJsonUtil.getString(jo,
						BmobConstant.PUSH_KEY_TARGETID);
				// ������Ϣ���շ���ObjectId--Ŀ���ǽ�����˻���½ͬһ�豸ʱ���޷����յ��ǵ�ǰ��½�û�����Ϣ��
				final String toId = BmobJsonUtil.getString(jo,
						BmobConstant.PUSH_KEY_TOID);
				String msgTime = BmobJsonUtil.getString(jo,
						BmobConstant.PUSH_READED_MSGTIME);
				if (fromId != null
						&& !BmobDB.create(context, toId).isBlackUser(fromId)) {// ����Ϣ���ͷ���Ϊ�������û�
					if (TextUtils.isEmpty(tag)) {// ��Я��tag��ǩ--�˿ɽ���İ���˵���Ϣ
						BmobChatManager.getInstance(context).createReceiveMsg(
								json, new OnReceiveListener() {

									@Override
									public void onSuccess(BmobMsg msg) {
										// TODO Auto-generated method stub
										if (ehList.size() > 0) {// �м�����ʱ�򣬴�����ȥ
											for (int i = 0; i < ehList.size(); i++) {
												((EventListener) ehList.get(i))
														.onMessage(msg);
											}
										} else {
											if (currentUser != null
													&& currentUser
															.getObjectId()
															.equals(toId)) {// ��ǰ��½�û����ڲ���Ҳ���ڽ��շ�id
												mNewNum++;
												showMsgNotify(context, msg);
											}
										}
									}

									@Override
									public void onFailure(int code, String arg1) {
										// TODO Auto-generated method stub
										BmobLog.i("��ȡ���յ���Ϣʧ�ܣ�" + arg1);
									}
								});

					} else {// ��tag��ǩ
						if (tag.equals(BmobConfig.TAG_ADD_CONTACT)) {
							// ���������������أ������º�̨��δ���ֶ�
							BmobInvitation message = BmobChatManager
									.getInstance(context).saveReceiveInvite(
											json, toId);
							if (currentUser != null) {// �е�½�û�
								if (toId.equals(currentUser.getObjectId())) {
									if (ehList.size() > 0) {// �м�����ʱ�򣬴�����ȥ
										for (EventListener handler : ehList)
											handler.onAddUser(message);
									} else {
										showOtherNotify(context,
												message.getFromname(), toId,
												message.getFromname()
														+ "������Ӻ���",
												MessageAndPushActivity.class);
									}
								}
							}
						} else if (tag.equals(BmobConfig.TAG_READED)) {// �Ѷ���ִ
							String conversionId = BmobJsonUtil.getString(jo,
									BmobConstant.PUSH_READED_CONVERSIONID);
							if (currentUser != null) {
								// ����ĳ����Ϣ��״̬
								BmobChatManager.getInstance(context)
										.updateMsgStatus(conversionId, msgTime);
								if (toId.equals(currentUser.getObjectId())) {
									if (ehList.size() > 0) {// �м�����ʱ�򣬴�����ȥ--�����޸Ľ���
										for (EventListener handler : ehList)
											handler.onReaded(conversionId,
													msgTime);
									}
								}
							}
						}
					}
				} else {// �ں������ڼ����е���Ϣ��Ӧ����Ϊ�Ѷ�����Ȼ��ȡ��������֮���ֿ��Բ�ѯ�ĵ�
					BmobChatManager.getInstance(context).updateMsgReaded(true,
							fromId, msgTime);
					BmobLog.i("����Ϣ���ͷ�Ϊ�������û�");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			// �����ȡ�����п�����web��̨���͸��ͻ��˵���Ϣ��Ҳ�п����ǿ������Զ��巢�͵���Ϣ����Ҫ���������н����ʹ���
			BmobLog.i("parseMessage����" + e.getMessage());
		}
	}

	/**
	 * ��ʾ��������Ϣ��֪ͨ
	 * 
	 * @Title: showNotify
	 * @return void
	 * @throws
	 */
	public void showMsgNotify(Context context, BmobMsg msg) {
		// ����֪ͨ��
		int icon = R.drawable.ic_launcher;
		String trueMsg = "";

		trueMsg = msg.getContent();

		CharSequence tickerText = msg.getBelongUsername() + ":" + trueMsg;
		String contentTitle = msg.getBelongUsername() + " (" + mNewNum
				+ "������Ϣ)";

		Intent intent = new Intent(context, MessageAndPushActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		boolean isAllowVoice = PrefUtils.isSoundOpen(context);
		boolean isAllowVibrate = PrefUtils.isViberateOpen(context);

 
		
		BmobNotifyManager.getInstance(context).showNotifyWithExtras(
				isAllowVoice, isAllowVibrate, icon, tickerText.toString(),
				contentTitle, tickerText.toString(), intent);
	}

	/**
	 * ��ʾ����Tag��֪ͨ showOtherNotify
	 */
	public void showOtherNotify(Context context, String username, String toId,
			String ticker, Class<?> cls) {
		boolean isAllowVoice = PrefUtils.isSoundOpen(context);

		boolean isAllowVibrate = PrefUtils.isViberateOpen(context);
		if (currentUser != null && currentUser.getObjectId().equals(toId)) {
			// ͬʱ����֪ͨ
			BmobNotifyManager.getInstance(context).showNotify(isAllowVoice,
					isAllowVibrate, R.drawable.ic_launcher, ticker, username,
					ticker.toString(), MessageAndPushActivity.class);
		}
	}

}
