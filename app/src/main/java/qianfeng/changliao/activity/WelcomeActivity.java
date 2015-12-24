package qianfeng.changliao.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import qianfeng.changliao.R;
import qianfeng.changliao.app.BroadcastAction;
import qianfeng.changliao.app.UserManager;
import qianfeng.changliao.net.http.HttpApi;
import qianfeng.changliao.net.http.VolleyRequest;
import qianfeng.changliao.net.socket.ChannelMessage;
import qianfeng.changliao.net.socket.ChatEngine;
import qianfeng.changliao.net.socket.protocol.ProtocolHandler;
import qianfeng.changliao.utils.Logutils;

import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;


import static com.android.volley.Request.Method.GET;

public class WelcomeActivity extends BaseActivity {

	private LoginReceiver mReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		initReceiver();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (isLogined()) {
					//请求加载好友列表
					requestAllUser();

				} else {
					startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
					finish();
				}

			}
		}, 2000);

	}

	@Override
	public void back(View view) {}

	//判断用户是否曾经登录过
	private boolean isLogined(){
		String userid = UserManager.getCurrentUserid(this);
		return (userid != null && !userid.equals(""));
	}


	class LoginReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(BroadcastAction.LOGIN_SUCESS)){
				Toast.makeText(WelcomeActivity.this, "登录成功..", Toast.LENGTH_SHORT).show();
				startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
				finish();
			}
		}
	}

	public void initReceiver(){
		mReceiver = new LoginReceiver();
		IntentFilter intentFilter =new IntentFilter();
		intentFilter.addAction(BroadcastAction.LOGIN_SUCESS);
		registerReceiver(mReceiver, intentFilter);
	}




	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	//请求所有好友
	public void requestAllUser() {

		StringRequest req = new StringRequest(
				GET,
				HttpApi.API_ALL_USER,
				//刘德华
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Logutils.logd(response);
						//解析所有好友列表
						parseAllUsers(response);

						startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
						WelcomeActivity.this.finish();
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						error.printStackTrace();
					}
				}

		);
		VolleyRequest.getInstance(mApp).sendRequest(req);

	}


	public void parseAllUsers(String response){
		JSONObject jsonObject = JSON.parseObject(response);
		if (jsonObject == null)return ;
		if (jsonObject.getInteger("code") != 0)return;

		JSONObject result = jsonObject.getJSONObject("result");
		JSONArray array = result.getJSONArray("friends");
		if (array != null && array.size()!= 0){
			UserManager.getInstance().addUsers(array);
		}
	}

}
