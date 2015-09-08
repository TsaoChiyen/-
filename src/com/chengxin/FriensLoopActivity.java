package com.chengxin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.chengxin.R;
import com.chengxin.Entity.CommentUser;
import com.chengxin.Entity.FavoriteItem;
import com.chengxin.Entity.FriendsLoop;
import com.chengxin.Entity.FriendsLoopItem;
import com.chengxin.Entity.Login;
import com.chengxin.Entity.MessageInfo;
import com.chengxin.Entity.MessageType;
import com.chengxin.Entity.MorePicture;
import com.chengxin.Entity.MovingContent;
import com.chengxin.Entity.MovingPic;
import com.chengxin.Entity.NotifiyVo;
import com.chengxin.Entity.WeiYuanState;
import com.chengxin.adapter.EmojiAdapter;
import com.chengxin.adapter.FriendsLoopAdapter;
import com.chengxin.adapter.WeiYuanViewPagerAdapter;
import com.chengxin.dialog.MMAlert;
import com.chengxin.dialog.MMAlert.OnAlertSelectId;
import com.chengxin.global.FeatureFunction;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.ImageLoader;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.net.WeiYuanException;
import com.chengxin.widget.MyPullToRefreshListView;
import com.chengxin.widget.RoundImageView;
import com.chengxin.widget.MyPullToRefreshListView.OnChangeStateListener;

/**
 * 朋友圈
 * @author dongli
 *
 */
public class FriensLoopActivity extends BaseActivity implements OnTouchListener, OnChangeStateListener, OnPageChangeListener{

	/*
	 * 定义全局变量
	 */
	public final static String MSG_REFRESH_MOVIINF = "weiyuan_refresh_friends_loop_action";
	private ListView mListView;
	private MyPullToRefreshListView mContainer;
	private TextView mRefreshViewLastUpdated,mLoginUserName,
	mSetCoverHintTextView;
	private boolean mIsRefreshing = false;

	private List<List<String>> mTotalEmotionList = new ArrayList<List<String>>();
	private ViewPager mViewPager;
	private WeiYuanViewPagerAdapter mEmotionAdapter;
	private LinkedList<View> mViewList = new LinkedList<View>();
	private LinearLayout mLayoutCircle;
	public int mPageIndxe = 0;
	private RelativeLayout mEmotionLayout;


	private RelativeLayout mBottomLayout;
	private RoundImageView mProfileHeaderIcon;
	private ImageView mHeaderBg,mPicBtn;
	private View mSearchHeader;
	private LinearLayout mFootView,mBottomMenuLayout;
	private EditText mCommentEdit;
	private Dialog  mPhoneDialog;


	protected AlertDialog mUpgradeNotifyDialog;
	private Button mCommentBtn;


	private FriendsLoopAdapter mAdapter;

	private DisplayMetrics mMetric;
	private int ICON_SIZE_WIDTH;
	private static  int ICON_SIZE_HEIGHT ;

	private FriendsLoop mMyAlbum;
	private List<FriendsLoopItem> mDataList = new ArrayList<FriendsLoopItem>();

	private ImageLoader mImageLoader;
	private String mTempFileName="front_cover.jpg";
	private String mCropImgPath,mToUserId;
	private List<MorePicture> mListpic = new ArrayList<MorePicture>(); 
	private Bitmap mBitmap;	
	private int mShareId,mPosition;
	private String mInputComment;





	/*
	 * 导入控件
	 * (non-Javadoc)
	 * @see com.weiyuan.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		mMetric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mMetric);
		mImageLoader = new ImageLoader();
		ICON_SIZE_WIDTH =mMetric.widthPixels;
		if(ICON_SIZE_WIDTH>640){
			ICON_SIZE_WIDTH = 640;
		}
		ICON_SIZE_HEIGHT = (ICON_SIZE_WIDTH/3)*2;//x:y 3:2
		setContentView(R.layout.friends_loop);
		registerReceiver();
		WeiYuanCommon.saveReadFriendsLoopTip(mContext, true);
		Intent hideIntent = new Intent(GlobalParam.ACTION_HIDE_FOUND_NEW_TIP);
		hideIntent.putExtra("found_type",1);
		sendBroadcast(hideIntent);
		sendBroadcast(new Intent(GlobalParam.ACTION_HIDE_NEW_FRIENDS_LOOP));

		initCompent();
		getLoopData(GlobalParam.LIST_LOAD_FIRST);
	}

	/*
	 * 注册通知
	 */
	private void registerReceiver(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(MSG_REFRESH_MOVIINF);
		registerReceiver(Receiver, filter);
	}

	/*
	 * 释放通知
	 */
	private void unregisterReceiver(){
		unregisterReceiver(Receiver);
	}

