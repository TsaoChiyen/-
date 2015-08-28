package com.chengxin.sortlist;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.chengxin.R;
import com.chengxin.Entity.Login;
import com.chengxin.global.ImageLoader;

public class SortAdapter extends BaseAdapter implements SectionIndexer{
	private List<Login> list = null;
	private Context mContext;
	private ImageLoader mImageLoader;

	public SortAdapter(Context mContext, List<Login> list) {
		this.mContext = mContext;
		this.list = list;
		this.mImageLoader = new ImageLoader();
	}

	
	public HashMap<String, Bitmap> getImageBuffer(){
		return mImageLoader.getImageBuffer();
	}
	
	
	public void updateListView(List<Login> list){
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		final Login mContent = list.get(position);
		if (convertView == null || ((ViewHolder) convertView.getTag()).mTag != position) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.contact_item, null);
			holder.mHeadrIcon = (ImageView)convertView.findViewById(R.id.headerimage);
			holder.mNameTextView = (TextView)convertView.findViewById(R.id.username);

			holder.index = (TextView) convertView.findViewById(R.id.sortKey);
			holder.indexLayout = (RelativeLayout)convertView.findViewById(R.id.grouplayout);
			holder.mContentSplite = (ImageView)convertView.findViewById(R.id.content_splite);
			holder.mSignTextView = (TextView)convertView.findViewById(R.id.prompt);
			holder.newFriendsIcon= (TextView)convertView.findViewById(R.id.new_notify);
			//holder.contactLayout = (LinearLayout)convertView.findViewById(R.id.contact_layout);//select_contact_splite

			holder.mTag = position;
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		
		String sort = "";
		if(position >0){
			int preIndex = position - 1;
			sort = this.list.get(preIndex).sort;//FeatureFunction.formartTime(mData.get(preIndex).createtime, "yyyy-MM-dd");
		}
		if(list.get(position).sort.equals(sort)){
			holder.indexLayout.setVisibility(View.GONE);
			holder.index.setVisibility(View.GONE);
			holder.mContentSplite.setVisibility(View.VISIBLE);
		}else{
			holder.indexLayout.setVisibility(View.VISIBLE);
			holder.index.setVisibility(View.VISIBLE);
			holder.mContentSplite.setVisibility(View.VISIBLE);

			if(mContent.sortName == null || mContent.sortName.equals("")){
				holder.indexLayout.setVisibility(View.GONE);
				holder.index.setVisibility(View.GONE);
			}else{
				holder.index.setText(mContent.sortName);
			}
		}
		
	
		String name = this.list.get(position).remark;
		if(name == null || name.equals("")){
			name = this.list.get(position).nickname;
		}
		holder.mNameTextView.setText(name);
		if(this.list.get(position).sign!=null && !this.list.get(position).sign.equals("")){
			holder.mSignTextView.setVisibility(View.VISIBLE);
			holder.mSignTextView.setText(this.list.get(position).sign);
		}
		if(name.equals(mContext.getResources().getString(R.string.new_friends))){
			if(mContent.newFriends == 1){
				holder.newFriendsIcon.setVisibility(View.VISIBLE);
			}else{
				holder.newFriendsIcon.setVisibility(View.GONE);
			}
			//holder.mNameTextView.getPaint().setFakeBoldText(true);
			holder.mHeadrIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.new_friends_icon));
			//holder.mContentSplite.setVisibility(View.VISIBLE);
		}else if(name.equals(mContext.getResources().getString(R.string.room_chat))){
			//holder.mNameTextView.getPaint().setFakeBoldText(true);
			//holder.mContentSplite.setVisibility(View.VISIBLE);
			holder.mHeadrIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.service_icon));
		}else if(name.equals(mContext.getResources().getString(R.string.public_number))){
			//holder.mNameTextView.getPaint().setFakeBoldText(true);
			//holder.mContentSplite.setVisibility(View.VISIBLE);
			holder.mHeadrIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.gongzong_icon));
		}else{
			if(this.list.get(position).headsmall!=null && !this.list.get(position).headsmall.equals("")){
				holder.mHeadrIcon.setTag(this.list.get(position).headsmall);
				mImageLoader.getBitmap(mContext, holder.mHeadrIcon,null,this.list.get(position).headsmall,
						0,false,true,false);
			}else{
				holder.mHeadrIcon.setImageResource(R.drawable.contact_default_header);
			}
		}
		//}



		return convertView;

	}



	final static class ViewHolder {
		public int mTag;
		public ImageView mHeadrIcon,mContentSplite;
		public TextView mNameTextView,mSignTextView;

		public TextView index;
		public RelativeLayout indexLayout,contactLayout;
		public TextView newFriendsIcon;
	}


	
	public int getSectionForPosition(int position) {
		//Log.e("SortAdapter", "positon:"+position);
		if(list.get(position)!=null && list.get(position).sort!=null
				&& !list.get(position).sort.equals("")){
			//Log.e("SortAdapter_two", "positon:"+position);
			return list.get(position).sort.charAt(0);
		}
		return 0;
	}

	
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).sort;
			if(sortStr!=null && !sortStr.equals("")){
				if(sortStr.toUpperCase()!=null && !sortStr.toUpperCase().equals("")){
					char firstChar = sortStr.toUpperCase().charAt(0);
					if (firstChar == section) {
						return i;
					}
				}
				
			}
			
			
		}

		return -1;
	}

	
	private String getAlpha(String str) {
		String  sortStr = str.trim().substring(0, 1).toUpperCase();
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}