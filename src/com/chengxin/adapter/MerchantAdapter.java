package com.chengxin.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chengxin.GoodsDetailActivity;
import com.chengxin.GoodsListActivity;
import com.chengxin.R;
import com.chengxin.Entity.Goods;
import com.chengxin.Entity.Merchant;
import com.chengxin.global.FeatureFunction;
import com.chengxin.global.ImageLoader;
import com.chengxin.global.ScreenUtils;
import com.chengxin.widget.FlowView;

/***
 * 商户数据适配器
 * @author dl
 *
 */
public class MerchantAdapter extends BaseAdapter {

	private final LayoutInflater mInflater;
	private Context mContext;
	private List<Merchant> mMerchantsList ;
	private ImageLoader mImageLoader;
	private int mItemWidth;
	private static int mColorId = 0; //0-红 1-蓝色 2 -黄色


	public MerchantAdapter (Context context,List<Merchant> list){
		mContext = context;
		mInflater = (LayoutInflater)context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		mMerchantsList = list;
		mImageLoader = new ImageLoader();
		mItemWidth = (ScreenUtils.getScreenWidth(mContext)-FeatureFunction.dip2px(mContext, 40))/3;
	}

	@Override
	public int getCount() {
		return mMerchantsList.size();
	}

	@Override
	public Object getItem(int position) {
		return mMerchantsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public HashMap<String, Bitmap> getImageBuffer(){
		return mImageLoader.getImageBuffer();
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;  

		if (convertView==null) {  
			convertView=mInflater.inflate(R.layout.merchant_list_item, null);   
			holder=new ViewHolder();  


			holder.mGoodsTypeName = (TextView) convertView.findViewById(R.id.goods_tye_name);
			holder.mGoodsTypeIcon = (ImageView) convertView.findViewById(R.id.goods_type_icon);
			holder.mGoodsDesLayout = (LinearLayout) convertView.findViewById(R.id.goods_layout);
			holder.mTypeLayout = (RelativeLayout)convertView.findViewById(R.id.type_layout);
			convertView.setTag(holder);  
		}else {
			holder=(ViewHolder) convertView.getTag();  
		}

		final Merchant goods = mMerchantsList.get(position);
		holder.mGoodsTypeName.setText(goods.name);

		if(goods.logo != null && !goods.logo.equals("")){
			mImageLoader.getBitmap(mContext, holder.mGoodsTypeIcon,null, goods.logo, 0, false, false, false);
		}else{

			switch (position%3) {
			case 0:
				mColorId = 0;
				holder.mGoodsTypeIcon.setImageResource(R.drawable.red_right_arrow);
				holder.mGoodsTypeName.setTextColor(Color.parseColor("#eb3266"));
				break;
			case 1:
				mColorId = 1;
				holder.mGoodsTypeIcon.setImageResource(R.drawable.blue_right_arrow);
				holder.mGoodsTypeName.setTextColor(Color.parseColor("#40a6db"));
				break;
			case 2:
				mColorId = 2;
				holder.mGoodsTypeIcon.setImageResource(R.drawable.orange_right_arrow);
				holder.mGoodsTypeName.setTextColor(Color.parseColor("#f29735"));
				break;
			default:
				break;
			}
		}

		final List<Goods> goodsList = goods.goodsList;
		if(goodsList != null && goodsList.size() > 0){
			holder.mGoodsDesLayout.setVisibility(View.VISIBLE);
			holder.mGoodsDesLayout.removeAllViews();
			int padding = FeatureFunction.dip2px(mContext, 5);
			for (int i = 0; i < goodsList.size(); i++) {
				View view = mInflater.inflate(R.layout.goods_item,null);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mItemWidth, mItemWidth);
				params.setMargins(padding, padding, padding, padding);
				view.setLayoutParams(params);
				
				FlowView imageView =(FlowView)view.findViewById(R.id.imageview);
				if(goodsList.get(i).logo!=null && !goodsList.get(i).logo.equals("")){
					imageView.setTag(goodsList.get(i).logo);
					mImageLoader.getBitmap(mContext,imageView,null,goodsList.get(i).logo,0,false,false,false);
				}else{
					imageView.setImageResource(R.drawable.goods_noraml);
				}
				
				((TextView)view.findViewById(R.id.content)).setText("￥"+goodsList.get(i).price);
				final int index = i;
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						//商品详情页面
						Intent detailIntent = new Intent();
						detailIntent.setClass(mContext, GoodsDetailActivity.class);
						detailIntent.putExtra("entity", goodsList.get(index));
						detailIntent.putExtra("shopid", goods.id);
						detailIntent.putExtra("addr", goods.address);
						detailIntent.putExtra("tel_phone",goods.phone);
						detailIntent.putExtra("shop_name",goods.name);
						detailIntent.putExtra("user", goods.user);
						mContext.startActivity(detailIntent);
					}
				});

				holder.mGoodsDesLayout.addView(view);
			}
		}else{
			holder.mGoodsDesLayout.setVisibility(View.GONE);
		}
		holder.mTypeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mContext, GoodsListActivity.class);
				intent.putExtra("shopid", goods.id);
				intent.putExtra("addr", goods.address);
				intent.putExtra("tel_phone",goods.phone);
				intent.putExtra("shop_name",goods.name);
				intent.putExtra("user", goods.user);
				mContext.startActivity(intent);
			}
		});



		return convertView;
	}

	final static class ViewHolder {  
		ImageView mGoodsTypeIcon;
		TextView mGoodsTypeName; 
		LinearLayout mGoodsDesLayout;
		RelativeLayout mTypeLayout;


		@Override
		public int hashCode() {
			return this.mGoodsTypeIcon.hashCode() + mGoodsTypeName.hashCode()
					+ mGoodsDesLayout.hashCode()+ mTypeLayout.hashCode();
		}
	} 

}
