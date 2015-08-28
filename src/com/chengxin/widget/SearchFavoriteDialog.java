package com.chengxin.widget;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.chengxin.FavoriteDetailActivity;
import com.chengxin.LocationActivity;
import com.chengxin.R;
import com.chengxin.Entity.FavoriteItem;
import com.chengxin.Entity.MessageType;
import com.chengxin.Entity.MovingContent;
import com.chengxin.Entity.MovingLoaction;
import com.chengxin.adapter.MyFavoriteListAdapter;

public class SearchFavoriteDialog extends Dialog implements OnItemClickListener, android.view.View.OnClickListener{

	private Context mContext;
	private EditText mContentEdit;
	private ListView mListView;
	private List<FavoriteItem> mFavoriteList;
	private List<FavoriteItem> mSearchList;
	private Button mCancleBtn;
	private MyFavoriteListAdapter mAdapter;
	public SearchFavoriteDialog(Context context, List<FavoriteItem> userList) {
		super(context, R.style.ContentOverlay);
		mContext = context;
		mFavoriteList = userList;
	}

	public SearchFavoriteDialog(Context context) {
		super(context, R.style.ContentOverlay);
		mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_search_dialog);
		initComponent();
	}

	private void initComponent() {
		mCancleBtn = (Button)findViewById(R.id.cancle_btn);
		mCancleBtn.setOnClickListener(this);
		mSearchList = new ArrayList<FavoriteItem>();
		mContentEdit = (EditText) findViewById(R.id.searchcontent);
		mContentEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if(mFavoriteList == null || mFavoriteList.size()<=0){
					return;
				}
				List<FavoriteItem> tempList = new ArrayList<FavoriteItem>();
				if (s.toString() != null && !s.toString().equals("")) {

					for (int i = 0; i < mFavoriteList.size(); i++) {
						if(mFavoriteList.get(i).typefile == MessageType.TEXT){
							MovingContent movingContent = MovingContent.getInfo(mFavoriteList.get(i).content);
							String content = movingContent.content;
							if(content!=null && !content.equals("")){
								if (content.contains(s.toString())) {
									tempList.add(mFavoriteList.get(i));
								}
							}
						}
						
						
					}

				}

				if (mSearchList != null) {
					mSearchList.clear();
				}

				mSearchList.addAll(tempList);
				updateListView();
				if(mSearchList!=null && mSearchList.size()>0){
					mListView.setVisibility(View.VISIBLE);
				}else{
					mListView.setVisibility(View.GONE);
				}
			}
		});

		mListView = (ListView) findViewById(R.id.contact_list);
		mListView.setVisibility(View.GONE);
		mListView.setDivider(mContext.getResources().getDrawable(R.drawable.order_devider_line));
		mListView.setCacheColorHint(0);
		mListView.setOnItemClickListener(this);
		mListView.setSelector(mContext.getResources().getDrawable(R.drawable.transparent_selector));
	}

	private void updateListView() {

		if(mSearchList != null && mSearchList.size() != 0){
			mListView.setVisibility(View.VISIBLE);
		}

		if(mAdapter != null){
			mAdapter.notifyDataSetChanged();
			return;
		}

		if (mSearchList != null) {
			mAdapter = new MyFavoriteListAdapter(mContext, mSearchList);
			mListView.setAdapter(mAdapter);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(0<=arg2 && arg2<mSearchList.size()){
			FavoriteItem item = mSearchList.get(arg2);
			if(item.typefile == MessageType.MAP){
				Intent intent  = new Intent(mContext, LocationActivity.class);
				MovingLoaction movingLoaction = MovingLoaction.getInfo(item.content);
				intent.putExtra("show", true);
				intent.putExtra("lat", movingLoaction.lat);
				intent.putExtra("lng", movingLoaction.lng);
				intent.putExtra("addr", movingLoaction.address);

				intent.putExtra("fuid", item.id);
				mContext.startActivity(intent);
			}else{
				Intent detailIntent = new Intent();
				detailIntent.setClass(mContext, FavoriteDetailActivity.class);
				detailIntent.putExtra("entity",item);
				mContext.startActivity(detailIntent);
			}
		}


		SearchFavoriteDialog.this.dismiss();
	}
	@Override
	public void onClick(View v) {
		SearchFavoriteDialog.this.dismiss();
	}
	
	private void hideKeyBoard(){
		if(getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null){
			InputMethodManager manager= (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
			manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);  
		}  
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){  
			if(mSearchList == null || mSearchList.size()<=0){
				hideKeyBoard();
				SearchFavoriteDialog.this.dismiss();
			}
		}  
		return super.onTouchEvent(event);
	}
}
