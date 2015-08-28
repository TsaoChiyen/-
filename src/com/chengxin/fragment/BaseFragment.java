package com.chengxin.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chengxin.MainActivity;
import com.chengxin.R;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.GlobleType;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.widget.CustomProgressDialog;

/**
 * Fragment基础页面
 */
public abstract class BaseFragment extends Fragment {
	protected Context mContext;
	protected int mWidth = 0;
	protected CustomProgressDialog mProgressDialog;
	
	public final static int LIST_LOAD_FIRST = 501;
	public final static int LIST_LOAD_REFERSH = 502;
	public final static int LIST_LOAD_MORE = 503;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this.getActivity();
		DisplayMetrics metrics = new DisplayMetrics();
		this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		mWidth = metrics.widthPixels;
	}
	
	public Handler mBaseHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case GlobalParam.SHOW_PROGRESS_DIALOG:
                String dialogMsg = (String)msg.obj;
                boolean isCancelable = false;
                if (msg.arg1 == 1) {
                	isCancelable = true;
                }
                baseShowProgressDialog(dialogMsg, isCancelable);
                break;
            case GlobalParam.HIDE_PROGRESS_DIALOG:
            	baseHideProgressDialog();
                break;
                
            case GlobalParam.MSG_NETWORK_ERROR:
				Toast.makeText(mContext,R.string.network_error,Toast.LENGTH_LONG).show();
				return;
				
			case GlobalParam.MSG_TICE_OUT_EXCEPTION:
				String message=(String)msg.obj;
				if (message==null || message.equals("")) {
					message=mContext.getString(R.string.timeout);
				}
				Toast.makeText(mContext,message, Toast.LENGTH_LONG).show();
				break;
            }
        }
    };
    
  
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.setupViews(getView());
	}
	
	public void showProgressDialog(String msg, boolean isCancelable){
		baseShowProgressDialog(msg, isCancelable);
	}
	
	public void showProgressDialog(String msg){
		showProgressDialog(msg, true);
	}
	
	public void hideProgressDialog(){
		mBaseHandler.sendEmptyMessage(GlobalParam.HIDE_PROGRESS_DIALOG);
	}
	
	public void baseShowProgressDialog(String msg, boolean isCancelable){
		mProgressDialog = new CustomProgressDialog(mContext);
		mProgressDialog.setMessage(msg);
		mProgressDialog.setCancelable(isCancelable);
		mProgressDialog.show();
	}
	
	protected void baseHideProgressDialog(){
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}
	
	public void showToast(String content){
	    Toast.makeText(mContext, content, Toast.LENGTH_LONG).show();
	}
	
	
	
	 /**+++ for title bar +++*/
    protected ImageView mLeftIcon, mRightBtn,mSearchBtn,mAddBtn,mMoreBtn;
    //protected LinearLayout mRightBtn;
    protected TextView titileTextView,mFristTitlte,mTrowTitle,mRightTextBtn;
    protected RelativeLayout mFirstLayout;
    protected LinearLayout mLeftBtn,mCenterLayout;
    
    protected void setTitleContent(int left_src_id, int right_src_id, int title_id){
        mLeftBtn = (LinearLayout)this.getView().findViewById(R.id.left_btn);
        mLeftIcon = (ImageView)this.getView().findViewById(R.id.left_icon);
        mRightBtn = (ImageView)this.getView().findViewById(R.id.right_btn);
        mRightTextBtn = (TextView)this.getView().findViewById(R.id.right_text_btn);
        //mRightBtn = (LinearLayout)findViewById(R.id.right_btn);
        mSearchBtn = (ImageView)this.getView().findViewById(R.id.search_btn);
        mAddBtn = (ImageView)this.getView().findViewById(R.id.add_btn);
        mMoreBtn = (ImageView)this.getView().findViewById(R.id.more_btn);
        
        titileTextView = (TextView)this.getView().findViewById(R.id.title);
        
        if (left_src_id != 0) {
        	mLeftIcon.setImageResource(left_src_id);
        }
        
        if (right_src_id != 0) {
        	mRightBtn.setImageResource(right_src_id);
        	mRightBtn.setVisibility(View.VISIBLE);
        }
        
        if (title_id != 0) {
            titileTextView.setText(title_id);
        }
    }
    
    protected void setTrowMenuTitleContent(int left_src_id, int right_src_id, 
    		String firstTitlte,String trowTitle){
        mLeftBtn = (LinearLayout)this.getView().findViewById(R.id.left_btn);
        mLeftIcon = (ImageView)this.getView().findViewById(R.id.left_icon);
        mRightBtn = (ImageView)this.getView().findViewById(R.id.right_btn);
        mRightTextBtn = (TextView)this.getView().findViewById(R.id.right_text_btn);
        //mRightBtn = (LinearLayout)findViewById(R.id.right_btn);
        mSearchBtn = (ImageView)this.getView().findViewById(R.id.search_btn);
        mAddBtn = (ImageView)this.getView().findViewById(R.id.add_btn);
        mMoreBtn = (ImageView)this.getView().findViewById(R.id.more_btn);
        
        titileTextView = (TextView)this.getView().findViewById(R.id.title);
        titileTextView.setVisibility(View.GONE);
        
        mFristTitlte= (TextView)this.getView().findViewById(R.id.other_title);
        mTrowTitle= (TextView)this.getView().findViewById(R.id.child_title);
        if(firstTitlte!=null && !firstTitlte.equals("")){
        	mFristTitlte.setText(firstTitlte);
        	mFristTitlte.setVisibility(View.VISIBLE);
        }
        if(trowTitle!=null && !trowTitle.equals("")){
        	mTrowTitle.setText(trowTitle);
        	mTrowTitle.setVisibility(View.VISIBLE);
        }
        if (left_src_id != 0) {
        	mLeftIcon.setImageResource(left_src_id);
        }
        
        if (right_src_id != 0) {
        	mRightBtn.setImageResource(right_src_id);
        	mRightBtn.setVisibility(View.VISIBLE);
        }
        
       
    }
    
    protected void setTitleContent(int left_src_id,boolean isShowSearch, int right_src_id, int title_id){
        mLeftBtn = (LinearLayout)this.getView().findViewById(R.id.left_btn);
        mLeftIcon = (ImageView)this.getView().findViewById(R.id.left_icon);
        mRightBtn = (ImageView)this.getView().findViewById(R.id.right_btn);
        mRightTextBtn = (TextView)this.getView().findViewById(R.id.right_text_btn);
        //mRightBtn = (LinearLayout)findViewById(R.id.right_btn);
        mSearchBtn = (ImageView)this.getView().findViewById(R.id.search_btn);
        mAddBtn = (ImageView)this.getView().findViewById(R.id.add_btn);
        mMoreBtn = (ImageView)this.getView().findViewById(R.id.more_btn);
        
        titileTextView = (TextView)this.getView().findViewById(R.id.title);
        
        if (left_src_id != 0) {
        	mLeftIcon.setImageResource(left_src_id);
        }
        
        if(isShowSearch){
        	mSearchBtn.setVisibility(View.VISIBLE);
        }
        
        if (right_src_id != 0) {
        	mRightBtn.setImageResource(right_src_id);
        	mRightBtn.setVisibility(View.VISIBLE);
        }
        
        if (title_id != 0) {
            titileTextView.setText(title_id);
        }
    }
    
    protected void setTitleContent(int left_src_id, boolean showSearchIcon,
    		boolean showAddIcon,boolean showMoreIcon,int title_id){
        mLeftBtn = (LinearLayout)this.getView().findViewById(R.id.left_btn);
        mLeftIcon = (ImageView)this.getView().findViewById(R.id.left_icon);
        mRightBtn = (ImageView)this.getView().findViewById(R.id.right_btn);
        mRightTextBtn = (TextView)this.getView().findViewById(R.id.right_text_btn);
        //mRightBtn = (LinearLayout)findViewById(R.id.right_btn);
        mSearchBtn = (ImageView)this.getView().findViewById(R.id.search_btn);
        mAddBtn = (ImageView)this.getView().findViewById(R.id.add_btn);
        mMoreBtn = (ImageView)this.getView().findViewById(R.id.more_btn);
        titileTextView = (TextView)this.getView().findViewById(R.id.title);
        mCenterLayout = (LinearLayout)this.getView().findViewById(R.id.center_layout);
        
        if (left_src_id != 0) {
        	mLeftIcon.setImageResource(left_src_id);
        }
        if(showSearchIcon){
        	mSearchBtn.setVisibility(View.VISIBLE);
        }
       
        if(showAddIcon){
        	mAddBtn.setVisibility(View.VISIBLE);
        }
        if(showMoreIcon){
        	mMoreBtn.setVisibility(View.VISIBLE);
        }
        
        
        if (title_id != 0) {
            titileTextView.setText(title_id);
        }
    }
    
    
    protected void setRightTextTitleContent(int left_src_id, String right_src_id, int title_id){
        mLeftBtn = (LinearLayout)this.getView().findViewById(R.id.left_btn);
        mLeftIcon = (ImageView)this.getView().findViewById(R.id.left_icon);
        mRightBtn = (ImageView)this.getView().findViewById(R.id.right_btn);
        mRightTextBtn = (TextView)this.getView().findViewById(R.id.right_text_btn);
        //mRightBtn = (LinearLayout)findViewById(R.id.right_btn);
        mSearchBtn = (ImageView)this.getView().findViewById(R.id.search_btn);
        mAddBtn = (ImageView)this.getView().findViewById(R.id.add_btn);
        mMoreBtn = (ImageView)this.getView().findViewById(R.id.more_btn);
        
        titileTextView = (TextView)this.getView().findViewById(R.id.title);
        
        if (left_src_id != 0) {
        	mLeftIcon.setImageResource(left_src_id);
        }
        
        if (right_src_id != null && right_src_id.equals("")) {
        	mRightTextBtn.setText(right_src_id);
        	mRightTextBtn.setVisibility(View.VISIBLE);
        }
        
        if (title_id != 0) {
            titileTextView.setText(title_id);
        }
    }
    
    
    protected void setRightTextTitleContent(int left_src_id, int right_src_id, int title_id){
        mLeftBtn = (LinearLayout)this.getView().findViewById(R.id.left_btn);
        mLeftIcon = (ImageView)this.getView().findViewById(R.id.left_icon);
        mRightBtn = (ImageView)this.getView().findViewById(R.id.right_btn);
        mRightTextBtn = (TextView)this.getView().findViewById(R.id.right_text_btn);
        //mRightBtn = (LinearLayout)findViewById(R.id.right_btn);
        mSearchBtn = (ImageView)this.getView().findViewById(R.id.search_btn);
        mAddBtn = (ImageView)this.getView().findViewById(R.id.add_btn);
        mMoreBtn = (ImageView)this.getView().findViewById(R.id.more_btn);
        
        titileTextView = (TextView)this.getView().findViewById(R.id.title);
        
        if (left_src_id != 0) {
        	mLeftIcon.setImageResource(left_src_id);
        }
        
        if (right_src_id != 0) {
        	mRightTextBtn.setText(right_src_id);
        	mRightTextBtn.setVisibility(View.VISIBLE);
        }
        
        if (title_id != 0) {
            titileTextView.setText(title_id);
        }
    }
    
    protected void setTitleContent(int left_src_id, int right_src_id, String title_text){
        mLeftBtn = (LinearLayout)this.getView().findViewById(R.id.left_btn);
        mLeftIcon = (ImageView)this.getView().findViewById(R.id.left_icon);
        mRightBtn = (ImageView)this.getView().findViewById(R.id.right_btn);
        mRightTextBtn = (TextView)this.getView().findViewById(R.id.right_text_btn);
        mSearchBtn = (ImageView)this.getView().findViewById(R.id.search_btn);
        mAddBtn = (ImageView)this.getView().findViewById(R.id.add_btn);
        mMoreBtn = (ImageView)this.getView().findViewById(R.id.more_btn);
        titileTextView = (TextView)this.getView().findViewById(R.id.title);
       // mRightBtn = (LinearLayout)findViewById(R.id.right_btn);
        
        if (left_src_id != 0) {
        	mLeftIcon.setImageResource(left_src_id);
        }
        
        if (right_src_id != 0) {
        	mRightBtn.setImageResource(right_src_id);
        	mRightBtn.setVisibility(View.VISIBLE);
        }
        
        if (title_text != null && !title_text.equals("")) {
            titileTextView.setText(title_text);
        }
    }
    
    /**--- for title bar ---*/
	
	abstract void setupViews(View contentView);
}
