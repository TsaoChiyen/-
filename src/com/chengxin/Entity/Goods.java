package com.chengxin.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.chengxin.org.json.JSONArray;
import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

/**
 * 商品
 * @author dl
 *
 */
public class Goods implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    public String id;               //< 商品id
    public int shopid;              //< 商铺id
    public int categoryid;          //< 商铺类型
    public String name;             //< 商品名称
    public String logo;             //< 商品图片地址
    public double price;            //< 商品价格
    public int star;                //< 星级数
    public int commentCount;        //< 商品评论数
    public long createtime;         //< 创建时间
    public int isfavorite;          //< 是否收藏
    public String introduce;        //< 商品介绍
    public String content;          //< 图文详细
    public String parameter;        //< 产品参数
    public String barcode;          //< 商品条码
    public int number;              //< 商品库存量
    public int status;              //< 商品状态 1，未上架； 2，已上架
    public int count;               //< 商品数量

    public List<Picture> pictureList;   //< 图片集
    public GoodsComment comment;        //< 评论内容

    public WeiYuanState state;          //< 返回的状态对象
    
    public boolean selected;            //< ui操作，是否选择

	public Goods() {
		super();
	}

	public Goods(String reqString) {
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

	public Goods(JSONObject obj){
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
		
		id          = json.getString("id");
		shopid      = json.getInt("shopid");
		categoryid  = json.getInt("categoryid");
        name        = json.getString("name");
		logo        = json.getString("logo");
		price       = json.getDouble("price");
		star        = json.getInt("star");
        commentCount = json.getInt("commentcount");
        createtime  = json.getLong("createtime");
        isfavorite  = json.getInt("isfavorite");
        content     = json.getString("content");
        parameter   = json.getString("parameter");
        barcode     = json.getString("barcode");
        number      = json.getInt("number");
        status      = json.getInt("status");
        count       = json.getInt("count");
        selected    = false;

        if(!json.isNull("picture")){
			String picString = json.getString("picture");
            
			if((picString != null && !picString.equals(""))
					&& picString.startsWith("[")){
				JSONArray array = json.getJSONArray("picture");
			
                if(array != null && array.length()>0){
					pictureList = new ArrayList<Picture>();
				
                    for (int i = 0; i < array.length(); i++) {
						pictureList.add(Picture.getInfo(array.getString(i)));
					}
				}
			}
		}
		
		if(!json.isNull("comment")) {
			String commentString = json.getString("comment");
            
			if((commentString != null && !commentString.equals(""))
					&& commentString.startsWith("{")){
				comment = new GoodsComment(json.getJSONObject("comment"));
			}
		}
	}
    
    public static List<Goods> constructGoodsList(JSONArray array){
        try {
            List<Goods> goodsList = new ArrayList<Goods>();
            int size = array.length();
            
            for (int i = 0; i < size; i++) {
                goodsList.add(new Goods(array.getJSONObject(i)));
            }
            
            return goodsList;
        } catch (JSONException jsone) {
            jsone.printStackTrace();
        }
        
        return null;
    }

}
