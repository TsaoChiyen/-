package com.chengxin.Entity;

import java.io.Serializable;

public class CookieEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String cookieName;
	public String cookieValue;
	public String cookieDomain;
	public CookieEntity() {
		super();
	}
	public CookieEntity(String cookieName, String cookieValue,
			String cookieDomain) {
		super();
		this.cookieName = cookieName;
		this.cookieValue = cookieValue;
		this.cookieDomain = cookieDomain;
	}
	
	
	
	
	//cookie.getName() + "=" + cookie.getValue() + "; domain=" +cookie.getDomain()
}
