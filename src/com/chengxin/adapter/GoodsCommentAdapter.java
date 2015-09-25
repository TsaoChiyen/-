package com.chengxin.adapter;

import java.util.Date;
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
import com.chengxin.Entity.GoodsComment;
import com.chengxin.global.FeatureFunction;
import com.chengxin.global.ImageLoader;

/**
 * 商品评论适配器
 * @author dongli
 *
 */
public class GoodsCommentAdapter extends BaseAdapter{

	private Context mContext;
	private List<GoodsComment> mDataList;
	private ImageLoader mImageLoader;


	public GoodsCommentAdapter(Context context,List<GoodsComment> list) {
		mContext = context;
		mDataList = list;
		mImageLoader = new ImageLoader();
	}

	@Override
	public int getCount() {
		return mDataList.size();
	}

	@Override
	public Object getItem(int position) {
		return mDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;  

		if (convertView==null) {  
			convertView=LayoutInflater.from(mContext).inflate(R.layout.goods_comment_item, null);   
			holder=new ViewHolder();  

			holder.mHeaderView = (ImageView)convertView.findViewById(R.id.header_icon);
			holder.mUserNameTextView = (TextView) convertView.findViewById(R.id.user_name);
			holder.mCreateTimeTextView = (TextView)convertView.findViewById(R.id.time);
			holder.mStarLayout = (LinearLayout)convertView.findViewById(R.id.star_layout);
			holder.mContentTextView = (TextView)convertView.findViewById(R.id.comment);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();  
		}

		GoodsComment item = mDataList.get(position);
		if(item.user != null){
			holder.mUserNameTextView.setText(item.user.nickname);
			String headerUrl = item.user.headsmall;
			if(headerUrl != null && !headerUrl.equals("")){
				holder.mHeaderView.setTag(headerUrl);
				mImageLoader.getBitmap(mContext, holder.mHeaderView,null,headerUrl,0,false,false,false);
			}else{
				holder.mHeaderView.setImageResource(R.drawable.contact_default_header);
			}
		}else{
			holder.mUserNameTextView.setText("");
			holder.mHeaderView.setImageResource(R.drawable.contact_default_header);

		}
		holder.mContentTextView.setText(item.content);
		holder.mCreateTimeTextView.setText(FeatureFunction.
				calculaterReleasedTime(mContext,new Date(item.createtime*1000), item.createtime*1000, 0));
		int star = item.star;
		if(holder.mStarLayout != null && holder.mStarLayout.getChildCount()>0){
			holder.mStarLayout.removeAllViews();
		}

		if( star !=0){
			for (int i = 0; i <star; i++) {
				TextView starTv = new TextView(mContext);
				starTv.setLayoutParams(new LinearLayout.LayoutParams(FeatureFunction.dip2px(mContext, 15),LinearLayout.LayoutParams.WRAP_CONTENT));
				starTv.setTextColor(Color.parseColor("#ffaa18"));
				starTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				starTv.setText("★");
				holder.mStarLayout.addView(starTv);
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
					holder.mStarLayout.addView(starTv);
				}
			}

		}else{//生成5个灰色星
			for (int i = 0; i <5; i++) {
				TextView starTv = new TextView(mContext);
				starTv.setLayoutParams(new LinearLayout.LayoutParams(FeatureFunction.dip2px(mContext, 15),LinearLayout.LayoutParams.WRAP_CONTENT));
				starTv.setTextColor(Color.parseColor("#BEBEBE"));
				starTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				starTv.setText("★");
				holder.mStarLayout.addView(starTv);
			}
		}

		



		return convertView;
	}


	final static class ViewHolder {  
		ImageView mHeaderView;
		TextView mUserNameTextView;  
		TextView mCreateTimeTextView;
		LinearLayout mStarLayout;
		TextView mContentTextView;


		@Override
		public int hashCode() {
			return this.mUserNameTextView.hashCode() + mContentTextView.hashCode() + 
					mCreateTimeTextView.hashCode() + mHeaderView.hashCode()
					+ mStarLayout.hashCode();
		}
	} 

}
