package com.chengxin.fragment;

import java.io.IOException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.transition.ChangeBounds;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chengxin.ApplyMerchantActivity;
import com.chengxin.CaptureActivity;
import com.chengxin.EditProfileActivity;
import com.chengxin.FeedBackActivity;
import com.chengxin.FriensLoopActivity;
import com.chengxin.GameWebViewActivity;
import com.chengxin.KeyNumberActivity;
import com.chengxin.MettingActivity;
import com.chengxin.MyAlbumActivity;
import com.chengxin.MyFavoriteActivity;
import com.chengxin.NearyUserListActivity;
import com.chengxin.R;
import com.chengxin.SendGoodsActivity;
import com.chengxin.SettingTab;
import com.chengxin.ShakeActivity;
import com.chengxin.DB.DBHelper;
import com.chengxin.DB.SessionTable;
import com.chengxin.Entity.Login;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.GlobleType;
import com.chengxin.global.ImageLoader;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.sortlist.PinYin;

/**
 * 发现Fragment的界面
 * 
 * @author dl
 */
public class MyFragment extends Fragment implements OnClickListener {

	// private Button btn_cancel;
	private View mMenuView;
	private LinearLayout my_profile, my_photo, my_collection, my_setting, my_feedback, mApplyMerchant, my_bank;
	private TextView mApplyNameTexViwe;
	private ImageView mApplyIcon;
	private ImageLoader mImageLoader;

	private void aaaa() {
		Fragment aaFragment = this.getParentFragment();
		FragmentManager fff = this.getChildFragmentManager();
		FragmentActivity aa = this.getActivity();
		Context context = this.getActivity();
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.bottomdialog, null);

		mImageLoader = new ImageLoader();

		/*
		 * btn_cancel = (Button) mMenuView.findViewById(R.id.btn_cancel); //ȡ��ť btn_cancel.setOnClickListener(new OnClickListener() {
		 * 
		 * public void onClick(View v) { //��ٵ����� //SaveDate.saveDate(context, new OAuthV2());
		 * 
		 * } });
		 */
		my_profile = (LinearLayout) mView.findViewById(R.id.my_profile);
		ImageView iv = (ImageView) mView.findViewById(R.id.user_icon);
		TextView userName = (TextView) mView.findViewById(R.id.user_name);
		TextView userSign = (TextView) mView.findViewById(R.id.user_sign);
		mApplyIcon = (ImageView) mView.findViewById(R.id.apply_icon);
		mApplyNameTexViwe = (TextView) mView.findViewById(R.id.apply_text);
		Login login = WeiYuanCommon.getLoginResult(context);
		if (login != null) {
			if (login.headsmall != null && !login.headsmall.equals("")) {
				mImageLoader.getBitmap(context, iv, null, login.headsmall, 0, false, true, false);
			}
			if (WeiYuanCommon.getLoginResult(context).isshop == 1) {// 商家-发布商品
				//mApplyIcon.setImageResource(R.drawable.send_goods_icon);
				//mApplyNameTexViwe.setText("钱包");
			}
		}

		userName.setText(login.nickname);
		userSign.setText(login.sign);

