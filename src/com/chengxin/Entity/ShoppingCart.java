package com.chengxin.Entity;

import java.io.Serializable;

/**
 * 购物车数据
 * @author dl
 *
 */
public class ShoppingCart implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**登录用户id*/
	public  String uid;
	
	/**商家id  1,2,3*/
	public int shopId;
	
	/**商品id 1,2,3,4*/
	public String goodsIds;
	
	/**商品数量*/
	public String goodsCounts;

	/**商家类型*/
	public int shopType = 0;

	public ShoppingCart(String uid, int shopId, String goodsIds,
			String goodsCounts, int shopType) {
		super();
		this.uid = uid;
		this.shopId = shopId;
		this.goodsIds = goodsIds;
		this.goodsCounts = goodsCounts;
		this.shopType = shopType;
	}

	public ShoppingCart() {
		super();
	}
	 
}
