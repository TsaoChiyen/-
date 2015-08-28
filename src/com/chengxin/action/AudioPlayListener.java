package com.chengxin.action;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.chengxin.ChatMainActivity;
import com.chengxin.R;
import com.chengxin.DB.DBHelper;
import com.chengxin.DB.MessageTable;
import com.chengxin.Entity.MessageInfo;
import com.chengxin.control.ReaderImpl;
import com.chengxin.global.FeatureFunction;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.receiver.PushChatMessage;

/**
 * 
 * 功能： 按钮控制播放音频. <br />
 * 按钮对应的 TAG 为 音频 的URL地址<br />
 * 
 */
public class AudioPlayListener implements OnClickListener{
	private String url;  //   音频网络地址.
	private AudioPlay mAudioPlay;
	private Context context;
	private AudioPlayStatus playStatus;
	
	public AudioPlayListener(Context context) {
		super();
		this.context = context;
		this.playStatus = new AudioPlayStatus();
		this.mAudioPlay = new AudioPlay(this.context){
			@Override
			protected void onPlayStart(int type) {
				super.onPlayStart(type);
				if(type == 0){
					playStatus.playing();
				}
			}

			@Override
			protected void onPlayStop() {
				super.onPlayStop();
				Log.e("AudioPlayListener", mAudioPlay.getMessageTag());
				mAudioPlay.setMessageTag("");
				Log.e("AudioPlayListener", "onPlayStop");
				playStatus.pause();
			}
		};
	}
	
	public AudioPlayStatus getPlayStatus(){
		return playStatus;
	}
	
	public String getMessageTag(){
		return mAudioPlay.getMessageTag();
	}

	@Override
	public void onClick(View v) {
		
		MessageInfo msg = (MessageInfo) v.getTag();
		if(msg == null){
			return;
		}
		
		if(mAudioPlay.getCurrentUrl().equals(msg.voiceUrl) && mAudioPlay.getPlayState()){
			mAudioPlay.stop(true);
			return;
		} else {
			mAudioPlay.stop(true);
		}
		
		Log.e("AudioPlayListener", "onClick");
		mAudioPlay.setMessageTag(msg.tag);
		
		this.playStatus.setBtn((RelativeLayout)v);
		this.playStatus.playing();
		
		url = msg.voiceUrl;
		String fileName = null;
		if(!url.startsWith("AUDIO_")){
			fileName = FeatureFunction.generator(url);
		}else{
			fileName = url;
		}
		File file = new File(ReaderImpl.getAudioPath(context), fileName);
		
		/*if(4 == msg.getSendState()){
			//Toast.makeText(context, context.getString(R.string.download_voice), Toast.LENGTH_SHORT).show();
			down(msg);
			return;
		}*/
		
		if(!file.exists()){
			//Toast.makeText(context, "播放文件不存在!", Toast.LENGTH_SHORT).show();
			if(url.startsWith("http://")){
				msg.sendState = 4;
				//Toast.makeText(context, context.getString(R.string.download_voice), Toast.LENGTH_SHORT).show();
				down(msg);
				return;
			}
		}
		// 为button添加播放状态
		//ImageView playImageView = (ImageView) v/*.findViewById(R.id.chat_talk_msg_info_msg_voice)*/;
		this.mAudioPlay.play(msg, 0, true);
		
		if(!msg.getFromId().equals(WeiYuanCommon.getUserId(context)) && msg.isReadVoice == 0){
			msg.isReadVoice = 1;
			SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
			MessageTable table = new MessageTable(db);
			table.update(msg);
			
			Intent intent = new Intent(ChatMainActivity.ACTION_READ_VOICE_STATE);
			intent.putExtra(PushChatMessage.EXTRAS_MESSAGE, msg);
			context.sendBroadcast(intent);
		}
	}
	
	public void stop(){
		mAudioPlay.stop();
	}
	
	public void play(MessageInfo messageInfo){
		Log.e("AudioPlayListener", "play");
		mAudioPlay.setMessageTag(messageInfo.tag);
		mAudioPlay.play(messageInfo, 1, true);
	}
	
	public void down(MessageInfo msg){
		
	}
	
	public class AudioPlayStatus {
		private AnimationDrawable animBtnPlay;

		private AnimationDrawable tempAnimBtnPlay;
		
		private RelativeLayout btn;

		public AudioPlayStatus() {
			super();
		}

		public void setBtn(RelativeLayout btn) {
			this.btn = btn;
			initParam();
		}

		private void initParam() {
			if(animBtnPlay != null){
				tempAnimBtnPlay = animBtnPlay;
			}
			animBtnPlay = (AnimationDrawable) ((ImageView)this.btn.findViewById(R.id.chat_talk_msg_info_msg_voice)).getDrawable();
			pause();
		}
		
		/**
		 * 等待位置..
		 * 
		 * 作者:Mr.Sam <br />
		 * 创建时间:2013-4-19<br />
		 * 修改时间:<br />
		 */
		public void pause() {
			Log.e("AudioPlayListener", "pause");
			if (btn!= null && animBtnPlay != null){
				animBtnPlay.stop();
				animBtnPlay.selectDrawable(0);
			}
		}

		/**
		 * 播放音频状态.
		 * 
		 * 作者:Mr.Sam <br />
		 * 创建时间:2013-4-19<br />
		 * 修改时间:<br />
		 */
		public void playing() {
			if (btn!= null && animBtnPlay != null) {
				Log.e("AudioPlayListener", "playing");
				if(tempAnimBtnPlay != null && tempAnimBtnPlay.isRunning()){
					tempAnimBtnPlay.stop();
					tempAnimBtnPlay.selectDrawable(0);
				}
				
				animBtnPlay.start();
			}

		}

	}

}
