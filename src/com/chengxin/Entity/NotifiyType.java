package com.chengxin.Entity;

/**
 * 
 * 功能： 系统通知信息 <br />
 * 客户端收到的通知格式 {"type":"1","content":"0","sent":"{xxxx}"} type 信息类型：
 * 1-系统消息，2-好友申请，3-申请查看资料，4-被评论,5-查看资料,6-删除好友 
 * 	content 消息内容：针对系统消息
	其余5种类型没有值
 *  sent
 * 发送人 用户信息. 
 * 版本：ver 1.0<br />
 * 
 * @Mr.Sam
 * @since
 */
public class NotifiyType {
	


	
	/** 系统消息 */
	public static final int SYSTEM_MSG = 1;
	
	/** 申请添加好友 */
	public static final int BE_FRIEND = 101;

	/**同意添加好友（ 添加好友成功） */
	public static final int ADDFRIENDED = 102;
	
	/** 不同意加好友（拒绝好友） */
	public static final int RESFUEFRIENDED = 103;
	
   /** 删除好友 */
	public static final int DEL_FRIEND = 104;
	
	/**
	 * 用户退出会话
	 */
	public static  final int EXIT_ROOM = 300;
	
	/** 管理员删除用户*/
	public static final int GROUP_KICK_OUT = 301;
	
	/** 
	 * 管理员编辑会话名称
	 */
	public static final int forNotifyChangeRoomName = 302;
	
	/**
	 * 管理员删除会话
	 */
	public static final int DEL_ROOM = 303;
	
	/**
	 * 用户修改自己的群昵称
	 */
	public static final int EDIT_MY_ROOM_NICKNAME =304;
	
	/**
	 * 一个用户加入会话
	 * no
	 */
	public static final int JOIN_ROOM =305;
	
	/**
	 * 添加赞
	 */
	public static final int ADD_ZAN = 400;
	
	/**
	 * 取消赞
	 */
	public static final int CANCLE_ZAN = 401;
	
	/**
	 *  回复
	 */
	public static final int REPLY = 402;
	
	/**
	 * 申请秘室
	 * no
	 */
	public static final int APPLY_METTING = 500;
	
	/**
	 * 同意申请秘室
	 * no
	 */
	public static final int AGREE_APPLY_METTING = 501;
	
	/**
	 * 不同意申请秘室
	 * no
	 */
	public static final int DIS_AGREE_APPLY_METTING = 502;
	
	/**
	 * 用户被踢出秘室
	 * no
	 */
	public static final int KICK_OUT_MEETING_USER = 506;
	
	/**
	 * 所有用户收到用户被踢的通知
	 * 507
	 * no
	 */
	
	public static final int All_USER_ACCEPT_KICK_OUT = 507;
	
	
	
}