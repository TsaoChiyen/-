package com.chengxin.adapter;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chengxin.ChatMainActivity;
import com.chengxin.LocationActivity;
import com.chengxin.R;
import com.chengxin.ShowImageActivity;
import com.chengxin.Entity.FavoriteItem;
import com.chengxin.Entity.MessageType;
import com.chengxin.Entity.MovingContent;
import com.chengxin.Entity.MovingLoaction;
import com.chengxin.Entity.MovingPic;
import com.chengxin.Entity.MovingVoice;
import com.chengxin.global.FeatureFunction;
import com.chengxin.global.ImageLoader;

/**
 * 我的收藏数据适配器
 * @author dongli
 *
 */
public class MyFavoriteListAdapter extends BaseAdapter{

	private final LayoutInflater mInflater;
	private List<FavoriteItem> mData;
	private Context mContext;
	private ImageLoader mImageLoader;
	private List<FavoriteItem> mAllTempWeiboList = new ArrayList<FavoriteItem>();

	public MyFavoriteListAdapter(Context context, List<FavoriteItem> data){
		mInflater = (LayoutInflater)context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
		mData = data;
		mImageLoader = new ImageLoader();
	}

	
	
	public List<FavoriteItem> getmAllTempWeiboList() {
		return mAllTempWeiboList;
	}



	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder;  

		if (convertView==null || ((ViewHolder)convertView.getTag()).mTag != position) {  
			convertView=mInflater.inflate(R.layout.favorite_item, null);   
			holder=new ViewHolder();  

			holder.mHeaderView = (ImageView) convertView.findViewById(R.id.header);
			holder.mUserNameTextView = (TextView) convertView.findViewById(R.id.user_name);
			holder.mTimeTextView = (TextView)convertView.findViewById(R.id.time);
			holder.mContentTextView = (TextView) convertView.findViewById(R.id.content);
			holder.mPicView = (ImageView)convertView.findViewById(R.id.image_icon);	
			holder.mLocationAddress = (TextView)convertView.findViewById(R.id.chat_talk_msg_map);
			holder.mShowImageLayout = (RelativeLayout)convertView.findViewById(R.id.show_other);
			holder.mVoiceLayout = (RelativeLayout)convertView.findViewById(R.id.voice_layout);
			holder.mVoiceTimeTextView = (TextView)convertView.findViewById(R.id.voice_time);
			
			convertView.setTag(holder);  
			holder.mTag = position;
		}else {
			holder=(ViewHolder) convertView.getTag();  
		}

		 final FavoriteItem favoritetitem = mData.get(position);
		 if(favoritetitem.headsmall!=null && !favoritetitem.headsmall.equals("")){
			 mImageLoader.getBitmap(mContext, holder.mHeaderView, null, favoritetitem.headsmall, 0, false,true,false);
		 }
		 holder.mUserNameTextView.setText(favoritetitem.nicknaem);
		 holder.mTimeTextView.setText(FeatureFunction.calculaterReleasedTime(mContext, new Date((favoritetitem.createtime*1000)), favoritetitem.createtime*1000, 0));
		 switch (favoritetitem.typefile) {
		case MessageType.TEXT:
			holder.mVoiceLayout.setVisibility(View.GONE);
			holder.mShowImageLayout.setVisibility(View.GONE);
			MovingContent movingContent = MovingContent.getInfo(favoritetitem.content);
			holder.mContentTextView.setText(EmojiUtil.getExpressionString(mContext, movingContent.content, ChatMainActivity.EMOJIREX));
			break;
		case MessageType.MAP:
			holder.mVoiceLayout.setVisibility(View.GONE);
			holder.mContentTextView.setVisibility(View.GONE);
			holder.mShowImageLayout.setVisibility(View.VISIBLE);
			final MovingLoaction movingLoaction = MovingLoaction.getInfo(favoritetitem.content);
			//显示地图所列图示例
			
			String ImageURL = "http://api.map.baidu.com/staticimage?center="+movingLoaction.lng+","+movingLoaction.lat+
					"&width=200&height=120&zoom=16&markers="+movingLoaction.lng+","+movingLoaction.lat+"&markerStyles=s";
		/*	String ImageURL = "http://api.map.baidu.com/staticimage?center="+movingLoaction.lng+
					","+movingLoaction.lat+"&width=300&height=200&zoom=18";*/
			holder.mPicView.setVisibility(View.VISIBLE);
			holder.mPicView.setTag(ImageURL);
		
			//location_msg
			holder.mPicView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.location_msg));
			holder.mLocationAddress.setVisibility(View.VISIBLE);
			holder.mLocationAddress.setText(movingLoaction.address);
			mImageLoader.getBitmap(mContext, holder.mPicView, null, ImageURL, 0, false, false,false);
			break;
		case MessageType.VOICE:
			MovingVoice movingVoice = MovingVoice.getInfo(favoritetitem.content);
			holder.mVoiceLayout.setVisibility(View.VISIBLE);
			holder.mContentTextView.setVisibility(View.GONE);
			holder.mShowImageLayout.setVisibility(View.GONE);
			holder.mVoiceTimeTextView.setText(movingVoice.time);
			break;
		case MessageType.PICTURE:
			holder.mVoiceLayout.setVisibility(View.GONE);
			holder.mContentTextView.setVisibility(View.GONE);
			holder.mShowImageLayout.setVisibility(View.VISIBLE);
			final MovingPic movingPic = /*new MovingPic(favoritetitem.content);*/MovingPic.getInfo(favoritetitem.content);
			holder.mPicView.setVisibility(View.VISIBLE);
			String picUrl =  movingPic.urlsmall;
			if(picUrl == null || picUrl.equals("")){
				picUrl = movingPic.urllarge;
			}
			if(picUrl.startsWith("http://")){
				mImageLoader.getBitmap(mContext, holder.mPicView, null,picUrl, 0,false,false,false);
			}else{
				Bitmap bitmap = null;
				if(!mImageLoader.getImageBuffer().containsKey(picUrl)){
					bitmap = BitmapFactory.decodeFile(picUrl);
					mImageLoader.getImageBuffer().put(picUrl, bitmap);
				}else {
					bitmap = mImageLoader.getImageBuffer().get(picUrl);
				}
				if(bitmap!=null && !bitmap.isRecycled()){
					holder.mPicView.setImageBitmap(bitmap);
				}
			}
			
		
			break;

		default:
			break;
		}

		return convertView;
	}


	final static class ViewHolder { 
		int mTag;
		ImageView mHeaderView;
		TextView mUserNameTextView;  
		TextView mTimeTextView;  
		TextView mContentTextView;
		TextView mLocationAddress;
		ImageView mPicView;
		
		Button mCancelBtn;
		RelativeLayout mShowImageLayout;
		
		RelativeLayout mVoiceLayout;
		TextView mVoiceTimeTextView;

		@Override
		public int hashCode() {
			return this.mUserNameTextView.hashCode() + mContentTextView.hashCode() + 
					mCancelBtn.hashCode() + mHeaderView.hashCode()
					+ mLocationAddress.hashCode()+mShowImageLayout.hashCode()
					+ mVoiceLayout.hashCode()+ mVoiceTimeTextView.hashCode();
		}
	} 

}
