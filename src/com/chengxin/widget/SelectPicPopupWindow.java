package com.chengxin.widget;



import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chengxin.R;
import com.chengxin.Entity.Login;
import com.chengxin.global.ImageLoader;
import com.chengxin.global.WeiYuanCommon;


public class SelectPicPopupWindow extends PopupWindow {
	//private Button  btn_cancel;
	private View mMenuView;
	private LinearLayout my_profile,my_photo,my_collection,my_setting,my_feedback; // ,mApplyMerchant;
//	private TextView mApplyNameTexViwe;
//	private ImageView mApplyIcon;
	private ImageLoader mImageLoader;

	public SelectPicPopupWindow(final Activity context,final OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.bottomdialog, null);

		mImageLoader = new ImageLoader();
		context.getWindowManager().getDefaultDisplay().getHeight();
		int w = context.getWindowManager().getDefaultDisplay().getWidth();
		/*btn_cancel = (Button) mMenuView.findViewById(R.id.btn_cancel);
		//ȡ��ť
		btn_cancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				//��ٵ�����
				//SaveDate.saveDate(context, new OAuthV2()); 
				
			}
		});*/
		my_profile = (LinearLayout) mMenuView.findViewById(R.id.my_profile);
		ImageView iv=(ImageView)mMenuView.findViewById(R.id.user_icon);
		TextView userName = (TextView)mMenuView.findViewById(R.id.user_name);
		TextView userSign = (TextView)mMenuView.findViewById(R.id.user_sign);
//		mApplyIcon = (ImageView)mMenuView.findViewById(R.id.apply_icon);
//		mApplyNameTexViwe = (TextView)mMenuView.findViewById(R.id.apply_text);
		Login login = WeiYuanCommon.getLoginResult(context);
		if(login!=null ){
			if(login.headsmall!=null && !login.headsmall.equals("")){
				mImageLoader.getBitmap(context, iv,null,login.headsmall,0,false,true,false);
			}
//			if(WeiYuanCommon.getLoginResult(context).isshop == 1){//商家-发布商品
//				mApplyIcon.setImageResource(R.drawable.send_goods_icon);
//				mApplyNameTexViwe.setText("发布商品");
//			}
		}
		
		
		userName.setText(login.nickname);
		userSign.setText(login.sign);
	
		my_profile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				itemsOnClick.onClick(v);
				dismiss();
			}
		});

		my_photo = (LinearLayout) mMenuView.findViewById(R.id.my_photo);
		my_photo.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				itemsOnClick.onClick(v);
				dismiss();
			}
		});
		my_collection = (LinearLayout) mMenuView.findViewById(R.id.my_collection);
		my_collection.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				itemsOnClick.onClick(v);
				dismiss();
			}
		});
		
//		mApplyMerchant = (LinearLayout)mMenuView.findViewById(R.id.apply_merchant);
//		mApplyMerchant.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				itemsOnClick.onClick(v);
//				dismiss();
//			}
//		});
//		
		my_setting = (LinearLayout) mMenuView.findViewById(R.id.my_setting);
		my_setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				itemsOnClick.onClick(v);
				dismiss();
			}
		});
		my_feedback = (LinearLayout)mMenuView.findViewById(R.id.my_feedback);
		my_feedback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				itemsOnClick.onClick(v);
				dismiss();
			}
		});
		//���ð�ť����
		//����SelectPicPopupWindow��View
		this.setContentView(mMenuView);
		//����SelectPicPopupWindow��������Ŀ�
		this.setWidth(w/2/*+20*/);
		//����SelectPicPopupWindow��������ĸ�
		this.setHeight(LayoutParams.WRAP_CONTENT);
		//����SelectPicPopupWindow��������ɵ��
		this.setFocusable(true);
		//����SelectPicPopupWindow�������嶯��Ч��
		this.setAnimationStyle(R.style.leftstyle);
		new ColorDrawable(000000);
		//����SelectPicPopupWindow��������ı���
		//this.setBackgroundDrawable(dw);
		this.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.no_top_arrow_bg));
		//mMenuView���OnTouchListener�����жϻ�ȡ����λ�������ѡ�����������ٵ�����
		mMenuView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				int height = mMenuView.findViewById(R.id.pop_layout).getTop();
				int y=(int) event.getY();
				if(event.getAction()==MotionEvent.ACTION_UP){
					if(y<height){
						dismiss();
					}
				}				
				return true;
			}
		});
	}
}
