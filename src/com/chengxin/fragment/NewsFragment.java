package com.chengxin.fragment;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chengxin.R;
import com.chengxin.Entity.Merchant;
import com.chengxin.Entity.MerchantEntity;
import com.chengxin.adapter.MerchantAdapter;
import com.chengxin.global.FeatureFunction;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.ImageLoader;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.net.WeiYuanException;
import com.chengxin.widget.HomeListView;
import com.chengxin.widget.HomePullToRefreshListView;
import com.chengxin.widget.HomePullToRefreshListView.OnChangeStateListener;

public class NewsFragment extends BaseFragment implements OnItemClickListener{

	private final static String TAG = "NewsFragment";
	Activity activity;
	List<Merchant> mNewsList = new ArrayList<Merchant>();
	//ListView mListView;
	MerchantAdapter mAdapter = null;

	String text;
	String channel_id;
	ImageView detail_loading;
	public final static int MSG_UPDATE_LISTVIEW = 0x10000;

	private HomePullToRefreshListView mContainer;
	private HomeListView mListView;


	private ImageLoader mImageLoader = new ImageLoader();
	protected int mShopType = 0;

	public int mPageIndex = 0;

	LinkedList<View> mDetailList;

	final int TYPE_FOCUS = 1;
	final int TYPE_LIST  = 2;

	private boolean mIsRefreshing = false;
	private LinearLayout mFootView;
	private boolean mLoading = false;
	private TextView mRefreshViewLastUpdated;
	private HashMap<String, Integer> mPageMap = new HashMap<String, Integer>();
	private HashMap<String, Boolean> mHasMoreMap = new HashMap<String, Boolean>();

	public final static String ACTION_REFRESH_NEWS = "com.chengdu.cdsjb.fragment.intent.action.ACTION_REFRESH_NEWS";
	public final static String ACTION_LOAD_DATA = "com.chengdu.cdsjb.fragment.intent.action.ACTION_LOAD_DATA";
	private int mInit = 0;

	private MerchantEntity mMerchant;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		Bundle args = getArguments();
		text = args != null ? args.getString("text") : "";
		channel_id = args != null ? args.getString("id", "0"):"0";
		if(!mPageMap.containsKey(channel_id)){
			mPageMap.put(channel_id, 1);
		}

