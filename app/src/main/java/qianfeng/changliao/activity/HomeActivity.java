package qianfeng.changliao.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import qianfeng.changliao.R;
import qianfeng.changliao.app.BroadcastAction;
import qianfeng.changliao.fragment.ContactsFragment;
import qianfeng.changliao.fragment.ConversationFragMent;
import qianfeng.changliao.fragment.ProfileFragment;
import qianfeng.changliao.net.socket.ChatEngine;

public class HomeActivity extends BaseActivity  {


	private Fragment mCurrentFrag;

	private FragmentManager mFragmentManager;

	private TextView mTV_Tile;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		mFragmentManager =getSupportFragmentManager();
		ChatEngine.getInstance(this);
		initView();
		initReceiver();
	}


	@Override
	public void back(View view) {
		onBackPressed();
	}

	public void initView(){
		mTV_Tile = (TextView) findViewById(R.id.title_text);
		mTV_Tile.setText("会话");
		showFragment("chatlist");
		mPreTab = findViewById(R.id.re_weixin);
		mPreTab.setSelected(true);
	}


	public void showFragment(String tag){

		Fragment fragment  = mFragmentManager.findFragmentByTag(tag);
		if (fragment == null){
			fragment = initFragmentWithTag(tag);
			mFragmentManager.beginTransaction().add(R.id.fragment_container, fragment, tag).commit();
		}

		if (mCurrentFrag == fragment)return;
		if (mCurrentFrag != null){
			mFragmentManager.beginTransaction().hide(mCurrentFrag).commit();
		}

		mFragmentManager.beginTransaction().show(fragment).commit();
		mCurrentFrag = fragment;
	}


	public Fragment initFragmentWithTag(String tag){
		Fragment fragment = null;
		if (tag.equals("chatlist")){
			fragment = new ConversationFragMent();
		}else if (tag.equals("profile")){
			fragment = new ProfileFragment();
		}else if (tag.equals("online")){
			fragment = new ContactsFragment();
		}
		return fragment;
	}


	//刘德华
	private View mPreTab;

	public void onTabClicked(View v) {
		int vid  = v.getId();
		if (vid==R.id.re_weixin){
			mTV_Tile.setText("会话");

			showFragment("chatlist");
		}else if (vid == R.id.re_profile){
			showFragment("profile");
			mTV_Tile.setText("个人设置");
		}else if (vid == R.id.re_contact_list){
			showFragment("online");
			mTV_Tile.setText("联系人");
		}

		if (mPreTab != null){
			mPreTab.setSelected(false);
		}
		v.setSelected(true);
		mPreTab =v;
	}


	private MessageReceiver mReceiver;
	//广播接收器
	class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(BroadcastAction.RECEIVED_MESSAGE)){
//				String message = intent.getStringExtra("msg");
//				ChatMessage chatMessage = ChatMessage.initWithJsonObject(JSON.parseObject(message));
//				if (chatMessage.toUserid.equals(withUserid)){
//				//终止继续传递
				abortBroadcast();
//				}

			}
		}
	}

	private void initReceiver(){
		mReceiver = new MessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(99);
		filter.addAction(BroadcastAction.RECEIVED_MESSAGE);
		registerReceiver(mReceiver,filter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

}

