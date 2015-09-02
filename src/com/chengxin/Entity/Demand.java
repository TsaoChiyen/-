package com.chengxin.Entity;

import java.io.Serializable;

import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

/**
 *  需求信息
 */
public class Demand implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    public int id;              //< 需求id
    public int uid;             //< 用户id
    public String content;      //< 内容
    public double lat;          //< 纬度
    public double lng;          //< 经度
    public double distance;     //< 距离
    public long createtime;     //< 创建时间
    
    public Login user;          //< 发布人信息
    public WeiYuanState state;  //< 返回的状态对象
    
    public Demand(String reqString) {
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
    
    public Demand(JSONObject obj){
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
        
        id = json.getInt("id");
        uid = json.getInt("uid");
        content = json.getString("content");
        lat = json.getDouble("lat");
        lng = json.getDouble("lng");
        distance = json.getDouble("distance");
        createtime = json.getLong("createtime");
        
        user = new Login(json.getJSONObject("user"));
    }
}