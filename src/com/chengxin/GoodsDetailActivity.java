package com.chengxin;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.ImageView.ScaleType;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chengxin.R;
import com.chengxin.Entity.CommentGoodsState;
import com.chengxin.Entity.Goods;
import com.chengxin.Entity.Login;
import com.chengxin.Entity.Picture;
import com.chengxin.Entity.ShoppingCart;
import com.chengxin.Entity.WeiYuanState;
import com.chengxin.adapter.ViewPagerAdapter;
import com.chengxin.exception.SPException;
import com.chengxin.fragment.AdViewFragment;
import com.chengxin.global.FeatureFunction;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.GlobleType;
import com.chengxin.global.ImageLoader;
import com.chengxin.global.MD5;
import com.chengxin.global.ScreenUtils;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.net.WeiYuanException;
import com.chengxin.widget.MyScrollView;
import com.chengxin.widget.ViewPager;
import com.chengxin.widget.MyScrollView.OnScrollListener;
import com.chengxin.widget.ViewPager.OnPageChangeListener;

/**
 * 商品详细
 * 
 * @author dl
 * 
 */
public class GoodsDetailActivity extends BaseActivity implements
		OnScrollListener, OnPageChangeListener {

	private static final int SHOW_IMAGE = 111111;
	private static final int GET_IMAGE = 111112;
	private final static int RECYCLE_BEFORE_BITMAP = 111117;
	private final static int RECYCLE_AFTER_BITMAP = 111118;

	private MyScrollView myScrollView;
	private LinearLayout mBuyLayout;
	private WindowManager mWindowManager;
	private LinearLayout mFlowMenuLayout;
	private LinearLayout mBuyBtn;
//	private ImageView mCommentBtn;

	private RelativeLayout mDragLayout;

	private WebView mWebView;

	private RadioGroup mRadioGroup;
	/** * 手机屏幕宽度 */
	private int screenWidth;

	/** * 悬浮框View */
	private static View suspendView;

	/** * 悬浮框的参数 */
	private static WindowManager.LayoutParams suspendLayoutParams;

	/** * 购买布局的高度 */
	private int buyLayoutHeight;

	/** * myScrollView与其父类布局的顶部距离 */
	private int myScrollViewTop;

	/** * 购买布局与其父类布局的顶部距离 */
	private int buyLayoutTop;

	/** webview中的URL */
	private String mTextPicUrl;
	private String mCanShuUrl;

	// 产品参数
	// http://www.uestcedu.com/article.html?key_code=notice&article_id=13892
	// 图文详细

	/** 选择的radioButton id */
	private int mSelectRadioButtonId; // 1-图文详细 2-参评参数

	private LinearLayout mCollectionBtn;
	private TextView mColleciontTextView;
//	private LinearLayout mCommentLayout;
//	private TextView mMoreCommentBtn;
//	private TextView mCommentCount;
	private TextView mGoodsName, mGoodsPriceTextView;
	private LinearLayout mJoinShopBtn, mTelMerchantBtn;

	private Goods mGoods;
	private ImageLoader mImageLoader;

	private LinearLayout mLayoutCircle;
	private ProgressBar mProgressBar;
	private RelativeLayout mViewPagerLayout;
	private LinkedList<View> mDetailList;
	private List<Picture> mPicList = new ArrayList<Picture>();
	private ViewPager mViewPager;
	ViewPagerAdapter mPageAdapter;
	HashMap<Integer, SoftReference<Bitmap>> mBitmapCache;
	private int mPage = 1;
	private int mSize = 0;
	private int mStartSize = 0;
	private int mPageIndxe = 0;
	private int mAblumPosition;
	public static final int BITMAP_SIZE = 6;

	private Login mUser;
	private String mShopAddr, mShopPhone, mShopName;
	private int mShopType = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.goods_info_view);
		mTextPicUrl = "http://www.uestcedu.com/";
		mCanShuUrl = "http://www.uestcedu.com/article.html?key_code=notice&article_id=13892";
		mSelectRadioButtonId = 2;
		mGoods = (Goods) getIntent().getSerializableExtra("entity");
		mUser = (Login) getIntent().getSerializableExtra("user");
		mShopAddr = getIntent().getStringExtra("addr");
		mShopPhone = getIntent().getStringExtra("tel_phone");
		mShopName = getIntent().getStringExtra("shop_name");
		mShopType  = getIntent().getIntExtra("shop_type", 0);
		mImageLoader = new ImageLoader();
		IntentFilter fileter = new IntentFilter();
		fileter.addAction(GlobalParam.ACTION_DESTROY_GOODS_DETAIL_PAGE);
		fileter.addAction(GlobalParam.ACTION_REFRESH_MERCHANT_GOODS_COUNT);
		registerReceiver(mDestroyReceiver, fileter);
		initCompent();
		setText();
		getGoodsDetail();
	}

	private BroadcastReceiver mDestroyReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent == null
					|| (intent.getAction() == null || intent.getAction()
							.equals(""))) {
				return;
			}
			String action = intent.getAction();
			if (action.equals(GlobalParam.ACTION_DESTROY_GOODS_DETAIL_PAGE)) {
				List<ShoppingCart> mCartList = WeiYuanCommon
						.getShoppingCartData(mContext);
				if (mCartList != null && mCartList.size() > 0) {// 加载购物车数据
					for (int i = 0; i < mCartList.size(); i++) {
						if (mCartList.get(i).shopId == mGoods.shopid) {
							mCartList.remove(mCartList.get(i));
							WeiYuanCommon.saveShoppingCartData(mContext,
									mCartList);

							sendBroadcast(new Intent(
									GlobalParam.ACTION_REFRESH_MERCHANT_GOODS_COUNT));
							break;
						}
					}
				}
				GoodsDetailActivity.this.finish();
			} else if (action
					.equals(GlobalParam.ACTION_REFRESH_MERCHANT_GOODS_COUNT)) {
				goodsCountTextView.setText(WeiYuanCommon.getGoodsCount() + "");
			}
		}
	};

	private void initCompent() {
		setRightTextTitleContent(R.drawable.back_btn, 0, R.string.goods_info,
				WeiYuanCommon.getGoodsCount());
		mShoopingCartBtn.setOnClickListener(this);
		mLeftBtn.setOnClickListener(this);
		myScrollView = (MyScrollView) findViewById(R.id.my_scrollview);
		mBuyLayout = (LinearLayout) findViewById(R.id.buy);
		myScrollView.setOnScrollListener(this);
		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		screenWidth = ScreenUtils.getScreenWidth(mContext);
		mFlowMenuLayout = (LinearLayout) findViewById(R.id.flow_menu_layout);
		mDragLayout = (RelativeLayout) findViewById(R.id.drag_layout);
		mDragLayout.setOnClickListener(this);

		mRadioGroup = (RadioGroup) findViewById(R.id.menu_tab_group);
		mRadioGroup.check(R.id.share);
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.like:
					// mFlowGroup.check(R.id.like);
					mSelectRadioButtonId = 1;
					setUrlForWebView(mTextPicUrl, false);
					break;
				case R.id.share:
					// mFlowGroup.check(R.id.share);
					mSelectRadioButtonId = 2;
					setUrlForWebView(mCanShuUrl, false);
					break;

				default:
					break;
				}
			}
		});

		mWebView = (WebView) findViewById(R.id.web_view);
		mWebView.getSettings().setDefaultTextEncodingName("UTF-8");
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
		setUrlForWebView(mCanShuUrl, false);
		mBuyBtn = (LinearLayout) findViewById(R.id.buy_btn);
		mBuyBtn.setOnClickListener(this);
