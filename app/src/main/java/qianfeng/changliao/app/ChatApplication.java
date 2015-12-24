package qianfeng.changliao.app;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import qianfeng.changliao.adapter.ContactApdater;
import qianfeng.changliao.beans.ChatUser;
import qianfeng.changliao.net.http.HttpApi;
import qianfeng.changliao.net.socket.ChatEngine;
import qianfeng.changliao.utils.Logutils;
import qianfeng.changliao.utils.Pref_Utils;

import static com.android.volley.Request.Method.GET;

/**
 * Created by wukai on 15/12/10.
 */
public class ChatApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

	}

}
