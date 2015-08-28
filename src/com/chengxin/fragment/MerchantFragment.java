package com.chengxin.fragment;


import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chengxin.GoodsDetailActivity;
import com.chengxin.R;
import com.chengxin.ShoppingCartActivity;
import com.chengxin.Entity.Merchant;
import com.chengxin.Entity.MerchantEntity;
import com.chengxin.Entity.MerchantMenu;
import com.chengxin.Entity.PopItem;
import com.chengxin.Entity.ShoppingCart;
import com.chengxin.adapter.MerchantAdapter;
import com.chengxin.global.FeatureFunction;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.ImageLoader;
import com.chengxin.global.ScreenUtils;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.net.WeiYuanException;
import com.chengxin.widget.HomePullToRefreshListView;
import com.chengxin.widget.HomePullToRefreshListView.OnChangeStateListener;

/**
 * 商户
 * @author dl
 *
 */
public class MerchantFragment extends BaseFragment implements OnClickListener,
OnChangeStateListener, OnItemClickListener{

	private Context mContext;
	private View mView;
	private LinearLayout mMenuLayout;
	private HomePullToRefreshListView mContainer;
	private TextView mRefreshViewLastUpdated;
	private LinearLayout mFootView;
	private LinearLayout mCategoryLinear;
	private boolean mIsRefreshing = false;
	private ListView mListView;

	//+++++商户更多菜单+++++
	RelativeLayout upBtn;
	private LinearLayout mMoreMenuLayout,mMerchantMoreMenuLayout;
	//-----商户更多菜单------

	private int mItemWidth;
	private MerchantEntity mMerchant;
	List<Merchant> mNewsList = new ArrayList<Merchant>();
	//ListView mListView;
	MerchantAdapter mAdapter = null;

	/*更多菜单*/
	private List<PopItem> mPopList = new ArrayList<PopItem>();
	private View mTopSpliteView;


	private List<MerchantMenu> mMenuList = new ArrayList<MerchantMenu>();
	private ImageLoader mImageLoader;
	private int mClickId;

	//购物车
	private RelativeLayout mBuyBtn;
	private TextView mGoodsCountTextView;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = MerchantFragment.this.getActivity();
		mItemWidth = ScreenUtils.getScreenWidth(mContext)/4;
		mImageLoader = new ImageLoader();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView= inflater.inflate(R.layout.merchant_fragment_view, container, false);
		return mView;	
	}



	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	private BroadcastReceiver mRefreshReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent == null || 
					(intent.getAction() == null || intent.getAction().equals(""))){
				return;
			}
			String action = intent.getAction();
			if(action.equals(GlobalParam.ACTION_REFRESH_MERCHANT_GOODS_COUNT)){
				mGoodsCountTextView.setText(WeiYuanCommon.getGoodsCount()+"");

			}
		}
	};

	@Override
	void setupViews(View contentView) {
		IntentFilter filter = new IntentFilter();
		filter.addAction(GlobalParam.ACTION_REFRESH_MERCHANT_GOODS_COUNT);
		mContext.registerReceiver(mRefreshReceiver, filter);

		mTopSpliteView = (View)mView.findViewById(R.id.top_splite);
		mBuyBtn = (RelativeLayout)mView.findViewById(R.id.buy_btn);
		mBuyBtn.setOnClickListener(this);
		mGoodsCountTextView = (TextView)mView.findViewById(R.id.shpping_number);
		mGoodsCountTextView.setText(WeiYuanCommon.getGoodsCount()+"");

		mMenuLayout = (LinearLayout)mView.findViewById(R.id.menu_layout);
		mCategoryLinear = (LinearLayout)mView.findViewById(R.id.category_linear);
		mRefreshViewLastUpdated = (TextView) mView.findViewById(R.id.pull_to_refresh_time);
		mContainer = (HomePullToRefreshListView) mView.findViewById(R.id.container);
		mListView = mContainer.getList();
		mListView.setDivider(null);
		mListView.setSelector(mContext.getResources().getDrawable(R.drawable.transparent_selector));
		mContainer.setOnChangeStateListener(this);
		mListView.setOnItemClickListener(this);
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:

					int count = mListView.getFooterViewsCount()!=0?(view.getCount()-1):view.getCount();
					if(view.getLastVisiblePosition() == count && (mMerchant !=null 	&&
							(mMerchant.pageInfo!=null && mMerchant.pageInfo.hasMore == 1))){
						if(mFootView!=null){
							Message message = new Message();
							message.what = GlobalParam.SHOW_LOADINGMORE_INDECATOR;
							message.obj = mFootView; 
							mHandler.sendMessage(message);
						}
						//mHandler.sendEmptyMessage(GlobalParam.SHOW_LOADINGMORE_INDECATOR);
					}
					break;

				default:
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});

		mMoreMenuLayout = (LinearLayout)mView.findViewById(R.id.menu_dialog_layout);
		mMoreMenuLayout.setOnClickListener(this);
		mMerchantMoreMenuLayout = (LinearLayout) mView.findViewById(R.id.merchant_more_menu_layout);
		RelativeLayout upBtn = (RelativeLayout)mView.findViewById(R.id.up_layout);
		upBtn.setOnClickListener(clickListener);
		getMerchantType();

		//initMenu();

	}




	private void getMerchantType(){
		if(!WeiYuanCommon.getNetWorkState()){
			mHandler.sendEmptyMessage(GlobalParam.MSG_NETWORK_ERROR);
			return;
		}
		new Thread(){
			public void run() {
				try {
					MerchantMenu merchantMenu =	WeiYuanCommon.getWeiYuanInfo().getShopType();
					WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_SHOW_MERCHANT_MENU_TYPE, merchantMenu);
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


	/**初始化商户类型菜单*/
	private void initMenu(){
		/*	String[] menuTitleArray = mContext.getResources().getStringArray(R.array.merber_noraml_menu_array);
		String[] menuIconArray = mContext.getResources().getStringArray(R.array.merber_noraml_icon_menu_array);*/
		if(mMenuList == null || mMenuList.size() <= 0){
			return;
		}

		List<MerchantMenu> tempList = new ArrayList<MerchantMenu>();
		if(mMenuList.size() <=3){
			for (int i = 0; i < mMenuList.size(); i++) {
				tempList.add(mMenuList.get(i));
			}
		}else{
			for (int i = 0; i < 3; i++) {
				tempList.add(mMenuList.get(i));
			}
		}
		if(tempList != null && tempList.size()>0){
			tempList.add(new MerchantMenu(3, "merber_down_arrow_icon",""));
		}else{
			return;
		}

		LayoutInflater inflater =LayoutInflater.from(mContext);
		for (int i = 0; i <tempList.size(); i++) {
			View view = inflater.inflate(R.layout.member_menu_item,null); 
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mItemWidth,mItemWidth-FeatureFunction.dip2px(mContext, 30));
			//params.gravity = Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL;
			view.setLayoutParams(params);

			ImageView splite = (ImageView)view.findViewById(R.id.splite);
			TextView titleTv = (TextView)view.findViewById(R.id.menu_text);
			if(i == tempList.size() - 1){
				titleTv.setVisibility(view.GONE);
				splite.setVisibility(View.GONE);
			}else{
				titleTv.setVisibility(view.VISIBLE);
				splite.setVisibility(View.VISIBLE);
			}
			ImageView iconImg = (ImageView)view.findViewById(R.id.menu_icon);
			titleTv.setText(tempList.get(i).name);



			if(i == 0){
				iconImg.setImageResource(FeatureFunction.getSourceIdByName(tempList.get(i).logo));
				mClickId = tempList.get(i).id;
				mContainer.clickrefresh();
			}else if(i == 3){
				iconImg.setImageResource(FeatureFunction.getSourceIdByName(tempList.get(i).logo));
			}else{
				if(tempList.get(i).logo != null && !tempList.get(i).logo.equals("")){
					mImageLoader.getBitmap(mContext, iconImg, null, tempList.get(i).logo,0, false,false,false);
				}else{
					iconImg.setImageResource(R.drawable.all_type_icon);
				}
			}
			final int id = i;
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					switch (id) {
					case 0://全部
						mClickId = mMenuList.get(id).id;
						mContainer.clickrefresh();
						break;
					case 1://女装
						mClickId = mMenuList.get(id).id;
						mContainer.clickrefresh();
						break;
					case 2://男装
						mClickId = mMenuList.get(id).id;
						mContainer.clickrefresh();
						break;
					case 3://显示更多菜单
						mMoreMenuLayout.setVisibility(View.VISIBLE);
						//初始化更多商户菜单	
						merchantMoreMenu();
						break;
					default:
						break;
					}
				}
			});
			mMenuLayout.addView(view);
		}
	}


	private void getNewsList(final int loadType,final int typeId){
		if(!WeiYuanCommon.getNetWorkState()){
			stopMsg(loadType);
			mHandler.sendEmptyMessage(GlobalParam.MSG_NETWORK_ERROR);
			return;
		}

		new Thread(){
			public void run() {
				try {
					int page = 0;
					if(mMerchant != null && mMerchant.pageInfo != null
							&& mMerchant.pageInfo.hasMore == 1){
						page = mMerchant.pageInfo.currentPage+1;
					}else{
						page = 1;
					}
					boolean isExitData = false;
					mMerchant =	WeiYuanCommon.getWeiYuanInfo().getMerchantkList(page, typeId);
					List<Merchant> tempList = new ArrayList<Merchant>();
					if(mMerchant.mMerchantList != null && mMerchant.mMerchantList.size()>0){
						isExitData = true;						
						tempList.addAll(mMerchant.mMerchantList);
					}
					WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_CLEAR_LISTENER_DATA, tempList,loadType);
					
					stopMsg(loadType);
					if(!isExitData){
						mHandler.sendEmptyMessage(GlobalParam.MSG_REMOVE_LISTVIEW_FOOTVIW);
					}
				} catch (WeiYuanException e) {
					e.printStackTrace();
					WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_TICE_OUT_EXCEPTION,
							mContext.getResources().getString(e.getStatusCode()));
				}catch (Exception e) {
					e.printStackTrace();
					stopMsg(loadType);
				}
			};
		}.start();
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		System.gc();
		if(mRefreshReceiver != null){
			mContext.unregisterReceiver(mRefreshReceiver);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buy_btn:
			Intent cartIntent = new Intent();
			cartIntent.setClass(mContext, ShoppingCartActivity.class);
			startActivity(cartIntent);
			break;
		case R.id.menu_dialog_layout:
			mMoreMenuLayout.setVisibility(View.GONE);
			break;

		default:
			break;
		}
	}

	@Override
	public void onChangeState(HomePullToRefreshListView container, int state) {
		mRefreshViewLastUpdated.setText(FeatureFunction.getRefreshTime());
		switch (state) {
		case HomePullToRefreshListView.STATE_LOADING:
			mHandler.sendEmptyMessage(GlobalParam.SHOW_SCROLLREFRESH);
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		if(mListView.getHeaderViewsCount() != 0){
			position --;
		}

		if(position >= 0 && position < mNewsList.size()){
			/*Intent intent = new Intent(mContext, BrowserActivity.class);
		    intent.putExtra("url", mNewsList.get(position).newsUrl);
		    intent.putExtra("content", mNewsList.get(position).content);
		    intent.putExtra("news", mNewsList.get(position));
		    startActivity(intent);*/
		}else if(position == mNewsList.size() && !(mMerchant!=null && mMerchant.pageInfo != null
				&& mMerchant.pageInfo.hasMore == 1)){
			if(mFootView!=null){
				Message message = new Message();
				message.what = GlobalParam.SHOW_LOADINGMORE_INDECATOR;
				message.obj = mFootView; 
				mHandler.sendMessage(message);
			}
		}
	}


	private void updateListView(){

		if(mAdapter != null){
			mAdapter.notifyDataSetChanged();
		}else{
			boolean isLoadMore = (mMerchant!=null && mMerchant.pageInfo!=null && mMerchant.pageInfo.hasMore == 1)?true:false;
			if (isLoadMore) {
				if (mFootView == null) {
					mFootView = (LinearLayout) LayoutInflater.from(mContext)
							.inflate(R.layout.hometab_listview_footer, null);
				}
				if (mListView.getFooterViewsCount() == 0) {
					mListView.addFooterView(mFootView);	
				}
			}

			mAdapter = new MerchantAdapter(mContext, mNewsList);
			mListView.setAdapter(mAdapter);
		}

	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GlobalParam.MSG_SHOW_MERCHANT_MENU_TYPE:
				MerchantMenu menu = (MerchantMenu)msg.obj;
				if(menu != null && menu.menuList!=null && menu.menuList.size()>0){
					if(mMenuList != null && mMenuList.size() >0){
						mMenuList.clear();
					}
					mMenuList.add(new MerchantMenu(0,"all_type_icon","全部"));
					mMenuList.addAll(menu.menuList);
					initMenu();
					mContainer.clickrefresh();
				}
				break;
			case GlobalParam.MSG_REMOVE_LISTVIEW_FOOTVIW:
				if (mFootView != null && mListView.getFooterViewsCount()>0) {
					mListView.removeFooterView(mFootView); 
				}
				if (mAdapter != null) {
					mAdapter.notifyDataSetChanged();
				}
				break;
				/*case MSG_UPDATE_LISTVIEW:
				updateListView(msg);
				break;*/
			case GlobalParam.SHOW_SCROLLREFRESH:
				if(mIsRefreshing){
					mIsRefreshing = false;
					mContainer.onRefreshComplete();
				}
				/*
				if(channel_id.equals("0")){
					getCoverNewsList(GlobalParam.LIST_LOAD_REFERSH);
				}else {*/
				if(mMerchant != null){
					mMerchant = null;
				}
				getNewsList(GlobalParam.LIST_LOAD_REFERSH,mClickId);
				/*}*/

				break;

			case GlobalParam.HIDE_SCROLLREFRESH:
				mIsRefreshing = false;
				mContainer.onRefreshComplete();
				mAdapter = new MerchantAdapter(mContext, mNewsList);
				mListView.setAdapter(mAdapter);
			/*	updateListView();*/
				break;
			case GlobalParam.MSG_CLEAR_LISTENER_DATA:
				if((msg.arg1 == GlobalParam.LIST_LOAD_FIRST || msg.arg1 == GlobalParam.LIST_LOAD_REFERSH)
						&& mNewsList != null && mNewsList.size()>0){
					mNewsList.clear();
					if(mAdapter!=null){
						mAdapter.notifyDataSetChanged();
					}
				}

				List<Merchant> tempList = (List<Merchant>)msg.obj;
				if(tempList!=null && tempList.size()>0){
					mNewsList.addAll(tempList);
				}
				break;
			case GlobalParam.MSG_SHOW_LISTVIEW_DATA:
				updateListView();
				break;

			case GlobalParam.SHOW_PROGRESS_DIALOG:
				String dialogMsg = (String)msg.obj;
				showProgressDialog(dialogMsg);
				break;

			case GlobalParam.HIDE_PROGRESS_DIALOG:
				baseHideProgressDialog();
				break;

			case GlobalParam.MSG_LOAD_ERROR:
				String error_Detail = (String)msg.obj;
				if(error_Detail != null && !error_Detail.equals("")){
					Toast.makeText(mContext,error_Detail,Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(mContext,R.string.load_error,Toast.LENGTH_LONG).show();
				}
				break;

			case GlobalParam.MSG_NETWORK_ERROR:
				Toast.makeText(mContext,R.string.network_error,Toast.LENGTH_LONG).show();
				break;

			case GlobalParam.MSG_TICE_OUT_EXCEPTION:
				String message=(String)msg.obj;
				if (message==null || message.equals("")) {
					message = mContext.getString(R.string.timeout);
				}
				Toast.makeText(mContext,message, Toast.LENGTH_LONG).show();
				break;

			case GlobalParam.SHOW_LOADINGMORE_INDECATOR:
				LinearLayout footView = (LinearLayout)msg.obj;				
				ProgressBar pb = (ProgressBar)footView.findViewById(R.id.hometab_addmore_progressbar);
				pb.setVisibility(View.VISIBLE);		 		
				TextView more = (TextView)footView.findViewById(R.id.hometab_footer_text);
				more.setText(mContext.getString(R.string.add_more_loading));
				getNewsList(GlobalParam.LIST_LOAD_MORE,mClickId);
				break;

			case GlobalParam.HIDE_LOADINGMORE_INDECATOR:
				if (mFootView != null){
					ProgressBar pbar = (ProgressBar)mFootView.findViewById(R.id.hometab_addmore_progressbar);
					pbar.setVisibility(View.GONE);
					TextView moreView = (TextView)mFootView.findViewById(R.id.hometab_footer_text);
					moreView.setText(R.string.add_more);
				}

				if(mMerchant != null && mMerchant.pageInfo != null && mMerchant.pageInfo.hasMore == 1){
					((TextView)mFootView.findViewById(R.id.hometab_footer_text)).setText(mContext.getString(R.string.no_more_data));
				}else {
					((TextView)mFootView.findViewById(R.id.hometab_footer_text)).setText(mContext.getString(R.string.add_more));
				}

				/*if (mAdapter != null){
					mAdapter.notifyDataSetChanged();
				}*/
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};


	private void stopMsg(int loadType){
		switch (loadType) {
		case GlobalParam.LIST_LOAD_FIRST:
			mHandler.sendEmptyMessage(GlobalParam.HIDE_PROGRESS_DIALOG);
			mHandler.sendEmptyMessage(GlobalParam.MSG_SHOW_LISTVIEW_DATA);
			break;
		case GlobalParam.LIST_LOAD_MORE:
			mHandler.sendEmptyMessage(GlobalParam.HIDE_LOADINGMORE_INDECATOR);
			break;

		case GlobalParam.LIST_LOAD_REFERSH:
			mHandler.sendEmptyMessage(GlobalParam.HIDE_SCROLLREFRESH);
			break;

		default:
			break;
		}
	}



	//+++++商户菜单++++++

	/**初始化商户更多菜单*/
	private void merchantMoreMenu(){
		if(mMerchantMoreMenuLayout != null && mMerchantMoreMenuLayout.getChildCount() >0){
			mMerchantMoreMenuLayout.removeAllViews();
		}
		LayoutInflater inflater =LayoutInflater.from(mContext);
		int rows = mMenuList.size() % 4 == 0?mMenuList.size()/4:mMenuList.size()/4+1;

		for (int i = 0; i < rows ; i++) {
			LinearLayout layout = new LinearLayout(mContext);
			if(i != rows-1){
				layout.setBackgroundResource(R.drawable.goods_info_splite);
			}
			layout.setOrientation(LinearLayout.HORIZONTAL);
			layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			for (int j = 0; j < 4; j++) {
				final int pos = i * 4 + j;
				if(pos < mMenuList.size()){
					View view = inflater.inflate(R.layout.member_menu_item,null); 
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mItemWidth,mItemWidth);
					//params.gravity = Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL;
					view.setLayoutParams(params);

					ImageView splite = (ImageView)view.findViewById(R.id.splite);
					TextView titleTv = (TextView)view.findViewById(R.id.menu_text);
					/*if(j== mMenuList.size() - 1){
						splite.setVisibility(View.GONE);
					}else{*/
						splite.setVisibility(View.VISIBLE);
					/*}*/
					ImageView iconImg = (ImageView)view.findViewById(R.id.menu_icon);
					titleTv.setText(mMenuList.get(pos).name);
					if(pos == 0){
						iconImg.setImageResource(R.drawable.all_type_icon);
					}
					else{
						if(mMenuList.get(pos).logo != null && !mMenuList.get(pos).logo.equals("")){
							mImageLoader.getBitmap(mContext, iconImg, null, mMenuList.get(pos).logo,0, false,false,false);
						}else{
							iconImg.setImageResource(R.drawable.all_type_icon);
						}
					}
					
					view.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							mClickId = mMenuList.get(pos).id;
							mMoreMenuLayout.setVisibility(View.GONE);
							mContainer.clickrefresh();
						}
					});
					layout.addView(view);
				}
			}
			mMerchantMoreMenuLayout.addView(layout);
		}
	}


	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			mMoreMenuLayout.setVisibility(View.GONE);
		}
	};
	//----商户菜单-----



}
