package com.chengxin;

import java.io.File;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.chengxin.R;
import com.chengxin.Entity.MapInfo;
import com.chengxin.Entity.WeiYuanState;
import com.chengxin.dialog.CPAlert;
import com.chengxin.dialog.MMAlert;
import com.chengxin.dialog.CPAlert.OnAlertOkSelectId;
import com.chengxin.dialog.MMAlert.OnAlertSelectId;
import com.chengxin.global.FeatureFunction;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.GlobleType;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.net.WeiYuanException;

/**
 * 申请成为商户
 * @author dl
 *
 */
public class ApplyMerchantActivity extends BaseActivity {
	private static final String TEMP_BUSI_FILE_NAME = "busi_pic.jpg";
	private static final String TEMP_AUTH_FILE_NAME = "auth_pic.jpg";
	
	private static final int IMAGE_BUSI_SELECTED  = 0x8001;
	private static final int IMAGE_AUTH_SELECTED  = 0x8002;

	private int			mCurrentImage = 0;
	private EditText 	mMerchantNameEdit, 	mContactNameEdit,	mContactTelEdit,
						mContactAddrEdit,	mDescEdit,			mBankEdit,
						mBankUserEdit,		mBankAccountEdit;
	private Button 		mCommitBtn;
	private TextView	mMerchantLocationBtn,	mAgreeProtocolBtn;
	private CheckBox 	mAgreeBtn;
	private ImageView 	mImgBusiness,		mImgAuth;
	private LinearLayout	 mBtnBusiness,	mBtnAuth;
	private Boolean 	mIsAgree = false;
	private Bitmap 		mBusiBitmap, mAuthBitmap;

	private String 	mInputMerchatnName, mInputContactName, 	mInputContactTel,
					mInputContactAddr, 	mInputDesc,			mInputBank,
					mInputBankUser,		mInputBankAccount;

	private String mBusiImageFilePath;
	private String mAuthImageFilePath;

	private MapInfo mMapInfo;
    Uri imageFilePath;

