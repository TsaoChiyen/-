package com.chengxin;



import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.chengxin.R;
import com.chengxin.Entity.Login;
import com.chengxin.Entity.LoginResult;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.ImageLoader;
import com.chengxin.global.ResearchCommon;
import com.chengxin.net.ResearchException;


/**
 * 摇一摇
 * @author dongli
 *
 */
public class ShakeActivity extends BaseActivity {

	ShakeListener mShakeListener = null;
	Vibrator mVibrator;
	private RelativeLayout mImgUp;
	private RelativeLayout mImgDn;

	private SoundPool sndPool;
	private HashMap<Integer, Integer> soundPoolMap = new HashMap<Integer, Integer>();

    private LocationClient mLocClient;
    private double mLng ;
    private double mLat;


    RelativeLayout mUserLayout;
    ImageView mUserHeaderView;
    TextView mUserName;
    TextView mDistance;

    Login mMatchUser = null;


	//addFriend
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shake_activity);
		mContext = this;
		initCompent();

        getLocation();
	}
	
	

	@Override
	protected void onResume() {
		super.onResume();
		if(mShakeListener!=null){
			mShakeListener.start();
		}
	}

	private void initCompent(){
		setTitleContent(R.drawable.back_btn, 0, "摇一摇");
		mLeftBtn.setOnClickListener(this);

		mVibrator = (Vibrator)getApplication().getSystemService(VIBRATOR_SERVICE);

		mImgUp = (RelativeLayout) findViewById(R.id.shakeImgUp);
		mImgDn = (RelativeLayout) findViewById(R.id.shakeImgDown);

		loadSound() ;
		mShakeListener = new ShakeListener(this);
		mShakeListener.setOnShakeListener(new ShakeListener.OnShakeListener() {
			public void onShake() {
				//Toast.makeText(getApplicationContext(), "开始查找！", Toast.LENGTH_SHORT).show();
				startAnim();  //开始 摇一摇手掌动画

                mUserLayout.setVisibility(View.GONE);
                mMatchUser = null;
				
				mShakeListener.stop();
				sndPool.play(soundPoolMap.get(0), (float) 1, (float) 1, 0, 0,(float) 1.2);//加载不同的声音
				new Handler().postDelayed(new Runnable(){
					public void run(){
						addFriend(1);
					}
				}, 2000);
			}
		});

        mUserLayout = (RelativeLayout)findViewById(R.id.user_layout);
        mUserLayout.setOnClickListener(this);
        mUserHeaderView = (ImageView)findViewById(R.id.header);
        mUserName = (TextView)findViewById(R.id.name);
        mDistance = (TextView)findViewById(R.id.distance);
    }

    /*
	 * 获取当前位置信息
	 */
    private Timer mTimer;
    private TimerTask mTimerTask;
    public MyLocationListenner mMyListener = new MyLocationListenner();

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
                mHandler.sendEmptyMessage(15);
            }
        };
        mTimer.schedule(mTimerTask, 60000, 1000);

       /* Message message = new Message();
        message.what = GlobalParam.SHOW_PROGRESS_DIALOG;
        message.obj = mContext.getResources().getString(R.string.location_doing);
        mHandler.sendMessage(message);*/
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

                ResearchCommon.setCurrentLat(Lat);
                ResearchCommon.setCurrentLng(Lng);

                SharedPreferences preferences = mContext.getSharedPreferences(ResearchCommon.LOCATION_SHARED, 0);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(ResearchCommon.LAT, String.valueOf(Lat));
                editor.putString(ResearchCommon.LNG, String.valueOf(Lng));
                editor.commit();

                mLng = Lng;
                mLat = Lat;

                //GeoPoint point = new GeoPoint((int)(mLat* 1e6), (int)(mLng* 1e6));
                //mMapController.setCenter(point);

                //addFriend(0);

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

	private void loadSound() {

		sndPool = new SoundPool(2, AudioManager.STREAM_SYSTEM, 5);
		new Thread() {
			public void run() {
				try {
					soundPoolMap.put(
							0,
							sndPool.load(getAssets().openFd(
									"sound/shake_sound_male.mp3"), 1));

					soundPoolMap.put(
							1,
							sndPool.load(getAssets().openFd(
									"sound/shake_match.mp3"), 1));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public void startAnim () {   //定义摇一摇动画动画
		AnimationSet animup = new AnimationSet(true);
		TranslateAnimation mytranslateanimup0 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,-0.5f);
		mytranslateanimup0.setDuration(1000);
		TranslateAnimation mytranslateanimup1 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,+0.5f);
		mytranslateanimup1.setDuration(1000);
		mytranslateanimup1.setStartOffset(1000);
		animup.addAnimation(mytranslateanimup0);
		animup.addAnimation(mytranslateanimup1);
		mImgUp.startAnimation(animup);

		AnimationSet animdn = new AnimationSet(true);
		TranslateAnimation mytranslateanimdn0 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,+0.5f);
		mytranslateanimdn0.setDuration(1000);
		TranslateAnimation mytranslateanimdn1 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,-0.5f);
		mytranslateanimdn1.setDuration(1000);
		mytranslateanimdn1.setStartOffset(1000);
		animdn.addAnimation(mytranslateanimdn0);
		animdn.addAnimation(mytranslateanimdn1);
		mImgDn.startAnimation(animdn);	
	}
	public void startVibrato(){		//定义震动
		mVibrator.vibrate( new long[]{500,200,500,200}, -1); //第一个｛｝里面是节奏数组， 第二个参数是重复次数，-1为不重复，非-1俄日从pattern的指定下标开始重复
	}

	public void shake_activity_back(View v) {     //标题栏 返回按钮
		this.finish();
	}  
	public void linshi(View v) {     //标题栏
		startAnim();
	}  
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mShakeListener != null) {
			mShakeListener.stop();
		}
	}

	private void addFriend(final int type){
		ResearchCommon.verifyNetwork(mContext);
		if (!ResearchCommon.getNetWorkState()) {
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread(){
			public void run() {
				try {
                    ResearchCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG, "正在搜索同一时刻摇晃手机的人");
                    LoginResult login = ResearchCommon.getResearchInfo().shakeSearch(String.valueOf(mLng), String.valueOf(mLat));
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
                    ResearchCommon.sendMsg(mHandler, GlobalParam.MSG_SHOW_LOAD_DATA, login);
				} catch (ResearchException e) {
					e.printStackTrace();
                    ResearchCommon.sendMsg(mBaseHandler, BASE_MSG_TIMEOUT_ERROR,
							mContext.getResources().getString(e.getStatusCode()));
				}catch (Exception e) {
					e.printStackTrace();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				}
			};
		}.start();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			ShakeActivity.this.finish();
			break;
        case R.id.user_layout:
            if (mMatchUser != null) {
                Intent intent = new Intent();
                intent.setClass(mContext, UserInfoActivity.class);
                intent.putExtra("uid", mMatchUser.uid);
                intent.putExtra("type",2);
               startActivity(intent);
            }
            break;

		default:
			break;
		}
	}

    private Handler mHandler = new Handler(){

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GlobalParam.MSG_SHOW_LOAD_DATA:
				sndPool.play(soundPoolMap.get(1), (float) 1, (float) 1, 0, 0,(float) 1.0);//加载不同的声音
				mVibrator.cancel();
				mShakeListener.start();
				LoginResult result = (LoginResult)msg.obj;
                mMatchUser = result.mLogin;
				if(result == null || result.mState == null 
						|| result.mState.code!=0 || mMatchUser == null || TextUtils.isEmpty(mMatchUser.uid)){
					Toast.makeText(mContext, "抱歉，暂时没有找到\n在同一时刻摇一摇的人。\n再试一次吧！", Toast.LENGTH_LONG).show();
					return;
				}

				//Intent intent = new Intent();
				//if(result.mUserList.size()==1){
					//intent.setClass(mContext,UserInfoActivity.class);
					//intent.putExtra("user",result.mUserList.get(0));
					//intent.putExtra("uid", result.mLogin.uid);
					//intent.putExtra("type",2);
					//startActivity(intent);

                showMatchUser(mMatchUser);
				/*}else{
					intent = new Intent();
					intent.setClass(mContext, SearchResultActivity.class);
					intent.putExtra("user_list",(Serializable)result.mUserList);
					startActivity(intent);
				}*/
				break;
                case 15:
                    //Toast.makeText(mContext, R.string.location_error, Toast.LENGTH_LONG).show();

                    if (mLocClient != null) {
                        mLocClient.stop();
                    }

                    //hideProgressDialog();
                    break;
			default:
				break;
			}
		}
		
	};

    ImageLoader imageLoader = new ImageLoader();

    private void showMatchUser(Login user){
        if (user != null){
            mUserLayout.setVisibility(View.VISIBLE);

            imageLoader.getBitmap(mContext, mUserHeaderView, null, user.headsmall, 0, true, false);

            mUserName.setText(user.nickname);

            double distance = Double.valueOf(user.distance);
            if (distance>1000){
                distance = distance / 1000;
                BigDecimal bg = new BigDecimal(distance);
                double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

                mDistance.setText(f1 + "千米");
            }
            else {
                BigDecimal bg = new BigDecimal(distance);
                double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

                mDistance.setText(f1 + "米");
            }
        }
    }
}
