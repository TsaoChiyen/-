package com.chengxin.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chengxin.R;
import com.chengxin.Entity.Merchant;
import com.chengxin.Entity.MerchantEntity;
import com.chengxin.Entity.PopItem;
import com.chengxin.Entity.ShopAreaList;
import com.chengxin.adapter.MerchantAdapter;
import com.chengxin.dialog.SearchShopDialog;
import com.chengxin.global.FeatureFunction;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.net.WeiYuanException;
import com.chengxin.widget.HomePullToRefreshListView;
import com.chengxin.widget.HomePullToRefreshListView.OnChangeStateListener;
import com.chengxin.widget.PopWindows;
import com.chengxin.widget.PopWindows.PopWindowsInterface;

/**
 * 商户
 * 
 * @author dl
 * 
 */
public class MerchantFragment extends BaseFragment implements OnChangeStateListener, OnItemClickListener, OnClickListener {

	private static final int MSG_AREA_LIST = 0x8601;
	private Context mContext;
	private View mView;
	// private LinearLayout mMenuLayout;
	private HomePullToRefreshListView mContainer;
	private TextView mRefreshViewLastUpdated;
	private LinearLayout mFootView;
	// private LinearLayout mCategoryLinear;
	private boolean mIsRefreshing = false;
	private ListView mListView;

	private List<PopItem> mAreaMenuList;
	private PopWindows mAreaPopWindows;
	private String mCity = null;

	// //+++++商户更多菜单+++++
	// RelativeLayout upBtn;
	// private LinearLayout mMoreMenuLayout,mMerchantMoreMenuLayout;
	// -----商户更多菜单------

	// private int mItemWidth;
	private MerchantEntity mMerchant;
	List<Merchant> mNewsList = new ArrayList<Merchant>();
	// ListView mListView;
	MerchantAdapter mAdapter = null;

	// /*更多菜单*/
	// private List<PopItem> mPopList = new ArrayList<PopItem>();
	// private View mTopSpliteView;

	// private List<MerchantMenu> mMenuList = new ArrayList<MerchantMenu>();
	// private ImageLoader mImageLoader;
//	private int mClickId = 0;
	private RelativeLayout mToolBarLayout;
	private TextView mButtonArea;

