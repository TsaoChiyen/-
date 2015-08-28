package com.chengxin;

import java.util.ArrayList;
import java.util.List;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;
import com.chengxin.R;
import com.learnncode.mediachooser.MediaChooser;
import com.learnncode.mediachooser.activity.HomeFragmentActivity;
import com.learnncode.mediachooser.adapter.MediaGridViewAdapter;

/**
 * 选择视频和图片
 * @author dongli
 *
 */
public class SelectPicAndVideoActivity extends BaseActivity{

	GridView gridView;
	MediaGridViewAdapter adapter;
	private ArrayList<String> mFilePathList=new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.select_image_video_view);
		initCompent();
		
		IntentFilter videoIntentFilter = new IntentFilter(MediaChooser.VIDEO_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
		registerReceiver(videoBroadcastReceiver, videoIntentFilter);

		IntentFilter imageIntentFilter = new IntentFilter(MediaChooser.IMAGE_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
		registerReceiver(imageBroadcastReceiver, imageIntentFilter);
	}


	private void initCompent(){
		setTitleContent(R.drawable.back_btn,R.drawable.send_map_btn, "选择图片或视频" );
		mLeftBtn.setOnClickListener(this);
		mRightBtn.setOnClickListener(this);
		gridView = (GridView)findViewById(R.id.gridView);
		Intent intent = new Intent(mContext, HomeFragmentActivity.class);
		startActivity(intent);
	}


	BroadcastReceiver videoBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			//Toast.makeText(mContext, "yippiee Video ", Toast.LENGTH_SHORT).show();
			//Toast.makeText(mContext, "Video SIZE :" + intent.getStringArrayListExtra("list").size(), Toast.LENGTH_SHORT).show();
			setAdapter(intent.getStringArrayListExtra("list"));
		}
	};


	BroadcastReceiver imageBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			//Toast.makeText(mContext, "yippiee Image ", Toast.LENGTH_SHORT).show();
			//Toast.makeText(mContext, "Image SIZE :" + intent.getStringArrayListExtra("list").size(), Toast.LENGTH_SHORT).show();
			setAdapter(intent.getStringArrayListExtra("list"));
		}
	};

	@Override
	protected void onDestroy() {
		unregisterReceiver(imageBroadcastReceiver);
		unregisterReceiver(videoBroadcastReceiver);
		super.onDestroy();
	}

	private void setAdapter( List<String> filePathList) {
		if(adapter == null){
			if(mFilePathList != null && mFilePathList.size()>0){
				mFilePathList.clear();
			}
			mFilePathList.addAll(filePathList);
			adapter = new MediaGridViewAdapter(mContext, 0, filePathList);
			gridView.setAdapter(adapter);
		}else{
			addTempAll(filePathList);
			adapter.addAll(filePathList);
			adapter.notifyDataSetChanged();
		}
	}
	
	public void addTempAll( List<String> mediaFile) {
		if(mediaFile != null){
			int count = mediaFile.size();
			for(int i = 0; i < count; i++){
				if(mFilePathList.contains(mediaFile.get(i))){

				}else{
					mFilePathList.add(mediaFile.get(i));
				}
			}
		}
	}


	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			SelectPicAndVideoActivity.this.finish();
			break;
		case R.id.right_btn://上传图片
			Intent intent = new Intent();
			intent.putStringArrayListExtra("filePathList",mFilePathList );
			setResult(RESULT_OK, intent);
			SelectPicAndVideoActivity.this.finish();
			break;

		default:
			break;
		}
	}
	
	

}
