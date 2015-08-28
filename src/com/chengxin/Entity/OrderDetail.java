package com.chengxin.Entity;

import java.io.Serializable;

import com.chengxin.org.json.JSONArray;
import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

public class OrderDetail implements Serializable{

	private static final long serialVersionUID = -1454386741515L;
	public Order mOrder;
	public WeiYuanState mState;
	
	public OrderDetail(){}
	
	public OrderDetail(String reString){
		try {
			JSONObject json = new JSONObject(reString);
			if(!json.isNull("data")){
				JSONArray array = json.getJSONArray("data");
				if(array != null && array.length() > 0){
					mOrder = new Order(array.getJSONObject(0));
				}
			}
			
			if(!json.isNull("state")){
				mState = new WeiYuanState(json.getJSONObject("state"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
