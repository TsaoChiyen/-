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
import com.chengxin.global.WeiYuanCommon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

 

/**
 * 购物
 * @author dl
 *
 */
public class KeyNumberActivity extends BaseActivity{

	private final String COTACT_PREFIX = "btnclick://";
	private WebView mWebView;

	private String mUrl;
	private int mType; 
	private String title;
	private Button mWebBack;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.game_view);
		mType = getIntent().getIntExtra("type",0);
		mUrl = getIntent().getStringExtra("url");
		title = getIntent().getStringExtra("title");
		initCompent();
	}

	private void initCompent(){

		mWebView = (WebView)findViewById(R.id.web_view);
		mWebView.getSettings().setDefaultTextEncodingName("UTF-8");

		mWebBack = (Button)findViewById(R.id.web_back);
		mWebBack.setOnClickListener(this);
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.getSettings().setJavaScriptEnabled(true);
		new WebViewTask().execute();
		//synCookies(mContext,mUrl);

	}





	private class MyWebViewClient extends WebViewClient{



		public boolean shouldOverrideUrlLoading(WebView view, String url) { 
			Log.e("MessageView", url); //tel:13330983586
			//url = "http://www.hui2013.cn/wap/index.php?tag=backapp";
			//http://www.hui2013.cn/wap/
			String overUrl = URLDecoder.decode(url);//url.URLDecode;
			Log.e("urlcode", overUrl);
			if((url != null && !url.equals(""))&& overUrl.contains("tag=backapp")){
				KeyNumberActivity.this.finish();
				return true;
				
			/*	int startIndex = overUrl.lastIndexOf("call_phone=");
				int endIndex = startIndex+"call_phone=".length()+11;
				if(endIndex >= overUrl.length()){
					return true;
				}*/
			
			/*	if(isNumeric2(phoneString)){
					Intent intent=new Intent("android.intent.action.CALL",Uri.parse("tel:"+phoneString));
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
			WeiYuanCommon.saveWebCookie(mContext, cookieStr);*/
			super.onPageFinished(view, url);

		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			if(mProgressDialog == null || !mProgressDialog.isShowing()){
				showProgressDialog("数据加载中,请稍后...");
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
	// 设置回退
	// 覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
			//mWebView.goBack(); // goBack()表示返回WebView的上一页面
			Log.e("onKeyDown", "onKeyDown");
			KeyNumberActivity.this.finish();
			return true;
		}
		return super.onKeyDown(keyCode,event);
	}


	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.web_back:
			KeyNumberActivity.this.finish();
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
			/*if (mCookie != null) {
				 delete old cookies 
				cookieManager.removeSessionCookie(); 
			}*/
			super.onPreExecute();
		}
		protected Boolean doInBackground(Void... param) {
			/* this is very important - THIS IS THE HACK */

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

			}
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