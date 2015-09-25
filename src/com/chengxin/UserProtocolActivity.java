package com.chengxin;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;

import com.chengxin.R;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.net.WeiYuanException;

/**
 * 用户协议
 * @author dongli
 *
 */
public class UserProtocolActivity extends BaseActivity implements OnClickListener{

	WebView mWebView;
	private String mUserProtocol;
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg); 
			switch (msg.what) {
			case GlobalParam.MSG_SHOW_LOAD_DATA:
				mWebView.loadData(mUserProtocol,"text/html; charset=UTF-8", null);
				break;

			default:
				break;
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_protocol);
		mContext = this;
		getProtocol();
		initCompnet();
	}
	
	private void initCompnet(){
	    setTitleContent(R.drawable.back_btn, 0, R.string.user_protocol);
	    mLeftBtn.setOnClickListener(this);
	    
		
		
		mWebView = (WebView)findViewById(R.id.webview);
		mWebView.getSettings().setDefaultTextEncodingName("UTF-8");
		mWebView.setPadding(5,5, 5, 5);
	}
	
	/*
	 * 获取用协议
	 */
	private void getProtocol(){
		if(!WeiYuanCommon.getNetWorkState()){
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread(){
			public void run() {
				try {
					WeiYuanCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG, "数据加载中,请稍后...");
					mUserProtocol = WeiYuanCommon.getWeiYuanInfo().getProtocol();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
					mHandler.sendEmptyMessage(GlobalParam.MSG_SHOW_LOAD_DATA);
				} catch (WeiYuanException e) {
					e.printStackTrace();
					WeiYuanCommon.sendMsg(mBaseHandler, BASE_MSG_NETWORK_ERROR,
							mContext.getResources().getString(e.getStatusCode()));
				}catch (Exception e) {
					e.printStackTrace();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				}
			};
		}.start();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			UserProtocolActivity.this.finish();
			break;

		default:
			break;
		}
	}

}
