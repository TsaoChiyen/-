package com.chengxin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.URLEncoder;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.chengxin.R;
import com.chengxin.DB.DBHelper;
import com.chengxin.DB.MessageTable;
import com.chengxin.DB.RoomTable;
import com.chengxin.DB.SessionTable;
import com.chengxin.DB.UserTable;
import com.chengxin.Entity.Card;
import com.chengxin.Entity.Login;
import com.chengxin.Entity.MapInfo;
import com.chengxin.Entity.MessageInfo;
import com.chengxin.Entity.MessageResult;
import com.chengxin.Entity.MessageType;
import com.chengxin.Entity.MovingContent;
import com.chengxin.Entity.MovingLoaction;
import com.chengxin.Entity.MovingPic;
import com.chengxin.Entity.MovingVoice;
import com.chengxin.Entity.OrderMenuItem;
import com.chengxin.Entity.PopItem;
import com.chengxin.Entity.Room;
import com.chengxin.Entity.Session;
import com.chengxin.Entity.WeiYuanState;
import com.chengxin.action.AudioPlayListener;
import com.chengxin.action.AudioRecorderAction;
import com.chengxin.adapter.EmojiAdapter;
import com.chengxin.adapter.EmojiUtil;
import com.chengxin.adapter.WeiYuanViewPagerAdapter;
import com.chengxin.control.ReaderImpl;
import com.chengxin.dialog.MMAlert;
import com.chengxin.dialog.MMAlert.OnAlertSelectId;
import com.chengxin.fragment.ChatFragment;
import com.chengxin.global.AjaxCallBack;
import com.chengxin.global.FeatureFunction;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.GlobleType;
import com.chengxin.global.ImageLoader;
import com.chengxin.global.ResearchCommon;
import com.chengxin.global.VoiceTask;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.net.ResearchException;
import com.chengxin.net.Utility;
import com.chengxin.net.WeiYuanException;
import com.chengxin.org.json.JSONArray;
import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;
import com.chengxin.receiver.NotifyChatMessage;
import com.chengxin.receiver.PushChatMessage;
import com.chengxin.service.SnsService;
import com.chengxin.service.type.XmppType;
import com.chengxin.widget.MainSearchDialog;
import com.chengxin.widget.PopWindows;
import com.chengxin.widget.ResizeLayout;
import com.chengxin.widget.MainSearchDialog.OnFinishClick;
import com.chengxin.widget.ResizeLayout.OnResizeListener;

/**
 * 
 * 功能： 个人会话 <br />
 * 
 * 
 * 1 成功; 0 失败; 2 正在发送;
 * 
 * @author dl
 * @since
 */
public class ChatMainActivity extends BaseActivity implements OnItemLongClickListener, OnClickListener, OnTouchListener, OnPageChangeListener {
	/**
	 * 定义全局变量
	 */
	private final int REQUEST_CODE_TAKE_VIDEO = 0x00126;// 0x121356;
	public static final String EMOJIREX = "emoji_[\\d]{0,3}";
	private static final String TAG = "chat_main";
	private final static int MSG_RESIZE = 1234;
	private final static int CAMERA_ACTIVITY = 987;

	private List<List<String>> mTotalEmotionList = new ArrayList<List<String>>();
	private ViewPager mViewPager;
	private WeiYuanViewPagerAdapter mEmotionAdapter;
	private LinkedList<View> mViewList = new LinkedList<View>();
	private LinearLayout mLayoutCircle;
	public int mPageIndxe = 0;
	private RelativeLayout mEmotionLayout;

	private RelativeLayout mChatBottmLayout;

	private ListView mListView;
	private Button mMsgSendBtn, mCameraBtn, mGalleryBtn, mEmotionBtn, mLocationBtn, mCardBtn, mFavoritebtn, mSmallVideoBtn;
	private Button mVoiceSendBtn;
	private ToggleButton mToggleBtn, mAddBtn, mKeyBoardBtn;
	private EditText mContentEdit;
	private View mChatExpraLayout;
	public static MyAdapter mAdapter;
	private List<MessageInfo> messageInfos;
	private ReaderImpl mReaderImpl;
	private AudioRecorderAction audioRecorder;
	private AudioPlayListener playListener;
	private AlertDialog messageDialog; // 消息功能ui
	private Handler handler = new Handler();
	private boolean opconnectState = false;

	private ProgressDialog waitDialog;

	private List<String> downVoiceList = new ArrayList<String>();
	private ResizeLayout mRootLayout;
	private ResizeLayout mListLayout;
	private static final int BIGGER = 1;
	private static final int SMALLER = 2;
	private ImageLoader mImageLoader = new ImageLoader();
	public static final int SEND_VOICE_TO_LIST = 4445;
	public static final int ADD_VOICE_TO_LIST = 4446;
	private static final int REQUEST_GET_IMAGE_BY_CAMERA = 1002;
	private static final int REQUEST_GET_VIDEO_BY_CAMERA = 1003;
	private static final int REQUEST_GET_URI = 101;
	public static final int REQUEST_GET_BITMAP = 124;
	private static final int REQUEST_GET_VIDEO_IMAGE = 125;

	private boolean mIsFirst = true;
	public static final String DESTORY_ACTION = "com.weiyuan.intent.action.DESTORY_ACTION";
	public static final String REFRESH_ADAPTER = "com.weiyuan.intent.action.REFRESH_ADAPTER";
	public static final String ACTION_READ_VOICE_STATE = "com.weiyuan.sns.push.ACTION_READ_VOICE_STATE";
	public static final String ACTION_CHANGE_FRIEND = "com.weiyuan.intent.action.ACTION_CHANGE_FRIEND";
	public static final String ACTION_RECORD_AUTH = "com.weiyuan.intent.action.ACTION_RECORD_AUTH";
	public static final String ACTION_RECOMMEND_CARD = "com.weiyuan.intent.action.ACTION_RECOMMEND_CARD";
	public static final String ACTION_DESTROY_ROOM = "com.weiyuan.intent.action.ACTION_RECOMMEND_CARD";
	public static final String ACTION_SHOW_NICKNAME = "com.weiyuan.intent.action.show.nickname";
	private final static int MAX_SECOND = 10;
	private final static int MIN_SECOND = 2;
	private HashMap<String, SoftReference<Bitmap>> mBitmapCache;
	private Login mLogin;
	private String mFilePath = "";
	private int mScalcWith, mScalcHeigth;

	private final static int SEND_SUCCESS = 13454;
	private final static int SEND_FAILED = 13455;
	private final static int CHANGE_STATE = 13456;
	private final static int HIDE_PROGRESS_DIALOG = 15453;
	private final static int SHOW_KICK_OUT_DIALOG = 15454;
	private boolean mIsRegisterReceiver = false;
	private Login fCustomerVo;
	private boolean mHasLocalData = true;
	private int mType = 100;
	private static final int RESQUEST_CODE = 100;

	private Dialog mPhoneDialog;
	private int mSendCard;
	private MessageInfo mCardMsg, mForMsg;
	private int mIsShowSearchDialog, mFromPage; // fromage: 1=来自会话详情的查找聊天记录
	private String mSearchContent;

    private LinearLayout mKeyBoardMenuLayout;
    private ImageView mNormalSplite;
    private RelativeLayout mChatLayout;
    private RelativeLayout mChatKeyBoradLayout;

