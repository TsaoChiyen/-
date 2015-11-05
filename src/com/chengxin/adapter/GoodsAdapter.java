package com.chengxin.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chengxin.R;
import com.chengxin.Entity.Goods;
import com.chengxin.global.FeatureFunction;
import com.chengxin.global.ImageLoader;

public class GoodsAdapter extends BaseAdapter{	
	private List<Goods> goodsList;
	private Context mContext;
	private final LayoutInflater mInflater;
	private ImageLoader mImageLoader;

	public GoodsAdapter(Context context,List<Goods> list) {
		mInflater = (LayoutInflater)context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
		goodsList =list;
		mImageLoader = new ImageLoader();
	}

	@Override
	public int getCount() {
		return goodsList.size();
	}

	@Override
	public Object getItem(int position) {
		return goodsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;  

		if (convertView==null) {  
			convertView=mInflater.inflate(R.layout.goods_list_item, null);   
			holder=new ViewHolder();  

			holder.goodsName = (TextView) convertView.findViewById(R.id.user_name);
			holder.goodsPrice = (TextView) convertView.findViewById(R.id.money);
			holder.goodsIcon = (ImageView) convertView.findViewById(R.id.header_icon);
			holder.starLayout = (LinearLayout) convertView.findViewById(R.id.star_layout);

			convertView.setTag(holder);  
		}else {
			holder=(ViewHolder) convertView.getTag();  
		}
		Goods item = goodsList.get(position);
		if(item.logo != null && !item.logo.equals("")){
			mImageLoader.getBitmap(mContext, holder.goodsIcon,null,item.logo,0,false,false,false);
			holder.goodsIcon.setTag(item.logo);
		}else{
			holder.goodsIcon.setImageResource(R.drawable.goods_noraml);
		}

		holder.goodsPrice.setText("￥"+item.price);
		holder.goodsName.setText(item.name);
		int star = item.star;
		if(holder.starLayout != null && holder.starLayout.getChildCount()>0){
			holder.starLayout.removeAllViews();
		}

		if( star !=0){
			for (int i = 0; i <star; i++) {
				TextView starTv = new TextView(mContext);
				starTv.setLayoutParams(new LinearLayout.LayoutParams(FeatureFunction.dip2px(mContext, 15),LinearLayout.LayoutParams.WRAP_CONTENT));
				starTv.setTextColor(Color.parseColor("#ffaa18"));
				starTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				starTv.setText("★");
				holder.starLayout.addView(starTv);
			}
			int count = 0;
			if(star < 5){
				count = 5-star;
			}
			if(count >0){
				for (int i = 0; i <count; i++) {
					TextView starTv = new TextView(mContext);
					starTv.setLayoutParams(new LinearLayout.LayoutParams(FeatureFunction.dip2px(mContext, 15),LinearLayout.LayoutParams.WRAP_CONTENT));
					starTv.setTextColor(Color.parseColor("#BEBEBE"));
					starTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
					starTv.setText("★");
					holder.starLayout.addView(starTv);
				}
			}

		}else{//生成5个灰色星
			for (int i = 0; i <5; i++) {
				TextView starTv = new TextView(mContext);
				starTv.setLayoutParams(new LinearLayout.LayoutParams(FeatureFunction.dip2px(mContext, 15),LinearLayout.LayoutParams.WRAP_CONTENT));
				starTv.setTextColor(Color.parseColor("#BEBEBE"));
				starTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				starTv.setText("★");
				holder.starLayout.addView(starTv);
			}
		}
		return convertView;
	}


	final static class ViewHolder {  
		TextView goodsName;  
		TextView goodsPrice;
		ImageView goodsIcon;
		LinearLayout starLayout;

		@Override
		public int hashCode() {
			return this.goodsName.hashCode() + goodsPrice.hashCode() + 
					goodsIcon.hashCode() + starLayout.hashCode();
		}
	} 


}
