package com.chengxin.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.chengxin.org.json.JSONArray;
import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

public class TouPiaoEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public List<Options> childList;
	public WeiYuanState state;
	public TouPiaoEntity() {
		super();
	}
	
	public TouPiaoEntity(String reqString) {
		super();
		if(reqString == null || reqString.equals("")){
			return;
		}
		try {
			JSONObject json = new JSONObject(reqString);
			if(!json.isNull("data")){
				String data = json.getString("data");
				if(data!=null && !data.equals("")){
					childList = new ArrayList<Options>();
					JSONArray array = json.getJSONArray("data");
					for (int i = 0; i < array.length(); i++) {
						childList.add(new Options(array.getJSONObject(i)));
					}
				}
			}
			if(!json.isNull("state")){
				state = new WeiYuanState(json.getJSONObject("state"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	

}
