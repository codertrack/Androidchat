package qianfeng.changliao.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.alibaba.fastjson.JSON;

import qianfeng.changliao.app.BroadcastAction;
import qianfeng.changliao.net.socket.protocol.ChatMessage;

/**
 * Created by wukai on 15/12/10.
 */
public class ChatService extends Service {

	private MessageReceiver mReceiver;

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		initReceiver();
	}



	private void initReceiver(){
		mReceiver = new MessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(99);

		filter.addAction(BroadcastAction.RECEIVED_MESSAGE);
		filter.addAction(BroadcastAction.LOGIN_SUCESS);
		filter.addAction(BroadcastAction.USER_ONLINE);
		filter.addAction(BroadcastAction.USER_OFFLINE);
		filter.addAction(BroadcastAction.SEND_MESSAGE_SUCESS);


		registerReceiver(mReceiver, filter);
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	//广播接收器
	class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(BroadcastAction.RECEIVED_MESSAGE)){
				String message = intent.getStringExtra("msg");
				ChatMessage chatMessage = ChatMessage.initWithJsonObject(JSON.parseObject(message));
				abortBroadcast();
				//发送通知
			}else if (action.equals(BroadcastAction.USER_ONLINE)){

			}else if (action.equals(BroadcastAction.USER_OFFLINE)){

			}else if (action.equals(BroadcastAction.LOGIN_SUCESS)){

			}
		}
	}

}
