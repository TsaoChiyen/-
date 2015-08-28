package com.chengxin;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import com.chengxin.R;

/**
 * 点击查看图文详情
 * @author dl
 *
 */
public class PicInfoActivity extends BaseActivity{

	private RadioGroup mRadioGroup;
	private WebView mWebView;

	private String mTextPicUrl,mCanShuUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.pic_text_view);
		mTextPicUrl = getIntent().getStringExtra("content");
		mCanShuUrl = getIntent().getStringExtra("parameter");
	/*	mTextPicUrl= "http://www.tmall.com";
		mCanShuUrl = "http://www.taobao.com";*/
		initCompent();
	}

	private void initCompent(){
		setTitleContent(R.drawable.back_btn,0,R.string.look_detail);
		mLeftBtn.setOnClickListener(this);

		mRadioGroup = (RadioGroup)findViewById(R.id.menu_tab_group);
		mRadioGroup.check(R.id.share);
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.like:
					//	mFlowGroup.check(R.id.like);
					setUrlForWebView(mTextPicUrl,false);
					break;
				case R.id.share:
					//mFlowGroup.check(R.id.share);
					setUrlForWebView(mCanShuUrl,false);
					break;

				default:
					break;
				}
			}
		});
	
		mWebView = (WebView)findViewById(R.id.web_view);
		mWebView.getSettings().setDefaultTextEncodingName("UTF-8");
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
		setUrlForWebView(mCanShuUrl,false);
	}

	private void setUrlForWebView(String url,boolean isTopRightBtn){
	
		mWebView.loadUrl(url);

	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			PicInfoActivity.this.finish();
			break;

		default:
			break;
		}
	}





}
