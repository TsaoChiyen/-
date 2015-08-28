package com.chengxin.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomViewPager extends ViewPager{

	@Override
    public boolean onTouchEvent(MotionEvent arg0) {
	    //return super.onTouchEvent(arg0);
		return false;
    }

	@Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
	    //return super.onInterceptTouchEvent(arg0);
		return false;
    }

	public CustomViewPager(Context context, AttributeSet attrs) {
	    super(context, attrs);
    }

	public CustomViewPager(Context context) {
	    super(context);
    }

}
