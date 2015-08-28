package com.chengxin;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chengxin.R;
import com.chengxin.DB.DBHelper;
import com.chengxin.DB.GroupTable;
import com.chengxin.DB.UserTable;
import com.chengxin.Entity.Group;
import com.chengxin.Entity.GroupList;
import com.chengxin.Entity.Login;
import com.chengxin.global.FeatureFunction;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.GlobleType;
import com.chengxin.global.ResearchCommon;
import com.chengxin.map.BMapApiApp;
import com.chengxin.net.ResearchException;
import com.chengxin.sortlist.PinYin;
import com.chengxin.sortlist.PinyinComparator;
import com.chengxin.sortlist.SideBar;
import com.chengxin.sortlist.SortAdapter;
import com.chengxin.widget.MyPullToRefreshListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Chony on 2015/4/7.
 * 公众号列表页面
 */
public class PublicNumberActivity extends BaseActivity implements MyPullToRefreshListView.OnChangeStateListener {

    private GroupList mGroup;//mPublicNumberList;
    private List<Group> mPublicNumberList = new ArrayList<Group>();

    public  List<Login> mSourceDataList=new ArrayList<Login>();

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private SortAdapter mAdapter;

    private boolean mIsRefreshing = false;
    private MyPullToRefreshListView mContainer;

