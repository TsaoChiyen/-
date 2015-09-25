package com.chengxin;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.chengxin.R;
import com.chengxin.Entity.CookieEntity;
import com.chengxin.global.GlobleType;
import com.chengxin.global.WeiYuanCommon;
 


import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

/**
 * 游戏
 * @author dl
 *
 */
public class GameWebViewActivity extends BaseActivity{

	private WebView mWebView;

	private String mUrl;
	private int mType ;
	private Button mWebBack;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = GameWebViewActivity.this;
		setContentView(R.layout.game_view);
		mUrl = getIntent().getStringExtra("url");
		mType = getIntent().getIntExtra("type", GlobleType.FOUND_GAME);
		initCompent();
	}

	private void initCompent(){
		if(mType == GlobleType.FOUND_HOTQUEE){//发现-热门广场
			mContext.getResources().getString(R.string.found_hot_q);
		}else if(mType == GlobleType.FOUND_GAME){//发现-游戏
			mContext.getResources().getString(R.string.found_game);
		}else if(mType == GlobleType.FOUND_FUN_EVENT){ //爱FUN活动
			
		}else if(mType == GlobleType.FOUND_FUN_MALL){ //爱FUN商城
			
		}else if(mType == GlobleType.FOUND_SEARCH){ //搜索
			
		}

		mWebBack = (Button)findViewById(R.id.web_back);
		mWebBack.setOnClickListener(this);

		/*setTitleContent(R.drawable.back_btn,0,"爱fun");*/
		mWebView = (WebView)findViewById(R.id.web_view);
		mWebView.getSettings().setDefaultTextEncodingName("UTF-8");



		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
		new WebViewTask().execute();

	}
	

	private class MyWebViewClient extends WebViewClient{



		public boolean shouldOverrideUrlLoading(WebView view, String url) {       
			Log.e("MessageView", url); //tel:13330983586
		//	url = "http://www.hui2013.cn/wap/index.php?tag=backapp";
			String overUrl = URLDecoder.decode(url);//url.URLDecode;
			Log.e("urlcode", overUrl);
			if((url != null && !url.equals(""))&& overUrl.contains("tag=backapp")){
				GameWebViewActivity.this.finish();
				return true;   
			/*	int startIndex = overUrl.lastIndexOf("call_phone=");
				int endIndex = startIndex+"call_phone=".length()+11;
				if(endIndex >= overUrl.length()){
					return true;
				}*/
			
			/*	if(isNumeric2(phoneString)){
					Intent intent=new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+phoneString));
					mParentContext.startActivity(intent);
				}*/
				
			}else{
				return super.shouldOverrideUrlLoading(view, url); 
			
				
			}     
		}   


		@Override
		public void onPageFinished(WebView view, String url) {
			hideProgressDialog();
			/*CookieManager cookieManager = CookieManager.getInstance();
			String cookieStr = cookieManager.getCookie(url);
		//	Log.e("onPageFinished", cookieStr);
			WeihuiCommon.saveWebCookie(mContext, cookieStr);*/
			super.onPageFinished(view, url);

		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			if(mProgressDialog == null || !mProgressDialog.isShowing()){
				//showProgressDialog("数据加载中,请稍后...");
			}

			super.onPageStarted(view, url, favicon);
			//SiBaDaCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG,"数据加载中,请稍后...");

		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
		}



	}





	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.web_back:
			GameWebViewActivity.this.finish();
			break;

		default:
			break;
		}
	}


	private class WebViewTask extends AsyncTask<Void, Void, Boolean> {
		CookieEntity mCookie;
		CookieManager cookieManager;

		@Override
		protected void onPreExecute() {
			if(mProgressDialog == null || !mProgressDialog.isShowing()){
				showProgressDialog("数据加载中,请稍后...");
			}
			//CookieSyncManager.createInstance(mContext);
			cookieManager = CookieManager.getInstance();

			mCookie = WeiYuanCommon.getWebCookie(mContext);
		/*	if (mCookie != null) {
				 delete old cookies 
				cookieManager.removeSessionCookie(); 
			}*/
			super.onPreExecute();
		}
		protected Boolean doInBackground(Void... param) {
			/* this is very important - THIS IS THE HACK */
		/*	SystemClock.sleep(1000);*/
			if(mCookie == null){
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(WeiYuanCommon.getLoginWapUrl(mContext));	
				HttpContext context = new BasicHttpContext();
				CookieStore cookieStore = new BasicCookieStore();
				context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
				try {
					HttpResponse response = client.execute(get, context);
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						List cookies = cookieStore.getCookies();
						if (!cookies.isEmpty()) {
							for (int i = cookies.size(); i > 0; i --) {
								Cookie cookie = (Cookie) cookies.get(i-1);
								if (cookie.getName().equalsIgnoreCase("PHPSESSID")) {
									// 使用一个常量来保存这个cookie，用于做session共享之用
									mCookie = new CookieEntity(cookie.getName(),cookie.getValue(), cookie.getDomain());
									WeiYuanCommon.saveWebCookie(mContext, new CookieEntity(cookie.getName(),cookie.getValue(), cookie.getDomain()));
									Log.e("createWebView", cookie+"");
									//mHandler.sendEmptyMessage(GlobalParam.MSG_LOAD_WEB_URL);
									//Utils.appCookie = cookie;
								}
							}
						}
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			};

			return false;
		}
		@Override
		protected void onPostExecute(Boolean result) {
			mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
			if (mCookie != null) {
				String cookieString = mCookie.cookieName+ "=" + mCookie.cookieValue + "; domain=" +mCookie.cookieDomain;
				if (mCookie != null) {
					 cookieManager.setAcceptCookie(true);
					/* delete old cookies */
					/*cookieManager.removeSessionCookie(); */
				}
				cookieManager.setCookie(mCookie.cookieDomain, cookieString);
				CookieSyncManager.getInstance().sync();
			}
			WebSettings webSettings = mWebView.getSettings();
			webSettings.setJavaScriptEnabled(true);
			webSettings.setBuiltInZoomControls(true);
			mWebView.setWebViewClient(new MyWebViewClient());
			mWebView.loadUrl(mUrl);
		}
	}



}