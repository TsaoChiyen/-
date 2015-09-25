package com.chengxin.services;import java.util.ArrayList;import java.util.List;import android.content.Intent;import android.os.Bundle;import android.os.Handler;import android.os.Message;import android.util.Log;import android.view.Gravity;import android.view.View;import android.widget.AdapterView;import android.widget.AdapterView.OnItemClickListener;import android.widget.ListView;import android.widget.RelativeLayout;import android.widget.Toast;import com.chengxin.BaseActivity;import com.chengxin.R;import com.chengxin.Entity.Order;import com.chengxin.Entity.OrderList;import com.chengxin.Entity.PopItem;import com.chengxin.Entity.StatusMenu;import com.chengxin.Entity.WeiYuanState;import com.chengxin.adapter.OrderAdapter;import com.chengxin.global.WeiYuanCommon;import com.chengxin.net.WeiYuanException;import com.chengxin.widget.PopWindows;import com.chengxin.widget.PopWindows.PopWindowsInterface;public class OrderActivity extends BaseActivity implements OnItemClickListener {	protected static final int MSG_ORDER_LIST_DONE = 0x9501;	private ListView mListView;	protected int mPage;	protected int mStatus;	private List< Order > mOrderList = new ArrayList< Order >();	private	List< Order > mDataList = new ArrayList< Order >();	private OrderAdapter mAdapter;	private List<PopItem> mStatusMenuList;	private PopWindows mPopWindows;	private RelativeLayout mTitleLayout;	@Override	protected void onCreate(Bundle savedInstanceState) {		super.onCreate(savedInstanceState);		mContext = this;		setContentView(R.layout.services_require_activity);		initComponent();		getData();	}	private void initComponent() {		setTitleContent(R.drawable.back_btn, false, false, true, R.string.order_main_title);		mLeftBtn.setOnClickListener(this);		mMoreBtn.setOnClickListener(this);				mTitleLayout = (RelativeLayout) findViewById(R.id.title_layout);				mListView = (ListView)findViewById(R.id.list);		mListView.setOnItemClickListener(this);		        mStatusMenuList = (List<PopItem>) StatusMenu.getPopMenuList();        mPopWindows = new PopWindows(mContext, mStatusMenuList, mTitleLayout, new PopWindowsInterface() {			@Override			public void onItemClick(int dataId, int position, View view) {				mStatus = dataId;				Log.i("DataID", String.valueOf(dataId));				showData();			}        });	}	private void getData() {		if (!WeiYuanCommon.getNetWorkState()) {			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);			return;		}		new Thread(){			public void run() {				try {					WeiYuanCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG, 							mContext.getResources().getString(R.string.get_dataing));										OrderList tempList = WeiYuanCommon.getWeiYuanInfo().getOrderList(mPage, mStatus, 2);					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);					WeiYuanCommon.sendMsg(mHandler, MSG_ORDER_LIST_DONE, tempList);				} catch (WeiYuanException e) {					e.printStackTrace();					WeiYuanCommon.sendMsg(mBaseHandler, BASE_MSG_TIMEOUT_ERROR, 							mContext.getResources().getString(e.getStatusCode()));				} catch (Exception e) {					e.printStackTrace();					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);				}			};		}.start();	}	protected Handler mHandler = new Handler() {		@Override		public void handleMessage(Message msg) {			super.handleMessage(msg);			switch (msg.what) {			case MSG_ORDER_LIST_DONE:				OrderList list = (OrderList) msg.obj;								if (list != null) {					mOrderList.clear();					WeiYuanState state = list.mState;					if(state == null){						Toast.makeText(mContext, R.string.commit_data_error,Toast.LENGTH_LONG).show();						return;					}										if(state.code == 0){						if (list.mOrderList != null && list.mOrderList.size() > 0) {							mOrderList.addAll(list.mOrderList);						}						showData();					} else {						Toast.makeText(mContext, state.errorMsg,Toast.LENGTH_LONG).show();					}				}				break;							default:				break;			}		}	};	@Override	public void onClick(View v) {		super.onClick(v);			switch (v.getId()) {		case R.id.left_btn:			this.finish();			break;		case R.id.more_btn:			mPopWindows.showGroupPopView(mStatusMenuList,					Gravity.RIGHT,					R.drawable.no_top_arrow_bg,					R.color.white,					0);			break;		default:			break;		}	}	protected void showData() {		mDataList.clear();				if (mOrderList.size() > 0) {			if (mStatus == 0) {				mDataList.addAll(mOrderList);			} else {				for (int i = 0; i < mOrderList.size(); i++) {					Order item = mOrderList.get(i);										if (item.status != null && Integer.parseInt(item.status) == mStatus) {						mDataList.add(item);					}				}			}		}				if (mAdapter == null) {			mAdapter = new OrderAdapter(mContext, mDataList);			mListView.setAdapter(mAdapter);		} else {			mAdapter.notifyDataSetChanged();		}	}	@Override	public void onItemClick(AdapterView<?> listView, View convertView, int position, long id) {		Order order = mDataList.get(position);				Intent intent = new Intent();		intent.setClass(mContext, OrderDetailActivity.class);		intent.putExtra("data", order);		startActivity(intent);	}}