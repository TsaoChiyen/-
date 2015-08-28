package com.chengxin.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.chengxin.org.json.JSONArray;
import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

public class WeiYuanProject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public WeiYuanState state;
	
	public List<WeiYuanProjectItem> childList;

	public WeiYuanProject() {
		super();
	}
	
	public WeiYuanProject(String reqString) {
		super();
		if(reqString == null || reqString.equals("")){
			return;
		}
		try {
			JSONObject json = new JSONObject(reqString);
			if(!json.isNull("state")){
				this.state = new WeiYuanState(json.getJSONObject("state"));
			}
			if(!json.isNull("data")){
				String dataString = json.getString("data");
				if(dataString!=null && !dataString.equals("")){
					JSONArray jsonArray = json.getJSONArray("data");
					if(jsonArray!=null && jsonArray.length()>0){
						childList = new ArrayList<WeiYuanProjectItem>();
						for (int i = 0; i < jsonArray.length(); i++) {
							childList.add(new WeiYuanProjectItem(jsonArray.getJSONObject(i)));
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
