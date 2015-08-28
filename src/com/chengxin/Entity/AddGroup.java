package com.chengxin.Entity;

import java.io.Serializable;

import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

public class AddGroup implements Serializable{

	private static final long serialVersionUID = -14641564545645L;
	public Group mGroup;
	public WeiYuanState mState;
	
	public AddGroup(){}
	
	public AddGroup(String reString){
		try {
			JSONObject json = new JSONObject(reString);
			if(!json.isNull("state")){
				mState = new WeiYuanState(json.getJSONObject("state"));
			}
			
			if(!json.isNull("data")){
				mGroup = new Group(json.getJSONObject("data"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