    private TextView mRefreshViewLastUpdated;
    private LinearLayout mCategoryLinear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.public_muber_activity_layout);
        mContext = this;
        pinyinComparator = new PinyinComparator();

        initComponent();

        /*SQLiteDatabase db = DBHelper.getInstance(mContext).getReadableDatabase();
        UserTable table = new UserTable(db);

        List<Login> orderList =new ArrayList<Login>();
        orderList = table.queryList(GlobleType.ORDER_TYPE);
        mSourceDataList.addAll(orderList);

        List<Login> serverList =new ArrayList<Login>();
        serverList = table.queryList(GlobleType.SERVICE_TYPE);
        mSourceDataList.addAll(serverList);

        updateListView();*/

        getPublicNumberList(GlobalParam.LIST_LOAD_FIRST);
    }

    private void initComponent(){
        setTitleContent(R.drawable.back_btn, R.drawable.add_icon_btn, R.string.public_number);

        mLeftBtn.setOnClickListener(this);
        mRightBtn.setOnClickListener(this);

        sideBar = (SideBar)findViewById(R.id.sidrbar);
        dialog = (TextView)findViewById(R.id.dialog);
        sideBar.setTextView(dialog);

        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = mAdapter.getPositionForSection(s.charAt(0));
                if(position != -1){
                    sortListView.setSelection(position);
                }
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        sideBar.setBackgroundColor(Color.parseColor("#00000000"));
                        sideBar.setChoose(-1);
                        sideBar.invalidate();
                        dialog.setVisibility(View.INVISIBLE);
                    }
                }, 2000);
            }
        });

        mCategoryLinear = (LinearLayout)findViewById(R.id.category_linear);
        mRefreshViewLastUpdated = (TextView) findViewById(R.id.pull_to_refresh_time);
        mContainer = (MyPullToRefreshListView) findViewById(R.id.container);
        sortListView = mContainer.getList();
        sortListView.setDivider(null);
        sortListView.setCacheColorHint(0);
        sortListView.setHeaderDividersEnabled(false);

        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            /**
             * listview 子项点击事件
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Login login = (Login) mAdapter.getItem(position);
                if(login.remark!=null && !login.remark.equals("")){
                    if(login.remark.equals(mContext.getResources().getString(R.string.new_friends))){
                        //进入新的朋友页面
                        Intent newFriendIntent = new Intent();
                        newFriendIntent.setClass(mContext, NewFriendsActivity.class);
                        startActivity(newFriendIntent);
                    }else if(login.remark.equals(mContext.getResources().getString(R.string.room_chat))){
                        //进入我的群组页面
                        Intent groupListIntent = new Intent();
                        groupListIntent.setClass(mContext, MyGroupListActivity.class);
                        startActivity(groupListIntent);
                    }else if(login.remark.equals(mContext.getResources().getString(R.string.public_number))){
                        //进入公众号页面
                        Intent groupListIntent = new Intent();
                        groupListIntent.setClass(mContext, PublicNumberActivity.class);
                        startActivity(groupListIntent);
                    }
                    else{//进入好友资料页面
                        Intent userInfoIntent = new Intent();
                        userInfoIntent.setClass(mContext, UserInfoActivity.class);
                        userInfoIntent.putExtra("type",2);
                        userInfoIntent.putExtra("uid", login.uid);
                        startActivity(userInfoIntent);
                    }
                }else{//跳转到用户信息页面
                    Intent userInfoIntent = new Intent();
                    userInfoIntent.setClass(mContext, SubscriptionNumDetailActivity.class);
                    userInfoIntent.putExtra("subscription", login);
                    startActivity(userInfoIntent);
                }

            }
        });
        sortListView.setSelector(mContext.getResources().getDrawable(R.drawable.transparent_selector));
        mContainer.setOnChangeStateListener(this);
        if(mSourceDataList!=null && mSourceDataList.size()>0){
            mSourceDataList.clear();
        }
    }

    /**
     * 获取通讯录人员列表
     * @param loadType
     */
    private void getPublicNumberList(final int loadType) {
        new Thread() {
            @Override
            public void run() {
                if (ResearchCommon.verifyNetwork(mContext)) {
                    new Thread() {
                        public void run() {
                            try {
                                mGroup = ResearchCommon.getResearchInfo().getPublicNumberList();

                                if (mGroup != null) {
                                    if (mGroup.mState != null && mGroup.mState.code == 0) {

                                        if (loadType != GlobalParam.LIST_LOAD_MORE) {
                                            if (mPublicNumberList != null) {
                                                mPublicNumberList.clear();
                                            }
                                        }

                                        List<Login> tempList = new ArrayList<Login>();
                                        if (mGroup.mGroupList != null) {
                                            mPublicNumberList.addAll(mGroup.mGroupList);
                                            SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
                                            GroupTable table = new GroupTable(db);
                                            table.insert(mGroup.mGroupList);

                                            for (int i = 0; i < mGroup.mGroupList.size(); i++) {
                                                if(mPublicNumberList.get(i).mStarList!=null
                                                        && mPublicNumberList.get(i).mStarList.size()>0){
                                                    tempList.addAll(mPublicNumberList.get(i).mStarList);
                                                }
                                            }
                                            for (int j = 0; j < mGroup.mGroupList.size(); j++) {
                                                if(mPublicNumberList.get(j).mUserList != null){
                                                    tempList.addAll(mPublicNumberList.get(j).mUserList);
                                                }
                                            }
                                        }

                                        ResearchCommon.sendMsg(mHandler, GlobalParam.MSG_CLEAR_LISTENER_DATA,tempList);
                                    } else {
                                        Message msg = new Message();
                                        msg.what = GlobalParam.MSG_LOAD_ERROR;
                                        if (mGroup.mState != null && mGroup.mState.errorMsg != null && !mGroup.mState.errorMsg.equals("")) {
                                            msg.obj = mGroup.mState.errorMsg;
                                        } else {
                                            msg.obj = BMapApiApp.getInstance().getResources().getString(R.string.load_error);
                                        }
                                        mHandler.sendMessage(msg);
                                    }
                                } else {
                                    mHandler.sendEmptyMessage(GlobalParam.MSG_LOAD_ERROR);
                                }

                            } catch (ResearchException e) {
                                e.printStackTrace();
                                Message msg = new Message();
                                msg.what = GlobalParam.MSG_TICE_OUT_EXCEPTION;
                                msg.obj = BMapApiApp.getInstance().getResources().getString(R.string.timeout);
                                mHandler.sendMessage(msg);
                            }

                            switch (loadType) {
                                case GlobalParam.LIST_LOAD_FIRST:
                                    mHandler.sendEmptyMessage(GlobalParam.HIDE_PROGRESS_DIALOG);
                                    break;
                                case GlobalParam.LIST_LOAD_MORE:
                                    mHandler.sendEmptyMessage(GlobalParam.HIDE_LOADINGMORE_INDECATOR);

                                case GlobalParam.LIST_LOAD_REFERSH:
                                    mHandler.sendEmptyMessage(GlobalParam.HIDE_SCROLLREFRESH);
                                    break;

                                default:
                                    break;
                            }
                        }
                    }.start();
                } else {
                    switch (loadType) {
                        case GlobalParam.LIST_LOAD_FIRST:
                            mHandler.sendEmptyMessage(GlobalParam.HIDE_PROGRESS_DIALOG);
                            break;
                        case GlobalParam.LIST_LOAD_MORE:
                            mHandler.sendEmptyMessage(GlobalParam.HIDE_LOADINGMORE_INDECATOR);

                        case GlobalParam.LIST_LOAD_REFERSH:
                            mHandler.sendEmptyMessage(GlobalParam.HIDE_SCROLLREFRESH);
                            break;

                        default:
                            break;
                    }
                    mHandler.sendEmptyMessage(GlobalParam.MSG_NETWORK_ERROR);
                }
            }

        }.start();
    }

    /**
     * 处理消息
     */
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case GlobalParam.SHOW_PROGRESS_DIALOG:
                    String dialogMsg = (String) msg.obj;
                    showProgressDialog(dialogMsg);
                    break;
                case GlobalParam.HIDE_PROGRESS_DIALOG:
                    hideProgressDialog();
                    updateListView();
                    break;

                case GlobalParam.SHOW_SCROLLREFRESH:
                    if (mIsRefreshing) {
                        mContainer.onRefreshComplete();
                        break;
                    }
                    mIsRefreshing = true;
                    getPublicNumberList(GlobalParam.LIST_LOAD_REFERSH);
                    break;

                case GlobalParam.HIDE_SCROLLREFRESH:
                    mIsRefreshing = false;
                    mContainer.onRefreshComplete();
                    //updateListView();
                    refreshUpdateListView();
                    break;
                case GlobalParam.MSG_CLEAR_LISTENER_DATA:
                    if (mIsRefreshing) {
                        mIsRefreshing = false;
                        mContainer.onRefreshComplete();
                    }
                    if(mSourceDataList != null && mSourceDataList.size()>0){
                        mSourceDataList.clear();
                        if(mAdapter!=null){
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    List<Login> tempList = (List<Login>)msg.obj;
                    if(tempList!=null && tempList.size()>0){
                        mSourceDataList.addAll(tempList);
                    }
                    break;

                case GlobalParam.MSG_LOAD_ERROR:
                    String error_Detail = (String) msg.obj;
                    if (error_Detail != null && !error_Detail.equals("")) {
                        Toast.makeText(mContext, error_Detail, Toast.LENGTH_LONG)
                                .show();
                    } else {
                        Toast.makeText(mContext, R.string.load_error,
                                Toast.LENGTH_LONG).show();
                    }
                    break;
                case GlobalParam.MSG_NETWORK_ERROR:
                    Toast.makeText(mContext, R.string.network_error,
                            Toast.LENGTH_LONG).show();
                    break;
                case GlobalParam.MSG_TICE_OUT_EXCEPTION:

                    String message = (String) msg.obj;
                    if (message == null || message.equals("")) {
                        message = BMapApiApp.getInstance().getResources().getString(R.string.timeout);
                    }
                    Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                    break;

                default:
                    break;
            }
        }
    };

    private void updateListView(){
        filledData();
        //根据a-z排序
        Collections.sort(mSourceDataList, pinyinComparator);
		/*	if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}else{*/
        mAdapter = new SortAdapter(mContext, mSourceDataList);
        sortListView.setAdapter(mAdapter);
		/*}*/
    }

    /**
     * 为ListView填充数据
     * @return
     */
    private void filledData(){

        try {
            for(int i=0; i<mSourceDataList.size(); i++){
                String name="";
                if(mSourceDataList.get(i).nameType == 1  ){//0-普通用户 1-操作栏 2-星标朋友
                    name = mSourceDataList.get(i).nickname;
                }else{
                    name = mSourceDataList.get(i).remark;
                }
                if(name == null || name.equals("")){
                    name = mSourceDataList.get(i).nickname;
                }


                //汉字转换成拼音
				/*String pinyin;
				pinyin = characterParser.getSelling(name);
				String sortString = pinyin.substring(0, 1).toUpperCase();*/
                String sortString  = mSourceDataList.get(i).sort;
                String sName = mSourceDataList.get(i).sortName;
                if(sName!=null && !sName.equals("")){
                    if(sName.equals("星标朋友")){

                    }else{

                    }
                }else{
                    if(sortString.matches("↑")){
                        mSourceDataList.get(i).sort = "↑";
                        mSourceDataList.get(i).sortName = "";
                        mSourceDataList.get(i).remark = name.substring(1,name.length());
                    }
                    else if(sortString.matches("[A-Z]") || sortString.matches("[a-z]")){
                        String sort = PinYin.getPingYin(name.trim());
                        if(sort==null || sort.length()<=0){
                            sort = "#";
                        }else{
                            sort = sort.substring(0, 1).toUpperCase();
                        }
                        mSourceDataList.get(i).sort = sort;
                        mSourceDataList.get(i).sortName = sort;
                    }else{
                        mSourceDataList.get(i).sortName = "#";
                        mSourceDataList.get(i).sort = "#";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示通讯录数据
     */
    private void refreshUpdateListView(){
        filledData();
        //根据a-z排序
        Collections.sort(mSourceDataList, pinyinComparator);
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }else{
            mAdapter = new SortAdapter(mContext, mSourceDataList);
            sortListView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onChangeState(MyPullToRefreshListView container, int state) {
        mRefreshViewLastUpdated.setText(FeatureFunction.getRefreshTime());

        switch (state) {
            case MyPullToRefreshListView.STATE_LOADING:
                mIsRefreshing = true;
                getPublicNumberList(GlobalParam.LIST_LOAD_FIRST);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.left_btn:
                finish();
                break;
            case R.id.right_btn:
                Intent intent = new Intent();
                intent.setClass(mContext, SearchOrderActvity.class);
                startActivity(intent);
                break;
        }
    }
}
