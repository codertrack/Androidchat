package qianfeng.changliao.net.socket.protocol;

import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import qianfeng.changliao.app.BroadcastAction;
import qianfeng.changliao.app.MessageManager;
import qianfeng.changliao.app.UserManager;
import qianfeng.changliao.beans.MessageBean;
import qianfeng.changliao.utils.Logutils;


/**
 * Created by wukai on 15/12/10.
 */
public class ProtocolHandler {
	//通道初始化
	public static final int CMD_INIT = 0x01;
	//消息转发
	public static final int CMD_CHAT = 0x02;
	//同步服务器时间
	public static final int SYNC_TIME = 0x03;
	//离线
	public static final int OFFLINE = 0x04;
	//上线
	public static final int ONLINE = 0x05;

	public Context mContext;

	public ProtocolHandler (Context context){
		mContext = context;
	}

	public void handleCmd(int cmd,String text){
		Logutils.logd("cmd->"+cmd+"/text->"+text);
		if (cmd == CMD_INIT){
 			handleInitCmd(text);
			//刘德华
		}else if (cmd == CMD_CHAT){
			handleChatMessage(text);
		}else if (cmd == SYNC_TIME){

		}else if (cmd == OFFLINE){
			handleOffLine(text);
		}else if (cmd == ONLINE){
			handleOnline(text);
		}
	}


	/**
	 *
	 * @param text
	 */
	public void handleChatMessage(String text){
		if (text == null)return;
		Intent intent = new Intent(BroadcastAction.RECEIVED_MESSAGE);
		intent.putExtra("msg",text);
		//发送有序广播，

		ChatMessage chatMessage = ChatMessage.initWithJsonObject(JSON.parseObject(text));
		MessageBean mb = new MessageBean(chatMessage);
		mb.received = MessageBean.Type.RECEIVED;

		MessageManager.getInstance().addMessage(chatMessage.fromUserid,mb);

		mContext.sendOrderedBroadcast(intent,null);


	}

	//处理初始化信息

	/**8
	 *
	 * @param text 当前在线用户
	 */
	public void handleInitCmd(String text){
		System.out.println("server response:"+text);
		JSONObject jsonObject = JSON.parseObject(text);
		if (jsonObject == null){
			Logutils.logd("return user null...");
			return;
		}

		if (jsonObject.getString("msg").equals("ok")){
			Intent intent = new Intent(BroadcastAction.LOGIN_SUCESS);

			//获取在线用户
			JSONArray jsonArray = jsonObject.getJSONArray("users");
			if (jsonArray != null && jsonArray.size() != 0) {
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject json = jsonArray.getJSONObject(i);
					String userid = json.getString("userid");
					UserManager.getInstance().addOnlineUser(userid);
				}
			}
			mContext.sendBroadcast(intent);
		}
	}



	public void handleOffLine(String userid){
		UserManager.getInstance().removeOnlineUser(userid);
		mContext.sendBroadcast(new Intent(BroadcastAction.USER_OFFLINE));

	}

	public void handleOnline(String lineId){
		UserManager.getInstance().addOnlineUser(lineId);
		mContext.sendBroadcast(new Intent(BroadcastAction.USER_ONLINE));
	}

}
