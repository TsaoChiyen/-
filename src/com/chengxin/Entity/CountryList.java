package com.chengxin.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.chengxin.org.json.JSONArray;
import com.chengxin.org.json.JSONException;

public class CountryList implements Serializable{

	private static final long serialVersionUID = 1946436545154L;
	
	public List<Country> mCountryList;
	public WeiYuanState mState;
	public PageInfo mPageInfo;

	public CountryList(){}
	
	public CountryList(String reString){
		try {
			if(reString == null || reString.equals("")){
				return;
			}
			
				JSONArray array = new JSONArray(reString);
				if(array != null){
					mCountryList = new ArrayList<Country>();
					for (int i = 0; i < array.length(); i++) {
						mCountryList.add(new Country(array.getJSONObject(i)));
						
					}
				}
		
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

	public String[] getString() {
		if (mCountryList != null && mCountryList.size() > 0) {
			String strList[] = new String[mCountryList.size()];
			
			for (int i = 0; i < mCountryList.size(); i++) {
				strList[i] = mCountryList.get(i).country;
			}
			
			return strList;
		}
		
		return null;
	}

	public int size() {
		if (mCountryList != null)
			return mCountryList.size();
		
		return 0;
	}
}
