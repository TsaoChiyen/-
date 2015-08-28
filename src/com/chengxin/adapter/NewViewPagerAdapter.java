package com.chengxin.adapter;

import java.util.List;

import android.os.Parcelable;
import android.view.View;

import com.chengxin.widget.ViewPager;

public class NewViewPagerAdapter extends ShoujibaoPagerAdapter{

	private List<View> views;

	public NewViewPagerAdapter(List<View> views) {
		this.views = views;
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
	}

	@Override
	public void finishUpdate(View arg0) {
	}

	@Override
	public int getCount() {
		return views.size();
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		try {
			((ViewPager) arg0).addView(views.get(arg1%views.size()), 0);
		} catch (Exception e) {
		}

		return views.get(arg1%views.size());
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
	}
}
