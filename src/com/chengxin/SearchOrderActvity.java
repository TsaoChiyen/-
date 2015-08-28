package com.chengxin;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.chengxin.R;
import com.chengxin.Entity.Login;
import com.chengxin.Entity.UserList;
import com.chengxin.adapter.SubScriptionNumAdapter;
import com.chengxin.global.ResearchCommon;
import com.chengxin.net.ResearchException;

/**
 * 通讯录-查找公众账号
 * @author dongli
 *
 */
public class SearchOrderActvity extends BaseActivity implements OnClickListener, OnItemClickListener{
    RelativeLayout mSearchBtn;
    EditText mSearchkeyEditText;

    List<Login> mSubscriptionNumList = new ArrayList<Login> ();
    ListView mListView;
    private SubScriptionNumAdapter mAdapter = null;
    public final  static int MSG_SHOW_LISTVIEW_DATA=0x00001;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.findsubscription_layout);
        InitComponent();
        registerMonitor();
    }
    
    private void registerMonitor() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(SUB_DETAIL_CHANGE);
		registerReceiver(mReceiver, filter);
	}

	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent){
			String action = intent.getAction();
			if (action.equals(SUB_DETAIL_CHANGE)) {
				String subid = intent.getStringExtra("uid");
				String isFollow = intent.getStringExtra("isFollow");
				int isGetMsg = intent.getIntExtra("isgetMsg",-1);
				if(subid!=null && !subid.equals("")){
					for (int i = 0; i < mSubscriptionNumList.size(); i++) {
						if (mSubscriptionNumList.get(i).uid.equals(subid)) {
							if(!TextUtils.isEmpty(isFollow)){
								mSubscriptionNumList.get(i).isfollow = isFollow;
							}
							if(isGetMsg!=-1){
								mSubscriptionNumList.get(i).isGetMsg = isGetMsg;
							}
						}
					}
					if(mAdapter!=null){
						mAdapter.notifyDataSetChanged();
					}
				}
			}
		}
	};
    
    private void InitComponent(){
        setTitleContent(R.drawable.back_btn,0, R.string.search_sub);
        mLeftBtn.setOnClickListener(this);
        mRightBtn.setOnClickListener(this);
        
        mSearchBtn = (RelativeLayout)findViewById(R.id.searchbtn);
        mSearchBtn.setOnClickListener(this);
        
        mSearchkeyEditText = (EditText)findViewById(R.id.name);
        
        mListView = (ListView)findViewById(R.id.listview);
        mListView.setCacheColorHint(0);
        mListView.setDivider(null);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
    	super.onClick(v);
        switch (v.getId()) {
        case R.id.left_btn:
            if(getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null){
                InputMethodManager manager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);  
            }
            SearchOrderActvity.this.finish();
            break;
            
        case R.id.searchbtn:
            if(getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null){
                InputMethodManager manager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);  
            }
            search();
            break;

        default:
            break;
        }
    }
    
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_SHOW_LISTVIEW_DATA:
                updateListView();
                break;
            }
        }
    };
    
    private void updateListView(){
        if (mAdapter == null) {
            mAdapter = new SubScriptionNumAdapter(mContext, mSubscriptionNumList, null);
            mListView.setAdapter(mAdapter);
        }
        
        mAdapter.notifyDataSetChanged();
    }
    
    private void search(){
        final String content = mSearchkeyEditText.getText().toString();
        if (content == null || content.equals("")) {
        	Toast.makeText(mContext,R.string.please_input_find_sub, Toast.LENGTH_LONG).show();
            return;
        }
        showProgressDialog(getString(R.string.searching_sub));
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                	UserList scrNum = ResearchCommon.getResearchInfo().searchSubscriptionNumList(content);
                    if (scrNum != null && scrNum.mUserList != null) {
                        mSubscriptionNumList.clear();
                        mSubscriptionNumList.addAll(scrNum.mUserList);
                    }
                    
                    mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
                    mHandler.sendEmptyMessage(MSG_SHOW_LISTVIEW_DATA);
                    
                } catch (ResearchException e) {
                    e.printStackTrace();
                    ResearchCommon.sendMsg(mBaseHandler, BASE_MSG_TIMEOUT_ERROR,
                    		mContext.getResources().getString(e.getStatusCode()));
                }
            }
        }).start();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Intent intent = new Intent();
        intent.setClass(SearchOrderActvity.this, SubscriptionNumDetailActivity.class);
        intent.putExtra("subscription", mSubscriptionNumList.get(arg2));
        intent.putExtra("fromsearch", 1);
        startActivity(intent);
    }
    
	@Override
	protected void onDestroy() {
		if( mReceiver!=null){
			unregisterReceiver(mReceiver);
		}
		super.onDestroy();
	}
}