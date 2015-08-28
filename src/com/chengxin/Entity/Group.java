package com.chengxin.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.chengxin.org.json.JSONArray;
import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

public class Group implements Serializable{

	private static final long serialVersionUID = -611345454874691L;
	
	/**
	 *  "id": "0",
      "uid": "200274",
      "teamName": "未分组",
      "userList": [
	 */
	
	public int id;
	public String teamName ="未分组";
	public List<Login> mUserList;//objcet
	public List<Login> mStarList;//object
	public boolean isCheck = false;
	
	public Group(){}
	
	public Group(JSONObject json){
		try {
			/*if(!json.isNull("id")){
				id = json.getInt("id");
			}*/
			if(!json.isNull("teamName")){
				teamName = json.getString("teamName");
			}
			mUserList = new ArrayList<Login>();
			mStarList = new ArrayList<Login>();
			if(!json.isNull("isstar")){
				if(json.getInt("isstar") == 1){
					Login login = new Login(json);
					login.sort = "☆";
					login.sortName = "星标朋友";
					mStarList.add(login);
				}
			}
			mUserList.add(new Login(json));
			/*//获取用户集合
			if(!json.isNull("userList")){
				JSONArray array = json.getJSONArray("userList");
				if(array != null && array.length() != 0){
					mUserList = new ArrayList<Login>();
					for (int i = 0; i < array.length(); i++) {
						Login user = new Login(array.getJSONObject(i));
						user.groupId = id;
						if(id >= 0){
							user.groupName = teamName;
						}
						mUserList.add(user);
					}
				}
			}*/
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

}
