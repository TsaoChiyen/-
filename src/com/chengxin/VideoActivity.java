package com.chengxin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chengxin.global.FeatureFunction;
import com.chengxin.global.WeiYuanCommon;

public class VideoActivity extends BaseActivity implements SurfaceHolder.Callback {

	private File myRecVideoFile;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private TextView tvTime;
	private TextView tvSize;
	private Button btnStart;
	private Button btnStop;
	private Button btnCancel;
	private MediaRecorder recorder;
	private Handler handler;
	private Camera camera;
	private boolean recording; // 记录是否正在录像,fasle为未录像, true 为正在录像
	private int minute = 0;
	private int second = 0;
	private String time="";
	private String size="";
	private String fileName;
	private boolean checkValue; //false:录制 true-停止

	/**
	 * 录制过程中,时间变化,大小变化
	 */
	private Runnable timeRun = new Runnable() {

		@Override
		public void run() {
			long fileLength=myRecVideoFile.length();
			if(fileLength<1024 && fileLength>0){
				size=String.format("%dB/10M", fileLength);
			}else if(fileLength>=1024 && fileLength<(1024*1024)){
				fileLength=fileLength/1024;
				size=String.format("%dK/10M", fileLength);
			}else if(fileLength>(1024*1024*1024)){
				fileLength=(fileLength/1024)/1024;
				size=String.format("%dM/10M", fileLength);
			}
			second++;
			if(second == 18){//15秒后结束拍摄
				second = 0;
				if(recorder!=null){
					releaseMediaRecorder();
					minute = 0;
					second = 0;
					btnStart.setEnabled(false);
					handler.removeCallbacks(timeRun);
					recording = false;
					getBitmapsFromVideo();

				}
			}
			/*if (second == 60) {
				minute++;
				second = 0;
			}*/
			time = String.format("%02d:%02d", minute, second);
			tvSize.setText(size);
			tvTime.setText(time);
			handler.postDelayed(timeRun, 1000);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recorded_video);
		mContext = this;
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);   

