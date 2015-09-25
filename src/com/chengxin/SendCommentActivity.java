package com.chengxin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Toast;

import com.chengxin.R;
import com.chengxin.Entity.CommentGoodsState;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.net.WeiYuanException;

/**
 * 发布评论
 * @author dl
 *
 */
public class SendCommentActivity extends BaseActivity{

	private Button mRealseBtn;
	private EditText mCommentEdit;
	private RatingBar mRatingBar;

	private String mInputCommint;
	private int mNumCount;
	private String mGoodsId;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.send_commit_view);
		mGoodsId = getIntent().getStringExtra("goods_id");
		initCompent();
	}

	/**初始化控件*/
	private void initCompent(){
		setTitleContent(R.drawable.back_btn, 0,R.string.relase_commit);
		mLeftBtn.setOnClickListener(this);
		mRealseBtn = (Button)findViewById(R.id.realse_btn);
		mRealseBtn.setOnClickListener(this);
		mCommentEdit = (EditText)findViewById(R.id.desc);
		mRatingBar = (RatingBar)findViewById(R.id.ratingBar1);
		mRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				System.out.println("等级：" + rating);  
				System.out.println("星星：" + ratingBar.getNumStars());  
				mNumCount = (int) rating;
			}
		});
	}

	//forGoodsAddComment

	private void comment(){
		if(!checkValue()){
			return;
		}
		
		if(!WeiYuanCommon.getNetWorkState()){
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread(){
			public void run() {
				try {
					WeiYuanCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG,
							mContext.getResources().getString(R.string.commit_dataing));
					CommentGoodsState state = (CommentGoodsState)WeiYuanCommon.getWeiYuanInfo().
							forGoodsAddComment(mGoodsId, mNumCount, mInputCommint);
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
					WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_CHECK_STATE,state);
				} catch (WeiYuanException e) {
					e.printStackTrace();
					WeiYuanCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG,
							mContext.getResources().getString(e.getStatusCode()));
				}catch (Exception e) {
					e.printStackTrace();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				}
			};
		}.start();
	}


	private boolean checkValue(){
		boolean isCheck = true;
		String hintMsg = "";
		mInputCommint = mCommentEdit.getText().toString();
		if(mInputCommint == null || mInputCommint.equals("")){
			isCheck = false;
			hintMsg = "请输入评论内容";
		}else if(mNumCount == 0){
			isCheck = false;
			hintMsg ="请为该商品添加星级评论内容";
		}
		if(!isCheck && (hintMsg!=null && !hintMsg.equals(""))){
			Toast.makeText(mContext, hintMsg,Toast.LENGTH_LONG).show();
		}
		
		return isCheck;
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			SendCommentActivity.this.finish();
			break;
		case R.id.realse_btn:
			comment();
			break;

		default:
			break;
		}
	}

	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GlobalParam.MSG_CHECK_STATE:
				CommentGoodsState commentGoodsState = (CommentGoodsState)msg.obj;
				if(commentGoodsState == null || commentGoodsState.state == null){
					Toast.makeText(mContext, R.string.commit_data_error,Toast.LENGTH_LONG).show();
					return;
				}
				Toast.makeText(mContext, commentGoodsState.state.errorMsg,Toast.LENGTH_LONG).show();
				if(commentGoodsState.state.code == 0){
					Intent intent = new Intent();
					intent.putExtra("comment",commentGoodsState);
					setResult(RESULT_OK,intent);
					SendCommentActivity.this.finish();
				}
				break;

			default:
				break;
			}
			
		}
		
	};
	
}
