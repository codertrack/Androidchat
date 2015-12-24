package qianfeng.changliao.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;


import qianfeng.changliao.R;
import qianfeng.changliao.adapter.ChatMessageApdater;
import qianfeng.changliao.app.BroadcastAction;
import qianfeng.changliao.app.MessageManager;
import qianfeng.changliao.app.UserManager;
import qianfeng.changliao.beans.Conversation;
import qianfeng.changliao.beans.MessageBean;
import qianfeng.changliao.net.socket.ChatEngine;
import qianfeng.changliao.net.socket.protocol.ChatMessage;

/**
 * Created by wukai on 15/12/17.
 */
public class ChatActivity extends BaseActivity {

	private ListView mListView;

	private EditText mET_Message;

	private String withUserid;

	private TextView mTv_Name;

	private ChatMessageApdater mAdapter;

	private Conversation conversation;

	//刘德华
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		initView();
		conversation = MessageManager.getInstance().getConversation(withUserid);
		mAdapter = new ChatMessageApdater(this,conversation);
		mListView.setAdapter(mAdapter);
		initReceiver();
	}

	public void initView(){
		mListView = (ListView) findViewById(R.id.list);
		mET_Message = (EditText) findViewById(R.id.et_sendmessage);
		mTv_Name = (TextView) findViewById(R.id.tv_chat_username);
		withUserid = getIntent().getStringExtra("userid");
		mTv_Name.setText(getIntent().getStringExtra("username"));
	}

	@Override
	public void back(View view) {
		if (conversation.getMessages().size() == 0){
			MessageManager.getInstance().removeConversation(conversation);
		}
		onBackPressed();
	}


	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.btn_send){
			sendMessage();
		}
	}

	public void sendMessage(){
		String msg = mET_Message.getEditableText().toString();
		if (msg == null ||msg.length() == 0)return;
		ChatMessage chatMessage = new ChatMessage();
		chatMessage.time =""+System.currentTimeMillis();
		chatMessage.fromUserid = UserManager.getCurrentUserid(this);
		chatMessage.text = msg;
		chatMessage.toUserid = withUserid;
		ChatEngine.getInstance(mApp).sendChatMessage(chatMessage);


		MessageBean mb = new MessageBean(chatMessage);
		mb.received = MessageBean.Type.SEND;
		conversation.addMessage(mb);
		mAdapter.notifyDataSetChanged();
		//清空输入框
		mET_Message.getEditableText().clear();
	}

	private MessageReceiver mReceiver;


	//广播接收器
	class MessageReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(BroadcastAction.RECEIVED_MESSAGE)){
//				String message = intent.getStringExtra("msg");
//				ChatMessage chatMessage = ChatMessage.initWithJsonObject(JSON.parseObject(message));
//				if (chatMessage.toUserid.equals(withUserid)){
					mAdapter.notifyDataSetChanged();
//					//终止继续传递
					abortBroadcast();
//				}

			}
		}
	}

	private void initReceiver(){
		mReceiver = new MessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(100);
		filter.addAction(BroadcastAction.RECEIVED_MESSAGE);
		registerReceiver(mReceiver,filter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}


}
