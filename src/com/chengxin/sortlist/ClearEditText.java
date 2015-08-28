package com.chengxin.sortlist;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;

import com.chengxin.R;

public class ClearEditText extends EditText implements  
        OnFocusChangeListener, TextWatcher { 
	
    private Drawable mClearDrawable; 
    public interface OnClearClick
	{
		void onClearListener();
	}
    
    
    private OnClearClick mOnClearClick;
    public void setOnClearClickLister( OnClearClick alertDo){
    	this.mOnClearClick = alertDo;
    }
	
 
    public ClearEditText(Context context) { 
    	this(context, null); 
    } 
 
    public ClearEditText(Context context, AttributeSet attrs) { 
    	this(context, attrs, android.R.attr.editTextStyle); 
    } 
    
    public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    
    
    private void init() { 
    	mClearDrawable = getCompoundDrawables()[2]; 
        if (mClearDrawable == null) { 
        	mClearDrawable = getResources() 
                    .getDrawable(R.drawable.clear_btn); 
        } 
        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight()); 
        setClearIconVisible(false); 
        setOnFocusChangeListener(this); 
        addTextChangedListener(this); 
    } 
 

    @Override 
    public boolean onTouchEvent(MotionEvent event) { 
        if (getCompoundDrawables()[2] != null) { 
            if (event.getAction() == MotionEvent.ACTION_UP) { 
            	boolean touchable = event.getX() > (getWidth() 
                        - getPaddingRight() - mClearDrawable.getIntrinsicWidth()) 
                        && (event.getX() < ((getWidth() - getPaddingRight())));
                if (touchable) { 
                	if(mOnClearClick!=null){
                		mOnClearClick.onClearListener();
                	}
                	
                    this.setText(""); 
                } 
            } 
        } 
 
        return super.onTouchEvent(event); 
    } 
 
    /**
     * 锟斤拷ClearEditText锟斤拷锟姐发锟斤拷浠拷锟绞憋拷锟斤拷卸锟斤拷锟斤拷锟斤拷址锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷图锟斤拷锟斤拷锟绞撅拷锟斤拷锟斤拷锟�
     */
    @Override 
    public void onFocusChange(View v, boolean hasFocus) { 
        if (hasFocus) { 
            setClearIconVisible(getText().length() > 0); 
        } else { 
            setClearIconVisible(false); 
        } 
    } 
 
 
    /**
     * 锟斤拷锟斤拷锟斤拷锟酵硷拷锟斤拷锟斤拷示锟斤拷锟斤拷锟截ｏ拷锟斤拷锟斤拷setCompoundDrawables为EditText锟斤拷锟斤拷锟斤拷去
     * @param visible
     */
    protected void setClearIconVisible(boolean visible) { 
        Drawable right = visible ? mClearDrawable : null; 
        setCompoundDrawables(getCompoundDrawables()[0], 
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]); 
    } 
     
    
    /**
     * 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷莘锟斤拷锟戒化锟斤拷时锟斤拷氐锟斤拷姆锟斤拷锟�
     */
    @Override 
    public void onTextChanged(CharSequence s, int start, int count, 
            int after) { 
        setClearIconVisible(s.length() > 0); 
    } 
 
    @Override 
    public void beforeTextChanged(CharSequence s, int start, int count, 
            int after) { 
         
    } 
 
    @Override 
    public void afterTextChanged(Editable s) { 
         
    } 
    
   
    /**
     * 锟斤拷锟矫晃讹拷锟斤拷锟斤拷
     */
    public void setShakeAnimation(){
    	this.setAnimation(shakeAnimation(5));
    }
    
    
    /**
     * 锟轿讹拷锟斤拷锟斤拷
     * @param counts 1锟斤拷锟接晃讹拷锟斤拷锟斤拷锟斤拷
     * @return
     */
    public static Animation shakeAnimation(int counts){
    	Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
    	translateAnimation.setInterpolator(new CycleInterpolator(counts));
    	translateAnimation.setDuration(1000);
    	return translateAnimation;
    }
 
 
}
