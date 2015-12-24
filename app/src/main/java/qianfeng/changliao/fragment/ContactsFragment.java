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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;



import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import qianfeng.changliao.R;
import qianfeng.changliao.activity.ChatActivity;
import qianfeng.changliao.adapter.ContactApdater;
import qianfeng.changliao.app.BroadcastAction;
import qianfeng.changliao.app.UserManager;
import qianfeng.changliao.beans.ChatUser;
import qianfeng.changliao.net.http.HttpApi;
import qianfeng.changliao.net.http.VolleyRequest;
import qianfeng.changliao.utils.Logutils;

import static com.android.volley.Request.Method.GET;
/**
 * Created by wukai on 15/12/16.
 */
public class ContactsFragment extends Fragment implements AdapterView.OnItemClickListener {

	private ListView mListView;

	private List<ChatUser> mFriends;

	private ContactApdater mAdapter;


	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.frag_contactlist,null);
		mListView = (ListView) view.findViewById(R.id.list);
		mListView.setOnItemClickListener(this);
		initReceiver();
		mFriends = UserManager.getInstance().getAllFriends(UserManager.getCurrentUserid(getActivity()));
		mAdapter = new ContactApdater(getActivity(),mFriends);

		mListView.setAdapter(mAdapter);
		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		Intent intent = new Intent(getActivity(), ChatActivity.class);
		intent.putExtra("userid", mAdapter.getItem(position).userid);
		intent.putExtra("username", mAdapter.getItem(position).username);
		startActivity(intent);

	}

	//广播接收器
	class OnLineReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(BroadcastAction.USER_OFFLINE)){

			}else if (action.equals(BroadcastAction.USER_ONLINE)){

			}
			//在线的显示在前面
			Collections.sort(mFriends, new Comparator<ChatUser>() {
				@Override
				public int compare(ChatUser lhs, ChatUser rhs) {
					int sum = 0;
					if (mFriends.contains(lhs.userid)) {
						sum -= 1;
					}
					if (mFriends.contains(rhs.userid)) {
						sum += 1;
					}
					return sum;
				}
			});
			mAdapter.notifyDataSetChanged();
		}
	}

	private OnLineReceiver mReceiver;

	private void initReceiver(){
		mReceiver = new OnLineReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(BroadcastAction.USER_OFFLINE);
		filter.addAction(BroadcastAction.USER_ONLINE);
		getActivity().registerReceiver(mReceiver,filter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(mReceiver);
	}


	@Override
	public void onResume() {
		super.onResume();
	}
}
