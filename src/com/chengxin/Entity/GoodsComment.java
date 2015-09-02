package com.chengxin.Entity;

import java.io.Serializable;

import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

public class GoodsComment implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    public int id;          //< 商品评论id
    public String uid;      //< 评论发表者id
    public String goodsid;  //< 商品id
    public int star;        //< 级数
    public String content;  //< 评论内容
    public long createtime; //< 发起评论时间

    public Login user;      //< 评论发表者信息
	
    public WeiYuanState state;  //< 状态信息

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
		}
        
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
		goodsid = json.getString("goods_id");
		star = json.getInt("star");
		content = json.getString("content");
		createtime = json.getLong("createtime");
		
		user = new Login(json.getJSONObject("user"));
	}

}
