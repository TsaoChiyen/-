package com.chengxin.profile.shopping;import android.content.Intent;import android.os.Bundle;import android.view.View;import android.widget.Button;import android.widget.EditText;import com.chengxin.BaseActivity;import com.chengxin.CaptureActivity;import com.chengxin.R;public class ShopOrderDeliveryActivity extends BaseActivity {	private static final int SCAN_FOR_BARCODE = 21;	private EditText mLogCompany;	private Button mScanLogSn;	private EditText mLogSn;	@Override	protected void onCreate(Bundle savedInstanceState) {		super.onCreate(savedInstanceState);		mContext = this;		setContentView(R.layout.order_delivery_activity);				setTitleContent(R.drawable.back_btn, R.drawable.ok_btn, R.string.shop_order_delivery);        mLeftBtn.setOnClickListener(this);        mRightBtn.setOnClickListener(this);		mLogCompany = (EditText)findViewById(R.id.log_company);		mLogSn = (EditText)findViewById(R.id.log_sn);		mScanLogSn = (Button)findViewById(R.id.scan_log_sn);		mScanLogSn.setOnClickListener(this);	}	@Override	public void onClick(View v) {		super.onClick(v);				switch (v.getId()) {		case R.id.left_btn:			this.finish();			break;		case R.id.right_btn:			String company = mLogCompany.getText().toString();			String sn = mLogSn.getText().toString();						if (company == null || company.equals("") ||					sn == null || sn.equals("")) {				return;			}						Intent intent = new Intent();			intent.putExtra("log_company", company);			intent.putExtra("log_sn", sn);			setResult(RESULT_OK, intent);			this.finish();			break;					case R.id.scan_log_sn:			Intent scanIntent = new Intent(mContext, CaptureActivity.class);			scanIntent.putExtra("scanmode", 1);			startActivityForResult(scanIntent, SCAN_FOR_BARCODE);		default:			break;		}	}	@Override	protected void onActivityResult(int arg0, int arg1, Intent arg2) {		super.onActivityResult(arg0, arg1, arg2);		switch (arg0) {		case SCAN_FOR_BARCODE:			if(arg1 == RESULT_OK){				String barcode = arg2.getStringExtra("data");				mScanLogSn.setText(barcode);			}						break;		}	}}