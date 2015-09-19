package com.chengxin.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.chengxin.org.json.JSONArray;
import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

/**
 *  还款账单
 */
public class Bill implements Serializable{

    private static final long serialVersionUID = 1L;
    
    public int id;              //< 账单id
    public int uid;             //< 用户id
    public int type;            //< 还款类型：1.贷款还款，2.信用卡还款
    public String name;         //< (贷款还款)账单名称
    public String bank;         //< (信用卡还款)信用卡银行
    public String card;         //< 信用卡卡号
    public double price;        //< (贷款还款)还款金额
    public int number;          //< (贷款还款)剩余期数
    public String mechanism;    //< (贷款还款)还款机构
    public long repayment;      //< (贷款还款)提醒日期，(信用卡还款)还款期限

    public Login user;          //< 还款人信息
    public WeiYuanState state;  //< 返回的状态对象

	public String email;
	public String emailpwd;
	public String emaillogin;
	public String emailsvr;

    public Bill(String reqString) {
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
    
    public Bill(JSONObject obj){
        super();

        try {
            initCompent(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    
    public Bill(int type) {
        super();
        this.type = type;
        this.id = 0;
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
        uid = json.getInt("uid");
        type = json.getInt("type");
        name = json.getString("name");
        bank = json.getString("bank");
        card = json.getString("card");
        price = json.getDouble("price");
        number = json.getInt("number");
        mechanism = json.getString("mechanism");
        repayment = json.getLong("repayment");
        
        if (!json.isNull("user")) {
            user = new Login(json.getJSONObject("user"));
        }
    }

	public static List<Bill> constructList(JSONArray array) {
		try {
			List<Bill> billList = new ArrayList<Bill>();
			int size = array.length();

			for (int i = 0; i < size; i++) {
				billList.add(new Bill(array.getJSONObject(i)));
			}
            
			return billList;
		} catch (JSONException jsone) {
			jsone.printStackTrace();
		}
		
		return null;
	}
}