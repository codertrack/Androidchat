package qianfeng.changliao.app;

import android.os.Looper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import android.os.Handler;
import qianfeng.changliao.beans.Conversation;
import qianfeng.changliao.beans.MessageBean;
import qianfeng.changliao.utils.Logutils;

/**
 * Created by wukai on 15/12/18.
 * 管理消息和会话列表
 */
public class MessageManager {

	public HashMap<String,Conversation> mHashConver;

	private Handler mHandler;
	private static MessageManager Instance;

	private List<Conversation> mConvers;

	private MessageManager(){
		mHashConver = new HashMap<>();
		changes = new ArrayList<>();
		mHandler = new Handler(Looper.getMainLooper());

	}

	public static MessageManager getInstance(){

		if (Instance == null){
			synchronized (MessageManager.class){
				if (Instance == null){
					Instance = new MessageManager();
				}
			}
		}
		return Instance;
	}


	//添加一条消息
	public void addMessage(final String withuserid,MessageBean mbean){
		Conversation conversation = getConversation(withuserid);
		conversation.addMessage(mbean);
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				notifyChangeAddMessage(withuserid);

			}
		});

	}

	public Conversation getConversation(final String withuserid){
		Conversation conversation = mHashConver.get(withuserid);
		if (conversation == null){
			newConversation(withuserid);
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					notifyChangeAddConversation(withuserid);

				}
			});
		}
		return mHashConver.get(withuserid);
	}

	private void newConversation(String withid){
		Conversation conversation = new Conversation();
		conversation.withUserid =withid;
		conversation.withUsername = UserManager.getInstance().getChatUser(withid).username;
		mHashConver.put(withid, conversation);
		mConvers.add(conversation);
		;
	}

	//获取会话列表
	public List<Conversation> getAllConversation(){
		if (mConvers == null){
			mConvers = new ArrayList<>();
		}
		Iterator<Conversation> iterator = mHashConver.values().iterator();
		for (;iterator.hasNext();){
			Conversation con = iterator.next();
			Logutils.logd("conversation size="+con.getMessages().size());
			if (con.getMessages().size()>0){
				mConvers.add(con);
			}
		}
		return mConvers;
	}


	public interface OnConversationChange{
		public void onAddConversation(String withuserid);
		public void onAddMessage(String withUserid);
	}


	private List<OnConversationChange> changes;

	public void addChangeObserver(OnConversationChange change){
		changes.add(change);
	}


	public void notifyChangeAddConversation(String userid){
		for (OnConversationChange change:changes){
			change.onAddConversation(userid);
		}
	}


	public void notifyChangeAddMessage(String userid){
		for (OnConversationChange change:changes){
			change.onAddMessage(userid);
		}
	}

	public void removeConversation(Conversation conversation){
		mHashConver.remove(conversation.withUserid);
		mConvers.remove(conversation);
	}
}
