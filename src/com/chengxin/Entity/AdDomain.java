package com.chengxin.Entity;import java.io.Serializable;import java.util.ArrayList;import java.util.List;import com.chengxin.org.json.JSONArray;import com.chengxin.org.json.JSONException;import com.chengxin.org.json.JSONObject;public class AdDomain implements Serializable {	/**	 * 	 */	private static final long serialVersionUID = 1L;	private static List<AdDomain> adList = new ArrayList<AdDomain>();		public String id;  	public String date;	public String prefix;	public String title;	public String topicFrom; 	public String topic;	public String imgUrl;	public int isAd;	public String startTime;	public String endTime;	public String targetUrl;	public int width;	public int height;	public int available;	public WeiYuanState state;	public boolean isUrl;	public AdDomain() {		super();	}	public AdDomain(String reqString) {		super();		try {			if(reqString == null || reqString.equals("")){				return;			}			initCompent(new JSONObject(reqString));		} catch (JSONException e) {			e.printStackTrace();		}	}	public AdDomain(JSONObject obj){		try {			initCompent(obj);		} catch (JSONException e) {			e.printStackTrace();		}	}		private void initCompent(JSONObject json) throws JSONException{		if(json == null || json.equals("")){			return;		}// data - id		if(!json.isNull("data")){			String dataString = json.getString("data");			if(dataString != null && !dataString.equals("")					&& dataString.startsWith("{")){				init(json.getJSONObject("data"));			}		}else{			init(json);		}		if(!json.isNull("state")){			String stateString = json.getString("state");			if(stateString != null && !stateString.equals("")					&& stateString.startsWith("{")){				state = new WeiYuanState(json.getJSONObject("state"));			}		}	}	private void init(JSONObject json) throws JSONException{		if(json == null || json.equals("")){			return;		}		id          = json.getString("id");		prefix 		= json.getString("prefix");		date      	= json.getString("date");		title  		= json.getString("title");		topicFrom   = json.getString("topicFrom");		topic       = json.getString("topic");		imgUrl      = json.getString("imgUrl");		startTime 	= json.getString("startTime");		endTime  	= json.getString("endTime");		targetUrl  	= json.getString("targetUrl");		isAd        = json.getInt("isAd");		width     	= json.getInt("width");		height   	= json.getInt("height");		available   = json.getInt("available");		isUrl		= true;	}        public static List< AdDomain > constructList(JSONArray array){        try {            List< AdDomain > list = new ArrayList< AdDomain >();            int size = array.length();                        for (int i = 0; i < size; i++) {            	list.add(new AdDomain(array.getJSONObject(i)));            }            if (list.size() > 0) {                if (adList == null) {                	adList = new ArrayList<AdDomain>();                } else {                	adList.clear();                }                                adList.addAll(list);            }                        return adList;        } catch (JSONException jsone) {            jsone.printStackTrace();        }                return null;    }	public static boolean hasData() {		return adList.size() > 0;	}	public static List<AdDomain> getAdList() {		return adList;	}	public static void setAdList(List<AdDomain> list) {		if (adList ==  null) {			adList = new ArrayList<AdDomain>();		} else {			adList.clear();		}				if (list != null && list.size() > 0) {			adList.addAll(list);		}	}}