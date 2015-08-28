package com.chengxin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.chengxin.R;
import com.chengxin.DB.DBHelper;
import com.chengxin.DB.MessageTable;
import com.chengxin.DB.SessionTable;
import com.chengxin.DB.UserTable;
import com.chengxin.Entity.Login;
import com.chengxin.Entity.LoginResult;
import com.chengxin.Entity.MessageType;
import com.chengxin.Entity.ResearchJiaState;
import com.chengxin.dialog.MMAlert;
import com.chengxin.dialog.MMAlert.OnAlertSelectId;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.GlobleType;
import com.chengxin.global.ImageLoader;
import com.chengxin.global.ResearchCommon;
import com.chengxin.net.ResearchException;

public class SubscriptionNumDetailActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener{
	Login mSubscription;
	Button mAddButton, mFocusBtn;
	ImageView mHeaderImageView;
	TextView mSubTitleTextView, mFuncTextView, mRenZhenTextView;
	ToggleButton mRecvinfoBtn;

	RelativeLayout mRecvinfoLayout,mHistorymsgLayout; 
	private int mIsRefresh;

	private ImageLoader mImageLoader = new ImageLoader();


	public final  static int MSG_SHOW_RESULT=0x00001;
	public final  static int MSG_SHOW_CHANGE_MESSAGERECV_RESULT=0x00002;
	public int mFromSearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subscriptionnum_detail_layout);
		mContext = this;
		mIsRefresh = getIntent().getIntExtra("isrefresh",0);
		mSubscription = (Login)getIntent().getSerializableExtra("subscription");
		mFromSearch = getIntent().getIntExtra("fromsearch",0);

		InitComponent();

		registerMonitor();

		refreshView();
	}

	private void InitComponent(){
		setTitleContent(R.drawable.back_btn, R.drawable.more_btn, R.string.detail_material);
		mLeftBtn.setOnClickListener(this);

		mAddButton = (Button)findViewById(R.id.addpubsh);
		mAddButton.setOnClickListener(this);

		mFocusBtn = (Button)findViewById(R.id.add_focus);
		mFocusBtn.setOnClickListener(this);

		mRecvinfoLayout = (RelativeLayout)findViewById(R.id.recvinfo_layout);
		mHistorymsgLayout = (RelativeLayout)findViewById(R.id.historymsg_layout);
		mHistorymsgLayout.setOnClickListener(this);
		
		/*if(mSubscription.type == 3){//服务号
			mFocusBtn.setVisibility(View.GONE);
		}*/
		if(mIsRefresh == 1){
			mRecvinfoLayout.setVisibility(View.GONE);
			mFocusBtn.setVisibility(View.GONE);
		}
		refreshData();

		//refreshView();
	}

	void refreshView(){
		mHeaderImageView = (ImageView)findViewById(R.id.header_image);
		mSubTitleTextView = (TextView)findViewById(R.id.sel_title);
		mFuncTextView = (TextView)findViewById(R.id.func_info);
		mRenZhenTextView = (TextView)findViewById(R.id.renzhen_info);
		mRecvinfoBtn = (ToggleButton)findViewById(R.id.recvinfo_btn);
		//mRecvinfoBtn.setOnCheckedChangeListener(this);
        mRecvinfoBtn.setOnClickListener(this);

		mImageLoader.getBitmap(mContext, mHeaderImageView, null, mSubscription.headsmall, 0, false, false);

        if (mSubscription.isfollow != null && mSubscription.isfollow.equals("1")) {
            setTitleContent(R.drawable.back_btn,  R.drawable.more_icon,  R.string.detail_material);
            mRightBtn.setOnClickListener(this);
            mFocusBtn.setText(R.string.cancle_focus);

            if(mFromSearch == 1){
                mRecvinfoLayout.setVisibility(View.GONE);
            }else{
                mRecvinfoLayout.setVisibility(View.VISIBLE);
            }
            mHistorymsgLayout.setVisibility(View.VISIBLE);
            mAddButton.setVisibility(View.GONE);

        }
        else {
            mFocusBtn.setText(R.string.focus);
            mAddButton.setVisibility(View.GONE);
            mRecvinfoLayout.setVisibility(View.GONE);
            mHistorymsgLayout.setVisibility(View.GONE);
        }

		if (mSubscription != null) {
			mSubTitleTextView.setText(mSubscription.name);
			mFuncTextView.setText(mSubscription.info);
			mRenZhenTextView.setText(mSubscription.auth);
			if (mSubscription.isGetMsg == 1) {
				mRecvinfoBtn.setChecked(true);
			}
			else {
				mRecvinfoBtn.setChecked(false);
			}
		}
	}

	private void registerMonitor() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(SUB_DETAIL_CHANGE);
		registerReceiver(mReceiver, filter);
	}

	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent){
			String action = intent.getAction();
			if (action.equals(SUB_DETAIL_CHANGE)) {
				Login sub  = (Login)intent.getSerializableExtra("sub_user");
				if (sub != null) {
					mSubscription = sub;
					refreshView();
				}
			}
		}
	};

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			SubscriptionNumDetailActivity.this.finish();
			break;
		case R.id.right_btn:
			showMoreDialog();
			break;
		case R.id.addpubsh:
			/*Intent intent = new Intent();
			intent.setClass(SubscriptionNumDetailActivity.this, AddPushMessageActivity.class);
			intent.putExtra("userid", mSubscription.uid);
			startActivity(intent);*/
			break;
		case R.id.add_focus:
            if (mSubscription.isfollow.equals("0")){
                addFoucs("1");
            }
            else {
                addFoucs("0");
            }

			break;
		case R.id.historymsg_layout:
			/*	Login user;
        	if (mSubscription.userID.equals(QiyueCommon.getUserId(mContext))) {
				user = QiyueCommon.getLoginResult(mContext);
			}else{*/
			SQLiteDatabase dbDatabase = DBHelper.getInstance(mContext).getReadableDatabase();
			UserTable userTable = new UserTable(dbDatabase);
			Login  user = userTable.query(mSubscription.uid);
			/*}*/
			if(user == null || user.equals("")){
				Toast.makeText(mContext, "本地暂无消息!", Toast.LENGTH_LONG).show();
				return;
			}
			Intent chatMainIntent = new Intent(mContext, ChatMainActivity.class);
			chatMainIntent.putExtra("data",user);
			chatMainIntent.putExtra("is_order", true);
			startActivityForResult(chatMainIntent,1);
			break;
        case R.id.recvinfo_btn:
            boolean isChecked = mRecvinfoBtn.isChecked();
            if(isChecked && mSubscription.isGetMsg==1){
                return;
            }
            addMessageListener(isChecked);
            break;

		default:
			break;
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SHOW_RESULT:
				ResearchJiaState status = (ResearchJiaState)msg.obj;
				if (status == null || status.code != 0) {
					if (status != null && status.errorMsg != null) {
						Toast.makeText(mContext, status.errorMsg,Toast.LENGTH_LONG).show();
					}
					else {
						if (msg.arg1 == 1) {
							if (mSubscription.isfollow.equals("1")) {
								Toast.makeText(mContext, R.string.cancel_focus_fail,Toast.LENGTH_LONG).show();
							}
							else {
								Toast.makeText(mContext,R.string.add_focus_fail,Toast.LENGTH_LONG).show();
							}

						}
						else if (msg.arg1 == 2) {
							Toast.makeText(mContext,R.string.delete_sub_fail,Toast.LENGTH_LONG).show();
						}
					}
				}
				else {
					if (msg.arg1 == 1) {
						if (mSubscription.isfollow.equals("1")) {
							mSubscription.isfollow = "0";
							mFocusBtn.setText(R.string.focus);
							SQLiteDatabase dbDatabase = DBHelper.getInstance(mContext).getReadableDatabase();
							UserTable userTable = new UserTable(dbDatabase);
							userTable.delete(mSubscription);
							Toast.makeText(mContext,R.string.cancel_focus_success,Toast.LENGTH_LONG).show();
						}
						else {
							mSubscription.isfollow = "1";
							mFocusBtn.setText(R.string.cancle_focus);
							SQLiteDatabase dbDatabase = DBHelper.getInstance(mContext).getReadableDatabase();
							UserTable userTable = new UserTable(dbDatabase);
							mSubscription.isGetMsg = 1;
							userTable.insert(mSubscription, 1);
							Toast.makeText(mContext,R.string.add_focus_success,Toast.LENGTH_LONG).show();
						}

						Intent intent = new Intent();
						intent.setAction(SUB_DETAIL_CHANGE);
						intent.putExtra("uid",mSubscription.uid);
						intent.putExtra("isFollow",mSubscription.isfollow);
						sendBroadcast(intent);
					}
					else if (msg.arg1 == 2) {
						Toast.makeText(mContext, R.string.delete_sub_success, Toast.LENGTH_LONG).show();
						
						SQLiteDatabase dbDatabase = DBHelper.getInstance(mContext).getReadableDatabase();
						UserTable userTable = new UserTable(dbDatabase);
						userTable.delete(mSubscription);
						
						Intent intent = new Intent();
						intent.setAction(SUB_DETAIL_CHANGE);
						intent.putExtra("uid",mSubscription.uid);
						sendBroadcast(intent);
						SubscriptionNumDetailActivity.this.finish();
					}
					
				}
				break;
			case MSG_SHOW_CHANGE_MESSAGERECV_RESULT:
                ResearchJiaState recv_status = (ResearchJiaState)msg.obj;
				if (recv_status == null || recv_status.code != 0) {
					if (recv_status != null && recv_status.errorMsg != null) {
						Toast.makeText(mContext, recv_status.errorMsg, Toast.LENGTH_LONG).show();
					}
				}
				else {
					if(recv_status.errorMsg!=null && !recv_status.errorMsg.equals("")){
						Toast.makeText(mContext, recv_status.errorMsg, Toast.LENGTH_LONG).show();
					}
					if (mSubscription.isGetMsg==1) {
						mRecvinfoBtn.setChecked(false);
						mSubscription.isGetMsg = 0;
					}
					else {
						mRecvinfoBtn.setChecked(true);
						mSubscription.isGetMsg = 1;
					}
					SQLiteDatabase dbDatabase = DBHelper.getInstance(mContext).getReadableDatabase();
					UserTable userTable = new UserTable(dbDatabase);
					userTable.updateIsGetMsg(mSubscription.uid,mSubscription.isGetMsg);
					
					Intent intent = new Intent();
					intent.setAction(SUB_DETAIL_CHANGE);
					intent.putExtra("uid",mSubscription.uid);
					intent.putExtra("isgetMsg",mSubscription.isGetMsg);
					sendBroadcast(intent);
					
				}
				break;
			case GlobalParam.MSG_SHOW_LOAD_DATA:
				LoginResult login = (LoginResult)msg.obj;
				if(login!=null && login.mState!=null && login.mState.code == 0){
					mSubscription = login.mLogin;
					refreshView();
				}
				break;
			}
		}
	};

	private void addFoucs(final String focus){
		ResearchCommon.sendMsg(mHandler, BASE_SHOW_PROGRESS_DIALOG,
				mContext.getResources().getString(R.string.send_request));
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
                    ResearchJiaState stauts;
					stauts = ResearchCommon.getResearchInfo().addFocus(mSubscription.uid, focus);

					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);

					Message msg = new Message();
					msg.what = MSG_SHOW_RESULT;
					msg.obj  = stauts;
					msg.arg1 = 1;

					mHandler.sendMessage(msg);

				} catch (ResearchException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void showConfireDialog(){
		MMAlert.showAlert(mContext, "是否确定清空内容?", mContext.getResources().
				getStringArray(R.array.confirm_item), 
				null, new OnAlertSelectId() {

			@Override
			public void onClick(int whichButton) {
				switch (whichButton) {
				case 0://清空内容
					
					SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
					MessageTable messageTable = new MessageTable(db);
					messageTable.delete(mSubscription.uid,0);
					SessionTable sessionTable = new SessionTable(db);
					sessionTable.delete(mSubscription.uid, MessageType.SUB_CHAT);
					sendBroadcast(new Intent(ChatMainActivity.REFRESH_ADAPTER));
					break;
			
				default:
					break;
				}
			}
		});
	}
	
	private void refreshData(){
		if (!ResearchCommon.getNetWorkState()) {
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread(){
			public void run() {
				try {
					LoginResult login = ResearchCommon.getResearchInfo().getPublicsInfo(mSubscription.uid);
                    ResearchCommon.sendMsg(mHandler, GlobalParam.MSG_SHOW_LOAD_DATA,login);
				} catch (ResearchException e) {
					e.printStackTrace();
                    ResearchCommon.sendMsg(mBaseHandler, BASE_MSG_TIMEOUT_ERROR,
							mContext.getResources().getString(e.getStatusCode()));
				}catch (Exception e) {
					e.printStackTrace();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				}
			};
		}.start();
	}

	private void showMoreDialog(){
		MMAlert.showAlert(mContext, null, mContext.getResources().
				getStringArray(R.array.order_more_item), 
				null, new OnAlertSelectId() {

			@Override
			public void onClick(int whichButton) {
				Log.e("whichButton", "whichButton: "+whichButton);
				switch (whichButton) {
				case 0://推荐给朋友
					Intent intent = new Intent(mContext, ChooseUserActivity.class);
					intent.putExtra("cardType",1);
					intent.putExtra("cardLogin",mSubscription);
					startActivity(intent);
					break;
				case 1://举报
					Intent jbIntent = new Intent(mContext, ReportedActivity.class);
					jbIntent.putExtra("fuid", mSubscription.uid);
					jbIntent.putExtra("type",GlobleType.REPORTED_SUB_TYPE);
					mContext.startActivity(jbIntent);
					break;
				case 2://清空内容
					showConfireDialog();
					break;
			
				default:
					break;
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.recvinfo_btn:
			/*if(isChecked && mSubscription.isGetMsg==1){
				return;
			}
			addMessageListener(isChecked);*/
			break;

		default:
			break;
		}
	}

	private void addMessageListener(final boolean isRecv){
		if(!ResearchCommon.getNetWorkState()){
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int recv = 0;
                    if (isRecv) {
                        recv = 1;
                    }
                    else {
                        recv = 0;
                    }
                    ResearchCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG,
                    		mContext.getResources().getString(R.string.send_request));
                    
                    ResearchJiaState stauts = ResearchCommon.getResearchInfo().isGetSubMsg(mSubscription.uid, recv);

                    mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);

                    Message msg = new Message();
                    msg.what = MSG_SHOW_CHANGE_MESSAGERECV_RESULT;
                    msg.obj  = stauts;

                    mHandler.sendMessage(msg);
                } catch (ResearchException e) {
                    e.printStackTrace();
                    ResearchCommon.sendMsg(mBaseHandler, BASE_MSG_TIMEOUT_ERROR,
                    		mContext.getResources().getString(e.getStatusCode()));
                }catch (Exception e) {
                	e.printStackTrace();
                	mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
                }
            }
        }).start();
	}

}

