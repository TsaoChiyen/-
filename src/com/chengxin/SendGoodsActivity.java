package com.chengxin;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chengxin.R;
import com.chengxin.Entity.Goods;
import com.chengxin.Entity.MerchantMenu;
import com.chengxin.Entity.MorePicture;
import com.chengxin.Entity.UploadImg;
import com.chengxin.Entity.WeiYuanState;
import com.chengxin.adapter.UploadPicAdapter;
import com.chengxin.adapter.UploadPicAdapter.ViewHolder;
import com.chengxin.dialog.MMAlert;
import com.chengxin.dialog.MMAlert.OnAlertSelectId;
import com.chengxin.global.FeatureFunction;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.ImageLoader;
import com.chengxin.global.ScreenUtils;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.net.WeiYuanException;
import com.chengxin.widget.MyGridView;

/**
 * 发布商品
 * @author dl
 *
 */
public class SendGoodsActivity extends BaseActivity implements OnItemClickListener{

	private static final int SCAN_FOR_BARCODE = 21;
	private TextView mGoodsTypeTextView;
	private final int REQUEST_GOODS_MENT = 11;

	private EditText mGoodsNameEdit,mGoodsPriceEdit,mGoodsContentEdit,
	mGoodsParamterEdit;

	private ImageView mGoodsLogoImageView;

	/*+++显示商品图片++*/
	private int IMAGE_MAX = 6;
	private MyGridView mMyGridView;
	private UploadPicAdapter mAdapter;
	private List<UploadImg> mImageList = new ArrayList<UploadImg>();
	private int mWidth = 0;
	/*---显示商品图片---*/

	private Bitmap mGoodsBitmapLogo;
	private String mGoodsLogoUrl;


	private int mGoodsTypeId;
	private String mInputGoodsName,mInputGoodsPrice,mInputGoodsContent,
	mInputGoodsParamter;

	private List<MorePicture> mPicList = new ArrayList<MorePicture>();

