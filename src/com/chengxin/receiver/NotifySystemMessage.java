package com.chengxin.receiver;

import android.util.Log;

import com.chengxin.Entity.NotifiyType;
import com.chengxin.Entity.NotifiyVo;
import com.chengxin.service.XmppManager;

public class NotifySystemMessage implements NotifyMessage {
	private static final String TAG = NotifySystemMessage.class.getCanonicalName();
	/**
	 * 聊天服务发送系统消息广播包<br/>
	 * 附加参数: {@link NotifySystemMessage#EXTRAS_NOTIFY_SYSTEM_MESSAGE} 附加参数: {@link NotifySystemMessage#EXTRAS_NOTIFY_SYSTEM_TAG}
	 */
	public static final String ACTION_NOTIFY_SYSTEM_MESSAGE = "com.weiyuan.sns.notify.ACTION_NOTIFY_SYSTEM_MESSAGE";

	/**
	 * 附加标识<br/>
	 * {@link NotifiyType}
	 */
	public static final String EXTRAS_NOTIFY_SYSTEM_TAG = "extra_tag";

	/**
	 * 附加信息<br/>
	 * {@link NotifiyVo}
	 */
	public static final String EXTRAS_NOTIFY_SYSTEM_MESSAGE = "extras_message";

	/**
	 * VIP 状态发生变化 附加参数: {@link NotifySystemMessage#EXTRAS_VIP}
	 * */
	public static final String ACTION_VIP_STATE = "com.weiyuan.sns.notify.ACTION_VIP_STATE";

	/**
	 * {@link CustomerVo}
	 * */
	public static final String EXTRAS_VIP = "extra_vip";

	private SystemNotifiy systemNotifiy;

	public NotifySystemMessage(XmppManager xmppManager) {
		super();
		this.systemNotifiy = new SystemNotifiy(xmppManager.getSnsService());
	}

	@Override
	public void notifyMessage(String msg) {
		Log.e("NotifySystemMessage", "notitySystemMessage()：" + msg);
		try {
			NotifiyVo notifiyVo = new NotifiyVo(msg);

			this.systemNotifiy.notifiy(notifiyVo);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "notityMessage()", e);
		}
	}

}
