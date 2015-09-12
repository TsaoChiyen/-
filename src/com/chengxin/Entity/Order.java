package com.chengxin.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.chengxin.org.json.JSONArray;
import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

public class Order implements Serializable{

	private static final long serialVersionUID = 143456464564165L;
	
    /**{
        "id": "29",
        "type": "1",
        "ordersn": "2015083049494856",
        "uid": "200423",
        "shopid": "48",
        "username": "发生",
        "phone": "123",
        "content": "",
        "address": "fdasd",
        "goods": [
                  {
                  "id": "51",
                  "count": "2",
                  "logo": "http://121.40.214.35:8000/Uploads/Picture/goods/20150817/s_fabcc31beb718860ead1293318d946dd.jpg",
                  "price": "1200.00",
                  "name": "gods"
                  }
                  ],
        "totalPrice": "222.00",
        "createtime": "1440939712",
        "deliverytime": null,
        "overTime": null,
        "logCompany": null,
        "logNumber": null,
        "status": "1",
        "shop": {
            "id": "48",
            "uid": "200423",
            "logo": "",
            "name": "xiak",
            "password": "96e79218965eb72c92a549dd5a330112",
            "username": "liu",
            "phone": "18983765245",
            "content": "",
            "useraddress": "",
            "address": "AAA",
            "lat": "29.573602",
            "lng": "106.545402",
            "city": "重庆",
            "shopPaper": "",
            "authPaper": "",
            "bank": "asdsad",
            "bankName": "asdadsad",
            "bankCard": "sdfdsfsfdsfdsf",
            "status": "1",
            "createtime": "1438071060",
            "service": [
                        {
                        "id": "11",
                        "uid": "200424",
                        "shopid": "48",
                        "name": "566",
                        "username": "15923562971"
                        },
                        {
                        "id": "12",
                        "uid": "200423",
                        "shopid": "48",
                        "name": "123",
                        "username": "13452372605"
                        },
                        {
                        "id": "14",
                        "uid": "200424",
                        "shopid": "48",
                        "name": "abcd",
                        "username": "15923562971"
                        }
                        ]
        }
    }
    */
    public String id;
    public String type;
    public String ordersn;
    public String uid;
    public String shopid;
    public String username;
    public String phone;
    public String content;
    public String address;
    public String createtime;
    public String deliverytime;
    public String overtime;
    public String status;
    public String logcompany;
    public String lognumber;
    
    public boolean selected = false;
    public boolean canCancel = false;

    public List <Goods> goods;
    public Merchant shop;

    public int 		totalCount;
    public float	totalPrice;

	public Order(){}
	
	public Order(JSONObject json) {
		try {
			id = json.getString("id");

            if(!json.isNull("type"))			type            = json.getString("type");
			if(!json.isNull("ordersn"))			ordersn         = json.getString("ordersn");
			if(!json.isNull("uid"))             uid             = json.getString("uid");
			if(!json.isNull("shopid"))			shopid          = json.getString("shopid");
			if(!json.isNull("username"))		username        = json.getString("username");
			if(!json.isNull("phone"))			phone           = json.getString("phone");
			if(!json.isNull("content"))			content         = json.getString("content");
			if(!json.isNull("address"))			address         = json.getString("address");
//			if(!json.isNull("totalPrice"))		totalPrice      = json.getString("totalPrice");
			if(!json.isNull("createtime"))		createtime      = json.getString("createtime");
			if(!json.isNull("deliverytime"))    deliverytime    = json.getString("deliverytime");
			if(!json.isNull("overTime"))		overtime        = json.getString("overTime");
			if(!json.isNull("logCompany"))		logcompany      = json.getString("logCompany");
			if(!json.isNull("logNumber"))		lognumber       = json.getString("logNumber");
			if(!json.isNull("status"))			status          = json.getString("status");

            if(!json.isNull("goods")){
				JSONArray array = json.getJSONArray("goods");
                
				if(array != null){
					goods = new ArrayList<Goods>();
                    
					List<Goods> tempList = Goods.constructGoodsList(array);
                    
					if(tempList != null){
						goods.addAll(tempList);
					}
				}
			}
            
            if(!json.isNull("shop")){
                JSONObject object = json.getJSONObject("shop");
                
                if(object != null){
                    shop = new Merchant(object);
                }
            }
            
            caculateSum();
            
		} catch ( NumberFormatException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void caculateSum() {
		totalCount = 0;
		totalPrice = 0;
		
		if (goods != null && goods.size() > 0) {
			for (int i = 0; i < goods.size(); i++) {
				Goods item = goods.get(i);
				totalCount += item.count;
				totalPrice += item.count * item.price;
			}
		}
	}

	public static List<Order> constructOrderList(JSONArray array){
		try {
			List<Order> orderList = new ArrayList<Order>();
			int size = array.length();

			for (int i = 0; i < size; i++) {
				orderList.add(new Order(array.getJSONObject(i)));
			}
            
			return orderList;
		} catch (JSONException jsone) {
			jsone.printStackTrace();
		}
		
		return null;
	}
}
