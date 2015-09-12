package com.chengxin.Entity;

import java.io.Serializable;

import com.chengxin.global.FeatureFunction;

/**
 * popwindow 子项
 * @author dongli
 *
 */
public class PopItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int id;
	public String option;
	public int resource_id;
	public boolean isVisible;
	public PopItem(int id, String menu_name,String resource_name) {
		super();
		this.id = id;
		this.option = menu_name;
		if(resource_name!=null && !resource_name.equals("")){
			this.resource_id = FeatureFunction.getSourceIdByName(resource_name);
		}
	}
	public PopItem(int id, String menu_name) {
		super();
		this.id = id;
		this.option = menu_name;
		
		if(menu_name!=null && !menu_name.equals("")){
			try {
				this.resource_id = FeatureFunction.getSourceIdByName(menu_name);
			} catch (Exception e) {
				e.printStackTrace();
				this.resource_id = 0;
			}
		}
	}

	public PopItem() {
		super();
	}
	public PopItem(int id) {
		super();
		this.id = id;
	}



}
