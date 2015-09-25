package com.chengxin;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chengxin.R;
import com.chengxin.Entity.Login;
import com.chengxin.Entity.MapInfo;
import com.chengxin.Entity.MorePicture;
import com.chengxin.Entity.UploadImg;
import com.chengxin.Entity.WeiYuanState;
import com.chengxin.adapter.UploadPicAdapter;
import com.chengxin.global.FeatureFunction;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.net.WeiYuanException;
import com.chengxin.widget.MyGridView;
 

/**
 * 发布一条动态信息
 * 
 * @author dongli
 * 
 */
public class SendMovingActivity extends PhotoBaseActivity implements OnClickListener, OnItemClickListener {
	public static final int MSG_SHOW_IMAGE = 0x00023;
	private static final int REQUEST_GET_URI = 101;
	public static final int REQUEST_GET_BITMAP = 102;
	public static final int REQUEST_GET_IMAGE_BY_CAMERA = 103;
	private static final int RESQUEST_CODE = 100;
	private static final int RESQUEST_AREA_CODE = 104;

	private EditText mContentEdit;

	private int IMAGE_MAX = 6;
	private MyGridView mGridView;
	private UploadPicAdapter mAdapter;
	private List<UploadImg> mImageList = new ArrayList<UploadImg>();
	private DisplayMetrics mMetrics;
	private int mWidth = 0;
	private ImageView mLocationIcon, mAreaIcon;
	private TextView mLocationAddress;
	private LinearLayout mLocationLayout, mAreaLayout;

	private String mInputContetn;

	private String mLat, mLng, mAddress, mAreaUid;

	private String mJumpImageUrl;
	private List<Login> mSelectUserList;

