package com.chengxin.Entity;


import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

public class MessageInfo extends SNSMessage{
	private static final long serialVersionUID = -4274108350647182194L;

	public String id; 			//消息ID
	public String tag = "";				//消息标识符
	public String fromid;  // 发送者id
	public String fromname; //发送者name
	public String fromurl; //发送者头像
	
	public String toid; //接收者，可以是某人，也可以是某个群id
	public String toname;//接收者name
	public String tourl;//接收者头像
	
	public String image;//上传图片
	public String imgUrlS = "";			//小图URL
	public String imgUrlL = "";			//大图URL
	public int imgWidth;				//小图宽度
	public int imgHeight;				//小图高度
	
	public String voice;//声音
	public String voiceUrl = "";		//音频URL
	public int voicetime;//声音时间长度
	public int isReadVoice = 0;
	
	public String videoUrl ="";				//视频url
	public int videotime = 0;               //视频长度
	
	public double mLat = 0;				//纬度
	public double mLng = 0;				//经度
	public String mAddress = "";		//地址
	
	
	public String content; //消息的文字内容
	
	public int typechat =100;//100-单聊 200-群聊 300-临时会话 默认为100
	public int typefile;//1-文字 2-图片 3-声音 4-位置 
	public long time;//发送消息的时间,毫秒（服务器生成）
	
	public Login mUser;
	
	public int sendState; // 消息发送成功与否的状态  1 成功, 2 正在发送， 4， 正在下载。0 失败
	public int readState; // 读取消息的状态.
	
	
	public long sendTime;   // 对方发送的时间
	public long pullTime;	// 得到消息的时间
	public int auto_id;
	public int sampleRate = 8000;		//播放音频采样率
	
	public int systeMessage = 0; //1-系统消息 0-聊天消息 2-秘室消息
	
	public String cardOwerName;//拥有名片者姓名
	
	public String imageString;//收到的图片信息
	public String   voiceString ;//收到的语音信息

	
	public int position;
	
	public MessageInfo(){
		
	}

	public MessageInfo(String reString){
		try {
			JSONObject json = new JSONObject(reString);
			if(!json.isNull("content")){
				content = json.getString("content");
			}
			
			if(!json.isNull("fromId")){
				fromid = json.getString("fromId");
			}
			
			if(!json.isNull("typechat")){
				typechat = json.getInt("typechat");
			}
			
			if(!json.isNull("sendTime")){
				sendTime = json.getLong("sendTime");
			}
			
			if(!json.isNull("toId")){
				toid = json.getString("toId");
			}
			
			if(!json.isNull("typefile")){
				typefile = json.getInt("typefile");
			}
			
			if(!json.isNull("voicetime")){
				voicetime = json.getInt("voicetime");
			}
			
		
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public MessageInfo (JSONObject json){
		try {
			
			if(!json.isNull("id")){
				id = json.getString("id");
			}
			
			if(!json.isNull("tag")){
				tag = json.getString("tag");
			}
			
			if(!json.isNull("typechat")){
				typechat = json.getInt("typechat");
			}
			
			if(!json.isNull("content")){
				content = json.getString("content");
			}
			
			if(!json.isNull("from")){
				JSONObject from = json.getJSONObject("from");
				fromid = from.getString("id");
				fromname= from.getString("name");
				fromurl = from.getString("url");
			}
			
			if(!json.isNull("time")){
				time = json.getLong("time");
			}
			
			if(!json.isNull("to")){
				JSONObject to = json.getJSONObject("to");
				toid = to.getString("id");
				toname = to.getString("name");
				tourl = to.getString("url");
			}
			
			if(!json.isNull("voice")){
				String voiceString = json.getString("voice");
				if(voiceString!=null && !voiceString.equals("") && voiceString.startsWith("{")){
					this.voiceString = voiceString;
					JSONObject voice = json.getJSONObject("voice");
					
					if(!voice.isNull("time")){
						voicetime = voice.getInt("time");
					}
					
					if(!voice.isNull("url")){
						voiceUrl = voice.getString("url");
					}
				}
			}
			
			
			if(!json.isNull("video")){
				String voiceString = json.getString("video");
				if(voiceString!=null && !voiceString.equals("") 
						&& voiceString.startsWith("{")){
					//this.videoString = voiceString;
					JSONObject voice = json.getJSONObject("video");
					
					if(!voice.isNull("time")){
						videotime = voice.getInt("time");
					}
					
					if(!voice.isNull("url")){
						videoUrl = voice.getString("url");
					}
				}
			}
			
			if(!json.isNull("image")){
				String imageString = json.getString("image");
				if(imageString!=null && !imageString.equals("") && imageString.startsWith("{")){
					this.imageString = imageString;
					JSONObject image = json.getJSONObject("image");
					imgUrlS = image.getString("urlsmall");
					imgUrlL = image.getString("urllarge");
					imgWidth = image.getInt("width");
					imgHeight = image.getInt("height");
				}
				
			}
			
			if(!json.isNull("location")){
				String locationString = json.getString("location");
				if(locationString!=null && !locationString.equals("")){
					JSONObject location = json.getJSONObject("location");
					mLat = location.getDouble("lat");
					mLng = location.getDouble("lng");
					mAddress = location.getString("address");
				}
				
			}
			
			if(!json.isNull("typefile")){
				typefile = json.getInt("typefile");
			}
			
			mUser = new Login();
			mUser.nickname = fromname;
			mUser.headsmall = fromurl;
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	
	

	public long getSendTime() {
		return sendTime;
	}

	public long getPullTime() {
		return pullTime;
	}

	public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
	}

	public void setPullTime(long pullTime) {
		this.pullTime = pullTime;
	}


	public String getToId() {
		return toid;
	}

	public String getFromId() {
		return fromid;
	}

	public int getType() {
		return typefile;
	}

	public String getContent() {
		return content;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setToId(String toId) {
		this.toid = toId;
	}

	public void setFromId(String fromId) {
		this.fromid = fromId;
	}

	public int getSendState() {
		return sendState;
	}

	public int getReadState() {
		return readState;
	}

	public void setSendState(int sendState) {
		this.sendState = sendState;
	}

	public void setReadState(int readState) {
		this.readState = readState;
	}

	public void setType(int type) {
		this.typefile = type;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getVoiceTime() {
		return voicetime;
	}

	public void setVoiceTime(int voiceTime) {
		this.voicetime = voiceTime;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + tag.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MessageInfo other = (MessageInfo) obj;
		if (tag != other.tag)
			return false;
		return true;
	}

}
