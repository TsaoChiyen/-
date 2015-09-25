package com.chengxin.receiver;

import com.chengxin.service.XmppManager;
import com.xmpp.push.sns.PacketListener;
import com.xmpp.push.sns.packet.Message;
import com.xmpp.push.sns.packet.Packet;

public class MultiMessageListener implements PacketListener{
	
	public static final String LOGTAG = "MultiMessageListener";
	private XmppManager xmppManager;
	
	public MultiMessageListener(XmppManager xmppManager) {
		super();
		this.xmppManager = xmppManager;
	}

	@Override
	public void processPacket(Packet packet) {
		Message message = (Message) packet;
		xmppManager.getSnsMessageLisener().notityMessage(xmppManager.getSnsMessageLisener().getNotifyChatMessage(), message.getBody());
	}

}
