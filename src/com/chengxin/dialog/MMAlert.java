package com.chengxin.dialog;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView.HitTestResult;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.chengxin.R;
import com.chengxin.Entity.GridViewMenuItem;


public final class MMAlert
{

	public interface OnAlertSelectId
	{
		void onClick(int whichButton);
	}

	public interface OnAlertOkSelectId
	{
		void onOkClick(int whichButton,String content );
	}


	private MMAlert()
	{
	}

	public static AlertDialog showAlert(final Context context, final int msgId, final int titleId, 
			final DialogInterface.OnClickListener lOk, final DialogInterface.OnClickListener lCancel) 
	{
		if (context instanceof Activity && ((Activity) context).isFinishing())
		{
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(R.drawable.ic_dialog_alert);
		builder.setTitle(titleId);
		builder.setMessage(msgId);
		builder.setPositiveButton(R.string.ok, lOk);
		builder.setNegativeButton(R.string.cancel, lCancel);
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	/**
	 * 拍照
	 * 从相册中选择照片
	 * @param context
	 * @param title
	 * @param items
	 * @param exit
	 * @param alertDo
	 * @return
	 */
	public static Dialog showAlert(final Context context, final String title, final String[] items, 
			String exit, final OnAlertSelectId alertDo) 
	{
		return showAlert(context, title, items, exit, alertDo, null);
	}

	/**
	 * 通用底部弹出框
	 */
	public static Dialog showShareAlert(final Context context, final String buttonTitle,final List<GridViewMenuItem> menuList, 
			int width, final OnAlertSelectId alertDo,final OnCancelListener cancelListter) 
	{
		return showNiceAler(context, buttonTitle, menuList, width, alertDo, cancelListter);
	}


	/**
	 *底部弹出的九宫格
	 */
	public static Dialog showNiceAler(final Context context,final String buttonTitle,
			final List<GridViewMenuItem> list,final int width,final OnAlertSelectId alertDo,
			OnCancelListener cancelListener){
		final Dialog dlg = new Dialog(context, R.style.MMThem_DataSheet);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.bottom_gridview_menu, null);
		LinearLayout parentLayout = (LinearLayout)layout.findViewById(R.id.control);

		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);

		int count = list.size() % 3 == 0?list.size()/3:list.size()/3+1;
		int index = 0;
		for (int i = 0; i < count; i++) {
			LinearLayout childLayout = new LinearLayout(context);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT);
			childLayout.setLayoutParams(params);
			childLayout.setOrientation(LinearLayout.HORIZONTAL);

			for (int j = index; j <list.size(); j++) {
				LinearLayout view = (LinearLayout)inflater.inflate(R.layout.gridview_menu_item, null);
				LinearLayout.LayoutParams itemParams = new LayoutParams(width,LayoutParams.WRAP_CONTENT);
				view.setLayoutParams(itemParams);


				ImageView menuIcon = (ImageView)view.findViewById(R.id.menu_icon);
				menuIcon.setImageResource(list.get(j).resource_id);
				TextView menuText = (TextView)view.findViewById(R.id.menu_text);
				menuText.setText(list.get(j).menu_name);
				childLayout.addView(view);

				final int id = j;
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.onClick(id);
						dlg.dismiss();
						layout.requestFocus();
					}
				});

				if((j+1)%3 == 0){
					index = j;
					break;
				}
			}
			parentLayout.addView(childLayout);
		}

		final TextView cancleBtn = (TextView)layout.findViewById(R.id.popup_text);
		cancleBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//alertDo.onClick(v.getId());
				dlg.dismiss();
				cancleBtn.requestFocus();
			}
		});

		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -100/*0*/;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		if (cancelListener != null)
			dlg.setOnCancelListener(cancelListener);

		dlg.setContentView(layout);
		dlg.show();

		return dlg;
	}



	/**
	 * @param context
	 *            Context.
	 * @param title
	 *            The title of this AlertDialog can be null .
	 * @param items
	 *            button name list.
	 * @param alertDo
	 *            methods call Id:Button + cancel_Button.
	 * @param exit
	 *            Name can be null.It will be Red Color
	 * @return A AlertDialog
	 */
	public static Dialog showAlert(final Context context, final String title, final String[] items, String exit, 
			final OnAlertSelectId alertDo, OnCancelListener cancelListener)
	{
		String cancel = context.getString(R.string.cancel);
		final Dialog dlg = new Dialog(context, R.style.MMThem_DataSheet);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.alert_dialog_menu_layout, null);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		final ListView list = (ListView) layout.findViewById(R.id.content_list);
		AlertAdapter adapter = new AlertAdapter(context, title, items, exit, cancel);
		list.setAdapter(adapter);
		list.setDividerHeight(0);

		list.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				if (!(title == null || title.equals("")) && position - 1 >= 0)
				{
					alertDo.onClick(position - 1);
					dlg.dismiss();
					list.requestFocus();
				}
				else
				{
					alertDo.onClick(position);
					dlg.dismiss();
					list.requestFocus();
				}

			}
		});
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		if (cancelListener != null)
			dlg.setOnCancelListener(cancelListener);

		dlg.setContentView(layout);
		dlg.show();

		return dlg;
	}

}

