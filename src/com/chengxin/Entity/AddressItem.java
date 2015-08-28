package com.chengxin.Entity;

import java.io.Serializable;
import java.util.ArrayList;

import com.chengxin.org.json.JSONArray;
import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

public class AddressItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public String addressid; //地址ID
	public String companyid; //地址ID
	public String uid;    //用户ID
	public String consignee; //收货人
	public String companyname; //公司名称
	public String position; //公司名称
	public String phone;  //联系电话
	public String province; //省份
	public String town;
	public String region;
	public String address;
	public String postcode; //邮政编码
	public String isdefault; //是否为默认的收货地址
    public String introduce; //公司简介

	public ResearchJiaState status;

	public AddressItem() {
		super();
	}

	public AddressItem(JSONObject obj) {
		super();
		init(obj);
		
	}

	private void init(JSONObject obj){
		if(obj == null){
			return;
		}
		try {
			this.addressid = obj.getString("addressid");
			this.companyid = obj.getString("companyid");
			this.uid = obj.getString("uid");
			this.consignee = obj.getString("consignee");
			this.companyname = obj.getString("companyname");
			this.position = obj.getString("position");
			this.phone = obj.getString("phone");
			this.province = obj.getString("province");
			this.town = obj.getString("town");
			this.region = obj.getString("region");
			this.address = obj.getString("address");
			this.postcode = obj.getString("postcode");
			this.isdefault = obj.getString("isdefault");
			this.introduce = obj.getString("introduce");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public AddressItem (String reqString){
		if(reqString == null || reqString.equals("")){
			return;
		}
		try {
			JSONObject json = new JSONObject(reqString);
			if(!json.isNull("data")){

				JSONObject obj = json.getJSONObject("data");
				if(obj != null){
					init(obj);
				}
			}

			if(!json.isNull("state")){
				this.status = new ResearchJiaState(json.getJSONObject("state"));
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
