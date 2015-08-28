package com.chengxin.Entity;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;

public class ChatImg implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String small;
	private String origin;
	private String imgWidth;
	private String imgHeight;
	
	
	
	
	
	public ChatImg(String small, String origin, String imgWidth,
			String imgHeight) {
		super();
		this.small = small;
		this.origin = origin;
		this.imgWidth = imgWidth;
		this.imgHeight = imgHeight;
	}



	public ChatImg() {
		super();
	}



	public ChatImg(String small) {
		super();
		this.small = small;
	}




	public static ChatImg getInfo(String json) {
		try {
			return JSONObject.toJavaObject(JSONObject.parseObject(json),
					ChatImg.class);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getInfo(ChatImg info) {
		String json = JSONObject.toJSON(info).toString();
		return json;
	}
	
	
	
	
	public String getSmall() {
		return small;
	}



	public void setSmall(String small) {
		this.small = small;
	}



	public String getOrigin() {
		return origin;
	}



	public void setOrigin(String origin) {
		this.origin = origin;
	}



	public String getImgWidth() {
		return imgWidth;
	}



	public void setImgWidth(String imgWidth) {
		this.imgWidth = imgWidth;
	}



	public String getImgHeight() {
		return imgHeight;
	}



	public void setImgHeight(String imgHeight) {
		this.imgHeight = imgHeight;
	}



	public static long getSerialversionuid() {
		return serialVersionUID;
	}



	@Override
	public String toString() {
		return "ChatImg [small=" + small + ", origin=" + origin + ", imgHeight=" + imgHeight
				+ ", imgWidth=" + imgWidth + "]";
	}
}