//		mCommentBtn = (ImageView) findViewById(R.id.comment_btn);
//		mCommentBtn.setOnClickListener(this);
		mCollectionBtn = (LinearLayout) findViewById(R.id.collection_btn);
		mCollectionBtn.setOnClickListener(this);
		mColleciontTextView = (TextView) findViewById(R.id.collection_text);
//		mCommentLayout = (LinearLayout) findViewById(R.id.comment_layout);
//		mMoreCommentBtn = (TextView) findViewById(R.id.more_comment_btn);
//		mMoreCommentBtn.setOnClickListener(this);
//		mCommentCount = (TextView) findViewById(R.id.comment_count);
		mGoodsName = (TextView) findViewById(R.id.goods_name);
		mGoodsPriceTextView = (TextView) findViewById(R.id.goods_price);

		mJoinShopBtn = (LinearLayout) findViewById(R.id.join_shop_btn);
		mTelMerchantBtn = (LinearLayout) findViewById(R.id.tel_merchant_btn);
		mJoinShopBtn.setOnClickListener(this);
		mTelMerchantBtn.setOnClickListener(this);

		mViewPagerLayout = (RelativeLayout) findViewById(R.id.view_page);
		mProgressBar = (ProgressBar) findViewById(R.id.imageviewer_progressbar);
		mProgressBar.setVisibility(View.GONE);
		mBitmapCache = new HashMap<Integer, SoftReference<Bitmap>>();
		mDetailList = new LinkedList<View>();
		mLayoutCircle = (LinearLayout) findViewById(R.id.layoutCircle);
		mViewPager = (ViewPager) findViewById(R.id.detail_viewpager);

		int width = ScreenUtils.getScreenWidth(mContext);
		int height = (int)(0.75 * width);

		mViewPager.setLayoutParams(new RelativeLayout.LayoutParams(width,
				height));

		mPageAdapter = new ViewPagerAdapter(mDetailList);

		mViewPager.setAdapter(mPageAdapter);
	}

	private void setText() {
		if (mGoods == null) {
			return;
		}
		mGoodsName.setText(mGoods.name);
		mGoodsPriceTextView.setText("￥" + mGoods.price);
//		mCommentCount.setText("(" + mGoods.commentCount + ")");
		if (mGoods.isfavorite == 0) {
			mColleciontTextView.setText("收藏");
		} else {
			mColleciontTextView.setText("取消收藏");
		}
//		showNewCommentLayout();

		if (mGoods.pictureList != null && mGoods.pictureList.size() > 0) {
			mViewPagerLayout.setVisibility(View.VISIBLE);
			mPicList = mGoods.pictureList;
			mHandler.sendEmptyMessage(GET_IMAGE);
			showCircle(mPicList.size());
			// showPicForViewPage(mPicList.size());
		} else {
			mViewPagerLayout.setVisibility(View.GONE);
		}
	}

