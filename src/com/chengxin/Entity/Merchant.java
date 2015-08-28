package com.chengxin.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.chengxin.org.json.JSONArray;
import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

/**
 * 商户实体类
 * @author dl
 *
 */
public class Merchant implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**商品类别*/
	public int goodsType;

	/**商铺创建者id*/
	public String uid;

	/**商铺名称*/
	public String goodsName;

	/**商铺创建者名称*/
	public String username;

	/**商铺联系电话*/
	public String phone;

	/**商铺备注*/
	public String content;

	/**商铺地址*/
	public String address;

	/**商铺经度*/
	public double lat;

	/**商铺纬度*/
	public double lng;

	/***/
	public int status;

	/**创建时间*/
	public long createtime;

	/**距离*/
	public int distance;	

	/**商品类别图标*/
	public String goodsTypeIcon;


	/***/
	public String picture;
	public String picturelarge;


	/**商品列表*/
	public List<Goods> goodsList;
	
	/**用户信息*/
	public Login user;
	

	/**状态值*/
	public WeiYuanState state;



	public Merchant() {
		super();
	}


	public Merchant(String reqString) {
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

	public Merchant(JSONObject obj){
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
		
		goodsType = json.getInt("id");
		uid = json.getString("uid");
		goodsName = json.getString("name");
		username = json.getString("username");
		phone = json.getString("phone");
		content = json.getString("content");
		address = json.getString("address");
		lat = json.getDouble("lat");
		lng = json.getDouble("lng");
		status = json.getInt("status");
		createtime = json.getLong("createtime");
		distance = json.getInt("distance");
		if(!json.isNull("goods")){
			String goodsString = json.getString("goods");
			if((goodsString != null && !goodsString.equals(""))
					&& goodsString.startsWith("[")){
				JSONArray array = json.getJSONArray("goods");
				if(array != null && array.length()>0){
					goodsList = new ArrayList<Goods>();
					for (int i = 0; i < array.length(); i++) {
						goodsList.add(new Goods(array.getJSONObject(i)));
					}
				}
			}
		}
		
		if(!json.isNull("user")){
			String userString = json.getString("user");
			if((userString != null && !userString.equals(""))
					&& userString.startsWith("{")){
				user = new Login(json.getJSONObject("user"));
			}
		}
	
	}
	

}
