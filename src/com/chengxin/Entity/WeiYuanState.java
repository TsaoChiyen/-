package com.chengxin.Entity;

import java.io.Serializable;

import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

public class WeiYuanState implements Serializable{
	//{"data":{},"state":{"code":0,"msg":"\u6dfb\u52a0\u597d\u53cb\u6210\u529f","debugMsg":""}}
	//{"data":{"id":328},"state":{"code":0,"msg":"\u5206\u4eab\u6210\u529f","debugMsg":""}}
	private static final long serialVersionUID = 149681634654564865L;
	
	public int code =-1;
	public String errorMsg = "";
	public String debugMsg = "";
	public String validCode;
	public String frontCover;
	public String order_sn ;
	public String data;
	public int weiboId;
	public int positon;
	public String uid;
	public int changeType; //新的朋友更换的状态
	public FriendsLoopItem friendsLoopitem;
	
	public WeiYuanState(){}
	
	
	
	public WeiYuanState(String validCode,int code) {
		super();
		this.validCode = validCode;
		this.code = code;
	}



	public WeiYuanState(JSONObject json) {
		try {
			if(!json.isNull("data")){
				JSONObject obj = json.getJSONObject("data");
				if(obj!=null && !obj.equals("")){
					if(!obj.isNull("id")){
						weiboId = obj.getInt("id");
					}
					if(!obj.isNull("code")){
						validCode = obj.getString("code");
					}
					if(!obj.isNull("cover")){
						frontCover = obj.getString("cover");
					}
					if(!obj.isNull("order_sn")){
						order_sn  = json.getString("order_sn");
					}
				}
			}
			if(!json.isNull("state")){
				String str = json.getString("state");
				if(str!=null && !str.equals("")){
					JSONObject childObj = json.getJSONObject("state");
					if(!childObj.isNull("code")){
						code = childObj.getInt("code");
					}
					
					if(!childObj.isNull("msg")){
						errorMsg = childObj.getString("msg");
					}
					if(!childObj.isNull("debugMsg")){
						debugMsg = childObj.getString("debugMsg");
					}
					if(!childObj.isNull("frontCover")){
						frontCover = childObj.getString("frontCover");
					}
				}
			}else{
				if(!json.isNull("code")){
					code = json.getInt("code");
				}
				
				if(!json.isNull("msg")){
					errorMsg = json.getString("msg");
				}
				if(!json.isNull("debugMsg")){
					debugMsg = json.getString("debugMsg");
				}
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

}
