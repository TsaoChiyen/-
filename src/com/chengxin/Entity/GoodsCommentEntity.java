package com.chengxin.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.chengxin.org.json.JSONArray;
import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

/**
 * 商品评论实体类
 * @author dongli
 *
 */
public class GoodsCommentEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public List<GoodsComment> commentList ;
	public WeiYuanState state;
	public PageInfo pageInfo;
	
	
	
	public GoodsCommentEntity(String reqString){
		try {
			if(reqString == null || reqString.equals("")){
				return;
			}
			JSONObject obj = new JSONObject(reqString);
			if(!obj.isNull("data")){
				String dataString = obj.getString("data");
				if((dataString != null && !dataString.equals(""))
						&& (dataString.startsWith("["))){
					JSONArray array = obj.getJSONArray("data");
					if(array != null && array.length()>0){
						commentList = new ArrayList<GoodsComment>();
						for (int i = 0; i < array.length(); i++) {
							commentList.add(new GoodsComment(array.getJSONObject(i)));
						}
					}
				}
			}
			state = new WeiYuanState(obj.getJSONObject("state"));
			if(!obj.isNull("pageInfo")){
				pageInfo = new PageInfo(obj.getJSONObject("pageInfo"));
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}


	}

}
