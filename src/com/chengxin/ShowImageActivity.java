package com.chengxin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

import com.chengxin.R;
import com.chengxin.Entity.MessageInfo;
import com.chengxin.Entity.MessageType;
import com.chengxin.Entity.MovingPic;
import com.chengxin.Entity.UploadImg;
import com.chengxin.Entity.WeiYuanState;
import com.chengxin.dialog.MMAlert;
import com.chengxin.dialog.MMAlert.OnAlertSelectId;
import com.chengxin.exception.SPException;
import com.chengxin.global.FeatureFunction;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.ImageLoader;
import com.chengxin.global.MD5;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.map.BMapApiApp;
import com.chengxin.net.WeiYuanException;
import com.chengxin.widget.GestureDetector;
import com.chengxin.widget.MyImageView;

/**
 * 查看大图
 * @author dongli
 *
 */
public class ShowImageActivity extends BaseActivity implements OnClickListener, OnTouchListener{


	private LinearLayout mBottomToolBar;
	private MyImageView mImageView;
	private String mImageUrl;
	private RelativeLayout mTitleLayout;
	private LinearLayout mBackBtn, mOkBtn;
	private RelativeLayout mRelativeLayout;
	final GetterHandler mAnimHandler = new GetterHandler();
	private Bitmap mBitmap;
	public final static int SET_IMAGE_BITMAP = 11126;
	private final static int SAVE_SUCCESS = 5126;
	protected GestureListener mGestureListener;
	protected GestureDetector mGestureDetector;
	private Dialog  mPhoneDialog;

	private MessageInfo mMessageInfo;
	private int mType;  //1-显示删除按钮
	private List<UploadImg> mImageList = new ArrayList<UploadImg>();
	private int mPos;
	
