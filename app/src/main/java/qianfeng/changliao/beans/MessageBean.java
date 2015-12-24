package qianfeng.changliao.beans;

import qianfeng.changliao.net.socket.protocol.ChatMessage;

/**
 * Created by wukai on 15/12/18.
 */
public class MessageBean  {

	public ChatMessage  msg;
	public Type received = Type.SEND;

	public MessageBean(ChatMessage chatMessage){
		msg = chatMessage;
	}
	 public enum Type{
		SEND,RECEIVED
	}
}
