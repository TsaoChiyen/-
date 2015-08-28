package com.chengxin.Entity;

import java.io.Serializable;

import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

public class Shop implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String youhulist; //优惠列表
	public String tuanlist; //团购地址
	public String merchantlist;//商家列表
	public String eventlist; //活动列表
	public String goodslist; //商城列表
	public String home; //热门广场
	public String search; //搜索
	public String game; //游戏
	public String host; //服务器地址
	
	
	public Shop() {
		super();
	}
	
	public Shop(JSONObject json){
		try {
			if(json ==null){
				return;
			}
			youhulist = json.getString("youhuilist");
			tuanlist = json.getString("tuanlist");
			merchantlist = json.getString("merchantlist");
			eventlist = json.getString("eventlist");
			goodslist = json.getString("goodslist");
			home = json.getString("home");
			search = json.getString("search");
			game = json.getString("game");
			if(!json.isNull("host")){
				host = json.getString("host");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
