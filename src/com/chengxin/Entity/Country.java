package com.chengxin.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.chengxin.org.json.JSONArray;
import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

public class Country implements Serializable{

	private static final long serialVersionUID = -146445435454L;
	
	public String countryID;//id
	public String country;//text
	public List<ChildCity> childList;
	
	public Country(){
		
	}

	public Country(JSONObject json){
		try {
			countryID = json.getString("id");
			country = json.getString("State");
		
			if(!json.isNull("Cities")){
				JSONArray jsonArray = json.getJSONArray("Cities");
				childList = new ArrayList<ChildCity>();
				for (int i = 0; i < jsonArray.length(); i++) {
					childList.add(new ChildCity(jsonArray.getJSONObject(i)));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String[] getChildrenString() {
		if (childList != null && childList.size() > 0) {
			String strList[] = new String[childList.size()];
			
			for (int i = 0; i < childList.size(); i++) {
				strList[i] = childList.get(i).text;
			}
			
			return strList;
		}
		
		return null;
	}
	
	public int children() {
		if (childList != null)
			return childList.size();
		
		return 0;
	}

}
