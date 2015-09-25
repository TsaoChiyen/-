package com.chengxin;

import com.chengxin.R;
import com.chengxin.Entity.Login;
import com.chengxin.Entity.WeiYuanState;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.GlobleType;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.net.WeiYuanException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

public class PrivateSetActivity extends BaseActivity implements OnCheckedChangeListener {

	private int mType;

	private RelativeLayout mAcceptMsgLayout,mVoiceLayout,mShakeLayout;
	private RelativeLayout mRecommendContactLayout;
	private LinearLayout mFirstLayout;
	private ImageView mShakeImag;

	/**
	 * 新消息通知
	 */
	private ToggleButton mAcceptNewNotifyBtn,mVoiceBtn,mShakeBtn;
	/**
	 * 隐私
	 */
	private ToggleButton mValidFrindApplyBtn,/*mReplyAndFriendBtn,*/mTuiJianContactBtn;
	private Login mLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.private_set);
		mType = getIntent().getIntExtra("type",GlobleType.PrivateSetActivity_Normal_TYPE);
		mLogin = WeiYuanCommon.getLoginResult(mContext);
		initCompent();
	}

	private void initCompent(){
		setTitleContent(R.drawable.back_btn, 0,0);
		mFirstLayout = (LinearLayout)findViewById(R.id.first_layout);
		if(mType == GlobleType.PrivateSetActivity_Normal_TYPE){
			titileTextView.setText( R.string.private_setting);
			mFirstLayout.setVisibility(View.VISIBLE);
			mRecommendContactLayout = (RelativeLayout)findViewById(R.id.recommend_contact_layout);
			mRecommendContactLayout.setVisibility(View.VISIBLE);

			mValidFrindApplyBtn =  (ToggleButton)findViewById(R.id.tglloop);
			mTuiJianContactBtn =  (ToggleButton)findViewById(R.id.tgl_recommend_friend);

			mValidFrindApplyBtn.setChecked(mLogin.isValidFriendAppley);
			mTuiJianContactBtn.setChecked(mLogin.isTuiJianContact);

			mValidFrindApplyBtn.setOnCheckedChangeListener(this);
			mTuiJianContactBtn.setOnCheckedChangeListener(this);

		}else if(mType == GlobleType.PrivateSetActivity_New_Msg_Notify_TYPE){
			mFirstLayout.setVisibility(View.GONE);
			mShakeImag = (ImageView)findViewById(R.id.shake_img);
			mShakeImag.setVisibility(View.VISIBLE);
			titileTextView.setText(R.string.new_msg_notify);
			mAcceptMsgLayout = (RelativeLayout)findViewById(R.id.accept_new_msg_layout);
			mVoiceLayout = (RelativeLayout)findViewById(R.id.voice_layout);
			mShakeLayout = (RelativeLayout)findViewById(R.id.shake_layout);
			mAcceptMsgLayout.setVisibility(View.VISIBLE);
		

			mAcceptNewNotifyBtn = (ToggleButton)findViewById(R.id.tgl_accept_new_msg);
			mVoiceBtn = (ToggleButton)findViewById(R.id.tgl_voice);
			mShakeBtn = (ToggleButton)findViewById(R.id.tgl_shake);
			if(mLogin.isAcceptNew){
				mVoiceLayout.setVisibility(View.VISIBLE);
				mShakeLayout.setVisibility(View.VISIBLE);
			}else{
				mVoiceLayout.setVisibility(View.GONE);
				mShakeLayout.setVisibility(View.GONE);
			}
			mVoiceBtn.setChecked(mLogin.isOpenVoice);
			mAcceptNewNotifyBtn.setChecked(mLogin.isAcceptNew);
			mShakeBtn.setChecked(mLogin.isOpenShake);

			mShakeBtn.setOnCheckedChangeListener(this);
			mVoiceBtn.setOnCheckedChangeListener(this);
			mAcceptNewNotifyBtn.setOnCheckedChangeListener(this);
		}

		mLeftBtn.setOnClickListener(this);


	}

	@Override
	public void onClick(View v) {
		//super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			PrivateSetActivity.this.finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.tgl_accept_new_msg:
			if(isChecked && mLogin.isAcceptNew){
				return;
			}

			mLogin.isAcceptNew = isChecked;
			if(!isChecked){
				mVoiceLayout.setVisibility(View.GONE);
				mShakeLayout.setVisibility(View.GONE);
			}else{
				mVoiceLayout.setVisibility(View.VISIBLE);
				mShakeLayout.setVisibility(View.VISIBLE);
			}
			WeiYuanCommon.saveLoginResult(mContext, mLogin);
			break;
		case R.id.tgl_voice:
			if(isChecked && mLogin.isOpenVoice){
				return;
			}
			mLogin.isOpenVoice = isChecked;
			WeiYuanCommon.saveLoginResult(mContext, mLogin);
			break;
		case R.id.tgl_shake:
			if(isChecked && mLogin.isOpenShake){
				return;
			}
			mLogin.isOpenShake = isChecked;
			WeiYuanCommon.saveLoginResult(mContext, mLogin);
			break;
		case R.id.tglloop://加我为朋友时需要验证
			if(isChecked && mLogin.isValidFriendAppley){
				return;
			}
			setVerify(isChecked);
			
			break;
		case R.id.tgl_repy_add_friend://回复即添加对方为好友
			if(isChecked && mLogin.isReplyAndFriend){
				return;
			}
			mLogin.isReplyAndFriend = isChecked;
			WeiYuanCommon.saveLoginResult(mContext, mLogin);
			break;
		case R.id.tgl_recommend_friend://向我推荐通讯录朋友
			if(isChecked && mLogin.isTuiJianContact){
				return;
			}
			mLogin.isTuiJianContact = isChecked;
			WeiYuanCommon.saveLoginResult(mContext, mLogin);
			break;
		default:
			break;
		}
	}

	private void setVerify(final boolean verify){
		if (!WeiYuanCommon.getNetWorkState()) {
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					int recv = 0;
					if (verify) {
						recv = 1;
					}
					else {
						recv = 0;
					}
					WeiYuanCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG,
							mContext.getResources().getString(R.string.send_request));

					WeiYuanState stauts = WeiYuanCommon.getWeiYuanInfo().setVerify(recv);

					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
					WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_CHECK_STATE,stauts);
				
				} catch (WeiYuanException e) {
					e.printStackTrace();
					WeiYuanCommon.sendMsg(mBaseHandler, BASE_MSG_TIMEOUT_ERROR,
							mContext.getResources().getString(e.getStatusCode()));
				}catch (Exception e) {
					e.printStackTrace();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				}
			}
		}).start();
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GlobalParam.MSG_CHECK_STATE:
				WeiYuanState weiYuanState = (WeiYuanState)msg.obj;
				if(weiYuanState == null || weiYuanState.code!=0){
					mValidFrindApplyBtn.setChecked(mLogin.isReplyAndFriend);
					Toast.makeText(mContext, "设置失败!",Toast.LENGTH_LONG).show();
					return;
				}
				
				if(weiYuanState.code == 0){
					if(mLogin.isValidFriendAppley ){
						mLogin.isValidFriendAppley = false;
					}else{
						mLogin.isValidFriendAppley = true;
					}
					WeiYuanCommon.saveLoginResult(mContext, mLogin);
					Toast.makeText(mContext, weiYuanState.errorMsg,Toast.LENGTH_LONG).show();
				}
				
				break;

			default:
				break;
			}
		}
		
	};



}
