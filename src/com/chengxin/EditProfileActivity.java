package com.chengxin;

import java.io.File;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chengxin.R;
import com.chengxin.Entity.Login;
import com.chengxin.Entity.LoginResult;
import com.chengxin.dialog.MMAlert;
import com.chengxin.dialog.MMAlert.OnAlertSelectId;
import com.chengxin.global.FeatureFunction;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.GlobleType;
import com.chengxin.global.ImageLoader;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.net.WeiYuanException;
 
 

@SuppressLint("NewApi")
public class EditProfileActivity extends BaseActivity implements OnClickListener {

	/*
	 * 定义全局变量
	 */
	private static final String TEMP_FILE_NAME = "header.jpg";
	private RelativeLayout mHeaderLayout, mAddrLayout, mSexLayout, mSignLayout, mNickNameLayout;

	private TextView mSexTextView, mAddrTextView, mSiTextView, mNickNameTextView;
	private ImageView mImageView;

	private String mInputNickName, mInputAddr, mInputSign;
	private int mInputSex = 2;

	/**
	 * // 省id
	 */
	private String mProvice;
	/**
	 * //市id
	 */
	private String mCity;

	private int mType;

	private Login mLogin;
	private Bitmap mBitmap;
	private String mImageFilePath;
	private ImageLoader mImageLoader;

	/*
	 * 处理消息
	 */
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GlobalParam.MSG_CHECK_STATE:
				LoginResult loginResult = (LoginResult) msg.obj;
				if (loginResult == null) {
					Toast.makeText(mContext, "提交数据失败...", Toast.LENGTH_LONG).show();
					return;
				}
				if (loginResult.mState.code != 0) {
					Toast.makeText(mContext, loginResult.mState.errorMsg, Toast.LENGTH_LONG).show();
					return;
				}
				Login login = loginResult.mLogin;
				login.password = WeiYuanCommon.getLoginResult(mContext).password;
				String oldheadUrl = WeiYuanCommon.getLoginResult(mContext).headsmall;
				String newHeadUrl = login.headsmall;
				WeiYuanCommon.saveLoginResult(mContext, login);
				setResult(RESULT_OK);
				Intent intent = new Intent(GlobalParam.ACTION_REFRESH_CHAT_HEAD_URL);
				intent.putExtra("oldurl", oldheadUrl);
				intent.putExtra("newurl", newHeadUrl);
				sendBroadcast(intent);
				EditProfileActivity.this.finish();
				/*
				 * WeiYuanState state = (WeiYuanState)msg.obj; if(state == null || state.equals("")){ Toast.makeText(mContext, R.string.commit_data_error,Toast.LENGTH_LONG).show(); return; } if(state.code == 0){
				 * 
				 * }else{ Toast.makeText(mContext, state.errorMsg, Toast.LENGTH_LONG).show(); }
				 */
				break;
			case GlobalParam.MSG_SHOW_LOAD_DATA:
				if (mInputAddr != null && !mInputAddr.equals("")) {
					mAddrTextView.setText(mInputAddr);
				}

				break;