	private int mClickType =0;  //0-商品缩略图 1-商品图
	private EditText mGoodsBarcodeEdit;
	private TextView mBtnBarcode;
	private String mInputGoodsBarcode;
	private Goods mGoods = null;
	private ImageLoader mImageLoader = new ImageLoader();
	private boolean isFirstShow = true;
	private int mPosition = -1;
	private int mOpenMode= 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.send_goods_view);
		mWidth = ScreenUtils.getScreenWidth(mContext); 
		intComponent();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if (isFirstShow) {
			isFirstShow = false;

			if (hasFocus && mGoods != null) {
				isFirstShow  = false;
				mGoodsBitmapLogo = mGoodsLogoImageView.getDrawingCache();
				mGoodsLogoUrl = FeatureFunction.saveTempBitmap(mGoodsBitmapLogo, "goods_logo.jpg");
				mGoodsLogoImageView.setDrawingCacheEnabled(false);
			}
		}
	}


	private void intComponent(){
		setTitleContent(R.drawable.back_btn,R.drawable.ok,R.string.send_goods);
		mLeftBtn.setOnClickListener(this);
		mRightBtn.setOnClickListener(this);
		
		mGoodsTypeTextView = (TextView)findViewById(R.id.goods_type);
		mGoodsTypeTextView.setOnClickListener(this);

		mGoodsLogoImageView = (ImageView)findViewById(R.id.goods_logo);
		mGoodsLogoImageView.setOnClickListener(this);

		mGoodsBarcodeEdit = (EditText)findViewById(R.id.goods_barcode);
		mGoodsNameEdit = (EditText)findViewById(R.id.goods_name);
		mGoodsPriceEdit = (EditText)findViewById(R.id.goods_price);
		mGoodsContentEdit = (EditText)findViewById(R.id.goods_content);
		mGoodsParamterEdit = (EditText)findViewById(R.id.goods_paramter);

		mBtnBarcode = (TextView)findViewById(R.id.btn_barcode);
		mBtnBarcode.setOnClickListener(this);
		
		
		mMyGridView  = (MyGridView)findViewById(R.id.gridview);
		mMyGridView.setOnItemClickListener(this);
		mImageList.add(new UploadImg("", 1));

		getDefaultData();

		mAdapter = new UploadPicAdapter(mContext, mImageList, mWidth);
		mMyGridView.setAdapter(mAdapter);
	}

	private void getDefaultData() {
		mOpenMode = getIntent().getIntExtra("openMode", 0);
		
		if (mOpenMode == 1) {
			mGoods = (Goods)getIntent().getExtras().get("goods");
			mPosition  = getIntent().getIntExtra("position", -1);
			
			if (mGoods != null) {
				mGoodsTypeId = mGoods.categoryid;
				
				mGoodsTypeTextView.setText(MerchantMenu.getNameById(mGoodsTypeId));
				mGoodsNameEdit.setText(mGoods.name);
				mGoodsBarcodeEdit.setText(mGoods.barcode);
				mGoodsPriceEdit.setText(String.valueOf(mGoods.price));
				mGoodsParamterEdit.setText(mGoods.parameter);
				mGoodsContentEdit.setText(mGoods.content);
				mGoodsLogoUrl = mGoods.logo;
				
				mGoodsLogoImageView.setDrawingCacheEnabled(true);
    			mImageLoader.getBitmap(mContext, mGoodsLogoImageView, null, mGoods.logo, 0, false, true,false);
    			
				if (mGoods.pictureList.size() > 0) {
					for (int i = 0; i < mGoods.pictureList.size(); i++) {
						mImageList.add(mImageList.size() - 1, new UploadImg(mGoods.pictureList.get(i).smallUrl, 2));
					}
				}
			}
		}
	}

	private boolean  checkText(){
		boolean isCheck = true;
		String hinMsg = "";
		mInputGoodsBarcode = mGoodsBarcodeEdit.getText().toString();
		mInputGoodsName = mGoodsNameEdit.getText().toString();
		mInputGoodsPrice = mGoodsPriceEdit.getText().toString();
		mInputGoodsContent = mGoodsContentEdit.getText().toString();
		mInputGoodsParamter = mGoodsParamterEdit.getText().toString();

		if(mGoodsTypeId == 0){
			isCheck = false;
			hinMsg = mContext.getResources().getString(R.string.please_select_goods_type);
		}else if ((mInputGoodsName == null || mInputGoodsName.equals(""))
				&& mImageList.size() == 1) {
			isCheck = false;
			hinMsg = mContext.getResources().getString(R.string.please_write_goods_name);
		}else if(mInputGoodsPrice == null || mInputGoodsPrice.equals("")){
			isCheck = false;
			hinMsg = mContext.getResources().getString(R.string.please_write_goods_price);
		}else if(mGoodsLogoUrl == null || mGoodsLogoUrl.equals("")){
			isCheck = false;
			hinMsg = mContext.getResources().getString(R.string.please_select_goods_logo);
		}else{
			if (mImageList!=null && mImageList.size()>0 ) {
				mPicList = new ArrayList<MorePicture>();

				for (int i = 0; i <mImageList.size(); i++) {
					if(mImageList.get(i).mType == 0){
						String key = "picture";
						if (i > 0) {
							int index = i+1;
							key = key+index;
						}
						mPicList.add(new MorePicture(key,mImageList.get(i).mPicPath));
					}

				}
			}
			if(mPicList == null || mPicList.size() <= 0){
				mPicList = new ArrayList<MorePicture>();
//
//				isCheck = false;
//				hinMsg = mContext.getResources().getString(R.string.please_select_goods_image);
			}
		}


		if (!isCheck && hinMsg!=null && !hinMsg.equals("")) {
			Toast.makeText(mContext, hinMsg,Toast.LENGTH_LONG).show();
		}
		return isCheck ;
	}


	private void sendGoods(){
		if(!checkText()){
			return;
		}
		if(!WeiYuanCommon.getNetWorkState()){
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}

		new Thread(){
			public void run() {
				new Thread(){
					public void run() {
						try {
							WeiYuanCommon.sendMsg(mBaseHandler,BASE_SHOW_PROGRESS_DIALOG,"正在提交数据,请稍后...");
							mPicList.add(new MorePicture("logo",mGoodsLogoUrl));
							
							WeiYuanState status = null;
							
							if (mGoods != null) {
								status =WeiYuanCommon.getWeiYuanInfo().editGoods(
										Integer.valueOf(mGoods.id),
										mGoodsTypeId, 
										mInputGoodsName,
										mInputGoodsPrice,
										mPicList,
										mInputGoodsContent,
										mInputGoodsParamter,
										mInputGoodsBarcode);
							} else {
								status =WeiYuanCommon.getWeiYuanInfo().addGoods(
										mGoodsTypeId, 
										mInputGoodsName,
										mInputGoodsPrice,
										mPicList,
										mInputGoodsContent,
										mInputGoodsParamter,
										mInputGoodsBarcode);
							}
							
							WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_CHECK_STATE,status);
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
			};
		}.start();
	}

	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GlobalParam.MSG_CHECK_STATE:
				WeiYuanState status = (WeiYuanState)msg.obj;
				if (status == null) {
					Toast.makeText(mContext, R.string.commit_data_error,Toast.LENGTH_LONG).show();
					return;
				}
				
				String hintMsg = status.errorMsg;
				
				if(status.code!=0){
					if(hintMsg==null || hintMsg.equals("")){
						hintMsg = mContext.getResources().getString(R.string.send_moving_error);
					}
					Toast.makeText(mContext, hintMsg,Toast.LENGTH_LONG).show();
					return;
				}
				
				if(hintMsg == null || hintMsg.equals("")){
					hintMsg = mContext.getResources().getString(R.string.send_goods_success);
				}
				Toast.makeText(mContext, hintMsg,Toast.LENGTH_LONG).show();
				
				if (mOpenMode == 1) {
					goodsChanged();
				}
				
				Intent intent = new Intent();
				intent.putExtra("data", mGoods);
				intent.putExtra("position", mPosition);
				SendGoodsActivity.this.setResult(RESULT_OK, intent);
				SendGoodsActivity.this.finish();
				break;

			default:
				break;
			}
		}

		private void goodsChanged() {
			if (mGoods != null) {
				mGoods.name = mInputGoodsName;
				mGoods.barcode = mInputGoodsBarcode;
				mGoods.price = Float.valueOf(mInputGoodsPrice);
				mGoods.categoryid = mGoodsTypeId;
				mGoods.content = mInputGoodsContent;
				mGoods.parameter = mInputGoodsParamter;
			}
		}

	};

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			SendGoodsActivity.this.finish();
			break;
		case R.id.goods_type:
			Intent intent = new Intent();
			intent.setClass(mContext, GoodsTypeActivity.class);
			startActivityForResult(intent, REQUEST_GOODS_MENT);
			break;
		case R.id.goods_logo:
			mClickType = 0;
			selectImg();
			break;
		case R.id.btn_barcode:
			Intent scanIntent = new Intent(mContext, CaptureActivity.class);
			scanIntent.putExtra("scanmode", 1);
			startActivityForResult(scanIntent, SCAN_FOR_BARCODE);
			break;
		case R.id.right_btn:
			sendGoods();
			break;
			//GoodsTypeActivity
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);

		switch (arg0) {
		case SCAN_FOR_BARCODE:
			if(arg1 == RESULT_OK){
				mInputGoodsBarcode = arg2.getStringExtra("data");
				mGoodsBarcodeEdit.setText(mInputGoodsBarcode);
			}
			
			break;
		case REQUEST_GOODS_MENT:
			if(arg1 == RESULT_OK){
				if(arg2 != null){
					MerchantMenu menu = (MerchantMenu)arg2.getSerializableExtra("menu_entity");
					if(menu != null){
						mGoodsTypeTextView.setText(menu.name);
						mGoodsTypeId = menu.id;
					}
				}
			}

			break;
		case 1:
			if(arg2!=null && arg1 == 2){
				List<UploadImg> imgList = (List<UploadImg>) arg2.getSerializableExtra("img_list");
				
				if(imgList!=null && imgList.size()>0){
					if(mImageList!=null && mImageList.size()>0){
						mImageList.clear();
					}
					mImageList.addAll(imgList);
					mAdapter.notifyDataSetChanged();
				}
			}
			break;

		case GlobalParam.REQUEST_GET_URI: 
			if (arg1 == RESULT_OK) {
				doChoose(true, arg2);
			}

			break;

		case GlobalParam.REQUEST_GET_IMAGE_BY_CAMERA:
			if(arg1 == RESULT_OK){
				doChoose(false, arg2);
			}
			break;

		case GlobalParam.REQUEST_GET_BITMAP:
			if(arg1 == RESULT_OK){
				String path = arg2.getStringExtra("path");
				if(!TextUtils.isEmpty(path)){

					if(mClickType == 1){

						//一张图片不能选择多次
						boolean isExist = false;
						if(mImageList.size() != 0){
							for (int i = 0; i < mImageList.size(); i++) {
								if(mImageList.get(i).mType == 0 && mImageList.get(i).mPicPath.equals(path)){
									isExist = true;
									break;
								}
							}
						}

						if(!isExist){
							mImageList.add(mImageList.size() - 1, new UploadImg(path, 0));
							if(mImageList.size() - 1 == IMAGE_MAX){
								mImageList.remove(mImageList.size() - 1);
							}
							mAdapter.notifyDataSetChanged();
						}
					}else if(mClickType == 0){
						mGoodsLogoUrl = path;
						mGoodsLogoImageView.setImageBitmap(null);
						if(mGoodsBitmapLogo != null && !mGoodsBitmapLogo.isRecycled()){
							mGoodsBitmapLogo.recycle();
							mGoodsBitmapLogo = null;
						}
						mGoodsBitmapLogo = BitmapFactory.decodeFile(path);
						mGoodsLogoImageView.setImageBitmap(mGoodsBitmapLogo);
					}

				}

			}

			break;

		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View convertView, int position, long id) {
		mClickType = 1;
		
		if(position < mImageList.size()){
			if (mImageList.get(position).mType == 2) {
				ViewHolder holder = (ViewHolder) convertView.getTag();
        		Bitmap bitmap = holder.mHeaderView.getDrawingCache();
        		mImageList.get(position).mPicPath = FeatureFunction.saveTempBitmap(bitmap, "goods_icon"+String.valueOf(position)+".jpg");
        		mImageList.get(position).mType = 0;
        		mAdapter.mImageMap.put(mImageList.get(position).mPicPath, bitmap);
				holder.mHeaderView.setDrawingCacheEnabled(false);
				
				Intent showImageIntent = new Intent();
				showImageIntent.setClass(mContext, ShowImageActivity.class);
				showImageIntent.putExtra("type",1);
				showImageIntent.putExtra("pos",position);
				showImageIntent.putExtra("img_list",(Serializable)mImageList);
				startActivityForResult(showImageIntent, 1);
			} else if (mImageList.get(position).mType == 0) {
				if(mAdapter.getIsDelete()){
					HashMap<String, Bitmap> hashMap = mAdapter.getImageBuffer();
					String path = mImageList.get(position).mPicPath;
					ImageView view = (ImageView) mMyGridView.findViewWithTag(path);
					mImageList.remove(position);
					if(view != null){
						view.setImageBitmap(null);
					}
					if (hashMap.get(path) != null) {
						Bitmap bitmap = hashMap.get(path);
						if(bitmap != null && !bitmap.isRecycled()){
							bitmap.recycle();
							bitmap = null;
						}

						hashMap.remove(path);
					}
					deleteImgFile(path);
					if(mImageList.get(mImageList.size() - 1).mType != 1){
						mImageList.add(new UploadImg("", 1));
					}
					mAdapter.notifyDataSetChanged();
				}else{
					Intent showImageIntent = new Intent();
					showImageIntent.setClass(mContext, ShowImageActivity.class);
					showImageIntent.putExtra("type",1);
					showImageIntent.putExtra("pos",position);
					showImageIntent.putExtra("img_list",(Serializable)mImageList);
					startActivityForResult(showImageIntent, 1);
				}

			}else if(mImageList.get(position).mType == 1){
				if(mAdapter.getIsDelete()){
					mAdapter.setIsDelete(false);
					mAdapter.notifyDataSetChanged();
				}else {
					if(mImageList.size() - 1 >= IMAGE_MAX){
						Toast.makeText(mContext, mContext.getString(R.string.upload_image_max), Toast.LENGTH_SHORT).show();
						return;
					}
					selectImg();
				}

			}
		}else {
			if(mAdapter.getIsDelete()){
				mAdapter.setIsDelete(false);
				mAdapter.notifyDataSetChanged();
			}
		}
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mImageList != null){
			for (int i = 0; i < mImageList.size(); i++) {
				deleteImgFile(mImageList.get(i).mPicPath);
				if(!TextUtils.isEmpty(mImageList.get(i).mPicPath)){
					ImageView view = (ImageView) mMyGridView.findViewWithTag(mImageList.get(i).mPicPath);
					if(view != null){
						view.setImageBitmap(null);
					}
				}
			}
		}

		if(mAdapter != null){
			FeatureFunction.freeBitmap(mAdapter.getImageBuffer());
		}

		mGoodsLogoImageView.setImageBitmap(null);
		if(mGoodsBitmapLogo != null && !mGoodsBitmapLogo.isRecycled()){
			mGoodsBitmapLogo.recycle();
			mGoodsBitmapLogo = null;
		}
	}

	private void deleteImgFile(String path){
		File file = new File(path);
		if(file != null && file.exists()){
			file.delete();
		}
	}

	private void selectImg(){
		MMAlert.showAlert(mContext,"",
				mContext.getResources().getStringArray(R.array.camer_item), 
				null, new OnAlertSelectId() {

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


	private void getImageFromCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		String tempUrl = FeatureFunction.getPhotoFileName(1);
		if(FeatureFunction.newFolder(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY)){
			File out = new File(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY, tempUrl);
			Uri uri = Uri.fromFile(out);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

			startActivityForResult(intent, GlobalParam.REQUEST_GET_IMAGE_BY_CAMERA);
		}
	}

	private void getImageFromGallery() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");

		startActivityForResult(intent, GlobalParam.REQUEST_GET_URI);
	}


	private void doChoose(final boolean isGallery, final Intent data) {
		if(isGallery){
			originalImage(data);
		}else {
			if(data != null){
				originalImage(data);
			}else{
				// Here if we give the uri, we need to read it
				String tempUrl = WeiYuanCommon.getCamerUrl(mContext);
				String path = Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY+tempUrl;
				Log.e("path", "path:"+path);
				if(tempUrl == null || tempUrl.equals("")){
					Toast.makeText(mContext, "图片不存在!", Toast.LENGTH_LONG).show();
					return;
				}
				Log.e("start-end", path.indexOf(".")+":"+path.length());
				String extension = path.substring(path.indexOf("."), path.length());
				if(FeatureFunction.isPic(extension)){
					//startPhotoZoom(Uri.fromFile(new File(path)));
					Intent intent = new Intent(mContext, RotateImageActivity.class);
					intent.putExtra("path", path);
					intent.putExtra("type", 0);
					startActivityForResult(intent, GlobalParam.REQUEST_GET_BITMAP);
				}
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
		//Log.d("may", "uri=" + uri + ", authority=" + uri.getAuthority());
		if (!TextUtils.isEmpty(uri.getAuthority())) {
			Cursor cursor = getContentResolver().query(uri,
					new String[] { MediaStore.Images.Media.DATA }, null, null,
					null);
			if (null == cursor) {
				//Toast.makeText(mContext, R.string.no_found, Toast.LENGTH_SHORT).show();
				return;
			}
			cursor.moveToFirst();
			String path = cursor.getString(cursor
					.getColumnIndex(MediaStore.Images.Media.DATA));
			Log.d("may", "path=" + path);
			String extension = path.substring(path.lastIndexOf("."), path.length());
			if(FeatureFunction.isPic(extension)){
				Intent intent = new Intent(mContext, RotateImageActivity.class);
				intent.putExtra("path", path);
				startActivityForResult(intent, GlobalParam.REQUEST_GET_BITMAP);

				//startPhotoZoom(data.getData());

			}else {
				//Toast.makeText(mContext, R.string.please_choose_pic, Toast.LENGTH_SHORT).show();
			}
			//ShowBitmap(false);


		} else {
			Log.d("may", "path=" + uri.getPath());
			String path = uri.getPath();
			String extension = path.substring(path.lastIndexOf("."), path.length());
			if(FeatureFunction.isPic(extension)){
				Intent intent = new Intent(mContext, RotateImageActivity.class);
				intent.putExtra("path", path);
				startActivityForResult(intent, GlobalParam.REQUEST_GET_BITMAP);
			}else {
				//Toast.makeText(mContext, R.string.please_choose_pic, Toast.LENGTH_SHORT).show();
			}
			//mImageFilePath = uri.getPath();
			//ShowBitmap(false);
		}
	}




}
