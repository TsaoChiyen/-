package com.chengxin.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.chengxin.org.json.JSONArray;
import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

public class OrderList implements Serializable{

	private static final long serialVersionUID = 1946436545154L;
	
	public List<Order> mOrderList;
	public WeiYuanState mState;
	public PageInfo mPageInfo;

	public OrderList(){}
	
	public OrderList(String reString){
		try {
			JSONObject json = new JSONObject(reString);
			
			if(!json.isNull("state")){
				mState = new WeiYuanState(json.getJSONObject("state"));
			}
			
			if(!json.isNull("pageInfo")){
				mPageInfo = new PageInfo(json.getJSONObject("pageInfo"));
			}

			if(!json.isNull("data")){
				JSONArray array = json.getJSONArray("data");

				if(array != null){
					mOrderList = new ArrayList<Order>();
					List<Order> tempList = Order.constructOrderList(array);
					if(tempList != null){
						mOrderList.addAll(tempList);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public int size() {
		if (mOrderList != null)		return mOrderList.size();
		else return 0;
	}

	public float totalPrice() {
		float price = 0;
		
		if (mOrderList != null && mOrderList.size() > 0) {
			for (int i = 0; i < mOrderList.size(); i++) {
				Order order = mOrderList.get(i);
				
				price += order.totalPrice;
			}
		}

		return price;
	}
}