		my_profile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				itemsOnClick.onClick(v);

			}
		});

		my_photo = (LinearLayout) mView.findViewById(R.id.my_photo);
		my_photo.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				itemsOnClick.onClick(v);

			}
		});
		my_collection = (LinearLayout) mView.findViewById(R.id.my_collection);
		my_collection.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				itemsOnClick.onClick(v);

			}
		});

		mApplyMerchant = (LinearLayout) mView.findViewById(R.id.apply_merchant);
		mApplyMerchant.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				itemsOnClick.onClick(v);
			 
			}
		});

		my_bank= (LinearLayout) mView.findViewById(R.id.my_bank);
		my_bank.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				itemsOnClick.onClick(v);
			 
			}
		});
		
		
		
		my_setting = (LinearLayout) mView.findViewById(R.id.my_setting);
		my_setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				itemsOnClick.onClick(v);

			}
		});
		// my_feedback = (LinearLayout) mView.findViewById(R.id.my_feedback);
		// my_feedback.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// itemsOnClick.onClick(v);
		// dismiss();
		// }
		// });
		// // ���ð�ť����
		// // ����SelectPicPopupWindow��View
		// this.setContentView(mMenuView);
		// // ����SelectPicPopupWindow��������Ŀ�
		// this.setWidth(w / 2/* +20 */);
		// // ����SelectPicPopupWindow��������ĸ�
		// this.setHeight(LayoutParams.WRAP_CONTENT);
		// // ����SelectPicPopupWindow��������ɵ��
		// this.setFocusable(true);
		// // ����SelectPicPopupWindow�������嶯��Ч��
		// this.setAnimationStyle(R.style.mystyle);
		// // ʵ��һ��ColorDrawable��ɫΪ��͸��
		// ColorDrawable dw = new ColorDrawable(000000);
		// // ����SelectPicPopupWindow��������ı���
		// // this.setBackgroundDrawable(dw);
		// this.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.no_top_arrow_bg));
		// // mMenuView���OnTouchListener�����жϻ�ȡ����λ�������ѡ�����������ٵ�����
		// mMenuView.setOnTouchListener(new OnTouchListener() {
		//
		// public boolean onTouch(View v, MotionEvent event) {
		//
		// int height = mMenuView.findViewById(R.id.pop_layout).getTop();
		// int y = (int) event.getY();
		// if (event.getAction() == MotionEvent.ACTION_UP) {
		// if (y < height) {
		// dismiss();
		// }
		// }
		// return true;
		// }
		// });
	}

	// 为弹出窗口实现监听类
	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {

			Context mContext = MyFragment.this.getActivity();

			switch (v.getId()) {
			case R.id.my_profile:
				/*
				 * Intent profileIntent = new Intent(); profileIntent.setClass(mContext, ProfileActivity.class); mContext.startActivity(profileIntent);
				 */
				Intent intent = new Intent(mContext, EditProfileActivity.class);
				/* intent.putExtra("login", mLogin); */
				startActivity(intent);
				break;
			case R.id.my_photo:
				Intent albumIntent = new Intent();
				albumIntent.setClass(mContext, MyAlbumActivity.class);
				mContext.startActivity(albumIntent);
				break;
			case R.id.my_collection:
				Intent collectionIntent = new Intent();
				collectionIntent.setClass(mContext, MyFavoriteActivity.class);
				mContext.startActivity(collectionIntent);
				break;
			case R.id.my_bank:
			case R.id.apply_merchant:
				Toast.makeText(getActivity(), "该功能正在开发中...", 3000).show();
				
//				Intent ApplyIntent = new Intent();
//				if (WeiYuanCommon.getLoginResult(mContext).isshop == 1) {// 商家-发布商品
//					ApplyIntent.setClass(mContext, SendGoodsActivity.class);
//				} else {
//					ApplyIntent.setClass(mContext, ApplyMerchantActivity.class);// 申请成为商家
//				}
//
//				mContext.startActivity(ApplyIntent);
				break;
			case R.id.my_setting:
				Intent settingIntent = new Intent();
				settingIntent.setClass(mContext, SettingTab.class);
				mContext.startActivity(settingIntent);
				break;
			case R.id.my_feedback:
				Intent feedbackIntent = new Intent();
				feedbackIntent.setClass(mContext, FeedBackActivity.class);
				mContext.startActivity(feedbackIntent);
				break;

			default:
				break;
			}
			// if (menuWindow != null && menuWindow.isShowing()) {
			// menuWindow.dismiss();
			// menuWindow = null;
			// }

		}
	};

	/**************************************************************************************************************************************************************************************************
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
		mParentContext = (Context) MyFragment.this.getActivity();
		PinYin.main();
	}

	/**
	 * 加载控件
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.chat_tab_my, container, false);
		return mView;
	}

	/**
	 * 初始化界面
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		aaaa();

        RelativeLayout mWeiGouWuLayout = (RelativeLayout) mView.findViewById(R.id.weigouwu_layout);
        RelativeLayout mShanJiaLayout = (RelativeLayout) mView.findViewById(R.id.shanjia_layout);
        RelativeLayout mYouHuiLayout = (RelativeLayout) mView.findViewById(R.id.youhui_layout);
        RelativeLayout mTuanGouLayout = (RelativeLayout) mView.findViewById(R.id.tuanggou_layout);
        RelativeLayout mHuoDongLayout = (RelativeLayout) mView.findViewById(R.id.huodong_layout);

        mWeiGouWuLayout.setOnClickListener(this);
        mShanJiaLayout.setOnClickListener(this);
        mYouHuiLayout.setOnClickListener(this);
        mTuanGouLayout.setOnClickListener(this);
        mHuoDongLayout.setOnClickListener(this);
		// mFriendsLoopLayout = (RelativeLayout) mView.findViewById(R.id.outlander_content);
		// mMeetingLayout = (RelativeLayout) mView.findViewById(R.id.app_news_content);
		// mNearyLayout = (RelativeLayout) mView.findViewById(R.id.server_content);
		// mShaoLayout = (RelativeLayout) mView.findViewById(R.id.shao_layout);
		//
		// mNewsFriendsLoopIcon = (TextView) mView.findViewById(R.id.friends_message_count);
		// mNewMeetingIcon = (TextView) mView.findViewById(R.id.app_news_message_count);
		//
		// mFriendsLoopLayout.setOnClickListener(this);
		// mMeetingLayout.setOnClickListener(this);
		// mNearyLayout.setOnClickListener(this);
		// mShaoLayout.setOnClickListener(this);
		// register();
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
						int count = table.queryMeetingSessionCount();
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
            case R.id.yao_layout: // 扫一扫
                Intent yaoIntent = new Intent(mParentContext, ShakeActivity.class);
                startActivity(yaoIntent);
                break;
            case R.id.weigouwu_layout:
                Intent mallIntent = new Intent();
                mallIntent.setClass(mParentContext, GameWebViewActivity.class);
                mallIntent.putExtra("type", GlobleType.FOUND_FUN_MALL);
                mallIntent.putExtra("url", WeiYuanCommon.getLoginResult(mParentContext).shop.goodslist);
                mParentContext.startActivity(mallIntent);
                break;
            case R.id.shanjia_layout:
                Intent merchant = new Intent();
                merchant.setClass(mParentContext, KeyNumberActivity.class);
                merchant.putExtra("type", 5);
                merchant.putExtra("url", WeiYuanCommon.getLoginResult(mParentContext).shop.merchantlist);
                merchant.putExtra("title", "商家");
                mParentContext.startActivity(merchant);
                break;
            case R.id.youhui_layout:
                Intent youHui = new Intent();
                youHui.setClass(mParentContext, KeyNumberActivity.class);
                youHui.putExtra("type", 3);
                youHui.putExtra("url", WeiYuanCommon.getLoginResult(mParentContext).shop.youhulist);
                youHui.putExtra("title", "优惠");
                mParentContext.startActivity(youHui);
                break;
            case R.id.tuanggou_layout:
                Intent tuan = new Intent();
                tuan.setClass(mParentContext, KeyNumberActivity.class);
                tuan.putExtra("type", 4);
                tuan.putExtra("url", WeiYuanCommon.getLoginResult(mParentContext).shop.tuanlist);
                tuan.putExtra("title", "找团购");
                mParentContext.startActivity(tuan);
                break;
            case R.id.huodong_layout:
                Intent eventIntent = new Intent();
                eventIntent.setClass(mParentContext, GameWebViewActivity.class);
                eventIntent.putExtra("type", GlobleType.FOUND_FUN_EVENT);
                eventIntent.putExtra("url", WeiYuanCommon.getLoginResult(mParentContext).shop.eventlist);
                mParentContext.startActivity(eventIntent);
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
