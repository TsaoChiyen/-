package com.chengxin.Entity;


public final class MessageType {
	
	/**
	 * 文字
	 */
	public static final int TEXT = 1;

	/**
	 * 图片
	 */
	public static final int PICTURE = 2;
	
	/**
	 * 声音
	 */
	public static final int VOICE = 3;

	
	/**
	 * 位置
	 */
	public static final int MAP = 4;
	
	/**
	 * 通讯录名片
	 */
	public static final int CARD = 5;
	
	

    //聊天类型
    /**
     * 单聊
     */
    public static final int SINGLE_CHAT = 100;

    /**
     * 群聊
     */
    public static final int GROUP_CHAT = 300;

    /**
     * 会话
     */
    public static final int SESSION_CHAT = 300;

    /**
     * 秘室
     */
    public static final int MEETING_CHAT = 500;

    /**
     * 服务号
     */
    public static final int SERVICE_CHAT = 600;

    /**
     * 订阅号
     */
    public static final int SUB_CHAT = 700;
	
	
	
	
	/**
	 * //小视频
	 */
	public static final int SMALL_VIDEO = 6;
	
	/*
	 *//**
	 * //图文
	 *//*
	public static final int PHOTOSEE=7;

	
	*//**
	 * // 订阅号名片
	 *//*
	public static final int SUBINFOCARD = 8; 
	
	*//**
	 *  // 思八达新闻分享
	 *  
	 *//*
	public static final int APPNEWS = 9;    */
	

	
	
	
	
	
	public static String timeUid(){
		return System.currentTimeMillis() + "";
	}
}
