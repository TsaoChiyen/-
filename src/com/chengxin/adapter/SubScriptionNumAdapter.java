package com.chengxin.adapter;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chengxin.R;
import com.chengxin.Entity.Login;
import com.chengxin.global.ImageLoader;

public class SubScriptionNumAdapter  extends BaseAdapter{

    public List<Login> mData;
    public Context mContext;
    LayoutInflater inflater;
    private Handler mHandler;
    private ImageLoader mImageLoader = new ImageLoader();
    
    public SubScriptionNumAdapter(Context context, List<Login> list,Handler handler) {
        inflater = LayoutInflater.from(context);
        mContext = context;
        this.mData = list;
        this.mHandler = handler;
    }
    
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mData.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHoler;
        if (convertView == null || ((ViewHolder) convertView.getTag()).mTag != position) {
            viewHoler = new ViewHolder();
            convertView = inflater.inflate(R.layout.publicnum_item, null);
            
            viewHoler.mHeaderView = (ImageView) convertView.findViewById(R.id.header_icon);
            viewHoler.mNameTextView = (TextView)convertView.findViewById(R.id.name);
            viewHoler.mInfoTextView = (TextView)convertView.findViewById(R.id.info);
            viewHoler.mTag = position;
            convertView.setTag(viewHoler);
        }else{
            viewHoler = (ViewHolder)convertView.getTag();
        }
        
        Login item = mData.get(position);
        
        if (item.headsmall != null && !item.headsmall.equals("")) {
            mImageLoader.getBitmap(mContext, viewHoler.mHeaderView,
                    null, item.headsmall, 0, false, false);
        }
        
        viewHoler.mNameTextView.setText(item.name);
        viewHoler.mInfoTextView.setText(item.info);

        return convertView;
    }
    
    
     class ViewHolder{
        public int mTag;
        public ImageView mHeaderView;
        public TextView mNameTextView;
        public TextView mInfoTextView;
        @Override
        public int hashCode() {
            return this.mHeaderView.hashCode() + this.mNameTextView.hashCode();
        }
    }
}