	private int mCheckId = 0;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {

			case GlobalParam.MSG_CHECK_STATE:
				WeiYuanState status = (WeiYuanState) msg.obj;
				if (status == null) {
					Toast.makeText(mContext, R.string.commit_data_error, Toast.LENGTH_LONG).show();
					return;
				}

				String hintMsg = status.errorMsg;

				if (status.code != 0) {
					if (hintMsg == null || hintMsg.equals("")) {
						hintMsg = mContext.getResources().getString(R.string.send_moving_error);
					}
					Toast.makeText(mContext, hintMsg, Toast.LENGTH_LONG).show();
					return;
				}

				if (hintMsg == null || hintMsg.equals("")) {
					hintMsg = mContext.getResources().getString(R.string.send_moving_success);
				}
				Toast.makeText(mContext, hintMsg, Toast.LENGTH_LONG).show();
				SendMovingActivity.this.finish();
				sendBroadcast(new Intent(FriensLoopActivity.MSG_REFRESH_MOVIINF));
				break;
			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_moving);
		mContext = this;
		mJumpImageUrl = getIntent().getStringExtra("moving_url");
		mMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
		mWidth = mMetrics.widthPixels;
		initCompent();
	}

	private void initCompent() {
		setRightTextTitleContent(R.drawable.back_btn, R.string.send, R.string.share);
		mLeftBtn.setOnClickListener(this);
		mRightTextBtn.setOnClickListener(this);

		mContentEdit = (EditText) findViewById(R.id.content);

		mContentEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				mContentEdit.invalidate();
				mContentEdit.requestLayout();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				int length = s.length();
				updateWordCount(length);
			}

		});

		mLocationIcon = (ImageView) findViewById(R.id.location_icon);
		mAreaIcon = (ImageView) findViewById(R.id.area_icon);
		mAreaLayout = (LinearLayout) findViewById(R.id.area_layout);
		mLocationLayout = (LinearLayout) findViewById(R.id.loacation_layout);
		mLocationLayout.setOnClickListener(this);
		mAreaLayout.setOnClickListener(this);

		mLocationAddress = (TextView) findViewById(R.id.loaction_addr);

		mGridView = (MyGridView) findViewById(R.id.gridview);
		mGridView.setOnItemClickListener(this);

		if (mJumpImageUrl != null && !mJumpImageUrl.equals("")) {
			mImageList.add(new UploadImg(mJumpImageUrl, 0));
		}
		mImageList.add(new UploadImg("", 1));

		mAdapter = new UploadPicAdapter(mContext, mImageList, mWidth);
		mGridView.setAdapter(mAdapter);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mImageList != null) {
			for (int i = 0; i < mImageList.size(); i++) {
				deleteImgFile(mImageList.get(i).mPicPath);
				if (!TextUtils.isEmpty(mImageList.get(i).mPicPath)) {
					ImageView view = (ImageView) mGridView.findViewWithTag(mImageList.get(i).mPicPath);
					if (view != null) {
						view.setImageBitmap(null);
					}
				}
			}
		}

		if (mAdapter != null) {
			FeatureFunction.freeBitmap(mAdapter.getImageBuffer());
		}
	}

	private boolean checkText() {
		boolean isCheck = true;
		String hinMsg = "";
		mInputContetn = mContentEdit.getText().toString();

		if ((mInputContetn == null || mInputContetn.equals("")) && mImageList.size() == 1) {
			isCheck = false;
			hinMsg = mContext.getResources().getString(R.string.please_wirte_content);
		}

		if (!isCheck && hinMsg != null && !hinMsg.equals("")) {
			Toast.makeText(mContext, hinMsg, Toast.LENGTH_LONG).show();
		}
		return isCheck;
	}

	private void sendMoving() {
		if (!WeiYuanCommon.getNetWorkState()) {
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		if (!checkText()) {
			return;
		}
		new Thread() {
			public void run() {
				try {
					WeiYuanCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG, "正在提交数据,请稍后...");
					List<MorePicture> picList = null;
					if (mImageList != null && mImageList.size() > 0) {
						picList = new ArrayList<MorePicture>();

						for (int i = 0; i < mImageList.size(); i++) {
							if (mImageList.get(i).mType != 1) {
								String key = "picture";
								if (i > 0) {
									int index = i + 1;
									key = key + index;
								}
								picList.add(new MorePicture(key, mImageList.get(i).mPicPath));
							}

						}
					}
					WeiYuanState status = WeiYuanCommon.getWeiYuanInfo().addShare(picList, mInputContetn, mLng, mLat, mAddress, mAreaUid);

					WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_CHECK_STATE, status);
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				} catch (WeiYuanException e) {
					e.printStackTrace();
					WeiYuanCommon.sendMsg(mBaseHandler, BASE_MSG_TIMEOUT_ERROR, mContext.getResources().getString(e.getStatusCode()));
				} catch (Exception e) {
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
			SendMovingActivity.this.finish();
			break;
		case R.id.loacation_layout:
			Intent intent = new Intent(this, LocationActivity.class);
			startActivityForResult(intent, RESQUEST_CODE);
			break;
		case R.id.area_layout:
			Intent areaIntent = new Intent(this, AreaActivity.class);
			if (mSelectUserList != null && mSelectUserList.size() > 0) {
				areaIntent.putExtra("userlist", (Serializable) mSelectUserList);
			}
			areaIntent.putExtra("checkId", mCheckId);
			startActivityForResult(areaIntent, RESQUEST_AREA_CODE);
			break;
		case R.id.right_text_btn:// 发送一条动态信息
			sendMoving();
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case 1:
			if (data != null && resultCode == 2) {
				List<UploadImg> imgList = (List<UploadImg>) data.getSerializableExtra("img_list");
				if (imgList != null && imgList.size() > 0) {
					if (mImageList != null && mImageList.size() > 0) {
						mImageList.clear();
					}
					mImageList.addAll(imgList);
					mAdapter.notifyDataSetChanged();
				}
			}
			break;
		case RESQUEST_CODE:
			if (data != null && RESULT_OK == resultCode) {
				Bundle bundle = data.getExtras();
				if (bundle != null) {

					MapInfo mapInfo = (MapInfo) data.getSerializableExtra("mapInfo");
					if (mapInfo == null) {
						mLocationIcon.setBackgroundResource(R.drawable.share_location_icon);
						Toast.makeText(mContext, mContext.getString(R.string.get_location_failed), Toast.LENGTH_SHORT).show();
						mLocationAddress.setText(mContext.getResources().getString(R.string.location_current));
						return;
					}
					mLocationAddress.setText(mapInfo.getAddr());
					mLat = mapInfo.getLat();
					mLng = mapInfo.getLng();
					mLocationIcon.setBackgroundResource(R.drawable.share_location_icon_check);
					mAddress = mapInfo.getAddr();

				}
			}
			break;
		case RESQUEST_AREA_CODE:
			if (data != null && RESULT_OK == resultCode) {
				Bundle bundle = data.getExtras();
				mAreaUid = "";
				if (bundle != null) {
					mAreaUid = data.getStringExtra("area_uid");
					mCheckId = data.getIntExtra("checkId", 0);
					if (mCheckId == 1 && (mAreaUid != null && !mAreaUid.equals("") && !mAreaUid.startsWith(",") && !mAreaUid.endsWith(","))) {
						mAreaIcon.setBackgroundResource(R.drawable.share_area_check);
					} else {
						mAreaIcon.setBackgroundResource(R.drawable.share_area);
					}
					mSelectUserList = (List<Login>) data.getSerializableExtra("userlist");
				}
			}
			break;
		case REQUEST_GET_URI:
			if (resultCode == RESULT_OK) {
				doChoose(true, data, false, WeiYuanCommon.getCamerUrl(mContext), 0, 0);
			}

			break;

		case REQUEST_GET_IMAGE_BY_CAMERA:
			if (resultCode == RESULT_OK) {
				doChoose(false, data, false, WeiYuanCommon.getCamerUrl(mContext), 0, 0);
			}
			break;

		case REQUEST_GET_BITMAP:
			if (resultCode == RESULT_OK) {
				String path = data.getStringExtra("path");
				if (!TextUtils.isEmpty(path)) {
					// 一张图片不能选择多次
					/*
					 * boolean isExist = false; if(mImageList.size() != 0){ for (int i = 0; i < mImageList.size(); i++) { if(mImageList.get(i).mType == 0 && mImageList.get(i).mPicPath.equals(path)){ isExist = true; break; } } }
					 * 
					 * if(!isExist){
					 */
					mImageList.add(mImageList.size() - 1, new UploadImg(path, 0));
					if (mImageList.size() - 1 == IMAGE_MAX) {
						mImageList.remove(mImageList.size() - 1);
					}
					mAdapter.notifyDataSetChanged();
					/*
					 * }
					 */
				}

			}

			break;
 

		case GlobalParam.REQUEST_GET_IMAGE_BY_CAMERA:
			if(resultCode == RESULT_OK){
				doChoose(false, data, false, WeiYuanCommon.getCamerUrl(mContext), 0, 0);
			}
			break;

		case GlobalParam.REQUEST_GET_BITMAP:
			if (resultCode == RESULT_OK) {
				String path = data.getStringExtra("path");
				if (!TextUtils.isEmpty(path)) {
					// 一张图片不能选择多次
					/*
					 * boolean isExist = false; if(mImageList.size() != 0){ for (int i = 0; i < mImageList.size(); i++) { if(mImageList.get(i).mType == 0 && mImageList.get(i).mPicPath.equals(path)){ isExist = true; break; } } }
					 * 
					 * if(!isExist){
					 */
					mImageList.add(mImageList.size() - 1, new UploadImg(path, 0));
					if (mImageList.size() - 1 == IMAGE_MAX) {
						mImageList.remove(mImageList.size() - 1);
					}

					mAdapter.notifyDataSetChanged();
					/*
					 * }
					 */
				}

			}

			break;

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void originalImage(Intent data) {
		/*
		 * switch (requestCode) {
		 */
		// case FLAG_CHOOSE:
		Uri uri = data.getData();
		// Log.d("may", "uri=" + uri + ", authority=" + uri.getAuthority());
		if (!TextUtils.isEmpty(uri.getAuthority())) {
			Cursor cursor = getContentResolver().query(uri, new String[] { MediaStore.Images.Media.DATA }, null, null, null);
			if (null == cursor) {
				// Toast.makeText(mContext, R.string.no_found, Toast.LENGTH_SHORT).show();
				return;
			}
			cursor.moveToFirst();
			String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
			Log.d("may", "path=" + path);
			String extension = path.substring(path.lastIndexOf("."), path.length());
			if (FeatureFunction.isPic(extension)) {
				Intent intent = new Intent(mContext, RotateImageActivity.class);
				intent.putExtra("path", path);
				startActivityForResult(intent, REQUEST_GET_BITMAP);

				// startPhotoZoom(data.getData());

			} else {
				// Toast.makeText(mContext, R.string.please_choose_pic, Toast.LENGTH_SHORT).show();
			}
			// ShowBitmap(false);

		} else {
			Log.d("may", "path=" + uri.getPath());
			String path = uri.getPath();
			String extension = path.substring(path.lastIndexOf("."), path.length());
			if (FeatureFunction.isPic(extension)) {
				Intent intent = new Intent(mContext, RotateImageActivity.class);
				intent.putExtra("path", path);
				startActivityForResult(intent, REQUEST_GET_BITMAP);
			} else {
				// Toast.makeText(mContext, R.string.please_choose_pic, Toast.LENGTH_SHORT).show();
			}
			// mImageFilePath = uri.getPath();
			// ShowBitmap(false);
		}
	}

	private void updateWordCount(int length) {
		/*
		 * if(mWordLimitText != null){ length = WORD_LIMIT - length; mWordLimitText.setText(String.valueOf(length)); }
		 */
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (arg2 < mImageList.size()) {
			if (mImageList.get(arg2).mType == 0) {
				if (mAdapter.getIsDelete()) {
					HashMap<String, Bitmap> hashMap = mAdapter.getImageBuffer();
					String path = mImageList.get(arg2).mPicPath;
					ImageView view = (ImageView) mGridView.findViewWithTag(path);
					mImageList.remove(arg2);
					if (view != null) {
						view.setImageBitmap(null);
					}
					if (hashMap.get(path) != null) {
						Bitmap bitmap = hashMap.get(path);
						if (bitmap != null && !bitmap.isRecycled()) {
							bitmap.recycle();
							bitmap = null;
						}

						hashMap.remove(path);
					}
					deleteImgFile(path);
					if (mImageList.get(mImageList.size() - 1).mType != 1) {
						mImageList.add(new UploadImg("", 1));
					}
					mAdapter.notifyDataSetChanged();
				} else {
					Intent showImageIntent = new Intent();
					showImageIntent.setClass(mContext, ShowImageActivity.class);
					showImageIntent.putExtra("type", 1);
					showImageIntent.putExtra("pos", arg2);
					showImageIntent.putExtra("img_list", (Serializable) mImageList);
					startActivityForResult(showImageIntent, 1);
				}

			} else if (mImageList.get(arg2).mType == 1) {
				if (mAdapter.getIsDelete()) {
					mAdapter.setIsDelete(false);
					mAdapter.notifyDataSetChanged();
				} else {
					if (mImageList.size() - 1 >= IMAGE_MAX) {
						Toast.makeText(mContext, mContext.getString(R.string.upload_image_max), Toast.LENGTH_SHORT).show();
						return;
					}
					selectImg();
				}

			}
		} else {
			if (mAdapter.getIsDelete()) {
				mAdapter.setIsDelete(false);
				mAdapter.notifyDataSetChanged();
			}
		}
	}

	/*
	 * @Override public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) { if(arg2 < mImageList.size()){ if(mImageList.get(arg2).mType == 0){ if(!mAdapter.getIsDelete()){ mAdapter.setIsDelete(true); mAdapter.notifyDataSetChanged(); } return true; } }
	 * 
	 * return false; }
	 */

	private void deleteImgFile(String path) {
		File file = new File(path);
		if (file != null && file.exists()) {
			file.delete();
		}
	}

}
