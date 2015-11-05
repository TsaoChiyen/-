package com.chengxin;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chengxin.R;
import com.chengxin.Entity.MerchantMenu;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.GlobleType;
import com.chengxin.global.ImageLoader;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.net.WeiYuanException;

/**
 * 商品类别
 * @author dl
 *
 */
public class GoodsTypeActivity extends BaseActivity{


	private LinearLayout mMenuLayout;
	private List<MerchantMenu> mMenuList = new ArrayList<MerchantMenu>();
	
	private ImageLoader mImageLoader;
	protected int mShopType = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.goods_type_view);
		mShopType  = getIntent().getIntExtra("type", GlobleType.SHOPPING_MANAGER);
		mImageLoader = new ImageLoader();
		initCompent();
		getGoodsType();
	}

	private void initCompent(){
		setTitleContent(R.drawable.back_btn,0,R.string.goods_type);
		mLeftBtn.setOnClickListener(this);
		mMenuLayout = (LinearLayout)findViewById(R.id.menu_layout);
	}

	private void getGoodsType(){
		if (MerchantMenu.hasData()) {
			mMenuList.addAll(MerchantMenu.getMenuList());
			initMenu();
			return;
		}
		
		if(!WeiYuanCommon.getNetWorkState()){
			mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}

		new Thread(){
			public void run() {
				try {
					WeiYuanCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG,
							mContext.getResources().getString(R.string.get_dataing));
					MerchantMenu merchantMenu =	WeiYuanCommon.getWeiYuanInfo().getShopType(mShopType);
					WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_SHOW_MERCHANT_MENU_TYPE, merchantMenu);
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				} catch (WeiYuanException e) {
					e.printStackTrace();
					WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_TICE_OUT_EXCEPTION,
							mContext.getResources().getString(e.getStatusCode()));
				}catch (Exception e) {
					e.printStackTrace();
					mHandler.sendEmptyMessage(GlobalParam.HIDE_PROGRESS_DIALOG);
				}
			};
		}.start();
	}


	/*初始化菜单*/
	private void initMenu(){
		if(mMenuLayout != null && mMenuLayout.getChildCount() >0){
			mMenuLayout.removeAllViews();
		}
		
		for (int i = 0; i < mMenuList.size(); i++) {
			View view = LayoutInflater.from(mContext).inflate(R.layout.goods_menu_type_item,null);
			ImageView icon = (ImageView)view.findViewById(R.id.menu_icon);
			TextView menuTextView = (TextView)view.findViewById(R.id.menu_name);
			
			String menuIconString = mMenuList.get(i).logo;
			
			if(menuIconString == null || menuIconString.equals("")){
				icon.setImageResource(R.drawable.all_type_icon);
			}else{
				mImageLoader.getBitmap(mContext, icon, null,menuIconString,0,false, false,false);
			}
			
			final int index = i;
			menuTextView.setText(mMenuList.get(i).name);
			
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra("menu_entity", mMenuList.get(index));
					setResult(RESULT_OK,intent);
					GoodsTypeActivity.this.finish();
				}
			});
			
			mMenuLayout.addView(view);
		}
	}




	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			GoodsTypeActivity.this.finish();
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
			case GlobalParam.MSG_SHOW_MERCHANT_MENU_TYPE:
				if (MerchantMenu.hasData()) {
					mMenuList.addAll(MerchantMenu.getMenuList());
					initMenu();
				}
				break;

			default:
				break;
			}
		}

	};


}
