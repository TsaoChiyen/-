package com.chengxin;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.chengxin.R;
import com.chengxin.DB.DBHelper;
import com.chengxin.DB.RoomTable;
import com.chengxin.DB.SessionTable;
import com.chengxin.DB.UserTable;
import com.chengxin.Entity.Login;
import com.chengxin.Entity.Room;
import com.chengxin.Entity.Session;
import com.chengxin.camera.CameraManager;
import com.chengxin.decoding.CaptureActivityHandler;
import com.chengxin.decoding.InactivityTimer;
import com.chengxin.fragment.ChatFragment;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.net.WeiYuanException;
import com.chengxin.widget.ViewfinderView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
/**
 * 扫一扫
 * @author 
 */
public class CaptureActivity extends BaseActivity implements Callback {

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.capture);
		mContext = this;
		setTitleContent(R.drawable.back_btn,0,R.string.scan_qr_code);
		mLeftBtn.setOnClickListener(this);
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String codeUrl = result.getText();
		if (codeUrl.equals("")) {
			Toast.makeText(CaptureActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
		}else{
			int index = codeUrl.lastIndexOf("/");//117.78.2.70/MTU2
			String groupId = codeUrl.substring(index+1);
			if(groupId!=null && !groupId.equals("")){
				groupId = new String( Base64.decode(groupId, Base64.DEFAULT));
				getGroupDetail(groupId);
			}
		}

	}

	
	private void getGroupDetail(final String groupId){
		new Thread(){
			@Override
			public void run(){
				if(WeiYuanCommon.verifyNetwork(mContext)){
					try {
						Room result = WeiYuanCommon.getWeiYuanInfo().getRoomInfoById(groupId);
						if(result != null && result.state != null && result.state.code == 0){

							SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
							RoomTable roomTab = new RoomTable(db);
							roomTab.insert(result);
							if((WeiYuanCommon.getUserId(mContext)!=null
									&& !WeiYuanCommon.getUserId(mContext).equals(""))){
								if(result.isjoin ==1){

									Intent destroy = new Intent(ChatMainActivity.DESTORY_ACTION);
									destroy.putExtra("type", 1);
									sendBroadcast(destroy);
									String roomId = result.groupId;
									List<Login> roomUsrList = result.mUserList;

									String groupHeadUrl="";
									if (roomUsrList != null ) {
										//RoomUserTable roomUserTable = new RoomUserTable(db);
										UserTable userTable = new UserTable(db);

										for (int j = 0; j < roomUsrList.size(); j++) {

											if(result.groupCount-1 == j){
												groupHeadUrl+=roomUsrList.get(j).headsmall;
											}else{
												groupHeadUrl+=roomUsrList.get(j).headsmall+",";
											}


											Login user = userTable.query(roomUsrList.get(j).uid);
											if(user == null){
												userTable.insert(roomUsrList.get(j), -999);
											}
										}
									}

									Session session = new Session();
									session.type = 300;
									session.name = result.groupName;
									session.heading = groupHeadUrl;
									session.lastMessageTime = System.currentTimeMillis();
									session.setFromId(result.groupId);
									session.mUnreadCount = 0;

									SessionTable table = new SessionTable(db);
									table.insert(session);
									sendBroadcast(new Intent(ChatFragment.ACTION_REFRESH_SESSION));

									Login user = new Login();
									user.uid = result.groupId;
									user.nickname = result.groupName;
									user.headsmall = groupHeadUrl;
									user.mIsRoom = 300;
									//user.headsmall = mSessionList.get(position).heading;
									Intent intent = new Intent(mContext, ChatMainActivity.class);
									intent.putExtra("data", user);

									startActivity(intent);
									CaptureActivity.this.finish();
								}else{
									Log.e("pushChatMsg", "groupId:"+groupId);
									Intent joinRoomIntent = new Intent();
									joinRoomIntent.setClass(mContext, JoinRoomDetailActivity.class);
									joinRoomIntent.putExtra("grouId",groupId);
									joinRoomIntent.putExtra("room", result);
									startActivity(joinRoomIntent);
									CaptureActivity.this.finish();
								}
							}

						}else {
							Message msg=new Message();
							msg.what=GlobalParam.MSG_LOAD_ERROR;
							if(result != null && result.state != null && result.state.errorMsg != null 
									&& !result.state.errorMsg.equals("")){
								msg.obj = result.state.errorMsg;
							}else {
								msg.obj = mContext.getString(R.string.load_error);
							}
							mHandler.sendMessage(msg);
						}
					} catch (WeiYuanException e) {
						e.printStackTrace();
						WeiYuanCommon.sendMsg(mBaseHandler, BASE_MSG_TIMEOUT_ERROR,
								mContext.getResources().getString(R.string.timeout));
					}

				}else {
					mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
				}
			}
		}.start();
	}


  
	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			CaptureActivity.this.finish();
			break;

		default:
			break;
		}
	}

	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GlobalParam.MSG_LOAD_ERROR:
				hideProgressDialog();
				String error_Detail = (String)msg.obj;
				if(error_Detail != null && !error_Detail.equals("")){
					Toast.makeText(mContext,error_Detail,Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(mContext,R.string.load_error,Toast.LENGTH_LONG).show();
				}
				break;

			default:
				break;
			}
		}

	};


}