		if(!mHasMoreMap.containsKey(channel_id)){
			mHasMoreMap.put(channel_id, false);
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		this.activity = activity;
		super.onAttach(activity);
	}
	/** 此方法意思为fragment是否可见 ,可见时候加载数据 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
	}

	View mView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup parent = null;  
		if (mView != null) {
			parent = (ViewGroup) mView.getParent(); 

			if (parent != null) {  
				parent.removeView(mView);  
			}
		}
		else {
			mView = LayoutInflater.from(mContext).inflate(R.layout.news_fragment, null);
		}
		return mView;
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(ACTION_REFRESH_NEWS)){
				mContainer.clickrefresh();
			}else if(intent.getAction().equals(ACTION_LOAD_DATA)){

				String id = intent.getStringExtra("id");
				if(id.equals(channel_id)){
					if(mNewsList == null || mNewsList.size() == 0){ //获取保存到本地的数据
						List<Merchant> newsList = WeiYuanCommon.getNewsList(mContext, channel_id);
						if(newsList != null && newsList.size() != 0){
							mNewsList.addAll(newsList);
							updateListView(TYPE_LIST);
						}
					}

					if(mInit == 0){//下拉刷新获取网络上的数据
						mContainer.clickrefresh();
					}

					mInit ++;
				}
			}
		}
	};

	private void getNewsList(final int loadType,final String typeId){
		if(!WeiYuanCommon.getNetWorkState()){
			stopMsg(loadType);
			mHandler.sendEmptyMessage(GlobalParam.MSG_NETWORK_ERROR);
			return;
		}

		new Thread(){
			public void run() {
				try {
					int page = 1;
					mMerchant =	WeiYuanCommon.getWeiYuanInfo().getMerchantkList(mShopType , page, 0);
					if(mMerchant.mMerchantList != null && mMerchant.mMerchantList.size()>0){
						List<Merchant> tempList = new ArrayList<Merchant>();
						tempList.addAll(mMerchant.mMerchantList);
						WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_CLEAR_LISTENER_DATA, tempList);
					}
					stopMsg(loadType);
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

		/*		new Thread() {
			@Override
			public void run() {

				if(WeiYuanCommon.verifyNetwork(mContext)){
					try {

						mLoading = true;
						//String fromid = "";
						int page = mPageMap.get(channel_id);
						switch (loadType) {
						case GlobalParam.LIST_LOAD_REFERSH:
							mPageMap.put(channel_id, 1);
							page = 1;
							break;
						default:
							break;
						}

						NewsList newsList = WeiYuanCommon.getPhonePaperInfo().getNewsList("1", channel_id, page);

						if(newsList != null && newsList.retCode == 0){

							boolean noMore = false;
							if(newsList.mPageInfo == null){
								if(loadType != GlobalParam.LIST_LOAD_REFERSH){
									noMore = true;
								}
							}else {

								if(newsList.mPageInfo.currentPage >= newsList.mPageInfo.totalPage){
									noMore = true;
								}else {

									mPageMap.put(channel_id, page + 1);
									noMore = false;
								}
							}

							mHasMoreMap.put(channel_id, noMore);

							Message message = new Message();
							message.what = MSG_UPDATE_LISTVIEW;
							message.arg1 = loadType;
							message.arg2 = TYPE_LIST;
							message.obj = newsList;
							mHandler.sendMessage(message);
						}else {
							Message msg=new Message();
							msg.what=GlobalParam.MSG_LOAD_ERROR;
							if(newsList != null && newsList.errorMsg != null && !newsList.errorMsg.equals("")){
								msg.obj = newsList.errorMsg;
							}else {
								msg.obj = mContext.getString(R.string.load_error);
							}
							mHandler.sendMessage(msg);
						}


			        } catch (WeiYuanException e) {
				        e.printStackTrace();
				        Message msg=new Message();
						msg.what=GlobalParam.MSG_TICE_OUT_EXCEPTION;
						msg.obj=mContext.getString(R.string.timeout);
						mHandler.sendMessage(msg);
			        }

					switch (loadType) {
					case GlobalParam.LIST_LOAD_FIRST:
						mHandler.sendEmptyMessage(GlobalParam.HIDE_PROGRESS_DIALOG);
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

					mLoading = false;
				}

			}
		}.start();*/
	}


	private void updateListView(Message message){
		Merchant newsList = (Merchant)message.obj;

		if(newsList == null){
			return;
		}
/*
		if (message.arg2 == TYPE_LIST) {
			if (newsList != null && newsList.retCode == 0) {
				if (message.arg1 == GlobalParam.LIST_LOAD_MORE) {
					if(newsList.mList != null){
						mNewsList.addAll(newsList.mList);	
					}
	            }
				else {
					mNewsList.clear();

					if(newsList.mList != null){
						mNewsList.addAll(newsList.mList);	
					}
				}

				WeiYuanCommon.saveNewsList(mContext, mNewsList, channel_id);
	        }
		}
		else if (message.arg2 == TYPE_FOCUS) {

			stopTimer();

			if (mFocusList != null) {
				mFocusList.clear();
			}

			if(newsList.mList != null){
				mFocusList.addAll(newsList.mList);
			}

			showImage();
		}
*/

		if (mAdapter == null) {
			mAdapter = new MerchantAdapter(mContext, mNewsList);
			if (mListView.getFooterViewsCount() == 0){	 			
				mFootView = (LinearLayout) LayoutInflater.from(activity)
						.inflate(R.layout.hometab_listview_footer, null);
				ProgressBar pb = (ProgressBar)mFootView.findViewById(R.id.hometab_addmore_progressbar);
				pb.setVisibility(View.GONE);

				mListView.addFooterView(mFootView);		

				if(mHasMoreMap.get(channel_id)){
					((TextView)mFootView.findViewById(R.id.hometab_footer_text)).setText(mContext.getString(R.string.no_more_data));
				}else {
					((TextView)mFootView.findViewById(R.id.hometab_footer_text)).setText(mContext.getString(R.string.add_more));
				}
			}else {
				if(mHasMoreMap.get(channel_id)){
					((TextView)mFootView.findViewById(R.id.hometab_footer_text)).setText(mContext.getString(R.string.no_more_data));
				}else {
					((TextView)mFootView.findViewById(R.id.hometab_footer_text)).setText(mContext.getString(R.string.add_more));
				}
			}

			mListView.setAdapter(mAdapter);
		}
		else {

			if(mHasMoreMap.get(channel_id)){
				((TextView)mFootView.findViewById(R.id.hometab_footer_text)).setText(mContext.getString(R.string.no_more_data));
			}else {
				((TextView)mFootView.findViewById(R.id.hometab_footer_text)).setText(mContext.getString(R.string.add_more));
			}

			mAdapter.notifyDataSetChanged();
			//mListView.setAdapter(mAdapter);
		}

		//mPullRefreshListView.onRefreshComplete();
	}

	private void updateListView(int type){

		/*if (type == TYPE_FOCUS) {

			showImage();
		}
		 */

		if (mAdapter == null) {
			mAdapter = new MerchantAdapter(mContext, mNewsList);

			if (mListView.getFooterViewsCount() == 0){	 			
				mFootView = (LinearLayout) LayoutInflater.from(activity)
						.inflate(R.layout.hometab_listview_footer, null);
				ProgressBar pb = (ProgressBar)mFootView.findViewById(R.id.hometab_addmore_progressbar);
				pb.setVisibility(View.GONE);

				mListView.addFooterView(mFootView);		

				if(mHasMoreMap.get(channel_id)){
					((TextView)mFootView.findViewById(R.id.hometab_footer_text)).setText(mContext.getString(R.string.no_more_data));
				}else {
					((TextView)mFootView.findViewById(R.id.hometab_footer_text)).setText(mContext.getString(R.string.add_more));
				}
			}else {
				if(mHasMoreMap.get(channel_id)){
					((TextView)mFootView.findViewById(R.id.hometab_footer_text)).setText(mContext.getString(R.string.no_more_data));
				}else {
					((TextView)mFootView.findViewById(R.id.hometab_footer_text)).setText(mContext.getString(R.string.add_more));
				}
			}

			mListView.setAdapter(mAdapter);
		}
		else {

			if(mHasMoreMap.get(channel_id)){
				((TextView)mFootView.findViewById(R.id.hometab_footer_text)).setText(mContext.getString(R.string.no_more_data));
			}else {
				((TextView)mFootView.findViewById(R.id.hometab_footer_text)).setText(mContext.getString(R.string.add_more));
			}

			mAdapter.notifyDataSetChanged();
			//mListView.setAdapter(mAdapter);
		}

		//mPullRefreshListView.onRefreshComplete();
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
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
					getNewsList(GlobalParam.LIST_LOAD_REFERSH,channel_id);
				/*}*/

				break;

			case GlobalParam.HIDE_SCROLLREFRESH:
				mIsRefreshing = false;
				mContainer.onRefreshComplete();
				updateListView(TYPE_FOCUS);
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
				updateListView(TYPE_FOCUS);
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
				getNewsList(GlobalParam.LIST_LOAD_MORE,channel_id);
				break;

			case GlobalParam.HIDE_LOADINGMORE_INDECATOR:
				if (mFootView != null){
					ProgressBar pbar = (ProgressBar)mFootView.findViewById(R.id.hometab_addmore_progressbar);
					pbar.setVisibility(View.GONE);
					TextView moreView = (TextView)mFootView.findViewById(R.id.hometab_footer_text);
					moreView.setText(R.string.add_more);
				}

				if(mHasMoreMap.get(channel_id)){
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

	/* 初始化选择城市的header
	public void initCityChannel() {
		View headview = LayoutInflater.from(activity).inflate(R.layout.city_category_list_tip, null);
		TextView chose_city_tip = (TextView) headview.findViewById(R.id.chose_city_tip);
		chose_city_tip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, CityListActivity.class);
				startActivity(intent);
			}
		});
		mListView.addHeaderView(headview);
	}*/

	/* 初始化通知栏目*/
	/*private void initNotify() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				//notify_view_text.setText(String.format(getString(R.string.ss_pattern_update), 10));
				notify_view.setVisibility(View.VISIBLE);
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						notify_view.setVisibility(View.GONE);
					}
				}, 2000);
			}
		}, 1000);
	}*/

	@Override
	public void onResume() {
		super.onResume();

		/*if(mAdapter != null){
	    	mAdapter = new NewsAdapter(mContext, mNewsList);
	    	mListView.setAdapter(mAdapter);
	    }*/

	}

	/* 摧毁视图 */
	@Override
	public void onDestroyView() {

		recyleBitmap();

		super.onDestroyView();
		Log.d("onDestroyView", "channel_id = " + channel_id);
		//System.gc();
	}
	/* 摧毁该Fragment，一般是FragmentActivity 被摧毁的时候伴随着摧毁 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "channel_id = " + channel_id);
		//stopTimer();
		//System.gc();

	}

	private void recyleBitmap(){
		/*if(mDetailList != null){
			for (int i = 0; i < mDetailList.size(); i++) {
				if(mDetailList.get(i) != null){
					ImageView imageView = (ImageView) mDetailList.get(i).findViewById(R.id.image);
					if(imageView  != null){
						imageView.setImageBitmap(null);
						imageView.setImageResource(R.drawable.normal);
					}
				}
			}
		}

		if(mImageLoader != null){
			FeatureFunction.freeBitmap(mImageLoader.getImageBuffer());
		}*/

		if(mNewsList != null){
			for (int i = 0; i < mNewsList.size(); i++) {
				ImageView imageView = (ImageView)mListView.findViewWithTag(mNewsList.get(i).logo);
				if(imageView != null){
					imageView.setImageBitmap(null);
				}
			}
		}

		if(mAdapter != null){
			FeatureFunction.freeBitmap(mAdapter.getImageBuffer());
		}

		System.gc();
	}

	private void recycleBitmapCaches(int start, int end){                
		if(mAdapter != null){
			HashMap<String, Bitmap> buffer = mAdapter.getImageBuffer();
			if(buffer != null){
				for(int i = start; i < end; i++){

					if(!TextUtils.isEmpty(mNewsList.get(i).logo)){
						String url = mNewsList.get(i).logo;
						ImageView imageView = (ImageView)mListView.findViewWithTag(url);
						if (imageView != null) {
							imageView.setImageBitmap(null);
							imageView.setImageResource(R.drawable.normal);
						}

						Bitmap bitmap = buffer.get(url);
						if (bitmap != null && !bitmap.isRecycled()) {
							bitmap.recycle();
							bitmap = null;
							buffer.remove(url);
						}
					}

				}
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(mListView.getHeaderViewsCount() != 0){
			position --;
		}

		if(position >= 0 && position < mNewsList.size()){
			/*Intent intent = new Intent(mContext, BrowserActivity.class);
		    intent.putExtra("url", mNewsList.get(position).newsUrl);
		    intent.putExtra("content", mNewsList.get(position).content);
		    intent.putExtra("news", mNewsList.get(position));
		    startActivity(intent);*/
		}else if(position == mNewsList.size() && !mHasMoreMap.get(channel_id)){
			if(mFootView!=null){
				Message message = new Message();
				message.what = GlobalParam.SHOW_LOADINGMORE_INDECATOR;
				message.obj = mFootView; 
				mHandler.sendMessage(message);
			}
		}
	}

	@Override
	void setupViews(View contentView) {
		mRefreshViewLastUpdated = (TextView) contentView.findViewById(R.id.pull_to_refresh_time);
		mContainer = (HomePullToRefreshListView) contentView.findViewById(R.id.container);
		mContainer.setOnChangeStateListener(new OnChangeStateListener() {

			@Override
			public void onChangeState(HomePullToRefreshListView container, int state) {
				mRefreshViewLastUpdated.setText(FeatureFunction.getRefreshTime());
				switch (state) {
				case HomePullToRefreshListView.STATE_LOADING:
					mHandler.sendEmptyMessage(GlobalParam.SHOW_SCROLLREFRESH);
					break;
				}
			}
		});

		mListView = mContainer.getList();
		mListView.setDivider(null);
		mListView.setDividerHeight(FeatureFunction.dip2px(mContext, 1));
		mListView.setCacheColorHint(0);
		mListView.setOnItemClickListener(this);

		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_REFRESH_NEWS);
		filter.addAction(ACTION_LOAD_DATA);
		mContext.registerReceiver(mReceiver, filter);

		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:

					if(view.getLastVisiblePosition() == (view.getCount()-1) && !mHasMoreMap.get(channel_id) && !mLoading){
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
				if(mListView.getHeaderViewsCount() == 1){
					firstVisibleItem = firstVisibleItem - 1;

					if(mListView.getFooterViewsCount() == 1){
						totalItemCount = totalItemCount - 2;
					}
				}else {
					totalItemCount = totalItemCount - 1;
				}

				int beforeItem = firstVisibleItem - 4;
				if(beforeItem > 0){
					recycleBitmapCaches(0, beforeItem);
				}

				int endItem = firstVisibleItem + visibleItemCount + 4;
				if(endItem < totalItemCount){
					recycleBitmapCaches(endItem, totalItemCount);
				}
			}
		});

		if(channel_id.equals("0")){
			if(mNewsList == null || mNewsList.size() == 0){
				List<Merchant> newsList = WeiYuanCommon.getNewsList(mContext, channel_id);
				if(newsList != null && newsList.size() != 0){
					mNewsList.addAll(newsList);
					updateListView(TYPE_LIST);
				}
			}

			if(mInit == 0){
				mContainer.clickrefresh();
			}

			mInit ++;
		}
	}

	private Timer mPicTimer;
	private TimerTask mTimerTask;
	private int mScrollCellCount = 0;
	private final static int CHANGE_IMAGE = 15345;

	private void startTimer(){
		if (mPicTimer != null) {
			return;
		}
		mPicTimer = new Timer();
		mTimerTask = new TimerTask() {
			@Override
			public void run() {
				if (++mScrollCellCount < 3) {
					return;
				}

				mScrollCellCount = 0;

				mPageIndex ++;
				/*if (mDetailList.size() < SMALL_VIEWPAGER_COUNT && mPageIndex == mDetailList.size()) {
					mPageIndex = 0;
                }
				else if(mPageIndex >= (Integer.MAX_VALUE - INIT_VIEWPAGER_INDEX)){
					mPageIndex = INIT_VIEWPAGER_INDEX;
				}*/
				if (mPageIndex == mDetailList.size()) {
					mPageIndex = 0;
				}
				mHandler.sendEmptyMessage(CHANGE_IMAGE);
			}
		};
		mPicTimer.schedule(mTimerTask, 1000, 1000);
	}

	private void stopTimer(){
		if(mPicTimer != null){
			mPicTimer.cancel();
			mPicTimer.purge();
			mPicTimer = null;
		}
	}

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

}
