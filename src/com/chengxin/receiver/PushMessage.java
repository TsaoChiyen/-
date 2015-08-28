package com.chengxin.receiver;

import com.chengxin.Entity.MessageInfo;

/**
 * 
 * 功能：推送信息,好友之间的信息推送. <br />
 * 日期：2013-4-27<br />
 * 地点：有慧科技;Mr.Sam<br />
 * 版本：ver 1.0<br />
 * 
 * @Mr.Sam
 * @since
 */
public interface PushMessage {
	void pushMessage(MessageInfo msg, String group);
}
