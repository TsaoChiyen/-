package com.chengxin;


import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chengxin.R;
import com.chengxin.Entity.Goods;
import com.chengxin.Entity.GoodsEntity;
import com.chengxin.Entity.Login;
import com.chengxin.adapter.GoodsAdapter;
import com.chengxin.global.FeatureFunction;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.net.WeiYuanException;
import com.chengxin.widget.MyPullToRefreshListView;
import com.chengxin.widget.MyPullToRefreshListView.OnChangeStateListener;

public class GoodsListActivity extends BaseActivity implements OnChangeStateListener,
OnItemClickListener {

	private ListView mListView;
	private MyPullToRefreshListView mContainer;
	private TextView mRefreshViewLastUpdated;
	private boolean mIsRefreshing = false;
	private TextView mPhoneNumberTextView,mMerchantAddress;
	private LinearLayout mTelBtn;
	

	private GoodsEntity mGoodsEntity;
	private List<Goods> mGoodsList = new ArrayList<Goods>();
	private GoodsAdapter mAdapter;
	private LinearLayout mFootView;
	
	

	private int mShopid;
	private String mShopAddr,mShopPhone,mShopName;
	private Login mUser;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.goods_list_view);
		mUser = (Login)getIntent().getSerializableExtra("user");
		mShopid = getIntent().getIntExtra("shopid",0);
		mShopAddr = getIntent().getStringExtra("addr");
		mShopPhone = getIntent().getStringExtra("tel_phone");
		mShopName = getIntent().getStringExtra("shop_name");
		intCompent();
	}

	private void intCompent(){
		setTitleContent(R.drawable.back_btn, 0,mShopName);
		mLeftBtn.setOnClickListener(this);
		

		mRefreshViewLastUpdated = (TextView) findViewById(R.id.pull_to_refresh_time);
		mContainer = (MyPullToRefreshListView)findViewById(R.id.container);
		
		mPhoneNumberTextView = (TextView)findViewById(R.id.phone_number);
		mMerchantAddress = (TextView)findViewById(R.id.merchant_addr);
		mTelBtn = (LinearLayout)findViewById(R.id.tel_btn);
		mTelBtn.setOnClickListener(this);
		
		mListView = mContainer.getList();
		mListView.setDivider(getResources().getDrawable(R.drawable.splite));
		mListView.setCacheColorHint(0);
		mListView.setSelector(mContext.getResources().getDrawable(R.drawable.transparent_selector));
		mContainer.setOnChangeStateListener(this);
		mListView.setHeaderDividersEnabled(false);
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				int count = mListView.getFooterViewsCount()!=0?(view.getCount()-1):view.getCount();
				if(view.getLastVisiblePosition() == count && mGoodsEntity!=null && mGoodsEntity.pageInfo != null
						&& mGoodsEntity.pageInfo.hasMore == 1){
					if(mFootView!=null){
						Message message = new Message();
						message.what = GlobalParam.SHOW_LOADINGMORE_INDECATOR;
						message.obj = mFootView; 
						mHandler.sendMessage(message);
					}
					//mHandler.sendEmptyMessage(GlobalParam.SHOW_LOADINGMORE_INDECATOR);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
		mListView.setOnItemClickListener(this);
		mContainer.clickrefresh();
		
		mPhoneNumberTextView.setText(mShopPhone);
		mMerchantAddress.setText("地址："+mShopAddr);

	}


	private void getGoodsList(final int loadType){
		if(!WeiYuanCommon.getNetWorkState()){
			stopMsg(loadType,mHandler);
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}

		new Thread(){
			public void run() {
				try {
					int page = 0;
					if(mGoodsEntity != null && mGoodsEntity.pageInfo != null
							&& mGoodsEntity.pageInfo.hasMore == 1){
						page = mGoodsEntity.pageInfo.currentPage+1;
					}else{
						page = 1;
					}
					boolean isExitData = false;
					mGoodsEntity =	WeiYuanCommon.getWeiYuanInfo().getGoodsList(page,mShopid);
					List<Goods> tempList = new ArrayList<Goods>();
					if(mGoodsEntity.goodsList != null && mGoodsEntity.goodsList.size()>0){
						isExitData = true;
						tempList.addAll(mGoodsEntity.goodsList);
					}
					WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_CLEAR_LISTENER_DATA, tempList,loadType);
					stopMsg(loadType,mHandler);
					if(!isExitData){
						mHandler.sendEmptyMessage(GlobalParam.MSG_REMOVE_LISTVIEW_FOOTVIW);
					}
				} catch (WeiYuanException e) {
					e.printStackTrace();
					WeiYuanCommon.sendMsg(mBaseHandler, BASE_MSG_TIMEOUT_ERROR,
							mContext.getResources().getString(e.getStatusCode()));
				}catch (Exception e) {
					e.printStackTrace();
					stopMsg(loadType,mHandler);
				}
			};
		}.start();
	}

	private void updateListView(){
		if(mAdapter != null){
			mAdapter.notifyDataSetChanged();
		}else{
			boolean isLoadMore = (mGoodsEntity!=null && mGoodsEntity.pageInfo!=null && mGoodsEntity.pageInfo.hasMore == 1)?true:false;
			if (isLoadMore) {
				if (mFootView == null) {
					mFootView = (LinearLayout) LayoutInflater.from(mContext)
							.inflate(R.layout.hometab_listview_footer, null);
				}
				if (mListView.getFooterViewsCount() == 0) {
					mListView.addFooterView(mFootView);	
				}
			}

			mAdapter = new GoodsAdapter(mContext, mGoodsList);
			mListView.setAdapter(mAdapter);
		}

	}




	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			GoodsListActivity.this.finish();
			break;
		case R.id.tel_btn://联系商家
			if(mShopPhone == null || mShopPhone.equals("")){
				return;
			}
			Intent intent=new Intent("android.intent.action.CALL",Uri.parse("tel:"+mShopPhone));
			startActivity(intent);
		
			break;

		default:
			break;
		}
	}

	@Override
	public void onChangeState(MyPullToRefreshListView container, int state) {
		mRefreshViewLastUpdated.setText(FeatureFunction.getRefreshTime());
		switch (state) {
		case MyPullToRefreshListView.STATE_LOADING:
			mHandler.sendEmptyMessage(GlobalParam.SHOW_SCROLLREFRESH);
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(0<=arg2 && arg2<mGoodsList.size()){
			Intent intent = new Intent();
			intent.setClass(mContext, GoodsDetailActivity.class);
			intent.putExtra("entity", mGoodsList.get(arg2));
			intent.putExtra("addr", mShopAddr);
			intent.putExtra("tel_phone",mShopPhone);
			intent.putExtra("shop_name",mShopName);
			intent.putExtra("user", mUser);
			startActivity(intent);
		}else{

		}
	}


	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GlobalParam.MSG_REMOVE_LISTVIEW_FOOTVIW:
				if (mFootView != null && mListView.getFooterViewsCount()>0) {
					mListView.removeFooterView(mFootView); 
				}
				if (mAdapter != null) {
					mAdapter.notifyDataSetChanged();
				}
				break;
			case GlobalParam.MSG_CLEAR_LISTENER_DATA:
				if ((msg.arg1 == GlobalParam.LIST_LOAD_FIRST || msg.arg1 == GlobalParam.LIST_LOAD_REFERSH)
						&& mGoodsList!=null && mGoodsList.size()>0) {
					mGoodsList.clear();
					if(mAdapter!=null){
						mAdapter.notifyDataSetChanged();
					}
				}

				List<Goods> tempList = (List<Goods>)msg.obj;
				if(tempList!=null && tempList.size()>0){
					mGoodsList.addAll(tempList);
				}
				break;
			case GlobalParam.SHOW_SCROLLREFRESH:
				if (mIsRefreshing) {
					mContainer.onRefreshComplete();
					break;
				}
				mIsRefreshing = true;
				if(mGoodsEntity != null){
					mGoodsEntity = null;
				}
				getGoodsList(GlobalParam.LIST_LOAD_REFERSH);
				break;

			case GlobalParam.HIDE_SCROLLREFRESH:
				mIsRefreshing = false;
				mContainer.onRefreshComplete();
				updateListView();
				break;
			case GlobalParam.MSG_CHECK_STATE:
				if (mFootView != null && mListView.getFooterViewsCount()>0) {
					mListView.removeFooterView(mFootView); 
				}
				if (mAdapter != null) {
					mAdapter.notifyDataSetChanged();
				}
			case GlobalParam.MSG_SHOW_LISTVIEW_DATA:
				updateListView();
				break;
			case GlobalParam.SHOW_LOADINGMORE_INDECATOR:
				LinearLayout footView = (LinearLayout)msg.obj;				
				ProgressBar pb = (ProgressBar)footView.findViewById(R.id.hometab_addmore_progressbar);
				pb.setVisibility(View.VISIBLE);		 		
				TextView more = (TextView)footView.findViewById(R.id.hometab_footer_text);
				more.setText(mContext.getString(R.string.add_more_loading));
				getGoodsList(GlobalParam.LIST_LOAD_MORE);
				break;
			case GlobalParam.HIDE_LOADINGMORE_INDECATOR:
				if (mFootView == null) {
					mFootView = (LinearLayout) LayoutInflater.from(mContext)
							.inflate(R.layout.hometab_listview_footer, null);
				}
				ProgressBar spb = (ProgressBar)mFootView.findViewById(R.id.hometab_addmore_progressbar);
				spb.setVisibility(View.GONE);	
				TextView smore = (TextView)mFootView.findViewById(R.id.hometab_footer_text);
				smore.setText("");
				if (mAdapter != null){
					mAdapter.notifyDataSetChanged();
				}
				break;
		

			default:
				break;
			}
		}

	};




}