		mSurfaceView = (SurfaceView) findViewById(R.id.videoView);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mSurfaceHolder.setKeepScreenOn(true);
		handler = new Handler();
		tvTime = (TextView) findViewById(R.id.tv_video_time);
		tvSize=(TextView)findViewById(R.id.tv_video_size);
		btnStop = (Button) findViewById(R.id.btn_video_stop);
		btnStart = (Button) findViewById(R.id.btn_video_start);
		btnCancel = (Button) findViewById(R.id.btn_video_cancel);
		btnCancel.setOnClickListener(listener);
		btnStart.setOnClickListener(listener);
		btnStop.setOnClickListener(listener);
		// 设置sdcard的路径
		fileName = Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY
				+"video_" +System.currentTimeMillis() + ".mp4";
		Log.e("fileName", fileName);
		//fileName += File.separator + File.separator+"Ruanko_Jobseeker"+File.separator+name;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// 开启相机
		if (camera == null) {
			int CammeraIndex=FindBackCamera();  
			/*if(CammeraIndex==-1){  
				Toast.makeText(getApplicationContext(), "您的手机不支持前置摄像头", Toast.LENGTH_SHORT);
				CammeraIndex=FindBackCamera();  
			}*/
			camera = Camera.open(CammeraIndex); 
			try {
				camera.setPreviewDisplay(mSurfaceHolder);
				camera.setDisplayOrientation(90);
			} catch (IOException e) {
				e.printStackTrace();
				camera.release();
			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// 开始预览
		camera.startPreview();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// 关闭预览并释放资源
		if (camera != null) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}

	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_video_stop:
				btnStart.setEnabled(true);
				second = 0;
				if(recorder!=null){
					releaseMediaRecorder();
					minute = 0;
					second = 0;
					handler.removeCallbacks(timeRun);
					recording = false;
					getBitmapsFromVideo();
				}
				break;
			case R.id.btn_video_start:
				if(checkValue){
					checkValue = false;
					btnStart.setText("开始");
					tvSize.setText("");
					tvTime.setText(time);
					second = 0;
					if(recorder!=null){
						releaseMediaRecorder();
						minute = 0;
						second = 0;
						handler.removeCallbacks(timeRun);
						recording = false;
						getBitmapsFromVideo();
					}
				}else{
					btnStart.setText("停止");
					checkValue = true;
					if(recorder!=null){
						releaseMediaRecorder();
						minute = 0;
						second = 0;
						handler.removeCallbacks(timeRun);
						recording = false;
					}
					recorder();
					//btnStart.setEnabled(false);
				}

				break;
			case R.id.btn_video_cancel:
				releaseMediaRecorder();
				handler.removeCallbacks(timeRun);
				minute=0;
				second=0;
				recording = false;
				VideoActivity.this.finish();
				break;
			}
		}
	};
	/*//判断前置摄像头是否存在
	private int FindFrontCamera(){  
		int cameraCount = 0;  
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();  
		cameraCount = Camera.getNumberOfCameras(); // get cameras number  

		for ( int camIdx = 0; camIdx < cameraCount;camIdx++ ) {  
			Camera.getCameraInfo( camIdx, cameraInfo ); // get camerainfo  
			if ( cameraInfo.facing ==Camera.CameraInfo.CAMERA_FACING_FRONT ) {   
				// 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置  
				return camIdx;  
			}  
		}  
		return -1;  
	}  */
	//判断后置摄像头是否存在
	private int FindBackCamera(){  
		int cameraCount = 0;  
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();  
		cameraCount = Camera.getNumberOfCameras(); // get cameras number  

		for ( int camIdx = 0; camIdx < cameraCount;camIdx++ ) {  
			Camera.getCameraInfo( camIdx, cameraInfo ); // get camerainfo  
			if ( cameraInfo.facing ==Camera.CameraInfo.CAMERA_FACING_BACK ) {   
				// 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置  
				return camIdx;  
			}  
		}  
		return -1;  
	}  

	//释放recorder资源 
	private void releaseMediaRecorder(){
		if (recorder != null) {
			recorder.stop();
			recorder.release();
			recorder = null;
		}
	}
	//开始录像
	public void recorder() {
		if (!recording) {
			try {
				// 关闭预览并释放资源
				if(camera!=null){
					camera.stopPreview();
					camera.release();
					camera = null;
				}
				recorder = new MediaRecorder();
				// 声明视频文件对象
				myRecVideoFile = new File(fileName);
				if(!myRecVideoFile.exists()){
					myRecVideoFile.getParentFile().mkdirs();
					myRecVideoFile.createNewFile();
				}

				recorder.reset();
				// 判断是否有前置摄像头，若有则打开，否则打开后置摄像头
				int CammeraIndex=FindBackCamera();  
				/*if(CammeraIndex==-1){  
					Toast.makeText(getApplicationContext(), "您的手机不支持前置摄像头", Toast.LENGTH_SHORT);
					CammeraIndex=FindBackCamera();  
				}*/
				camera = Camera.open(CammeraIndex);
				// 设置摄像头预览顺时针旋转90度，才能使预览图像显示为正确的，而不是逆时针旋转90度的。
				camera.setDisplayOrientation(90);
				camera.unlock();
				recorder.setCamera(camera); //设置摄像头为相机

				recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);//视频源 
				recorder.setAudioSource(MediaRecorder.AudioSource.MIC); // 录音源为麦克风
				recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW)); //设置视频和声音的编码为系统自带的格式	
				recorder.setOutputFile(myRecVideoFile.getAbsolutePath());
				recorder.setPreviewDisplay(mSurfaceHolder.getSurface()); // 预览
				recorder.setMaxFileSize(10*1024*1024); //设置视频文件的最大值为10M,单位B
				//recorder.setVideoSize(800, 480);

				//recorder.setMaxDuration(3*1000);//设置视频的最大时长，单位毫秒
				//				recorder.setOrientationHint(90);//视频旋转90度，没有用
				recorder.prepare(); // 准备录像
				recorder.start(); // 开始录像
				handler.post(timeRun); // 调用Runable
				recording = true; // 改变录制状态为正在录制

			} catch (IOException e1) {
				releaseMediaRecorder();
				handler.removeCallbacks(timeRun);
				minute = 0;
				second = 0;
				recording = false;
				btnStart.setEnabled(true);
			} catch (IllegalStateException e) {
				releaseMediaRecorder();
				handler.removeCallbacks(timeRun);
				minute = 0;
				second = 0;
				recording = false;
				btnStart.setEnabled(true);
			}
		} else
			Toast.makeText(getApplicationContext(), "视频录制中...", Toast.LENGTH_SHORT);
	}


	/**
	 * 将视频转换为多张图片
	 */
	public void getBitmapsFromVideo() {

		new Thread(){
			public void run() {
				WeiYuanCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG, "正在获取视频地址");
				String dataPath = fileName;
				MediaMetadataRetriever retriever = new MediaMetadataRetriever();
				retriever.setDataSource(dataPath);
				// 取得视频的长度(单位为毫秒)
				String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
				// 取得视频的长度(单位为秒)
				int seconds = Integer.valueOf(time) / 1000;
				String[] picArray = null;
				if(seconds !=0 ){
					picArray = new String[seconds];
					// 得到每一秒时刻的bitmap比如第一秒,第二秒
					for (int i = 0; i < seconds; i++) {
						Bitmap bitmap = retriever.getFrameAtTime(i*1000*1000,MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
						String path =  Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY
								+System.currentTimeMillis()+"video_pic"+i+".jpg"; 
						picArray[i] = path;
						FileOutputStream fos = null;
						try {
							fos = new FileOutputStream(path);
							bitmap.compress(CompressFormat.JPEG, 80, fos);
							fos.close();
						} catch (Exception e) {
							mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
							e.printStackTrace();
						}
					}
				}

				if(picArray !=null && picArray.length>0){
					String jpgPath = Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY
							+System.currentTimeMillis()+"video.gif";
					if(JpgToGif.jpgToGif(picArray, jpgPath)){
						Log.e("gif_url", jpgPath);
						for (int j = 0; j < picArray.length; j++) {
							File file = new File(picArray[j]);
							if(file != null && file.exists()){
								file.delete();
							}
						}
						Intent intent = new Intent();
						intent.putExtra("video_url", fileName);
						intent.putExtra("video_gif", jpgPath);
						setResult(RESULT_OK, intent);
						VideoActivity.this.finish();
						mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
					}else{
						mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
						Toast.makeText(mContext, R.string.file_not_exist,Toast.LENGTH_LONG).show();
					}
				}else{
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
					Toast.makeText(mContext, R.string.file_not_exist,Toast.LENGTH_LONG).show();
				}

			};
		}.start();

	}

	/**
	 * 调用系统拍摄视频
	 *//*
	public void camerVideo(){
		Intent mIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE); 
		mIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 70000);//70s
		startActivityForResult(mIntent, CAMERA_ACTIVITY);//CAMERA_ACTIVITY = 1 
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode != RESULT_OK)   return;
		try { 
			AssetFileDescriptor videoAsset = getContentResolver().openAssetFileDescriptor(intent.getData(), "r");
			FileInputStream fis = videoAsset.createInputStream();
			File tmpFile = new File(Environment.getExternalStorageDirectory(), "recordVideo_0_5.3gp");
			FileOutputStream fos = new FileOutputStream(tmpFile);
			byte[] buf = new byte[1024];
			int len;
			while ((len = fis.read(buf)) > 0) {
				fos.write(buf, 0, len);
			}           
			fis.close(); 
			fos.close(); 
		} catch (IOException io_e) {
			// TODO: handle error 
		}
	}*/
}

