package com.chengxin;

import com.chengxin.R;
import com.chengxin.global.GlobalParam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * 提交订单状态
 * @author dl
 *
 */
public class CommitOrderStateActivity extends BaseActivity {

	private Button mOkBtn;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.commit_order_state);
		initCompent();
	}


	private void initCompent(){
		setTitleContent(R.drawable.back_btn,0,R.string.commit_state);
		mLeftBtn.setOnClickListener(this);
		mOkBtn = (Button)findViewById(R.id.ok_btn);
		mOkBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			CommitOrderStateActivity.this.finish();
			break;
		case R.id.ok_btn://确定
			CommitOrderStateActivity.this.finish();
			sendBroadcast(new Intent(GlobalParam.ACTION_DESTROY_GOODS_DETAIL_PAGE));
			sendBroadcast(new Intent(GlobalParam.ACTION_DESTROY_SHOPPING_CART_PAGE));
			sendBroadcast(new Intent("jump_to_main"));
			break;

		default:
			break;
		}
	}

}
