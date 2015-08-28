package com.chengxin.Entity;

import java.io.Serializable;

import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

public class GoodsComment implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**商品评论id*/
	public int id;

	/**评论发表这id*/
	public String uid;

	/**商品id*/
	public String goods_id;

	/**星级数*/
	public int star;

	/**评论内容*/
	public String content;

	/**发起评论时间*/
	public long createtime;

	/**评论发表者信息*/
	public Login user;
	
	/**状态信息*/
	public WeiYuanState state;


	public GoodsComment(String reqString) {
		super();
		try {
			if(reqString == null || reqString.equals("")){
				return;
			}
			initCompent(new JSONObject(reqString));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public GoodsComment(JSONObject obj){
		try {
			initCompent(obj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}	


	private void initCompent(JSONObject json) throws JSONException{
		if(json == null || json.equals("")){
			return;
		}// data - id
		if(!json.isNull("data")){
			String dataString = json.getString("data");
			if(dataString != null && !dataString.equals("")
					&& dataString.startsWith("{")){
				init(json.getJSONObject("data"));
			}
		}else{
			init(json);
		}
		if(!json.isNull("state")){
			String stateString = json.getString("state");
			if(stateString != null && !stateString.equals("")
					&& stateString.startsWith("{")){
				state = new WeiYuanState(json.getJSONObject("state"));
			}
		}
	}

	private void init(JSONObject json) throws JSONException{
		if(json == null || json.equals("")){
			return;
		}
		
		id = json.getInt("id");
		uid = json.getString("uid");
		goods_id = json.getString("goods_id");
		star = json.getInt("star");
		content = json.getString("content");
		createtime = json.getLong("createtime");
		
		user = new Login(json.getJSONObject("user"));
	}

}
