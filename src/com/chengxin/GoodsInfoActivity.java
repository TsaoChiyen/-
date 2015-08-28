package com.chengxin;

import android.os.Bundle;
import android.webkit.WebView;
import com.chengxin.R;

/**
 * 商品详细
 * @author dl
 *
 */
public class GoodsInfoActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_goods_info_view);
		mContext = this;
		WebView webView = (WebView)findViewById(R.id.web_view);
		webView.getSettings().setDefaultTextEncodingName("UTF-8");
		/*webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setBuiltInZoomControls(true);*/
		webView.loadUrl("http://www.uestcedu.com/");
	}

}
