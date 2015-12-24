package qianfeng.changliao.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;

import java.util.List;

import qianfeng.changliao.R;
import qianfeng.changliao.activity.ChatActivity;
import qianfeng.changliao.adapter.ConversationApdater;
import qianfeng.changliao.app.BroadcastAction;
import qianfeng.changliao.app.MessageManager;
import qianfeng.changliao.app.UserManager;
import qianfeng.changliao.beans.Conversation;
import qianfeng.changliao.beans.MessageBean;
import qianfeng.changliao.net.socket.protocol.ChatMessage;
import qianfeng.changliao.utils.Logutils;

/**
 * Created by wukai on 15/12/16.
 */
public class ConversationFragMent extends Fragment implements AdapterView.OnItemClickListener {

	private ListView mListView;

	private List<Conversation> mConvers;

	private ConversationApdater mAdapter;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.frag_conversation,null);
		mListView = (ListView) view.findViewById(R.id.list);
		mListView.setOnItemClickListener(this);
		mConvers = MessageManager.getInstance().getAllConversation();
		mAdapter = new ConversationApdater(getActivity(),mConvers);
		mListView.setAdapter(mAdapter);
		MessageManager.getInstance().addChangeObserver(new MessageChangeObServer());
		return view;

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(getActivity(), ChatActivity.class);
		intent.putExtra("userid", mAdapter.getItem(position).withUserid);
		intent.putExtra("username", mAdapter.getItem(position).withUsername);
		startActivity(intent);
	}

	//广播接收器
	class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(BroadcastAction.RECEIVED_MESSAGE)){
					mAdapter.notifyDataSetChanged();
					abortBroadcast();
				}
			}
		}


	@Override
	public void onDetach() {
		super.onDetach();
	}


	@Override
	public void onResume() {
		super.onResume();
		Logutils.logd("convers:" + mConvers.size());
		mAdapter.notifyDataSetChanged();
	}


	class MessageChangeObServer implements MessageManager.OnConversationChange{
		@Override
		public void onAddConversation(String withuserid) {
			mAdapter.notifyDataSetChanged();
			Logutils.logd("onAddConversation");
		}

		@Override
		public void onAddMessage(String withUserid) {
			mAdapter.notifyDataSetChanged();
		}

	}



}