//	/* 更新商品的评论数 */
//	private void updataGoodsComment() {
//		mCommentCount.setText("(" + mGoods.commentCount + ")");
//		showNewCommentLayout();
//	}
//
//	/* 显示最新一条商品评论信息 */
//	public void showNewCommentLayout() {
//		if (mGoods == null || mGoods.comment == null) {
//			return;
//		}
//
//		mCommentLayout.setVisibility(View.VISIBLE);
//		mMoreCommentBtn.setVisibility(View.VISIBLE);
//		if (mCommentLayout != null && mCommentLayout.getChildCount() > 0) {
//			mCommentLayout.removeAllViews();
//		}
//		View view = LayoutInflater.from(mContext).inflate(
//				R.layout.goods_comment_item, null);
//		ImageView headIcon = (ImageView) view.findViewById(R.id.header_icon);
//		TextView username = (TextView) view.findViewById(R.id.user_name);
//		TextView createTime = (TextView) view.findViewById(R.id.time);
//		LinearLayout starLayout = (LinearLayout) view
//				.findViewById(R.id.star_layout);
//		TextView comment = (TextView) view.findViewById(R.id.comment);
//
//		if (mGoods.comment.user != null) {
//			username.setText(mGoods.comment.user.nickname);
//			String headUrl = mGoods.comment.user.headsmall;
//			if (headUrl != null && !headUrl.equals("")) {
//				mImageLoader.getBitmap(mContext, headIcon, null, headUrl, 0,
//						false, false, false);
//			} else {
//				headIcon.setImageResource(R.drawable.contact_default_header);
//			}
//		} else {
//			username.setText("");
//			headIcon.setImageResource(R.drawable.contact_default_header);
//		}
//		createTime.setText(FeatureFunction.calculaterReleasedTime(mContext,
//				new Date(mGoods.comment.createtime * 1000),
//				mGoods.comment.createtime * 1000, 0));
//		comment.setText(mGoods.comment.content);
//		int star = mGoods.comment.star;
//
//		if (star != 0) {
//			for (int i = 0; i < star; i++) {
//				TextView starTv = new TextView(mContext);
//				starTv.setLayoutParams(new LinearLayout.LayoutParams(
//						FeatureFunction.dip2px(mContext, 15),
//						LinearLayout.LayoutParams.WRAP_CONTENT));
//				starTv.setTextColor(Color.parseColor("#ffaa18"));
//				starTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
//				starTv.setText("★");
//				starLayout.addView(starTv);
//			}
//			int count = 0;
//			if (star < 5) {
//				count = 5 - star;
//			}
//			if (count > 0) {
//				for (int i = 0; i < count; i++) {
//					TextView starTv = new TextView(mContext);
//					starTv.setLayoutParams(new LinearLayout.LayoutParams(
//							FeatureFunction.dip2px(mContext, 15),
//							LinearLayout.LayoutParams.WRAP_CONTENT));
//					starTv.setTextColor(Color.parseColor("#BEBEBE"));
//					starTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
//					starTv.setText("★");
//					starLayout.addView(starTv);
//				}
//			}
//
//		} else {// 生成5个灰色星
//			for (int i = 0; i < 5; i++) {
//				TextView starTv = new TextView(mContext);
//				starTv.setLayoutParams(new LinearLayout.LayoutParams(
//						FeatureFunction.dip2px(mContext, 15),
//						LinearLayout.LayoutParams.WRAP_CONTENT));
//				starTv.setTextColor(Color.parseColor("#BEBEBE"));
//				starTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
//				starTv.setText("★");
//				starLayout.addView(starTv);
//			}
//		}
//		view.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent userInfoIntent = new Intent();
//				userInfoIntent.setClass(mContext, UserInfoActivity.class);
//				userInfoIntent.putExtra("type", 2);
//				userInfoIntent.putExtra("uid", mGoods.comment.uid);
//				startActivity(userInfoIntent);
//			}
//		});
//
//		mCommentLayout.addView(view);
//	}
//
	private void showCircle(int totalSize) {
		int size = totalSize;
		mLayoutCircle.removeAllViews();

		for (int i = 0; i < size; i++) {
			ImageView img = new ImageView(this);
			// img.setLayoutParams()
			if (mPageIndxe == i)
				img.setImageResource(R.drawable.image_uncheck);
			else
				img.setImageResource(R.drawable.image_check);
			img.setPadding(0, 0, 10, 0);
			mLayoutCircle.addView(img);
		}
	}

	/** 获取商品详细 */
	private void getGoodsDetail() {
		if (!WeiYuanCommon.getNetWorkState()) {
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread() {
			public void run() {
				try {
					WeiYuanCommon.sendMsg(mBaseHandler,
							BASE_SHOW_PROGRESS_DIALOG, mContext.getResources()
									.getString(R.string.get_dataing));
					Goods goods = WeiYuanCommon.getWeiYuanInfo()
							.getGoodsDetail(mShopType, mGoods.id);
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
					WeiYuanCommon.sendMsg(mHandler,
							GlobalParam.MSG_SHOW_LOAD_DATA, goods);
				} catch (WeiYuanException e) {
					e.printStackTrace();
					WeiYuanCommon.sendMsg(mBaseHandler, BASE_MSG_TIMEOUT_ERROR,
							mContext.getResources()
									.getString(e.getStatusCode()));
				} catch (Exception e) {
					e.printStackTrace();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				}

			};
		}.start();
	}

	private void setUrlForWebView(String url, boolean isTopRightBtn) {
		if (mSelectRadioButtonId == 2) {
			// mWebView.setScrollY(0);
			mWebView.setScrollY(buyLayoutTop);
		}
		mWebView.loadUrl(url);

	}

	/**
	 * 窗口有焦点的时候，即所有的布局绘制完毕的时候，我们来获取购买布局的高度和myScrollView距离父类布局的顶部位置
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			buyLayoutHeight = mBuyLayout.getHeight();
			buyLayoutTop = mBuyLayout.getTop();
			myScrollViewTop = myScrollView.getTop();
		}
	}

	/**
	 * 滚动的回调方法，当滚动的Y距离大于或者等于 购买布局距离父类布局顶部的位置，就显示购买的悬浮框 当滚动的Y的距离小于
	 * 购买布局距离父类布局顶部的位置加上购买布局的高度就移除购买的悬浮框
	 * 
	 */
	@Override
	public void onScroll(int scrollY) {
		if (scrollY >= buyLayoutTop) {
			// mFlowMenuLayout.setVisibility(View.VISIBLE);
			/*
			 * if(suspendView == null){ //showSuspend(); }
			 */
		} else if (scrollY <= buyLayoutTop + buyLayoutHeight) {
			// mFlowMenuLayout.setVisibility(View.GONE);
			/*
			 * if(suspendView != null){ //removeSuspend(); }
			 */
		}
	}

	/**
	 * 显示购买的悬浮框
	 */
	private void showSuspend() {
		if (suspendView == null) {
			suspendView = LayoutInflater.from(GoodsDetailActivity.this)
					.inflate(R.layout.goods_flow_view, null);
			RadioGroup radioGroup = (RadioGroup) findViewById(R.id.menu_tab_group);
			if (mSelectRadioButtonId == 1) {
				radioGroup.check(R.id.like);
				setUrlForWebView(mTextPicUrl, false);
			} else if (mSelectRadioButtonId == 2) {
				radioGroup.check(R.id.share);
				setUrlForWebView(mCanShuUrl, false);
			}

			radioGroup
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(RadioGroup group,
								int checkedId) {
							switch (checkedId) {
							case R.id.like:
								mSelectRadioButtonId = 1;
								setUrlForWebView(mTextPicUrl, false);
								break;
							case R.id.share:
								mSelectRadioButtonId = 2;
								setUrlForWebView(mCanShuUrl, false);
								break;

							default:
								break;
							}
						}
					});
			if (suspendLayoutParams == null) {
				suspendLayoutParams = new LayoutParams();
				suspendLayoutParams.type = LayoutParams.TYPE_PHONE;
				suspendLayoutParams.format = PixelFormat.RGBA_8888;
				suspendLayoutParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
						| LayoutParams.FLAG_NOT_FOCUSABLE;
				suspendLayoutParams.gravity = Gravity.TOP;
				suspendLayoutParams.width = screenWidth;
				suspendLayoutParams.height = buyLayoutHeight;
				suspendLayoutParams.x = 0;
				suspendLayoutParams.y = myScrollViewTop;
			}
		}

		mWindowManager.addView(suspendView, suspendLayoutParams);
	}

	/**
	 * 移除购买的悬浮框
	 */
	private void removeSuspend() {
		if (suspendView != null) {
			mWindowManager.removeView(suspendView);
			suspendView = null;
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			GoodsDetailActivity.this.finish();
			break;
		case R.id.drag_layout:// 查看图文详情
			if (mGoods == null) {
				return;
			}
			Intent detailIntent = new Intent();
			detailIntent.setClass(mContext, PicInfoActivity.class);
			detailIntent.putExtra("content", mGoods.content);
			detailIntent.putExtra("parameter", mGoods.parameter);
			startActivity(detailIntent);
			break;
		case R.id.collection_btn:// 收藏
			if (mGoods == null || (mGoods.id == null || mGoods.id.equals(""))) {
				return;
			}
			collection(mGoods.isfavorite == 0 ? 1 : 0);
			break;
		case R.id.shooping_count:// 添加到购物车
			Intent cartIntent = new Intent();
			cartIntent.setClass(mContext, ShoppingCartActivity.class);
			startActivity(cartIntent);
			break;
		case R.id.buy_btn: // 购物车数量加1
			List<ShoppingCart> shoppingList = WeiYuanCommon
					.getShoppingCartData(mContext);
			if (shoppingList == null) {
				shoppingList = new ArrayList<ShoppingCart>();
			}
			ShoppingCart shoppingCart = null;
			int index = -1;
			if (shoppingList != null && shoppingList.size() > 0) {
				for (int i = 0; i < shoppingList.size(); i++) {
					if (mGoods.shopid == shoppingList.get(i).shopId) {
						shoppingCart = shoppingList.get(i);
						index = i;
						break;
					}
				}
			}
			boolean isAdd = false;

			if (shoppingCart != null) {// 1.商铺存在
				isAdd = true;
				String goodsIds = shoppingCart.goodsIds;
				if ((goodsIds != null && !goodsIds.equals(""))
						&& !goodsIds.startsWith(",") && !goodsIds.endsWith(",")) {

					if (goodsIds.contains(",")) { // 包含多个商品
						String[] goodsId = goodsIds.split(",");
						if (goodsId != null && goodsId.length > 0) {
							boolean isAddGoods = false;
							for (int i = 0; i < goodsId.length; i++) {
								if (goodsId[i].equals(mGoods.id)) { // 修改相同的商铺下的相同商品数量
									String[] goodsCount = shoppingCart.goodsCounts
											.split(","); // 获取相同商铺下的相同商品数量
									goodsCount[i] = goodsCount[i] + 1;
									shoppingCart.goodsCounts = "";
									for (int j = 0; j < goodsCount.length; j++) {
										if (j != goodsCount.length - 1) {
											shoppingCart.goodsCounts = shoppingCart.goodsCounts
													+ goodsCount[i] + ",";
										} else {
											shoppingCart.goodsCounts = shoppingCart.goodsCounts
													+ goodsCount[i];
										}
									}

									shoppingList
											.remove(shoppingList.get(index));
									shoppingList.add(shoppingCart);
									WeiYuanCommon.saveShoppingCartData(
											mContext, shoppingList);
									return;
								} else {
									isAddGoods = true;
								}
							}
							if (!isAddGoods) { // 在同一商铺下添加一个新的商品
								shoppingList.remove(shoppingList.get(index));
								shoppingCart.goodsIds = shoppingCart.goodsIds
										+ "," + mGoods.id;
								shoppingCart.goodsCounts = shoppingCart.goodsCounts
										+ "," + 1;
								shoppingList.add(shoppingCart);
								WeiYuanCommon.saveShoppingCartData(mContext,
										shoppingList);
							}
						}
					} else { // 商品存在 ----只有一个商品的情况
						String goodsId = goodsIds;
						if (goodsId.equals(mGoods.id)) { // 商品数量+1
							// shoppingList.remove(shoppingList.get(index));
							try {
								int count = 1 + Integer
										.parseInt(shoppingCart.goodsCounts);
								shoppingCart.goodsCounts = count + "";
								WeiYuanCommon.saveShoppingCartData(mContext,
										shoppingList);
							} catch (NumberFormatException e) {
								e.printStackTrace();
							}
						} else {// 不存在该商品 --- 重新添加一个包含商品数量和商品id的商品
							shoppingList.remove(shoppingList.get(index));
							shoppingCart.goodsIds = shoppingCart.goodsIds + ","
									+ mGoods.id;
							shoppingCart.goodsCounts = shoppingCart.goodsCounts
									+ "," + 1;
							shoppingList.add(shoppingCart);
							WeiYuanCommon.saveShoppingCartData(mContext,
									shoppingList);
						}
					}
				} else { // 商品不存在 ----重新添加一个包含商品数量和商品id的商品
					shoppingList.remove(shoppingList.get(index));
					shoppingCart.goodsIds = shoppingCart.goodsIds + ","
							+ mGoods.id;
					shoppingCart.goodsCounts = shoppingCart.goodsCounts + ","
							+ 1;
					shoppingList.add(shoppingCart);
					WeiYuanCommon.saveShoppingCartData(mContext, shoppingList);
				}
			}
			if (!isAdd) {// 2.商铺不存在 ----重新添加一个包含商品数量和商品id的商铺
				shoppingCart = new ShoppingCart(
						WeiYuanCommon.getUserId(mContext), mGoods.shopid,
						mGoods.id, 1 + "");
				shoppingList.add(shoppingCart);
				WeiYuanCommon.saveShoppingCartData(mContext, shoppingList);
			}
			goodsCountTextView.setText(WeiYuanCommon.getGoodsCount() + "");
			// 刷新商户界面中的商品数量
			sendBroadcast(new Intent(
					GlobalParam.ACTION_REFRESH_MERCHANT_GOODS_COUNT));
			Toast.makeText(mContext, "加入购物车成功!", Toast.LENGTH_LONG).show();
			break;
		case R.id.comment_btn:// 发布评论
			if (mGoods == null || (mGoods.id == null || mGoods.id.equals(""))) {
				return;
			}
			Intent realseCommentIntent = new Intent();
			realseCommentIntent.setClass(mContext, SendCommentActivity.class);
			realseCommentIntent.putExtra("goods_id", mGoods.id);
			startActivityForResult(realseCommentIntent,
					GlobleType.REQUEST_GOODS_COMMENT);
			break;
		case R.id.more_comment_btn:// 查看更多评论
			if (mGoods == null || (mGoods.id == null || mGoods.id.equals(""))) {
				return;
			}
			Intent CommentListIntent = new Intent();
			CommentListIntent
					.setClass(mContext, GoodsCommentListActivity.class);
			CommentListIntent.putExtra("goods_id", mGoods.id);
			startActivity(CommentListIntent);

			break;
		case R.id.join_shop_btn:// 进入店铺
			if (mGoods == null || (mGoods.id == null || mGoods.id.equals(""))) {
				return;
			}
			Intent goodsListIntent = new Intent();
			goodsListIntent.setClass(mContext, GoodsListActivity.class);
			goodsListIntent.putExtra("shopid", mGoods.shopid);
			goodsListIntent.putExtra("addr", mShopAddr);
			goodsListIntent.putExtra("tel_phone", mShopPhone);
			goodsListIntent.putExtra("shop_name", mShopName);
			goodsListIntent.putExtra("user", mUser);
			startActivity(goodsListIntent);
			break;
		case R.id.tel_merchant_btn:// 联系商家
			if (mGoods == null || mUser == null) {
				return;
			}
			Intent intent = new Intent(mContext, ChatMainActivity.class);
			mUser.mIsRoom = 100;
			intent.putExtra("data", mUser);
			intent.putExtra("addr", mShopAddr);
			intent.putExtra("tel_phone", mShopPhone);
			intent.putExtra("shop_name", mShopName);
			intent.putExtra("user", mUser);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	/* 收藏 */
	private void collection(final int action) {
		if (!WeiYuanCommon.getNetWorkState()) {
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread() {
			public void run() {
				try {
					WeiYuanCommon.sendMsg(mBaseHandler,
							BASE_SHOW_PROGRESS_DIALOG, mContext.getResources()
									.getString(R.string.commit_dataing));
					WeiYuanState state = WeiYuanCommon.getWeiYuanInfo()
							.cancleOrCollection(mShopType, mGoods.id, action);
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
					WeiYuanCommon.sendMsg(mHandler,
							GlobalParam.MSG_CHECK_FAVORITE_STATUS, state);
				} catch (WeiYuanException e) {
					e.printStackTrace();
					WeiYuanCommon.sendMsg(mBaseHandler,
							BASE_SHOW_PROGRESS_DIALOG, mContext.getResources()
									.getString(e.getStatusCode()));
				} catch (Exception e) {
					e.printStackTrace();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				}
			};
		}.start();
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GlobalParam.MSG_CHECK_FAVORITE_STATUS:
				WeiYuanState state = (WeiYuanState) msg.obj;
				if (state == null) {
					Toast.makeText(mContext, R.string.commit_data_error,
							Toast.LENGTH_LONG).show();
					return;
				}
				if (state.code == 0) {
					if (mGoods.isfavorite == 1) {
						mGoods.isfavorite = 0;
						mColleciontTextView.setText("收藏");
					} else {
						mGoods.isfavorite = 1;
						mColleciontTextView.setText("取消收藏");
					}
				}
				break;
			case GlobalParam.MSG_SHOW_LOAD_DATA:
				Goods goods = (Goods) msg.obj;
				if (goods != null) {
					mGoods = goods;
					setText();
				}
				break;
			case GET_IMAGE:

				if (mPicList != null && mPicList.size() != 0) {
					if (mPage == 1) {
						mAblumPosition = 0;
					}
					for (int i = mSize; i < mPicList.size(); i++) {
						mDetailList.add(null);
					}
					int length;
					if (BITMAP_SIZE < mPicList.size() - mSize) {
						length = BITMAP_SIZE;
					} else {
						length = mPicList.size() - mSize;
					}

					mStartSize = mSize;

					for (int i = 0; i < length; i++) {
						mDetailList.set(mStartSize + i,
								addView(GoodsDetailActivity.this));
						if (!mPicList.get(mStartSize + i).originUrl.equals("")) {
							// ProgressBar progressBar =
							// (ProgressBar)mDetailList.get(i).findViewById(R.id.progressbar);
							// progressBar.setVisibility(View.VISIBLE);
							downLoadImage(
									mPicList.get(mStartSize + i).originUrl,
									mStartSize + i, mStartSize);
						}
					}

					if (mPageAdapter != null) {
						mPageAdapter.notifyDataSetChanged();
					}

					mViewPager
							.setOnPageChangeListener(GoodsDetailActivity.this);
					mViewPager.setCurrentItem(mSize);
					mSize = mPicList.size();
				} else {
					// 没有没有房源图片
					/*
					 * if(mPage == 1){ Toast.makeText(getApplicationContext(),
					 * R.string.no_commodity, Toast.LENGTH_LONG).show();
					 * //AblumDetailActivity.this.finish(); }else {
					 * //Toast.makeText(getApplicationContext(), "���޸����Ʒ",
					 * Toast.LENGTH_LONG).show();
					 * mHandler.sendEmptyMessage(SHOW_CHOOSE_DIALOG); }
					 */

				}
				break;
			case SHOW_IMAGE:
				Bitmap bitmap = (Bitmap) msg.obj;
				int position = msg.arg1;
				if (mDetailList != null && mDetailList.get(position) != null) {
					ProgressBar progressBar = (ProgressBar) mDetailList.get(
							position).findViewById(R.id.progressbar);
					if (progressBar.getVisibility() == View.VISIBLE) {
						progressBar.setVisibility(View.GONE);
					}
					ImageView imageView = (ImageView) mDetailList.get(position)
							.findViewById(R.id.imageviewer_multitouchimageview);
					imageView.setImageBitmap(bitmap);
					imageView.setScaleType(ScaleType.FIT_CENTER);
					imageView.setVisibility(View.VISIBLE);
					if (position == 0 || position == mStartSize) {
						mBaseHandler
								.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
						mProgressBar.setVisibility(View.GONE);
					}
				}
				break;
			case RECYCLE_BEFORE_BITMAP:
				int beforeindex = msg.arg1;
				freeBefore(beforeindex);
				break;
			case RECYCLE_AFTER_BITMAP:
				int bitmapindex = msg.arg1;
				freeAfter(bitmapindex);
				break;

			default:
				break;
			}
		}

	};

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (arg0 == GlobleType.REQUEST_GOODS_COMMENT && arg1 == RESULT_OK) {
			if (arg2 != null) {
				CommentGoodsState commentState = (CommentGoodsState) arg2
						.getSerializableExtra("comment");
				if (commentState != null) {
					mGoods.commentCount = commentState.commentcount;
					mGoods.star = commentState.star;
					mGoods.comment = commentState.goodsComment;
//					updataGoodsComment();
					/*
					 * Goods goods = (Goods)msg.obj; if(goods != null){ mGoods =
					 * goods;
					 * 
					 * }
					 */

					setText();
				}
			}

		}
	}

	public View addView(Context context) {
		View view = null;
		view = LayoutInflater.from(context).inflate(R.layout.book_detail, null);

		ImageView imageView = (ImageView) view
				.findViewById(R.id.imageviewer_multitouchimageview);
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// mShowZoomInOutLayout.showLinearLayout();
			}
		});

		return view;
	}

	private void downLoadImage(final String mImageUrl, final int position,
			final int size) {
		new Thread() {
			@Override
			public void run() {
				byte[] imageData = null;
				if (mImageUrl != null) {
					try {
						File file = null;
						String fileName = new MD5().getMD5ofStr(mImageUrl);// url.replaceAll("/",
						if (FeatureFunction.checkSDCard()) {

							if (FeatureFunction.newFolder(Environment
									.getExternalStorageDirectory()
									+ ImageLoader.SDCARD_PICTURE_CACHE_PATH)) {
								file = new File(
										Environment
												.getExternalStorageDirectory()
												+ ImageLoader.SDCARD_PICTURE_CACHE_PATH,
										fileName);
								if (file != null && file.exists()) {
									try {
										FileInputStream fin = new FileInputStream(
												file.getPath());
										int length = fin.available();
										byte[] buffer = new byte[length];
										fin.read(buffer);
										fin.close();
										imageData = buffer;
									} catch (FileNotFoundException e) {
										e.printStackTrace();
									} catch (IOException e) {
										e.printStackTrace();
									}

								} else {
									if (WeiYuanCommon.getNetWorkState()) {
										imageData = getImage(
												new URL(mImageUrl), file,
												position);
									}
								}
								try {
									// Bitmap bitmap = null;
									Message message = new Message();
									if (imageData != null) {
										BitmapFactory.Options opt = new BitmapFactory.Options();
										opt.inSampleSize = 1;
										opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
										opt.inPurgeable = true;
										opt.inInputShareable = true;
										BitmpCache(
												position,
												new SoftReference<Bitmap>(
														BitmapFactory
																.decodeByteArray(
																		imageData,
																		0,
																		imageData.length,
																		opt)));
										if (mBitmapCache != null
												&& mBitmapCache.get(position) != null
												&& mBitmapCache.get(position)
														.get() != null
												&& !mBitmapCache.get(position)
														.get().isRecycled()) {
											message.obj = mBitmapCache.get(
													position).get();
										} else {
											message.obj = BitmapFactory
													.decodeByteArray(imageData,
															0, imageData.length);
										}
										// bitmap =
										// BitmapFactory.decodeByteArray(imageData,
										// 0,imageData.length);
									} else {
										// bitmap =
										// BitmapFactory.decodeResource(getResources(),
										// R.drawable.detailcover);
										BitmpCache(
												position,
												new SoftReference<Bitmap>(
														BitmapFactory
																.decodeResource(
																		getResources(),
																		R.drawable.detailcover)));
										if (mBitmapCache != null
												&& mBitmapCache.get(position) != null
												&& mBitmapCache.get(position)
														.get() != null
												&& !mBitmapCache.get(position)
														.get().isRecycled()) {
											message.obj = mBitmapCache.get(
													position).get();
										} else {
											message.obj = BitmapFactory
													.decodeResource(
															getResources(),
															R.drawable.detailcover);
										}
									}

									imageData = null;
									System.gc();
									message.arg1 = position;
									message.arg2 = size;
									message.what = SHOW_IMAGE;
									mHandler.sendMessage(message);
								} catch (Exception e) {
									Log.e("BookDetail", "Out of Memory");
									e.printStackTrace();
								}

							}
						}

					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (SPException e) {
						e.printStackTrace();
					}
				}
			}

		}.start();

	}

	private void writeBitmapToCache(byte[] imgData, File file) {

		FileOutputStream fos = null;
		BufferedOutputStream outPutBuffer = null;

		if (file != null) {
			try {
				if (!file.exists()) {
					file.createNewFile();
				}
				fos = new FileOutputStream(file);

				outPutBuffer = new BufferedOutputStream(fos);
				outPutBuffer.write(imgData);
				outPutBuffer.flush();
				fos.flush();

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (fos != null) {
						fos.close();
					}

					if (outPutBuffer != null) {
						outPutBuffer.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private byte[] getImage(URL path, File file, int position)
			throws SPException {
		HttpURLConnection conn = null;
		InputStream is = null;
		byte[] imgData = null;
		try {
			URL url = path;
			conn = (HttpURLConnection) url.openConnection();
			is = conn.getInputStream();
			// Get the length
			int length = (int) conn.getContentLength();
			if (length != -1) {
				imgData = new byte[length];
				byte[] temp = new byte[512];
				int readLen = 0;
				int destPos = 0;
				while ((readLen = is.read(temp)) > 0) {
					System.arraycopy(temp, 0, imgData, destPos, readLen);
					destPos += readLen;
					/*
					 * 显示加载图片的进度 Message message = new Message(); message.what =
					 * GlobalParam.SHOW_PROGRESS_DIALOG; message.arg1 =
					 * position; mHandler.sendMessage(message);
					 */
				}

				/*
				 * 隐藏加载图片的进度 Message message = new Message(); message.what =
				 * GlobalParam.HIDE_PROGRESS_DIALOG; message.arg1 = position;
				 * mHandler.sendMessage(message);
				 */

				if (file != null) {
					writeBitmapToCache(imgData, file);
				}
			}

			if (is != null) {
				is.close();
			}

			if (conn != null) {
				conn.disconnect();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			throw new SPException(R.string.exception_out_of_memory);
		}

		return imgData;
	}

	private void freeBitmap(HashMap<Integer, SoftReference<Bitmap>> cache) {
		if (cache.isEmpty()) {
			return;
		}
		for (SoftReference<Bitmap> bitmap : cache.values()) {
			if (bitmap != null && bitmap.get() != null
					&& !bitmap.get().isRecycled()) {
				bitmap.get().recycle();
				bitmap = null;

			}
		}
		cache.clear();
		System.gc();
	}

	private void freeBefore(final int position) {
		new Thread() {
			@Override
			public void run() {
				if (mBitmapCache.isEmpty()) {
					return;
				}

				if (mBitmapCache.size() >= 0) {
					for (int i = 0; i < position; i++) {
						if (mDetailList != null && mDetailList.get(i) != null) {
							ImageView imageView = (ImageView) mDetailList
									.get(i)
									.findViewById(
											R.id.imageviewer_multitouchimageview);
							imageView.setImageBitmap(null);
						}

						if (mBitmapCache.get(i) != null
								&& mBitmapCache.get(i).get() != null
								&& !mBitmapCache.get(i).get().isRecycled()) {
							mBitmapCache.get(i).get().recycle();
							mBitmapCache.get(i).clear();
							mBitmapCache.remove(i);
						}
					}
					if (mBitmapCache != null) {
						Log.e("mBitmapCache", "mBitmapCache.size() = "
								+ mBitmapCache.size());
					}
					System.gc();
				}
			}
		}.start();
	}

	private void freeAfter(final int position) {
		new Thread() {
			@Override
			public void run() {
				if (mBitmapCache.size() > 0) {

					for (int i = position; i < mPicList.size(); i++) {
						if (mDetailList != null && mDetailList.get(i) != null) {
							ImageView imageView = (ImageView) mDetailList
									.get(i)
									.findViewById(
											R.id.imageviewer_multitouchimageview);
							imageView.setImageBitmap(null);
						}

						if (mBitmapCache.get(i) != null
								&& mBitmapCache.get(i).get() != null
								&& !mBitmapCache.get(i).get().isRecycled()) {
							mBitmapCache.get(i).get().recycle();
							mBitmapCache.get(i).clear();
							mBitmapCache.remove(i);
						}
					}
					System.gc();
				}
			}
		}.start();
	}

	private void BitmpCache(int position, SoftReference<Bitmap> bitmap) {
		if (!mBitmapCache.containsKey(position)) {
			mBitmapCache.put(position, bitmap);
		}
	}

	@Override
	public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2) {

	}

	@Override
	public void onPageSelected(int position) {
		if (position + BITMAP_SIZE - 1 < mPicList.size()) {
			if (mDetailList.get(position + BITMAP_SIZE - 1) == null) {
				mDetailList.set(position + BITMAP_SIZE - 1,
						addView(GoodsDetailActivity.this));
			}
			if (!mPicList.get(position + BITMAP_SIZE - 1).originUrl.equals("")) {
				downLoadImage(
						mPicList.get(position + BITMAP_SIZE - 1).originUrl,
						position + BITMAP_SIZE - 1, mStartSize);
			}

		}
		if (position > mAblumPosition && position + 3 < mPicList.size()
				&& position - 3 >= 0) {
			downLoadImage(mPicList.get(position + 3).originUrl, position + 3,
					mStartSize);
			Message msg = new Message();
			msg.what = RECYCLE_BEFORE_BITMAP;
			msg.arg1 = position - 3;
			mHandler.sendMessage(msg);
		}
		if (position < mAblumPosition && position - 3 >= 0
				&& position + 3 < mPicList.size()) {
			downLoadImage(mPicList.get(position - 3).originUrl, position - 3,
					mStartSize);
			Message msg = new Message();
			msg.what = RECYCLE_AFTER_BITMAP;
			msg.arg1 = position + 3;
			mHandler.sendMessage(msg);
		}
		mAblumPosition = position;
		mPageIndxe = position;
		showCircle(mSize);

	}

	@Override
	public void onPageScrollStateChanged(int paramInt) {

	}

	@Override
	protected void onDestroy() {
		freeBitmap(mBitmapCache);
		if (mDestroyReceiver != null) {
			unregisterReceiver(mDestroyReceiver);
		}
		super.onDestroy();
	}

}