	/**
	 * 导入控件
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("ChatMainActivity", "onCreated");
		mContext = this;
		setContentView(R.layout.chat_main);
		sendBroadcast(new Intent(DESTORY_ACTION));
		mSendCard = getIntent().getIntExtra("cardType", 0);
		mCardMsg = (MessageInfo) getIntent().getSerializableExtra("cardMsg");
		mForMsg = (MessageInfo) getIntent().getSerializableExtra("forMsg");
		mIsShowSearchDialog = getIntent().getIntExtra("is_show_dialog", 0);
		mSearchContent = getIntent().getStringExtra("search_content");
		mFromPage = getIntent().getIntExtra("from_page", 0);
		initComponent();
        mPopWindows = new PopWindows(mContext, mPopList);
	}

	/**
	 * 初始化控件
	 */
	private void initComponent() {
		setTitleContent(R.drawable.back_btn, 0, "");
		mLeftBtn.setOnClickListener(this);
		mRightBtn.setOnClickListener(this);
        mChatLayout = (RelativeLayout)findViewById(R.id.RelativeLayout1);
		mBitmapCache = new HashMap<String, SoftReference<Bitmap>>();
		mLogin = WeiYuanCommon.getLoginResult(mContext);
		opconnectState = isOpconnect();
		// broadcast = new MyBroadcast();
		registerReceiver();

		mChatBottmLayout = (RelativeLayout) findViewById(R.id.RelativeLayout1);

		mListView = (ListView) findViewById(R.id.chat_main_list_msg);
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:
					if (view.getFirstVisiblePosition() == 0) {
						if (mHasLocalData) {
							SQLiteDatabase db = DBHelper.getInstance(mContext).getReadableDatabase();

							MessageTable messageTable = new MessageTable(db);
							List<MessageInfo> tempList = messageTable.query(fCustomerVo.uid, messageInfos.get(0).auto_id, mType);
							if (tempList == null || tempList.size() < 20) {
								mHasLocalData = false;
							}

							if (tempList != null && tempList.size() != 0) {
								messageInfos.addAll(0, tempList);
								mListView.setSelection(tempList.size());
								mAdapter.notifyDataSetChanged();

							}
						}
					}
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				int beforeItem = firstVisibleItem - 4;
				if (beforeItem > 0) {
					recycleBitmapCaches(0, beforeItem);
				}

				int endItem = firstVisibleItem + visibleItemCount + 4;
				if (endItem < totalItemCount) {
					recycleBitmapCaches(endItem, totalItemCount);
				}
			}
		});

		mListView.setOnItemLongClickListener(this);
		mMsgSendBtn = (Button) findViewById(R.id.chat_box_btn_send);
		mMsgSendBtn.setText(mContext.getString(R.string.send));
		mMsgSendBtn.setOnClickListener(this);

		mContentEdit = (EditText) findViewById(R.id.chat_box_edit_keyword);
		mContentEdit.setOnClickListener(this);

		mToggleBtn = (ToggleButton) findViewById(R.id.chat_box_btn_info);
		mToggleBtn.setOnClickListener(this);

        mKeyBoardBtn = (ToggleButton) findViewById(R.id.chat_box_btn_keyboard);
        mKeyBoardBtn.setOnClickListener(this);

		mVoiceSendBtn = (Button) findViewById(R.id.chat_box_btn_voice);
		mVoiceSendBtn.setText(mContext.getString(R.string.pressed_to_record));
		mVoiceSendBtn.setOnTouchListener(new OnVoice());
		// mVoiceSendBtn.setVisibility(View.VISIBLE);

        mChatKeyBoradLayout = (RelativeLayout)findViewById(R.id.noraml_keyborad_layout);
        mKeyBoardMenuLayout = (LinearLayout)findViewById(R.id.key_board_menu_layout);
        mNormalSplite = (ImageView)findViewById(R.id.noraml_splite);

		mAddBtn = (ToggleButton) findViewById(R.id.chat_box_btn_add);
		mAddBtn.setOnClickListener(this);

		mChatExpraLayout = (View) findViewById(R.id.chat_box_layout_expra);

		mCameraBtn = (Button) findViewById(R.id.chat_box_expra_btn_camera);
		mCameraBtn.setOnClickListener(this);

		mGalleryBtn = (Button) findViewById(R.id.chat_box_expra_btn_picture);
		mGalleryBtn.setOnClickListener(this);

		mEmotionBtn = (Button) findViewById(R.id.chat_box_expra_btn_experssion);
		mEmotionBtn.setOnClickListener(this);

		mLocationBtn = (Button) findViewById(R.id.chat_box_expra_btn_location);
		mLocationBtn.setOnClickListener(this);

		mCardBtn = (Button) findViewById(R.id.chat_box_expra_btn_card);
		mCardBtn.setOnClickListener(this);

		mFavoritebtn = (Button) findViewById(R.id.chat_box_expra_btn_favorite);
		mFavoritebtn.setOnClickListener(this);

		mSmallVideoBtn = (Button) findViewById(R.id.small_video);
		mSmallVideoBtn.setOnClickListener(this);

		mViewPager = (ViewPager) findViewById(R.id.imagepager);
		mViewPager.setOnPageChangeListener(this);
		mLayoutCircle = (LinearLayout) findViewById(R.id.circlelayout);
		mEmotionLayout = (RelativeLayout) findViewById(R.id.emotionlayout);
		mEmotionLayout.setVisibility(View.GONE);

		mTotalEmotionList = getEmojiList();
		for (int i = 0; i < mTotalEmotionList.size(); i++) {
			addView(i);
		}

		mEmotionAdapter = new WeiYuanViewPagerAdapter(mViewList);
		mViewPager.setAdapter(mEmotionAdapter);
		mViewPager.setCurrentItem(0);
		showCircle(mViewList.size());

		mRootLayout = (ResizeLayout) findViewById(R.id.rootlayout);
		mRootLayout.setOnResizeListener(new OnResizeListener() {

			@Override
			public void OnResize(int w, int h, int oldw, int oldh) {
				int change = BIGGER;
				if (mIsFirst) {
					change = SMALLER;
					mIsFirst = false;
				}
				if (h < oldh) {
					change = SMALLER;
				}
				Message msg = new Message();
				msg.what = MSG_RESIZE;
				msg.arg1 = change;
				mHandler.sendMessage(msg);
			}
		});
		mListLayout = (ResizeLayout) findViewById(R.id.listlayout);
		mListLayout.setOnResizeListener(new OnResizeListener() {

			@Override
			public void OnResize(int w, int h, int oldw, int oldh) {

				int change = BIGGER;
				if (mIsFirst) {
					change = SMALLER;
					mIsFirst = false;
				}
				if (h < oldh) {
					change = SMALLER;
				}

				if (mListView.getLastVisiblePosition() == messageInfos.size() - 1) {
					Message msg = new Message();
					msg.what = MSG_RESIZE;
					msg.arg1 = change;
					mHandler.sendMessage(msg);
				}
			}
		});

		fCustomerVo = (Login) getIntent().getSerializableExtra("data");
		if (fCustomerVo.mIsRoom == 0)
			fCustomerVo.mIsRoom = 600;
		mType = fCustomerVo.mIsRoom;

        if (mType == MessageType.SERVICE_CHAT){
            SQLiteDatabase dbDatabase = DBHelper.getInstance(mContext).getReadableDatabase();
            UserTable userTable = new UserTable(dbDatabase);
            Login user = userTable.query(fCustomerVo.uid);
            if(user != null){
                fCustomerVo = user;
            }

            updateMenu();

            getServerMenuList(fCustomerVo.uid);
        }

		if (mType == GlobleType.SINGLE_CHAT) {// 单聊模式
			mRightBtn.setImageDrawable(mContext.getResources().getDrawable(R.drawable.people_btn));
			mRightBtn.setVisibility(View.VISIBLE);
		} else if (mType == GlobleType.GROUP_CHAT) {// 群聊模式
			mRightBtn.setImageDrawable(mContext.getResources().getDrawable(R.drawable.chat_btn));
			mRightBtn.setVisibility(View.VISIBLE);
		} else if (mType == GlobleType.MEETING_CHAT) {// 秘室模式

		}
		if (mFromPage == 1) {
			mRightBtn.setVisibility(View.GONE);
			mChatBottmLayout.setVisibility(View.GONE);
		} else {
			mChatBottmLayout.setVisibility(View.VISIBLE);
		}

		titileTextView.setText(getFromName());

		clearNotification();

		audioRecorder = new AudioRecorderAction(getBaseContext());
		mReaderImpl = new ReaderImpl(ChatMainActivity.this, handler, audioRecorder) {

			@Override
			public void stop(String path) {

				if (TextUtils.isEmpty(path)) {
					showToast(mContext.getString(R.string.record_time_too_short));
					return;
				}

				if (audioRecorder.getRecordTime() > AudioRecorderAction.MAX_TIME) {
					showToast(mContext.getString(R.string.record_time_too_long));
					return;
				} else if (audioRecorder.getRecordTime() < AudioRecorderAction.MIN_TIME) {
					showToast(mContext.getString(R.string.record_time_too_short));
					return;
				}
				File file = new File(path);
				if (file.exists()) {
					sendFile(MessageType.VOICE, path);
				} else {
					Toast.makeText(mContext, mContext.getString(R.string.file_not_exist), Toast.LENGTH_SHORT).show();
				}
			}

		};
		playListener = new AudioPlayListener(this) {

			@Override
			public void down(MessageInfo msg) {
				super.down(msg);
				downVoice(msg, 1);
			}

		};

		initMessageInfos();
		mListView.setOnTouchListener(this);
		mListView.setOnItemLongClickListener(this);
		mVoiceSendBtn.setOnTouchListener(new OnVoice());

		if (!opconnectState) {
			Toast.makeText(mContext, mContext.getString(R.string.connect_to_server), Toast.LENGTH_SHORT).show();
		}

		mContentEdit.setOnFocusChangeListener(sendTextFocusChangeListener);
		mContentEdit.setOnClickListener(sendTextClickListener);

		mContentEdit.setHint(mContext.getString(R.string.input_message_hint));
		mAdapter = new MyAdapter();
		mListView.setAdapter(mAdapter);

		if (mSearchContent != null && !mSearchContent.equals("")) {// 搜索内容需要定位
			checkRecordId();
		} else {
			mListView.setSelection(messageInfos.size() - 1);
		}
		if (messageInfos == null || messageInfos.size() < 20) {
			mHasLocalData = false;
		}

		/**
		 * 发送名片
		 */
		if (mSendCard == 1 && mCardMsg != null) {
			createDialog(mContext, mContext.getResources().getString(R.string.confirm_send) + mCardMsg.cardOwerName + mContext.getResources().getString(R.string.from_card_to_chat));
		}
		/**
		 * 转发内容
		 */
		if (mForMsg != null) {
			sendBroad2Save(mForMsg, true);
		}

		/**
		 * 显示搜索对话框
		 */
		if (mIsShowSearchDialog == 1) {
			MainSearchDialog searchDialog = new MainSearchDialog(mContext, 1, new OnFinishClick() {

				@Override
				public void onFinishListener() {
					ChatMainActivity.this.finish();
				}
			}, 1, fCustomerVo.uid, mType);
			searchDialog.show();
		}
	}

    /**
     * 获取服务号菜单
     */
    private void getServerMenuList(final String fuid){
        new Thread(){
            @Override
            public void run(){
                if(ResearchCommon.verifyNetwork(mContext)) {
                    try {
                        String result = ResearchCommon.getResearchInfo().getServerMenuList(fuid);
                        if (result != null) {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(result);
                                fCustomerVo.menuString = jsonObject.getString("data");
                            } catch (JSONException e) {
                                e.printStackTrace();
                                return;
                            }

                            if(fCustomerVo.menuString!=null && !fCustomerVo.menuString.equals("")){
                                JSONArray array = null;
                                try {
                                    array = new JSONArray(fCustomerVo.menuString);
                                    if(array!=null && array.length()>0){
                                        fCustomerVo.menuList = new ArrayList<OrderMenuItem>();
                                        for (int i = 0; i < array.length(); i++) {
                                            fCustomerVo.menuList.add(new OrderMenuItem(array.getJSONObject(i)));
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            SQLiteDatabase dbDatabase = DBHelper.getInstance(mContext).getReadableDatabase();
                            UserTable userTable = new UserTable(dbDatabase);
                            userTable.update(fCustomerVo);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateMenu();
                                }
                            });
                        }
                    } catch (ResearchException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void updateMenu(){
        showChatKeyBorad();
        togInfoKeySelect(false);
    }

    /**
     * 键盘选择器
     */
    private void togInfoKeySelect(boolean isShow){
        hideExpra();
        animation(isShow);
    }

    private void animation(boolean isShow){
        if(isShow){
            AnimationSet animdn = new AnimationSet(true);
            TranslateAnimation mytranslateanimdn0 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,+1.0f);
            mytranslateanimdn0.setDuration(200);
            TranslateAnimation mytranslateanimdn1 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,-1.0f);
            mytranslateanimdn1.setDuration(200);
            mytranslateanimdn1.setStartOffset(200);
            animdn.addAnimation(mytranslateanimdn0);
            animdn.addAnimation(mytranslateanimdn1);
            mChatLayout.startAnimation(animdn);
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mKeyBoardBtn.isChecked()) {
                    mChatLayout.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    mKeyBoardMenuLayout.setVisibility(View.VISIBLE);
                    mNormalSplite.setVisibility(View.GONE);
                    mChatKeyBoradLayout.setVisibility(View.GONE);
                } else {
                    mChatLayout.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    mKeyBoardMenuLayout.setVisibility(View.GONE);
                    mNormalSplite.setVisibility(View.VISIBLE);
                    mChatKeyBoradLayout.setVisibility(View.VISIBLE);
                }
            }
        }, 200);
    }


    private String getFromName() {
		String nickname = fCustomerVo.remark;
		if (nickname == null || nickname.equals("")) {
			nickname = fCustomerVo.nickname;
		}
		return nickname;
	}

	/**
	 * 创建发送名片对话框
	 * 
	 * @param context
	 * @param cardTitle
	 */
	protected void createDialog(Context context, String cardTitle) {
		mPhoneDialog = new Dialog(context, R.style.dialog);
		LayoutInflater factor = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View serviceView = factor.inflate(R.layout.card_dialog, null);

		mPhoneDialog.setContentView(serviceView);
		mPhoneDialog.show();
		mPhoneDialog.setCancelable(false);
		mPhoneDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT
		/* mContext.getResources().getDimensionPixelSize(R.dimen.bind_phone_height) */
		, LayoutParams.WRAP_CONTENT);

		/*
		 * TextView signId=(TextView) serviceView .findViewById(R.id.sign_id); signId.setText(string[0]);
		 */
		final TextView phoneEdit = (TextView) serviceView.findViewById(R.id.card_title);

		phoneEdit.setText(cardTitle);

		Button okBtn = (Button) serviceView.findViewById(R.id.yes);
		okBtn.setText(mContext.getResources().getString(R.string.ok));

		okBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mPhoneDialog != null) {
					mPhoneDialog.dismiss();
					mPhoneDialog = null;
				}
				if (mCardMsg != null) {
					sendBroad2Save(mCardMsg, false);
				}

			}
		});

		Button Cancel = (Button) serviceView.findViewById(R.id.no);
		Cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mPhoneDialog != null) {
					mPhoneDialog.dismiss();
					mPhoneDialog = null;
				}
			}
		});
	}

	/**
	 * 释放图片
	 * 
	 * @param start
	 * @param end
	 */
	private void recycleBitmapCaches(int start, int end) {
		if (mAdapter != null) {
			HashMap<String, Bitmap> buffer = mImageLoader.getImageBuffer();
			for (int i = start; i < end; i++) {
				if (messageInfos.get(i).typefile == MessageType.PICTURE) {
					String url = messageInfos.get(i).imgUrlS;
					ImageView imageView = (ImageView) mListView.findViewWithTag(url);
					if (imageView != null) {
						imageView.setImageBitmap(null);
						imageView.setImageResource(R.drawable.normal);
					}

					if (url.startsWith("http://") && 1 == messageInfos.get(i).getSendState()) {
						Bitmap bitmap = buffer.get(url);
						if (bitmap != null && !bitmap.isRecycled()) {
							bitmap.recycle();
							bitmap = null;
							buffer.remove(url);
						}
					} else {
						if (mBitmapCache != null && mBitmapCache.get(url) != null) {
							Bitmap bitmap = mBitmapCache.get(url).get();
							if (bitmap != null && !bitmap.isRecycled()) {
								bitmap.recycle();
								bitmap = null;
								mBitmapCache.remove(url);
							}
						}

					}
				} else if (messageInfos.get(i).typefile == MessageType.SMALL_VIDEO) {
					/*
					 * String url = messageInfos.get(i).imgUrlS; if( url != null && !url.equals("")){ GifView gifView = (GifView)mListView.findViewWithTag(url); if (gifView != null) { gifView.setGifImage(0); //imageView.setImageResource(R.drawable.normal); }
					 * 
					 * if(url.startsWith("http://") && 1 == messageInfos.get(i).getSendState()){ Bitmap bitmap = buffer.get(url); if (bitmap != null && !bitmap.isRecycled()) { bitmap.recycle(); bitmap = null; buffer.remove(url); } }else { if(mBitmapCache!=null && mBitmapCache.get(url)!=null){ Bitmap bitmap = mBitmapCache.get(url).get(); if (bitmap != null && !bitmap.isRecycled()) {
					 * bitmap.recycle(); bitmap = null; mBitmapCache.remove(url); } }
					 * 
					 * } }
					 */
				}
			}

		}
	}

	/**
	 * 创建销毁对话框
	 * 
	 * @param title
	 */
	private void destoryDialog(String title) {
		AlertDialog builder = new AlertDialog.Builder(this).create();
		builder.setTitle(title);
		builder.setButton(mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				ChatMainActivity.this.finish();
			}
		});
		builder.setCancelable(false);
		builder.setCanceledOnTouchOutside(false);
		builder.show();
	}

	/**
	 * 隐藏底部控件
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {

			if (mChatExpraLayout.getVisibility() == View.VISIBLE || mEmotionLayout.getVisibility() == View.VISIBLE) {
				hideEmojiGridView();
			}

			if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
				InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 处理消息
	 */
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_RESIZE:
				if (msg.arg1 != BIGGER) {
					if ((mSearchContent == null || mSearchContent.equals("")) && messageInfos != null && messageInfos.size() != 0) {
						mListView.setSelection(messageInfos.size() - 1);
					}
				}

				break;

			case SEND_SUCCESS:
				MessageInfo messageInfo = (MessageInfo) msg.obj;
				updateNewMessage(messageInfo);
				modifyMessageState(messageInfo);
				if (msg.arg2 == 4) {
					// Toast.makeText(mContext, R.string.refuest_message, Toast.LENGTH_LONG).show();
				}
				break;

			case CHANGE_STATE:
				MessageInfo messageSend = (MessageInfo) msg.obj;
				updateMessage(messageSend);
				break;

			case SEND_FAILED:
				MessageInfo message = (MessageInfo) msg.obj;
				/*
				 * if(isResend == 0){ Intent refreshIntent = new Intent(MessageTab.ACTION_REFRESH_SESSION); refreshIntent.putExtra("message", message); mContext.sendBroadcast(refreshIntent); }
				 */
				updateMessage(message);
				modifyMessageState(message);
				break;

			case GlobalParam.HIDE_PROGRESS_DIALOG:
				hideProgressDialog();

				break;

			case GlobalParam.SHOW_PROGRESS_DIALOG:
				String dialogMsg = (String) msg.obj;
				showProgressDialog(dialogMsg);
				mProgressDialog.setCancelable(false);
				break;

			case GlobalParam.MSG_NETWORK_ERROR:
				hideProgressDialog();
				Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_LONG).show();
				break;

			case GlobalParam.MSG_TICE_OUT_EXCEPTION:
				hideProgressDialog();
				String prompt = (String) msg.obj;
				if (prompt == null || prompt.equals("")) {
					prompt = mContext.getString(R.string.timeout);
				}
				Toast.makeText(mContext, prompt, Toast.LENGTH_LONG).show();
				break;

			case HIDE_PROGRESS_DIALOG:
				hideProgressDialog();
				break;

			case SHOW_KICK_OUT_DIALOG:
				destoryDialog(mContext.getString(R.string.you_have_been_removed_from_group));
				break;
			case GlobalParam.MSG_CHECK_FAVORITE_STATUS:
				WeiYuanState favoriteResult = (WeiYuanState) msg.obj;
				if (favoriteResult == null) {
					Toast.makeText(mContext, R.string.commit_dataing, Toast.LENGTH_LONG).show();
					return;
				}
				if (favoriteResult.code != 0) {
					Toast.makeText(mContext, favoriteResult.errorMsg, Toast.LENGTH_LONG).show();
					return;
				}
				break;

			default:
				break;
			}
		}

	};

	/**
	 * 拍一张照片
	 */
	private void getImageFromCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		String tmpUrl = FeatureFunction.getPhotoFileName(1);
		if (FeatureFunction.newFolder(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY)) {
			File out = new File(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY, tmpUrl);// TEMP_FILE_NAME /*FeatureFunction.getPhotoFileName()*/);
			Uri uri = Uri.fromFile(out);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

			startActivityForResult(intent, REQUEST_GET_IMAGE_BY_CAMERA);
		}
	}

	/**
	 * 从相册中选取图片
	 */
	private void getImageFromGallery() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");

		startActivityForResult(intent, REQUEST_GET_URI);
	}

	/**
	 * 视频拍摄
	 */
	private void videoMethod() {
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
		startActivityForResult(intent, REQUEST_CODE_TAKE_VIDEO);
	}

	/**
	 * 处理图片
	 * 
	 * @param isGallery
	 * @param data
	 */
	private void doChoose(final boolean isGallery, final Intent data) {
		if (isGallery) {
			originalImage(data);
		} else {
			if (data != null) {
				originalImage(data);
			} else {
				// Here if we give the uri, we need to read it
				String tempUrl = WeiYuanCommon.getCamerUrl(mContext);
				String path = Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY + tempUrl;
				Log.e("path", "path:" + path);
				if (tempUrl == null || tempUrl.equals("")) {
					return;
				}
				Log.e("start-end", path.indexOf(".") + ":" + path.length());
				String extension = path.substring(path.indexOf("."), path.length());
				if (FeatureFunction.isPic(extension)) {
					// startPhotoZoom(Uri.fromFile(new File(path)));
					Intent intent = new Intent(mContext, RotateImageActivity.class);
					intent.putExtra("path", path);
					startActivityForResult(intent, REQUEST_GET_BITMAP);
				}
			}
		}
	}

	private void originalVideo(Intent data) {
		/*
		 * switch (requestCode) {
		 */
		// case FLAG_CHOOSE:
		Uri uri = data.getData();
		// Log.d("may", "uri=" + uri + ", authority=" + uri.getAuthority());
		if (!TextUtils.isEmpty(uri.getAuthority())) {
			Cursor cursor = getContentResolver().query(uri, new String[] { MediaStore.Video.Media.DATA }, null, null, null);
			if (null == cursor) {
				// Toast.makeText(mContext, R.string.no_found, Toast.LENGTH_SHORT).show();
				return;
			}
			cursor.moveToFirst();
			String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
			Log.d("video_may", "path=" + path);
			String extension = path.substring(path.lastIndexOf("."), path.length());
			if (FeatureFunction.isMp4(extension)) {
				File file = new File(path);
				if (file != null && file.exists()) {
					sendFile(MessageType.SMALL_VIDEO, path);
				}
				// startPhotoZoom(data.getData());

			} else {
				// Toast.makeText(mContext, R.string.please_choose_pic, Toast.LENGTH_SHORT).show();
			}
			// ShowBitmap(false);

		} else {
			Log.d("may", "path=" + uri.getPath());
			String path = uri.getPath();
			String extension = path.substring(path.lastIndexOf("."), path.length());
			if (FeatureFunction.isPic(extension)) {
				Intent intent = new Intent(mContext, RotateImageActivity.class);
				intent.putExtra("path", path);
				startActivityForResult(intent, REQUEST_GET_BITMAP);
			} else {
				// Toast.makeText(mContext, R.string.please_choose_pic, Toast.LENGTH_SHORT).show();
			}
			// mImageFilePath = uri.getPath();
			// ShowBitmap(false);
		}
	}

	private void originalImage(Intent data) {
		/*
		 * switch (requestCode) {
		 */
		// case FLAG_CHOOSE:
		Uri uri = data.getData();
		// Log.d("may", "uri=" + uri + ", authority=" + uri.getAuthority());
		if (!TextUtils.isEmpty(uri.getAuthority())) {
			Cursor cursor = getContentResolver().query(uri, new String[] { MediaStore.Images.Media.DATA }, null, null, null);
			if (null == cursor) {
				// Toast.makeText(mContext, R.string.no_found, Toast.LENGTH_SHORT).show();
				return;
			}
			cursor.moveToFirst();
			String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
			Log.d("may", "path=" + path);
			String extension = path.substring(path.lastIndexOf("."), path.length());
			if (FeatureFunction.isPic(extension)) {
				Intent intent = new Intent(mContext, RotateImageActivity.class);
				intent.putExtra("path", path);
				startActivityForResult(intent, REQUEST_GET_BITMAP);

				// startPhotoZoom(data.getData());

			} else {
				// Toast.makeText(mContext, R.string.please_choose_pic, Toast.LENGTH_SHORT).show();
			}
			// ShowBitmap(false);

		} else {
			Log.d("may", "path=" + uri.getPath());
			String path = uri.getPath();
			String extension = path.substring(path.lastIndexOf("."), path.length());
			if (FeatureFunction.isPic(extension)) {
				Intent intent = new Intent(mContext, RotateImageActivity.class);
				intent.putExtra("path", path);
				startActivityForResult(intent, REQUEST_GET_BITMAP);
			} else {
				// Toast.makeText(mContext, R.string.please_choose_pic, Toast.LENGTH_SHORT).show();
			}
			// mImageFilePath = uri.getPath();
			// ShowBitmap(false);
		}
	}

	/**
	 * 页面返回结果
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {

		case REQUEST_CODE_TAKE_VIDEO:// 拍摄视频 if (resultCode == RESULT_OK) {
			Uri uriVideo = data.getData();
			Cursor cursor = this.getContentResolver().query(uriVideo, null, null, null, null);
			if (cursor.moveToNext()) {
				/** _data：文件的绝对路径 ，_display_name：文件名 */

				String strVideoPath = cursor.getString(cursor.getColumnIndex("_data"));
				Toast.makeText(this, strVideoPath, Toast.LENGTH_SHORT).show();
			}
			break;
		case RESQUEST_CODE:
			if (data != null && RESULT_OK == resultCode) {
				Bundle bundle = data.getExtras();
				if (bundle != null) {

					MapInfo mapInfo = (MapInfo) data.getSerializableExtra("mapInfo");
					if (mapInfo == null) {
						Toast.makeText(mContext, mContext.getString(R.string.get_location_failed), Toast.LENGTH_SHORT).show();
						return;
					}

					sendMap(mapInfo);
				}
			}
			break;

		case REQUEST_GET_URI:
			if (resultCode == RESULT_OK) {
				doChoose(true, data);
			}

			break;
		case 1:
			if (resultCode == RESULT_OK) {
				originalVideo(data);
			}

			break;

		case REQUEST_GET_IMAGE_BY_CAMERA:
			if (resultCode == RESULT_OK) {
				doChoose(false, data);
			}
			break;

		case REQUEST_GET_BITMAP:
			if (resultCode == RESULT_OK) {
				mFilePath = data.getStringExtra("path");
				int width = data.getIntExtra("width", 0);
				int height = data.getIntExtra("height", 0);
				if (width != 0 && height != 0) {
					int[] scale = FeatureFunction.getScalcSize(width, height);
					if (scale[0] != 0 && scale[1] != 0) {
						mScalcWith = scale[0];
						mScalcHeigth = scale[1];
						Log.e("width - height", mScalcWith + ":" + mScalcHeigth);
					}

				}
				if (mFilePath != null && !mFilePath.equals("")) {
					sendFile(MessageType.PICTURE, mFilePath);
				}
			}
			break;
		case REQUEST_GET_VIDEO_BY_CAMERA:
			if (resultCode == RESULT_OK) {
				String videoUrl = data.getStringExtra("video_url");
				String jifUrl = data.getStringExtra("video_gif");
				if (videoUrl != null && !videoUrl.equals("")) {
					if (jifUrl != null && !jifUrl.equals("")) {
						sendFile(MessageType.SMALL_VIDEO, videoUrl, jifUrl);
					} else {
						sendFile(MessageType.SMALL_VIDEO, videoUrl);
					}

					/*
					 * File file = File.(videoUrl); if(file.exists()){ sendFile(MessageType.SMALL_VIDEO, videoUrl); }else { Toast.makeText(mContext, mContext.getString(R.string.file_not_exist), Toast.LENGTH_SHORT).show(); }
					 */
				} else {
					Toast.makeText(mContext, mContext.getString(R.string.file_not_exist), Toast.LENGTH_SHORT).show();
				}

			}
			break;
		case REQUEST_GET_VIDEO_IMAGE:
			if (resultCode == RESULT_OK && data != null) {
				ArrayList<String> fileStringList = data.getStringArrayListExtra("filePathList");
				if (fileStringList != null && fileStringList.size() > 0) {
					for (int i = 0; i < fileStringList.size(); i++) {
						String filePath = fileStringList.get(i);
						String extension = filePath.substring(filePath.lastIndexOf("."), filePath.length());
						if (FeatureFunction.isVideo(extension)) {// 视频
							File file = new File(filePath);
							if (file != null) {
								if (file.length() < 0) {
									Toast.makeText(mContext, "请检查文件是否可以播放", Toast.LENGTH_LONG).show();
								} else if (0 < file.length() && file.length() < 10 * 1024 * 1024) {// 设置视频文件的最大值为10M,单位B
									sendFile(MessageType.SMALL_VIDEO, filePath);
								} else {
									Toast.makeText(mContext, "文件最大为10M,请选择其它文件", Toast.LENGTH_LONG).show();
								}
//							} else {
//								Toast.makeText(mContext, "请检查文件是否存在", Toast.LENGTH_LONG).show();
							}
						} else if (FeatureFunction.isPic(extension)) {// 图片
							sendFile(MessageType.PICTURE, filePath);
						} else {
							Toast.makeText(mContext, "只能上传jpg、png、jpeg类型的图片", Toast.LENGTH_LONG).show();
						}
					}
				}
			}

			/*
			 * // =
			 */
			break;
		case CAMERA_ACTIVITY:
			if (resultCode != RESULT_OK)
				return;
			try {
				AssetFileDescriptor videoAsset = getContentResolver().openAssetFileDescriptor(data.getData(), "r");
				FileInputStream fis = videoAsset.createInputStream();
				File tmpFile = new File(Environment.getExternalStorageDirectory(), "recordVideo_0_5.mp4");
				FileOutputStream fos = new FileOutputStream(tmpFile);
				byte[] buf = new byte[1024];
				int len;
				while ((len = fis.read(buf)) > 0) {
					fos.write(buf, 0, len);
				}
				fis.close();
				fos.close();
			} catch (IOException io_e) {
				// TODO: handle error
			}
			break;

		default:
			break;
		}
	}

	/**
	 * 获取本地聊天内容
	 */
	private void initMessageInfos() {
		SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
		MessageTable messageTable = new MessageTable(db);
		boolean status = messageTable.updateReadState(fCustomerVo.uid, mType);
		if (status) {
			mContext.sendBroadcast(new Intent(ChatFragment.ACTION_REFRESH_SESSION));
			mContext.sendBroadcast(new Intent(GlobalParam.ACTION_UPDATE_SESSION_COUNT));
			if (mType == GlobleType.MEETING_CHAT) {
				mContext.sendBroadcast(new Intent(MettingDetailActivity.ACTION_HIDE_NEW_MEETING_TIP));
				mContext.sendBroadcast(new Intent(GlobalParam.ACTION_REFRESH_MEETING_LIST));
			}
		}
		messageInfos = messageTable.query(fCustomerVo.uid, -1, mType);

		if (messageInfos == null) {
			messageInfos = new ArrayList<MessageInfo>();
		} else {
			for (int i = 0; i < messageInfos.size(); i++) {
				if (messageInfos.get(i).readState == 0) {
					messageInfos.get(i).readState = 1;
					updateMessage(messageInfos.get(i));
				} else if (messageInfos.get(i).sendState == 2) {
					messageInfos.get(i).sendState = 0;
					updateMessage(messageInfos.get(i));
				}
			}
		}

	}

	/**
	 * 获取定位记录的下标
	 */
	private void checkRecordId() {
		int checkId = -1;
		if (mSearchContent != null && !mSearchContent.equals("")) {
			SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
			MessageTable messageTable = new MessageTable(db);
			boolean isExits = true;

			for (int i = 0; i < messageInfos.size(); i++) {
				if ((messageInfos.get(i).content != null && !messageInfos.get(i).content.equals("")) && !messageInfos.get(i).content.equals(mSearchContent)) {
					isExits = false;
				} else if (messageInfos.get(i).content == null || messageInfos.get(i).content.equals("")) {
					isExits = false;
				} else {
					checkId = i;
					isExits = true;
					break;
				}

				if (i == messageInfos.size() && !isExits) {
					isExits = false;
				}
			}
			if (!isExits) {
				List<MessageInfo> tempList = messageTable.query(fCustomerVo.uid, messageInfos.get(0).auto_id, mType);
				if (tempList == null || tempList.size() < 20) {
					mHasLocalData = false;
				}

				if (tempList != null && tempList.size() != 0) {
					messageInfos.addAll(0, tempList);
					checkRecordId();
				}

			}
			if (checkId != -1) {
				mListView.setSelection(checkId);
			}
			mAdapter.notifyDataSetChanged();

		}
	}

	/************ 发送消息 ************/
	private void send() {
		// startService(new Intent(getBaseContext(), SnsService.class));
	}

	/**
	 * 发送文本
	 */
	private void sendText() {
		send();
		Log.d(TAG, "sendText()");
		String str = mContentEdit.getText().toString();
		if (str != null && (str.trim().replaceAll("\r", "").replaceAll("\t", "").replaceAll("\n", "").replaceAll("\f", "")) != "") {
			if (str.length() > WeiYuanCommon.MESSAGE_CONTENT_LEN) {
				// showToast(mContext.getString(R.string.message_limit_count));
				return;
			}
			mContentEdit.setText("");

			MessageInfo msg = new MessageInfo();
			msg.fromid = WeiYuanCommon.getUserId(mContext);
			msg.tag = UUID.randomUUID().toString();
			msg.fromname = mLogin.nickname;
			msg.fromurl = mLogin.headsmall;
			msg.toid = fCustomerVo.uid;
			msg.toname = getFromName();
			msg.tourl = fCustomerVo.headsmall;
			msg.typefile = MessageType.TEXT;
			msg.content = str;
			msg.typechat = mType;
			msg.time = System.currentTimeMillis();
			msg.readState = 1;

			sendBroad2Save(msg, false);
		}
	}

	/**
	 * 发送地图
	 * 
	 * @param mapInfo
	 */
	private void sendMap(MapInfo mapInfo) {
		send();
		MessageInfo msg = new MessageInfo();
		msg.fromid = WeiYuanCommon.getUserId(mContext);
		msg.tag = UUID.randomUUID().toString();
		msg.fromname = mLogin.nickname;
		msg.fromurl = mLogin.headsmall;
		msg.toid = fCustomerVo.uid;
		msg.toname = getFromName();
		msg.tourl = fCustomerVo.headsmall;
		msg.typefile = MessageType.MAP;
		msg.mLat = Double.parseDouble(mapInfo.getLat());
		msg.mLng = Double.parseDouble(mapInfo.getLng());
		msg.mAddress = mapInfo.getAddr();
		msg.typechat = mType;
		msg.time = System.currentTimeMillis();
		msg.readState = 1;

		sendBroad2Save(msg, false);
	}

	/**
	 * 发送声音和图片
	 * 
	 * @param type
	 *            2-图片 3-声音 6-小视屏
	 * @param filePath
	 */
	private void sendFile(int type, String filePath) {

		MessageInfo msg = new MessageInfo();
		msg.fromid = WeiYuanCommon.getUserId(mContext);
		msg.tag = UUID.randomUUID().toString();
		msg.fromname = mLogin.nickname;
		msg.fromurl = mLogin.headsmall;
		msg.toid = fCustomerVo.uid;
		msg.toname = getFromName();
		msg.tourl = fCustomerVo.headsmall;
		msg.imgWidth = mScalcWith;
		msg.imgHeight = mScalcHeigth;
		if (type == MessageType.VOICE) {
			msg.voiceUrl = filePath;
			msg.setVoiceTime((int) mReaderImpl.getReaderTime());
		} else if (type == MessageType.PICTURE) {
			msg.imgUrlS = filePath;
		} else if (type == MessageType.SMALL_VIDEO) {
			msg.videoUrl = filePath;
		}
		msg.typefile = type;
		msg.typechat = mType;
		msg.time = System.currentTimeMillis();
		msg.readState = 1;
		addMessageInfo(msg);
		sendFilePath(msg, 0);

	}

	/**
	 * 发送声音和图片
	 * 
	 * @param type
	 *            2-图片 3-声音 6-小视屏
	 * @param filePath
	 */
	private void sendFile(int type, String filePath, String gifPath) {

		MessageInfo msg = new MessageInfo();
		msg.fromid = WeiYuanCommon.getUserId(mContext);
		msg.tag = UUID.randomUUID().toString();
		msg.fromname = mLogin.nickname;
		msg.fromurl = mLogin.headsmall;
		msg.toid = fCustomerVo.uid;
		msg.toname = getFromName();
		msg.tourl = fCustomerVo.headsmall;
		msg.imgWidth = mScalcWith;
		msg.imgHeight = mScalcHeigth;
		if (type == MessageType.VOICE) {
			msg.voiceUrl = filePath;
			msg.setVoiceTime((int) mReaderImpl.getReaderTime());
		} else if (type == MessageType.PICTURE) {
			msg.imgUrlS = filePath;
		} else if (type == MessageType.SMALL_VIDEO) {
			msg.videoUrl = filePath;
			if (gifPath != null && !gifPath.equals("")) {
				msg.imgUrlS = gifPath;
			}
		}
		msg.typefile = type;
		msg.typechat = mType;
		msg.time = System.currentTimeMillis();
		msg.readState = 1;
		addMessageInfo(msg);
		sendFilePath(msg, 0);

	}

	private void sendFilePath(MessageInfo messageInfo, int isResend) {
		sendMessage(messageInfo, isResend, false);
	}

	/**
	 * 重发文件
	 * 
	 * @param messageInfo
	 */
	private void resendFile(MessageInfo messageInfo) {
		try {
			sendFilePath(messageInfo, 1);
		} catch (Exception e) {
			Log.d(TAG, "resendVoice:", e);
			showToast(mContext.getString(R.string.resend_failed));
		}
	}

	// 下载音频
	private synchronized void downVoice(final MessageInfo msg, final int type) {
		if (!FeatureFunction.checkSDCard()) {
			return;
		}

		if (downVoiceList.contains(msg.voiceUrl)) {
			// showToast(mContext.getString(R.string.download_voice));
			return;
		}
		downVoiceList.add(msg.voiceUrl);
		File voicePath = ReaderImpl.getAudioPath(getBaseContext());
		String tag = FeatureFunction.generator(msg.voiceUrl);
		String tagName = new File(voicePath, tag).getAbsolutePath();
		HttpGet get = new HttpGet(msg.voiceUrl);
		DefaultHttpClient client = Utility.getHttpClient(mContext);

		VoiceTask<File> voiceTask = new VoiceTask<File>(client, new SyncBasicHttpContext(new BasicHttpContext()), new AjaxCallBack<File>() {
			@Override
			public void onSuccess(File t) {
				super.onSuccess(t);
				downVoiceSuccess(msg, type);
				downVoiceList.remove(msg.voiceUrl);
			}

			@Override
			public void onFailure(Throwable t, String strMsg) {
				super.onFailure(t, strMsg);
				showToast(mContext.getString(R.string.download_voice_error) + strMsg);
				downVoiceList.remove(msg.voiceUrl);
			}
		});

		Executor executor = Executors.newFixedThreadPool(5, new ThreadFactory() {
			private final AtomicInteger mCount = new AtomicInteger(1);

			@Override
			public Thread newThread(Runnable r) {
				Thread tread = new Thread(r, "FinalHttp #" + mCount.getAndIncrement());
				tread.setPriority(Thread.NORM_PRIORITY - 1);
				return tread;
			}
		});
		voiceTask.executeOnExecutor(executor, get, tagName);
	}

	private void downVoiceSuccess(final MessageInfo msg, final int type) {
		msg.setSendState(1);
		SQLiteDatabase dbDatabase = DBHelper.getInstance(mContext).getWritableDatabase();
		MessageTable messageTable = new MessageTable(dbDatabase);
		messageTable.update(msg);
		if (type == 1 && playListener.getMessageTag().equals(msg.tag)) {
			playListener.play(msg);
		}
	}

	/**
	 * 删除临时图片
	 * 
	 * @param path
	 */
	private void deleteImgFile(String path) {
		File file = new File(path);
		if (file != null && file.exists()) {
			file.delete();
		}
	}

	/********* 聊天选择器 ***********/

	private void togInfoSelect() {
		hideExpra();
		if (mToggleBtn.isChecked()) {
			// 语音开启显示
			mContentEdit.setVisibility(View.GONE);
			mMsgSendBtn.setVisibility(View.GONE);
			mVoiceSendBtn.setVisibility(View.VISIBLE);
			hideSoftKeyboard(mToggleBtn);
		} else {
			mContentEdit.setVisibility(View.VISIBLE);
			mMsgSendBtn.setVisibility(View.VISIBLE);
			mVoiceSendBtn.setVisibility(View.GONE);
			hideSoftKeyboard(mToggleBtn);
		}
	}

	/* 重发信息 */
	private void btnResendAction(MessageInfo messageInfo) {
		if (messageDialog != null && messageDialog.isShowing()) {
			messageDialog.cancel();
		}
		if (messageInfo != null) {
			switch (messageInfo.typefile) {
			case MessageType.PICTURE:
			case MessageType.VOICE:
				resendFile(messageInfo);
				break;
			case MessageType.TEXT:
				// sendBroad2Update(messageInfo, 1);
				sendMessage(messageInfo, 1, false);
				break;

			default:
				break;
			}
		}
	};

	/**
	 * 显示表情
	 */
	private void btnEmojiAction() {
		showEmojiGridView();
	}

	/*
	 * 显示照相机
	 */
	private void btnCameraAction() {
		getImageFromCamera();
		hideExpra();
	}

	/*
	 * 选择图片
	 */
	private void btnPhotoAction() {

		Intent intent = new Intent(mContext, SelectPicAndVideoActivity.class);
		startActivityForResult(intent, REQUEST_GET_VIDEO_IMAGE);
		// getImageFromGallery();
		hideExpra();
	}

	/*
	 * 发送地图
	 */
	private void btnLocationAction() {
		hideExpra();
		Intent intent = new Intent(this, LocationActivity.class);
		startActivityForResult(intent, RESQUEST_CODE);
	}

	/*
	 * 处理+号事件
	 */
	private void btnAddAction() {
		if (mChatExpraLayout.getVisibility() == View.VISIBLE) {
			hideExpra();
		} else {

			if (mEmotionLayout.getVisibility() == View.VISIBLE) {
				hideEmojiGridView();
			}
			showExpra();
		}
	}

	/*
	 * 隐藏键盘显示聊天选择器
	 */
	private void showExpra() {
		hideSoftKeyboard();
		mChatExpraLayout.setVisibility(View.VISIBLE);
	}

	/*
	 * 隐藏聊天选择器
	 */
	private void hideExpra() {
		mChatExpraLayout.setVisibility(View.GONE);
		mAddBtn.setChecked(false);
	}

	/*
	 * 键盘返回事件 (non-Javadoc)
	 * 
	 * @see android.app.Activity#dispatchKeyEvent(android.view.KeyEvent)
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				hideSoftKeyboard();
				if (mChatExpraLayout.getVisibility() == View.VISIBLE || mEmotionLayout.getVisibility() == View.VISIBLE) {
					hideEmojiGridView();
					// hideExpra();
					return true;
				}
			} else {
				if (mIsShowSearchDialog == 1) {
					ChatMainActivity.this.finish();
					return true;
				}
			}

		}
		return super.dispatchKeyEvent(event);
	}

	/*
	 * 消息发送成功
	 */
	private void sendBroad2Save(MessageInfo msg, boolean isForward) {
		addMessageInfo(msg);
		msg.setSendState(1);
		/*
		 * Intent intent = new Intent(PushChatMessage.ACTION_SEND_MESSAGE); intent.putExtra(PushChatMessage.EXTRAS_MESSAGE, msg); intent.putExtra("groupname", mRoomName); sendBroadcast(intent);
		 */
		sendMessage(msg, 0, isForward);
	}

	/*
	 * 发送消息
	 */
	private void sendMessage(final MessageInfo msg, final int isResend, final boolean isForward) {
		new Thread() {
			@Override
			public void run() {
				if (WeiYuanCommon.verifyNetwork(mContext)) {
					msg.sendState = 2;
					Message stateMessage = new Message();
					stateMessage.obj = msg;
					stateMessage.what = CHANGE_STATE;
					mHandler.sendMessage(stateMessage);
					try {
						MessageResult result = WeiYuanCommon.getWeiYuanInfo().sendMessage(msg, isForward);
						if (result != null && result.mState != null && (result.mState.code == 0 || result.mState.code == 4)) {
							result.mMessageInfo.sendState = 1;
							if (msg.typefile == MessageType.VOICE) {
								String voice = FeatureFunction.generator(result.mMessageInfo.voiceUrl);
								FeatureFunction.reNameFile(new File(msg.voiceUrl), voice);
							}
							result.mMessageInfo.readState = 1;
							Message message = new Message();
							message.what = SEND_SUCCESS;
							message.arg1 = isResend;
							if (result.mState.code == 4) {
								message.arg2 = 4;
							}
							message.obj = result.mMessageInfo;
							mHandler.sendMessage(message);
							return;
						} else if (result != null && result.mState != null && result.mState.code == 3) {
							SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
							SessionTable sessionTable = new SessionTable(db);
							MessageTable messageTable = new MessageTable(db);
							Session session = sessionTable.query(fCustomerVo.uid, 300);
							if (session != null) {
								messageTable.delete(fCustomerVo.uid, 300);
								sessionTable.delete(fCustomerVo.uid, 300);

								mContext.sendBroadcast(new Intent(ChatFragment.ACTION_REFRESH_SESSION));
								mContext.sendBroadcast(new Intent(GlobalParam.ACTION_UPDATE_SESSION_COUNT));
							}
							mHandler.sendEmptyMessage(SHOW_KICK_OUT_DIALOG);
							return;
						}
					} catch (WeiYuanException e) {
						e.printStackTrace();
					}

				} else {
					mHandler.sendEmptyMessage(GlobalParam.MSG_NETWORK_ERROR);
				}

				msg.sendState = 0;
				Message message = new Message();
				message.what = SEND_FAILED;
				message.arg1 = isResend;
				message.obj = msg;
				mHandler.sendMessage(message);
			}
		}.start();
	}

	/**
	 * op是否连接
	 * 
	 * @return 作者:Mr.Sam <br />
	 *         创建时间:2013-6-9<br />
	 *         修改时间:<br />
	 */
	private boolean isOpconnect() {
		return true;
	}

	/*
	 * 将消息显示在界面中
	 */
	private void addMessageInfo(MessageInfo info) {

		mVoiceSendBtn.setText(mContext.getString(R.string.pressed_to_record));
		mContentEdit.setHint(mContext.getString(R.string.input_message_hint));
		if (messageInfos.size() == 0) {
			messageInfos.add(info);
		} else {
			boolean isEixst = false;
			for (int i = 0; i < messageInfos.size(); i++) {
				if (messageInfos.get(i).tag.equals(info.tag)) {
					isEixst = true;
					break;
				}
			}
			if (!isEixst) {
				messageInfos.add(info);
			}
		}
		mAdapter.notifyDataSetInvalidated();
		if (messageInfos != null && messageInfos.size() != 0) {
			mListView.setSelection(messageInfos.size() - 1);
		}

		// clearNotification();
		insertMessage(info);
		Session session = new Session();
		session.setFromId(fCustomerVo.uid);
		session.name = getFromName();
		session.heading = fCustomerVo.headsmall;
		session.type = mType;
		session.lastMessageTime = info.time;
		insertSession(session);

		mContext.sendBroadcast(new Intent(ChatFragment.ACTION_REFRESH_SESSION));
	}

	@Override
	protected void onPause() {
		super.onPause();
		mVoiceSendBtn.setBackgroundResource(R.drawable.login_btn_n);
		if (!mReaderImpl.mIsStop) {
			mReaderImpl.cancelDg();
		} else {
			mReaderImpl.mIsStop = false;
		}

	}

	/*
	 * 页面销毁 (non-Javadoc)
	 * 
	 * @see com.weiyuan.BaseActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		WeiYuanCommon.saveCamerUrl(mContext, "");

		// updateSessionList();
		if (mIsRegisterReceiver) {
			unregisterReceiver();
		}

		if (mReaderImpl != null) {
			mReaderImpl.unregisterRecordReceiver();
		}
		/*
		 * Intent intent = new Intent(MessageTab.ACTION_RESET_SESSION_COUNT); intent.putExtra("fromid", fCustomerVo.uid); sendBroadcast(intent);
		 */
		mContext.sendBroadcast(new Intent(ChatFragment.ACTION_REFRESH_SESSION));
		mContext.sendBroadcast(new Intent(GlobalParam.ACTION_UPDATE_SESSION_COUNT));
		playListener.stop();

		/* ++释放图片++ */
		if (messageInfos != null) {
			for (int i = 0; i < messageInfos.size(); i++) {

				if (!TextUtils.isEmpty(messageInfos.get(i).fromurl)) {
					ImageView headerView = (ImageView) mListView.findViewWithTag(messageInfos.get(i).fromurl);
					if (headerView != null) {
						headerView.setImageBitmap(null);
						headerView.setImageResource(R.drawable.contact_default_header);
					}
				}

				if (messageInfos.get(i).typefile == MessageType.PICTURE) {
					ImageView imageView = (ImageView) mListView.findViewWithTag(messageInfos.get(i).content);
					if (imageView != null) {
						imageView.setImageBitmap(null);
						imageView.setVisibility(View.GONE);
					}

					if (!messageInfos.get(i).imgUrlS.startsWith("http://") && messageInfos.get(i).sendState != 1) {
						mBitmapCache.remove(messageInfos.get(i).imgUrlS);
					}
				}
			}
		}

		Set<String> keys = mBitmapCache.keySet();
		if (keys != null && !keys.isEmpty()) {
			for (String key : keys) {
				deleteImgFile(key);
			}
		}

		FeatureFunction.freeBitmap(mImageLoader.getImageBuffer());
		freeBitmap(mBitmapCache);
		System.gc();
		/*--释放图片--*/
	}

	/*
	 * 清空图片缓存
	 */
	private void freeBitmap(HashMap<String, SoftReference<Bitmap>> cache) {
		if (cache == null || cache.isEmpty()) {
			return;
		}
		for (SoftReference<Bitmap> bitmap : cache.values()) {
			if (bitmap.get() != null && !bitmap.get().isRecycled()) {
				bitmap.get().recycle();
				bitmap = null;

			}
		}
		cache.clear();
	}

	/*
	 * 注册通知
	 */
	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(SnsService.ACTION_CONNECT_CHANGE);
		filter.addAction(PushChatMessage.ACTION_SEND_STATE);
		filter.addAction(NotifyChatMessage.ACTION_NOTIFY_CHAT_MESSAGE);
		filter.addAction(NotifyChatMessage.ACTION_CHANGE_VOICE_CONTENT);
		filter.addAction(DESTORY_ACTION);
		filter.addAction(REFRESH_ADAPTER);
		filter.addAction(ACTION_READ_VOICE_STATE);
		filter.addAction(ACTION_CHANGE_FRIEND);
		filter.addAction(ACTION_RECORD_AUTH);
		filter.addAction(ACTION_DESTROY_ROOM);
		filter.addAction(GlobalParam.BE_KICKED_ACTION);
		filter.addAction(ACTION_RECOMMEND_CARD);
		filter.addAction(ACTION_SHOW_NICKNAME);
		filter.addAction(GlobalParam.ACTION_RESET_GROUP_NAME);
		registerReceiver(chatReceiver, filter);
		mIsRegisterReceiver = true;
	}

	/*
	 * 销毁通知
	 */
	private void unregisterReceiver() {
		unregisterReceiver(chatReceiver);
	}

	/**
	 * 添加表情滑动控件
	 * 
	 * @param i
	 *            添加的位置
	 */
	private void addView(final int i) {
		View view = LayoutInflater.from(mContext).inflate(R.layout.emotion_gridview, null);
		GridView gridView = (GridView) view.findViewById(R.id.emoji_grid);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position < mTotalEmotionList.get(i).size() - 1) {
					ImageView imageView = (ImageView) view.findViewById(R.id.emotion);
					if (imageView != null) {
						Drawable drawable = imageView.getDrawable();
						if (drawable instanceof BitmapDrawable) {
							Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
							String name = mTotalEmotionList.get(i).get(position);

							Drawable mDrawable = new BitmapDrawable(getResources(), bitmap);
							int width = getResources().getDimensionPixelSize(R.dimen.pl_emoji);
							int height = width;
							mDrawable.setBounds(0, 0, width > 0 ? width : 0, height > 0 ? height : 0);
							ImageSpan span = new ImageSpan(mDrawable);

							SpannableString spannableString = new SpannableString("[" + name + "]");
							// 类似于集合中的(start, end)，不包括起始值也不包括结束值。
							// 同理，Spannable.SPAN_INCLUSIVE_EXCLUSIVE类似于 [start，end)
							spannableString.setSpan(span, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
							Editable dEditable = mContentEdit.getEditableText();
							int index = mContentEdit.getSelectionStart();
							dEditable.insert(index, spannableString);
						}
					}
				} else {
					int index = mContentEdit.getSelectionStart();

					String text = mContentEdit.getText().toString();
					if (index > 0) {
						String text2 = text.substring(index - 1);
						if ("]".equals(text2)) {
							int start = text.lastIndexOf("[");
							int end = index;
							mContentEdit.getText().delete(start, end);
							return;
						}
						mContentEdit.getText().delete(index - 1, index);
					}
				}
			}

		});
		gridView.setAdapter(new EmojiAdapter(mContext, mTotalEmotionList.get(i), WeiYuanCommon.mScreenWidth));
		mViewList.add(view);
	}

	/**
	 * 显示表情处于第几页标志
	 * 
	 * @param size
	 */
	private void showCircle(int size) {
		mLayoutCircle.removeAllViews();

		for (int i = 0; i < size; i++) {
			ImageView img = new ImageView(mContext);
			img.setLayoutParams(new LinearLayout.LayoutParams(FeatureFunction.dip2px(mContext, 5), FeatureFunction.dip2px(mContext, 5)));
			LinearLayout layout = new LinearLayout(mContext);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			int margin = FeatureFunction.dip2px(mContext, 5);
			params.setMargins(margin, 0, margin, 0);
			layout.setLayoutParams(params);
			layout.addView(img);
			// img.setLayoutParams()
			if (mPageIndxe == i) {
				img.setImageResource(R.drawable.circle_d);
			} else {
				img.setImageResource(R.drawable.circle_n);
			}
			mLayoutCircle.addView(layout);
		}
	}

	/**
	 * 获取表情列表
	 * 
	 * @return
	 * @Mr.Sam <br />
	 *         创建时间:2013-6-21<br />
	 *         修改时间:<br />
	 */
	private List<List<String>> getEmojiList() {
		List<String> emojiList = new ArrayList<String>();
		String baseName = "emoji_";
		for (int i = 85; i <= 88; i++) {
			emojiList.add(baseName + i);
		}

		for (int i = 340; i <= 363; i++) {
			emojiList.add(baseName + i);
		}

		for (int i = 94; i <= 101; i++) {
			emojiList.add(baseName + i);
		}

		for (int i = 115; i <= 117; i++) {
			emojiList.add(baseName + i);
		}

		for (int i = 364; i <= 373; i++) {
			emojiList.add(baseName + i);
		}

		for (int i = 12; i <= 17; i++) {
			emojiList.add(baseName + i);
		}

		for (int i = 0; i <= 11; i++) {
			emojiList.add(baseName + i);
		}

		for (int i = 18; i <= 84; i++) {
			emojiList.add(baseName + i);
		}

		for (int i = 89; i <= 93; i++) {
			emojiList.add(baseName + i);
		}

		for (int i = 101; i <= 114; i++) {
			emojiList.add(baseName + i);
		}

		for (int i = 114; i <= 339; i++) {
			emojiList.add(baseName + i);
		}

		List<List<String>> totalList = new ArrayList<List<String>>();
		int page = emojiList.size() % 20 == 0 ? emojiList.size() / 20 : emojiList.size() / 20 + 1;
		for (int i = 0; i < page; i++) {
			int startIndex = i * 20;
			List<String> singleList = new ArrayList<String>();
			if (singleList != null) {
				singleList.clear();
			}
			int endIndex = 0;
			if (i < page - 1) {
				endIndex = startIndex + 20;
			} else if (i == page - 1) {
				endIndex = emojiList.size() - 1;
			}

			singleList.addAll(emojiList.subList(startIndex, endIndex));
			singleList.add("delete_emotion_btn");
			totalList.add(singleList);

		}

		return totalList;
	}

	/*
	 * 销毁通知栏的通知列表
	 */
	void clearNotification() {
		// getNotificationManager().cancel(String.valueOf(fCustomerVo.uid).hashCode());
		NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		/*
		 * if(notificationManager != null && fCustomerVo != null && fCustomerVo.uid!=null && !fCustomerVo.uid.equals("")){ notificationManager.cancel(fCustomerVo.uid.hashCode()); }
		 */

		notificationManager.cancel(0);

	}

	/**
	 * 语音按钮触发
	 */
	class OnVoice implements OnTouchListener {
		// 说话键按下和弹起处理事件
		@Override
		public boolean onTouch(View view, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mVoiceSendBtn.setBackgroundResource(R.drawable.login_btn_d);
				if (!FeatureFunction.checkSDCard()) {
					break;
				}
				mReaderImpl.showDg();
				break;
			case MotionEvent.ACTION_UP:
				mVoiceSendBtn.setBackgroundResource(R.drawable.login_btn_n);
				if (!mReaderImpl.mIsStop) {
					mReaderImpl.cancelDg();
				} else {
					mReaderImpl.mIsStop = false;
				}
				break;
			}
			return true;
		}

	}

	/*
	 * public FinalBitmap getPhotoBitmap() { return FinalFactory.createFinalAlbumBitmap(getBaseContext()); }
	 */

	/*
	 * 消息适配器
	 */
	class MyAdapter extends BaseAdapter {
		private final LayoutInflater mInflater;
		HashMap<String, View> hashMap;
		private boolean mIsShowNickName;

		public MyAdapter() {
			mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			hashMap = new HashMap<String, View>();
			if (mType == GlobleType.GROUP_CHAT) {// 单聊模式
				SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
				RoomTable roomTable = new RoomTable(db);
				Room room = roomTable.query(fCustomerVo.uid);
				if (room != null) {
					mIsShowNickName = room.isShowNickname == 1 ? true : false;
				}
			}

		}

		@Override
		public int getCount() {
			return messageInfos.size();
		}

		@Override
		public MessageInfo getItem(int arg0) {
			return messageInfos.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public void setIsShowNickName(boolean isShow) {
			this.mIsShowNickName = isShow;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			int type = 0;
			final MessageInfo messageInfo = messageInfos.get(position);
			if (messageInfo.getFromId().equals(mLogin.uid)) {
				type = 0;
			} else {
				type = 1;
			}

			ViewHolder viewHolder = null;
			convertView = hashMap.get(messageInfo.tag);

			if (convertView == null) {
				if (1 == type) {
					convertView = mInflater.inflate(R.layout.chat_talk_left, null);
				} else {
					convertView = mInflater.inflate(R.layout.chat_talk_right, null);
				}
				viewHolder = ViewHolder.getInstance(convertView);
				convertView.setTag(viewHolder);
				hashMap.put(messageInfo.tag, convertView);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.imgMsgPhoto.setImageBitmap(null);
			viewHolder.imgMsgPhoto.setImageResource(R.drawable.normal);
			if (messageInfo.typefile == MessageType.VOICE && 1 == messageInfo.getSendState()) {
				// 声音
				setOnLongClick(viewHolder.mRootLayout, position, 0, null, messageInfo);
			} else {
				if (1 == messageInfo.getSendState()) {
					// 文本
					setOnLongClick(viewHolder.txtMsg, position, 1, messageInfo.getContent(), messageInfo);
					// 图片
					setOnLongClick(viewHolder.imgMsgPhoto, position, 0, null, messageInfo);
					// 地图
					setOnLongClick(viewHolder.mapLayout, position, 0, null, messageInfo);
					setOnLongClick(viewHolder.cardLayout, position, 0, null, messageInfo);
				}

			}

			/*
			 * viewHolder.mCommentBtn.setOnClickListener(new OnClickListener() {
			 * 
			 * @Override public void onClick(View v) { mListView.setSelection(position); showMenuDialog(messageInfo); } });
			 */

			if (position > 0) {
				bindView(viewHolder, messageInfo, messageInfos.get(position - 1).time);
			} else {
				bindView(viewHolder, messageInfo, 0);
			}

			return convertView;
		}

		private void setOnLongClick(View v, final int position, final int type, final String content, final MessageInfo msg) {
			v.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					// showPromptDialog(position);
					showPromptDialog(position, type, content, msg);
					return true;
				}
			});
		}

		private void bindView(ViewHolder viewHolder, final MessageInfo messageInfo, final long lasttime) {
			final int type = messageInfo.getFromId().equals(mLogin.uid) ? 0 : 1;
			if (0 == type) {
				viewHolder.nickName.setVisibility(View.GONE);
				if (0 == messageInfo.getSendState()) {
					// viewHolder.imgSendState.setVisibility(View.VISIBLE);
				} else {
					viewHolder.imgSendState.setVisibility(View.GONE);
				}

				viewHolder.imgSendState.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						showResendDialog(messageInfo);
					}
				});
			} else {
				viewHolder.imgVoiceReadState.setVisibility(View.GONE);
				if (mIsShowNickName) {
					viewHolder.nickName.setVisibility(View.VISIBLE);
					viewHolder.nickName.setText(messageInfo.fromname);
				} else {
					viewHolder.nickName.setVisibility(View.GONE);
				}

			}

			viewHolder.imgHead.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, UserInfoActivity.class);
					intent.putExtra("uid", messageInfo.fromid);
					if (type == 0) {
						intent.putExtra("isLogin", 1);
					}
					intent.putExtra("type", 2);
					mContext.startActivity(intent);
				}
			});

			if (!TextUtils.isEmpty(messageInfo.fromurl)) {
				viewHolder.imgHead.setTag(messageInfo.fromurl);
				// viewHolder.imgHead.setOnClickListener(showInfo);
				mImageLoader.getBitmap(mContext, viewHolder.imgHead, null, messageInfo.fromurl, 0, false, false, false);
			}

			// viewHolder.txtTime.setVisibility(View.GONE);
			String time = FeatureFunction.calculaterReleasedTime(mContext, new Date(messageInfo.time), messageInfo.time, lasttime);
			if (time == null || time.equals("")) {
				viewHolder.txtTime.setVisibility(View.GONE);
			} else {
				viewHolder.txtTime.setVisibility(View.VISIBLE);
				viewHolder.txtTime.setText(time);
			}

			if (messageInfo.typefile == MessageType.VOICE) {
				int length = messageInfo.getVoiceTime();
				float max = mContext.getResources().getDimension(R.dimen.voice_max_length);
				float min = mContext.getResources().getDimension(R.dimen.voice_min_length);
				int width = (int) min;
				if (length >= MIN_SECOND && length <= MAX_SECOND) {
					width += (length - MIN_SECOND) * (int) ((max - min) / (MAX_SECOND - MIN_SECOND));
				} else if (length > MAX_SECOND) {
					width = (int) max;
				}

				viewHolder.txtVoiceNum.getLayoutParams();
				// timeParams.addRule(RelativeLayout.CENTER_VERTICAL);
				RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(width, FeatureFunction.dip2px(mContext, 48));
				if (type == 0) {
					// timeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
					// timeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, viewHolder.mRootLayout.getId());
					param.addRule(RelativeLayout.LEFT_OF, viewHolder.imgHead.getId());
					param.setMargins(0, FeatureFunction.dip2px(mContext, 5), FeatureFunction.dip2px(mContext, 5), FeatureFunction.dip2px(mContext, 5));
				} else {
					// timeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
					// timeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, viewHolder.mRootLayout.getId());
					param.addRule(RelativeLayout.RIGHT_OF, viewHolder.imgHead.getId());
					if (mIsShowNickName) {
						param.setMargins(FeatureFunction.dip2px(mContext, 5), FeatureFunction.dip2px(mContext, 25), 0, FeatureFunction.dip2px(mContext, 5));
					} else {
						param.setMargins(FeatureFunction.dip2px(mContext, 5), FeatureFunction.dip2px(mContext, 5), 0, FeatureFunction.dip2px(mContext, 5));
					}

				}
				param.addRule(RelativeLayout.BELOW, viewHolder.txtTime.getId());
				// viewHolder.txtVoiceNum.setLayoutParams(timeParams);
				viewHolder.mRootLayout.setLayoutParams(param);

			} else {
				int padding = FeatureFunction.dip2px(mContext, 5);
				if (type == 0) {
					viewHolder.mParams.setMargins(0, padding, FeatureFunction.dip2px(mContext, 5), FeatureFunction.dip2px(mContext, 5));
				} else {
					viewHolder.mParams.setMargins(FeatureFunction.dip2px(mContext, 5), padding, 0, FeatureFunction.dip2px(mContext, 5));
				}
				viewHolder.mRootLayout.setLayoutParams(viewHolder.mParams);
			}

			switch (messageInfo.typefile) {
			case MessageType.TEXT:
				viewHolder.videoIcon.setVisibility(View.GONE);
				viewHolder.mapLayout.setVisibility(View.GONE);
				viewHolder.cardLayout.setVisibility(View.GONE);
				viewHolder.txtMsgMap.setVisibility(View.GONE);
				viewHolder.txtVoiceNum.setVisibility(View.GONE);
				viewHolder.gifView.setVisibility(View.GONE);
				if (viewHolder.wiatProgressBar != null) {
					viewHolder.wiatProgressBar.setVisibility(View.GONE);
				}
				viewHolder.imgMsgVoice.setVisibility(View.GONE);
				viewHolder.imgMsgPhoto.setVisibility(View.GONE);
				viewHolder.txtMsg.setVisibility(View.VISIBLE);
				viewHolder.txtMsg.setText(EmojiUtil.getExpressionString(getBaseContext(), messageInfo.getContent(), EMOJIREX));

				break;
			case MessageType.PICTURE:
				viewHolder.videoIcon.setVisibility(View.GONE);
				viewHolder.mapLayout.setVisibility(View.GONE);
				viewHolder.cardLayout.setVisibility(View.GONE);
				viewHolder.txtMsgMap.setVisibility(View.GONE);
				viewHolder.gifView.setVisibility(View.GONE);
				String urlpic = messageInfo.imgUrlS;
				if (urlpic == null || urlpic.equals("")) {
					urlpic = messageInfo.imgUrlL;
				}
				final String path = urlpic;

				viewHolder.txtVoiceNum.setVisibility(View.GONE);
				viewHolder.imgMsgPhoto.setTag(path);
				if (path.startsWith("http://") && 1 == messageInfo.getSendState()) {
					if (viewHolder.wiatProgressBar != null) {
						viewHolder.wiatProgressBar.setVisibility(View.VISIBLE);
					}
					viewHolder.imgMsgVoice.setVisibility(View.GONE);
					viewHolder.txtMsg.setVisibility(View.GONE);

					viewHolder.imgMsgPhoto.setTag(path);
					if (!mImageLoader.getImageBuffer().containsKey(path)) {
						viewHolder.imgMsgPhoto.setImageBitmap(null);
						viewHolder.imgMsgPhoto.setImageResource(R.drawable.normal);
						mImageLoader.getBitmap(mContext, viewHolder.imgMsgPhoto, viewHolder.wiatProgressBar, path, 0, false, false, false);
					} else {
						viewHolder.wiatProgressBar.setVisibility(View.GONE);
						viewHolder.imgMsgPhoto.setImageBitmap(mImageLoader.getImageBuffer().get(path));
					}

					viewHolder.imgMsgPhoto.setVisibility(View.VISIBLE);
					viewHolder.imgMsgPhoto.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mContext, ShowImageActivity.class);
							intent.putExtra("imageurl", messageInfo.imgUrlL);
							intent.putExtra("message", messageInfo);
							String ownerId = "";
							String groupId = "";

							if (messageInfo.getFromId().equals(mLogin.uid)) {
								if (messageInfo.typechat == 300) {
									groupId = messageInfo.fromid;
								} else if (messageInfo.typechat == 100) {
									ownerId = messageInfo.fromid;
								}
							} else {
								if (messageInfo.typechat == 300) {
									groupId = messageInfo.toid;
								} else if (messageInfo.typechat == 100) {
									ownerId = messageInfo.toid;
								}

							}
							intent.putExtra("fuid", ownerId);
							intent.putExtra("groupid", groupId);
							mContext.startActivity(intent);
						}
					});

				} else {
					Bitmap bitmap = null;
					if (!mBitmapCache.containsKey(path)) {
						bitmap = BitmapFactory.decodeFile(path);
						mBitmapCache.put(path, new SoftReference<Bitmap>(bitmap));
					} else {
						bitmap = mBitmapCache.get(path).get();
					}
					if (bitmap != null && !bitmap.isRecycled()) {
						viewHolder.imgMsgPhoto.setImageBitmap(bitmap);
					}/*
					 * else{ bitmap = BitmapFactory.decodeFile(path); viewHolder.imgMsgPhoto.setImageBitmap(bitmap); }
					 */
					viewHolder.imgMsgPhoto.setVisibility(View.VISIBLE);
					if (viewHolder.wiatProgressBar != null) {
						viewHolder.imgMsgVoice.setVisibility(View.GONE);
						// viewHolder.imgMsgPhoto.setVisibility(View.GONE);
						viewHolder.txtMsg.setVisibility(View.GONE);

						if (2 == messageInfo.getSendState()) {
							viewHolder.wiatProgressBar.setVisibility(View.VISIBLE);
						} else {
							viewHolder.wiatProgressBar.setVisibility(View.GONE);
						}
					}
				}
				break;
			case MessageType.VOICE:
				viewHolder.videoIcon.setVisibility(View.GONE);
				viewHolder.mapLayout.setVisibility(View.GONE);
				viewHolder.cardLayout.setVisibility(View.GONE);
				viewHolder.gifView.setVisibility(View.GONE);
				if (2 == messageInfo.getSendState()) {
					if (viewHolder.wiatProgressBar != null) {
						viewHolder.imgMsgVoice.setVisibility(View.GONE);
						viewHolder.imgMsgPhoto.setVisibility(View.GONE);
						viewHolder.txtMsg.setVisibility(View.GONE);
						viewHolder.wiatProgressBar.setVisibility(View.VISIBLE);
					}
				} else {
					if (viewHolder.wiatProgressBar != null) {
						viewHolder.wiatProgressBar.setVisibility(View.GONE);
					}
					/*
					 * if(4 == messageInfo.getSendState()){ downVoice(messageInfo); }
					 */
					viewHolder.imgMsgPhoto.setVisibility(View.GONE);
					viewHolder.imgMsgVoice.setVisibility(View.VISIBLE);
					viewHolder.txtMsg.setVisibility(View.GONE);
					viewHolder.mRootLayout.setTag(messageInfo);
					viewHolder.mRootLayout.setOnClickListener(playListener);
					viewHolder.txtVoiceNum.setVisibility(View.VISIBLE);
					viewHolder.txtVoiceNum.setText(messageInfo.getVoiceTime() + "''");
					// viewHolder.imgMsgVoice.setLayoutParams(new Rela)
					try {
						AnimationDrawable drawable = (AnimationDrawable) viewHolder.imgMsgVoice.getDrawable();
						if (playListener.getMessageTag().equals(messageInfo.tag)) {
							drawable.start();
						} else {
							drawable.stop();
							drawable.selectDrawable(0);
						}

					} catch (Exception e) {

					}
				}
				break;

			case MessageType.MAP:
				viewHolder.videoIcon.setVisibility(View.GONE);
				viewHolder.gifView.setVisibility(View.GONE);
				viewHolder.mapLayout.setVisibility(View.VISIBLE);
				viewHolder.cardLayout.setVisibility(View.GONE);
				viewHolder.txtVoiceNum.setVisibility(View.GONE);
				if (viewHolder.wiatProgressBar != null) {
					viewHolder.wiatProgressBar.setVisibility(View.GONE);
				}
				viewHolder.imgMsgVoice.setVisibility(View.GONE);
				viewHolder.imgMsgPhoto.setVisibility(View.GONE);
				viewHolder.txtMsg.setVisibility(View.GONE);
				try {
					if (!TextUtils.isEmpty(messageInfo.mAddress)) {
						viewHolder.txtMsgMap.setText(messageInfo.mAddress);
					}

					// http://api.map.baidu.com/staticimage?center=103.978683,30.739114&width=300&height=200&zoom=16&&markerStyles=s
					// http://api.map.baidu.com/staticimage?center=103.978683,30.739114&width=200&height=120&zoom=16&markers=103.978683,30.739114&markerStyles=s
					// 显示地图所列图示例
					/*
					 * String ImageURL = "http://api.map.baidu.com/staticimage?center="+messageInfo.mLng+ ","+messageInfo.mLat+"&width=300&height=200&zoom=16";
					 */
					String ImageURL = "http://api.map.baidu.com/staticimage?center=" + messageInfo.mLng + "," + messageInfo.mLat + "&width=200&height=140&zoom=16&markers=" + messageInfo.mLng + ","
							+ messageInfo.mLat + "&markerStyles=s";
					mImageLoader.getBitmap(mContext, viewHolder.mapIcon, null, ImageURL, 0, false, true, false);

					viewHolder.mapLayout.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mContext, LocationActivity.class);
							intent.putExtra("show", true);
							intent.putExtra("lat", messageInfo.mLat);
							intent.putExtra("lng", messageInfo.mLng);
							intent.putExtra("addr", messageInfo.mAddress);
							String ownerId = "";
							String groupId = "";

							if (messageInfo.getFromId().equals(mLogin.uid)) {
								if (messageInfo.typechat == 300) {
									groupId = messageInfo.fromid;
								} else if (messageInfo.typechat == 100) {
									ownerId = messageInfo.fromid;
								}
							} else {
								if (messageInfo.typechat == 300) {
									groupId = messageInfo.toid;
								} else if (messageInfo.typechat == 100) {
									ownerId = messageInfo.toid;
								}

							}
							intent.putExtra("fuid", ownerId);
							intent.putExtra("groupid", groupId);
							mContext.startActivity(intent);
						}
					});
				} catch (Exception e) {
				}

				viewHolder.txtMsgMap.setTag(messageInfo);
				break;
			case MessageType.CARD:
				viewHolder.videoIcon.setVisibility(View.GONE);
				viewHolder.gifView.setVisibility(View.GONE);
				viewHolder.mapLayout.setVisibility(View.GONE);
				viewHolder.txtVoiceNum.setVisibility(View.GONE);
				if (viewHolder.wiatProgressBar != null) {
					viewHolder.wiatProgressBar.setVisibility(View.GONE);
				}
				viewHolder.imgMsgVoice.setVisibility(View.GONE);
				viewHolder.imgMsgPhoto.setVisibility(View.GONE);
				viewHolder.txtMsg.setVisibility(View.GONE);
				viewHolder.txtMsgMap.setVisibility(View.GONE);
				viewHolder.cardLayout.setVisibility(View.VISIBLE);
				final Card card = Card.getInfo(messageInfo.getContent());
				if (card != null) {
					final String userId = card.uid;
					if (card.headsmall != null && !card.headsmall.equals("")) {
						mImageLoader.getBitmap(mContext, viewHolder.cardHeader, null, card.headsmall, 0, false, false, false);
					}
					viewHolder.cardLayout.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (userId != null && !userId.equals("")) {
								/*
								 * SQLiteDatabase dbDatabase = DBHelper.getInstance(mContext).getReadableDatabase(); UserTable userTable = new UserTable(dbDatabase); Login user = userTable.query(userId);
								 */

								Intent intent = new Intent(mContext, UserInfoActivity.class);
								intent.putExtra("uid", card.uid);
								intent.putExtra("type", 2);
								startActivity(intent);
							}

						}
					});

					viewHolder.cardName.setText(card.nickname);
					viewHolder.cardEM.setText(card.content);
				}
				break;
			case MessageType.SMALL_VIDEO:// 小视频
				viewHolder.videoIcon.setVisibility(View.GONE);
				viewHolder.mapLayout.setVisibility(View.GONE);
				viewHolder.txtVoiceNum.setVisibility(View.GONE);
				if (viewHolder.wiatProgressBar != null) {
					viewHolder.wiatProgressBar.setVisibility(View.GONE);
				}
				viewHolder.imgMsgVoice.setVisibility(View.GONE);
				viewHolder.imgMsgPhoto.setVisibility(View.GONE);
				viewHolder.txtMsg.setVisibility(View.VISIBLE);
				viewHolder.txtMsgMap.setVisibility(View.GONE);
				viewHolder.cardLayout.setVisibility(View.GONE);
				viewHolder.gifView.setVisibility(View.VISIBLE);

				String gifPic = messageInfo.imgUrlS;
				if (gifPic == null || gifPic.equals("")) {// 显示视频图标
					final String videoPath = messageInfo.videoUrl;
					viewHolder.gifView.setTag(videoPath);

					if (videoPath.startsWith("http://") && 1 == messageInfo.getSendState()) {

						if (viewHolder.wiatProgressBar != null) {
							viewHolder.wiatProgressBar.setVisibility(View.GONE);
						}
						viewHolder.videoIcon.setVisibility(View.VISIBLE);
						if (!mImageLoader.getImageBuffer().containsKey(videoPath)) {
							viewHolder.imgMsgPhoto.setImageBitmap(null);
							viewHolder.imgMsgPhoto.setImageResource(R.drawable.normal);
							mImageLoader.getBitmap(mContext, viewHolder.gifView, viewHolder.wiatProgressBar, videoPath, 0, false, false, true);
						} else {
							viewHolder.wiatProgressBar.setVisibility(View.GONE);
							viewHolder.gifView.setImageBitmap(mImageLoader.getImageBuffer().get(videoPath));
						}
					} else {
						Bitmap bitmap = null;
						bitmap = ThumbnailUtils.createVideoThumbnail(messageInfo.videoUrl, Thumbnails.FULL_SCREEN_KIND);
						if (bitmap != null) {
							bitmap = Bitmap.createScaledBitmap(bitmap, FeatureFunction.dip2px(mContext, 120), FeatureFunction.dip2px(mContext, 120), false);
						}
						/*
						 * if(!mBitmapCache.containsKey(videoPath)){ bitmap = ThumbnailUtils.createVideoThumbnail(messageInfo.videoUrl, Thumbnails.FULL_SCREEN_KIND); if (bitmap != null) { bitmap = Bitmap.createScaledBitmap(bitmap, FeatureFunction.dip2px(mContext, 120),FeatureFunction.dip2px(mContext, 120), false); } mBitmapCache.put(videoPath, new SoftReference<Bitmap>(bitmap)); }else { bitmap =
						 * mBitmapCache.get(videoPath).get(); }
						 */
						if (bitmap != null && !bitmap.isRecycled()) {
							viewHolder.gifView.setImageBitmap(bitmap);
						}/*
						 * else{ bitmap = BitmapFactory.decodeFile(path); viewHolder.imgMsgPhoto.setImageBitmap(bitmap); }
						 */

						viewHolder.videoIcon.setVisibility(View.VISIBLE);
						if (viewHolder.wiatProgressBar != null) {
							if (2 == messageInfo.getSendState()) {
								viewHolder.wiatProgressBar.setVisibility(View.VISIBLE);
							} else {
								viewHolder.wiatProgressBar.setVisibility(View.GONE);
							}
						}
					}
				} else {
					final String gifPath = gifPic;
					viewHolder.gifView.setTag(gifPath);
					if (gifPath.startsWith("http://") && 1 == messageInfo.getSendState()) {

						if (viewHolder.wiatProgressBar != null) {
							viewHolder.wiatProgressBar.setVisibility(View.VISIBLE);
						}
						if (!mImageLoader.getImageBuffer().containsKey(gifPath)) {
							// viewHolder.gifView.setImageResource(R.drawable.normal);
							mImageLoader.getNetGif(mContext, viewHolder.gifView, viewHolder.wiatProgressBar, gifPath);
							// mImageLoader.getBitmap(mContext, viewHolder.gifView, viewHolder.wiatProgressBar, gifPath, 0, false, false);
						} else {// 获取缓存中的gif图片
							viewHolder.wiatProgressBar.setVisibility(View.GONE);
							viewHolder.gifView.setImageDrawable(null);
						}
					} else {
						try {
							GifDrawable gifFromPath = new GifDrawable(gifPath);
							viewHolder.gifView.setImageDrawable(gifFromPath);
						} catch (IOException e) {
							e.printStackTrace();
						}
						if (viewHolder.wiatProgressBar != null) {

							if (2 == messageInfo.getSendState()) {
								viewHolder.wiatProgressBar.setVisibility(View.VISIBLE);
							} else {
								viewHolder.wiatProgressBar.setVisibility(View.GONE);
							}
						}
					}
				}

				viewHolder.gifView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setDataAndType(Uri.parse(messageInfo.videoUrl), "video/mp4");
						startActivity(intent);

						/*
						 * Intent intent = new Intent(mContext, PlayVideoActivity.class); intent.putExtra("path",messageInfo.videoUrl); mContext.startActivity(intent);
						 */
					}
				});

				break;
			default:
				break;
			}
		}

		private void showResendDialog(final MessageInfo messageInfo) {

			MMAlert.showAlert(mContext, "", mContext.getResources().getStringArray(R.array.resend_item), null, new OnAlertSelectId() {

				@Override
				public void onClick(int whichButton) {
					switch (whichButton) {
					case 0:
						messageInfo.sendState = 2;
						MyAdapter.this.notifyDataSetChanged();
						btnResendAction(messageInfo);
						break;
					default:
						break;
					}
				}
			});

		}

	}

	static class ViewHolder {
		int flag = 0; // 1 好友 0 自己
		TextView txtTime, txtMsg, txtVoiceNum, mShideView;
		ImageView imgHead, imgMsgPhoto, imgMsgVoice, imgSendState, imgVoiceReadState;
		ProgressBar wiatProgressBar;
		RelativeLayout mRootLayout, mDisplayLayout;
		private RelativeLayout.LayoutParams mParams;
		// gif 图片
		GifImageView gifView;
		ImageView videoIcon;

		// 名片
		LinearLayout cardLayout;
		ImageView cardHeader;
		TextView cardName, cardEM;

		// 地图
		RelativeLayout mapLayout;
		TextView txtMsgMap;
		ImageView mapIcon;

		// 昵称
		TextView nickName;

		public static ViewHolder getInstance(View view) {
			ViewHolder holder = new ViewHolder();
			holder.mRootLayout = (RelativeLayout) view.findViewById(R.id.chat_talk_msg_info);
			holder.mParams = (RelativeLayout.LayoutParams) holder.mRootLayout.getLayoutParams();
			holder.txtTime = (TextView) view.findViewById(R.id.chat_talk_txt_time);
			holder.txtMsg = (TextView) view.findViewById(R.id.chat_talk_msg_info_text);

			holder.imgHead = (ImageView) view.findViewById(R.id.chat_talk_img_head);
			holder.imgMsgPhoto = (ImageView) view.findViewById(R.id.chat_talk_msg_info_msg_photo);
			holder.imgMsgVoice = (ImageView) view.findViewById(R.id.chat_talk_msg_info_msg_voice);

			holder.imgSendState = (ImageView) view.findViewById(R.id.chat_talk_msg_sendsate);
			holder.wiatProgressBar = (ProgressBar) view.findViewById(R.id.chat_talk_msg_progressBar);
			holder.txtVoiceNum = (TextView) view.findViewById(R.id.chat_talk_voice_num);
			holder.imgVoiceReadState = (ImageView) view.findViewById(R.id.unread_voice_icon);

			// +++通讯录名片++//
			holder.cardLayout = (LinearLayout) view.findViewById(R.id.card_layout);
			holder.cardHeader = (ImageView) view.findViewById(R.id.card_header);
			holder.cardName = (TextView) view.findViewById(R.id.card_name);
			holder.cardEM = (TextView) view.findViewById(R.id.card_emal);
			// --通讯录名片--//

			// ++地图++//
			holder.mapLayout = (RelativeLayout) view.findViewById(R.id.map_layout);
			holder.txtMsgMap = (TextView) view.findViewById(R.id.chat_talk_msg_map);
			holder.mapIcon = (ImageView) view.findViewById(R.id.map_icon);
			// --地图--//

			// +++gif图片+++//
			holder.gifView = (GifImageView) view.findViewById(R.id.gifview);
			holder.videoIcon = (ImageView) view.findViewById(R.id.video_icon);
			// -----gif图片-----//

			holder.nickName = (TextView) view.findViewById(R.id.from_message_nickname);
			return holder;
		}
	}

	/************** 表情功能 *************/

	// 显示表情列表
	private void showEmojiGridView() {
		hideExpra();
		mToggleBtn.setChecked(false);
		togInfoSelect();
		mEmotionLayout.setVisibility(View.VISIBLE);
	}

	// 隐藏表情列表
	private void hideEmojiGridView() {
		hideExpra();

		mEmotionLayout.setVisibility(View.GONE);
	}

	private View.OnFocusChangeListener sendTextFocusChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			if (hasFocus) {
				// 文本框得到焦点，隐藏附加信息和表情列表
				hideEmojiGridView();
			}

		}
	};

	private View.OnClickListener sendTextClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// 获取到文本框的点击事件隐藏表情
			hideEmojiGridView();
		}
	};

	/** 聊天广播 */
	private BroadcastReceiver chatReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (SnsService.ACTION_CONNECT_CHANGE.equals(action)) {
				Log.d(TAG, "receiver:" + action);
				String type = intent.getExtras().getString(SnsService.EXTRAS_CHANGE);
				Log.d(TAG, "receiver:Exper" + type);
				if (XmppType.XMPP_STATE_AUTHENTICATION.equals(type)) {
					// 认证成功
					opconnectState = true;
				} else if (XmppType.XMPP_STATE_AUTHERR.equals(type)) {
					// 认证失败
					opconnectState = false;
					showToast(mContext.getString(R.string.login_user_auth_error));
				} else if (XmppType.XMPP_STATE_REAUTH.equals(type)) {
					// 未认证
					opconnectState = false;
				} else if (XmppType.XMPP_STATE_START.equals(type)) {
					// 开始登录
					opconnectState = false;
				} else if (XmppType.XMPP_STATE_STOP.equals(type)) {
					// 没开启登录
					opconnectState = false;
				}
			} else if (PushChatMessage.ACTION_SEND_STATE.equals(action)) {
				Log.d(TAG, "receiver:" + PushChatMessage.ACTION_SEND_STATE);
				MessageInfo messageInfo = (MessageInfo) intent.getSerializableExtra(PushChatMessage.EXTRAS_MESSAGE);
				updateMessage(messageInfo);
				changeSendState(messageInfo);

			} else if (NotifyChatMessage.ACTION_NOTIFY_CHAT_MESSAGE.equals(action)) {
				final MessageInfo msg = (MessageInfo) intent.getSerializableExtra(NotifyChatMessage.EXTRAS_NOTIFY_CHAT_MESSAGE);
				if ((msg.typechat == GlobleType.SINGLE_CHAT && msg.fromid.equals(fCustomerVo.uid))
						|| ((msg.typechat == GlobleType.GROUP_CHAT || msg.typechat == GlobleType.MEETING_CHAT) && msg.toid.equals(fCustomerVo.uid))) {
					msg.readState = 1;
					// changeReadState(msg);
					updateMessage(msg);
					mContext.sendBroadcast(new Intent(ChatFragment.ACTION_REFRESH_SESSION));
					mContext.sendBroadcast(new Intent(GlobalParam.ACTION_UPDATE_SESSION_COUNT));
					notifyMessage(msg);
				}
			} else if (action.equals(DESTORY_ACTION)) {
				/*
				 * if(intent.getIntExtra("type", 0) !=1){ Toast.makeText(mContext,"你已经被管理员移除了", Toast.LENGTH_LONG).show(); }
				 * 
				 * Log.e("ChatMainActivity", "DESTORY_ACTION");
				 */
				ChatMainActivity.this.finish();
			} else if (action.equals(REFRESH_ADAPTER)) {
				String id = intent.getStringExtra("id");
				if (!TextUtils.isEmpty(id)) {
					if (!fCustomerVo.uid.equals(id)) {
						return;
					}
				}
				SQLiteDatabase db = DBHelper.getInstance(mContext).getReadableDatabase();
				MessageTable messageTable = new MessageTable(db);
				messageInfos = messageTable.query(fCustomerVo.uid, -1, mType);

				if (messageInfos == null) {
					messageInfos = new ArrayList<MessageInfo>();
				}

				mAdapter.notifyDataSetChanged();
			} else if (action.equals(GlobalParam.BE_KICKED_ACTION)) {
				String id = intent.getStringExtra("id");
				String uid = intent.getStringExtra("uid");
				int type = intent.getIntExtra("type", 0);
				String hintMsg = intent.getStringExtra("hintMsg");
				if (type != 0) {
					if (id.equals(fCustomerVo.uid)) {
						destoryDialog(hintMsg);
					}
				} else {
					if (!TextUtils.isEmpty(id)) {
						if (!TextUtils.isEmpty(uid) && uid.equals(WeiYuanCommon.getUserId(mContext))) {
							if (id.equals(fCustomerVo.uid)) {
								destoryDialog(mContext.getString(R.string.you_have_been_removed_from_group));
							}
						}

					}
				}

			} else if (ACTION_READ_VOICE_STATE.equals(action)) {
				final MessageInfo messageInfo = (MessageInfo) intent.getSerializableExtra(PushChatMessage.EXTRAS_MESSAGE);
				updateMessage(messageInfo);
				changeVoiceState(messageInfo);

			} else if (action.equals(ACTION_RECORD_AUTH)) {
				Toast.makeText(mContext, mContext.getString(R.string.record_auth_control), Toast.LENGTH_LONG).show();
			} else if (action.equals(GlobalParam.ACTION_RESET_GROUP_NAME)) {
				String groupId = intent.getStringExtra("group_id");
				String groupName = intent.getStringExtra("group_name");
				if (groupId != null && !groupId.equals("")) {
					if (groupName != null && !groupName.equals("")) {
						if (fCustomerVo.uid.equals(groupId)) {
							titileTextView.setText(groupName);
						}
					}
				}

			} else if (action.equals(ACTION_RECOMMEND_CARD)) {
				MessageInfo msg = (MessageInfo) intent.getSerializableExtra("cardMsg");
				if (msg != null) {
					Log.e("send_card", "true++++++++");
					sendBroad2Save(msg, false);
				}
			} else if (action.equals(ACTION_SHOW_NICKNAME)) {
				boolean isShowNickName = intent.getBooleanExtra("is_show_nickname", false);
				if (mAdapter != null) {
					mAdapter.setIsShowNickName(isShowNickName);
					mAdapter.notifyDataSetChanged();
					// mAdapter.
				}
			}
		}
	};

	private void updateNewMessage(MessageInfo messageInfo) {
		SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
		MessageTable table = new MessageTable(db);
		table.updateMessage(messageInfo);
	}

	private void updateMessage(MessageInfo messageInfo) {
		SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
		MessageTable table = new MessageTable(db);
		table.update(messageInfo);
	}

	private void insertSession(Session session) {
		SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
		SessionTable table = new SessionTable(db);
		Session existSession = table.query(fCustomerVo.uid, session.type);
		if (existSession != null) {
			if (existSession.isTop != 0) {
				List<Session> exitsSesList = table.getTopSessionList();
				if (exitsSesList != null && exitsSesList.size() > 0) {
					for (int i = 0; i < exitsSesList.size(); i++) {
						Session ses = exitsSesList.get(i);
						if (ses.isTop > 1) {
							ses.isTop = ses.isTop - 1;
							table.update(ses, ses.type);
						}
					}
				}
				session.isTop = table.getTopSize();
			}
			table.update(session, session.type);
		} else {
			table.insert(session);
		}
	}

	private void insertMessage(MessageInfo messageInfo) {
		SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
		MessageTable table = new MessageTable(db);
		table.insert(messageInfo);
	}

	private void changeSendState(final MessageInfo messageInfo) {
		if (messageInfo != null) {
			handler.post(new Runnable() {

				@Override
				public void run() {
					modifyMessageState(messageInfo);
				}
			});
		}
	}

	private void notifyMessage(final MessageInfo msg) {
		if (msg == null) {
			return;
		}
		handler.post(new Runnable() {

			@Override
			public void run() {
				try {
					// 当该信息不来自好友就过滤掉!
					if (msg.getFromId().equals(WeiYuanCommon.getUserId(mContext))) {
						return;
					}
					messageInfos.add(msg);
					mAdapter.notifyDataSetInvalidated();
					if (messageInfos.size() == 1 || (mListView.getLastVisiblePosition() == messageInfos.size() - 2)) {
						mListView.setSelection(messageInfos.size() - 1);
					}
				} catch (Exception e) {

				}

			}
		});
	}

	private void changeVoiceState(final MessageInfo messageInfo) {
		if (messageInfo != null) {
			handler.post(new Runnable() {

				@Override
				public void run() {
					modifyMessageVoiceState(messageInfo);
				}
			});
		}
	}

	private void changeReadState(final MessageInfo messageInfo) {
		if (messageInfo != null) {
			handler.post(new Runnable() {

				@Override
				public void run() {
					modifyMessageReadState(messageInfo);
				}
			});
		}
	}

	private void modifyMessageReadState(MessageInfo messageInfo) {
		for (int i = 0; i < messageInfos.size(); i++) {
			if (messageInfo.tag.equals(messageInfos.get(i).tag)) {
				MessageInfo tempInfo = messageInfos.get(i);
				tempInfo.setReadState(messageInfo.readState);
				mAdapter.notifyDataSetInvalidated();
				break;
			}
		}

	}

	private void modifyMessageState(MessageInfo messageInfo) {

		for (int i = 0; i < messageInfos.size(); i++) {
			if (messageInfo.tag.equals(messageInfos.get(i).tag)) {
				MessageInfo tempInfo = messageInfos.get(i);
				tempInfo.setSendState(messageInfo.getSendState());
				tempInfo.id = messageInfo.id;
				tempInfo.imgUrlS = messageInfo.imgUrlS;
				tempInfo.imgUrlL = messageInfo.imgUrlL;
				tempInfo.imgWidth = messageInfo.imgWidth;
				tempInfo.imgHeight = messageInfo.imgHeight;
				tempInfo.voiceUrl = messageInfo.voiceUrl;
				tempInfo.readState = messageInfo.readState;
				tempInfo.time = messageInfo.time;
				mAdapter.notifyDataSetInvalidated();
				break;
			}
		}

	}

	private void modifyMessageVoiceState(MessageInfo messageInfo) {
		for (int i = 0; i < messageInfos.size(); i++) {
			if (messageInfo.tag.equals(messageInfos.get(i).tag)) {
				MessageInfo tempInfo = messageInfos.get(i);
				tempInfo.isReadVoice = messageInfo.isReadVoice;
				break;
			}
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// showLongDialog(mContext, arg2);
		return true;
	}

	protected void createDialog(Context context, String cardTitle, final int type, final String okTitle) {
		mPhoneDialog = new Dialog(context, R.style.dialog);
		LayoutInflater factor = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View serviceView = factor.inflate(R.layout.card_dialog, null);

		mPhoneDialog.setContentView(serviceView);
		mPhoneDialog.show();
		mPhoneDialog.setCancelable(false);
		mPhoneDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT
		/* mContext.getResources().getDimensionPixelSize(R.dimen.bind_phone_height) */
		, LayoutParams.WRAP_CONTENT);

		/*
		 * TextView signId=(TextView) serviceView .findViewById(R.id.sign_id); signId.setText(string[0]);
		 */
		final TextView phoneEdit = (TextView) serviceView.findViewById(R.id.card_title);

		phoneEdit.setText(cardTitle);

		Button okBtn = (Button) serviceView.findViewById(R.id.yes);
		okBtn.setText(okTitle);

		okBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mPhoneDialog != null) {
					mPhoneDialog.dismiss();
					mPhoneDialog = null;
				}

				WeiYuanCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG, "请求发送中,请稍后...");

				if (type == 1) {
					// addBlock();
				} else {
					// deleteFriend();
				}
				/*
				 * send(); Card card = new Card(mCardLogin.uid, mCardLogin.headsmall, mCardLogin.name, mCardLogin.auth); MessageInfo msg = new MessageInfo(); msg.setContent(Card.getInfo(card)); msg.setFromId(SiBaDaCommon.getUserId(mContext)); msg.setToId(fCustomerVo.uid); msg.isRoom = mIsRoom; msg.type = MessageType.SUBINFOCARD; msg.isMsgBurn = 0;//是否是阅后即焚消息 msg.isMsgImportant = 0;//是否重要消息
				 * msg.sendTime = System.currentTimeMillis(); msg.pullTime = System.currentTimeMillis(); sendBroad2Save(msg);
				 */

			}
		});

		Button Cancel = (Button) serviceView.findViewById(R.id.no);
		Cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mPhoneDialog != null) {
					mPhoneDialog.dismiss();
					mPhoneDialog = null;
				}
			}
		});
	}

	/**
	 * pop 长按事件
	 * 
	 * @param pos
	 * @param type
	 *            1-text 消息
	 * @param content
	 */
	private void showPromptDialog(final int pos, final int type, final String content, final MessageInfo oldMsg) {
		// normal_message_item
		String[] itemMenu;
		if (type == 1) {
			itemMenu = mContext.getResources().getStringArray(R.array.chat_message_item);
		} else {
			itemMenu = mContext.getResources().getStringArray(R.array.normal_message_item);
		}
		if (oldMsg.typefile == MessageType.CARD) {
			itemMenu = new String[] { mContext.getResources().getString(R.string.forward) };
		}
		MMAlert.showAlert(mContext, "", itemMenu, null, new OnAlertSelectId() {

			@Override
			public void onClick(int whichButton) {
				switch (whichButton) {
				case 0:
					if (type == 1) {// 复制
						ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
						cm.setText(content);

					} else {// 转发
							// msg
						Intent chooseUserIntent = new Intent();
						chooseUserIntent.setClass(mContext, ChooseUserActivity.class);
						// chooseUserIntent.putExtra("cardType",1);
						chooseUserIntent.putExtra("forward_msg", oldMsg);
						startActivity(chooseUserIntent);
						// sendBroad2Save(msg);
					}
					break;
				case 1:
					if (type == 1) {// 转发
						Intent chooseUserIntent = new Intent();
						chooseUserIntent.setClass(mContext, ChooseUserActivity.class);
						// chooseUserIntent.putExtra("cardType",1);
						chooseUserIntent.putExtra("forward_msg", oldMsg);
						startActivity(chooseUserIntent);
						// sendBroad2Save(msg);
					} else {// 收藏
						String ownerId = "";
						String groupId = "";
						if (oldMsg.typechat == 300 || oldMsg.typechat == 500) {
							groupId = oldMsg.toid;
							ownerId = oldMsg.fromid;
						} else if (oldMsg.typechat == 100) {
							ownerId = oldMsg.fromid;
						}
						/*
						 * if(oldMsg.getFromId().equals(mLogin.uid)){ if(oldMsg.typechat == 300){ groupId = oldMsg.fromid; ownerId = oldMsg.fromid; }else if(oldMsg.typechat == 100){ ownerId = oldMsg.fromid; } }else{ if(oldMsg.typechat == 300){ groupId = oldMsg.toid; ownerId = oldMsg.toid; }else if(oldMsg.typechat == 100){ ownerId = oldMsg.toid; }
						 * 
						 * }
						 */
						switch (oldMsg.typefile) {
						case MessageType.TEXT:

							MovingContent movingContent = new MovingContent(URLEncoder.encode(content), MessageType.TEXT + "");
							favoriteMoving(MovingContent.getInfo(movingContent), ownerId, groupId);
							break;
						case MessageType.PICTURE:
							MovingPic movingPic = new MovingPic(oldMsg.imgUrlL, oldMsg.imgUrlS, MessageType.PICTURE + "");
							favoriteMoving(MovingPic.getInfo(movingPic), ownerId, groupId);
							break;
						case MessageType.VOICE:
							MovingVoice movingVoice = new MovingVoice(oldMsg.voicetime + "", oldMsg.voiceUrl, MessageType.VOICE + "");
							favoriteMoving(MovingVoice.getInfo(movingVoice), ownerId, groupId);
							break;
						case MessageType.MAP:
							MovingLoaction movingLocation = new MovingLoaction(oldMsg.mLat + "", oldMsg.mLng + "", oldMsg.mAddress, MessageType.MAP + "");
							favoriteMoving(MovingLoaction.getInfo(movingLocation), ownerId, groupId);
							break;

						default:
							break;
						}

					}
					break;
				case 2:// 收藏
					String ownerId = "";
					String groupId = "";
					if (oldMsg.typechat == 300 || oldMsg.typechat == 500) {
						groupId = oldMsg.toid;
						ownerId = oldMsg.fromid;
					} else if (oldMsg.typechat == 100) {
						ownerId = oldMsg.fromid;
					}
					/*
					 * if(oldMsg.getFromId().equals(mLogin.uid)){ if(oldMsg.typechat == 300 || oldMsg.typechat == 500){ groupId = oldMsg.toid; ownerId = oldMsg.fromid; }else if(oldMsg.typechat == 100){ ownerId = oldMsg.fromid; } }else{ if(oldMsg.typechat == 300 || oldMsg.typechat == 500){ groupId = oldMsg.toid; ownerId = oldMsg.fromid; }else if(oldMsg.typechat == 100){ ownerId = oldMsg.fromid; }
					 * 
					 * }
					 */
					switch (oldMsg.typefile) {
					case MessageType.TEXT:
						MovingContent movingContent = new MovingContent(content, MessageType.TEXT + "");
						favoriteMoving(MovingContent.getInfo(movingContent), ownerId, groupId);
						break;
					case MessageType.PICTURE:
						MovingPic movingPic = new MovingPic(oldMsg.imgUrlS, oldMsg.imgUrlL, MessageType.PICTURE + "");
						favoriteMoving(MovingPic.getInfo(movingPic), ownerId, groupId);
						break;
					case MessageType.VOICE:
						MovingVoice movingVoice = new MovingVoice(oldMsg.voicetime + "", oldMsg.voiceUrl, MessageType.VOICE + "");
						favoriteMoving(MovingVoice.getInfo(movingVoice), ownerId, groupId);
						break;
					case MessageType.MAP:
						MovingLoaction movingLocation = new MovingLoaction(oldMsg.mLat + "", oldMsg.mLng + "", oldMsg.mAddress, MessageType.MAP + "");
						favoriteMoving(MovingLoaction.getInfo(movingLocation), ownerId, groupId);
						break;
					default:
						break;
					}
					break;
				default:
					break;
				}
			}
		});
	}

	private void favoriteMoving(final String favoriteContent, final String ownerUid, final String groupid) {
		if (!WeiYuanCommon.getNetWorkState()) {
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread() {
			public void run() {
				try {
					WeiYuanCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG, mContext.getResources().getString(R.string.send_request));
					WeiYuanState status = WeiYuanCommon.getWeiYuanInfo().favoreiteMoving(ownerUid, groupid, favoriteContent);
					WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_CHECK_FAVORITE_STATUS, status);
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				} catch (WeiYuanException e) {
					e.printStackTrace();
					WeiYuanCommon.sendMsg(mBaseHandler, BASE_MSG_TIMEOUT_ERROR, mContext.getResources().getString(e.getStatusCode()));
				} catch (Exception e) {
					e.printStackTrace();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				}
			};
		}.start();
	}

	/*
	 * 按钮点击事件 (non-Javadoc)
	 * 
	 * @see com.weiyuan.BaseActivity#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.chat_box_btn_send:
			// hideSoftKeyboard();
			// hideEmojiGridView();
			sendText();
			break;

		case R.id.chat_box_btn_info:
			togInfoSelect();
			break;
		case R.id.chat_box_btn_add:
			btnAddAction();
			break;
        case R.id.chat_box_btn_keyboard:
            togInfoKeySelect(true);
            break;

		case R.id.chat_box_expra_btn_camera:
			btnCameraAction();
			break;
		case R.id.chat_box_expra_btn_picture:
			btnPhotoAction();
			break;
		case R.id.chat_box_expra_btn_location:
			btnLocationAction();
			break;

		case R.id.chat_box_expra_btn_experssion:
			btnEmojiAction();
			break;
		case R.id.chat_box_expra_btn_card:
			hideExpra();
			Intent atIntent = new Intent();
			atIntent.setClass(mContext, ChooseUserActivity.class);
			atIntent.putExtra("isJump", 1);
			atIntent.putExtra("toLogin", fCustomerVo);
			startActivityForResult(atIntent, 1);
			break;
		case R.id.chat_box_expra_btn_favorite:
			hideExpra();
			Intent favoriteIntent = new Intent();
			favoriteIntent.setClass(mContext, MyFavoriteActivity.class);
			favoriteIntent.putExtra("isShow", false);
			// startActivityForResult(favoriteIntent, 1);
			startActivity(favoriteIntent);
			break;
		case R.id.small_video:// 小视频
			// 调用系统录制视频
			/*
			 * Intent mIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE); mIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 70000);//70s startActivityForResult(mIntent, CAMERA_ACTIVITY);//CAMERA_ACTIVITY = 1
			 */
			Intent videoIntent = new Intent();
			videoIntent.setClass(mContext, VideoActivity.class);
			startActivityForResult(videoIntent, REQUEST_GET_VIDEO_BY_CAMERA);
			break;

		case R.id.left_btn:
			hideSoftKeyboard();
			this.finish();
			break;

		case R.id.right_btn:
			SQLiteDatabase dbDatabase = DBHelper.getInstance(mContext).getWritableDatabase();
			SessionTable sessionTable = new SessionTable(dbDatabase);
			Session existSession = sessionTable.query(fCustomerVo.uid, mType);
			Intent detailIntent = new Intent(mContext, ChatDetailActivity.class);
			if (existSession != null) {
				if (existSession.isTop >= 1) {
					detailIntent.putExtra("isTop", true);
				} else {
					detailIntent.putExtra("isTop", false);
				}
			}
			detailIntent.putExtra("groupid", fCustomerVo.uid);

			detailIntent.putExtra("typechat", mType);
			if (mType != 100) {
				detailIntent.putExtra("isOwner", getIntent().getIntExtra("isOwner", 0));
				/*
				 * if(mGroupListItemIndex!=-1){ detailIntent.putExtra("group_list_item_index",mGroupListItemIndex); }
				 */
			} else {

				detailIntent.putExtra("isSignChat", 1);
				detailIntent.putExtra("to_login", fCustomerVo);
			}

			mContext.startActivity(detailIntent);
			// showMoreDialog(mContext);
			break;

		default:
			break;
		}
	}

	public void hideSoftKeyboard() {
		hideSoftKeyboard(getCurrentFocus());
	}

	public void hideSoftKeyboard(View view) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (view != null) {
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	public void showSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) mContentEdit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
	}

	public ProgressDialog getWaitDialog() {
		return waitDialog;
	}

	public void showToast(String content) {
		Toast.makeText(mContext, content, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int position) {
		mPageIndxe = position;
		showCircle(mViewList.size());
	}

	public static MyAdapter getAdapter() {
		if (mAdapter != null) {
			return mAdapter;
		}
		return null;
	}

    List<OrderMenuItem> mChildMenuList = new ArrayList<OrderMenuItem>();
    private List<PopItem> mPopList = new ArrayList<PopItem>();
    private PopWindows mPopWindows;

    private void showChatKeyBorad(){
        if(mKeyBoardMenuLayout!=null && mKeyBoardMenuLayout.getChildCount()>0){
            mKeyBoardMenuLayout.removeAllViews();
        }
        LayoutInflater layoutFlater = LayoutInflater.from(mContext);
        if(fCustomerVo.menuList == null || fCustomerVo.menuList.size()<=0){
            return;
        }

        mNormalSplite.setVisibility(View.VISIBLE);
        mKeyBoardBtn.setVisibility(View.VISIBLE);

        mKeyBoardBtn.setChecked(true);
        int size = fCustomerVo.menuList.size();
        if(size == 0){
            return;
        }
        for (int i = 0; i < size; i++) {

            View view = layoutFlater.inflate(R.layout.order_menu_item,null);

            view.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.FILL_PARENT,1
            ));


            TextView menuTitle = (TextView)view.findViewById(R.id.menu_title);
            menuTitle.setText(fCustomerVo.menuList.get(i).name);
            if(fCustomerVo.menuList.get(i).childMenuList!=null
                    && fCustomerVo.menuList.get(i).childMenuList.size()>0){
                TextView tv = (TextView)view.findViewById(R.id.exits_child_menu);
                tv.setVisibility(View.VISIBLE);
            }

            final int index = i;

            view.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            View view = null;
                                            int size = 0;
                                            String jumpUrl = "";
                                            if (mChildMenuList != null && mChildMenuList.size() > 0) {
                                                mChildMenuList.clear();
                                            }
                                            for (int j = 0; j < mKeyBoardMenuLayout.getChildCount(); j++) {
                                                if (j == index) {
                                                    view = mKeyBoardMenuLayout.getChildAt(j);
                                                    mChildMenuList.addAll(fCustomerVo.menuList.get(j).childMenuList);
                                                    jumpUrl = fCustomerVo.menuList.get(j).url;
                                                    size = mChildMenuList.size();
                                                    break;
                                                }
                                            }
                                            if (view == null) {
                                                return;
                                            }
                                            if (size == 0) {
                                                if (!TextUtils.isEmpty(jumpUrl)) {
                                                    Intent intent = new Intent(mContext, BrowserActivity.class);
                                                    intent.putExtra("url", jumpUrl);
                                                    intent.putExtra("is_hide_more_btn", 1);
                                                    intent.putExtra("data", fCustomerVo);
                                                    intent.putExtra("title", fCustomerVo.menuList.get(index).name);
                                                    startActivity(intent);
                                                    return;
                                                }
                                                else {
                                                    final int click_index = index;
                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                ResearchCommon.getResearchInfo().doMenuFuntion(String.valueOf(fCustomerVo.menuList.get(click_index).id));
                                                            } catch (ResearchException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }).start();

                                                    return;
                                                }

                                            }
                                            if (mPopList != null && mPopList.size() > 0) {
                                                mPopList.clear();
                                            }
                                            for (int k = 0; k < mChildMenuList.size(); k++) {
                                                mPopList.add(new PopItem(k, mChildMenuList.get(k).name));
                                            }

                                            mPopWindows.showGroupPopView(view, mPopList, Gravity.LEFT, R.drawable.order_menu_bg, R.color.order_menu_color,
                                                    1, new PopWindows.PopWindowsInterface() {

                                                        @Override
                                                        public void onItemClick(int dataId, int position, View view) {
                                                            String jumpUrl = mChildMenuList.get(position).url;
                                                            if (!TextUtils.isEmpty(jumpUrl)) {
                                                                Intent intent = new Intent(mContext, BrowserActivity.class);
                                                                intent.putExtra("url", jumpUrl);
                                                                intent.putExtra("title", mChildMenuList.get(position).name);
                                                                intent.putExtra("data", fCustomerVo);
                                                                startActivity(intent);
                                                            } else {
                                                                final int click_index = position;
                                                                new Thread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        try {
                                                                            ResearchCommon.getResearchInfo().doMenuFuntion(String.valueOf(mChildMenuList.get(click_index).id));
                                                                        } catch (ResearchException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    }
                                                                }).start();
                                                            }
                                                        }
                                                    });
                                        }
                                    }

            );
            mKeyBoardMenuLayout.addView(view);
        }
    }

}
