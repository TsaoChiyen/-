package com.chengxin.Entity;

import java.io.Serializable;

import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

public class FavoriteItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int id;
	public String uid;
	public String fid;
	public String otherid;
	public String content;
	public long createtime;
	public String headsmall;
	public String nicknaem;
	public int typefile;
	

	public FavoriteItem() {
		super();
	}
	
	public FavoriteItem(String reqString) {
		super();
		try {
			if(reqString == null || reqString.equals("")){
				return;
			}
			JSONObject json = new JSONObject(reqString);
			if(!json.isNull("id")){
				this.id = json.getInt("id");
			}
			this.uid = json.getString("uid");
			this.fid = json.getString("fid");
			this.otherid = json.getString("otherid");
			this.content = json.getString("content");
			if(this.content!=null && !this.content.equals("")){
				JSONObject obj = new JSONObject(this.content);
				if(!obj.isNull("typefile")){
					typefile = obj.getInt("typefile");
				}
			}
			this.createtime = json.getLong("createtime");
			this.headsmall = json.getString("headsmall");
			this.nicknaem = json.getString("nickname");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
}
