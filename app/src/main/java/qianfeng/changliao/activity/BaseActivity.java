package qianfeng.changliao.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import qianfeng.changliao.app.ChatApplication;

/**
 * Created by wukai on 15/12/10.
 */
public abstract class BaseActivity extends FragmentActivity {

	protected ChatApplication mApp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApp = (ChatApplication) getApplication();
	}
	//吴凯

	public abstract void back(View view);



}
