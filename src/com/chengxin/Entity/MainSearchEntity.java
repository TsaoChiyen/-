package com.chengxin.Entity;

import java.io.Serializable;

/**
 * 搜索用户和聊天记录使用的类
 * @author dongli
 *
 */
public class MainSearchEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int metId;

	public String uid;
	public String sortKey;
	public String searchContent;
	public int typeChat;
	public String nickname;
	public String remarkname;
	public String content;
	public String headSmall;
	public int olwerModle;//所属模块 1-通讯录 2-聊天记录
	public long time;
	
	
	public MainSearchEntity(String sortKey, int typeChat, String nickname,
			String content, String headSmall, long time,int olwerModle,
			String uid,String searchContent,String remarkname) {
		super();
		this.sortKey = sortKey;
		this.typeChat = typeChat;
		this.nickname = nickname;
		this.content = content;
		this.headSmall = headSmall;
		this.time = time;
		this.olwerModle = olwerModle;
		this.uid = uid;
		this.searchContent = searchContent;
		this.remarkname = remarkname;
	}
	
	public MainSearchEntity(String sortKey, int typeChat, String nickname,
			String content, String headSmall, long time,int olwerModle,
			String uid,int metId,String remarkname) {
		super();
		this.sortKey = sortKey;
		this.typeChat = typeChat;
		this.nickname = nickname;
		this.content = content;
		this.headSmall = headSmall;
		this.time = time;
		this.olwerModle = olwerModle;
		this.uid = uid;
		this.metId = metId;
		this.remarkname = remarkname;
	}
	public MainSearchEntity() {
		super();
	}
	
	
	
	
}
