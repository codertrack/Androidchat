package qianfeng.changliao.app;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by wukai on 15/11/3.
 * 内存缓存
 */
public class MemoryCache implements ImageLoader.ImageCache {

	private LruCache<String,Bitmap> mCache;

	public  MemoryCache(){
		//初始化缓存对象并设置容量大小
		/**
		 * maxsize的值代表容器的大小：
		 * 如果重写了Lrucache的sizeof方法的话，maxsize就代表容器所占用的字节数
		 * 如果没有重写sizeof方法的话，maxsize就代表容器所能容纳的对象个数
		 */

		mCache = new LruCache<String,Bitmap>(15*1024*1024){

			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getByteCount();
			}
		};

	}

	@Override
	public Bitmap getBitmap(String url) {
		//从缓存里面拿出一张对象

		return mCache.get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		//给缓存容器里面添加一个对象
			mCache.put(url,bitmap);
	}

}
