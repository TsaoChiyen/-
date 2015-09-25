package com.chengxin.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chengxin.CaptureActivity;
import com.chengxin.FriensLoopActivity;
import com.chengxin.GameWebViewActivity;
import com.chengxin.MettingActivity;
import com.chengxin.NearyUserListActivity;
import com.chengxin.R;
import com.chengxin.DB.DBHelper;
import com.chengxin.DB.SessionTable;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.sortlist.PinYin;

/**
 * 发现Fragment的界面
 * 
 * @author dl
 */
public class FoundFragment extends Fragment implements OnClickListener {

	/**
	 * 定义全局变量
	 */
	private View mView;

	private RelativeLayout mFriendsLoopLayout, mMeetingLayout, mNearyLayout, mShaoLayout;
	private Context mParentContext;
	private TextView mNewsFriendsLoopIcon, mNewMeetingIcon;

	/**
	 * 导入控件
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mParentContext = (Context) FoundFragment.this.getActivity();
		PinYin.main();
	}

	/**
	 * 加载控件
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.chat_tab_header, container, false);
		return mView;
	}

	/**
	 * 初始化界面
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mFriendsLoopLayout = (RelativeLayout) mView.findViewById(R.id.outlander_content);
		mMeetingLayout = (RelativeLayout) mView.findViewById(R.id.app_news_content);
		mNearyLayout = (RelativeLayout) mView.findViewById(R.id.server_content);
		mShaoLayout = (RelativeLayout) mView.findViewById(R.id.shao_layout);

		mView.findViewById(R.id.weigouwu_layout);
		mView.findViewById(R.id.shanjia_layout);
		mView.findViewById(R.id.youhui_layout);
		mView.findViewById(R.id.tuanggou_layout);
		mView.findViewById(R.id.huodong_layout);
		RelativeLayout mYouXiLayout = (RelativeLayout) mView.findViewById(R.id.youxi_layout);
		RelativeLayout mYaoLayout = (RelativeLayout) mView.findViewById(R.id.yao_layout);
		
		mNewsFriendsLoopIcon = (TextView) mView.findViewById(R.id.friends_message_count);
		mNewMeetingIcon = (TextView) mView.findViewById(R.id.app_news_message_count);

		mFriendsLoopLayout.setOnClickListener(this);
		mMeetingLayout.setOnClickListener(this);
		mNearyLayout.setOnClickListener(this);
		mShaoLayout.setOnClickListener(this);
		mYaoLayout.setOnClickListener(this);
		
		/*mWeiGouWuLayout.setOnClickListener(this);
		mShanJiaLayout.setOnClickListener(this);
		mYouHuiLayout.setOnClickListener(this);
		mTuanGouLayout.setOnClickListener(this);
		mHuoDongLayout.setOnClickListener(this);*/
		mYouXiLayout.setOnClickListener(this);

		register();
	}

	/**
	 * 注册界面通知
	 */
	private void register() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(GlobalParam.ACTION_SHOW_NEW_FRIENDS_LOOP);
		filter.addAction(GlobalParam.ACTION_HIDE_NEW_FRIENDS_LOOP);
		filter.addAction(GlobalParam.ACTION_SHOW_NEW_MEETING);
		filter.addAction(GlobalParam.ACTION_HIDE_NEW_MEETING);
		mParentContext.registerReceiver(mReBoradCast, filter);
	}

	/**
	 * 处理通知
	 */
	BroadcastReceiver mReBoradCast = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null) {
				String action = intent.getAction();
				if (action.equals(GlobalParam.ACTION_SHOW_NEW_FRIENDS_LOOP)) {
					if (mNewsFriendsLoopIcon != null) {
						int count = WeiYuanCommon.getFriendsLoopTip(mParentContext);
						if (count != 0) {
							mNewsFriendsLoopIcon.setVisibility(View.VISIBLE);
							mNewsFriendsLoopIcon.setText(count + "");
						}
					}
				} else if (action.equals(GlobalParam.ACTION_HIDE_NEW_FRIENDS_LOOP)) {
					if (mNewsFriendsLoopIcon != null) {
						mNewsFriendsLoopIcon.setVisibility(View.GONE);
					}
				} else if (action.equals(GlobalParam.ACTION_SHOW_NEW_MEETING)) {
					if (mNewMeetingIcon != null) {
						SQLiteDatabase db = DBHelper.getInstance(mParentContext).getReadableDatabase();
						SessionTable table = new SessionTable(db);
						table.queryMeetingSessionCount();
						mNewMeetingIcon.setVisibility(View.VISIBLE);
						/*
						 * if(count!=0){ mNewMeetingIcon.setVisibility(View.VISIBLE); //mNewMeetingIcon.setText(count+""); }
						 */
					}
				} else if (action.equals(GlobalParam.ACTION_HIDE_NEW_MEETING)) {
					if (mNewMeetingIcon != null) {
						mNewMeetingIcon.setVisibility(View.GONE);
					}
				}
			}
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 按钮点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.outlander_content:
			Intent intent = new Intent();
			intent.setClass(mParentContext, FriensLoopActivity.class);
			startActivity(intent);
			break;
		case R.id.app_news_content:
			Intent meeting = new Intent();
			meeting.setClass(mParentContext, MettingActivity.class);
			startActivity(meeting);
			break;
		case R.id.server_content:
			Intent nearyIntent = new Intent();
			nearyIntent.setClass(mParentContext, NearyUserListActivity.class);
			startActivity(nearyIntent);
			// NearyUserListActivity
			break;
		case R.id.shao_layout: // 扫一扫
			Intent scanIntent = new Intent(mParentContext, CaptureActivity.class);
			startActivity(scanIntent);
			break;
		case R.id.youxi_layout:
			Intent gameIntent = new Intent();
			gameIntent.setClass(mParentContext, GameWebViewActivity.class);
			gameIntent.putExtra("url", WeiYuanCommon.getLoginResult(mParentContext).shop.game);
			mParentContext.startActivity(gameIntent);
			break;
		default:
			break;
		}

	}

	/**
	 * 销毁页面
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mReBoradCast != null) {
			mParentContext.unregisterReceiver(mReBoradCast);
		}
	}

}