class GridViewAdapter extends BaseAdapter{
	public List<GridViewMenuItem> mData;
	private Context mContext;

	public GridViewAdapter(Context context,List<GridViewMenuItem> list) {
		this.mData = list;
		this.mContext = context;

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
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null || ((ViewHolder)convertView.getTag()).mTag != position){
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.gridview_menu_item, null);
			holder.mTag = position;
			holder.mMenuText = (TextView)convertView.findViewById(R.id.menu_text);
			holder.mMenuIcon = (ImageView)convertView.findViewById(R.id.menu_icon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		GridViewMenuItem item = mData.get(position);
		holder.mMenuIcon.setImageResource(item.resource_id);
		holder.mMenuText.setText(item.menu_name);
		return convertView;
	}

	static class ViewHolder
	{
		int mTag;
		TextView mMenuText;
		ImageView mMenuIcon;
	}


}



class AlertAdapter extends BaseAdapter
{
	public static final int TYPE_BUTTON = 0;
	public static final int TYPE_TITLE = 1;
	public static final int TYPE_EXIT = 2;
	public static final int TYPE_CANCEL = 3;
	private List<String> items = new ArrayList<String>();
	private int[] types;
	private boolean isTitle = false;
	private boolean isCancle = false;
	private Context context;
	private int changeLenth;


	public AlertAdapter(Context context, String title, String[] items, String exit, String cancel)
	{
		changeLenth = items.length;

		for (int i = 0; i < items.length; i++) {
			this.items.add(items[i]);
		}

		this.types = new int[this.items.size() + 3];
		this.context = context;
		if (title != null && !title.equals("")) {
			types[0] = TYPE_TITLE;
			this.isTitle = true;
			this.items.add(0, title);
		}

		if (exit != null && !exit.equals("")) {
			// this.isExit = true;
			types[this.items.size()] = TYPE_EXIT;
			this.items.add(exit);
		}

		if (cancel != null && !cancel.equals("")) {
			// this.isSpecial = true;
			types[this.items.size()] = TYPE_CANCEL;
			isCancle = true;
			this.items.add(cancel);
		}
	}

	@Override
	public int getCount()
	{
		return items.size();
	}

	@Override
	public Object getItem(int position) 
	{
		return items.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public boolean isEnabled(int position) 
	{
		if (position == 0 && isTitle) {
			return false;
		} else {
			return super.isEnabled(position);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final String textString = (String) getItem(position);
		ViewHolder holder;
		int type = types[position];
		if (convertView == null || ((ViewHolder) convertView.getTag()).type != type) {
			holder = new ViewHolder();
			if (type == TYPE_CANCEL) {//
				convertView = View.inflate(context, R.layout.alert_dialog_menu_list_layout_cancel, null);
				holder.text = (TextView) convertView.findViewById(R.id.popup_text);
			} else if (type == TYPE_BUTTON) {//
				convertView = View.inflate(context, R.layout.alert_dialog_menu_list_layout, null);
				holder.text = (TextView) convertView.findViewById(R.id.popup_text);
				if(changeLenth == 1 ){
					holder.text.setBackgroundResource(R.drawable.white_btn_n);
				}else if(changeLenth >= 2){
					if(position == 0){
						holder.text.setBackgroundResource(R.drawable.pop_top);
					}else if(position == changeLenth - 1){
						holder.text.setBackgroundResource(R.drawable.pop_bottom);
					}else{
						holder.text.setBackgroundResource(R.drawable.pop_center);
					}
				}
			} else if (type == TYPE_TITLE || type == TYPE_EXIT) {//
				convertView = View.inflate(context, R.layout.alert_dialog_menu_list_layout_title, null);
				holder.text = (TextView) convertView.findViewById(R.id.popup_text);
			} /*else if (type == TYPE_EXIT) {
				convertView = View.inflate(context, R.layout.alert_dialog_menu_list_layout_special, null);
			}*/
			holder.type = type;
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.text.setText(textString);
		return convertView;
	}

	static class ViewHolder
	{
		TextView text;
		int type;
	}
}

