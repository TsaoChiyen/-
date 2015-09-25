package com.chengxin.financial;import java.util.ArrayList;import java.util.List;import android.content.Intent;import android.os.Bundle;import android.os.Handler;import android.os.Message;import android.view.Gravity;import android.view.LayoutInflater;import android.view.View;import android.widget.Button;import android.widget.LinearLayout;import android.widget.ListView;import android.widget.RelativeLayout;import android.widget.TextView;import android.widget.Toast;import com.chengxin.BaseActivity;import com.chengxin.ChatMainActivity;import com.chengxin.LocationActivity;import com.chengxin.R;import com.chengxin.Entity.Financier;import com.chengxin.Entity.FinancingGoods;import com.chengxin.Entity.FinancingGoodsList;import com.chengxin.Entity.Login;import com.chengxin.Entity.LoginResult;import com.chengxin.Entity.MapInfo;import com.chengxin.Entity.PopItem;import com.chengxin.Entity.WeiYuanState;import com.chengxin.adapter.FinancingGoodsAdapter;import com.chengxin.adapter.FinancingGoodsAdapter.OnFinancingGoodsClickListener;import com.chengxin.dialog.CPAlert;import com.chengxin.dialog.CPAlert.OnAlertOkSelectId;import com.chengxin.global.GlobalParam;import com.chengxin.global.GlobleType;import com.chengxin.global.WeiYuanCommon;import com.chengxin.map.BMapApiApp;import com.chengxin.net.WeiYuanException;import com.chengxin.widget.PopWindows;import com.chengxin.widget.PopWindows.PopWindowsInterface;public class FinancialMainActivity extends BaseActivity implements OnFinancingGoodsClickListener {		protected static final int MSG_GOODS_LIST = 0x7947;	protected static final int MSG_FINANCIER_ADDED = 0x7948;	protected static final int MSG_FINANCIER_EDITED = 0x7949;	private static final int REQUEST_FINANCIER_INFO = 0x7950;	protected static final int REQUEST_PRODUCT_INFO = 0x7951;	private static final int RESQUEST_CHOOSE_CITY = 0x8001;		private LinearLayout mListFooter;	private ListView mListView;	private TextView mFooterText;	private List < FinancingGoods > mList = new ArrayList< FinancingGoods >();	private FinancingGoodsAdapter mAdapter;		protected int mPosition = -1;	private List<PopItem> mPopList = new ArrayList<PopItem>();	private PopWindows mPopWindows;	private RelativeLayout mTitleLayout;		Financier mFinancier = null;	private Button mBtnLocate;	private Button mBtnChoose;	private int mCurrentType = 0;	protected String mCurrentCity = null;	@Override	protected void onCreate(Bundle savedInstanceState) {		super.onCreate(savedInstanceState);		mContext = this;		setContentView(R.layout.activity_financing_main);				if(BMapApiApp.getContryList() == null				||BMapApiApp.getContryList().mCountryList == null				|| BMapApiApp.getContryList().mCountryList.size()<=0){			try {				BMapApiApp.setContryList(WeiYuanCommon.getWeiYuanInfo().getCityAndContryUser());			} catch (WeiYuanException e) {				e.printStackTrace();			}		}		Login user = WeiYuanCommon.getLoginResult(mContext);				if (user != null) {			mFinancier = user.financier;		}		initComponent();		getData();	}	private void initComponent() {		setTitleContent(R.drawable.back_btn, false, false, true, R.string.financing_main_title);		mLeftBtn.setOnClickListener(this);		mMoreBtn.setOnClickListener(this);				mTitleLayout = (RelativeLayout) findViewById(R.id.title_layout);		mListFooter = (LinearLayout)LayoutInflater.from(mContext).inflate(R.layout.services_require_footer, null);		mFooterText = (TextView)mListFooter.findViewById(R.id.sumary);		mListView = (ListView)findViewById(R.id.list);		mListView.addFooterView(mListFooter);				mBtnLocate = (Button)findViewById(R.id.btn_locate);		mBtnChoose = (Button)findViewById(R.id.btn_choose);		mBtnLocate.setOnClickListener(this);		mBtnChoose.setOnClickListener(this);			    Login user = WeiYuanCommon.getLoginResult(mContext);	    int status = 0;	    	    if (user != null) {	    	status = user.isfinanc;	    }	    	    if (status == 0) {		    mPopList.add(new PopItem(0, "申请入驻"));	    } else if (status == 2) {		    mPopList.add(new PopItem(1, "审核中..."));	    } else {		    mPopList.add(new PopItem(2, "基本信息管理"));			mPopList.add(new PopItem(3, "服务产品管理"));	    }		mPopWindows = new PopWindows(mContext, mPopList, mTitleLayout, new PopWindowsInterface() {			@Override			public void onItemClick(int dataId, int position, View view) {				switch (dataId) {				case 0:	//申请入驻					Intent intent = new Intent();					intent.setClass(mContext, FinancierInfoActivity.class);					startActivityForResult(intent, REQUEST_FINANCIER_INFO);					break;				case 1:	//审核中...					Toast.makeText(mContext, "你的申请正在审核中，敬请期待", Toast.LENGTH_LONG).show();					break;				case 2:	//基本信息管理					intent = new Intent();					intent.setClass(mContext, FinancierInfoActivity.class);					intent.putExtra("data", mFinancier);					startActivityForResult(intent, REQUEST_FINANCIER_INFO);					break;				case 3:	//服务产品管理					intent = new Intent();					intent.setClass(mContext, FinancingProductActivity.class);					intent.putExtra("financier", mFinancier);					startActivityForResult(intent, REQUEST_PRODUCT_INFO);					break;				default:					break;				}			}		});	}	private void getData() {		if (!WeiYuanCommon.getNetWorkState()) {			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);			return;		}		new Thread(){			public void run() {				try {					WeiYuanCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG, 							mContext.getResources().getString(R.string.get_dataing));										FinancingGoodsList tempList = null;										switch ((int)mCurrentType) {					case 0:						tempList = WeiYuanCommon.getWeiYuanInfo().getFinancingGoodsList(								null,								0,								0);						break;					case 1:						tempList = WeiYuanCommon.getWeiYuanInfo().getFinancingGoodsList(								null,								Double.valueOf(mMapInfo.getLat()),								Double.valueOf(mMapInfo.getLng()));						break;					case 2:						tempList = WeiYuanCommon.getWeiYuanInfo().getFinancingGoodsList(								mCurrentCity ,								0,								0);						break;					}					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);					WeiYuanCommon.sendMsg(mHandler, MSG_GOODS_LIST, tempList);				} catch (WeiYuanException e) {					e.printStackTrace();					WeiYuanCommon.sendMsg(mBaseHandler, BASE_MSG_TIMEOUT_ERROR, 							mContext.getResources().getString(e.getStatusCode()));				} catch (Exception e) {					e.printStackTrace();					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);				}			};		}.start();	}	protected Handler mHandler = new Handler() {		@Override		public void handleMessage(Message msg) {			super.handleMessage(msg);			switch (msg.what) {			case GlobalParam.MSG_GET_CONTACT_DATA:				LoginResult login = (LoginResult)msg.obj;								if(login == null){					Toast.makeText(mContext, R.string.commit_data_error,Toast.LENGTH_LONG).show();					return;				}								if(login.mState.code == 0){					Login user = login.mLogin;										if(user != null){						Intent intent = new Intent(mContext, ChatMainActivity.class);						user.mIsRoom = 100;						intent.putExtra("data", user);						startActivity(intent);					}				} else {					Toast.makeText(mContext, login.mState.errorMsg,Toast.LENGTH_LONG).show();				}				break;						case MSG_FINANCIER_ADDED:				Financier financier = (Financier) msg.obj;								if (financier != null) {					WeiYuanState state = financier.state;					if(state == null){						Toast.makeText(mContext, R.string.commit_data_error,Toast.LENGTH_LONG).show();						return;					}										if(state.code == 0){						Login user = WeiYuanCommon.getLoginResult(mContext);												if (user != null) {							user.financier = financier;							user.isfinanc = 2;							WeiYuanCommon.saveLoginResult(mContext, user);						}					} else {						Toast.makeText(mContext, state.errorMsg,Toast.LENGTH_LONG).show();					}				}				break;							case MSG_FINANCIER_EDITED:				financier = (Financier) msg.obj;								if (financier != null) {					WeiYuanState state = financier.state;					if(state == null){						Toast.makeText(mContext, R.string.commit_data_error,Toast.LENGTH_LONG).show();						return;					}										if(state.code == 0){						updateLoginWithFinancier(financier);					} else {						Toast.makeText(mContext, state.errorMsg,Toast.LENGTH_LONG).show();					}				}				break;							case MSG_GOODS_LIST:				FinancingGoodsList list = (FinancingGoodsList) msg.obj;								if (list != null) {					mList.clear();					WeiYuanState state = list.mState;					if(state == null){						Toast.makeText(mContext, R.string.commit_data_error,Toast.LENGTH_LONG).show();						return;					}										if(state.code == 0){						if (list.mList != null && list.mList.size() > 0) {							mList.addAll(list.mList);						}						showData();					} else {						Toast.makeText(mContext, state.errorMsg,Toast.LENGTH_LONG).show();					}				}				break;							default:				break;			}		}	};	private MapInfo mMapInfo;	@Override	public void onClick(View v) {		super.onClick(v);		switch (v.getId()) {		case R.id.left_btn:			this.finish();			break;		case R.id.more_btn:			mPopWindows.showGroupPopView(mPopList,					Gravity.RIGHT,					R.drawable.no_top_arrow_bg,					R.color.white,					0);						break;					case R.id.btn_locate:			Intent intent = new Intent(this, LocationActivity.class);			startActivityForResult(intent, GlobleType.RESQUEST_MAP_LOACTION);			break;					case R.id.btn_choose:			CPAlert.showAlert(					mContext,					"选择城市", 					BMapApiApp.getContryList(),					new OnAlertOkSelectId() {												@Override						public void onOkClick(int whichButton, String state, String city) {							if (city != null ) {								if (city.equals(" - ")) {									mCurrentCity = state;								} else {									mCurrentCity = city;								}							} else if (state != null) {								mCurrentCity = state;							}							mCurrentType = 2;							getData();						}					});			break;		default:			break;		}	}		protected void showData() {		if (mAdapter == null) {			mAdapter = new FinancingGoodsAdapter(mContext, mList);			mAdapter.setOnFinancingGoodsClickListener(this);			mListView.setAdapter(mAdapter);		} else {			mAdapter.notifyDataSetChanged();;		}				showFooter();	}	private void showFooter() {		if (mList.size() == 0) {			mListFooter.setVisibility(View.GONE);		} else {			mListFooter.setVisibility(View.VISIBLE);			mFooterText.setText(String.format("%d项融资产品", mList.size()));		}	}	@Override	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		super.onActivityResult(requestCode, resultCode, data);		switch (requestCode) {		case RESQUEST_CHOOSE_CITY:			if(data != null && RESULT_OK == resultCode){				Bundle bundle = data.getExtras();				if(bundle != null){					mCurrentCity = data.getStringExtra("city");//					String mAddr = data.getStringExtra("addr");//					String mProvice = data.getStringExtra("provice");//					String mCity = data.getStringExtra("city");					mCurrentType = 2;					getData();				}			}			break;					case GlobleType.RESQUEST_MAP_LOACTION:			if(data != null && RESULT_OK == resultCode){				Bundle bundle = data.getExtras();				if(bundle != null){					mMapInfo = (MapInfo)data.getSerializableExtra("mapInfo");										if(mMapInfo == null){						Toast.makeText(mContext, mContext.getString(R.string.get_location_failed), Toast.LENGTH_SHORT).show();						return;					}					mCurrentType = 1;					getData();				}			}			break;		case REQUEST_FINANCIER_INFO:			if (resultCode == RESULT_OK) {				if (data != null) {					if (data.hasExtra("data")) {						Financier financier = (Financier) data.getExtras().get("data");												if (financier != null) {							updateFinancier(financier);						}					}				}			}			break;		case REQUEST_PRODUCT_INFO:			if (resultCode == RESULT_OK) {				getData();			}		default:			break;		}	}	private void updateFinancier(final Financier financier) {		if (!WeiYuanCommon.getNetWorkState()) {			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);			return;		}		new Thread(){			public void run() {				try {					if (financier.id == 0) {						Financier temp = WeiYuanCommon.getWeiYuanInfo().applyFinacier(								financier.company,								financier.address,								financier.city,								financier.lat,								financier.lng,								financier.workPaper,								financier.idcard,								financier.certificate);												WeiYuanCommon.sendMsg(mHandler, MSG_FINANCIER_ADDED, temp);					} else {						Financier temp = WeiYuanCommon.getWeiYuanInfo().editFinacier(								financier.id,								financier.company,								financier.address,								financier.city,								financier.lat,								financier.lng,								financier.workPaper,								financier.idcard,								financier.certificate);						WeiYuanCommon.sendMsg(mHandler, MSG_FINANCIER_EDITED, temp);					}									} catch (WeiYuanException e) {					e.printStackTrace();					WeiYuanCommon.sendMsg(mBaseHandler, BASE_MSG_TIMEOUT_ERROR, 							mContext.getResources().getString(e.getStatusCode()));				} catch (Exception e) {					e.printStackTrace();				}			};		}.start();			}	protected void updateLoginWithFinancier(Financier financier) {		Login user = WeiYuanCommon.getLoginResult(mContext);				if (user != null) {			if (financier.company != null && !financier.company.isEmpty()) {				user.financier.company = financier.company;			}						if (financier.address != null && !financier.address.isEmpty()) {				user.financier.address = financier.address;			}			if (financier.city != null && !financier.city.isEmpty()) {				user.financier.city = financier.city;			}						if (financier.workPaper != null && !financier.workPaper.isEmpty()) {				user.financier.workPaper = financier.workPaper;			}						if (financier.idcard != null && !financier.idcard.isEmpty()) {				user.financier.idcard = financier.idcard;			}						if (financier.certificate != null && !financier.certificate.isEmpty()) {				user.financier.certificate = financier.certificate;			}						WeiYuanCommon.saveLoginResult(mContext, user);		}	}	@Override	public void onClick(View v, int Position, int uid) {		beginChatWithUid(String.valueOf(uid));	}	private void beginChatWithUid(final String uid) {		if (uid != null && !uid.equals("")) {			if (!WeiYuanCommon.getNetWorkState()) {				mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);				return;			}			new Thread(){				public void run() {					try {						LoginResult user = WeiYuanCommon.getWeiYuanInfo().getUserInfo(uid);						WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_GET_CONTACT_DATA, user);					} catch (WeiYuanException e) {						e.printStackTrace();						WeiYuanCommon.sendMsg(mBaseHandler, BASE_MSG_TIMEOUT_ERROR, 								mContext.getResources().getString(e.getStatusCode()));					} catch (Exception e) {						e.printStackTrace();					}				};			}.start();		}	}}