	// 购物车
	// private RelativeLayout mBuyBtn;
	// private TextView mGoodsCountTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = MerchantFragment.this.getActivity();
		// mItemWidth = ScreenUtils.getScreenWidth(mContext)/4;
		// mImageLoader = new ImageLoader();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.merchant_fragment_view, container,
				false);
		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	void setupViews(View contentView) {
//		IntentFilter filter = new IntentFilter();
//		filter.addAction(GlobalParam.ACTION_REFRESH_MERCHANT_GOODS_COUNT);
//
		mToolBarLayout = (RelativeLayout)mView.findViewById(R.id.shop_tool_bar_layout);
		mButtonArea = (TextView)mView.findViewById(R.id.btn_shop_area);
		mButtonArea.setOnClickListener(this);
		
		getShopArea();
		
		mRefreshViewLastUpdated = (TextView) mView
				.findViewById(R.id.pull_to_refresh_time);
		mContainer = (HomePullToRefreshListView) mView
				.findViewById(R.id.container);
		mListView = mContainer.getList();
		mListView.setDivider(null);
		mListView.setSelector(mContext.getResources().getDrawable(
				R.drawable.transparent_selector));
		mContainer.setOnChangeStateListener(this);
		mListView.setOnItemClickListener(this);
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:

					int count = mListView.getFooterViewsCount() != 0 ? (view
							.getCount() - 1) : view.getCount();
					if (view.getLastVisiblePosition() == count
							&& (mMerchant != null && (mMerchant.pageInfo != null && mMerchant.pageInfo.hasMore == 1))) {
						if (mFootView != null) {
							Message message = new Message();
							message.what = GlobalParam.SHOW_LOADINGMORE_INDECATOR;
							message.obj = mFootView;
							mHandler.sendMessage(message);
						}
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

		mContainer.clickrefresh();
	}

	private void getNewsList(final int loadType, final String city) {
		if (!WeiYuanCommon.getNetWorkState()) {
			stopMsg(loadType);
			mHandler.sendEmptyMessage(GlobalParam.MSG_NETWORK_ERROR);
			return;
		}

		new Thread() {
			public void run() {
				try {
					int page = 0;
					if (mMerchant != null && mMerchant.pageInfo != null
							&& mMerchant.pageInfo.hasMore == 1) {
						page = mMerchant.pageInfo.currentPage + 1;
					} else {
						page = 1;
					}
					boolean isExitData = false;
					mMerchant = WeiYuanCommon.getWeiYuanInfo()
							.getShopList(page, 0, city);
					List<Merchant> tempList = new ArrayList<Merchant>();

					if (mMerchant.mMerchantList != null
							&& mMerchant.mMerchantList.size() > 0) {
						isExitData = true;
						tempList.addAll(mMerchant.mMerchantList);
					}

					WeiYuanCommon.sendMsg(mHandler,
							GlobalParam.MSG_CLEAR_LISTENER_DATA, tempList,
							loadType);

					stopMsg(loadType);

					if (!isExitData) {
						mHandler.sendEmptyMessage(GlobalParam.MSG_REMOVE_LISTVIEW_FOOTVIW);
					}
				} catch (WeiYuanException e) {
					e.printStackTrace();
					WeiYuanCommon.sendMsg(mHandler,
							GlobalParam.MSG_TICE_OUT_EXCEPTION, mContext
									.getResources()
									.getString(e.getStatusCode()));
				} catch (Exception e) {
					e.printStackTrace();
					stopMsg(loadType);
				}
			};
		}.start();
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		if (mListView.getHeaderViewsCount() != 0) {
			position--;
		}

		if (position >= 0 && position < mNewsList.size()) {
			/*
			 * Intent intent = new Intent(mContext, BrowserActivity.class);
			 * intent.putExtra("url", mNewsList.get(position).newsUrl);
			 * intent.putExtra("content", mNewsList.get(position).content);
			 * intent.putExtra("news", mNewsList.get(position));
			 * startActivity(intent);
			 */
		} else if (position == mNewsList.size()
				&& !(mMerchant != null && mMerchant.pageInfo != null && mMerchant.pageInfo.hasMore == 1)) {
			if (mFootView != null) {
				Message message = new Message();
				message.what = GlobalParam.SHOW_LOADINGMORE_INDECATOR;
				message.obj = mFootView;
				mHandler.sendMessage(message);
			}
		}
	}

	private void updateListView() {

		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		} else {
			boolean isLoadMore = (mMerchant != null
					&& mMerchant.pageInfo != null && mMerchant.pageInfo.hasMore == 1) ? true
					: false;
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
			case MSG_AREA_LIST:
				if (ShopAreaList.hasData()) {
					initAreaPopMenu();
				}
				break;
			case GlobalParam.MSG_REMOVE_LISTVIEW_FOOTVIW:
				if (mFootView != null && mListView.getFooterViewsCount() > 0) {
					mListView.removeFooterView(mFootView);
				}
				if (mAdapter != null) {
					mAdapter.notifyDataSetChanged();
				}
				break;
			case GlobalParam.SHOW_SCROLLREFRESH:
				if (mIsRefreshing) {
					mIsRefreshing = false;
					mContainer.onRefreshComplete();
				}

				if (mMerchant != null) {
					mMerchant = null;
				}

				getNewsList(GlobalParam.LIST_LOAD_REFERSH, mCity);

				break;

			case GlobalParam.HIDE_SCROLLREFRESH:
				mIsRefreshing = false;
				mContainer.onRefreshComplete();
				mAdapter = new MerchantAdapter(mContext, mNewsList);
				mListView.setAdapter(mAdapter);

				break;
			case GlobalParam.MSG_CLEAR_LISTENER_DATA:
				if ((msg.arg1 == GlobalParam.LIST_LOAD_FIRST || msg.arg1 == GlobalParam.LIST_LOAD_REFERSH)
						&& mNewsList != null && mNewsList.size() > 0) {
					mNewsList.clear();
					if (mAdapter != null) {
						mAdapter.notifyDataSetChanged();
					}
				}

				List<Merchant> tempList = (List<Merchant>) msg.obj;
				if (tempList != null && tempList.size() > 0) {
					mNewsList.addAll(tempList);
				}
				break;
			case GlobalParam.MSG_SHOW_LISTVIEW_DATA:
				updateListView();
				break;

			case GlobalParam.SHOW_PROGRESS_DIALOG:
				String dialogMsg = (String) msg.obj;
				showProgressDialog(dialogMsg);
				break;

			case GlobalParam.HIDE_PROGRESS_DIALOG:
				baseHideProgressDialog();
				break;

			case GlobalParam.MSG_LOAD_ERROR:
				String error_Detail = (String) msg.obj;
				if (error_Detail != null && !error_Detail.equals("")) {
					Toast.makeText(mContext, error_Detail, Toast.LENGTH_LONG)
							.show();
				} else {
					Toast.makeText(mContext, R.string.load_error,
							Toast.LENGTH_LONG).show();
				}
				break;

			case GlobalParam.MSG_NETWORK_ERROR:
				Toast.makeText(mContext, R.string.network_error,
						Toast.LENGTH_LONG).show();
				break;

			case GlobalParam.MSG_TICE_OUT_EXCEPTION:
				String message = (String) msg.obj;
				if (message == null || message.equals("")) {
					message = mContext.getString(R.string.timeout);
				}
				Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
				break;

			case GlobalParam.SHOW_LOADINGMORE_INDECATOR:
				LinearLayout footView = (LinearLayout) msg.obj;
				ProgressBar pb = (ProgressBar) footView
						.findViewById(R.id.hometab_addmore_progressbar);
				pb.setVisibility(View.VISIBLE);
				TextView more = (TextView) footView
						.findViewById(R.id.hometab_footer_text);
				more.setText(mContext.getString(R.string.add_more_loading));
				getNewsList(GlobalParam.LIST_LOAD_MORE, mCity);
				break;

			case GlobalParam.HIDE_LOADINGMORE_INDECATOR:
				if (mFootView != null) {
					ProgressBar pbar = (ProgressBar) mFootView
							.findViewById(R.id.hometab_addmore_progressbar);
					pbar.setVisibility(View.GONE);
					TextView moreView = (TextView) mFootView
							.findViewById(R.id.hometab_footer_text);
					moreView.setText(R.string.add_more);
				}

				if (mMerchant != null && mMerchant.pageInfo != null
						&& mMerchant.pageInfo.hasMore == 1) {
					((TextView) mFootView
							.findViewById(R.id.hometab_footer_text))
							.setText(mContext.getString(R.string.no_more_data));
				} else {
					((TextView) mFootView
							.findViewById(R.id.hometab_footer_text))
							.setText(mContext.getString(R.string.add_more));
				}

				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	private void stopMsg(int loadType) {
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
	
	private void getShopArea() {
		if (ShopAreaList.hasData()) {
			mHandler.sendEmptyMessage(MSG_AREA_LIST);
			return;
		}
		if(!WeiYuanCommon.getNetWorkState()){
			mHandler.sendEmptyMessage(GlobalParam.MSG_NETWORK_ERROR);
			return;
		}
		new Thread(){
			public void run() {
				try {
					ShopAreaList temp = WeiYuanCommon.getWeiYuanInfo().getShopAreaList();
					WeiYuanCommon.sendMsg(mHandler, MSG_AREA_LIST, temp);
				} catch (WeiYuanException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}
	
	private void initAreaPopMenu() {
        if (ShopAreaList.hasData()) {
            mAreaMenuList = (List<PopItem>) ShopAreaList.getPopMenuList();
            mAreaPopWindows = new PopWindows(mContext, mAreaMenuList, mToolBarLayout, new PopWindowsInterface() {

				@Override
    			public void onItemClick(int dataId, int position, View view) {
    				if (position == 0 ) {
        				mCity = null;
    				} else {
        				mCity = mAreaMenuList.get(position).option;
    				}

    				mButtonArea.setText(mAreaMenuList.get(position).option);
    				getNewsList(LIST_LOAD_FIRST, mCity);
    			}

            });
        }
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_shop_area:
			mAreaPopWindows.showGroupPopView(mAreaMenuList,
					Gravity.CENTER,
					R.drawable.no_top_arrow_bg,
					R.color.white,
					0);
			break;
		default:
			break;
		}
	}

	public void startSearch() {
		SearchShopDialog dialog = new SearchShopDialog(mContext, mNewsList);
		dialog.show();
	}
}
