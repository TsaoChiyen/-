package com.chengxin.Entity;

import java.io.Serializable;

import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

public class CommentGoodsState implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**商品评论条数*/
	public int commentcount;

	/**评论内容*/
	public GoodsComment goodsComment;

	/**评论的星星数*/
	public int star;

	/**状态信息*/
	public WeiYuanState state;



	public CommentGoodsState(String reqString) {
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

	public CommentGoodsState(JSONObject obj){
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

		if(!json.isNull("data")){
			String dataString = json.getString("data");
			if((dataString != null && !dataString.equals(""))
					&& dataString.startsWith("{")){
				JSONObject childObj = json.getJSONObject("data");
				commentcount = childObj.getInt("commentcount");
				star = childObj.getInt("star");
				if(!childObj.isNull("comment")){
					String commentString = childObj.getString("comment");
					if((commentString != null && !commentString.equals(""))
							&& (commentString.startsWith("{"))){
						goodsComment = new GoodsComment(childObj.getJSONObject("comment"));
					}
				}
			}
		}

		if(!json.isNull("state")){
			state = new WeiYuanState(json.getJSONObject("state"));
		}


	}





}
