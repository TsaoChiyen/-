package com.chengxin;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.chengxin.R;
import com.chengxin.Entity.MapInfo;
import com.chengxin.Entity.MessageInfo;
import com.chengxin.Entity.MessageType;
import com.chengxin.Entity.MovingLoaction;
import com.chengxin.Entity.WeiYuanState;
import com.chengxin.dialog.MMAlert;
import com.chengxin.dialog.MMAlert.OnAlertSelectId;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.map.BMapApiApp;
import com.chengxin.net.WeiYuanException;

/*
 * 定位页面
 */
public class LocationActivity extends BaseActivity implements OnClickListener, OnTouchListener  {
	
	/*
	 * 定义全局变量
	 */
	private MapView mMapView;
	private MKSearch mSearch;

	private Timer mSearchTimer;
	private TimerTask mSearchTimerTask;

	private MapController mMapController;
	private int mZoomLevel = 20;
	private BMapApiApp mApp;


	private boolean mIsFirstInit = false;
	private DisplayMetrics mDMetrics = new DisplayMetrics();

	private double mLng ;
	private double mLat;

	boolean mIsRegister = false;

	private MapInfo mMapInfo;

	private LocationClient mLocClient;
	public MyLocationListenner mMyListener = new MyLocationListenner();
	private Timer mTimer;
	private TimerTask mTimerTask;
	public final  static int MSG_LOCATION_ERROR=0x00015;
	public final  static int MSG_SHOW_NEARY_LOCATION=0x00050;
	public final  static int MSG_SHOW_NEARY_MAP=0x00041;

	boolean mIsShow = true;
	boolean mIsSetting = true;

	private String mCurrentName="";
	private String mCurrentAddr="";
	private String mCurrentTempLat ;
	private String mCurrentTempLng;
	private double mCurrentLat;
	private double mCurrentLng;
	private String mSendAddr;


	private LinearLayout mNearyAddrLayout;
	private TextView mCurrentText;
	private CheckBox mCheckCurrentAddr;
	private Button mAppointer;
	private ScrollView mScrollView;


	private SearchLocationDialog mSearchLocationDialog;
	private List<MKPoiInfo> mSearchPoinInfoList = new ArrayList<MKPoiInfo>();

	private String mFuid,mGroupId;;

	private MyOverlay mOverlay;


	/*
	 * 导入控件
	 * (non-Javadoc)
	 * @see com.weiyuan.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_layout);
		mContext = this;

		mFuid = getIntent().getStringExtra("fuid");
		mGroupId = getIntent().getStringExtra("groupid");

		double lng = getIntent().getDoubleExtra("lng", WeiYuanCommon.getCurrentLng(mContext));
		double lat = getIntent().getDoubleExtra("lat", WeiYuanCommon.getCurrentLat(mContext));
		mSendAddr = getIntent().getStringExtra("addr");
		if (lng != 0) {
			mLng = lng;
		};

		if (lat != 0) {
			mLat = lat;
		};

		mIsShow = getIntent().getBooleanExtra("show", false);

		registerReceiver();

		initComponent();

		if (!mIsShow) {
			getLocation();
		}
	}

	/*
	 * 注册通知
	 */
	private void registerReceiver(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(GlobalParam.SWITCH_LANGUAGE_ACTION);
		registerReceiver(mReceiver, filter);
	}

