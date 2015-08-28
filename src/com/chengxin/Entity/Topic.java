package com.chengxin.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.chengxin.org.json.JSONArray;
import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

public class Topic implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public List<TopicItem> topicList;
	public WeiYuanState status;
	public PageInfo page;
	public Topic(List<TopicItem> topicList, WeiYuanState status, PageInfo page) {
		super();
		this.topicList = topicList;
		this.status = status;
		this.page = page;
	}
	public Topic() {
		super();
	}
	
	public Topic(String reqString){
		if(reqString == null || reqString.equals("")){
			return;
		}
		try {
			JSONObject jsonObj = new JSONObject(reqString);
			if(!jsonObj.isNull("data")){
				JSONArray jsonArray = jsonObj.getJSONArray("data");
				if(jsonArray!=null && jsonArray.length()>0){
					topicList = new ArrayList<TopicItem>();
					for (int i = 0; i < jsonArray.length(); i++) {
						topicList.add(new TopicItem(jsonArray.getString(i)));
					}
				}
				
			}
			if(!jsonObj.isNull("state")){
				this.status = new WeiYuanState(jsonObj.getJSONObject("state"));
			}
			if(!jsonObj.isNull("pageinfo")){
				this.page = new PageInfo(jsonObj.getJSONObject("pageinfo"));
			}
			if(!jsonObj.isNull("pageInfo")){
				this.page = new PageInfo(jsonObj.getJSONObject("pageInfo"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
}
