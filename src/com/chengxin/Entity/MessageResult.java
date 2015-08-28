package com.chengxin.Entity;

import java.io.Serializable;

import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

public class MessageResult implements Serializable{

	private static final long serialVersionUID = -1436465487871L;
	
	public MessageInfo mMessageInfo;
	public WeiYuanState mState;
	
	public MessageResult(String reString){
		try {
			JSONObject json = new JSONObject(reString);
			if(!json.isNull("state")){
				mState = new WeiYuanState(json.getJSONObject("state"));
			}
			
			if(!json.isNull("data")){
				mMessageInfo = new MessageInfo(json.getJSONObject("data"));
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
