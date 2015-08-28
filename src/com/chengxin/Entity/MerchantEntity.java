package com.chengxin.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.chengxin.org.json.JSONArray;
import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

public class MerchantEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public List<Merchant> mMerchantList;

	public WeiYuanState state;

	/**分页信息*/
	public PageInfo pageInfo;

	public MerchantEntity() {
		super();
	}

	public MerchantEntity(List<Merchant> merchantList, WeiYuanState state,
			PageInfo pageInfo) {
		super();
		this.mMerchantList = merchantList;
		this.state = state;
		this.pageInfo = pageInfo;
	}

	public MerchantEntity(String reqString){
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
						mMerchantList = new ArrayList<Merchant>();
						for (int i = 0; i < array.length(); i++) {
							mMerchantList.add(new Merchant(array.getJSONObject(i)));
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
