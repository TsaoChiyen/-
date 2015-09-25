package com.chengxin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Chony on 2015/6/30.
 */
public class BrowserActivity extends BaseActivity{

    WebView mWebView;
    String mUrl = "";
    String mTitle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        mUrl = getIntent().getStringExtra("url");
        mTitle = getIntent().getStringExtra("title");

        setContentView(R.layout.activity_browser_layout);

        initComponment();
    }

    private void initComponment(){
        setTitleContent(R.drawable.back_btn, 0, mTitle);
        mLeftBtn.setOnClickListener(this);

        mWebView = (WebView)findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setDefaultTextEncodingName("UTF-8");

        mWebView.setWebViewClient(new WebViewClient());

        if (!TextUtils.isEmpty(mUrl)){
            if (!mUrl.startsWith("http://") && !mUrl.startsWith("https://")){
                mUrl = "http://" + mUrl;
            }
            mWebView.loadUrl(mUrl);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.left_btn:
                finish();
                break;
        }
    }
}
