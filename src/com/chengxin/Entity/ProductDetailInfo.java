package com.chengxin.Entity;

import java.io.Serializable;

import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

public class ProductDetailInfo implements Serializable{

	private static final long serialVersionUID = -1564678166341312L;
	
	public ProductDetail mProductDetail;
	public WeiYuanState mState;
	
	public ProductDetailInfo(){}
	
	public ProductDetailInfo(String reString){
		try {
			JSONObject json = new JSONObject(reString);
			if(!json.isNull("data")){
				mProductDetail = new ProductDetail(json.getJSONObject("data"));
			}
			
			if(!json.isNull("state")){
				mState = new WeiYuanState(json.getJSONObject("state"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
