package com.chengxin;

import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chengxin.R;
import com.chengxin.Entity.MapInfo;
import com.chengxin.Entity.WeiYuanState;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.GlobleType;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.net.WeiYuanException;

/**
 * 申请成为商户
 * @author dl
 *
 */
public class ApplyMerchantActivity extends BaseActivity {
	private EditText mMerchantNameEdit,mContactNameEdit,
	mContactTelEdit,mContactAddrEdit,mDescEdit;
	private Button mCommitBtn;
	private TextView mMerchantLocationBtn;


	private String mInputMerchatnName,mInputContactName,
	mInputContactTel,mInputContactAddr,mInputDesc;

	private MapInfo mMapInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.apply_merchant_view);
		initCompent();
	}

	private void initCompent(){
		setTitleContent(R.drawable.back_btn,0,R.string.apply_merchant);
		mLeftBtn.setOnClickListener(this);

		mMerchantNameEdit = (EditText)findViewById(R.id.merchant_name);
		mMerchantLocationBtn = (TextView)findViewById(R.id.merchant_location);
		mContactNameEdit = (EditText)findViewById(R.id.contact_name);
		mContactTelEdit = (EditText)findViewById(R.id.contact_tel);
		mContactAddrEdit = (EditText)findViewById(R.id.contact_addr);
		mDescEdit = (EditText)findViewById(R.id.desc);

		mMerchantLocationBtn.setOnClickListener(this);
		mCommitBtn = (Button)findViewById(R.id.commit_btn);
		mCommitBtn.setOnClickListener(this);
	}

	private boolean checkValue(){
		boolean isCheck = true;
		String hintMsg = "";
		mInputMerchatnName = mMerchantNameEdit.getText().toString();
		mInputContactName = mContactNameEdit.getText().toString();
		mInputContactTel = mContactTelEdit.getText().toString(); 
		mInputContactAddr = mContactAddrEdit.getText().toString();
		mInputDesc = mDescEdit.getText().toString();

		if(mInputMerchatnName == null || mInputMerchatnName.equals("")){
			isCheck = false;
			hintMsg = "请输入商铺名称";
		}else if(mInputContactName == null || mInputContactName.equals("")){
			isCheck = false;
			hintMsg = "请输入联系人名称";
		}else if(mInputContactTel == null || mInputContactTel.equals("")){
			isCheck = false;
			hintMsg = "请输入联系号码";
		}else if(mInputContactAddr == null || mInputContactAddr.equals("")){
			isCheck = false;
			hintMsg = "请输入联系地址";
		}else if(mMapInfo == null
				|| (mMapInfo.getLat() == null || mMapInfo.getLat().equals(""))
				||(mMapInfo.getLng() == null || mMapInfo.getLng().equals(""))){
			isCheck = false;
			hintMsg = "请选择商铺位置";
		}	else{
			if(!WeiYuanCommon.isMobileNum(mInputContactTel)){
				isCheck = false;
				hintMsg = "请输入正确的手机格式";
			}else if(mInputMerchatnName.length()>30){
				isCheck = false;
				hintMsg = "商铺名称为1-30个字";
			}else if(mInputContactName.length()>10){
				isCheck = false;
				hintMsg = "联系人为1-10个字";
			}else if(mInputDesc !=null && !mInputDesc.equals("")){
				if(mInputDesc.length()>30){
					isCheck = false;
					hintMsg = "备注信息不能超过30个字";
				}
			}
		}
		if(!isCheck && (hintMsg != null && !hintMsg.equals(""))){
			Toast.makeText(mContext, hintMsg, Toast.LENGTH_LONG).show();
		}
		return isCheck;
	}

	private void commit(){
		if(!checkValue()){
			return;
		}
		if(!WeiYuanCommon.getNetWorkState()){
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread(){
			public void run() {
				try {
					WeiYuanCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG, 
							mContext.getResources().getString(R.string.commit_dataing));
					WeiYuanState state = WeiYuanCommon.getWeiYuanInfo().applyMerchant(mInputMerchatnName, mInputContactName,
							mInputContactTel, mInputContactAddr, mMapInfo.getLat(), mMapInfo.getLng(), mInputDesc);
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
					WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_CHECK_STATE,state);
				} catch (NotFoundException e) {
					e.printStackTrace();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
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



	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			ApplyMerchantActivity.this.finish();
			break;
		case R.id.merchant_location:
			Intent intent = new Intent(this, LocationActivity.class);
			startActivityForResult(intent, GlobleType.RESQUEST_MAP_LOACTION);
			break;
		case R.id.commit_btn:
			commit();
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent data) {
		super.onActivityResult(arg0, arg1, data);
		if(arg0 == GlobleType.RESQUEST_MAP_LOACTION ){
			if(data != null && RESULT_OK == arg1){
				Bundle bundle = data.getExtras();
				if(bundle != null){

					mMapInfo = (MapInfo)data.getSerializableExtra("mapInfo");
					if(mMapInfo == null){
						Toast.makeText(mContext, mContext.getString(R.string.get_location_failed), Toast.LENGTH_SHORT).show();
						return;
					}
					mMerchantLocationBtn.setText(mMapInfo.getAddr());

				}
			}

		}
	}


	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GlobalParam.MSG_CHECK_STATE:
				WeiYuanState sate = (WeiYuanState)msg.obj;
				if(sate == null){
					Toast.makeText(mContext, R.string.commit_data_error,Toast.LENGTH_LONG).show();
					return;
				}
				Toast.makeText(mContext, sate.errorMsg,Toast.LENGTH_LONG).show();
				if(sate.code == 0){
					ApplyMerchantActivity.this.finish();
				}
				break;

			default:
				break;
			}
		}

	};





}
