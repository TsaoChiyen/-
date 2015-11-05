package com.chengxin;

import com.chengxin.R;
import com.chengxin.Entity.Login;
import com.chengxin.Entity.WeiYuanState;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.GlobleType;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.net.WeiYuanException;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 修改密码
 * @author dongli
 *
 */
public class ModifyPwdActivity extends BaseActivity{
	
	public static final int LOGIN_PWD = 0x101;
	public static final int SHOPPING_PWD = 0x102;
	public static final int BASKET_PWD = 0x103;
	public static final int PWD_MODIFY = 0;
	public static final int PWD_SETTING= 1;
	public static final int PWD_VERIFY = 3;
	protected static final int CHECK_OLD_PASSWORD = 0x4001;
	
	private Button mOkBtn;
	private EditText mOldPwdEdit,mNewPwdEdit,mConfirmPwdEdit;
	private String mInputOldPwd,mInputNewPwd,mInputConfirmPwd;
	private int mtype = PWD_MODIFY; // 0:修改 else：设置
	private int mPwdType = 0;
	private int mpos;
	
	/*
	 * 处理消息
	 */
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case CHECK_OLD_PASSWORD:
				WeiYuanState state = (WeiYuanState)msg.obj;
				
				if(state == null ){
					Toast.makeText(mContext,R.string.commit_data_error, Toast.LENGTH_LONG).show();
				}

