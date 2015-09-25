package com.chengxin;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.chengxin.Entity.WeiYuanState;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.net.WeiYuanException;

/**
 * 忘记密码
 * @author dongli
 *
 */
public class ForgetPwdActity extends BaseActivity {

	private Button mOkBtn;
	private String mInputPhone;
	private Dialog  mDialog;

	/*
	 * 处理消息
	 */
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GlobalParam.MSG_CHECK_STATE:
				WeiYuanState state = (WeiYuanState)msg.obj;
				if(state == null ||state.code!=0 ){
					Toast.makeText(mContext, R.string.commit_data_error,Toast.LENGTH_LONG).show();
					return;
				}
				createDialog(mContext);

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
		setContentView(R.layout.forget_pwd);
		mContext = this;
		initCompent();
	}

	/*
	 * 实例化控件
	 */
	private void initCompent(){
		setTitleContent(R.drawable.back_btn,0,R.string.find_pwd);
		mLeftBtn.setOnClickListener(this);
		mOkBtn = (Button)findViewById(R.id.ok_btn);
		mOkBtn.setOnClickListener(this);
	}

	/*
	 * 查找密码
	 */
	private void findPwd(){
		if(!WeiYuanCommon.getNetWorkState()){
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread(){
			public void run() {
				try {
					WeiYuanCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG, "数据提交中,请稍后...");
					WeiYuanState state = WeiYuanCommon.getWeiYuanInfo().findPwd(mInputPhone);
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
			ForgetPwdActity.this.finish();
			break;
		case R.id.ok_btn:
		
			break;

		default:
			break;
		}
	}


	private void createDialog(Context context) {
		mDialog = new Dialog (context,R.style.dialog);
		LayoutInflater factor = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View serviceView = factor.inflate(R.layout.normal_hint_dialog, null);

		mDialog.setContentView(serviceView);
		mDialog.show();
		mDialog.setCancelable(false);	
		mDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT
				/*mContext.getResources().getDimensionPixelSize(R.dimen.bind_phone_height)*/
				, LayoutParams.WRAP_CONTENT);


		TextView chatContent=(TextView) serviceView
				.findViewById(R.id.card_title);
		chatContent.setText("新密码已发送到你的手机上，请注意查收！");

		Button okBtn=(Button)serviceView.findViewById(R.id.yes);

		okBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(mDialog!=null){
					mDialog.dismiss();
					mDialog = null;
				}
				ForgetPwdActity.this.finish();
			}
		});

	}


}