	private String mFuid;
	private String mGroupid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.view_image);
		mMessageInfo = (MessageInfo) getIntent().getSerializableExtra("message");
		mType = getIntent().getIntExtra("type",0);
		mFuid = getIntent().getStringExtra("fuid");
		mGroupid = getIntent().getStringExtra("groupid");
		if(mType == 1){
			mImageList = (List<UploadImg>) getIntent().getSerializableExtra("img_list");
			mPos = getIntent().getIntExtra("pos", -1);
			mImageUrl = mImageList.get(mPos).mPicPath;
		}else if(mType == 2){
			mImageUrl = getIntent().getStringExtra("imageurl");
		}
		else{
			mImageUrl = getIntent().getStringExtra("imageurl");
		}
		initComponent();
	}

	private void initComponent(){
		if(mType == 1){
			setRightTextTitleContent(R.drawable.back_btn,R.string.del,R.string.look_big_img);
			mRightTextBtn.setOnClickListener(this);
		}else if(mType == 2){
			setTitleContent(R.drawable.back_btn,0,R.string.look_big_img);
		}
		else{
			setTitleContent(R.drawable.back_btn,false,false,true,R.string.look_big_img);
			mMoreBtn.setOnClickListener(this);
		}

		mLeftBtn.setOnClickListener(this);

		mImageView = (MyImageView) findViewById(R.id.imageview);
		mImageView.setOnTouchListener(this);
		mBottomToolBar  = (LinearLayout)findViewById(R.id.imageviewer_toolbar);
		mBottomToolBar.setVisibility(View.GONE);

		mTitleLayout = (RelativeLayout) findViewById(R.id.imageviewer_relativelayout_top);
		mTitleLayout.setVisibility(View.GONE);


		mBackBtn = (LinearLayout) findViewById(R.id.imageviewer_linearlayout_return);

		mBackBtn.setOnClickListener(this);
		mOkBtn = (LinearLayout) findViewById(R.id.imageviewer_linearlayout_save);
		mOkBtn.setOnClickListener(this);
		mGestureListener = new GestureListener();
		mGestureDetector = new GestureDetector(this, mGestureListener, null, true);
		mRelativeLayout = (RelativeLayout) findViewById(R.id.showZoomInOutLayout);
		mRelativeLayout.setOnTouchListener(new OnTouchListener(){

			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					showOnScreenControls();
					scheduleDismissOnScreenControls();
					break;

				case MotionEvent.ACTION_MOVE:
					break;

				case MotionEvent.ACTION_UP:
					break;

				default:
					break;
				}
				return false;
			}
		});


		if(mImageUrl != null && !mImageUrl.equals("")){
			Message message = new Message();
			message.what = GlobalParam.SHOW_PROGRESS_DIALOG;
			message.obj = BMapApiApp.getInstance().getResources().getString(R.string.add_more_loading);
			mAnimHandler.sendMessage(message);
		}
	}

	class GetterHandler extends Handler {
		private static final int IMAGE_GETTER_CALLBACK = 1;

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case IMAGE_GETTER_CALLBACK:
				((Runnable) msg.obj).run();
				break;

			case SET_IMAGE_BITMAP:
				if(mBitmap != null){
					mImageView.setImageBitmap(mBitmap);
				}else {
					Toast.makeText(mContext, BMapApiApp.getInstance().getResources().getString(R.string.load_error), Toast.LENGTH_LONG).show();
					ShowImageActivity.this.finish();
				}
				break;

			case GlobalParam.SHOW_PROGRESS_DIALOG:
				String str = (String)msg.obj;
				showProgressDialog(str);

				loadImage(mImageUrl);
				break;

			case GlobalParam.HIDE_PROGRESS_DIALOG:
				hideProgressDialog();
				break;
			case GlobalParam.NO_SD_CARD:
				Toast.makeText(mContext, R.string.no_sd_card_hint,Toast.LENGTH_LONG).show();
				break;
			case SAVE_SUCCESS:
				Toast.makeText(mContext, BMapApiApp.getInstance().getResources().getString(R.string.save_picture_to_ablun), Toast.LENGTH_SHORT).show();
				break;

			}
		}

		public void postGetterCallback(Runnable callback) {
			postDelayedGetterCallback(callback, 0);
		}

		public void postDelayedGetterCallback(Runnable callback, long delay) {
			if (callback == null) {
				throw new NullPointerException();
			}
			Message message = Message.obtain();
			message.what = IMAGE_GETTER_CALLBACK;
			message.obj = callback;
			sendMessageDelayed(message, delay);
		}

		public void removeAllGetterCallbacks() {
			removeMessages(IMAGE_GETTER_CALLBACK);
		}
	}

	private final Runnable mDismissOnScreenControlRunner = new Runnable() {
		public void run() {
			hideOnScreenControls();
		}
	};

	private void hideOnScreenControls() {

		if (mTitleLayout.getVisibility() == View.VISIBLE) {
			Animation a = new AlphaAnimation(1, 0);;
			a.setDuration(500);
			mTitleLayout.startAnimation(a);
			mTitleLayout.setVisibility(View.GONE);
		}

	}

	private void showOnScreenControls() {
		//if (mPaused) return;
		// If the view has not been attached to the window yet, the
		// zoomButtonControls will not able to show up. So delay it until the
		// view has attached to window.
		if (mTitleLayout.getVisibility() != View.VISIBLE) {
			Animation animation = new AlphaAnimation(0, 1);
			animation.setDuration(500);
			mTitleLayout.startAnimation(animation);
			mTitleLayout.setVisibility(View.GONE);
		}else {
			hideOnScreenControls();
		}
	}

	private void scheduleDismissOnScreenControls() {
		mAnimHandler.removeCallbacks(mDismissOnScreenControlRunner);
		mAnimHandler.postDelayed(mDismissOnScreenControlRunner, 2000);
	}


	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.imageviewer_linearlayout_return:
			ShowImageActivity.this.finish();
			break;

		case R.id.imageviewer_linearlayout_save:
			showPromptDialog(mContext);
			break;

		case R.id.imageview:
			showOnScreenControls();
			scheduleDismissOnScreenControls();
			break;
		case R.id.left_btn:
			ShowImageActivity.this.finish();
			break;
		case R.id.more_btn:
			showMoreMenu();
			break;
		case R.id.right_text_btn:
			createDialog(mContext,mContext.getResources().getString(R.string.del_friends_image_hint),
					mContext.getResources().getString(R.string.ok));
			break;

		default:
			break;
		}
	}

	
	/**
	 * 
	 * @param context
	 * @param cardTitle
	 * @param type 1-加入黑名单 2-添加好友申请 
	 * @param okTitle
	 */
	protected void createDialog(Context context, String cardTitle,final String okTitle) {
		mPhoneDialog = new Dialog (context,R.style.dialog);
		LayoutInflater factor = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View serviceView = factor.inflate(R.layout.card_dialog, null);

		mPhoneDialog.setContentView(serviceView);
		mPhoneDialog.show();
		mPhoneDialog.setCancelable(false);	
		mPhoneDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT
				, LayoutParams.WRAP_CONTENT);

	
		final TextView phoneEdit=(TextView)serviceView.findViewById(R.id.card_title);
		phoneEdit.setText(cardTitle);
	

		Button okBtn=(Button)serviceView.findViewById(R.id.yes);
		okBtn.setText(okTitle);


		okBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mPhoneDialog!=null) {
					mPhoneDialog.dismiss();
					mPhoneDialog=null;
				}
				WeiYuanCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG, 
						mContext.getResources().getString(R.string.send_request));
				mImageList.remove(mPos);
				Intent intent = new Intent();
				intent.putExtra("img_list",(Serializable)mImageList);
				setResult(2, intent);
				ShowImageActivity.this.finish();
			}
		});

		Button Cancel = (Button)serviceView.findViewById(R.id.no);
		Cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mPhoneDialog!=null) {
					mPhoneDialog.dismiss();
					mPhoneDialog = null;
				}
			}
		});
	}

	
	private void showMoreMenu(){

		MMAlert.showAlert(mContext, "", mContext.getResources().
				getStringArray(R.array.image_more_menu), 
				null, new OnAlertSelectId() {

			@Override
			public void onClick(int whichButton) {
				switch (whichButton) {
				case 0://发送给朋友
					if(mMessageInfo.imageString == null || mMessageInfo.imageString.equals("")){
						MovingPic picMoving = new MovingPic(mMessageInfo.imgUrlL, mMessageInfo.imgUrlS,mMessageInfo.typefile+"");
						mMessageInfo.imageString = MovingPic.getInfo(picMoving);
					}
					Intent chooseUserIntent = new Intent();
					chooseUserIntent.setClass(mContext, ChooseUserActivity.class);
					chooseUserIntent.putExtra("forward_msg", mMessageInfo);
					startActivity(chooseUserIntent);
					break;
				case 1://收藏
					MovingPic pic = new MovingPic(mImageUrl, MessageType.PICTURE+"");
					favoriteMoving(MovingPic.getInfo(pic));
					break;
				case 2://保存到手机
					showPromptDialog(mContext);
					break;
				default:
					break;
				}
			}
		});
	}

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
					WeiYuanState status = WeiYuanCommon.getWeiYuanInfo().favoreiteMoving(mFuid, mGroupid, favoriteContent);
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

	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
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



	@Override
	public boolean onTouch(View v, MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	class GestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			showOnScreenControls();
			scheduleDismissOnScreenControls();
			return super.onSingleTapConfirmed(e);
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return super.onSingleTapUp(e);
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			return super.onDoubleTap(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			return super.onFling(e1, e2, velocityX, velocityY);
		}

	}

	private void showPromptDialog(final Context context){

		final Dialog dlg = new Dialog(context, R.style.MMThem_DataSheet);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.add_block_prompt_dialog, null);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);

		final TextView prompt = (TextView)layout.findViewById(R.id.prmopt);
		prompt.setText(BMapApiApp.getInstance().getResources().getString(R.string.save_picture_prompt));

		final Button agreeBtn = (Button)layout.findViewById(R.id.okbtn);
		final Button cancelBtn = (Button)layout.findViewById(R.id.cancelbtn);
		agreeBtn.setText(BMapApiApp.getInstance().getResources().getString(R.string.save));
		cancelBtn.setText(BMapApiApp.getInstance().getResources().getString(R.string.cancel));

		agreeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
				savePicture();
			}
		});

		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
			}
		});


		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setCancelable(true);

		dlg.setContentView(layout);
		dlg.show();
	}

	private void loadImage(final String mImageUrl ){
		if(!WeiYuanCommon.verifyNetwork(mContext)){
			Toast.makeText(mContext, BMapApiApp.getInstance().getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
			hideProgressDialog();
			ShowImageActivity.this.finish();
			return;
		}
		new Thread(){
			@Override
			public void run() {
				super.run();
				if(mImageUrl != null){
					try {
						if(mType == 1){
							mBitmap = BitmapFactory.decodeFile(mImageUrl);
						}else{
							File file = null;
							byte[] imageData = null;
							String fileName = new MD5().getMD5ofStr(mImageUrl);// url.replaceAll("/",
							if (FeatureFunction.checkSDCard()) {

								if (FeatureFunction.newFolder(Environment.getExternalStorageDirectory()
										+ ImageLoader.SDCARD_PICTURE_CACHE_PATH)) {
									file = new File(
											Environment.getExternalStorageDirectory()
											+ ImageLoader.SDCARD_PICTURE_CACHE_PATH, fileName);
									if(file != null && file.exists()){
										try {
											FileInputStream fin = new FileInputStream(file.getPath());
											int length = fin.available();
											byte[] buffer = new byte[length];
											fin.read(buffer);
											fin.close();
											imageData = buffer;
											buffer = null;
											System.gc();
										} catch (FileNotFoundException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

									}
									else{
										imageData = FeatureFunction.getImage(new URL(mImageUrl),file);
									}
								}
							}else{
								mAnimHandler.sendEmptyMessage(GlobalParam.HIDE_PROGRESS_DIALOG);
								mAnimHandler.sendEmptyMessage(GlobalParam.NO_SD_CARD);
								return;
							}
							if(imageData != null){
								mBitmap = BitmapFactory.decodeByteArray(imageData, 0,imageData.length);
							}
							imageData = null;
						}



						System.gc();

					} catch (MalformedURLException e) {
						e.printStackTrace();

					} catch (SPException e) {
						e.printStackTrace();
					}

					mAnimHandler.sendEmptyMessage(GlobalParam.HIDE_PROGRESS_DIALOG);
					mAnimHandler.sendEmptyMessage(SET_IMAGE_BITMAP);
				}
			}
		}.start();
	}


	private void savePicture(){
		new Thread(){
			@Override
			public void run(){
				String fileName = FeatureFunction.getPhotoFileName(0);
				String uri = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, fileName, "");
				String filePath = FeatureFunction.getFilePathByContentResolver(mContext, Uri.parse(uri));
				int index = filePath.lastIndexOf("/");
				String filePrefix = filePath.substring(0, index + 1) + fileName;
				try {
					File bitmapFile = new File(filePrefix);
					FileOutputStream bitmapWriter;
					bitmapWriter = new FileOutputStream(bitmapFile);
					if (mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bitmapWriter)) {
						File oldfile = new File(filePath);
						if(oldfile.exists()){
							oldfile.delete();
						}
						bitmapWriter.flush();
						bitmapWriter.close();
						Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);     
						Uri refreshUri = Uri.fromFile(bitmapFile);     
						intent.setData(refreshUri);     
						mContext.sendBroadcast(intent);  
						mAnimHandler.sendEmptyMessage(SAVE_SUCCESS);
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();

	}
}
