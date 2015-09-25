package com.chengxin.receiver;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.chengxin.FriensLoopActivity;
import com.chengxin.MainActivity;
import com.chengxin.MyGroupListActivity;
import com.chengxin.R;
import com.chengxin.DB.DBHelper;
import com.chengxin.DB.MessageTable;
import com.chengxin.DB.RoomTable;
import com.chengxin.DB.SessionTable;
import com.chengxin.Entity.Login;
import com.chengxin.Entity.NewFriendItem;
import com.chengxin.Entity.NotifiyType;
import com.chengxin.Entity.NotifiyVo;
import com.chengxin.Entity.Room;
import com.chengxin.Entity.SNSMessage;
import com.chengxin.Entity.Session;
import com.chengxin.fragment.ChatFragment;
import com.chengxin.fragment.ContactsFragment;
import com.chengxin.global.FeatureFunction;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.GlobleType;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.map.BMapApiApp;
import com.chengxin.service.SnsService;

public class SystemNotifiy extends AbstractNotifiy {
	public static final int NOTION_ID = 10023;

	private Context mContext;

	public SystemNotifiy(SnsService context) {
		super(context);
		mContext = context;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void notifiy(SNSMessage message) {
		if (message instanceof NotifiyVo) {
			NotifiyVo notifiyVo = (NotifiyVo) message;
			SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();

			SessionTable sessionTable = new SessionTable(db);
			MessageTable messageTable = new MessageTable(db);
			Session session = null;
			String msg = "";
			switch (notifiyVo.getType()) {
			case NotifiyType.SYSTEM_MSG:
				/*
				 * msg = BMapApiApp.getInstance().getResources().getString(R.string.system_info); break;
				 */
				return;
			case NotifiyType.DEL_FRIEND:
				Log.e("SystemNotify", "删除好友通知");
				return;
			case NotifiyType.BE_FRIEND:// 申请添加 - 添加 0
			case NotifiyType.ADDFRIENDED:// 同意添加 - 1
				int validType = -1; // 0-添加 1-已添加 2-等待验证 3-同意对方的请求
				if (notifiyVo.getType() == NotifiyType.BE_FRIEND) {
					msg = BMapApiApp.getInstance().getResources().getString(R.string.apply_friend);
					mContext.sendBroadcast(new Intent(ContactsFragment.ACTION_SHOW_NEW_FRIENDS));
					mContext.sendBroadcast(new Intent(GlobalParam.ACTION_SHOW_CONTACT_NEW_TIP));

					WeiYuanCommon.saveContactTip(mContext, 1);
					validType = 3;
				} else if (notifiyVo.getType() == NotifiyType.ADDFRIENDED) {
					validType = 1;
					mContext.sendBroadcast(new Intent(ContactsFragment.REFRESH_FRIEND_ACTION));
				}

				checkFriendsNotify(notifiyVo.getUser(), validType, notifiyVo.getContent());
				Intent refreshIntent = new Intent(GlobalParam.ACTION_REFRESH_NEW_FRIENDS);

				mContext.sendBroadcast(refreshIntent);

				if (notifiyVo.getType() == NotifiyType.ADDFRIENDED) {
					return;
				}
				// msg = BMapApiApp.getInstance().getResources().getString(R.string.add_friend_success);
				break;
			case NotifiyType.RESFUEFRIENDED:
				msg = BMapApiApp.getInstance().getResources().getString(R.string.refused_friend_success);
				return;
			case NotifiyType.EXIT_ROOM:// 用户退出群
				mContext.sendBroadcast(new Intent(MyGroupListActivity.REFRESH_ROOM_ACTION));
				return;
			case NotifiyType.GROUP_KICK_OUT:// 管理员删除用户
				if (notifiyVo.getUserId().equals(WeiYuanCommon.getUserId(mContext))) {
					session = sessionTable.query(notifiyVo.roomID, 300);
					if (session != null) {
						messageTable.delete(notifiyVo.roomID, 300);
						sessionTable.delete(notifiyVo.roomID, 300);

						mContext.sendBroadcast(new Intent(ChatFragment.ACTION_REFRESH_SESSION));
						mContext.sendBroadcast(new Intent(GlobalParam.ACTION_UPDATE_SESSION_COUNT));
						NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
						notificationManager.cancel(0);
					}
				}

				// msg = mContext.getString(R.string.you_have_been_kick_out_group);
				Intent kickIntent = new Intent(GlobalParam.BE_KICKED_ACTION);
				kickIntent.putExtra("id", notifiyVo.roomID);
				kickIntent.putExtra("uid", notifiyVo.getUserId());
				mContext.sendBroadcast(kickIntent);
				mContext.sendBroadcast(new Intent(MyGroupListActivity.REFRESH_ROOM_ACTION));

				return;
			case NotifiyType.forNotifyChangeRoomName:// 管理员编辑会话名称
				RoomTable roomTable = new RoomTable(db);
				SessionTable sesTable = new SessionTable(db);
				Room oldRoom = roomTable.query(notifiyVo.roomID);
				Session ses = sesTable.query(notifiyVo.roomID, 300);
				if (ses != null) {
					ses.name = notifiyVo.roomName;
					sessionTable.update(ses, 300);
				}
				if (oldRoom != null) {
					oldRoom.groupName = notifiyVo.roomName;
					roomTable.update(oldRoom);
				}
				Intent intent = new Intent(GlobalParam.ACTION_RESET_GROUP_NAME);
				intent.putExtra("group_id", notifiyVo.roomID);
				intent.putExtra("group_name", notifiyVo.roomName);
				mContext.sendBroadcast(intent);
				return;
			case NotifiyType.EDIT_MY_ROOM_NICKNAME:// 群组用户修改自己的群昵称
				Intent groupNameIntent = new Intent(GlobalParam.ACTION_RESET_MY_GROUP_NAME);
				groupNameIntent.putExtra("my_group_nickname", notifiyVo.userName);
				groupNameIntent.putExtra("group_id", notifiyVo.roomID);
				groupNameIntent.putExtra("uid", notifiyVo.getUserId());
				mContext.sendBroadcast(groupNameIntent);
				return;
			case NotifiyType.JOIN_ROOM:// 一个用户加入会话
				return;
			case NotifiyType.DEL_ROOM:// 管理员删除会话
				session = sessionTable.query(notifiyVo.roomID, 300);
				if (session != null) {
					messageTable.delete(notifiyVo.roomID, 300);
					sessionTable.delete(notifiyVo.roomID, 300);

					mContext.sendBroadcast(new Intent(ChatFragment.ACTION_REFRESH_SESSION));
					mContext.sendBroadcast(new Intent(GlobalParam.ACTION_UPDATE_SESSION_COUNT));
				}
				NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.cancel(0);

				// msg = mContext.getString(R.string.you_have_been_kick_out_group);
				Intent kicIntent = new Intent(GlobalParam.ROOM_BE_DELETED_ACTION);
				kicIntent.putExtra("roomID", notifiyVo.roomID);
				mContext.sendBroadcast(kicIntent);
				mContext.sendBroadcast(new Intent(MyGroupListActivity.REFRESH_ROOM_ACTION));
				return;
			case NotifiyType.ADD_ZAN:// 赞
				mContext.sendBroadcast(new Intent(GlobalParam.ACTION_REFRESH_MOVING_DETAIL));
				if (notifiyVo.getUserId().equals(WeiYuanCommon.getUserId(mContext))) {
					return;
				}
				List<NotifiyVo> list = WeiYuanCommon.getMovingResult(mContext);
				if (list == null) {
					list = new ArrayList<NotifiyVo>();
				}
				boolean isExitsi = false;
				if (list != null && list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).shareId == notifiyVo.shareId && list.get(i).getType() == notifiyVo.getType()) {
							isExitsi = true;
						}

						if (i == list.size() - 1) {
							if (!isExitsi) {
								list.add(0, notifiyVo);
								break;
							}
						}
					}
				} else {
					list.add(0, notifiyVo);
				}

				WeiYuanCommon.saveMoving(mContext, list);

				mContext.sendBroadcast(new Intent(GlobalParam.ACTION_REFRESH_MYALBUM_MESSAGE));
				try {
					ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
					ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
					if (cn.getClassName().equals(cn.getPackageName() + ".FriensLoopActivity")) {
						if (FeatureFunction.isAppOnForeground(mContext)) {
							// return;
						}
					} else {
						mContext.sendBroadcast(new Intent(GlobalParam.ACTION_SHOW_FOUND_NEW_TIP));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				mContext.sendBroadcast(new Intent(FriensLoopActivity.MSG_REFRESH_MOVIINF));
				return;
			case NotifiyType.CANCLE_ZAN:// 取消赞
				mContext.sendBroadcast(new Intent(GlobalParam.ACTION_REFRESH_MOVING_DETAIL));
				if (notifiyVo.getUserId().equals(WeiYuanCommon.getUserId(mContext))) {
					return;
				}
				List<NotifiyVo> cancleLlist = WeiYuanCommon.getMovingResult(mContext);
				List<NotifiyVo> tempCancleLlist = new ArrayList<NotifiyVo>();
				for (NotifiyVo notifiy : cancleLlist) {
					if (notifiy.shareId == notifiy.shareId && notifiy.getType() == NotifiyType.ADD_ZAN) {

					} else {
						tempCancleLlist.add(notifiy);
					}
				}

				WeiYuanCommon.saveMoving(mContext, tempCancleLlist);
				mContext.sendBroadcast(new Intent(GlobalParam.ACTION_REFRESH_MYALBUM_MESSAGE));
				mContext.sendBroadcast(new Intent(FriensLoopActivity.MSG_REFRESH_MOVIINF));
				return;
			case NotifiyType.REPLY:// 评论
				if (notifiyVo.getUserId().equals(WeiYuanCommon.getUserId(mContext))) {
					return;
				}
				List<NotifiyVo> replyLlist = WeiYuanCommon.getMovingResult(mContext);
				if (replyLlist == null) {
					replyLlist = new ArrayList<NotifiyVo>();
				}
				replyLlist.add(0, notifiyVo);
				WeiYuanCommon.saveMoving(mContext, replyLlist);
				mContext.sendBroadcast(new Intent(GlobalParam.ACTION_REFRESH_MYALBUM_MESSAGE));
				mContext.sendBroadcast(new Intent(GlobalParam.ACTION_REFRESH_MYALBUM_MESSAGE));
				try {
					ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
					ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
					if (cn.getClassName().equals(cn.getPackageName() + ".FriensLoopActivity")) {
						if (FeatureFunction.isAppOnForeground(mContext)) {
							// return;
						}
					} else {
						mContext.sendBroadcast(new Intent(GlobalParam.ACTION_SHOW_FOUND_NEW_TIP));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				mContext.sendBroadcast(new Intent(FriensLoopActivity.MSG_REFRESH_MOVIINF));
				return;
			case NotifiyType.APPLY_METTING:// 申请加入秘室
				// found_type
				Intent applyIntent = new Intent(GlobalParam.ACTION_SHOW_FOUND_NEW_TIP);
				applyIntent.putExtra("found_type", 2);
				mContext.sendBroadcast(applyIntent);
				mContext.sendBroadcast(new Intent(GlobalParam.ACTION_REFRESH_MEETING_LIST));
				return;

			case NotifiyType.AGREE_APPLY_METTING:// 同意申请加入秘室
			case NotifiyType.DIS_AGREE_APPLY_METTING:// 不同意申请加入秘室
				mContext.sendBroadcast(new Intent(GlobalParam.ACTION_UPDATE_MEETING_DETAIL));
				mContext.sendBroadcast(new Intent(GlobalParam.ACTION_REFRESH_MEETING_LIST));
				return;

			case NotifiyType.KICK_OUT_MEETING_USER:// 用户被踢出秘室
				/* if(notifiyVo.getUserId().equals(WeiYuanCommon.getUserId(mContext))){ */
				session = sessionTable.query(notifiyVo.roomID, GlobleType.MEETING_CHAT);
				if (session != null) {
					messageTable.delete(notifiyVo.roomID, GlobleType.MEETING_CHAT);
					sessionTable.delete(notifiyVo.roomID, GlobleType.MEETING_CHAT);

					mContext.sendBroadcast(new Intent(ChatFragment.ACTION_REFRESH_SESSION));
					mContext.sendBroadcast(new Intent(GlobalParam.ACTION_UPDATE_SESSION_COUNT));
					NotificationManager snotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
					snotificationManager.cancel(0);
				}
				/* } */

				// msg = mContext.getString(R.string.you_have_been_kick_out_group);
				Intent meetKickIntent = new Intent(GlobalParam.BE_KICKED_ACTION);
				meetKickIntent.putExtra("id", notifiyVo.roomID);
				meetKickIntent.putExtra("type", 1);
				meetKickIntent.putExtra("hintMsg", notifiyVo.getContent());
				meetKickIntent.putExtra("uid", notifiyVo.getUserId());
				mContext.sendBroadcast(meetKickIntent);
				mContext.sendBroadcast(new Intent(GlobalParam.ACTION_REFRESH_MEETING_LIST));
				mContext.sendBroadcast(new Intent(GlobalParam.ACTION_DESTROY_MEETING_PAGE));
				return;

			case NotifiyType.All_USER_ACCEPT_KICK_OUT:// 所有用户都会受到踢出参会人员的通知
				return;

			default:
				return;
			}

			Intent updateintent = new Intent(NotifySystemMessage.ACTION_NOTIFY_SYSTEM_MESSAGE);
			// updateintent.putExtra(NotifySystemMessage.EXTRAS_NOTIFY_SYSTEM_MESSAGE, notifiyVo);
			mContext.sendBroadcast(updateintent);

			try {
				ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
				ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
				if (cn.getClassName().equals(cn.getPackageName() + ".NewFriendsActivity")) {
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// isAcceptNew
			if (!WeiYuanCommon.getLoginResult(mContext).isAcceptNew) {
				return;
			}

			// Notification
			Notification notification = new Notification();
			notification.icon = R.drawable.ic_launcher; // 设置通知的图标
			long currentTime = System.currentTimeMillis();
			if (currentTime - WeiYuanCommon.getNotificationTime(mContext) > WeiYuanCommon.NOTIFICATION_INTERVAL) {
				if (WeiYuanCommon.getLoginResult(mContext).isAcceptNew/* getOpenSound(mContext) */) {
					if (WeiYuanCommon.getLoginResult(mContext).isOpenVoice) {
						notification.defaults |= Notification.DEFAULT_SOUND;
					}
					if (WeiYuanCommon.getLoginResult(mContext).isOpenShake) {
						notification.defaults |= Notification.DEFAULT_VIBRATE;
					}
				}
				WeiYuanCommon.saveNotificationTime(mContext, currentTime);
			}

			notification.defaults |= Notification.DEFAULT_LIGHTS;
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			// 音频将被重复直到通知取消或通知窗口打开。
			// notification.flags |= Notification.FLAG_INSISTENT;
			notification.flags |= Notification.FLAG_SHOW_LIGHTS;
			notification.when = currentTime;

			Intent intent = new Intent(getService(), MainActivity.class);
			intent.putExtra("notify", true);

			PendingIntent contentIntent = PendingIntent.getActivity(getService(), NOTION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			//
			notification.setLatestEventInfo(getService(), mContext.getString(R.string.has_new_notification), msg, contentIntent);
			getNotificationManager().notify(NOTION_ID, notification);
		}
	}

	/**
	 * 保存新的朋友数据
	 * 
	 * @param login
	 * @param type
	 *            0-添加 1-已添加 2-等待验证
	 */
	private void checkFriendsNotify(Login login, int type, String content) {
		List<NewFriendItem> mUserList = new ArrayList<NewFriendItem>();
		mUserList.add(new NewFriendItem(login.phone, login.uid, login.name, login.headsmall, type, content, WeiYuanCommon.getUserId(mContext), 1));

		// 获取系统中保存的新的朋友
		List<NewFriendItem> lastNewFriendsList = WeiYuanCommon.getNewFriendItemResult(mContext);

		boolean isExitsLastData = false;
		for (int i = 0; i < mUserList.size(); i++) {
			String currentUid = mUserList.get(i).uid;
			if (lastNewFriendsList != null && lastNewFriendsList.size() > 0) {
				isExitsLastData = true;
				for (int j = 0; j < lastNewFriendsList.size(); j++) {
					if (lastNewFriendsList.get(j).uid.equals(currentUid)) {
						mUserList.get(i).colorBgtype = 0;
						/*
						 * if(lastNewFriendsList.get(j).type!=0){ mUserList.get(i).type = lastNewFriendsList.get(j).type; }
						 */
						break;
					}
				}
			}
		}
		if (isExitsLastData) {
			for (int l = 0; l < lastNewFriendsList.size(); l++) {
				boolean isExits = true;
				for (int m = 0; m < mUserList.size(); m++) {
					if (mUserList.get(m).uid.equals(lastNewFriendsList.get(l).uid)) {
						isExits = false;
					}
					if (m == mUserList.size() - 1) {
						if (isExits) {
							mUserList.add(lastNewFriendsList.get(l));
							break;
						}
					}
				}
			}
		}
		List<NewFriendItem> newList = new ArrayList<NewFriendItem>();
		List<NewFriendItem> oldList = new ArrayList<NewFriendItem>();
		for (int i = 0; i < mUserList.size(); i++) {
			if (mUserList.get(i).colorBgtype == 1) {
				newList.add(mUserList.get(i));
			} else if (mUserList.get(i).colorBgtype == 0) {
				oldList.add(mUserList.get(i));
			}
		}
		if (mUserList != null && mUserList.size() > 0) {
			mUserList.clear();
		}
		mUserList.addAll(newList);
		mUserList.addAll(oldList);
		WeiYuanCommon.saveNewFriendsResult(mContext, mUserList, mUserList.size());
	}

}
