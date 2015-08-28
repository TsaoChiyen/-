package com.chengxin.Entity;

import java.io.Serializable;
import java.util.List;

import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

public class WeiYuanProjectItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String id;
	public String text;
	public int vieworder;
	public List<Login> userList;
	public WeiYuanProjectItem() {
		super();
	}
	public WeiYuanProjectItem(JSONObject json) {
		super();
		try {
			if(json == null ){
				return;
			}
			if(!json.isNull("id")){
				this.id = json.getString("id");
			}
			if(!json.isNull("text")){
				this.text = json.getString("text");
			}
			if(!json.isNull("vieworder")){
				this.vieworder = json.getInt("vieworder");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
}
