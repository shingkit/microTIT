package com.gyxy.sns.receiver;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gyxy.sns.R;
import com.gyxy.sns.db.dao.PushDao;
import com.gyxy.sns.ui.activity.MessageAndPushActivity;
import com.gyxy.sns.utils.LogUtils;
import com.gyxy.sns.utils.PrefUtils;

public class PushReceiver extends BroadcastReceiver {

	private static final String TAG = "PushReceiver";
	private String title;
	private String content;

	@Override
	public void onReceive(Context context, Intent intent) {
		// if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
		if (intent.getAction().equals("cn.bmob.push.action.MESSAGE")) {
			Log.d("bmob", "客户端收到推送内容：" + intent.getStringExtra("msg"));
			String jsonStr = intent.getStringExtra("msg");
			try {
				JSONObject json = new JSONObject(jsonStr);
				title = (String) json.get("title");
				content = (String) json.get("content");
				NotificationManager nm = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				Notification notification = new Notification(
						R.drawable.ic_launcher, "接收到一条消息",
						System.currentTimeMillis());
				Intent intent2 = new Intent(context,MessageAndPushActivity.class);
				PendingIntent pendingIntent = PendingIntent.getActivity(context,
						100, intent2, PendingIntent.FLAG_ONE_SHOT);
				notification.setLatestEventInfo(context, // the context to use
						title,
						// the title for the notification
						content, // the details to display in the notification
						pendingIntent); // the contentIntent (see above)
				if (PrefUtils.isSoundOpen(context)
						&& PrefUtils.isViberateOpen(context))
					notification.defaults = Notification.DEFAULT_ALL;
				else if (PrefUtils.isSoundOpen(context)) {
					notification.defaults = Notification.DEFAULT_SOUND&Notification.DEFAULT_LIGHTS;
				} else if(PrefUtils.isViberateOpen(context)){
					notification.defaults = Notification.DEFAULT_VIBRATE&Notification.DEFAULT_LIGHTS;
				}else{
					notification.defaults=Notification.DEFAULT_LIGHTS;
				}
				nm.notify(100, notification);
				PushDao dao=new PushDao(context);
				dao.add(title, content);
			} catch (JSONException e) {
				e.printStackTrace();
				LogUtils.E(TAG, "不是push消息");
			}

		}

	}

}