				if (state.code == 0) {
					new Thread(){
						public void run() {
							try {
								WeiYuanCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG, 
										mContext.getResources().getString(R.string.send_request));
								WeiYuanState state = null;
								
								if (mPwdType == BASKET_PWD) {
									state = WeiYuanCommon.getWeiYuanInfo().setShopPassword(
											1,
											mInputNewPwd);
								} else {
									state = WeiYuanCommon.getWeiYuanInfo().setShopPassword(
											0,
											mInputNewPwd);
								}
								
								mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
								WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_CHECK_STATE,state);
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
				}else{
					String hintMsg =state.errorMsg;
					
					if (hintMsg == null || hintMsg.equals("") ) {
						hintMsg = mContext.getResources().getString(R.string.commit_data_error);
					}
					
					Toast.makeText(mContext, hintMsg, Toast.LENGTH_LONG).show();
				}
				break;
			case GlobalParam.MSG_CHECK_STATE:
				state = (WeiYuanState)msg.obj;
				if(state == null ){
					Toast.makeText(mContext,R.string.commit_data_error, Toast.LENGTH_LONG).show();
				}

				if (state.code == 0) {
					if (mtype != PWD_VERIFY) {
						Login login = WeiYuanCommon.getLoginResult(mContext);

						if (login != null && mPwdType > 0) {
							if (mPwdType == LOGIN_PWD) {
								login.password = mInputNewPwd;
							} else if (mPwdType == SHOPPING_PWD) {
								login.hasShopPass = 1;
							} else if (mPwdType == BASKET_PWD) {
								login.hasBasketPass = 1;
							}

							WeiYuanCommon.saveLoginResult(mContext, login);
						}
					}
					
//					String hintMsg =state.errorMsg;
//					
//					if(hintMsg != null && !hintMsg.equals("") ){
//						Toast.makeText(mContext, hintMsg, Toast.LENGTH_LONG).show();
//					}
//					
					Intent intent = new Intent();
					intent.putExtra("pos", mpos);
					setResult(RESULT_OK, intent);
					ModifyPwdActivity.this.finish();
				}else{
					String hintMsg = state.errorMsg;
					
					if (hintMsg == null || hintMsg.equals("") ) {
						hintMsg = mContext.getResources().getString(R.string.commit_data_error);
					}
					
					Toast.makeText(mContext, hintMsg, Toast.LENGTH_LONG).show();
				}
				break;

			default:
				break;
			}
		}
		
	};
	
	/*
	 * 导入控件
	 * (non-Javadoc)
	 * @see com.weiyuan.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modify_pwd_view);

		mContext = this;
		initComponent();
	}
	
	/*
	 * 实例化控件
	 */
	private void initComponent(){
		mtype = getIntent().getIntExtra("type", PWD_MODIFY);
		mPwdType = getIntent().getIntExtra("passtype", LOGIN_PWD);
		mpos = getIntent().getIntExtra("pos", 0);
		
		if (mtype == PWD_MODIFY) {
			setTitleContent(R.drawable.back_btn,0,R.string.modify_pwd);
		} else if (mtype == PWD_VERIFY) {
			setTitleContent(R.drawable.back_btn,0,R.string.verify_pwd);
		} else {
			setTitleContent(R.drawable.back_btn,0,R.string.setting_pwd);
		}

		mLeftBtn.setOnClickListener(this);
		
		mOkBtn = (Button)findViewById(R.id.ok);
		mOkBtn.setOnClickListener(this);
		
		mOldPwdEdit = (EditText)findViewById(R.id.old_pwd);
		mNewPwdEdit = (EditText)findViewById(R.id.new_pwd);
		mConfirmPwdEdit = (EditText)findViewById(R.id.confirm_pwd);

		if (mtype == PWD_SETTING) {
			mOldPwdEdit.setVisibility(View.GONE);
			mNewPwdEdit.setHint("输入密码");
			mConfirmPwdEdit.setHint("确认密码");
		} else if (mtype == PWD_VERIFY) {
			mOldPwdEdit.setHint("输入密码");
			mNewPwdEdit.setVisibility(View.GONE);
			mConfirmPwdEdit.setVisibility(View.GONE);
		}
	}
	
	/*
	 * 修改密码
	 */
	private void modifyPwd(){
		if (!checkValue()) return;
		
		if (!WeiYuanCommon.getNetWorkState()) {
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread(){
			public void run() {
				try {
					WeiYuanCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG, 
							mContext.getResources().getString(R.string.send_request));
					
					WeiYuanState state = null;
					
					if (mPwdType == LOGIN_PWD) {
						state = WeiYuanCommon.getWeiYuanInfo().editPasswd(mInputOldPwd, mInputNewPwd);
					} else if (mPwdType == SHOPPING_PWD) {
						if (mtype == PWD_SETTING) {
							state = WeiYuanCommon.getWeiYuanInfo().setShopPassword(
									0,
									mInputNewPwd);
						} else {
							state = WeiYuanCommon.getWeiYuanInfo().verifyShopPassword(0, mInputOldPwd);
						}
					} else if (mPwdType == BASKET_PWD) {
						if (mtype == PWD_SETTING) {
							state = WeiYuanCommon.getWeiYuanInfo().setShopPassword(
									1,
									mInputNewPwd);
						} else {
							state = WeiYuanCommon.getWeiYuanInfo().verifyShopPassword(1, mInputOldPwd);
						}
					}
					
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
					
					if (mPwdType == SHOPPING_PWD) {
						if (mtype == PWD_MODIFY) {
							WeiYuanCommon.sendMsg(mHandler, CHECK_OLD_PASSWORD, state);
						} else if (mtype == PWD_VERIFY) {
							WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_CHECK_STATE, state);
						} else {
							WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_CHECK_STATE,state);
						}
					} else if (mPwdType == BASKET_PWD) {
						if (mtype == PWD_MODIFY) {
							WeiYuanCommon.sendMsg(mHandler, CHECK_OLD_PASSWORD, state);
						} else if (mtype == PWD_VERIFY) {
							WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_CHECK_STATE, state);
						} else {
							WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_CHECK_STATE,state);
						}
					} else {
						WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_CHECK_STATE,state);
					}
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
	
	private boolean checkValue() {
		mInputNewPwd = mNewPwdEdit.getText().toString();
		mInputOldPwd = mOldPwdEdit.getText().toString();
		mInputConfirmPwd = mConfirmPwdEdit.getText().toString();

		boolean isCheck = true;
		String hint = null;
		
		if (mtype == PWD_MODIFY) {
			if ((mInputNewPwd == null || mInputNewPwd.equals(""))
					|| (mInputOldPwd == null || mInputOldPwd.equals(""))
					|| (mInputConfirmPwd == null || mInputConfirmPwd.equals(""))) {
				hint = mContext.getResources().getString(R.string.please_input_old_new_confirm);
				isCheck = false;
			} else if (mInputNewPwd.equals(mInputOldPwd)) {
				hint = mContext.getResources().getString(R.string.new_old_pwd_not_equalse);
				isCheck = false;
			}
		} else if (mtype == PWD_VERIFY) {
			if ((mInputOldPwd == null || mInputOldPwd.equals(""))) {
				hint = "请输入密码";
				isCheck = false;
			}
		} else {
			if ((mInputNewPwd == null || mInputNewPwd.equals(""))
					|| (mInputConfirmPwd == null || mInputConfirmPwd.equals(""))) {
				hint = mContext.getResources().getString(R.string.please_input_old_new_confirm);
				isCheck = false;
			}
		}

		if (mtype != PWD_VERIFY) {
			if(!mInputNewPwd.equals(mInputConfirmPwd)){
				hint = mContext.getResources().getString(R.string.check_pwd_hint);
				isCheck = false;
			}
		}
		
		if (!isCheck && hint != null && !hint.equals("")) {
			Toast.makeText(mContext, hint,Toast.LENGTH_LONG).show();
		}

		return isCheck;
	}

	/*
	 * 按钮点击事件
	 * (non-Javadoc)
	 * @see com.weiyuan.BaseActivity#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			this.finish();
			break;
		case R.id.ok:
			modifyPwd();
			break;
		default:
			break;
		}
	}
}
