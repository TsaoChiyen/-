package com.chengxin;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chengxin.R;
import com.chengxin.Entity.Goods;
import com.chengxin.Entity.Merchant;
import com.chengxin.Entity.MerchantEntity;
import com.chengxin.Entity.ShoppingCart;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.ImageLoader;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.net.WeiYuanException;

/***
 * 购物车
 * @author dl
 *
 */
public class ShoppingCartActivity extends BaseActivity{

	private Button mCommitBtn;
	private String mGoodsId;

	private LinearLayout mShoppingCartLayout;
	private List<Merchant> mShoppingCartList = new ArrayList<Merchant>();
	private List<ShoppingCart> mCartList = new ArrayList<ShoppingCart>();
	private ImageLoader mImageLoader;

	/**订单数据*/
	private Merchant mOrderMerchantList;


	/*编辑or完成*/
	public boolean mIsModify;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.shopping_cart_view);
		mImageLoader = new ImageLoader();
		IntentFilter fileter = new IntentFilter();
		fileter.addAction(GlobalParam.ACTION_DESTROY_SHOPPING_CART_PAGE);
		registerReceiver(mDestroyReceiver, fileter);
		initCompent();
	}

	private BroadcastReceiver mDestroyReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent == null || 
					(intent.getAction() == null || intent.getAction().equals(""))){
				return;
			}
			String action = intent.getAction();
			if(action.equals(GlobalParam.ACTION_DESTROY_SHOPPING_CART_PAGE)){
				ShoppingCartActivity.this.finish();
			}
		}
	};

	private void initCompent(){
		String title = mContext.getResources().getString(R.string.shopping_cart)+"("+WeiYuanCommon.getGoodsCount()+")";
		setTitleContent(R.drawable.back_btn, R.drawable.modify_cart_btn,title);
		mLeftBtn.setOnClickListener(this);
		mRightBtn.setOnClickListener(this);
		mCommitBtn = (Button)findViewById(R.id.commit_btn);
		mCommitBtn.setOnClickListener(this);
		mShoppingCartLayout = (LinearLayout)findViewById(R.id.member_layout);

		mCartList = WeiYuanCommon.getShoppingCartData(mContext);
		if(mCartList != null && mCartList.size()>0){//加载购物车数据
			//shop/api/cartGoodsList
			StringBuilder buffer = new StringBuilder("{");		
			for (int i = 0; i < mCartList.size(); i++) {
				if(i != mCartList.size() - 1){
					buffer = buffer.append("\"").append(mCartList.get(i).shopId)
							.append("\"").append(":").append("\"").append(mCartList.get(i).goodsIds)
							.append("\"").append(",");
				}else{
					buffer = buffer.append("\"").append(mCartList.get(i).shopId)
							.append("\"").append(":").append("\"").append(mCartList.get(i).goodsIds)
							.append("\"").append("}");
				}
			}
			mGoodsId = buffer.toString();
			Log.e("goods_id",buffer.toString());
			if((mGoodsId != null && !mGoodsId.equals(""))
					&& !mGoodsId.startsWith(",") && !mGoodsId.endsWith(",")){
				getCartData();
			}

		}

	}

	/**设置购物车标题*/
	private void getTitleText(){
		String title = mContext.getResources().getString(R.string.shopping_cart)+"("+WeiYuanCommon.getGoodsCount()+")";
		titileTextView.setText(title);
	}


	/*获取购物车数据*/
	private void getCartData(){
		if(!WeiYuanCommon.getNetWorkState()){
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}

		new Thread(){
			public void run() {
				try {
					WeiYuanCommon.sendMsg(mBaseHandler,BASE_SHOW_PROGRESS_DIALOG,
							mContext.getResources().getString(R.string.get_dataing));
					MerchantEntity mMerchant =	WeiYuanCommon.getWeiYuanInfo().getShoppingCartList(mGoodsId);
					if(mMerchant.mMerchantList != null && mMerchant.mMerchantList.size()>0){
						List<Merchant> tempList = new ArrayList<Merchant>();
						tempList.addAll(mMerchant.mMerchantList);
						WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_SHOW_LOAD_DATA, tempList);
					}
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				} catch (NotFoundException e) {
					e.printStackTrace();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				} catch (WeiYuanException e) {
					e.printStackTrace();
					WeiYuanCommon.sendMsg(mBaseHandler,BASE_SHOW_PROGRESS_DIALOG,
							mContext.getResources().getString(e.getStatusCode()));
				}catch (Exception e) {
					e.printStackTrace();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				}
			};
		}.start();
	}

	/*显示购物车数据*/
	private void showShoppingCartData(){
		mCommitBtn.setVisibility(View.VISIBLE);
		if(mShoppingCartLayout != null && mShoppingCartLayout.getChildCount()>0){
			mShoppingCartLayout.removeAllViews();
		}
		for (int i = 0; i < mShoppingCartList.size(); i++) {
			Merchant merchant = mShoppingCartList.get(i);
			View view = LayoutInflater.from(mContext).inflate(R.layout.cart_item,null);
			TextView merchantTextView = (TextView)view.findViewById(R.id.member_name);
			merchantTextView.setText(merchant.name);

			final TextView orderPriceTextView = (TextView)view.findViewById(R.id.goods_price);
			LinearLayout checkLayout = (LinearLayout)view.findViewById(R.id.check_layout);
			CheckBox checkBox = (CheckBox)view.findViewById(R.id.check_button);
			if(i == 0){
				mOrderMerchantList = mShoppingCartList.get(i);
				checkBox.setChecked(true);
			}else{
				checkBox.setChecked(false);
			}
			final int index = i;

			checkLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					for (int j = 0; j < mShoppingCartLayout.getChildCount(); j++) {
						if(j == index){
							mOrderMerchantList = mShoppingCartList.get(j);
							((CheckBox)mShoppingCartLayout.getChildAt(j).findViewById(R.id.check_button)).setChecked(true);
						}else{
							((CheckBox)mShoppingCartLayout.getChildAt(j).findViewById(R.id.check_button)).setChecked(false);
						}
					}
				}
			});

			LinearLayout goodsLayout = (LinearLayout)view.findViewById(R.id.goods_layout);
			List<Goods> goodsList = merchant .goodsList;
			double merchantGoodsPrice=0;
			if(goodsList != null && goodsList.size()>0){
				if(goodsLayout != null && goodsLayout.getChildCount() >0){
					goodsLayout.removeAllViews();
				}
				for (int j = 0; j < goodsList.size(); j++) {
					final int childIndex = j;
					View childView = LayoutInflater.from(mContext).inflate(R.layout.cart_goods_item, null);
					ImageView goodsIcon = (ImageView)childView.findViewById(R.id.goods_icon);
					TextView goodsName = (TextView)childView.findViewById(R.id.goods_name);
					TextView goodsPrice = (TextView)childView.findViewById(R.id.money);
					final TextView goodsCountTextView = (TextView)childView.findViewById(R.id.goods_count_text);
					final EditText goodsCountEdit = (EditText)childView.findViewById(R.id.goods_count);
					goodsCountEdit.addTextChangedListener(new TextWatcher() {

						@Override
						public void onTextChanged(CharSequence s, int start, int before, int count) {

						}

						@Override
						public void beforeTextChanged(CharSequence s, int start, int count,
								int after) {

						}

						@Override
						public void afterTextChanged(Editable s) {
							int coun = mShoppingCartList.get(index) .goodsList.get(childIndex).count;
							double price = mShoppingCartList.get(index) .goodsList.get(childIndex).price;
							orderPriceTextView.setText("￥"+(coun*price));

						}
					});
					Button delBtn = (Button)childView.findViewById(R.id.deletebtn); //删除商品按钮
					delBtn.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							int childCountView = ((LinearLayout) mShoppingCartLayout.getChildAt(index).findViewById(R.id.goods_layout)).getChildCount();
							if(index == 0){
								mOrderMerchantList = null;
							}
							if(childCountView == 1){		//商铺下只有一个商品	
								mShoppingCartLayout.removeViewAt(index);
								for (int k = 0; k < mCartList.size(); k++) {
									if(mCartList.get(k).shopId == mShoppingCartList.get(index).id){
										mCartList.remove(mCartList.get(k));
										break;
									}
								}
								WeiYuanCommon.saveShoppingCartData(mContext, mCartList);
								mShoppingCartList.remove(mShoppingCartList.get(index));							
							}else {//商铺下有多个商品	
								((LinearLayout) mShoppingCartLayout.getChildAt(index).findViewById(R.id.goods_layout)).removeViewAt(childIndex);
								//++++++保存修改购物车数据++++
								modifyShoppingCartData(index);
								//---保存修改购物车数据----
							}
							if(mShoppingCartLayout.getChildCount() == 0){
								mCommitBtn.setVisibility(View.GONE);
							}
							getTitleText();
							//刷新商户界面中的商品数量
							sendBroadcast(new Intent(GlobalParam.ACTION_REFRESH_MERCHANT_GOODS_COUNT));
						}
					});

					ImageView mJianBtn = (ImageView)childView.findViewById(R.id.jian_btn);
					ImageView mAddBtn = (ImageView)childView.findViewById(R.id.add_btn);
					mJianBtn.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							int count = mShoppingCartList.get(index) .goodsList.get(childIndex).count;
							if(count <=1){
								Toast.makeText(mContext, "受不了了,宝贝不能在减少了哦", Toast.LENGTH_LONG).show();
								return;
							}
							mShoppingCartList.get(index) .goodsList.get(childIndex).count = count-1;
							int  goodsCount = count-1;
							goodsCountEdit.setText(goodsCount+"");
							goodsCountTextView.setText(goodsCount+"");
							modifyGoodsCount(index,goodsCount+"");
							getTitleText();
							//刷新商户界面中的商品数量
							sendBroadcast(new Intent(GlobalParam.ACTION_REFRESH_MERCHANT_GOODS_COUNT));
						}
					});

					mAddBtn.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							int count = mShoppingCartList.get(index) .goodsList.get(childIndex).count;
							mShoppingCartList.get(index) .goodsList.get(childIndex).count = count+1;
							int  goodsCount = count+1;
							goodsCountEdit.setText(goodsCount+"");
							modifyGoodsCount(index,goodsCount+"");
							goodsCountTextView.setText(goodsCount+"");
							getTitleText();
							//刷新商户界面中的商品数量
							sendBroadcast(new Intent(GlobalParam.ACTION_REFRESH_MERCHANT_GOODS_COUNT));
						}
					});




					Goods goods = goodsList.get(j);
					String goodsUrl = goods.logo;
					if(goodsUrl != null && !goodsUrl.equals("")){
						goodsIcon.setTag(goodsUrl);
						mImageLoader.getBitmap(mContext, goodsIcon, null, goodsUrl,0,false,true,false);
					}else{
						goodsIcon.setImageResource(R.drawable.contact_default_header);
					}

					goodsName.setText(goods.name);
					goodsPrice.setText("￥"+goods.price);
					String goodsCount = "";
					for (int k = 0; k < mCartList.size(); k++) {
						ShoppingCart cart = mCartList.get(i);
						String goodsIds = cart.goodsIds;
						if(goodsIds.contains(",")){
							String[] goodsArray = goodsIds.split(",");
							for (int l = 0; l < goodsArray.length; l++) {
								if(goodsArray[l].equals(goods.id)){
									String[] goodsCountArray = cart.goodsCounts.split(",");
									goodsCount = goodsCountArray[l];
									break;
								}
							}
						}else{
							goodsCount  = cart.goodsCounts;
						}
					}
					goodsCountTextView.setText(goodsCount);
					goodsCountEdit.setText(goodsCount);
					int gCount = 0;
					if(goodsCount != null && !goodsCount.equals("")){
						try {
							gCount = Integer.parseInt(goodsCount);
							for (int k = 0; k < merchant .goodsList.size(); k++) {
								merchant .goodsList.get(k).count = gCount;
							}
							merchantGoodsPrice = merchantGoodsPrice+(gCount*goods.price);
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
					}

					goodsLayout.addView(childView);
				}
			}
			orderPriceTextView.setText("￥"+merchantGoodsPrice); //单个商家商品总价格

			mShoppingCartLayout.addView(view);
		}
		//  <include layout="@layout/cart_item" />
	}

	/**修改保存的购物车数据*/
	private void modifyShoppingCartData(int index){

		boolean isReturn = false;
		for (int k = 0; k < mCartList.size(); k++) {
			if(mCartList.get(k).shopId == mShoppingCartList.get(index).id){
				String[] goodsIds = mCartList.get(k).goodsIds.split(",");
				String[] goodsCounts =  mCartList.get(k).goodsCounts.split(",");
				List<Goods> goodsList = mShoppingCartList.get(index).goodsList;
				for (int l = 0; l < goodsIds.length; l++) {
					for (int m = 0; m < goodsList.size(); m++) {
						if(goodsIds[l].equals(goodsList.get(m).id)){
							mShoppingCartList.get(index).goodsList.remove(goodsList.get(m).id);
							goodsIds[l] = "";
							goodsCounts[l]="";

							//++++重新拼接商品id和商品数量++++
							String[] goodsString = spellGoodsIdAndGoodsCount(goodsIds,goodsCounts);

							if(goodsString[0] != null && !goodsString[0].equals("")){
								mCartList.get(k).goodsIds = goodsString[0];//商品id
							}

							if(goodsString[1] != null && !goodsString[1].equals("")){
								mCartList.get(k).goodsCounts = goodsString[1];//商品数量
							}
							//----重新拼接商品id和商品数量----
							isReturn = true;
							//保存购物车数据
							WeiYuanCommon.saveShoppingCartData(mContext, mCartList);
							break;
						}
						if(isReturn){
							break;
						}
					}
					if(isReturn){
						break;
					}
				}
				if(isReturn){
					break;
				}
			}
			if(isReturn){
				break;
			}
		}
	}


	/**修改商品数量*/
	private void modifyGoodsCount(int index,String goodsCount){
		boolean isReturn = false;
		for (int k = 0; k < mCartList.size(); k++) {
			if(mCartList.get(k).shopId == mShoppingCartList.get(index).id){
				String[] goodsIds = mCartList.get(k).goodsIds.split(",");
				String[] goodsCounts =  mCartList.get(k).goodsCounts.split(",");
				List<Goods> goodsList = mShoppingCartList.get(index).goodsList;
				for (int l = 0; l < goodsIds.length; l++) {
					for (int m = 0; m < goodsList.size(); m++) {
						if(goodsIds[l].equals(goodsList.get(m).id)){
							goodsCounts[l] = goodsCount;
							//++++重新拼接商品id和商品数量++++
							String[] goodsString = spellGoodsIdAndGoodsCount(goodsIds,goodsCounts);

							if(goodsString[0] != null && !goodsString[0].equals("")){
								mCartList.get(k).goodsIds = goodsString[0];//商品id
							}

							if(goodsString[1] != null && !goodsString[1].equals("")){
								mCartList.get(k).goodsCounts = goodsString[1];//商品数量
							}
							//保存购物车数据
							WeiYuanCommon.saveShoppingCartData(mContext, mCartList);
							isReturn = true;							
						}
						if(isReturn){
							return;
						}
					}
					if(isReturn){
						return;
					}
				}
			}
			if(isReturn){
				return;
			}
		}
	}



	/**拼接商品id和商品数量*/
	private String[] spellGoodsIdAndGoodsCount(String[] goodsIds,String[] goodsCounts){
		String[] spllArray = new String[2];
		String goodsId = "";
		String goodsCount = "";
		for (int n = 0; n < goodsIds.length; n++) {
			if(n != goodsIds.length - 1){
				if(goodsIds[n]!= null && !goodsIds[n].equals("")){
					goodsId = goodsId+goodsIds[n]+",";
				}

				if(goodsCounts[n] != null && !goodsCounts[n].equals("")){
					goodsCount = goodsCount+goodsCounts[n]+",";
				}

			}else {
				if(goodsIds[n]!= null && !goodsIds[n].equals("")){
					goodsId = goodsId+goodsIds[n];
				}

				if(goodsCounts[n] != null && !goodsCounts[n].equals("")){
					goodsCount = goodsCount+goodsCounts[n];
				}
			}
		}
		spllArray[0]=goodsId;
		spllArray[1]=goodsCount;
		return spllArray;
	}



	/*编辑购物车数据*/
	private void modifyCartData(){
		if(mShoppingCartLayout == null && mShoppingCartLayout.getChildCount() <=0){
			return;
		}
		for (int i = 0; i < mShoppingCartLayout.getChildCount(); i++) {
			LinearLayout layout =	(LinearLayout) mShoppingCartLayout.getChildAt(i).findViewById(R.id.goods_layout);
			for (int j = 0; j < layout.getChildCount(); j++) {
				if(mIsModify){
					layout.getChildAt(j).findViewById(R.id.deletebtn).setVisibility(View.VISIBLE);
					layout.getChildAt(j).findViewById(R.id.input_num_layout).setVisibility(View.VISIBLE);
					layout.getChildAt(j).findViewById(R.id.goods_count_text).setVisibility(View.GONE);
				}else{
					layout.getChildAt(j).findViewById(R.id.deletebtn).setVisibility(View.GONE);
					layout.getChildAt(j).findViewById(R.id.input_num_layout).setVisibility(View.GONE);
					layout.getChildAt(j).findViewById(R.id.goods_count_text).setVisibility(View.VISIBLE);
				}

			}
		}
	}




	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			ShoppingCartActivity.this.finish();
			break;
		case R.id.right_btn://修改购物车商品
			if(!mIsModify){
				mIsModify = true;
				mRightBtn.setImageResource(R.drawable.complet_modify_cart_btn);
				modifyCartData();
			}else{
				mIsModify = false;
				modifyCartData();
				mRightBtn.setImageResource(R.drawable.modify_cart_btn);

			}
			break;
		case R.id.commit_btn://提交
			if(mIsModify){
				Toast.makeText(mContext, "请完成购物车修改后在提交订单",Toast.LENGTH_LONG).show();
				return;
			}
			if(mOrderMerchantList == null){
				Toast.makeText(mContext, "请选择商品", Toast.LENGTH_LONG).show();
				return;
			}
			Intent commitIntent = new Intent();
			commitIntent.setClass(mContext, CommitOrderActivity.class);
			commitIntent.putExtra("entity",mOrderMerchantList);
			startActivity(commitIntent);
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
			case GlobalParam.MSG_SHOW_LOAD_DATA:
				List<Merchant> tempList =(List<Merchant>)msg.obj;
				if(tempList!= null && tempList.size() >0){
					mShoppingCartList.addAll(tempList);
					showShoppingCartData();
				}
				break;

			default:
				break;
			}
		}

	};

	@Override
	protected void onDestroy() {
		if(mDestroyReceiver != null){
			unregisterReceiver(mDestroyReceiver);
		}
		super.onDestroy();
	}
}
