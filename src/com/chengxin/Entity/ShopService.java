package com.chengxin.Entity;

import java.io.Serializable;

import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

public class ShopService implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public String id; 			//< 客服id
	public String uid; 			//< 客服的 openfire id
	public String shopid; 		//< 客服所属商家id
	public String name; 		//< 客服名称
	public String username; 	//< 客服账户

	public ShopService() {
		super();
	}

	public ShopService(JSONObject json) {
		try {
			if (json == null) {
				return;
			}

			id = json.getString("id");
			uid = json.getString("uid");
			shopid = json.getString("shopid");
			name = json.getString("name");
			username = json.getString("username");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
