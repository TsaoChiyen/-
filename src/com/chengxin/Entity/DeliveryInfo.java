package com.chengxin.Entity;

import java.io.Serializable;

import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

public class DeliveryInfo implements Serializable{

	private static final long serialVersionUID = -14564545455L;
	
	public Delivery mDelivery;
	public WeiYuanState mState;
	
	public DeliveryInfo(){
		
	}
	
	public DeliveryInfo(String reString){
		try {
			JSONObject json = new JSONObject(reString);
			if(!json.isNull("data")){
				mDelivery = new Delivery(json.getJSONObject("data"));
			}
			
			if(!json.isNull("state")){
				mState = new WeiYuanState(json.getJSONObject("state"));
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

}
