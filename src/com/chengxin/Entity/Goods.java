package com.chengxin.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Comment;

import com.chengxin.org.json.JSONArray;
import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

/**
 * 商品
 * @author dl
 *
 */
public class Goods implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**商品id*/
	public String id;

	/**商铺id*/
	public int shopid;

	/**商铺类型*/
	public int categoryid;

	/**商品图片地址-logo*/
	public String goodsUrl;

	/**商品名称-name*/
	public String goodsName;	

	/**商品价格-price*/
	public double goodsPrice;
	
	/**商品数量*/
	public int goodsCount;
	

	/**星级数*/
	public int star;

	/**图片*/
	public List<Picture> pictureList;

	/**商品评论数*/
	public int commentCount;

	/**创建时间*/
	public long createtime;

	/**是否收藏*/
	public int isfavorite;
	
	/**评论内容*/
	public GoodsComment goodsComment;
	
	/**图文详细*/
	public String content;
	
	/**产品参数*/
	public String parameter;

	
	/**状态值*/
	public WeiYuanState state;
	
	
	
	



	public Goods() {
		super();
	}

	public Goods(String reqString) {
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

	public Goods(JSONObject obj){
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
		
		id = json.getString("id");
		shopid = json.getInt("shopid");
		categoryid = json.getInt("categoryid");
		goodsUrl = json.getString("logo");
		goodsName = json.getString("name");
		goodsPrice = json.getDouble("price");
		star = json.getInt("star");
		if(!json.isNull("picture")){
			String picString = json.getString("picture");
			if((picString != null && !picString.equals(""))
					&& picString.startsWith("[")){
				JSONArray array = json.getJSONArray("picture");
				if(array != null && array.length()>0){
					pictureList = new ArrayList<Picture>();
					for (int i = 0; i < array.length(); i++) {
						pictureList.add(Picture.getInfo(array.getString(i)));
					}
				}
				
			}
		}
		
		commentCount = json.getInt("commentcount");
		createtime = json.getLong("createtime");
		isfavorite = json.getInt("isfavorite");
		
		if(!json.isNull("comment")){
			String commentString = json.getString("comment");
			if((commentString != null && !commentString.equals(""))
					&& commentString.startsWith("{")){
				goodsComment = new GoodsComment(json.getJSONObject("comment"));
			}
		}
		
		if(!json.isNull("content")){
			content = json.getString("content");
		}
		
		
		if(!json.isNull("parameter")){
			parameter = json.getString("parameter");
		}
		

	}


}
