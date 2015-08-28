package com.chengxin;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.TextView;

import com.chengxin.R;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.net.WeiYuanException;

/*
 * 帮助页面
 */
public class HelpWebViewActivity extends BaseActivity implements OnClickListener{

	private WebView mWebView;
	private int mType = 0;
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GlobalParam.MSG_SHOW_LOAD_DATA:
				String helpHtml = (String) msg.obj; 
				if(helpHtml!=null && !helpHtml.equals("")){
					mWebView.loadData(helpHtml,"text/html; charset=UTF-8", null);
					//mWebView.loadData(helpHtml, "'text/html'", "GBK");
				}
				break;

			default:
				break;
			}
		}
		
	};
	
	/*
	 * 导入控件
	 * (non-Javadoc)
	 * @see com.weiyuan.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_webview);
		mContext = this;
		initComponent();
	}

	/*
	 * 实例化控件
	 */
	private void initComponent(){

		mType = getIntent().getIntExtra("type", 0);
		setTitleContent(R.drawable.back_btn, 0, 0);
		mLeftBtn.setOnClickListener(this);
		if(mType == 0){
			
			titileTextView.setText(R.string.operation_help);
		}else {
			titileTextView.setText(R.string.help_center);
		}

		mWebView = (WebView) findViewById(R.id.webview);
		
		loadHelpHtml();
	}

	/*
	 * 加载帮助页面内容
	 */
	private void loadHelpHtml(){
		if(!WeiYuanCommon.getNetWorkState()){
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread(){
			public void run() {
				try {
					WeiYuanCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG,"数据加载中,请稍后...");
					String helpHtml = WeiYuanCommon.getWeiYuanInfo().getHelpHtml();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
					WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_SHOW_LOAD_DATA, helpHtml);
				} catch (WeiYuanException e) {
					e.printStackTrace();
					WeiYuanCommon.sendMsg(mBaseHandler, BASE_MSG_TIMEOUT_ERROR,
							mContext.getResources().getString(e.getStatusCode()));
				}catch (Exception e) {
					e.printStackTrace();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				}
			};
		}.start();
	}
	
	
	/*
	 * 按钮点击事件
	 * (non-Javadoc)
	 * @see com.weiyuan.BaseActivity#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			this.finish();
			break;

		default:
			break;
		}
	}

}
