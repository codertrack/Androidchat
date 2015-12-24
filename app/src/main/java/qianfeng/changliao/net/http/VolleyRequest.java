package qianfeng.changliao.net.http;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by wukai on 15/10/14.
 */

/**
 * 用于加载数据的单例Volley封装类
 *
 */
public class VolleyRequest {


	private static VolleyRequest mInstance ;

	private RequestQueue mQueue;

	private VolleyRequest(Context context){
		mQueue = Volley.newRequestQueue(context);
	}

	public static   VolleyRequest getInstance(Context context){
		if (mInstance ==null){
			synchronized (VolleyRequest.class){
				if (mInstance == null){
					mInstance = new VolleyRequest(context);
				}
			}
		}
	return mInstance;
	}


	//发送请求
	public void sendRequest(Request request){
		mQueue.add(request);
	}

	//清空缓存
	public void clearCache(){
		mQueue.getCache().clear();
	}



}
