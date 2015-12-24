package qianfeng.changliao.net.http;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.ImageLoader.ImageCache;
/**
 * Created by wukai on 15/10/14.
 */
public class VolleyImageLoader extends LruCache<String,Bitmap>
		 implements ImageCache {

	//定义请求队列成员
	private RequestQueue mQueue;

	//定义ImageLoader成员变量
	private ImageLoader mLoader;

	//缓存使用的最大内存占用10m
	private static final int CACHESIZE =10*1024*1024;

	private VolleyImageLoader(int maxsize,Context context){


		super(maxsize);//初始化缓存对象
		//实例化请求队列
		mQueue = Volley.newRequestQueue(context);
		//初始化ImageLoader
		mLoader = new ImageLoader(mQueue,this);

	}


	@Override
	public Bitmap getBitmap(String url) {
		//从内存缓存中得到一张图片返回
		return get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		//往缓存里面添加一张图片
		put(url,bitmap);
	}


	@Override
	protected int sizeOf(String key, Bitmap bm) {
		//获取每一张图片所占用的内存大小
		return bm.getRowBytes()*bm.getHeight();
	}


	//初始化
	private static VolleyImageLoader mInstance;

	public static VolleyImageLoader getInstance(Context context){

		//懒汉式的初始化对象方式
		if (mInstance == null){
			synchronized (VolleyImageLoader.class){
				if (mInstance == null){
					mInstance = new VolleyImageLoader(CACHESIZE,context);
				}

			}
		}
		return  mInstance;
	}


	//加载图片方法
	public void loadImage(NetworkImageView view,String url){
		view.setImageUrl(url,mLoader);
	}


	public void startLoad(){
		mQueue.start();;
	}

	public void pauseLoad(){
		mQueue.stop();
	}

}
