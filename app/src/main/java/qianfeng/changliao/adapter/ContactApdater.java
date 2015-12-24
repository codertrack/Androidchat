package qianfeng.changliao.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import qianfeng.changliao.R;
import qianfeng.changliao.app.UserManager;
import qianfeng.changliao.beans.ChatUser;
import qianfeng.changliao.net.http.VolleyImageLoader;

/**
 * Created by wukai on 15/12/17.
 */
public class ContactApdater extends BaseAdapter{

	private List<ChatUser> mUsers;

	private Context mContext;

	public ContactApdater(Context context,List<ChatUser> users){
		mContext = context;
		mUsers = users;
	}
	@Override
	public int getCount() {
		return mUsers == null ?0:mUsers.size();
	}

	@Override
	public ChatUser getItem(int position) {
		return mUsers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null){
			//刘德华
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_chatuser,parent,false);
			holder = new ViewHolder();
			holder.imageView = (NetworkImageView) convertView.findViewById(R.id.iv_avatar);
			holder.username = (TextView) convertView.findViewById(R.id.tv_contact_name);
			holder.shuoshuo = (TextView)convertView.findViewById(R.id.tv_contact_shuoshuo);

			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();
		ChatUser chatUser = getItem(position);

		holder.imageView.setDefaultImageResId(R.drawable.default_useravatar);
		//只加载在线用户的头像
		boolean isOnline = UserManager.getInstance().isOnline(chatUser.userid);
		VolleyImageLoader.getInstance(mContext).loadImage(holder.imageView, chatUser.avart);

		if (isOnline){
			holder.username.setText(chatUser.username+"/在线");
			holder.username.setTextColor(Color.RED);
		}else {
			holder.username.setText(chatUser.username+"/离线");
			holder.username.setTextColor(Color.DKGRAY);
		}

		holder.shuoshuo.setText(chatUser.shuoshuo);
		return convertView;
	}


	class ViewHolder{
		public TextView username;
		public TextView shuoshuo;
		public NetworkImageView imageView;
	}
}
