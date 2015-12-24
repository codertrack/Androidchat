package qianfeng.changliao.adapter;

import android.content.Context;
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
import qianfeng.changliao.beans.Conversation;
import qianfeng.changliao.net.http.VolleyImageLoader;

/**
 * Created by wukai on 15/12/17.
 */
public class ConversationApdater extends BaseAdapter{

	private List<Conversation> mUsers;

	private Context mContext;

	public ConversationApdater(Context context, List<Conversation> cons){
		mContext = context;
		mUsers = cons;
	}
	@Override
	public int getCount() {
		return mUsers == null ?0:mUsers.size();
	}

	@Override
	public Conversation getItem(int position) {
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
			holder.lastMessage = (TextView)convertView.findViewById(R.id.tv_contact_shuoshuo);
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();

		Conversation conversation = getItem(position);
		//withUserid
		ChatUser user = UserManager.getInstance().getChatUser(conversation.withUserid);
		//设置用户默认头像
		holder.imageView.setDefaultImageResId(R.drawable.default_useravatar);
		//加载用户头像
		VolleyImageLoader.getInstance(mContext).loadImage(holder.imageView, user.avart);
		//设置当前聊天的用户名
		holder.username.setText(conversation.withUsername);
		//填充最后一条聊天信息
		holder.lastMessage.setText(conversation.lastMessage);

		return convertView;
	}

	class ViewHolder{
		public TextView username;
		public TextView lastMessage;
		public NetworkImageView imageView;
	}
}
