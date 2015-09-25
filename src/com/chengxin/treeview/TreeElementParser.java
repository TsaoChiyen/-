package com.chengxin.treeview;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.chengxin.Entity.ChildCity;
import com.chengxin.Entity.Country;
import com.chengxin.Entity.Login;
import com.chengxin.Entity.WeiYuanProjectItem;

/**
 * 类名称：TreeElementParser.java
 * @author wader
 * 类描述：树形组件节点解析类，将树节点类（TreeElement）字符串信息集合解析为属性节点类集合
 * 创建时间：2011—11-29 17:36
 */

public class TreeElementParser {

	private static final String TAG = "TreeElementParser";
	/**
	 * 把节点字符串信息集合解析成节点集合 这里的解析可根据实际情况进行改动
	 * @param <T>
	 *
	 * @param list
	 * @return
	 */
	public static <T> List<TreeElement> getTreeElements(List<T> list) {
		if (list == null) {
			Log.d(TAG,
					"the string list getted from solarterm.properties by ResManager.loadTextRes is null");
			return null;
		}

		List<TreeElement> treeElements = new ArrayList<TreeElement>();

		for (int i = 0; i < list.size(); i++) {
			boolean hasChild_1 = false;
			T t = list.get(i);
			if(t instanceof Country){
				Country contry = (Country) t;
				List<ChildCity> childCities = contry.childList;

				for (int j = 0; childCities != null && j < childCities.size(); j++) {
					boolean hasChild_2 = false;
					List<Login> usrLists = childCities.get(j).userList;
					for (int k = 0; usrLists != null && k < usrLists.size(); k++) {

						TreeElement element = new TreeElement();
						element.setId(usrLists.get(k).uid);
						element.setLevel(3);
						element.setTitle(usrLists.get(k).name);
						element.setHeadUrl(usrLists.get(k).headsmall);
						element.setSign(usrLists.get(k).sign);
						element.setFold(false);
						element.setBigItem(true);
						element.setHasChild(false);
						element.setHasParent(true);
						element.setParentId(String.valueOf(childCities.get(j).id));
						treeElements.add(element);

						hasChild_2 = true;
					}

					if (hasChild_2) {
						TreeElement element = new TreeElement();
						element.setId(String.valueOf(childCities.get(j).id));
						element.setLevel(2);
						element.setTitle(childCities.get(j).text);
						element.setFold(false);
						element.setHasChild(hasChild_2);
						element.setHasParent(true);
						element.setParentId(contry.countryID);
						treeElements.add(element);

						hasChild_1 = true;
					}
				}

				if (hasChild_1) {
					TreeElement element = new TreeElement();
					element.setId(contry.countryID);
					element.setLevel(1);
					element.setTitle(contry.country);
					element.setFold(false);
					element.setHasChild(hasChild_1);
					element.setHasParent(false);
					element.setParentId("null");
					treeElements.add(element);
				}
			}else if(t instanceof WeiYuanProjectItem){
				WeiYuanProjectItem weiYuanProjectItem = (WeiYuanProjectItem)t;
				List<Login> usrLists = weiYuanProjectItem.userList;
				for (int k = 0; usrLists != null && k < usrLists.size(); k++) {

					TreeElement element = new TreeElement();
					element.setId(usrLists.get(k).uid);
					element.setLevel(2);
					element.setTitle(usrLists.get(k).name);
					element.setHeadUrl(usrLists.get(k).headsmall);
					element.setBigItem(true);
					element.setSign(usrLists.get(k).sign);
					element.setFold(false);
					element.setHasChild(false);
					element.setHasParent(true);
					element.setParentId(weiYuanProjectItem.id);
					treeElements.add(element);
					hasChild_1 = true;
				}
				if (hasChild_1) {
					TreeElement element = new TreeElement();
					element.setId(weiYuanProjectItem.id);
					element.setLevel(1);
					element.setTitle(weiYuanProjectItem.text);
					element.setFold(false);
					element.setHasChild(hasChild_1);
					element.setHasParent(false);
					element.setParentId("null");
					treeElements.add(element);
				}
			}

		} 

		return treeElements;
	}

	public static <T> List<TreeElement> getTreeMenuElements(List<T> list) {
		if (list == null) {
			Log.d(TAG,
					"the string list getted from solarterm.properties by ResManager.loadTextRes is null");
			return null;
		}

		List<TreeElement> treeElements = new ArrayList<TreeElement>();

		for (int i = 0; i < list.size(); i++) {
			T t = list.get(i);
			if(t instanceof Country){
				Country contry = (Country) t;
				List<ChildCity> childCities = contry.childList;
				boolean hasChild_1 = false;
				for (int j = 0; childCities != null && j < childCities.size(); j++) {

					TreeElement element = new TreeElement();
					element.setId(String.valueOf(childCities.get(j).id));
					element.setLevel(2);
					element.setTitle(childCities.get(j).text);
					element.setFold(false);
					element.setHasChild(false);
					element.setHasParent(true);
					element.setParentTitle(contry.country);
					element.setParentId(contry.countryID);
					treeElements.add(element);
					hasChild_1 = true;
				}

				if (hasChild_1) {
					TreeElement element = new TreeElement();
					element.setId(contry.countryID);
					element.setLevel(1);
					element.setTitle(contry.country);
					element.setFold(false);
					element.setHasChild(hasChild_1);
					element.setHasParent(false);
					element.setParentId("null");
					treeElements.add(element);
				}
			}else if(t instanceof WeiYuanProjectItem){
				WeiYuanProjectItem weiYuanProjectItem = (WeiYuanProjectItem)t;
					TreeElement element = new TreeElement();
					element.setId(weiYuanProjectItem.id);
					element.setLevel(1);
					element.setTitle(weiYuanProjectItem.text);
					element.setFold(false);
					element.setHasChild(false);
					element.setHasParent(false);
					element.setParentId("null");
					treeElements.add(element);
			}

		} 

		return treeElements;
	}
}