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
import qianfeng.changliao.net.http.HttpApi;
import qianfeng.changliao.net.http.VolleyRequest;
import qianfeng.changliao.net.socket.ChannelMessage;
import qianfeng.changliao.net.socket.ChatEngine;
import qianfeng.changliao.net.socket.protocol.ProtocolHandler;
import qianfeng.changliao.utils.Logutils;

import static com.android.volley.Request.Method.POST;

/**
 *
 * 从服务器返回一个userid给客户端，客户端通过这个userid与聊天服务器进行通讯
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {

	private EditText mET_Name,mET_Pass,mET_Repass;

	private Button mBtn_Reg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reg);
		initView();
	}


	private void initView(){
		mET_Name = (EditText) findViewById(R.id.et_username);
		mET_Pass = (EditText) findViewById(R.id.et_password);
		mET_Repass = (EditText) findViewById(R.id.et_retype);

		mBtn_Reg = (Button) findViewById(R.id.btn_reg);
		mBtn_Reg.setOnClickListener(this);
	}

	@Override
	public void back(View view) {
		onBackPressed();
	}


	private void onRegister(){
		String username = mET_Name.getText().toString().trim();
		String password = mET_Pass.getText().toString().trim();
		String repasswd = mET_Repass.getText().toString().trim();
		//验证输入信息
		if (username != null
				&& username.length() !=0
				&& password != null
				&& password.length() != 0
				&& repasswd != null
				&& repasswd.length() != 0
				){

			if (!repasswd.equals(password)){
				Toast.makeText(this,"两次密码输入不一致",Toast.LENGTH_SHORT).show();
				return;
			}
			requestRegister(username, password);
			//丁香花
		}

	}


	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_reg){
			onRegister();
		}
	}

	//用户登录
	private void requestRegister(final String username,final String passwd){
		StringRequest request = new StringRequest(
				POST,
				HttpApi.API_REG,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Logutils.logd(response);
						JSONObject json = JSON.parseObject(response);

						if (json != null){
							if (json.getIntValue("code") !=0)return;
							JSONObject result = json.getJSONObject("result");


							if (result == null)return;
							String account = result.getString("account");
							if (account != null && account.length() != 0){
								Logutils.logd("account->" + account);
								//保存当前用户
								UserManager.setCurrentUserid(mApp, account);

								JSONArray array = json.getJSONArray("friends");
								if (array == null)return;
								UserManager.getInstance().addUsers(array);

								Toast.makeText(RegisterActivity.this, "注册成功..请使用"+account+"账号登录客户端", Toast.LENGTH_SHORT).show();
								//关闭activity
								Intent home = new Intent(RegisterActivity.this,HomeActivity.class);
								startActivity(home);
								RegisterActivity.this.finish();
							}
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
				param.put("username",username);
				param.put("password",passwd);
				return param;
			}
		};

		VolleyRequest.getInstance(mApp).sendRequest(request);

	}



}
