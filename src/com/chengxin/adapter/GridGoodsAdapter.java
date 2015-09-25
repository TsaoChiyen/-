package com.chengxin.adapter;import java.util.List;import com.chengxin.R;import com.chengxin.Entity.Goods;import com.chengxin.global.ImageLoader;import android.content.Context;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import android.widget.BaseAdapter;import android.widget.ImageView;import android.widget.LinearLayout;import android.widget.TextView;public class GridGoodsAdapter extends BaseAdapter {	private List <Goods> mList;	private Context mContext;	private LayoutInflater mInflater;	private ImageLoader mImageLoader = new ImageLoader(); 	public GridGoodsAdapter(Context context, List<Goods> list) {		super();		mList = list;		mContext = context;		mInflater = LayoutInflater.from(context); 	}	@Override	public int getCount() {		return mList.size();	}	@Override	public Object getItem(int position) {		return mList.get(position);	}	@Override	public long getItemId(int position) {		return Long.parseLong(mList.get(position).id);	}	@Override	public View getView(int position, View convertView, ViewGroup parent) {		ViewHolder holder;				if(convertView == null || ((ViewHolder)convertView.getTag()).mTag != position){			holder = new ViewHolder();			convertView = mInflater.inflate(R.layout.gridview_goods_item, null);			holder.mTag = position;			holder.mLayoutLogo = (LinearLayout)convertView.findViewById(R.id.layout_logo);			holder.mPrice = (TextView)convertView.findViewById(R.id.goods_price);			holder.mText = (TextView)convertView.findViewById(R.id.goods_name);			holder.mLogo = (ImageView)convertView.findViewById(R.id.goods_logo);			convertView.setTag(holder);		} else {			holder = (ViewHolder) convertView.getTag();		}				Goods item = mList.get(position);				if (item.logo != null && !item.logo.equals("") && !item.logo.equals("null")) {			mImageLoader.getBitmap(mContext, holder.mLogo, null, item.logo, 0, false, false, false);		}				holder.mText.setText(item.name);		holder.mPrice.setText(String.format("￥%.2f", item.price));				return convertView;	}	static class ViewHolder	{		int mTag;		TextView mPrice;		TextView mText;		ImageView mLogo;		LinearLayout mLayoutLogo;				@Override		public int hashCode() {			return  mText.hashCode() + mPrice.hashCode() + mLogo.hashCode() + mLayoutLogo.hashCode();		}	}}