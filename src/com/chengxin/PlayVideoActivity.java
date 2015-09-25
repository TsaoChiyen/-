package com.chengxin;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.VideoView;
import com.chengxin.R;

public class PlayVideoActivity extends BaseActivity{
	private VideoView mVideoView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hp_videoview);
		mContext = this;
		mVideoView = (VideoView) this.findViewById(R.id.hp_videoview);
		String path = getIntent().getStringExtra("path");


		if (path.indexOf("http://") != -1) {
			mVideoView.setVideoURI(Uri.parse(path));// 网络资源
		} else if (path.contains("/WeiYuan/")) {
			///storage/emulated/0/WeiYuan/video_1418065240218.mp4
			mVideoView.setVideoPath(path);// 本地资源
		} else {
			Toast.makeText(this, "视频资源不存在", Toast.LENGTH_LONG).show();
			PlayVideoActivity.this.finish();
			return;
		}
		//mVideoView.setMediaController(new MediaController(PlayVideoActivity.this));// 设置模式，播放进度条
		mVideoView.requestFocus();
		mVideoView.start();

	}

}
