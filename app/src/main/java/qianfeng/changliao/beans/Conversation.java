package qianfeng.changliao.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wukai on 15/12/18.
 */
public class Conversation {

	private List<MessageBean> mMessages;
	public String withUserid;
	public String withUsername;
	public String lastMessage;

	public Conversation(){
		mMessages = new ArrayList<>();
	}

	public void addMessage(MessageBean messageBean){
		mMessages.add(messageBean);
		lastMessage = messageBean.msg.text;
	}

	public List<MessageBean> getMessages(){
		return mMessages;
	}

}
