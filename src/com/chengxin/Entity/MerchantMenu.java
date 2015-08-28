package com.chengxin.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.chengxin.org.json.JSONArray;
import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

public class MerchantMenu implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int id;
	public String logo;
	public String name;
	public int vieworder;
	public List<MerchantMenu> menuList;
	
	
	
	public MerchantMenu(int id, String logo, String name) {
		super();
		this.id = id;
		this.logo = logo;
		this.name = name;
	}



	public MerchantMenu() {
		super();
	}
	
	

	public MerchantMenu(String reqString) {
		super();
		if(reqString == null || reqString.equals("")){
			return;
		}
		
		try {
			JSONObject obj = new JSONObject(reqString);
			if(!obj.isNull("state")){
				WeiYuanState state = new WeiYuanState(obj.getJSONObject("state"));
				if(state != null && state.code == 0){
					if(!obj.isNull("data")){
						String dataString = obj.getString("data");
						if((dataString != null && !dataString.equals(""))
								&& dataString.startsWith("[")){
							JSONArray array = obj.getJSONArray("data");
							if(array != null && array.length() >0){
								menuList = new ArrayList<MerchantMenu>();
								for (int i = 0; i < array.length(); i++) {
									menuList.add(new MerchantMenu(array.getJSONObject(i)));
								}
							}
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	private MerchantMenu(JSONObject obj) throws JSONException{
		if(obj == null  || obj.equals("")){
			return;
		}
		id = obj.getInt("id");
		logo = obj.getString("logo");
		name = obj.getString("name");
		vieworder = obj.getInt("vieworder");
	}
	
	

}
