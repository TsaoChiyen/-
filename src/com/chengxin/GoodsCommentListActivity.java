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
import android.widget.Toast;

import com.chengxin.R;
import com.chengxin.Entity.GoodsComment;
import com.chengxin.Entity.GoodsCommentEntity;
import com.chengxin.adapter.GoodsCommentAdapter;
import com.chengxin.adapter.MerchantAdapter;
import com.chengxin.global.FeatureFunction;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.net.WeiYuanException;
import com.chengxin.widget.MyPullToRefreshListView;
import com.chengxin.widget.MyPullToRefreshListView.OnChangeStateListener;

/**
 * 商品评论列表
 * @author dongli
 *
 */
public class GoodsCommentListActivity extends BaseActivity implements OnChangeStateListener, OnItemClickListener{

	private MyPullToRefreshListView mContainer;
	private TextView mRefreshViewLastUpdated;
	private LinearLayout mFootView;
	private LinearLayout mCategoryLinear;
	private boolean mIsRefreshing = false;
	private ListView mListView;

	private String mGoodsId;


	private GoodsCommentAdapter mAdapter;
	private GoodsCommentEntity mGoodsCommentEntity;
	private List<GoodsComment> mCommentList = new ArrayList<GoodsComment>();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.goods_comment_list);
		mGoodsId = getIntent().getStringExtra("goods_id");
		initCommpent();
		mContainer.clickrefresh();
	}

	private void initCommpent(){
		setTitleContent(R.drawable.back_btn,0,R.string.goods_comment_list);
		mLeftBtn.setOnClickListener(this);

		mCategoryLinear = (LinearLayout)findViewById(R.id.category_linear);
		mRefreshViewLastUpdated = (TextView) findViewById(R.id.pull_to_refresh_time);
		mContainer = (MyPullToRefreshListView) findViewById(R.id.container);
		mListView = mContainer.getList();
		mListView.setDivider(null);
		mListView.setCacheColorHint(0);
		mListView.setHeaderDividersEnabled(false);

		mListView.setSelector(mContext.getResources().getDrawable(R.drawable.transparent_selector));
		mContainer.setOnChangeStateListener(this);
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:
					int count = mListView.getFooterViewsCount()!=0?(view.getCount()-1):view.getCount();
					if(view.getLastVisiblePosition() == count && (mGoodsCommentEntity !=null 
							&& (mGoodsCommentEntity.pageInfo!=null && mGoodsCommentEntity.pageInfo.hasMore == 1))){
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
		mListView.setOnItemClickListener(this);

	}

	private void getGoodsCommentList(final int loadType){
		if(!WeiYuanCommon.getNetWorkState()){
			stopMsg(loadType);
			mHandler.sendEmptyMessage(GlobalParam.MSG_NETWORK_ERROR);
			return;
		}

		new Thread(){
			public void run() {
				try {
					int page;

					if(mGoodsCommentEntity != null && mGoodsCommentEntity.pageInfo != null
							&& mGoodsCommentEntity.pageInfo.hasMore == 1){
						page = mGoodsCommentEntity.pageInfo.currentPage+1;
					}else{
						page = 1;
					}
					boolean isExitData = false;

					mGoodsCommentEntity =	WeiYuanCommon.getWeiYuanInfo().forGoodsCommentList(mGoodsId,page);
					if(mGoodsCommentEntity.commentList != null && mGoodsCommentEntity.commentList.size()>0){
						isExitData = true;
						List<GoodsComment> tempList = new ArrayList<GoodsComment>();
						tempList.addAll(mGoodsCommentEntity.commentList);
						WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_CLEAR_LISTENER_DATA, tempList,loadType);
					}
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
	public void onClick(View v) {
		super.onClick(v);

		switch (v.getId()) {
		case R.id.left_btn:
			GoodsCommentListActivity.this.finish();
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

	private void updateListView(){
		if(mAdapter != null){
			mAdapter.notifyDataSetChanged();
		}else{
			boolean isLoadMore = (mGoodsCommentEntity!=null && mGoodsCommentEntity.pageInfo!=null && mGoodsCommentEntity.pageInfo.hasMore == 1)?true:false;
			if (isLoadMore) {
				if (mFootView == null) {
					mFootView = (LinearLayout) LayoutInflater.from(mContext)
							.inflate(R.layout.hometab_listview_footer, null);
				}
				if (mListView.getFooterViewsCount() == 0) {
					mListView.addFooterView(mFootView);	
				}
			}

			mAdapter = new GoodsCommentAdapter(mContext, mCommentList);
			mListView.setAdapter(mAdapter);
		}
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GlobalParam.MSG_REMOVE_LISTVIEW_FOOTVIW:
				if (mFootView != null && mListView.getFooterViewsCount()>0) {
					mListView.removeFooterView(mFootView); 
				}
				if (mAdapter != null) {
					mAdapter.notifyDataSetChanged();
				}
				break;

			case GlobalParam.SHOW_SCROLLREFRESH:
				if(mIsRefreshing){
					mIsRefreshing = false;
					mContainer.onRefreshComplete();
				}
				/*
				if(channel_id.equals("0")){
					getCoverNewsList(GlobalParam.LIST_LOAD_REFERSH);
				}else {*/
				if(mGoodsCommentEntity != null){
					mGoodsCommentEntity = null;
				}
				getGoodsCommentList(GlobalParam.LIST_LOAD_REFERSH);
				/*}*/

				break;

			case GlobalParam.HIDE_SCROLLREFRESH:
				mIsRefreshing = false;
				mContainer.onRefreshComplete();
				updateListView();
				break;
			case GlobalParam.MSG_CLEAR_LISTENER_DATA:
				if((msg.arg1 == GlobalParam.LIST_LOAD_FIRST || msg.arg1 == GlobalParam.LIST_LOAD_REFERSH)
						&& mCommentList != null && mCommentList.size()>0){
					mCommentList.clear();
					if(mAdapter!=null){
						mAdapter.notifyDataSetChanged();
					}
				}

				List<GoodsComment> tempList = (List<GoodsComment>)msg.obj;
				if(tempList!=null && tempList.size()>0){
					mCommentList.addAll(tempList);
				}
				break;
			case GlobalParam.MSG_SHOW_LISTVIEW_DATA:
				updateListView();
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
				getGoodsCommentList(GlobalParam.LIST_LOAD_MORE);
				break;

			case GlobalParam.HIDE_LOADINGMORE_INDECATOR:
				if (mFootView != null){
					ProgressBar pbar = (ProgressBar)mFootView.findViewById(R.id.hometab_addmore_progressbar);
					pbar.setVisibility(View.GONE);
					TextView moreView = (TextView)mFootView.findViewById(R.id.hometab_footer_text);
					moreView.setText(R.string.add_more);
				}

				if(mGoodsCommentEntity != null && mGoodsCommentEntity.pageInfo != null && mGoodsCommentEntity.pageInfo.hasMore == 1){
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if(mListView.getHeaderViewsCount() != 0){
			position --;
		}

		if(position >= 0 && position < mCommentList.size()){
			Intent userInfoIntent = new Intent();
			userInfoIntent.setClass(mContext, UserInfoActivity.class);
			userInfoIntent.putExtra("type",2);
			userInfoIntent.putExtra("uid", mCommentList.get(position).uid);
			startActivity(userInfoIntent);
		}else if(position == mCommentList.size() && !(mGoodsCommentEntity!=null && mGoodsCommentEntity.pageInfo != null
				&& mGoodsCommentEntity.pageInfo.hasMore == 1)){
			if(mFootView!=null){
				Message message = new Message();
				message.what = GlobalParam.SHOW_LOADINGMORE_INDECATOR;
				message.obj = mFootView; 
				mHandler.sendMessage(message);
			}
		}
	}

}
