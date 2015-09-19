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

    public int id;              //< 商户id
    public String uid;          //< 用户id
    public String logo;         //< 商户LOGO
    public String name;         //< 商铺名称
    public String username;     //< 联系人
    public String phone;        //< 联系电话
    public String content;      //< 备注
    public String useraddress;  //< 联系地址
    public String address;      //< 商户地址
    public double lat;          //< 商铺纬度
    public double lng;          //< 商铺经度
    public String city;         //< 商户所在城市
    public String shopPaper;    //< 商户营业执照
    public String authPaper;    //< 商户授权证书
    public String bank;         //< 银行名
    public String bankName;     //< 银行用户名
    public String bankCard;     //< 银行账户
    public int status;          //< 商户状态
    public long createtime;     //< 入驻时间
    public String password;     //< 独立密码
    public int distance;        //< 距离

    public List<Goods> goodsList;           //< 商品列表
    public Login user;                      //< 用户信息
    public List<ShopService> serviceList;   //< 商家客服列表

    public WeiYuanState state;              //< 返回的状态对象

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
		
		id          = json.getInt("id");
		uid         = json.getString("uid");
        logo        = json.getString("logo");
		name	    = json.getString("name");
		username    = json.getString("username");
		phone       = json.getString("phone");
		content     = json.getString("content");
        useraddress = json.getString("useraddress");
		address     = json.getString("address");
		lat         = json.getDouble("lat");
		lng         = json.getDouble("lng");
        city        = json.getString("city");
        shopPaper   = json.getString("shopPaper");
        authPaper   = json.getString("authPaper");
        bank        = json.getString("bank");
        bankName    = json.getString("bankName");
        bankCard    = json.getString("bankCard");
		status      = json.getInt("status");
		createtime  = json.getLong("createtime");
        password    = json.getString("password");
		distance    = json.getInt("distance");
        
		if(!json.isNull("goods")){
			String goodsString = json.getString("goods");
            
			if((goodsString != null && !goodsString.equals(""))
					&& goodsString.startsWith("[")){
				JSONArray array = json.getJSONArray("goods");
                
				if (array != null && array.length()>0) {
					goodsList = new ArrayList<Goods>();
                    
					for (int i = 0; i < array.length(); i++) {
						goodsList.add(new Goods(array.getJSONObject(i)));
					}
				}
			}
		}
		
		if (!json.isNull("user")) {
			String userString = json.getString("user");
            
			if((userString != null && !userString.equals(""))
					&& userString.startsWith("{")){
				user = new Login(json.getJSONObject("user"));
			}
		}
	
        if(!json.isNull("service")){
            String goodsString = json.getString("service");
            
            if((goodsString != null && !goodsString.equals(""))
               && goodsString.startsWith("[")){
                JSONArray array = json.getJSONArray("service");
                
                if (array != null && array.length()>0) {
                    serviceList = new ArrayList<ShopService>();
                    
                    for (int i = 0; i < array.length(); i++) {
                        serviceList.add(new ShopService(array.getJSONObject(i)));
                    }
                }
            }
        }
	}
	

}
