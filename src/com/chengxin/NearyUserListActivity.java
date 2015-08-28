package com.chengxin;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
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
import com.chengxin.Entity.Login;
import com.chengxin.Entity.UserList;
import com.chengxin.adapter.SearchResultAdapter;
import com.chengxin.global.FeatureFunction;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.net.WeiYuanException;
import com.chengxin.widget.MyPullToRefreshListView;
import com.chengxin.widget.MyPullToRefreshListView.OnChangeStateListener;

public class NearyUserListActivity extends BaseActivity implements OnChangeStateListener, OnItemClickListener{

	private ListView mListView;
	private MyPullToRefreshListView mContainer;
	private TextView mRefreshViewLastUpdated;
	private boolean mIsRefreshing = false;

	private LinearLayout mFootView;

	private SearchResultAdapter mAdapter;
	private UserList mUser;
	private List<Login> mDataList = new ArrayList<Login>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.neary_user_list);
		mContext = this;
		initCompent();
		getNearyUserData(GlobalParam.LIST_LOAD_FIRST);
	}

	private void initCompent(){
		setTitleContent(R.drawable.back_btn,0,"附近的人");
		mLeftBtn.setOnClickListener(this);
		mRefreshViewLastUpdated = (TextView) findViewById(R.id.pull_to_refresh_time);
		mContainer = (MyPullToRefreshListView)findViewById(R.id.container);
		mListView = mContainer.getList();
		mListView.setDivider(getResources().getDrawable(R.drawable.splite));
		mListView.setCacheColorHint(0);
		mListView.setSelector(mContext.getResources().getDrawable(R.drawable.transparent_selector));
		mContainer.setOnChangeStateListener(this);
		mListView.setHeaderDividersEnabled(false);
		mListView.setOnItemClickListener(this);
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (view.getLastVisiblePosition() == mDataList.size() && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
					if(mUser == null){
						return;
					}
					//boolean isLoadMore = (mUser!=null && mUser.mPageInfo.currentPage == mUser.mPageInfo.totalPage)?false:true;
					if ( mUser.mPageInfo.hasMore == 1) {
						if (mFootView == null) {
							mFootView = (LinearLayout) LayoutInflater.from(mContext)
									.inflate(R.layout.hometab_listview_footer, null);
						}

						ProgressBar pb = (ProgressBar)mFootView.findViewById(R.id.hometab_addmore_progressbar);
						pb.setVisibility(View.VISIBLE);		 		
						TextView more = (TextView)mFootView.findViewById(R.id.hometab_footer_text);
						more.setText(mContext.getString(R.string.add_more_loading));
						if (mListView.getFooterViewsCount() == 0) {
							mListView.addFooterView(mFootView);	
						}
						getNearyUserData(GlobalParam.LIST_LOAD_MORE);

					}}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
	}


	/*
	 * 获取朋友圈内容
	 */
	private void getNearyUserData(final int loadType){
		if (!WeiYuanCommon.getNetWorkState()) {
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
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread(){
			public void run() {
				try {
					if (loadType == GlobalParam.LIST_LOAD_FIRST) {
						WeiYuanCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG,
								mContext.getResources().getString(R.string.get_dataing));
					}
					boolean isExitsData = true;
					if (mUser!=null && mUser.mPageInfo.currentPage == mUser.mPageInfo.totalPage) {
						isExitsData = false;
					}
					int page = 0;
					if (loadType == GlobalParam.LIST_LOAD_FIRST) {
						page = 1;
					}else if(loadType == GlobalParam.LIST_LOAD_MORE){
						page = mUser.mPageInfo.currentPage+1;
					}
					if (isExitsData) {
						mUser = WeiYuanCommon.getWeiYuanInfo().getNearyUserList(
								WeiYuanCommon.getCurrentLat(mContext), WeiYuanCommon.getCurrentLng(mContext),page);
						List<Login> tempList = new ArrayList<Login>();

						if (mUser != null && mUser.mUserList!=null && mUser.mUserList.size() > 0) {
							isExitsData = true;
							tempList.addAll(mUser.mUserList); 
						} else{
							isExitsData = false;
						}
						WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_CLEAR_LISTENER_DATA,tempList,loadType);
					}


					if (loadType == GlobalParam.LIST_LOAD_FIRST) {
						mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
					}

					switch (loadType) {
					case GlobalParam.LIST_LOAD_FIRST:
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
					if (!isExitsData) {
						mHandler.sendEmptyMessage(GlobalParam.MSG_CHECK_STATE);
					}

				} catch (WeiYuanException e) {
					e.printStackTrace();
					WeiYuanCommon.sendMsg(mBaseHandler,BASE_MSG_NETWORK_ERROR, 
							mContext.getResources().getString(e.getStatusCode()));
				}catch (Exception e) {
					e.printStackTrace();
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
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				}
			};
		}.start();
	}

	/*
	 * 显示listview 数据
	 */
	private void updateListView(){
		if (mDataList == null || mDataList.size() == 0) {
			return;
		}

		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}else{



			// add listview last item
			boolean isLoadMore = (mUser!=null && mUser.mPageInfo!=null && mUser.mPageInfo.hasMore == 1)?true:false;
			if (isLoadMore) {
				if (mFootView == null) {
					mFootView = (LinearLayout) LayoutInflater.from(mContext)
							.inflate(R.layout.hometab_listview_footer, null);
				}
				if (mListView.getFooterViewsCount() == 0) {
					mListView.addFooterView(mFootView);	
				}
			}
			mAdapter = new SearchResultAdapter(mContext,mDataList,false,null,true);
			mListView.setAdapter(mAdapter);
		}
	}


	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			NearyUserListActivity.this.finish();
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

	/*
	 * 处理消息
	 */
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GlobalParam.MSG_CLEAR_LISTENER_DATA:

				if ((msg.arg1 == GlobalParam.LIST_LOAD_FIRST || msg.arg1 == GlobalParam.LIST_LOAD_REFERSH)
						&& mDataList!=null && mDataList.size()>0) {
					mDataList.clear();
					if(mAdapter!=null){
						mAdapter.notifyDataSetChanged();
					}
				}

				List<Login> tempList = (List<Login>)msg.obj;
				if(tempList!=null && tempList.size()>0){
					mDataList.addAll(tempList);
				}
				break;

			case GlobalParam.SHOW_SCROLLREFRESH:
				if (mIsRefreshing) {
					mContainer.onRefreshComplete();
					break;
				}
				mIsRefreshing = true;

				mUser = null;
				getNearyUserData(GlobalParam.LIST_LOAD_REFERSH);
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
			case GlobalParam.HIDE_LOADINGMORE_INDECATOR:

				if (mFootView == null) {
					mFootView = (LinearLayout) LayoutInflater.from(mContext)
							.inflate(R.layout.hometab_listview_footer, null);
				}
				ProgressBar pb = (ProgressBar)mFootView.findViewById(R.id.hometab_addmore_progressbar);
				pb.setVisibility(View.GONE);	
				TextView more = (TextView)mFootView.findViewById(R.id.hometab_footer_text);
				more.setText("");
				if (mAdapter != null){
					mAdapter.notifyDataSetChanged();
				}
				break;

			default:
				break;
			}
		}

	};

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (0<=arg2 && arg2<mDataList.size()) {
			Intent profileDetailIntent = new Intent();
			profileDetailIntent.setClass(mContext, UserInfoActivity.class);
			Login login = mDataList.get(arg2);
			profileDetailIntent.putExtra("type", 1);
			profileDetailIntent.putExtra("user",login);
			startActivity(profileDetailIntent);
		}
	}





}
