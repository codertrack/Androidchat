package qianfeng.changliao.app;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import qianfeng.changliao.beans.ChatUser;
import qianfeng.changliao.net.socket.ChannelMessage;
import qianfeng.changliao.net.socket.ChatEngine;
import qianfeng.changliao.net.socket.protocol.ProtocolHandler;
import qianfeng.changliao.utils.Pref_Utils;

/**
 * Created by wukai on 15/12/17.
 */
public class UserManager  {

	private Set<String> onLineUser;

	private List<ChatUser> mFriends;

	private HashMap<String,ChatUser> mAllUsers;


	private static UserManager Instance;


	public static UserManager getInstance(){

		if (Instance == null){
			Instance = new UserManager();

		}
		return Instance;
	}


	public UserManager(){
		onLineUser = new HashSet<>();
		mAllUsers = new HashMap<>();

	}


	public boolean isOnline(String userid){
		boolean flag = onLineUser.contains(userid);;
		return flag;
	}

	//添加好友
	//刘德华
	public void addUser(ChatUser user){
		mAllUsers.put(user.userid,user);
	}

	public ChatUser getChatUser(String userid){
		return mAllUsers.get(userid);
	}

	public void addOnlineUser(String userid){
		onLineUser.add(userid);
	}

	public void removeOnlineUser(String userid){
		onLineUser.remove(userid);
	}

	public void addUsers(JSONArray jsonArray){
		if (jsonArray == null)return;
		JSONObject obj = null;
		for (int i = 0; i < jsonArray.size(); i++) {
			ChatUser chatUser = new ChatUser();
			obj = jsonArray.getJSONObject(i);
			chatUser.avart = obj.getString("avart");
			chatUser.username = obj.getString("username");
			chatUser.shuoshuo = obj.getString("shuoshuo");
			chatUser.userid = obj.getString("account");
			addUser(chatUser);
		}
	}


	public static String getCurrentUserid(Context context){
		return Pref_Utils.getString(context, SettingKey.PREF_KEY_USERID);
	}

	public static   void setCurrentUserid(Context context, String currentUserid) {
		Pref_Utils.putString(context, SettingKey.PREF_KEY_USERID, currentUserid);
	}



	//获取好友
	public List<ChatUser> getAllFriends(String userid) {
		if (mFriends == null) {
			mFriends = new ArrayList<>();
		}
		Iterator<ChatUser> iterator = mAllUsers.values().iterator();
		for (;iterator.hasNext();){
			ChatUser chatUser = iterator.next();
			if (!chatUser.userid.equals(userid)){
				mFriends.add(chatUser);
			}
		}
		//在线的显示在前面
		Collections.sort(mFriends, new Comparator<ChatUser>() {
			@Override
			public int compare(ChatUser lhs, ChatUser rhs) {
				int sum = 0;
				if (onLineUser.contains(lhs.userid)) {
					sum -= 1;
				}
				if (onLineUser.contains(rhs.userid)) {
					sum += 1;
				}
				return sum;
			}
		});
		return mFriends;
	}



}