	/*
	 * 处理通知
	 */
	private BroadcastReceiver Receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			String action = intent.getAction();
			if(action.equals(MSG_REFRESH_MOVIINF)){
				mMyAlbum = null;
				getLoopData(GlobalParam.LIST_LOAD_FIRST);
			}
		}
	};



	/*
	 * 实例化控件
	 */
	private void initCompent(){
		setRightTextTitleContent(R.drawable.back_btn,R.string.share,R.string.friends_loop);
		mLeftBtn.setOnClickListener(this);
		mRightTextBtn.setOnClickListener(this);


		mCommentBtn = (Button)findViewById(R.id.send);
		mCommentBtn.setOnClickListener(this);

		mBottomLayout = (RelativeLayout)findViewById(R.id.bottom_menu);
		mBottomLayout.setVisibility(View.GONE);

		mPicBtn = (ImageView)findViewById(R.id.pic);
		mPicBtn.setOnClickListener(this);
		mBottomMenuLayout = (LinearLayout)findViewById(R.id.comment_bottom_menu);

		mRefreshViewLastUpdated = (TextView) findViewById(R.id.pull_to_refresh_time);
		mContainer = (MyPullToRefreshListView)findViewById(R.id.container);
		mListView = mContainer.getList();
		mListView.setOnTouchListener(this);
		mListView.setDivider(getResources().getDrawable(R.drawable.splite));
		mListView.setCacheColorHint(0);
		mListView.setSelector(mContext.getResources().getDrawable(R.drawable.transparent_selector));
		mContainer.setOnChangeStateListener(this);
		mListView.setHeaderDividersEnabled(false);
		mSearchHeader = LayoutInflater.from(this).inflate(R.layout.friends_loop_header,null);
		mProfileHeaderIcon = (RoundImageView)mSearchHeader.findViewById(R.id.header_icon);
		mImageLoader.getBitmap(mContext, mProfileHeaderIcon,null, WeiYuanCommon.getLoginResult(mContext).headsmall,0,false,false,false);
		mHeaderBg = (ImageView)mSearchHeader.findViewById(R.id.img_bg);
		RelativeLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, ICON_SIZE_HEIGHT);
		mHeaderBg.setLayoutParams(params);
		mSetCoverHintTextView = (TextView)mSearchHeader.findViewById(R.id.set_cover_hint);
		String coverUrl = WeiYuanCommon.getLoginResult(mContext).cover;
		if(coverUrl!=null && !coverUrl.equals("")){
			mImageLoader.getBitmap(mContext, mHeaderBg, null, WeiYuanCommon.getLoginResult(mContext).cover, 0, false, false,false);

			mSetCoverHintTextView.setVisibility(View.GONE);
		}else{
			mSetCoverHintTextView.setVisibility(View.VISIBLE);
		}
		mLoginUserName = (TextView)mSearchHeader.findViewById(R.id.login_user_name);//
		mLoginUserName.setText(WeiYuanCommon.getLoginResult(mContext).nickname);
		mProfileHeaderIcon.setOnClickListener(this);
		mHeaderBg.setOnClickListener(this);
		
		if(mListView.getHeaderViewsCount() ==0  || mSearchHeader!=null){
			mListView.addHeaderView(mSearchHeader);
		}
		
		mAdapter = new FriendsLoopAdapter(mContext, mDataList, mHandler, mMetric);
		mListView.setAdapter(mAdapter);
		
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

				switch (scrollState) {  
				case OnScrollListener.SCROLL_STATE_FLING:  
					mAdapter.setFlagBusy(true);  
					break;  
				case OnScrollListener.SCROLL_STATE_IDLE: //加载更多
					mAdapter.setFlagBusy(false); 
					if (mBottomLayout.getVisibility() == View.VISIBLE) {
						mBottomLayout.setVisibility(View.GONE);
						mCommentEdit.setText("");
					}
					
					if ( view.getFirstVisiblePosition() == 0) {
						mHandler.sendEmptyMessage(GlobalParam.LIST_LOAD_REFERSH);
					}
					if (view.getLastVisiblePosition() == (view.getCount() - 1) ) {
						if(mMyAlbum == null || mMyAlbum.pageInfo == null){
							return;
						}
						if ( mMyAlbum.pageInfo.hasMore == 1) {
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
							getLoopData(GlobalParam.LIST_LOAD_MORE);

						}else{//没有更多数据时
							mHandler.sendEmptyMessage(GlobalParam.MSG_CHECK_STATE);
						}

					}
					
					if(mAdapter!=null){
						mAdapter.notifyDataSetChanged();
					}

					break;  
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:  
					mAdapter.setFlagBusy(true);  
					break;  
				default:  
					break;  
				}


			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				int beforeItem = firstVisibleItem - 2;
				if(beforeItem > 1){
					scrollRecycleBitmapCaches(1, beforeItem);
				}

				int endItem = firstVisibleItem + visibleItemCount + 2;
				if(endItem < totalItemCount-2){
					scrollRecycleBitmapCaches(endItem, totalItemCount-2);
				}
			}
		});

		
		mCommentEdit = (EditText)findViewById(R.id.edit);
		mCommentEdit.setOnFocusChangeListener(sendTextFocusChangeListener);
		mCommentEdit.setOnClickListener(sendTextClickListener);


		mViewPager = (ViewPager) findViewById(R.id.imagepager);
		mViewPager.setOnPageChangeListener(this);
		mLayoutCircle = (LinearLayout) findViewById(R.id.circlelayout);
		mEmotionLayout = (RelativeLayout) findViewById(R.id.emotionlayout);
		mEmotionLayout.setVisibility(View.GONE);

		mTotalEmotionList = getEmojiList();
		for (int i = 0; i < mTotalEmotionList.size(); i++) {
			addView(i);
		}

		mEmotionAdapter = new WeiYuanViewPagerAdapter(mViewList);
		mViewPager.setAdapter(mEmotionAdapter);
		mViewPager.setCurrentItem(0);
		showCircle(mViewList.size());

	}

	// 显示表情列表
	private void showEmojiGridView(){
		
		hideSoftKeyboard();
		mEmotionLayout.setVisibility(View.VISIBLE);
	}



	// 隐藏表情列表
	private void hideEmojiGridView(){
		mEmotionLayout.setVisibility(View.GONE);
	}


	private View.OnFocusChangeListener sendTextFocusChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if(hasFocus){
				// 文本框得到焦点，隐藏附加信息和表情列表
				hideEmojiGridView();
			}

		}
	};

	private View.OnClickListener sendTextClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// 获取到文本框的点击事件隐藏表情
			hideEmojiGridView();
		}
	};


	/*
	 * 获取朋友圈内容
	 */
	private void getLoopData(final int loadType){
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
					if (mMyAlbum!=null && mMyAlbum.pageInfo.currentPage == mMyAlbum.pageInfo.totalPage) {
						isExitsData = false;
					}
					int page = 0;
					if (loadType == GlobalParam.LIST_LOAD_FIRST) {
						page = 1;
					}else if(loadType == GlobalParam.LIST_LOAD_MORE){
						page = mMyAlbum.pageInfo.currentPage+1;
					}
					if (isExitsData) {
						//mMoving = QiyueCommon.getQiyueInfo().getMovingData(QiyueCommon.getUserId(mContext),page);
						mMyAlbum = WeiYuanCommon.getWeiYuanInfo().shareList(page);
						List<FriendsLoopItem> tempList = new ArrayList<FriendsLoopItem>();
					/*	if ((loadType == GlobalParam.LIST_LOAD_FIRST || loadType == GlobalParam.LIST_LOAD_REFERSH)
								&& mDataList!=null && mDataList.size()>0) {
							mDataList.clear();
						}*/
						if (mMyAlbum != null && mMyAlbum.childList!=null && mMyAlbum.childList.size() > 0) {
							isExitsData = true;
							tempList.addAll(mMyAlbum.childList); 
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
			
			// add listview first item 
			if(mListView.getHeaderViewsCount() ==0  || mSearchHeader!=null){
				mListView.addHeaderView(mSearchHeader);
			}
			
			
			// add listview last item
			boolean isLoadMore = (mMyAlbum!=null && mMyAlbum.pageInfo!=null && mMyAlbum.pageInfo.hasMore == 1)?true:false;
			if (isLoadMore) {
				if (mFootView == null) {
					mFootView = (LinearLayout) LayoutInflater.from(mContext)
							.inflate(R.layout.hometab_listview_footer, null);
				}
				if (mListView.getFooterViewsCount() == 0) {
					mListView.addFooterView(mFootView);	
				}
			}
			mAdapter = new FriendsLoopAdapter(mContext, mDataList,mHandler,mMetric);
			mListView.setAdapter(mAdapter);
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
			FriensLoopActivity.this.finish();
			break;
		case R.id.right_text_btn:
			Intent intent = new Intent();
			intent.setClass(mContext, SendMovingActivity.class);
			startActivity(intent);
			break;
		case R.id.img_bg:
			if (mListpic!=null && mListpic.size()>0) {
				mListpic.clear();
			}
			selectImg();
			break;
		case R.id.header_icon:
			Intent  myAlbumIntent = new Intent();
			myAlbumIntent.setClass(mContext, MyAlbumActivity.class);
			myAlbumIntent.putExtra("toUserID",WeiYuanCommon.getUserId(mContext));
			startActivity(myAlbumIntent);
			break;
		case R.id.pic:
			showEmojiGridView();
			mCommentEdit.setVisibility(View.VISIBLE);
			break;
		case R.id.send:
			mInputComment = mCommentEdit.getText().toString();
			if (mInputComment == null || mInputComment.equals("")) {
				Toast.makeText(mContext, mContext.getResources().getString(R.string.please_input_comment_contnet),Toast.LENGTH_LONG).show();
				return;
			}
			hideEmojiGridView();
			comment(mInputComment);


			break;
		default:
			break;
		}
	}

	/**
	 * 选择图片对话框
	 */
	private void selectImg(){
		MMAlert.showAlert(mContext,"",
				mContext.getResources().getStringArray(R.array.camer_item), 
				null, new OnAlertSelectId() {

			@Override
			public void onClick(int whichButton) {
				switch (whichButton) {
				case 0:
					getImageFromGallery();
					break;
				case 1:
					getImageFromCamera();
					break;
				default:
					break;
				}
			}
		});
	}


	/*
	 * 拍一张
	 */
	private void getImageFromCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		if(FeatureFunction.newFolder(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY)){
			File out = new File(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY, mTempFileName);
			Uri uri = Uri.fromFile(out);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

			startActivityForResult(intent, GlobalParam.REQUEST_GET_IMAGE_BY_CAMERA);
		}

	}

	/*
	 * 从相册中选取
	 */
	private void getImageFromGallery() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");

		startActivityForResult(intent, GlobalParam.REQUEST_GET_URI);
	}

	/*
	 * 处理选择的图片
	 */
	private void doChoose(final boolean isGallery, final Intent data) {
		if(isGallery){
			originalImage(data);
		}else {
			if(data != null){
				originalImage(data);
			}else{
				// Here if we give the uri, we need to read it
				String path = Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY+mTempFileName;
				startPhotoZoom(Uri.fromFile(new File(path)));
				//mImageFilePath = FeatureFunction.PUB_TEMP_DIRECTORY+TEMP_FILE_NAME;
				//ShowBitmap(false);
			}
		}
	}

	private void originalImage(Intent data) {
		/*
		 * switch (requestCode) {
		 */
		// case FLAG_CHOOSE:
		Uri uri = data.getData();
		if (uri != null) {
			//Log.d("may", "uri=" + uri + ", authority=" + uri.getAuthority());
			if (!TextUtils.isEmpty(uri.getAuthority())) {
				Cursor cursor = getContentResolver().query(uri,
						new String[] { MediaStore.Images.Media.DATA }, null, null,
						null);
				if (null == cursor) {
					Toast.makeText(mContext, R.string.no_found, Toast.LENGTH_SHORT).show();
					return;
				}
				cursor.moveToFirst();
				String imageFilePath = cursor.getString(cursor
						.getColumnIndex(MediaStore.Images.Media.DATA));
				Log.d("may", "path=" + imageFilePath);
				startPhotoZoom(uri);
				//ShowBitmap(false);
			} else {
				Log.d("may", "path=" + uri.getPath());
				Intent intent = new Intent(mContext,FriensLoopActivity.class);
				intent.putExtra("path", uri.getPath());
				startActivityForResult(intent, GlobalParam.REQUEST_GET_BITMAP);
				//mImageFilePath = uri.getPath();
				//ShowBitmap(false);
			}
		}
		else {
			Intent intent = new Intent(mContext, RotateImageActivity.class);
			intent.putExtra("path",Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY+mTempFileName);
			startActivityForResult(intent, GlobalParam.REQUEST_GET_BITMAP);
		}
	}

	/*
	 * 裁剪图片
	 */
	public void startPhotoZoom(Uri uri) {
		/*
		 * 至于下面这个Intent的ACTION是怎么知道的，大家可以看下自己路径下的如下网页
		 * yourself_sdk_path/docs/reference/android/content/Intent.html
		 * 直接在里面Ctrl+F搜：CROP ，之前小马没仔细看过，其实安卓系统早已经有自带图片裁剪功能,
		 * 是直接调本地库的，小马不懂C C++  这个不做详细了解去了，有轮子就用轮子，不再研究轮子是怎么
		 * 制做的了...吼吼
		 */


		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		//下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		intent.putExtra("scale", true);
		intent.putExtra("scaleUpIfNeeded", true);
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 3);
		intent.putExtra("aspectY", 2);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", /*ICON_SIZE_WIDTH*/720);
		intent.putExtra("outputY", /*ICON_SIZE_HEIGHT*/480);
		mCropImgPath = Environment.getExternalStorageDirectory() +  FeatureFunction.PUB_TEMP_DIRECTORY + "album.jpg";
		File file = new File(mCropImgPath);
		try {
			file.createNewFile();
		} catch (IOException e) {
		}
		//Uri imageUri = Uri.parse(mCropImgPath);
		Uri imageUri = Uri.fromFile(file);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		Log.e("startPhotoZoom", "width:"+ICON_SIZE_WIDTH+" height:"+ICON_SIZE_HEIGHT);
		intent.putExtra("return-data", false);
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, GlobalParam.REQUEST_GET_BITMAP);
	}


	/*
	 * 处理选择的图片
	 * (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case GlobalParam.REQUEST_GET_URI: 
			if (resultCode == RESULT_OK) {
				doChoose(true, data);
			}
			break;

		case GlobalParam.REQUEST_GET_IMAGE_BY_CAMERA:
			if(resultCode == RESULT_OK){
				doChoose(false, data);
			}
			break;
		case GlobalParam.REQUEST_GET_BITMAP:
			/*try {*/
			if(resultCode == RESULT_OK){
				Bundle extras = data.getExtras();
				//String path = data.getStringExtra("path");
				if(mCropImgPath != null && !mCropImgPath.equals("")){
					//if (extras != null) {
					String path = mCropImgPath;
					mHeaderBg.setImageBitmap(null);
					if(mBitmap != null && !mBitmap.isRecycled()){
						mBitmap.recycle();
						mBitmap = null;
					}

					//mBitmap = extras.getParcelable("data");
					mBitmap =BitmapFactory.decodeFile(path);
					if(mBitmap != null)
					{
						mHeaderBg.setImageBitmap(mBitmap);
					}
					File file = new File(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY + mTempFileName+".jpg");
					if(file != null && file.exists()){
						file.delete();
						file = null;
					}

					//mCropImgPath = FeatureFunction.saveTempBitmap(mBitmap, "album.jpg");
					showModifybgDialog();
				}
				//Log.e("FriendsLoopActivity", "REQUEST_GET_BITMAP");

			}

		

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/*
	 * 显示更换背景提示框
	 */
	private void showModifybgDialog(){

		AlertDialog.Builder builder = new AlertDialog.Builder(FriensLoopActivity.this);	
		builder.setIcon(R.drawable.ic_dialog_alert);
		builder.setTitle(mContext.getResources().getString(R.string.are_you_change_bg));
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//这里添加点击确定后的逻辑
				//showDialog("你选择了确定");
				uploadBg();
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//这里添加点击确定后的逻辑
				//showDialog("你选择了取消");
				if (mMyAlbum!=null && mMyAlbum.frontCover!=null && !mMyAlbum.frontCover.equals("")) {
					mImageLoader.getBitmap(mContext, mHeaderBg,null,mMyAlbum.frontCover, 0, false,false,false);
				}else{
					mHeaderBg.setImageResource(R.drawable.head_img);
				}

			}
		});
		builder.create().show();

	}

	/*
	 * 更换背景
	 */
	private void uploadBg(){
		if (!WeiYuanCommon.getNetWorkState()) {
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}

		new Thread(){
			public void run() {
				try {

					if (mCropImgPath !=null && !mCropImgPath.equals("")) {
						/*if (out.length() > 5 * Mb) {
							QiyueCommon.sendMsg(mHandler, MainActivity.MSG_EXCEPTION,
									mContext.getResources().getString(
											R.string.error_image_oversize));
							return;
						}*/
					}
					mListpic.add(new MorePicture("picture",mCropImgPath));
					WeiYuanCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG, mContext.getResources().getString(R.string.send_request));
					WeiYuanState status = WeiYuanCommon.getWeiYuanInfo().uploadUserBg(
							WeiYuanCommon.getUserId(mContext), mListpic);
					WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_UPLOAD_STATUS,status);
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				} catch (WeiYuanException e) {
					e.printStackTrace();
					WeiYuanCommon.sendMsg(mBaseHandler, GlobalParam.MSG_TICE_OUT_EXCEPTION,
							mContext.getResources().getString(e.getStatusCode()));
				}catch (Exception e) {
					e.printStackTrace();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				}
			};
		}.start();
	}



	
	/*
	 * 隐藏键盘
	 * (non-Javadoc)
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){  

			for (int i = 0; i < mDataList.size(); i++) {
				if(mDataList.get(i).showView == 1){
					mDataList.get(i).showView = 0;
				}
			}
			if(mAdapter!=null ){
				mAdapter.notifyDataSetChanged();
			}

			if(getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null){
				InputMethodManager manager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);  
			}  
			
			if (mBottomLayout.getVisibility() == View.VISIBLE) {
				mBottomLayout.setVisibility(View.GONE);
				mCommentEdit.setText("");
			}
			
			
		}  
		return super.onTouchEvent(event);  
	}


	/*
	 * 下拉刷新
	 * (non-Javadoc)
	 * @see com.weiyuan.widget.MyPullToRefreshListView.OnChangeStateListener#onChangeState(com.weiyuan.widget.MyPullToRefreshListView, int)
	 */
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

				List<FriendsLoopItem> tempList = (List<FriendsLoopItem>)msg.obj;
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

				mMyAlbum = null;
				getLoopData(GlobalParam.LIST_LOAD_REFERSH);
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
			case GlobalParam.MSG_UPLOAD_STATUS:
				WeiYuanState returnStatus = (WeiYuanState)msg.obj;
				if (returnStatus == null) {
					Toast.makeText(mContext, R.string.commit_data_error,Toast.LENGTH_LONG).show();
					return;
				}
				if (returnStatus.code !=0) {
					Toast.makeText(mContext, returnStatus.errorMsg,Toast.LENGTH_LONG).show();
					return;
				}
				Login login = WeiYuanCommon.getLoginResult(mContext);
				login.cover = returnStatus.frontCover;
				WeiYuanCommon.saveLoginResult(mContext, login);
				//x:y 217*144
				/*RelativeLayout.LayoutParams params  = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,ICON_SIZE_WIDTH*144/217);
				mHeaderBg.setLayoutParams(params);*/
				break;
			case GlobalParam.MSG_DEL_FRIENDS_LOOP:
				int delPos = msg.arg1;
				if(mDataList!=null && mDataList.size()>0){
					mShareId = mDataList.get(delPos).id;
					mToUserId = mDataList.get(delPos).uid;
					mPosition = delPos;
					createDialog(mContext, mContext.getResources().getString(R.string.del_friends_loop_hint),
							mContext.getResources().getString(R.string.ok),mShareId);
				}

				break;
			case  GlobalParam.MSG_CHECK_FRIENDS_LOOP_POP_STATUS:

				for (int i = 0; i < mDataList.size(); i++) {
					if(mDataList.get(i).showView == 1){
						mDataList.get(i).showView = 0;
					}
				}
				int pos = msg.arg1;
				mDataList.get(pos).showView = 1;
				if(mAdapter!=null ){
					mAdapter.notifyDataSetChanged();
				}
				break;
			case GlobalParam.MSG_SHOW_BOTTOM_COMMENT_MENU:
				for (int i = 0; i < mDataList.size(); i++) {
					if(mDataList.get(i).showView == 1){
						mDataList.get(i).showView = 0;
					}
				}
				if(mAdapter!=null ){
					mAdapter.notifyDataSetChanged();
				}

				int commentIndex = msg.arg1;
				
				FriendsLoopItem commentMvoing = mDataList.get(commentIndex);
				mBottomLayout.setVisibility(View.VISIBLE);

				TranslateAnimation animation = new TranslateAnimation(mMetric.widthPixels, 0, 0, 0);
				animation.setDuration(500);
				animation.setAnimationListener(mAnimationListener);
				mBottomLayout.startAnimation(animation);

				mShareId = commentMvoing.id;
				mToUserId = commentMvoing.uid;
				mPosition = commentIndex;
				break;
			case GlobalParam.MSG_SHOW_FRIENDS_FAVORITE_DIALOG:
				FriendsLoopItem item = (FriendsLoopItem) msg.obj;
				int type = msg.arg1;  //1收藏文本 2收藏图片
				int picIndex = msg.arg2;
				mShareId = item.id;
				mToUserId = item.uid;
				showFavoriteDialog(type,item,picIndex);
				break;
			case GlobalParam.MSG_COMMINT_ZAN:
				int zanIndex = msg.arg1;
				if (zanIndex < 0) {
					return;
				}
				mPosition = zanIndex;
				FriendsLoopItem zanMvoing = mDataList.get(zanIndex);
				mShareId = zanMvoing.id;
				zan();
				break;
			case GlobalParam.MSG_COMMENT_STATUS:
				WeiYuanState commentResult = (WeiYuanState)msg.obj;
				if(commentResult == null){
					Toast.makeText(mContext, "提交数据失败!", Toast.LENGTH_LONG).show();
					return;
				}
				if( commentResult.code!=0){
					Toast.makeText(mContext, commentResult.errorMsg, Toast.LENGTH_LONG).show();
					return;
				}
				if(mDataList!=null && mDataList.size()>=mPosition){
					if(mDataList.get(mPosition).replylist == null){
						mDataList.get(mPosition).replylist = new ArrayList<CommentUser>();
					}
					mDataList.get(mPosition).replylist.add(new CommentUser(mShareId, WeiYuanCommon.getUserId(mContext),
							WeiYuanCommon.getLoginResult(mContext).nickname,
							mToUserId,null,mInputComment, System.currentTimeMillis()/1000));

					if(mAdapter!=null){
						mAdapter.setData(mDataList);
						mAdapter.notifyDataSetChanged();
					}
				}



				mInputComment = "";
				mCommentEdit.setText("");
				mShareId = 0;
				mToUserId = "";
				mPosition = 0;
				if (mBottomLayout.getVisibility() == View.VISIBLE) {
					mBottomLayout.setVisibility(View.GONE);
				}
				if(mAdapter!=null){
					mAdapter.notifyDataSetChanged();
				}
				break;
			case GlobalParam.MSG_ZAN_STATUS:
				WeiYuanState zanResult = (WeiYuanState)msg.obj;
				if(zanResult == null){
					Toast.makeText(mContext, R.string.commit_dataing, Toast.LENGTH_LONG).show();
					return;
				}
				if(zanResult.code!=0){
					Toast.makeText(mContext, zanResult.errorMsg, Toast.LENGTH_LONG).show();
					return;
				}
				if(zanResult.friendsLoopitem!=null){
					if(mDataList!=null && mDataList.size()>=mPosition){
						mDataList.remove(mPosition);
						mDataList.add(mPosition, zanResult.friendsLoopitem);
					}
				}

				for (int i = 0; i < mDataList.size(); i++) {
					if(mDataList.get(i).showView == 1){
						mDataList.get(i).showView = 0;
					}
				}
				if(mAdapter!=null ){
					mAdapter.notifyDataSetChanged();
				}

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
			case GlobalParam.MSG_CHECK_DEL_SHARE_STATUS:
				WeiYuanState delStatus = (WeiYuanState)msg.obj;
				if(delStatus == null){
					Toast.makeText(mContext, R.string.commit_data_error,Toast.LENGTH_LONG).show();
					return;
				}
				if(delStatus.code !=0){
					Toast.makeText(mContext, delStatus.errorMsg,Toast.LENGTH_LONG).show();
					return;
				}
				mDataList.remove(mPosition);
				if(mAdapter!=null){
					mAdapter.notifyDataSetChanged();
				}
				List<NotifiyVo> messageList = new ArrayList<NotifiyVo>();
				List<NotifiyVo> tempMsgList = WeiYuanCommon.getMovingResult(mContext);
				if(tempMsgList!=null && tempMsgList.size()>0){
					for (NotifiyVo notifiyVo : tempMsgList) {
						if(notifiyVo.shareId != mShareId){
							messageList.add(notifiyVo);
						}
					}
				
					WeiYuanCommon.saveMoving(mContext, messageList);
				}
				break;
			default:
				break;
			}
		}

	};

	/*
	 * 评论
	 */
	private void comment(final String content){
		if (!WeiYuanCommon.getNetWorkState()) {
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread(){
			public void run() {
				try {

					WeiYuanCommon.sendMsg(mBaseHandler,BASE_SHOW_PROGRESS_DIALOG,
							mContext.getResources().getString(R.string.send_request));
					WeiYuanState status = WeiYuanCommon.getWeiYuanInfo().shareReply(mShareId,mToUserId,content);
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
					WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_COMMENT_STATUS,status);
				} catch (WeiYuanException e) {
					e.printStackTrace();
					WeiYuanCommon.sendMsg(mBaseHandler, BASE_MSG_TIMEOUT_ERROR,
							mContext.getResources().getString(e.getStatusCode()));
				}catch (Exception e) {
					e.printStackTrace();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				}
			};
		}.start();
	}

	/*
	 * 赞
	 */
	private void zan(){
		if (!WeiYuanCommon.getNetWorkState()) {
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread(){
			public void run() {
				try {
					WeiYuanCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG,
							mContext.getResources().getString(R.string.send_request));
					WeiYuanState status = WeiYuanCommon.getWeiYuanInfo().sharePraise(mShareId);
					if(status !=null && status.code == 0){
						FriendsLoopItem shareDetail = WeiYuanCommon.getWeiYuanInfo().shareDetail(mShareId);
						if(shareDetail !=null){
							status.friendsLoopitem = shareDetail;
						}
					}
					WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_ZAN_STATUS,status);
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



	/*
	 * 收藏图片
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
					WeiYuanState status = WeiYuanCommon.getWeiYuanInfo().favoreiteMoving(mToUserId, null, favoriteContent);
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

	/*
	 * 删除分享
	 */
	private void delShare(final int shareId){
		if (!WeiYuanCommon.getNetWorkState()) {
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread(){
			public void run() {
				try {
					WeiYuanCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG, 
							mContext.getResources().getString(R.string.send_request));
					WeiYuanState status = WeiYuanCommon.getWeiYuanInfo().deleteShare(shareId);
					WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_CHECK_DEL_SHARE_STATUS, status);
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				} catch (NotFoundException e) {
					e.printStackTrace();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				} catch (WeiYuanException e) {
					e.printStackTrace();
					WeiYuanCommon.sendMsg(mBaseHandler, BASE_MSG_TIMEOUT_ERROR, 
							mContext.getResources().getString(e.getStatusCode()));
				}catch (Exception e) {
					e.printStackTrace();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				}
			};
		}.start();

	}


	/*
	 * 滑动删除图片
	 */
	private void scrollRecycleBitmapCaches(	int start, int end){                
		for(int i = start; i < end; i++){
			if(mDataList.get(i).listpic != null && mDataList.get(i).listpic.size() != 0){

				if(mAdapter != null && mAdapter.getImageBuffer() != null){
					LinearLayout picLayout = (LinearLayout) mListView.findViewWithTag(mDataList.get(i).id);
					for (int j = 0; j < mDataList.get(i).listpic.size(); j++) {

						if(picLayout != null){
							ImageView imageView = (ImageView) picLayout.findViewWithTag(mDataList.get(i).listpic.get(j).smallUrl);
							if(imageView != null){
								imageView.setImageBitmap(null);
								imageView.setImageResource(R.drawable.normal);
							}
						}

						Bitmap bitmap = mAdapter.getImageBuffer().get(mDataList.get(i).listpic.get(j).smallUrl);
						if (bitmap != null && !bitmap.isRecycled()) {
							bitmap.recycle();
							bitmap = null;
							mAdapter.getImageBuffer().remove(mDataList.get(i).listpic.get(j).smallUrl);
						}

					}
				}

			}
		}

	}


	/**
	 * 
	 * @param FriendsLoopItem 
	 * @param type 1-收藏或取消收藏文本 2-收藏或取消收藏图片
	 */
	private void showFavoriteDialog(final int type,final FriendsLoopItem item,final int picIndex) {
		if(item == null ){
			return;
		}
		String[] items ;
		if(item.favorite == 1){
			items =  new String[] { getString(R.string.cancle_favorite)};
		}else{
			items =  new String[] { getString(R.string.favorite)};
		}


		AlertDialog.Builder builder = new AlertDialog.Builder(
				FriensLoopActivity.this)
		.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {


				switch (type) {
				case 1:
					MovingContent movingContent = new MovingContent(item.content,MessageType.TEXT+"");
					favoriteMoving(MovingContent.getInfo(movingContent));
					break;
				case 2:
					if(picIndex!=-1 && (item.listpic!=null && item.listpic.size()>0)){
						MovingPic pic = new MovingPic(item.listpic.get(picIndex).originUrl,item.listpic.get(picIndex).smallUrl, MessageType.PICTURE+"");
						favoriteMoving(MovingPic.getInfo(pic));
					}
					break;

				default:
					break;
				}
				if (mUpgradeNotifyDialog != null) {
					mUpgradeNotifyDialog.dismiss();
				}
			}
		});
		mUpgradeNotifyDialog = builder.show();
	}

	AnimationListener mAnimationListener = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			mBottomLayout.setVisibility(View.VISIBLE);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			mBottomLayout.clearAnimation();
			mCommentEdit.setFocusable(true); 
			mCommentEdit.setFocusableInTouchMode(true);
			mCommentEdit.requestFocus();
			InputMethodManager inputManager =(InputMethodManager)mCommentEdit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE); 
			inputManager.showSoftInput(mCommentEdit, 0);

		}
	};

	/**
	 * 添加表情滑动控件
	 * @param i					添加的位置
	 */
	private void addView(final int i){
		View view = LayoutInflater.from(mContext).inflate(R.layout.emotion_gridview, null);
		GridView gridView = (GridView) view.findViewById(R.id.emoji_grid);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position < mTotalEmotionList.get(i).size() - 1){
					ImageView imageView = (ImageView)view.findViewById(R.id.emotion);
					if(imageView != null){
						Drawable drawable = imageView.getDrawable();
						if(drawable instanceof BitmapDrawable){
							Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
							String name = mTotalEmotionList.get(i).get(position);

							Drawable mDrawable = new BitmapDrawable(getResources(), bitmap);
							int width = getResources().getDimensionPixelSize(R.dimen.pl_emoji);
							int height = width;
							mDrawable.setBounds(0, 0, width > 0 ? width : 0, height > 0 ? height : 0); 
							ImageSpan span = new ImageSpan(mDrawable);

							SpannableString spannableString = new SpannableString("[" + name + "]");
							//类似于集合中的(start, end)，不包括起始值也不包括结束值。
							// 同理，Spannable.SPAN_INCLUSIVE_EXCLUSIVE类似于 [start，end)
							spannableString.setSpan(span, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
							Editable dEditable = mCommentEdit.getEditableText();
							int index = mCommentEdit.getSelectionStart();
							dEditable.insert(index, spannableString);
						}
					}
				}else {
					int index = mCommentEdit.getSelectionStart();

					String text = mCommentEdit.getText().toString();  
					if (index > 0) {  
						String text2 = text.substring(index - 1);  
						if ("]".equals(text2)) {  
							int start = text.lastIndexOf("[");  
							int end = index;  
							mCommentEdit.getText().delete(start, end);  
							return;  
						}  
						mCommentEdit.getText().delete(index - 1, index);  
					}  
				}
			}

		});
		gridView.setAdapter(new EmojiAdapter(mContext, mTotalEmotionList.get(i), WeiYuanCommon.mScreenWidth));
		mViewList.add(view);
	}


	/**
	 * 显示表情处于第几页标志
	 * @param size
	 */
	private void showCircle(int size){
		mLayoutCircle.removeAllViews();

		for( int i = 0; i < size; i++){
			ImageView img = new ImageView(mContext);
			img.setLayoutParams(new LinearLayout.LayoutParams(FeatureFunction.dip2px(mContext, 5), FeatureFunction.dip2px(mContext, 5)));
			LinearLayout layout = new LinearLayout(mContext);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			int margin = FeatureFunction.dip2px(mContext, 5);
			params.setMargins(margin, 0, margin, 0);
			layout.setLayoutParams(params);
			layout.addView(img);
			//img.setLayoutParams()
			if ( mPageIndxe == i){
				img.setImageResource(R.drawable.circle_d);
			} else{
				img.setImageResource(R.drawable.circle_n);
			}
			mLayoutCircle.addView(layout);
		}
	}


	/**
	 * 获取表情列表
	 * @return
	 * @Mr.Sam <br />
	 * 创建时间:2013-6-21<br />
	 * 修改时间:<br />
	 */
	private List<List<String>> getEmojiList() {
		List<String> emojiList = new ArrayList<String>();
		String baseName = "emoji_";
		for (int i = 85; i <= 88; i++) {
			emojiList.add(baseName + i);
		}

		for (int i = 340; i <= 363; i++) {
			emojiList.add(baseName + i);
		}

		for (int i = 94; i <= 101; i++) {
			emojiList.add(baseName + i);
		}

		for (int i = 115; i <= 117; i++) {
			emojiList.add(baseName + i);
		}

		for (int i = 364; i <= 373; i++) {
			emojiList.add(baseName + i);
		}

		for (int i = 12; i <= 17; i++) {
			emojiList.add(baseName + i);
		}

		for (int i = 0; i <= 11; i++) {
			emojiList.add(baseName + i);
		}

		for (int i = 18; i <= 84; i++) {
			emojiList.add(baseName + i);
		}

		for (int i = 89; i <= 93; i++) {
			emojiList.add(baseName + i);
		}

		for (int i = 101; i <= 114; i++) {
			emojiList.add(baseName + i);
		}

		for (int i = 114; i <= 339; i++) {
			emojiList.add(baseName + i);
		}

		List<List<String>> totalList = new ArrayList<List<String>>();
		int page = emojiList.size() % 20 ==0 ? emojiList.size() / 20 : emojiList.size() / 20 + 1;
		for (int i = 0; i < page; i++) {
			int startIndex = i * 20;
			List<String> singleList = new ArrayList<String>();
			if(singleList != null){
				singleList.clear();
			}
			int endIndex = 0;
			if(i < page - 1){
				endIndex = startIndex + 20;
			}else if(i == page - 1){
				endIndex = emojiList.size() - 1;
			}

			singleList.addAll(emojiList.subList(startIndex, endIndex));
			singleList.add("delete_emotion_btn");
			totalList.add(singleList);

		}

		return totalList;
	}





	public void hideSoftKeyboard(){
		hideSoftKeyboard(getCurrentFocus());
	}
	public void hideSoftKeyboard(View view){
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if(view != null){
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		

		InputMethodManager manager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		manager.hideSoftInputFromWindow(mCommentEdit.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	
		if(mBottomLayout.getVisibility() == View.VISIBLE){
			mBottomLayout.setVisibility(View.GONE);
			mCommentEdit.setText("");
		}
	}
	
	
	@Override
	protected void onDestroy() {
		WeiYuanCommon.saveReadFriendsLoopTip(mContext, true);
		Intent hideIntent = new Intent(GlobalParam.ACTION_HIDE_FOUND_NEW_TIP);
		hideIntent.putExtra("found_type",1);
		sendBroadcast(hideIntent);
		sendBroadcast(new Intent(GlobalParam.ACTION_HIDE_NEW_FRIENDS_LOOP));
		unregisterReceiver(Receiver);
		super.onDestroy();
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int position) {
		mPageIndxe = position;
		showCircle(mViewList.size());
	}
	/**
	 * 
	 * @param context
	 * @param cardTitle
	 * @param type 1-加入黑名单 2-添加好友申请 
	 * @param okTitle
	 */
	protected void createDialog(Context context, String cardTitle,final String okTitle,final int shareId) {
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
				delShare(shareId);
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





}
