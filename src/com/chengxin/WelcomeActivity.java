package com.chengxin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.view.KeyEvent;

import com.chengxin.R;
import com.chengxin.global.WeiYuanCommon;

/**
 * 欢迎页面
 * 
 * @author dongli
 * 
 */
public class WelcomeActivity extends Activity {

	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		mContext = this;
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		WeiYuanCommon.mScreenWidth = metrics.widthPixels;
		WeiYuanCommon.mScreenHeight = metrics.heightPixels;
		new Thread() {
			public void run() {
				Cursor cursor = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[] { ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME }, null,
						null, "sort_key COLLATE LOCALIZED asc");
			};
		}.start();
		showMainpage();
	}

	public void showMainpage() {

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {

				Intent intentt = new Intent(WelcomeActivity.this, MainActivity.class);
				WelcomeActivity.this.startActivity(intentt);
				WelcomeActivity.this.finish();
			}
		}, 2000);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			this.finish();
			System.exit(0);
		}
		return super.dispatchKeyEvent(event);
	}

}
