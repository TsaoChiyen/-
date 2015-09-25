package com.chengxin.fragment;import java.lang.reflect.Field;import java.util.ArrayList;import java.util.LinkedList;import java.util.List;import android.content.Context;import android.content.Intent;import android.os.Bundle;import android.os.Handler;import android.os.Message;import android.support.v4.app.Fragment;import android.view.LayoutInflater;import android.view.View;import android.view.View.OnClickListener;import android.view.ViewGroup;import android.widget.ImageView;import android.widget.LinearLayout;import android.widget.ProgressBar;import android.widget.RelativeLayout;import com.chengxin.R;import com.chengxin.Entity.Picture;import com.chengxin.adapter.ViewPagerAdapter;import com.chengxin.financial.FinancialMainActivity;import com.chengxin.financial.MyBillActivity;import com.chengxin.global.ScreenUtils;import com.chengxin.widget.ViewPager;import com.chengxin.widget.ViewPager.OnPageChangeListener;public class FinancialManagerFragment extends Fragment implements OnClickListener, OnPageChangeListener {	private static final int GET_IMAGE = 111112;	public static final int BITMAP_SIZE = 6;	private final static int RECYCLE_BEFORE_BITMAP = 111117;	private final static int RECYCLE_AFTER_BITMAP = 111118;	private Context mParentContext;	private View mView;	private RelativeLayout mViewPagerLayout;	private ProgressBar mProgressBar;	private LinkedList<View> mDetailList;	private LinearLayout mLayoutCircle;	private ViewPager mViewPager;	private ViewPagerAdapter mPageAdapter;	private List< Picture > mPicList = new ArrayList<Picture>();	protected int mPage = 1;	protected int mAblumPosition;	protected int mSize = 0;	private int mStartSize = 0;	private int mPageIndex = 0;	private RelativeLayout mBillManagerLayout;	private RelativeLayout mFinanceLayout;	@Override	public void onCreate(Bundle savedInstanceState) {		super.onCreate(savedInstanceState);		mParentContext = (Context) FinancialManagerFragment.this.getActivity();	}	@Override	public View onCreateView(LayoutInflater inflater, ViewGroup container,			Bundle savedInstanceState) {		mView = inflater.inflate(R.layout.finance_manager, container, false);		return mView;	}	@Override	public void onDestroy() {		super.onDestroy();	}	@Override	public void onActivityCreated(Bundle savedInstanceState) {		super.onActivityCreated(savedInstanceState);				mBillManagerLayout = (RelativeLayout)mView.findViewById(R.id.layout_bill_man);		mFinanceLayout = (RelativeLayout)mView.findViewById(R.id.layout_finance);		mBillManagerLayout.setOnClickListener(this);		mFinanceLayout.setOnClickListener(this);		mViewPagerLayout = (RelativeLayout)mView.findViewById(R.id.view_page);		mProgressBar = (ProgressBar) mView.findViewById(R.id.imageviewer_progressbar);		mProgressBar.setVisibility(View.GONE);//		mBitmapCache = new HashMap<Integer, SoftReference<Bitmap>>();		mDetailList = new LinkedList<View>();		mLayoutCircle = (LinearLayout) mView.findViewById(R.id.layoutCircle);		mViewPager = (ViewPager)mView.findViewById(R.id.detail_viewpager);		int width  = ScreenUtils.getScreenWidth(mParentContext);		int height = (width * 9) / 16;		mViewPager.setLayoutParams(new RelativeLayout.LayoutParams(width, height));		mViewPagerLayout.setLayoutParams(new LinearLayout.LayoutParams(width, height));		mViewPager.setOnPageChangeListener(this);		mPageAdapter = new ViewPagerAdapter(mDetailList);		mViewPager.setAdapter(mPageAdapter);		getAdvPictures();	}	public View addView(Context context) {		View view=null;		view = LayoutInflater.from(context).inflate(R.layout.book_detail, null);		ImageView imageView = (ImageView) view.findViewById(R.id.imageviewer_multitouchimageview);		imageView.setOnClickListener(new OnClickListener() {			@Override			public void onClick(View v) {				//mShowZoomInOutLayout.showLinearLayout();			}		});		return view;	}	private void getAdvPictures() {		mSize = 0;		mPicList.clear();		Picture pic = new Picture();		pic.originUrl = "adv_pic1";		mPicList.add(pic);				pic = new Picture();		pic.originUrl = "adv_pic2";		mPicList.add(pic);				mHandler.sendEmptyMessage(GET_IMAGE);		showCircle(mPicList.size());	}	private void loadImage(final String mImageUrl, final int position, final int size){		if(mDetailList != null && mDetailList.get(position) != null){			ProgressBar progressBar = (ProgressBar)mDetailList.get(position).findViewById(R.id.progressbar);			if(progressBar.getVisibility() == View.VISIBLE){				progressBar.setVisibility(View.GONE);			}						try {				String name = mImageUrl;				Field field = R.drawable.class.getDeclaredField(name);				int resId = field.getInt(null);				ImageView imageView = (ImageView)mDetailList.get(position).findViewById(R.id.imageviewer_multitouchimageview);				imageView.setImageResource(resId);				imageView.setVisibility(View.VISIBLE);								if(position == 0 || position == mStartSize){					mProgressBar.setVisibility(View.GONE);				}			} catch (NoSuchFieldException e) {				e.printStackTrace();			} catch (IllegalAccessException e) {				e.printStackTrace();			} catch (IllegalArgumentException e) {				e.printStackTrace();			}		}	}	private Handler mHandler = new Handler(){		@Override		public void handleMessage(Message msg) {			super.handleMessage(msg);			switch (msg.what) {			case GET_IMAGE:				if(mPicList != null && mPicList.size() != 0){					if(mPage == 1){						mAblumPosition = 0;					}					for(int i = mSize; i< mPicList.size(); i++){						mDetailList.add(null);					}					int length;					if(BITMAP_SIZE < mPicList.size()-mSize){						length = BITMAP_SIZE;					}else {						length = mPicList.size()-mSize;					}					mStartSize = mSize;					for(int i = 0; i< length; i++){						mDetailList.set(mStartSize+i, addView(mParentContext));						if (!mPicList.get(mStartSize+i).originUrl.equals("")) {							loadImage(mPicList.get(mStartSize+i).originUrl, mStartSize+i, mStartSize);						} 					}					if(mPageAdapter != null){						mPageAdapter.notifyDataSetChanged();					}										mViewPager.setCurrentItem(mSize);					mSize = mPicList.size();				}				break;			}		}	};		private void showCircle(int totalSize) {		int size = totalSize;		mLayoutCircle.removeAllViews();		for( int i = 0; i < size; i++){			ImageView img = new ImageView(mParentContext);			//img.setLayoutParams()			if ( mPageIndex == i)				img.setImageResource(R.drawable.image_uncheck);			else				img.setImageResource(R.drawable.image_check);			img.setPadding(0, 0, 10, 0);			mLayoutCircle.addView(img);		}	}	@Override	public void onClick(View v) {		switch (v.getId()) {		case R.id.layout_bill_man:			Intent intent = new Intent();			intent.setClass(mParentContext, MyBillActivity.class);			mParentContext.startActivity(intent);			break;		case R.id.layout_finance:			intent = new Intent();			intent.setClass(mParentContext, FinancialMainActivity.class);			mParentContext.startActivity(intent);			break;		default:			break;		}	}	@Override	public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2) {			}	@Override	public void onPageSelected(int position) {		if(position+BITMAP_SIZE-1 < mPicList.size()){			if(mDetailList.get(position+BITMAP_SIZE-1) == null){				mDetailList.set(position+BITMAP_SIZE-1, addView(mParentContext));			}			if (!mPicList.get(position+BITMAP_SIZE-1).originUrl.equals("")) {				loadImage(mPicList.get(position+BITMAP_SIZE-1).originUrl, position+BITMAP_SIZE-1, mStartSize);			} 		}		if(position > mAblumPosition && position + 3 < mPicList.size() && position - 3 >= 0){			loadImage(mPicList.get(position + 3).originUrl, position + 3, mStartSize);			Message msg = new Message();			msg.what = RECYCLE_BEFORE_BITMAP;			msg.arg1 = position - 3; 			mHandler.sendMessage(msg);		}		if(position < mAblumPosition && position - 3 >= 0 && position + 3 < mPicList.size()){			loadImage(mPicList.get(position - 3).originUrl, position - 3, mStartSize);			Message msg = new Message();			msg.what = RECYCLE_AFTER_BITMAP;			msg.arg1 = position + 3; 			mHandler.sendMessage(msg);		}				mAblumPosition = position;		mPageIndex = position;		showCircle(mSize);	}	@Override	public void onPageScrollStateChanged(int paramInt) {			}}