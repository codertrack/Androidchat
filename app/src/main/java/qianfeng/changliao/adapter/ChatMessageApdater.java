package qianfeng.changliao.adapter;

import android.content.Context;
import android.os.Message;
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
import qianfeng.changliao.beans.MessageBean;
import qianfeng.changliao.net.http.VolleyImageLoader;
import qianfeng.changliao.net.socket.protocol.ChatMessage;
import qianfeng.changliao.utils.Logutils;
import qianfeng.changliao.utils.TimeUtils;

/**
 * Created by wukai on 15/12/17.
 *
 */
public class ChatMessageApdater extends BaseAdapter{

	private Conversation mConversation;

	private List<MessageBean> mMessages;
	private Context mContext;

	public ChatMessageApdater(Context context, Conversation conversation){
		mContext = context;
		mMessages = conversation.getMessages();
	}


	@Override
	public int getCount() {
		return mMessages== null ?0:mMessages.size();
	}

	@Override
	public MessageBean getItem(int position) {
		return mMessages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getViewTypeCount() {
		return 3;
	}

	@Override
	public int getItemViewType(int position) {
		return getItem(position).received == MessageBean.Type.RECEIVED?1:2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		if (getItemViewType(position) == 1){
			if (convertView == null){
				//刘德华
				convertView = LayoutInflater.from(mContext).inflate(R.layout.row_received_message,parent,false);
				holder = new ViewHolder();
				holder.imageView = (NetworkImageView) convertView.findViewById(R.id.iv_chat_avart);
				holder.content = (TextView)convertView.findViewById(R.id.tv_chatcontent);
				holder.time = (TextView)convertView.findViewById(R.id.timestamp);
				convertView.setTag(holder);
			}

		}else if (getItemViewType(position) ==2){
			//刘德华
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.row_sent_message, parent, false);
				holder = new ViewHolder();
				holder.imageView = (NetworkImageView) convertView.findViewById(R.id.iv_chat_avart);
				holder.content = (TextView)convertView.findViewById(R.id.tv_chatcontent);
				holder.time = (TextView)convertView.findViewById(R.id.timestamp);
				convertView.setTag(holder);
			}
		}


		holder = (ViewHolder) convertView.getTag();
		MessageBean message = getItem(position);
		holder.imageView.setDefaultImageResId(R.drawable.default_useravatar);
		String avart = UserManager.getInstance().getChatUser(message.msg.fromUserid).avart;
		VolleyImageLoader.getInstance(mContext).loadImage(holder.imageView, avart);
		holder.content.setText(message.msg.text);
		holder.time.setText(TimeUtils.getTime(Long.parseLong(message.msg.time)));

		return convertView;
	}


	class ViewHolder{
		public TextView time;
		public TextView content;
		public NetworkImageView imageView;
	}


}
