package com.chengxin.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.chengxin.org.json.JSONArray;
import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

public class OrderMenuItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int id;
	public String name;
	public String url;
	public List<OrderMenuItem> childMenuList = new ArrayList<OrderMenuItem>();
	public OrderMenuItem() {
		super();
	}
	
	public OrderMenuItem(JSONObject json) {
		super();
		if(json == null || json.equals("")){
			return;
		}
		
		try {
			id = json.getInt("id");
			name = json.getString("name");
			url = json.getString("link");
			if(!json.isNull("list")){
				String childObj = json.getString("list");
				if(childObj!=null && !childObj.equals("")){
					JSONArray array = json.getJSONArray("list");
					for (int i = 0; i <array.length(); i++) {
						childMenuList.add(new OrderMenuItem(array.getJSONObject(i)));
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
}