	private ImageView currImageView = null;
	private Bitmap currBitmap = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.apply_merchant_view);
		initComponent();
	}

	private void initComponent(){
		setTitleContent(R.drawable.back_btn,0,R.string.apply_merchant);
		mLeftBtn.setOnClickListener(this);

		mMerchantNameEdit = (EditText)findViewById(R.id.merchant_name);
		mMerchantLocationBtn = (TextView)findViewById(R.id.merchant_location);
		mContactNameEdit = (EditText)findViewById(R.id.contact_name);
		mContactTelEdit = (EditText)findViewById(R.id.contact_tel);
		mContactAddrEdit = (EditText)findViewById(R.id.contact_addr);
		mDescEdit = (EditText)findViewById(R.id.desc);

		mBankEdit = (EditText)findViewById(R.id.bank_name);
		mBankEdit.setFocusable(false);
		mBankEdit.setFocusableInTouchMode(false);
		mBankEdit.setOnClickListener(this);
		
		mBankUserEdit = (EditText)findViewById(R.id.bank_user);
		mBankAccountEdit = (EditText)findViewById(R.id.bank_account);
		
		mMerchantLocationBtn.setOnClickListener(this);
		mCommitBtn = (Button)findViewById(R.id.commit_btn);
		mCommitBtn.setOnClickListener(this);
		
		mAgreeBtn = (CheckBox)findViewById(R.id.chk_agree);
		
		mAgreeProtocolBtn = (TextView)findViewById(R.id.btn_protocol);
		mAgreeProtocolBtn.setOnClickListener(this);
		
		mImgBusiness = (ImageView)findViewById(R.id.img_business);
		mImgAuth = (ImageView)findViewById(R.id.img_auth);

		mBtnBusiness = (LinearLayout)findViewById(R.id.btn_business);
		mBtnBusiness.setOnClickListener(this);
		mBtnAuth = (LinearLayout)findViewById(R.id.btn_auth);
		mBtnAuth.setOnClickListener(this);
	}

	private boolean checkValue(){
		boolean isCheck = true;
		String hintMsg = "";
		mIsAgree = mAgreeBtn.isChecked();
		mInputMerchatnName = mMerchantNameEdit.getText().toString();
		mInputContactName = mContactNameEdit.getText().toString();
		mInputContactTel = mContactTelEdit.getText().toString(); 
		mInputContactAddr = mContactAddrEdit.getText().toString();
		mInputDesc = mDescEdit.getText().toString();
		mInputBank = mBankEdit.getText().toString();
		mInputBankUser = mBankUserEdit.getText().toString();
		mInputBankAccount = mBankAccountEdit.getText().toString();
		
		if (mIsAgree == false) {
			isCheck = false;
			hintMsg = "请选择同意入驻协议";
		} else if(mInputMerchatnName == null || mInputMerchatnName.equals("")){
			isCheck = false;
			hintMsg = "请输入商铺名称";
		}else if(mInputContactName == null || mInputContactName.equals("")){
			isCheck = false;
			hintMsg = "请输入联系人名称";
		}else if(mInputContactTel == null || mInputContactTel.equals("")){
			isCheck = false;
			hintMsg = "请输入联系号码";
		}else if(mInputContactAddr == null || mInputContactAddr.equals("")){
			isCheck = false;
			hintMsg = "请输入联系地址";
		}else if(mMapInfo == null
				|| (mMapInfo.getLat() == null || mMapInfo.getLat().equals(""))
				||(mMapInfo.getLng() == null || mMapInfo.getLng().equals(""))){
			isCheck = false;
			hintMsg = "请选择商铺位置";
		} else {
			if(!WeiYuanCommon.isMobileNum(mInputContactTel)){
				isCheck = false;
				hintMsg = "请输入正确的手机格式";
			}else if(mInputMerchatnName.length()>30){
				isCheck = false;
				hintMsg = "商铺名称为1-30个字";
			}else if(mInputContactName.length()>10){
				isCheck = false;
				hintMsg = "联系人为1-10个字";
			}else if(mInputDesc !=null && !mInputDesc.equals("")){
				if(mInputDesc.length()>30){
					isCheck = false;
					hintMsg = "备注信息不能超过30个字";
				}
			}
		}
		if(!isCheck && (hintMsg != null && !hintMsg.equals(""))){
			Toast.makeText(mContext, hintMsg, Toast.LENGTH_LONG).show();
		}
		return isCheck;
	}

	private void commit(){
		if(!checkValue()){
			return;
		}
		if(!WeiYuanCommon.getNetWorkState()){
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread(){
			public void run() {
				try {
					WeiYuanCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG, 
							mContext.getResources().getString(R.string.commit_dataing));
					
					WeiYuanState state = WeiYuanCommon.getWeiYuanInfo().applyMerchant
                        (mInputMerchatnName,
                         mMapInfo.getAddr(),
                         mInputContactName,
                         mInputContactTel,
                         mInputContactAddr,
                         mMapInfo.getLat(),
                         mMapInfo.getLng(),
                         mMapInfo.getCtiy(),
                         null,
                         mBusiImageFilePath,
                         mAuthImageFilePath,
                         mInputBank,
                         mInputBankUser,
                         mInputBankAccount,
                         mInputDesc);
					
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
					WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_CHECK_STATE,state);
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

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			ApplyMerchantActivity.this.finish();
			break;
		case R.id.merchant_location:
			Intent intent = new Intent(this, LocationActivity.class);
			startActivityForResult(intent, GlobleType.RESQUEST_MAP_LOACTION);
			break;
		case R.id.commit_btn:
			commit();
			break;
		case R.id.btn_protocol:
			Intent getProtocolIntent = new Intent();
			getProtocolIntent.setClass(mContext, UserProtocolActivity.class);
			startActivity(getProtocolIntent);
			break;
		case R.id.btn_business:
			mCurrentImage = IMAGE_BUSI_SELECTED;
			selectImg();
			break;
		case R.id.btn_auth:
			mCurrentImage = IMAGE_AUTH_SELECTED;
			selectImg();
			break;
		case R.id.bank_name:
			String list[] = mContext.getResources().getStringArray(R.array.bank_array);
			CPAlert.showAlert(
					mContext,
					"选择开户银行",
					list,
					new OnAlertOkSelectId() {
				
				@Override
				public void onOkClick(int whichButton, String unused, String bank) {
					mBankEdit.setText(bank);
				}
			});
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case GlobleType.RESQUEST_MAP_LOACTION:
			if(data != null && RESULT_OK == resultCode){
				Bundle bundle = data.getExtras();
				if(bundle != null){

					mMapInfo = (MapInfo)data.getSerializableExtra("mapInfo");
					if(mMapInfo == null){
						Toast.makeText(mContext, mContext.getString(R.string.get_location_failed), Toast.LENGTH_SHORT).show();
						return;
					}
					mMerchantLocationBtn.setText(mMapInfo.getAddr());

				}
			}
			break;

		case GlobalParam.REQUEST_GET_URI: 
			if (resultCode == RESULT_OK) {
//				 Uri selectedImage = data.getData();
//				try {
//					Bitmap bmp = BitmapFactory.decodeStream(getContentResolver()
//							.openInputStream(selectedImage));
//					mImgBusiness.setImageBitmap(bmp);
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				}
	            doChoose(true, data);
			}

			break;

		case GlobalParam.REQUEST_GET_IMAGE_BY_CAMERA:
			if(resultCode == RESULT_OK){
//				try {
//					Bundle extra = data.getExtras();
//					/**
//					 * 然而为了节约内存的消耗，这里返回的图片是一个121*162的缩略图。 那么如何返回我们需要的大图呢？看上面
//					 * 然而存储了图片。有了图片的存储位置，能不能直接将图片显示出来呢》
//					 * 这个问题就设计到对于图片的处理和显示，是非常消耗内存的，对于PC来说可能不算什么，但是对于手机来说
//					 * 很可能使你的应用因为内存耗尽而死亡。不过还好，Android为我们考虑到了这一点
//					 * Android中可以使用BitmapFactory类和他的一个内部类BitmapFactory
//					 * .Options来实现图片的处理和显示
//					 * BitmapFactory是一个工具类，里面包含了很多种获取Bitmap的方法
//					 * 。BitmapFactory.Options类中有一个inSampleSize
//					 * ，比如设定他的值为8，则加载到内存中的图片的大小将 是原图片的1/8大小。这样就远远降低了内存的消耗。
//					 * BitmapFactory.Options op = new BitmapFactory.Options();
//					 * op.inSampleSize = 8; Bitmap pic =
//					 * BitmapFactory.decodeFile(imageFilePath, op);
//					 * 这是一种快捷的方式来加载一张大图，因为他不用考虑整个显示屏幕的大小和图片的原始大小
//					 * 然而有时候，我需要根据我们的屏幕来做相应的缩放，如何操作呢？
//					 * 
//					 */
//					// 首先取得屏幕对象
//					Display display = this.getWindowManager()
//							.getDefaultDisplay();
//					// 获取屏幕的宽和高
//					Point size = null;
//					display.getSize(size);
//					/**
//					 * 为了计算缩放的比例，我们需要获取整个图片的尺寸，而不是图片
//					 * BitmapFactory.Options类中有一个布尔型变量inJustDecodeBounds
//					 * ，将其设置为true 这样，我们获取到的就是图片的尺寸，而不用加载图片了。
//					 * 当我们设置这个值的时候，我们接着就可以从BitmapFactory
//					 * .Options的outWidth和outHeight中获取到值
//					 */
//					BitmapFactory.Options op = new BitmapFactory.Options();
//					// op.inSampleSize = 8;
//					op.inJustDecodeBounds = true;
//					// Bitmap pic = BitmapFactory.decodeFile(imageFilePath,
//					// op);//调用这个方法以后，op中的outWidth和outHeight就有值了
//					// 由于使用了MediaStore存储，这里根据URI获取输入流的形式
//					Bitmap pic = BitmapFactory.decodeStream(this
//							.getContentResolver()
//							.openInputStream(imageFilePath), null, op);
//					int wRatio = (int) Math.ceil(op.outWidth / (float) size.x); // 计算宽度比例
//					int hRatio = (int) Math.ceil(op.outHeight / (float) size.y); // 计算高度比例
//					Log.v("Width Ratio:", wRatio + "");
//					Log.v("Height Ratio:", hRatio + "");
//					/**
//					 * 接下来，我们就需要判断是否需要缩放以及到底对宽还是高进行缩放。 如果高和宽不是全都超出了屏幕，那么无需缩放。
//					 * 如果高和宽都超出了屏幕大小，则如何选择缩放呢》 这需要判断wRatio和hRatio的大小
//					 * 大的一个将被缩放，因为缩放大的时，小的应该自动进行同比率缩放。 缩放使用的还是inSampleSize变量
//					 */
//					if (wRatio > 1 && hRatio > 1) {
//						if (wRatio > hRatio) {
//							op.inSampleSize = wRatio;
//						} else {
//							op.inSampleSize = hRatio;
//						}
//					}
//
//					op.inJustDecodeBounds = false; // 注意这里，一定要设置为false，因为上面我们将其设置为true来获取图片尺寸了
//					pic = BitmapFactory.decodeStream(this.getContentResolver()
//							.openInputStream(imageFilePath), null, op);
//					mImgBusiness.setImageBitmap(pic);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
				doChoose(false, data);
			}
			break;
			
		case GlobalParam.REQUEST_GET_BITMAP:
			if(resultCode == RESULT_OK){

				Bundle extras = data.getExtras();

				if (extras != null) {
					File file = null;

					if (mCurrentImage == IMAGE_BUSI_SELECTED) {
						currImageView = mImgBusiness;
						currBitmap = mBusiBitmap;
						file = new File(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY + TEMP_BUSI_FILE_NAME);
					} else if (mCurrentImage == IMAGE_AUTH_SELECTED) {
						currImageView = mImgAuth;
						currBitmap = mBusiBitmap;
						file = new File(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY + TEMP_AUTH_FILE_NAME);
					}
					
					if (currImageView != null) {
						currImageView.setImageBitmap(null);
						
						if (currBitmap != null && !currBitmap.isRecycled()){
							currBitmap.recycle();
							currBitmap = null;
						}

						currBitmap = extras.getParcelable("data");
						currImageView.setImageBitmap(currBitmap);

						if(file != null && file.exists()){
							file.delete();
							file = null;
						}

						if (mCurrentImage == IMAGE_BUSI_SELECTED) {
							mBusiBitmap = currBitmap;
							mBusiImageFilePath = FeatureFunction.saveTempBitmap(currBitmap, "temp_busi.jpg");
						} else if (mCurrentImage == IMAGE_AUTH_SELECTED) {
							mAuthBitmap = currBitmap;
							mAuthImageFilePath = FeatureFunction.saveTempBitmap(currBitmap, "temp_auth.jpg");
						}
					}
				}
			}
			break;	

		default:
			break;
		}
	}


	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {
			case GlobalParam.MSG_CHECK_STATE:
				WeiYuanState sate = (WeiYuanState)msg.obj;
				
				if(sate == null){
					Toast.makeText(mContext, R.string.commit_data_error,Toast.LENGTH_LONG).show();
					return;
				}
				
				Toast.makeText(mContext, sate.errorMsg,Toast.LENGTH_LONG).show();

				if(sate.code == 0){
					ApplyMerchantActivity.this.finish();
				}
				
				break;

			default:
				break;
			}
		}

	};

	/**
	 * 底部弹出框
	 */
	private void selectImg(){
		MMAlert.showAlert(mContext, "", mContext.getResources()
				.getStringArray(R.array.camer_item), null, new OnAlertSelectId() {

			@Override
			public void onClick(int whichButton) {
				Log.e("whichButton", "whichButton: "+whichButton);
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
			File out = null;

			if (mCurrentImage == IMAGE_BUSI_SELECTED) {
				out = new File(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY, TEMP_BUSI_FILE_NAME);
			} else if (mCurrentImage == IMAGE_AUTH_SELECTED) {
				out = new File(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY, TEMP_AUTH_FILE_NAME);
			}
			
			if (out != null) {
				Uri uri = Uri.fromFile(out);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

				startActivityForResult(intent, GlobalParam.REQUEST_GET_IMAGE_BY_CAMERA);
			}
		}

	}

	/*
	 * 从相册中选一张
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
        if(isGallery || data != null){
            originalImage(data);
        }else {
			// Here if we give the uri, we need to read it
			// String tempUrl =tempFile;
			// if (TextUtils.isEmpty(tempUrl)){
			String tempUrl = null;
        	
        	if (mCurrentImage == IMAGE_BUSI_SELECTED) {
    			tempUrl = TEMP_BUSI_FILE_NAME;
        	} else if (mCurrentImage == IMAGE_AUTH_SELECTED) {
    			tempUrl = TEMP_BUSI_FILE_NAME;
        	}
			// }
        	
        	if (tempUrl != null) {
    			String path = Environment.getExternalStorageDirectory()
    					+ FeatureFunction.PUB_TEMP_DIRECTORY + tempUrl;
    			String extension = path.substring(path.indexOf("."), path.length());
    			if (FeatureFunction.isPic(extension)) {
    				// if (isScale){
    				startPhotoZoom(Uri.fromFile(new File(path)));
    				// }
    				// else {
    				// Intent intent = new Intent(mContext,
    				// RotateImageActivity.class);
    				// intent.putExtra("path", path);
    				// startActivityForResult(intent,
    				// GlobalParam.REQUEST_GET_BITMAP);
    				// }
    			} else {
    				// Toast.makeText(mContext, R.string.please_choose_pic,
    				// Toast.LENGTH_SHORT).show();
    			}
    			// mImageFilePath =
    			// FeatureFunction.PUB_TEMP_DIRECTORY+TEMP_FILE_NAME;
    			// ShowBitmap(false);
        	}
        }
    }

	/**
     * 根据图片路径进入裁剪页面
     *
     * @param data
     */
    private void originalImage(Intent data) {
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
//                if (isScale){
                    startPhotoZoom(uri);
//                }
//                else {
//                    Intent intent = new Intent(mContext, RotateImageActivity.class);
//                    intent.putExtra("path", localImagePath);
//                    startActivityForResult(intent, GlobalParam.REQUEST_GET_BITMAP);
//                }
//            } else {
            }
        } else {
            String path = uri.getPath();
            String extension = path.substring(path.lastIndexOf("."),
                    path.length());
            if (FeatureFunction.isPic(extension)) {
//                if (isScale){
                    startPhotoZoom(Uri.fromFile(new File(path)));
//                }
//                else {
//                    Intent intent = new Intent(mContext, RotateImageActivity.class);
//                    intent.putExtra("path", path);
//                    startActivityForResult(intent, GlobalParam.REQUEST_GET_BITMAP);
//                }
//            } else {
            }
        }
    }

	/**
	 * 裁剪图片
	 */
	 /**
     * 调用系统裁剪功能
     *
     * @param uri
     *            文件URI
     */
    public void startPhotoZoom(Uri uri) {
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

        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);

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

	public static String getDataColumn(Context context, Uri uri,
			String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
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

    /*
	 * 页面销毁释放图片
	 * (non-Javadoc)
	 * @see com.weiyuan.BaseActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mBusiBitmap!=null && !mBusiBitmap.isRecycled()) {
			mImgBusiness.setImageDrawable(null);
			mBusiBitmap.recycle();
		}

		if (mAuthBitmap!=null && !mAuthBitmap.isRecycled()) {
			mImgAuth.setImageDrawable(null);
			mAuthBitmap.recycle();
		}
	}	
}
