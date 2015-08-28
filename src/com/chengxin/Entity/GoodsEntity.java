package com.chengxin.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.chengxin.org.json.JSONArray;
import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

/**
 * 商品列表
 * @author dl
 *
 */
public class GoodsEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public List<Goods> goodsList;
	public WeiYuanState state;
	public PageInfo pageInfo;



	public GoodsEntity() {
		super();
	}


	public GoodsEntity(List<Goods> goodsList, WeiYuanState state,
			PageInfo pageInfo) {
		super();
		this.goodsList = goodsList;
		this.state = state;
		this.pageInfo = pageInfo;
	}

	public GoodsEntity(String reqString){
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
						goodsList = new ArrayList<Goods>();
						for (int i = 0; i < array.length(); i++) {
							goodsList.add(new Goods(array.getJSONObject(i)));
						}
					}
				}
			}
			state = new WeiYuanState(obj.getJSONObject("state"));
			pageInfo = new PageInfo(obj.getJSONObject("pageInfo"));

		} catch (JSONException e) {
			e.printStackTrace();
		}


	}





}