	/*
	 * 处理通知
	 */
	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent){
			if(intent != null){
				if(intent.getAction().equals(GlobalParam.SWITCH_LANGUAGE_ACTION)){
				}
			}
		}
	};

	@Override
	protected void onStop() {
		super.onStop();
		WeiYuanCommon.appOnStop(mContext);
	}

	/*
	 * 实例化控件
	 */
	private void initComponent(){
		mScrollView = (ScrollView)findViewById(R.id.location_layout);
		if(!mIsShow){
			setTitleContent(R.drawable.back_btn,true, R.drawable.send_map_btn, R.string.location);
			mScrollView.setVisibility(View.VISIBLE);
			mRightBtn.setOnClickListener(this);
			mSearchBtn.setOnClickListener(this);
			mLeftBtn.setOnClickListener(this);
		}else{
			setTitleContent(R.drawable.back_btn,false,false,true,R.string.map);
			mMoreBtn.setOnClickListener(this);
			mLeftBtn.setOnClickListener(this);
		}

		mAppointer = (Button)findViewById(R.id.mappointer);

		mCurrentText = (TextView)findViewById(R.id.current_addr);
		mCheckCurrentAddr = (CheckBox)findViewById(R.id.check_current);
		mCheckCurrentAddr.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked && mIsSetting){
					if(mNearyAddrLayout!=null && mNearyAddrLayout.getChildCount()>0){
						for (int i = 0; i < mNearyAddrLayout.getChildCount(); i++) {
							for (int j = 0; j < mNearyAddrLayout.getChildCount(); j++) {
								View view = mNearyAddrLayout.getChildAt(j);
								CheckBox checkBox = (CheckBox) view.findViewById(R.id.checklocation);
								checkBox.setChecked(false);
							}
						}
					}
					mLng = mCurrentLng;
					mLat = mCurrentLat;
					mMapInfo = new MapInfo(mCurrentName,mCurrentAddr,mCurrentTempLat,mCurrentTempLng);
					GeoPoint point = new GeoPoint((int)(mLat* 1e6), (int)(mLng* 1e6));
					
					mMapController.setCenter(point);

				}
			}
		});
		mNearyAddrLayout = (LinearLayout)findViewById(R.id.neary_location_item_layout);


		getWindowManager().getDefaultDisplay().getMetrics(mDMetrics);

		mApp = (BMapApiApp)this.getApplication();

		if (mApp.mBMapManager == null) {
			mApp.mBMapManager = new BMapManager(getApplication());
			mApp.mBMapManager.init(mApp.strKey, new BMapApiApp.MyGeneralListener(){
				@Override
				public void onGetNetworkState(int iError) {
					super.onGetNetworkState(iError);
					if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
						if (mSearchTimer != null) {
							mSearchTimer.cancel();
							mSearchTimer.purge();
							mSearchTimer = null;
						}
						Log.e("onGetNetworkState", "onGetNetworkState");
						/*Toast.makeText(BMapApiApp.getInstance().getApplicationContext(), "您的网络出错啦！",
			                    Toast.LENGTH_LONG).show();*/
					} else if (iError == MKEvent.ERROR_NETWORK_DATA) {
						if (mSearchTimer != null) {
							mSearchTimer.cancel();
							mSearchTimer.purge();
							mSearchTimer = null;
						}
						/*Toast.makeText(BMapApiApp.getInstance().getApplicationContext(), "输入正确的检索条件！",
			                        Toast.LENGTH_LONG).show();*/
					}
				}

				@Override
				public void onGetPermissionState(int iError) {
					super.onGetPermissionState(iError);
					if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
						Toast.makeText(BMapApiApp.getInstance().getApplicationContext(), 
								"请在 BMapApiApp.java文件输入正确的授权Key！", Toast.LENGTH_LONG).show();
					}
				}

			});
		}

		mApp.mBMapManager.start();

		mMapView = (MapView) findViewById(R.id.bmapView);
		mMapView.setOnTouchListener(this);
		mMapView.setBuiltInZoomControls(true);
		mMapView.setTraffic(true);
		
		if(mIsShow){
			addItemForMap();
			mAppointer.setVisibility(View.GONE);
		}else{
			mAppointer.setVisibility(View.VISIBLE);
			//设置在缩放动画过程中也显示overlay,默认为不绘制
		}
		GeoPoint point = new GeoPoint((int)(mLat* 1e6), (int)(mLng* 1e6));
		mMapController = mMapView.getController();
		mMapController.setZoom(mZoomLevel);
		mMapController.setCenter(point);

		mMapView.regMapViewListener(mApp.mBMapManager, mMapViewListener);  //注册监听  
		mSearch = new MKSearch();

		mSearch.init(mApp.mBMapManager, mMySearchListener);

		if (mSearchTimer != null) {
			mSearchTimer.cancel();
			mSearchTimer.purge();
			mSearchTimer = null;
		}

		mSearchTimer = new Timer();
	}

	/*
	 * 处理消息
	 */
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GlobalParam.SHOW_PROGRESS_DIALOG:
				String hintMsg = (String)msg.obj;
				if (hintMsg!=null && !hintMsg.equals("")) {
					showProgressDialog(hintMsg);
				}
				break;
			case GlobalParam.HIDE_PROGRESS_DIALOG:
				hideProgressDialog();
				break;
			case MSG_LOCATION_ERROR:
				Toast.makeText(mContext, R.string.location_error, Toast.LENGTH_LONG).show();

				if (mLocClient != null) {
					mLocClient.stop();
				}
				
				hideProgressDialog();
				break;
			case MSG_SHOW_NEARY_LOCATION:
				List<MKPoiInfo> poiInfoList = (List<MKPoiInfo>)msg.obj;
				mCurrentText.setText(mCurrentAddr);
				showNearyAddr(poiInfoList);
				break;
			case MSG_SHOW_NEARY_MAP:
				break;
			case GlobalParam.MSG_CHECK_FAVORITE_STATUS:
				WeiYuanState favoriteResult = (WeiYuanState)msg.obj;
				if(favoriteResult == null){
					Toast.makeText(mContext, R.string.commit_dataing, Toast.LENGTH_LONG).show();
					return;
				}
				if(favoriteResult.code!=0){
					Toast.makeText(mContext, favoriteResult.errorMsg, Toast.LENGTH_LONG).show();
					return;
				}
				break;

			default:
				break;
			}
		}
	};


	/*
	 * 显示附近的地址
	 */
	private void showNearyAddr(final List<MKPoiInfo> list){

		if(mNearyAddrLayout!=null && mNearyAddrLayout.getChildCount()!=0){
			mNearyAddrLayout.removeAllViews();
		}
		if(list == null || list.size()<=0){
			return;
		}
		LayoutInflater flater = (LayoutInflater) getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
		for (int i = 0; i < list.size(); i++) {
			View view = flater.inflate(R.layout.neary_location_item,null);
			TextView title = (TextView)view.findViewById(R.id.location_text);
			TextView addr = (TextView)view.findViewById(R.id.location_addr);
			addr.setVisibility(View.VISIBLE);
			final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checklocation);
			checkBox.setVisibility(View.VISIBLE);
			/*	if(i == mSelectItemId){
				checkBox.setChecked(true);
			}else{
				checkBox.setChecked(false);
			}*/

			title.setText(list.get(i).name);
			addr.setText(list.get(i).address);
			final int pos = i;
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					for (int j = 0; j < mNearyAddrLayout.getChildCount(); j++) {
						if(j !=pos){
							View view = mNearyAddrLayout.getChildAt(j);
							CheckBox checkBox = (CheckBox) view.findViewById(R.id.checklocation);
							checkBox.setChecked(false);
						}
					}
					mIsSetting = true;
					mCheckCurrentAddr.setChecked(false);
					checkBox.setChecked(true);
					double tempLat = Double.parseDouble(String.valueOf(list.get(pos).pt.getLatitudeE6()))/1e6;
					double tempLng =  Double.parseDouble(String.valueOf(list.get(pos).pt.getLongitudeE6()))/1e6;
					mMapInfo = new MapInfo(list.get(pos).name,list.get(pos).address, String.valueOf(tempLat), String.valueOf(tempLng));
					mLng = tempLng;
					mLat = tempLat;
					GeoPoint point = new GeoPoint((int)(mLat* 1e6), (int)(mLng* 1e6));
					/*mMapController = mMapView.getController();
					mMapController.setZoom(mZoomLevel);*/
					mMapController.setCenter(point);
					/*mSelectItemId = pos;*/
				}
			});
			mNearyAddrLayout.addView(view);
		}
	}

	/*
	 * 获取当前位置信息
	 */
	private void getLocation(){
		mLocClient = new LocationClient(getApplicationContext());
		mLocClient.registerLocationListener(mMyListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);//打开gps
		option.setCoorType("bd09ll");     //设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();

		if (mTimer != null) {
			mTimer.cancel();
			mTimer.purge();
			mTimer = null;
		}

		mTimer = new Timer();
		mTimerTask = new TimerTask() {
			@Override
			public void run() {
				mTimer.cancel();
				mTimer.purge();
				mTimer = null;
				mHandler.sendEmptyMessage(MSG_LOCATION_ERROR);
			}
		};
		mTimer.schedule(mTimerTask, 60000, 1000);

		Message message = new Message();
		message.what = GlobalParam.SHOW_PROGRESS_DIALOG;
		message.obj = mContext.getResources().getString(R.string.location_doing);
		mHandler.sendMessage(message);

	}

	/*
	 * 初始化搜索事件
	 */
	private MKSearchListener mMySearchListener = new MKSearchListener() {
		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
		}

		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {

		}

		@Override
		public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
			if(arg0!=null && arg0.getAllPoi()!=null){
				if(mSearchPoinInfoList!=null && mSearchPoinInfoList.size()>0){
					mSearchPoinInfoList.clear();
				}
				mSearchPoinInfoList.addAll(arg0.getAllPoi());
				if(mSearchLocationDialog!=null){
					mSearchLocationDialog.updateListView();
				}
				/*if(arg0.g){

				}*/
			}
		}

		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult arg0,
				int arg1) {

		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
		}

		@Override
		public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
			if (mSearchTimer != null) {
				mSearchTimer.cancel();
				mSearchTimer.purge();
				mSearchTimer = null;
			}
			if(arg0 != null){
				List<MKPoiInfo> poiList = arg0.poiList;
				if(arg0.strAddr != null && !arg0.strAddr.equals("")){
					mIsSetting = false;
					mCheckCurrentAddr.setChecked(true);
					String cityName = arg0.addressComponents.city;
					mCurrentName = cityName;
					mCurrentAddr = arg0.strAddr;
					double tempLat = Double.parseDouble(String.valueOf(arg0.geoPt.getLatitudeE6()))/1e6;
					double tempLng =  Double.parseDouble(String.valueOf(arg0.geoPt.getLongitudeE6()))/1e6;
					mCurrentLat = tempLat;
					mCurrentLng = tempLng;
					mCurrentTempLat = String.valueOf(tempLat);
					mCurrentTempLng =  String.valueOf(tempLng);
					mMapInfo = new MapInfo(cityName,arg0.strAddr,mCurrentTempLat ,mCurrentTempLng);
				}
				mHandler.sendEmptyMessage(GlobalParam.HIDE_PROGRESS_DIALOG);
				WeiYuanCommon.sendMsg(mHandler, MSG_SHOW_NEARY_LOCATION, poiList);
			}
		}

		@Override
		public void onGetPoiDetailSearchResult(int arg0, int arg1) {
			Log.e("onGetPoiDetailSearchResult", "onGetPoiDetailSearchResult++++");
		}

		@Override
		public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1, int arg2) {
		}
	}; 

	MKMapViewListener mMapViewListener = new MKMapViewListener() {    
		@Override    
		public void onMapMoveFinish() {    
			// 此处可以实现地图移动完成事件的状态监听    
			Log.e("onMapMoveFinish", "onMapMoveFinish");
			getData(false);
		}    

		@Override    
		public void onClickMapPoi(MapPoi arg0) {    
			// 此处可实现点击到地图可点标注时的监听    
		}    

		@Override  
		public void onGetCurrentMap(Bitmap b) {  
			//用MapView.getCurrentMap()发起截图后，在此处理截图结果.    
		}  

		@Override  
		public void onMapAnimationFinish() {  
			/** 
			 *  地图完成带动画的操作（如: animationTo()）后，此回调被触发 
			 */  
			Log.e("onMapAnimationFinish", "onMapAnimationFinish");
			getData(false);
		}  

		@Override  
		public void onMapLoadFinish() {  
			//地图初始化完成时，此回调被触发. 

			if (!mIsRegister) {
				return; 
			}

			if(mIsFirstInit){
				return;
			}

			Log.e("onMapLoadFinish", "onMapLoadFinish");

			mIsFirstInit = true;
			getData(false);

		}       
	};    

	/*
	 * 拖动地图后 获取位置信息
	 */
	private void getData(boolean hasPt) {
		int[] loc = new int[2];
		mMapView.getLocationInWindow(loc);

		Log.e("loc", loc[0] + ", " + loc[1]);

		GeoPoint center = null;

		while (center == null && !hasPt) {
			center = mMapView.getProjection().fromPixels(mDMetrics.widthPixels, mDMetrics.heightPixels/2);
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if(center != null || hasPt){
			double templng = 0;
			double templat = 0;

			if (hasPt) {
				templng = mLng;
				templat = mLat;
			}
			else{
				templng = Double.parseDouble(String.valueOf(center.getLongitudeE6()))/1e6;
				templat = Double.parseDouble(String.valueOf(center.getLatitudeE6()))/1e6;
			}


			final double lat = templat;
			final double lng = templng;

			mSearchTimerTask = new TimerTask() {
				@Override
				public void run() {
					GeoPoint point = new GeoPoint((int)(lat* 1e6), (int)(lng* 1e6));
					mSearch.reverseGeocode(point);

				}
			};
			if(mSearchTimer == null){
				mSearchTimer = new Timer();
			}
			mSearchTimer.schedule(mSearchTimerTask, 1000, 1000);
		}
	}


	@Override
	protected void onPause() {

		if(mApp != null && mApp.mBMapManager != null){
			mApp.mBMapManager.stop();
		}

		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		WeiYuanCommon.appOnResume(mContext);
		if(mApp != null && mApp.mBMapManager != null){
			mApp.mBMapManager.start();
		}

		if (mMapView != null) {
			mMapView.onResume();
		}

		super.onResume();
	}

	/*
	 * 页面销毁释放地图
	 * (non-Javadoc)
	 * @see com.weiyuan.BaseActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		if (mMapView != null) {
			mMapView.destroy();
		}
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mMapView != null) {
			mMapView.onSaveInstanceState(outState);
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (mMapView != null) {
			mMapView.onRestoreInstanceState(savedInstanceState);
		}
	}

	/*
	 * 按钮点击事件
	 * (non-Javadoc)
	 * @see com.weiyuan.BaseActivity#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			LocationActivity.this.finish();
			break;
		case R.id.right_btn:
			if(mMapInfo == null){
				Toast.makeText(mContext, mContext.getResources().getString(R.string.please_select_map),Toast.LENGTH_LONG).show();
				return;
			}
			Intent intent = new Intent();
			intent.putExtra("mapInfo", mMapInfo);
			setResult(RESULT_OK, intent);
			LocationActivity.this.finish();
			break;
		case R.id.search_btn:
			mSearchLocationDialog = new SearchLocationDialog(mContext);
			mSearchLocationDialog.show();
			break;
			//strAddr - 地址名
			//city - 地址所在城市
		case R.id.more_btn:
			showMoreMenu();
			break;

		default:
			break;
		}
	}

	/*
	 * 显示更多菜单
	 */
	private void showMoreMenu(){

		MMAlert.showAlert(mContext, "", mContext.getResources().
				getStringArray(R.array.map_more_menu), 
				null, new OnAlertSelectId() {

			@Override
			public void onClick(int whichButton) {
				switch (whichButton) {
				case 0://收藏
					MovingLoaction movingLocation = new MovingLoaction(mLat+"",mLng+"",mSendAddr,MessageType.MAP+"");
					favoriteMoving(MovingLoaction.getInfo(movingLocation));
					break;
				case 1://转发给好友
					MessageInfo msg = new MessageInfo();

					msg.typefile = MessageType.MAP;
					msg.mLat = mLat;
					msg.mLng = mLng;
					msg.mAddress = mSendAddr;


					Intent chooseUserIntent = new Intent();
					chooseUserIntent.setClass(mContext, ChooseUserActivity.class);
					//chooseUserIntent.putExtra("cardType",1);
					chooseUserIntent.putExtra("forward_msg", msg);
					startActivity(chooseUserIntent);
					break;
				default:
					break;
				}
			}
		});
	}

	
	/*
	 * 收藏显示的地图
	 */
	private void favoriteMoving(final String favoriteContent){
		if (!WeiYuanCommon.getNetWorkState()) {
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread(){
			public void run() {
				try {
					WeiYuanCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG,
							mContext.getResources().getString(R.string.send_request));
					WeiYuanState status = WeiYuanCommon.getWeiYuanInfo().favoreiteMoving(mFuid, mGroupId, favoriteContent);
					WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_CHECK_FAVORITE_STATUS,status);
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				} catch (WeiYuanException e) {
					e.printStackTrace();
					WeiYuanCommon.sendMsg(mBaseHandler,BASE_MSG_TIMEOUT_ERROR,
							mContext.getResources().getString(e.getStatusCode()));
				}catch (Exception e) {
					e.printStackTrace();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				}
			};
		}.start();
	}




	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		return false;
	}

	/*
	 * 定位当前位置事件
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location != null){
				if(mTimer != null){
					mTimer.cancel();
					mTimer.purge();
					mTimer = null;
				}

				if (mLocClient != null) {
					mLocClient.stop();
				}

				double Lat = location.getLatitude();
				double Lng = location.getLongitude();

				WeiYuanCommon.setCurrentLat(Lat);
				WeiYuanCommon.setCurrentLng(Lng);

				SharedPreferences preferences = mContext.getSharedPreferences(WeiYuanCommon.LOCATION_SHARED, 0);
				Editor editor = preferences.edit();
				editor.putString(WeiYuanCommon.LAT, String.valueOf(Lat));
				editor.putString(WeiYuanCommon.LNG, String.valueOf(Lng));
				editor.commit();

				mLng = Lng;
				mLat = Lat;

				GeoPoint point = new GeoPoint((int)(mLat* 1e6), (int)(mLng* 1e6));
				mMapController.setCenter(point);

				getData(true);

			}else {
				if(mTimer != null){
					mTimer.cancel();
					mTimer.purge();
					mTimer = null;
				}

				if (mLocClient != null) {
					mLocClient.stop();
				}
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null){
				return ;
			}
		}
	}

	
	/*
	 * 在地图上标明点
	 */
	private void addItemForMap(){
		if(mOverlay == null){
			mOverlay= new MyOverlay(mContext.getResources().getDrawable(R.drawable.icon_markf_h),mMapView);
		}

		if(mOverlay.size() != 0){
			mOverlay.removeAll();
		}

		if(mMapView.getOverlays().size() != 0){
			mMapView.getOverlays().remove(0);
		}
		GeoPoint point = new GeoPoint((int)(mLat* 1e6), (int)(mLng* 1e6));
		OverlayItem item = new OverlayItem(point,"0", "1");
		mOverlay.addItem(item);
		mMapView.setTraffic(true);
		mMapView.getOverlays().add(mOverlay);
		mMapView.refresh();
	}

	/*
	 * 标注当前位置
	 */
	public class MyOverlay extends ItemizedOverlay<OverlayItem>{

		public MyOverlay(Drawable defaultMarker, MapView mapView) {
			super(defaultMarker, mapView);
		}

	

		@Override
		protected boolean onTap(int arg0) {
			return super.onTap(arg0);
		}



		@Override
		public boolean onTap(GeoPoint pt , MapView mMapView){

			return false;
		}
	}



	/**
	 * 地图搜索控件
	 * @author dongli
	 *
	 */
	public class SearchLocationDialog extends Dialog implements OnItemClickListener{


		private Context mContext;
		private EditText mContentEdit;
		private ListView mListView;
		private Button mClearBtn;
		private LocationAdapter mLocationAdapter;

		public SearchLocationDialog(Context context) {
			super(context, R.style.ContentOverlay);
			mContext = context;

		}

		public SearchLocationDialog(Context context, int theme) {
			super(context, R.style.ContentOverlay);
			mContext = context;
		}

		public SearchLocationDialog(Context context, boolean cancelable,
				OnCancelListener cancelListener) {
			super(context, cancelable, cancelListener);
			mContext = context;
		}


		/*
		 * 导入搜索控件
		 * (non-Javadoc)
		 * @see android.app.Dialog#onCreate(android.os.Bundle)
		 */
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.contact_search_dialog);
			initComponent();
		}

		/*
		 * 初始化搜索对话框
		 */
		private void initComponent() {

			mClearBtn = (Button)findViewById(R.id.cancle_btn);
			mClearBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if(mClearBtn.getText().toString().equals(
							mContext.getResources().getString(R.string.search))){
						if(mContentEdit.getText().toString()!=null
								&& !mContentEdit.getText().toString().equals("")){
							mSearch.poiSearchInCity(mCurrentName,
									mContentEdit.getText().toString());
						}

					}else if(mClearBtn.getText().toString().equals(
							mContext.getResources().getString(R.string.cancel))){
						if(mSearchPoinInfoList!=null && mSearchPoinInfoList.size()>0){
							mSearchPoinInfoList.clear();
						}
						SearchLocationDialog.this.dismiss();
					}
				}
			});
			mContentEdit = (EditText) findViewById(R.id.searchcontent);
			mContentEdit.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before,
						int count) {

				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {

				}

				@Override
				public void afterTextChanged(Editable s) {
					if(s!=null && s.toString()!=null && !s.toString().equals("")){
						mClearBtn.setText(mContext.getResources().getString(R.string.search));
					}else{
						mClearBtn.setText(mContext.getResources().getString(R.string.cancel));
					}

					//arg0 String cityName arg1 String searchKey

				}
			});

			mListView = (ListView) findViewById(R.id.contact_list);
			mListView.setVisibility(View.GONE);
			mListView.setDivider(mContext.getResources().getDrawable(R.drawable.order_devider_line));
			mListView.setCacheColorHint(0);
			mListView.setOnItemClickListener(this);
			mListView.setSelector(mContext.getResources().getDrawable(R.drawable.transparent_selector));
		}


		/*
		 * 更新搜索出的结果
		 */
		public  void updateListView() {

			if(mSearchPoinInfoList != null && mSearchPoinInfoList.size() != 0){
				mListView.setVisibility(View.VISIBLE);
				mLocationAdapter = new LocationAdapter();
				mListView.setAdapter(mLocationAdapter);
			}
		}

		/*
		 * 在地图上显示点击的图片位置
		 * (non-Javadoc)
		 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
		 */
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			double tempLat = Double.parseDouble(String.valueOf(mSearchPoinInfoList.get(arg2).pt.getLatitudeE6()))/1e6;
			double tempLng =  Double.parseDouble(String.valueOf(mSearchPoinInfoList.get(arg2).pt.getLongitudeE6()))/1e6;
			mMapInfo = new MapInfo(mSearchPoinInfoList.get(arg2).name,mSearchPoinInfoList.get(arg2).address,
					String.valueOf(tempLat), String.valueOf(tempLng));
			mLng = tempLng;
			mLat = tempLat;
			GeoPoint point = new GeoPoint((int)(mLat* 1e6), (int)(mLng* 1e6));
		/*	mMapController = mMapView.getController();
			mMapController.setZoom(mZoomLevel);*/
			mMapController.setCenter(point);
			mMapView.setTraffic(true);
			getData(true);

			SearchLocationDialog.this.dismiss();
		}

		/*
		 * 显示搜索处理的数据
		 */
		public class LocationAdapter extends BaseAdapter{
			private final LayoutInflater mInflater;


			public LocationAdapter() {
				super();
				mInflater = (LayoutInflater)mContext.getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
			}

			@Override
			public int getCount() {
				return mSearchPoinInfoList.size();
			}

			@Override
			public Object getItem(int position) {
				return mSearchPoinInfoList.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				convertView=mInflater.inflate(R.layout.neary_location_item, null); 

				TextView title = (TextView)convertView.findViewById(R.id.location_text);
				ImageView splite = (ImageView)convertView.findViewById(R.id.splite);
				splite.setVisibility(View.GONE);
				TextView addr = (TextView)convertView.findViewById(R.id.location_addr);
				title.setText(mSearchPoinInfoList.get(position).name);
				addr.setText(mSearchPoinInfoList.get(position).address);
				return convertView;
			}

		}

	}
}
