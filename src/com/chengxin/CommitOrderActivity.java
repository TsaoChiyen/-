package com.chengxin;

import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chengxin.R;
import com.chengxin.Entity.Goods;
import com.chengxin.Entity.Merchant;
import com.chengxin.Entity.Order;
import com.chengxin.Entity.ShoppingCart;
import com.chengxin.Entity.UniPayResult;
import com.chengxin.Entity.WeiYuanState;
import com.chengxin.dialog.MMAlert;
import com.chengxin.dialog.MMAlert.OnAlertSelectId;
import com.chengxin.global.GlobalParam;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.net.WeiYuanException;
import com.unionpay.UPPayAssistEx;
import com.unionpay.uppay.PayActivity;

/**
 * 提交订单
 * 
 * @author dl
 * 
 */
public class CommitOrderActivity extends BaseActivity {

	private Button mCommitBtn;
	private EditText mContactNameEdit, mTelPhoneEdit, mAddrEdit, mDescEdit;

	private String mInputContactName, mInputTelPhone, mInputAddr, mInputDesc;

	private Merchant mMerchant;
	private EditText mPayModeEdit;
	private int mType;
	private int mShopType = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.commit_order_view);
		mMerchant = (Merchant) getIntent().getSerializableExtra("entity");
		mShopType = getIntent().getIntExtra("shop_type", 0);
		initCompent();
		IntentFilter fileter = new IntentFilter();
		fileter.addAction(GlobalParam.ACTION_DESTROY_GOODS_DETAIL_PAGE);
		registerReceiver(mDestroyReceiver, fileter);
	}

	private void initCompent() {
		setTitleContent(R.drawable.back_btn, 0, R.string.commit_order);
		mLeftBtn.setOnClickListener(this);
		mCommitBtn = (Button) findViewById(R.id.commit_btn);
		mCommitBtn.setOnClickListener(this);

		mContactNameEdit = (EditText) findViewById(R.id.contact_name);
		mTelPhoneEdit = (EditText) findViewById(R.id.contact_tel);
		mAddrEdit = (EditText) findViewById(R.id.contact_addr);
		mPayModeEdit = (EditText) findViewById(R.id.pay_mode);
		mPayModeEdit.setFocusable(false);
		mPayModeEdit.setFocusableInTouchMode(false);
		mPayModeEdit.setOnClickListener(this);
		mDescEdit = (EditText) findViewById(R.id.desc);

		mPayModeEdit.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		if (mDestroyReceiver != null) {
			unregisterReceiver(mDestroyReceiver);
		}
		super.onDestroy();
	}

	/* 判断输入的值 */
	private boolean checkvalue() {
		boolean isCheck = true;
		String hintMsg = "";
		mInputContactName = mContactNameEdit.getText().toString();
		mInputTelPhone = mTelPhoneEdit.getText().toString();
		mInputAddr = mAddrEdit.getText().toString();
		mInputDesc = mDescEdit.getText().toString();
		if (mInputContactName == null || mInputContactName.equals("")) {
			isCheck = false;
			hintMsg = "请输入联系人名称";
		} else if (mInputTelPhone == null || mInputTelPhone.equals("")) {
			isCheck = false;
			hintMsg = "请输入联系电话";
		} else if (!WeiYuanCommon.isMobileNum(mInputTelPhone)) {
			isCheck = false;
			hintMsg = "请输入正确的电话号码";
		} else if (mInputAddr == null || mInputAddr.equals("")) {
			isCheck = false;
			hintMsg = "请输入联系人地址";
		} else if (mType == 0) {
			isCheck = false;
			hintMsg = "请选择支付方式";
		} else if (mInputDesc != null && !mInputDesc.equals("")) {
			if (mInputDesc.length() > 150) {
				isCheck = false;
				hintMsg = "备注信息不能超过150个字";
			}
		}
		if (!isCheck && (hintMsg != null && !hintMsg.equals(""))) {
			Toast.makeText(mContext, hintMsg, Toast.LENGTH_LONG).show();
		}
		return isCheck;
	}

	private BroadcastReceiver mDestroyReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent == null
					|| (intent.getAction() == null || intent.getAction()
							.equals(""))) {
				return;
			}
			String action = intent.getAction();
			if (action.equals(GlobalParam.ACTION_DESTROY_GOODS_DETAIL_PAGE)) {
				List<ShoppingCart> mCartList = WeiYuanCommon
						.getShoppingCartData(mContext);
				if (mCartList != null && mCartList.size() > 0) {// 加载购物车数据
					for (int i = 0; i < mCartList.size(); i++) {
						if (mCartList.get(i).shopId == mMerchant.id) {
							mCartList.remove(mCartList.get(i));
							WeiYuanCommon.saveShoppingCartData(mContext,
									mCartList);

							sendBroadcast(new Intent(
									GlobalParam.ACTION_REFRESH_MERCHANT_GOODS_COUNT));
							break;
						}
					}
				}
				CommitOrderActivity.this.finish();
			}
		}
	};

	/* 提交订单 */
	private void commitOrder() {
		if (!checkvalue()) {
			return;
		}

		final List<Goods> goodsList = mMerchant.goodsList;
		if (goodsList == null || goodsList.size() <= 0) {
			return;
		}
		if (!WeiYuanCommon.getNetWorkState()) {
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread() {
			public void run() {
				try {
					WeiYuanCommon.sendMsg(mBaseHandler,
							BASE_SHOW_PROGRESS_DIALOG, mContext.getResources()
									.getString(R.string.commit_dataing));
					String goods = "";

					// 商品id1*count1,id2*count2
					for (int i = 0; i < goodsList.size(); i++) {
						Goods good = goodsList.get(i);

						if (i != goodsList.size() - 1) {
							goods = goods + good.id + "*" + good.count + ",";
						} else {
							goods = goods + good.id + "*" + good.count;
						}
					}

					Order order = WeiYuanCommon.getWeiYuanInfo().submitOrder(
							mShopType,
							mType,
							goods,
							mInputContactName,
							mInputTelPhone,
							mInputAddr,
							mInputDesc,
							mMerchant.id);
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
					WeiYuanCommon.sendMsg(mHandler,
							GlobalParam.MSG_CHECK_STATE, order);
				} catch (NotFoundException e) {
					e.printStackTrace();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				} catch (WeiYuanException e) {
					e.printStackTrace();
					WeiYuanCommon.sendMsg(mBaseHandler,
							BASE_SHOW_PROGRESS_DIALOG, mContext.getResources()
									.getString(e.getStatusCode()));
				} catch (Exception e) {
					e.printStackTrace();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				}
			};
		}.start();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			CommitOrderActivity.this.finish();
			break;
		case R.id.commit_btn:// 提交订单
			commitOrder();
			break;
		case R.id.pay_mode:
			final String item[] = mContext.getResources().getStringArray(
					R.array.pay_mode_item);
			MMAlert.showAlert(mContext, "", item, null, new OnAlertSelectId() {

				@Override
				public void onClick(int whichButton) {
					mType = whichButton + 1;
					mPayModeEdit.setText(item[whichButton]);
				}
			});
			break;
		default:
			break;
		}
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GlobalParam.MSG_CHECK_STATE:
				Order order = (Order) msg.obj;

				if (order == null) {
					return;
				}

				WeiYuanState state = order.state;

				if (state == null) {
					Toast.makeText(mContext, R.string.commit_data_error,
							Toast.LENGTH_LONG).show();
					return;
				}

				if (state.errorMsg.length() > 0) {
					Toast.makeText(mContext, state.errorMsg,
							Toast.LENGTH_LONG).show();
				}
				
				if (state.code == 0) {
					UniPayResult payResult = order.uniPayResult;

					if (payResult != null && payResult.tn != null
							&& !payResult.tn.equals("")) {
						confirmPay(payResult.tn);
					} else {
						Intent resultIntent = new Intent();
						resultIntent.setClass(mContext,
								CommitOrderStateActivity.class);
						startActivity(resultIntent);
					}
				}

				break;

			default:
				break;
			}
		}

	};

	private void confirmPay(String payTN) {
		// “00” – 银联正式环境
		// “01” – 银联测试环境,该环境中不发生真实交易
		if (payTN == null || payTN.equals("")) {
			return;
		}

		String serverMode = "01";
		UPPayAssistEx.startPayByJAR(this, PayActivity.class, null, null, payTN,
				serverMode);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		String msg = "";
		/*
		 * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
		 */
		String str = data.getExtras().getString("pay_result");
		if (str.equalsIgnoreCase("success")) {
			msg = "支付成功！";
		} else if (str.equalsIgnoreCase("fail")) {
			msg = "支付失败！";
		} else if (str.equalsIgnoreCase("cancel")) {
			msg = "用户取消了支付";
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("支付结果通知");
		builder.setMessage(msg);
		builder.setInverseBackgroundForced(true);
		// builder.setCustomTitle();
		builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

				Intent resultIntent = new Intent();
				resultIntent.setClass(mContext, CommitOrderStateActivity.class);
				startActivity(resultIntent);
			}
		});
		builder.create().show();
	}
}
