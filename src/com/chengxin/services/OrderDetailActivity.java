package com.chengxin.services;import android.content.Intent;import android.net.Uri;import android.os.Bundle;import android.os.Handler;import android.os.Message;import android.view.View;import android.view.ViewGroup;import android.widget.Button;import android.widget.ImageView;import android.widget.ListAdapter;import android.widget.ListView;import android.widget.TextView;import android.widget.Toast;import com.chengxin.BaseActivity;import com.chengxin.ChatMainActivity;import com.chengxin.R;import com.chengxin.WriteUserInfoActivity;import com.chengxin.Entity.Login;import com.chengxin.Entity.LoginResult;import com.chengxin.Entity.Order;import com.chengxin.Entity.WeiYuanState;import com.chengxin.adapter.OrderGoodsAdapter;import com.chengxin.adapter.OrderShopServiceAdapter;import com.chengxin.adapter.OrderShopServiceAdapter.OnServiceContactListener;import com.chengxin.global.GlobalParam;import com.chengxin.global.GlobleType;import com.chengxin.global.WeiYuanCommon;import com.chengxin.net.WeiYuanException;public class OrderDetailActivity extends BaseActivity {	private Order mOrder;	private TextView mOrderSnText;	private ListView mGoodsList;	private TextView mOrderCusumerName;	private TextView mOrderCosumerPhone;	private TextView mOrderCosumerAddress;	private Button mOrderBtnDeliver;	private Button mOrderBtnRetreate;	private OrderGoodsAdapter mAdapter;	private OrderShopServiceAdapter mServiceAdapter;	private int mPosition = -1;	private boolean mModified = false;	private TextView mTotalCount;	private TextView mTotalPrice;	private ListView mServicesList;	private TextView mShopName;	private TextView mShopPhone;	private TextView mShopAddr;	private ImageView mShopCall;	private ImageView mShopChat;	@Override	protected void onCreate(Bundle savedInstanceState) {		super.onCreate(savedInstanceState);		mContext = this;		setContentView(R.layout.order_detail_activity);		initComponent();		getData();	}	private void initComponent() {		mOrder = (Order)getIntent().getExtras().get("data");		mPosition = getIntent().getIntExtra("position", -1);		setTitleContent(R.drawable.back_btn, 0, R.string.order_detail_title);        mLeftBtn.setOnClickListener(this);	        mOrderSnText = (TextView)findViewById(R.id.order_sn);                mGoodsList = (ListView)findViewById(R.id.goods_list);        mServicesList = (ListView)findViewById(R.id.services_list);                mTotalCount = (TextView)findViewById(R.id.total_count);        mTotalPrice = (TextView)findViewById(R.id.total_price);                mShopName = (TextView)findViewById(R.id.text_shop_name);        mShopPhone = (TextView)findViewById(R.id.text_shop_phone);        mShopAddr = (TextView)findViewById(R.id.text_shop_address);                mShopCall = (ImageView)findViewById(R.id.btn_shop_call);        mShopCall.setOnClickListener(this);                mShopChat = (ImageView)findViewById(R.id.btn_shop_chat);        mShopChat.setOnClickListener(this);                mOrderCusumerName = (TextView)findViewById(R.id.order_cusumer_name);        mOrderCosumerPhone = (TextView)findViewById(R.id.order_cosumer_phone);                mOrderCosumerAddress = (TextView)findViewById(R.id.order_cosumer_address);                mOrderBtnDeliver = (Button)findViewById(R.id.order_btn_deliver);        mOrderBtnRetreate = (Button)findViewById(R.id.order_btn_retreate);        int status = Integer.parseInt(mOrder.status);                if (status == 1) { // 待发货        	mOrderBtnDeliver.setText("提醒发货");        	mOrderBtnDeliver.setTag("1");        } else if (status == 3) { // 未付款        	mOrderBtnDeliver.setText("立即付款");        	mOrderBtnDeliver.setTag("2");        } else if (status == 2) { // 已发货        	mOrderBtnDeliver.setText("确认收货");        	mOrderBtnDeliver.setTag("4");        } else { // 已发货        	mOrderBtnDeliver.setText("查看物流");        	mOrderBtnDeliver.setTag("3");        }                if (status == 2) { // 已发货        	mOrderBtnRetreate.setText("查看物流");        	mOrderBtnRetreate.setTag("3");        } else if (status > 2){        	mOrderBtnRetreate.setVisibility(View.GONE);        }                mOrderBtnDeliver.setOnClickListener(this);        mOrderBtnRetreate.setOnClickListener(this);	}	private void getData() {		if (mOrder != null) {			mOrderSnText.setText(String.format("订单:%s", mOrder.ordersn));			mOrderCusumerName.setText(mOrder.username);			mOrderCosumerPhone.setText(mOrder.phone);			mOrderCosumerAddress.setText(mOrder.address);						mTotalCount.setText(String.format("共计:%d 件商品", mOrder.totalCount));			mTotalPrice.setText(String.format("总计金额: %.2f", mOrder.totalPrice));						if (mOrder.shop != null) {				mShopName.setText(mOrder.shop.name);				mShopPhone.setText(mOrder.shop.phone);				mShopAddr.setText(mOrder.shop.address);			}						if (mOrder.goods != null && mOrder.goods.size() > 0) {				mAdapter = new OrderGoodsAdapter(mContext, mOrder.goods);				mGoodsList.setAdapter(mAdapter);				setListViewHeightBasedOnChildren(mGoodsList);			}			if (mOrder.shop != null) {				if (mOrder.shop.serviceList != null && mOrder.shop.serviceList.size() > 0) {					mServiceAdapter = new OrderShopServiceAdapter(mContext, mOrder.shop.serviceList);					mServicesList.setAdapter(mServiceAdapter);					setListViewHeightBasedOnChildren(mServicesList);										mServiceAdapter.setOnServiceContactListener(new OnServiceContactListener() {						@Override						public void onChatWithUid(String uid) {							beginChatWithUid(uid);						}						@Override						public void onCallPhone(String phone) {							callPhone(phone);						}											});				}			}		}	}	/**	* @param listView	*/	private void setListViewHeightBasedOnChildren(ListView listView) {		ListAdapter listAdapter = listView.getAdapter();				if (listAdapter == null) {			return;		}		  		int totalHeight = 0;				for (int i = 0; i < listAdapter.getCount(); i++) {			View listItem = listAdapter.getView(i, null, listView);			listItem.measure(0, 0);			totalHeight += listItem.getMeasuredHeight();		}		  		ViewGroup.LayoutParams params = listView.getLayoutParams();		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));		listView.setLayoutParams(params);	}	@Override	public void onClick(View v) {		super.onClick(v);				Intent intent = null;				switch (v.getId()) {		case R.id.left_btn:			if (mModified) {				intent = new Intent();				if (intent != null)  {					intent.putExtra("position", mPosition);					setResult(RESULT_OK, intent);				}			}			this.finish();			break;		case R.id.btn_shop_chat:			if (mOrder.shop != null && mOrder.shop.uid != null) {				beginChatWithUid(mOrder.shop.uid);			}			break;		case R.id.btn_shop_call:			if (mOrder.shop != null && mOrder.shop.phone != null) {				callPhone(mOrder.shop.phone);			}			break;		case R.id.order_btn_deliver:			String tag = (String)v.getTag();						if (tag.equals("1")) {				if (mOrder.shop != null && mOrder.shop.uid != null) {					beginChatWithUid(mOrder.shop.uid);				}			} else if (tag.equals("2")) {				submitPayment();			} else if (tag.equals("3")) {				logisticsTrack();			} else if (tag.equals("4")) {				recieveGoods();			}						break;		case R.id.order_btn_retreate:			tag = (String)v.getTag();			if (tag.equals("3")) {				logisticsTrack();			} else {				Intent nickNameIntent = new Intent();				nickNameIntent.setClass(mContext, WriteUserInfoActivity.class);				nickNameIntent.putExtra("content", mOrder.address);				nickNameIntent.putExtra("type", GlobleType.MODIFY_SHOPPING_ADDRESS);				startActivityForResult(nickNameIntent, GlobleType.MODIFY_SHOPPING_ADDRESS);			}			break;		default:			break;		}	}	private void callPhone(String phone) {		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));		if (intent != null) startActivity(intent);	}	private void beginChatWithUid(final String uid) {		if (uid != null && !uid.equals("")) {			if (!WeiYuanCommon.getNetWorkState()) {				mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);				return;			}			new Thread(){				public void run() {					try {						LoginResult user = WeiYuanCommon.getWeiYuanInfo().getUserInfo(uid);						WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_GET_CONTACT_DATA, user);					} catch (WeiYuanException e) {						e.printStackTrace();						WeiYuanCommon.sendMsg(mBaseHandler, BASE_MSG_TIMEOUT_ERROR, 								mContext.getResources().getString(e.getStatusCode()));					} catch (Exception e) {						e.printStackTrace();					}				};			}.start();		}	}	@Override	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		super.onActivityResult(requestCode, resultCode, data);				switch (requestCode) {		case GlobleType.MODIFY_SHOPPING_ADDRESS:			if (resultCode == RESULT_OK) {				String address = data.getStringExtra("shopping_address");				if (address != null && !address.equals("")) {					mOrder.address = address;					commitShoppingAddress(address);				}			}						break;		}	}	private void logisticsTrack() {			}		private void submitPayment() {			}		private void commitShoppingAddress(final String address) {		if (!WeiYuanCommon.getNetWorkState()) {			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);			return;		}		new Thread(){			public void run() {				try {					WeiYuanState state = WeiYuanCommon.getWeiYuanInfo().editShippingAddress(mOrder.id, address);					WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_MODIFY_SHOPPING_ADDRESS, state);				} catch (WeiYuanException e) {					e.printStackTrace();					WeiYuanCommon.sendMsg(mBaseHandler, BASE_MSG_TIMEOUT_ERROR, 							mContext.getResources().getString(e.getStatusCode()));				} catch (Exception e) {					e.printStackTrace();				}			};		}.start();	}	private void recieveGoods() {		if (!WeiYuanCommon.getNetWorkState()) {			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);			return;		}		new Thread(){			public void run() {				try {					WeiYuanCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG, 							mContext.getResources().getString(R.string.get_dataing));					WeiYuanState state = WeiYuanCommon.getWeiYuanInfo().recieveGoodsByOrderId(mOrder.id);					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);					WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_CHECK_STATE, state);				} catch (WeiYuanException e) {					e.printStackTrace();					WeiYuanCommon.sendMsg(mBaseHandler, BASE_MSG_TIMEOUT_ERROR, 							mContext.getResources().getString(e.getStatusCode()));				} catch (Exception e) {					e.printStackTrace();					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);				}			};		}.start();	}	private Handler mHandler = new Handler() {				@Override		public void handleMessage(Message msg) {			super.handleMessage(msg);			switch (msg.what) {			case GlobalParam.MSG_CHECK_STATE:				WeiYuanState state = (WeiYuanState)msg.obj;								if(state == null){					Toast.makeText(mContext, R.string.commit_data_error,Toast.LENGTH_LONG).show();					return;				}								if(state.code != 0){					Toast.makeText(mContext, state.errorMsg,Toast.LENGTH_LONG).show();				}				break;			case GlobalParam.MSG_GET_CONTACT_DATA:				LoginResult login = (LoginResult)msg.obj;								if(login == null){					Toast.makeText(mContext, R.string.commit_data_error,Toast.LENGTH_LONG).show();					return;				}								if(login.mState.code == 0){					Login user = login.mLogin;										if(user != null){						Intent intent = new Intent(mContext, ChatMainActivity.class);						user.mIsRoom = 100;						intent.putExtra("data", user);						startActivity(intent);					}				} else {					Toast.makeText(mContext, login.mState.errorMsg,Toast.LENGTH_LONG).show();				}				break;						case GlobalParam.MSG_MODIFY_SHOPPING_ADDRESS:				state = (WeiYuanState)msg.obj;								if(state == null){					Toast.makeText(mContext, R.string.commit_data_error,Toast.LENGTH_LONG).show();					return;				}								if(state.code == 0) {					mOrderCosumerAddress.setText(mOrder.address);				} else {					Toast.makeText(mContext, state.errorMsg,Toast.LENGTH_LONG).show();				}			default:				break;			}		}	};}