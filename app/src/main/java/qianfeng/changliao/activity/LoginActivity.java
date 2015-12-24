package qianfeng.changliao.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import qianfeng.changliao.R;
import qianfeng.changliao.app.BroadcastAction;
import qianfeng.changliao.app.UserManager;
import qianfeng.changliao.beans.ChatUser;
import qianfeng.changliao.net.http.HttpApi;
import qianfeng.changliao.net.http.VolleyRequest;
import qianfeng.changliao.net.socket.ChannelMessage;
import qianfeng.changliao.net.socket.ChatEngine;
import qianfeng.changliao.net.socket.protocol.ProtocolHandler;
import qianfeng.changliao.utils.Logutils;

import static com.android.volley.Request.Method.POST;

/**
 * Created by wukai on 15/12/11.
 *
 * 用户登录简单的登录，
 * 输入用户名即可 用户密码都是123456
 * 作用是从服务器返回一个userid给客户端，客户端通过这个userid与聊天服务器进行通讯
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

	private EditText mET_Name,mET_Pass;

	private Button mBtn_Login;

	private BroadcastReceiver mReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initView();
	}



	private void initView(){
		mET_Name = (EditText) findViewById(R.id.et_username);
		mET_Pass = (EditText) findViewById(R.id.et_password);
		mBtn_Login = (Button) findViewById(R.id.btn_login);
		mBtn_Login.setOnClickListener(this);
		findViewById(R.id.btn_register).setOnClickListener(this);

	}

	@Override
	public void back(View view) {
		onBackPressed();
	}


	private void onLogin(){
		String username = mET_Name.getText().toString().trim();
		String password = mET_Pass.getText().toString().trim();
		if (username != null
				&& username.length() !=0
				&& password != null
				&& password.length() != 0
				){
			requestLogin(username, password);
			//丁香花

		}else {
			Toast.makeText(this,"请输入用户名或者密码",Toast.LENGTH_SHORT).show();
		}
	}


	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_login){
			onLogin();
		}else if (v.getId() == R.id.btn_register){
			startActivity(new Intent(this,RegisterActivity.class));
		}
	}

	//用户登录
	private void requestLogin(final String account,final String passwd){
		StringRequest request = new StringRequest(
				POST,
				HttpApi.API_LOGIN,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Logutils.logd(response);
						JSONObject json = JSON.parseObject(response);
						if (json != null) {
							if (json.getIntValue("code") != 0) return;
							JSONObject result = json.getJSONObject("result");
							JSONArray jsonArray = result.getJSONArray("friends");

							UserManager.setCurrentUserid(mApp, account);
							//加载好友列表
							UserManager.getInstance().addUsers(jsonArray);
							//跳转
							startActivity(new Intent(LoginActivity.this,HomeActivity.class));
							LoginActivity.this.finish();
						}
					}
				},

				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						System.out.println("error:"+error.getMessage());
					}
				}
		){
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				HashMap<String,String> param = new HashMap<>();
				param.put("account",account);
				param.put("password",passwd);
				return param;
			}
		};

		VolleyRequest.getInstance(mApp).sendRequest(request);

	}


}
