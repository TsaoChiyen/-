package com.chengxin.Entity;

import java.io.Serializable;

import com.chengxin.global.WeiYuanCommon;
import com.chengxin.map.BMapApiApp;
import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

public class NewFriendItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String phone;
	public String loginId;
	public String uid;
	public String name;
	public int verify;
	public String headsmall;
	public int type; //0-添加 1-已添加 2-等待验证
	public int fromtype;//0-系统推送 1-好有申请
	public String contactName;
	public int colorBgtype =1 ;
	
	public NewFriendItem() {
		super();
	}
	public NewFriendItem(JSONObject json) {
		super();
		try {
			if(json ==null || json.equals("")){
				return;
			}
			loginId = WeiYuanCommon.getUserId(BMapApiApp.getInstance());
			phone = json.getString("phone");
			type = json.getInt("type");
			uid = json.getString("uid");
			name = json.getString("nickname");
			if(!json.isNull("name")){
				name = json.getString("name");
			}
			String head = json.getString("headsmall");
			if(head!=null && !head.equals("")){
				headsmall = /*WeiYuanInfo.HEAD_URL+*/head;
			}
			verify = json.getInt("verify");
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public NewFriendItem(String phone, String uid, String name,
			String headsmall, int type, String contactName,String loginId,
			int fromType) {
		super();
		this.loginId = loginId;
		this.phone = phone;
		this.uid = uid;
		this.name = name;
		this.headsmall = headsmall;
		this.type = type;
		this.contactName = contactName;
		this.fromtype = fromType;
	}
	
	
	
	
	
}
