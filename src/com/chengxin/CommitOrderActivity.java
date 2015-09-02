package com.chengxin;

import java.util.List;

import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chengxin.R;
import com.chengxin.Entity.Goods;
import com.chengxin.Entity.Merchant;
import com.chengxin.Entity.WeiYuanState;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.net.WeiYuanException;

/**
 *提交订单
 * @author dl
 *
 */
public class CommitOrderActivity extends BaseActivity {
	private Button mCommitBtn;
	private EditText mContactNameEdit,mTelPhoneEdit,mAddrEdit,
			mDescEdit;
	
	private String mInputContactName,mInputTelPhone,mInputAddr,
			mInputDesc;
	
	private Merchant mMerchant;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.commit_order_view);
		mMerchant = (Merchant)getIntent().getSerializableExtra("entity");
		initCompent();
	}
	
	private void initCompent(){
		setTitleContent(R.drawable.back_btn,0,R.string.commit_order);
		mLeftBtn.setOnClickListener(this);
		mCommitBtn = (Button)findViewById(R.id.commit_btn);
		mCommitBtn.setOnClickListener(this);
		
		mContactNameEdit = (EditText)findViewById(R.id.contact_name);
		mTelPhoneEdit = (EditText)findViewById(R.id.contact_tel);
		mAddrEdit = (EditText)findViewById(R.id.contact_addr);
		mDescEdit = (EditText)findViewById(R.id.desc);
	}

	/*判断输入的值*/
	private boolean checkvalue(){
		boolean isCheck = true;
		String hintMsg = "";
		mInputContactName = mContactNameEdit.getText().toString();
		mInputTelPhone = mTelPhoneEdit.getText().toString();
		mInputAddr = mAddrEdit.getText().toString();
		mInputDesc = mDescEdit.getText().toString();
		if(mInputContactName == null || mInputContactName.equals("")){
			isCheck = false;
			hintMsg = "请输入联系人名称";
		}else if(mInputTelPhone == null || mInputTelPhone.equals("")){
			isCheck = false;
			hintMsg = "请输入联系电话";
		}else if(!WeiYuanCommon.isMobileNum(mInputTelPhone)){
			isCheck = false;
			hintMsg = "请输入正确的电话号码";
		}else if(mInputAddr == null || mInputAddr.equals("")){
			isCheck = false;
			hintMsg = "请输入联系人地址";
		}else if(mInputDesc != null && !mInputDesc.equals("")){
			if(mInputDesc.length() >150){
				isCheck = false;
				hintMsg = "备注信息不能超过150个字";
			}
		}
		if(!isCheck && (hintMsg != null && !hintMsg.equals(""))){
			Toast.makeText(mContext, hintMsg, Toast.LENGTH_LONG).show();
		}
		return isCheck;
	}
	
	/*提交订单*/
	private void commitOrder(){
		if(!checkvalue()){
			return;
		}
		
		final List<Goods> goodsList = mMerchant.goodsList;
		if(goodsList == null || goodsList.size() <=0){
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
					String goods = "";
					
					//商品id1*count1,id2*count2
					for (int i = 0; i < goodsList.size(); i++) {
						if(i != goodsList.size() - 1){
							goods = goods + goodsList.get(i).price+"*"+goodsList.get(i).count+",";
						}else{
							goods = goods + goodsList.get(i).price+"*"+goodsList.get(i).count;
						}
					}
					
					WeiYuanState state = WeiYuanCommon.getWeiYuanInfo().submitOrder(goods, mInputContactName, mInputTelPhone, mInputAddr, 
							mInputDesc, mMerchant.id);
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
					WeiYuanCommon.sendMsg(mHandler,GlobalParam.MSG_CHECK_STATE, state);
				} catch (NotFoundException e) {
					e.printStackTrace();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				} catch (WeiYuanException e) {
					e.printStackTrace();
					WeiYuanCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG, 
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
			CommitOrderActivity.this.finish();
			break;
		case R.id.commit_btn://提交订单
			commitOrder();
			break;

		default:
			break;
		}
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GlobalParam.MSG_CHECK_STATE:
				WeiYuanState state = (WeiYuanState)msg.obj;
				if(state == null){
					Toast.makeText(mContext, R.string.commit_data_error,Toast.LENGTH_LONG).show();
					return;
				}
				Toast.makeText(mContext, state.errorMsg,Toast.LENGTH_LONG).show();
				
				if(state.code == 0){
					Intent resultIntent = new Intent();
					resultIntent.setClass(mContext, CommitOrderStateActivity.class);
					startActivity(resultIntent);
					CommitOrderActivity.this.finish();
				}
			
				break;

			default:
				break;
			}
		}
		
	};
	
	
	

}