			default:
				break;
			}
		}

	};

	/*
	 * 导入控件 (non-Javadoc)
	 * 
	 * @see com.weiyuan.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.complete_user_info);
		mContext = this;
		mLogin = WeiYuanCommon.getLoginResult(mContext);
		mImageLoader = new ImageLoader();
		initCompent();

	}

	/*
	 * 实例化控件
	 */
	private void initCompent() {
		setTitleContent(R.drawable.back_btn, R.drawable.ok_btn, R.string.edit_profile);
		mLeftBtn.setOnClickListener(this);
		mRightBtn.setOnClickListener(this);

		mHeaderLayout = (RelativeLayout) findViewById(R.id.new_header_layout);
		mNickNameLayout = (RelativeLayout) findViewById(R.id.nickname_layout);
		mAddrLayout = (RelativeLayout) findViewById(R.id.addr_layout);
		mSexLayout = (RelativeLayout) findViewById(R.id.sex_layout);
		mSignLayout = (RelativeLayout) findViewById(R.id.sign_layout);
		mHeaderLayout.setOnClickListener(this);
		mNickNameLayout.setOnClickListener(this);
		mAddrLayout.setOnClickListener(this);
		mSexLayout.setOnClickListener(this);
		mSignLayout.setOnClickListener(this);

		mNickNameTextView = (TextView) findViewById(R.id.nickname_content);
		mSexTextView = (TextView) findViewById(R.id.sex_content);
		mAddrTextView = (TextView) findViewById(R.id.addr_content);
		mSiTextView = (TextView) findViewById(R.id.sign_content);

		mImageView = (ImageView) findViewById(R.id.new_header_icon);
		setText();

	}

	/*
	 * 给控件设置值
	 */
	private void setText() {
		if (mLogin == null || mLogin.equals("")) {
			return;
		}
		if (mLogin.headsmall != null && !mLogin.headsmall.equals("")) {
			mImageView.setTag(mLogin.headsmall);
			mImageLoader.getBitmap(mContext, mImageView, null, mLogin.headsmall, 0, false, true, false);
		}
		mInputSex = mLogin.gender;
		if (mLogin.gender == 0) {
			mSexTextView.setText(mContext.getResources().getString(R.string.man));
		} else if (mLogin.gender == 1) {
			mSexTextView.setText(mContext.getResources().getString(R.string.femal));
		} else if (mLogin.gender == 2) {
			mSexTextView.setText(mContext.getResources().getString(R.string.no_limit));
		}

		mInputNickName = mLogin.nickname;
		mNickNameTextView.setText(mInputNickName + " ");

		mInputSign = mLogin.sign;
		mSiTextView.setText(mInputSign + " ");

		mProvice = mLogin.provinceid;
		mCity = mLogin.cityid;
		mAddrTextView.setText(mProvice + "  " + mCity + " ");
	}

	/*
	 * 完善用户资料
	 */
	private void completeUserInfo() {
		if (!WeiYuanCommon.getNetWorkState()) {
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
		}
		new Thread() {
			public void run() {

				try {
					WeiYuanCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG, mContext.getResources().getString(R.string.commit_dataing));
					LoginResult login = WeiYuanCommon.getWeiYuanInfo().modifyUserInfo(mImageFilePath, mInputNickName, mInputSex, mInputSign, mProvice, mCity);
					WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_CHECK_STATE, login);
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

	/*
	 * 按钮点击事件 (non-Javadoc)
	 * 
	 * @see com.weiyuan.BaseActivity#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			this.finish();
			break;
		case R.id.right_btn:
			/*
			 * if(mHeadUrl == null || mHeadUrl.equals("") ){ if((mImageFilePath == null || mImageFilePath.equals(""))){ Toast.makeText(mContext, mContext.getResources().getString(R.string.head_url_null),Toast.LENGTH_LONG).show(); return; } }
			 */
			if (mInputNickName == null || mInputNickName.equals("")) {
				Toast.makeText(mContext, mContext.getResources().getString(R.string.nickname_not_null), Toast.LENGTH_LONG).show();
				return;
			}
			completeUserInfo();
			break;
		case R.id.new_header_layout:
			selectImg();
			break;
		case R.id.nickname_layout:
			Intent nickNameIntent = new Intent();
			nickNameIntent.setClass(mContext, WriteUserInfoActivity.class);
			nickNameIntent.putExtra("content", mInputNickName);
			nickNameIntent.putExtra("type", GlobleType.COMPLETE_NICKNAME);
			mType = GlobleType.COMPLETE_NICKNAME;
			startActivityForResult(nickNameIntent, 1);
			break;
		case R.id.addr_layout:
			Intent intent = new Intent();
			intent.setClass(mContext, TreeViewActivity.class);
			intent.putExtra("type", GlobleType.TreeViewActivity_City_TYPE);
			mType = GlobleType.COMPLETE_ADDR;
			startActivityForResult(intent, 1);
			break;
		case R.id.sex_layout:
			MMAlert.showAlert(mContext, "", mContext.getResources().getStringArray(R.array.sex_array), null, new OnAlertSelectId() {

				@Override
				public void onClick(int whichButton) {
					switch (whichButton) {
					case 0:
						mInputSex = 0;
						mSexTextView.setText(mContext.getResources().getString(R.string.man));
						break;
					case 1:
						mInputSex = 1;
						mSexTextView.setText(mContext.getResources().getString(R.string.femal));
						break;
					default:
						break;
					}
				}
			});

			break;

		case R.id.sign_layout:
			Intent signIntent = new Intent();
			signIntent.setClass(mContext, WriteUserInfoActivity.class);
			signIntent.putExtra("content", mInputSign);
			signIntent.putExtra("type", GlobleType.COMPLETE_SIGN);
			mType = GlobleType.COMPLETE_SIGN;
			startActivityForResult(signIntent, 1);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mImageLoader.getImageBuffer().containsKey(mLogin.headsmall)) {
			mImageView.setImageDrawable(null);
			if (mImageLoader.getImageBuffer().get(mLogin.headsmall) != null) {
				mImageLoader.getImageBuffer().get(mLogin.headsmall).recycle();
			}
		}
	}

	/*
	 * 页面回调事件 (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1 && resultCode == RESULT_OK) {
			if (mType == GlobleType.COMPLETE_SEX) {
				mInputSex = data.getIntExtra("sex", 0);
				if (mInputSex == 0) {
					mSexTextView.setText(mContext.getResources().getString(R.string.man));
				} else if (mInputSex == 1) {
					mSexTextView.setText(mContext.getResources().getString(R.string.femal));
				} else if (mInputSex == 2) {
					mSexTextView.setText(mContext.getResources().getString(R.string.no_limit));
				}

			} else if (mType == GlobleType.COMPLETE_NICKNAME) {
				mInputNickName = data.getStringExtra("nickname");
				mNickNameTextView.setText(mInputNickName + " ");
			} else if (mType == GlobleType.COMPLETE_ADDR) {
				mInputAddr = data.getStringExtra("addr");
				// 省id
				// 市id
				mProvice = data.getStringExtra("provice");
				mCity = data.getStringExtra("city");
				mAddrTextView.setText(mInputAddr + " ");
			} else if (mType == GlobleType.COMPLETE_EMAIL) {

			} else if (mType == GlobleType.COMPLETE_COMPANY) {

			} else if (mType == GlobleType.COMPLETE_SIGN) {
				mInputSign = data.getStringExtra("sign");
				mSiTextView.setText(mInputSign + " ");
			} else if (mType == GlobleType.COMPLETE_HANGYUE) {

			} else if (mType == GlobleType.COMPLETE_SUBJECT) {

			}
		}

		switch (requestCode) {
		case GlobalParam.REQUEST_GET_URI:
			if (resultCode == RESULT_OK) {
				doChoose(true, data);
			}

			break;

		case GlobalParam.REQUEST_GET_IMAGE_BY_CAMERA:
			if (resultCode == RESULT_OK) {
				doChoose(false, data);
			}
			break;
		case GlobalParam.REQUEST_GET_BITMAP:
			if (resultCode == RESULT_OK) {

				Bundle extras = data.getExtras();
				if (extras != null) {

					mImageView.setImageBitmap(null);
					if (mBitmap != null && !mBitmap.isRecycled()) {
						mBitmap.recycle();
						mBitmap = null;
					}

					mBitmap = extras.getParcelable("data");
					mImageView.setImageBitmap(mBitmap);
					File file = new File(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY + TEMP_FILE_NAME);
					if (file != null && file.exists()) {
						file.delete();
						file = null;
					}

					mImageFilePath = FeatureFunction.saveTempBitmap(mBitmap, "header.jpg");
				}

			}
			break;
		default:
			break;
		}
	}

	/*
	 * 选择图片对话框
	 */
	private void selectImg() {
		MMAlert.showAlert(mContext, "", mContext.getResources().getStringArray(R.array.camer_item), null, new OnAlertSelectId() {

			@Override
			public void onClick(int whichButton) {
				Log.e("whichButton", "whichButton: " + whichButton);
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

		if (FeatureFunction.newFolder(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY)) {
			File out = new File(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY, TEMP_FILE_NAME);
			Uri uri = Uri.fromFile(out);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

			startActivityForResult(intent, GlobalParam.REQUEST_GET_IMAGE_BY_CAMERA);
		}

	}

	/*
	 * 从相册中选择
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
		 doChoose(isGallery, data, true, "", 0, 0);
	}

	 /*
		 * 处理选择的图片
		 */
	    public void doChoose(boolean isGallery, final Intent data, boolean isScale, String tempFile, int width, int height) {
	        if(isGallery){
	            originalImage(data, isScale, width, height);
	        }else {
	            if(data != null){
	                originalImage(data, isScale, width, height);
	            }else{
	                // Here if we give the uri, we need to read it
	                String tempUrl =tempFile;
	                if (TextUtils.isEmpty(tempUrl)){
	                    tempUrl = TEMP_FILE_NAME;
	                }
	                String path = Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY+tempUrl;
	                String extension = path.substring(path.indexOf("."), path.length());
	                if(FeatureFunction.isPic(extension)){
	                    if (isScale){
	                        startPhotoZoom(Uri.fromFile(new File(path)), width, height);
	                    }
	                    else {
	                        Intent intent = new Intent(mContext, RotateImageActivity.class);
	                        intent.putExtra("path", path);
	                        startActivityForResult(intent, GlobalParam.REQUEST_GET_BITMAP);
	                    }
	                }else {
	                    //Toast.makeText(mContext, R.string.please_choose_pic, Toast.LENGTH_SHORT).show();
	                }
	                //mImageFilePath = FeatureFunction.PUB_TEMP_DIRECTORY+TEMP_FILE_NAME;
	                //ShowBitmap(false);
	            }
	        }
	    }

 
	/**
     * 根据图片路径进入裁剪页面
     *
     * @param data
     */
    private void originalImage(Intent data, boolean isScale, int width, int height) {
        Uri uri = data.getData();

        if (!TextUtils.isEmpty(uri.getAuthority())) {
            ContentResolver resolver = this.getContentResolver();
            String[] projection = { MediaStore.Images.Media.DATA };
            String localImagePath = "";
            if (Build.VERSION.SDK_INT > 18) {
                String lastPathSegment = uri.getLastPathSegment();
                if (!lastPathSegment.matches("\\d+"))
                    lastPathSegment = lastPathSegment.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = { lastPathSegment };
                Cursor cursor = resolver.query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                        selection, selectionArgs, null);
                int columnIndex = cursor.getColumnIndex(projection[0]);
                if (cursor.moveToFirst())
                    localImagePath = cursor.getString(columnIndex);
                cursor.close();
            } else {
                Cursor cursor = resolver.query(uri, projection, null, null,
                        null);
                int columnIndex = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                localImagePath = cursor.getString(columnIndex);
                cursor.close();
            }

            String extension = localImagePath.substring(
                    localImagePath.lastIndexOf("."), localImagePath.length());
            if (FeatureFunction.isPic(extension)) {
                if (isScale){
                    startPhotoZoom(uri, width, height);
                }
                else {
                    Intent intent = new Intent(mContext, RotateImageActivity.class);
                    intent.putExtra("path", localImagePath);
                    startActivityForResult(intent, GlobalParam.REQUEST_GET_BITMAP);
                }
            } else {
            }
        } else {
            String path = uri.getPath();
            String extension = path.substring(path.lastIndexOf("."),
                    path.length());
            if (FeatureFunction.isPic(extension)) {
                if (isScale){
                    startPhotoZoom(Uri.fromFile(new File(path)), width, height);
                }
                else {
                    Intent intent = new Intent(mContext, RotateImageActivity.class);
                    intent.putExtra("path", path);
                    startActivityForResult(intent, GlobalParam.REQUEST_GET_BITMAP);
                }
            } else {
            }
        }
    }
	 /**
     * 调用系统裁剪功能
     *
     * @param uri
     *            文件URI
     */
    public void startPhotoZoom(Uri uri, int width, int height) {
        Intent intent = new Intent("com.android.camera.action.CROP");

        if (android.os.Build.VERSION.SDK_INT > 18) {
            String url=getPath(mContext, uri);
            intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
        }else{
            intent.setDataAndType(uri, "image/*");
        }

        //intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例

        if (width == 0 || height == 0) {
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            // outputX outputY 是裁剪图片宽高
            intent.putExtra("outputX", 180);
            intent.putExtra("outputY", 180);
            intent.putExtra("return-data", true);
        }
        else{
            intent.putExtra("scale", true);
            intent.putExtra("scaleUpIfNeeded", true);

            intent.putExtra("aspectX", 3);
            intent.putExtra("aspectY", 2);
            // outputX outputY 是裁剪图片宽高
            intent.putExtra("outputX", width);
            intent.putExtra("outputY", height);

            String imagePath = Environment.getExternalStorageDirectory() +  FeatureFunction.PUB_TEMP_DIRECTORY + "album.jpg";
            File file = new File(imagePath);
            try {
                file.createNewFile();
            } catch (IOException e) {
            }

            Uri imageUri = Uri.fromFile(file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("noFaceDetection", true);
            intent.putExtra("return-data", false);
        }

        startActivityForResult(intent, GlobalParam.REQUEST_GET_BITMAP);
    }
    

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT > 18;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
