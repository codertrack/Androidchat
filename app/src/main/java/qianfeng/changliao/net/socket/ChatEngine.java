package qianfeng.changliao.net.socket;

import android.content.Context;
import android.os.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import qianfeng.changliao.net.socket.protocol.ChatMessage;
import qianfeng.changliao.net.socket.protocol.ProtocolHandler;
import qianfeng.changliao.utils.Logutils;

/**
 * Created by wukai on 15/12/10.
 */
public class ChatEngine {

	//无链接
	private int STATE_IDLE = 0;
	//链接中
	private int STATE_CONNECTING = 1;
	//链接成功
	private int STATE_CONNECTED = 2;

	//消息头
	public static final int MSG_RECEIVED = 1;

	public static final int MSG_CONNECTED = 2;

	public static final int MSG_CONN_LOST = 3;


	private Selector mSelector;

	private String SERVER_ADDR = "192.168.57.1";

	private int SERVER_PORT = 9999;


	private int mState =STATE_IDLE;

	//重连次数
	private final int RECONN = 3;

	private SocketChannel mSocketChannel;
	private UserChannel mUserChannel;

	private SelectorDispatcher mDispatcher;

	public ChatEngine(Context context){
		//初始化用户通道
		mContext =context;
		mUserChannel = new UserChannel(mContext);
		connectServer();
	}

	private void initServer(){
		InetSocketAddress ia = new InetSocketAddress(SERVER_ADDR,SERVER_PORT);

		try {
			mSocketChannel = SocketChannel.open();
			mSocketChannel.configureBlocking(false);
			//打开事件处理器
			mSelector = Selector.open();
			//注册连接事件⌚️
			mSocketChannel.register(mSelector, SelectionKey.OP_CONNECT);
			//链接服务器
			mState = STATE_CONNECTING;
			mSocketChannel.connect(ia);


		} catch (IOException e) {
			e.printStackTrace();
			Logutils.logd("链接异常");
		}

	}


	public class SelectorDispatcher extends Thread{

		public boolean readFlag = true;

		public SelectorDispatcher (){
			setName("SelectorDispatcher-client");
		}

		@Override
		public void run() {
			//接收缓冲区
			ByteBuffer buffer = ByteBuffer.allocate(1024*5);
			//初始化链接
			initServer();
			//读取事件
			while (readFlag){
				selectEvent(buffer);
			}
		}

		public void selectEvent(ByteBuffer buf){
			try {
				int count = mSelector.select();

				if (count<=0)return;

				Set<SelectionKey> keys =mSelector.selectedKeys();
				for (SelectionKey selectionKey: keys){

					if (selectionKey.isConnectable()){
						//注册消息事件
						if (mSocketChannel.isConnectionPending()){
							//完成链接
							mSocketChannel.finishConnect();
							mSocketChannel.register(mSelector, SelectionKey.OP_READ);
							Logutils.logd("连接成功.....");
							//初始化收发通道
							//刘德华
							mState = STATE_CONNECTED;

							mUserChannel.initChannel(mSocketChannel);
							//发送连接成功消息
							Message msg = Message.obtain();
							msg.what = MSG_CONNECTED;
							mUserChannel.getHandler().sendMessage(msg);

						}

					}else if (selectionKey.isReadable()){

						int len = mSocketChannel.read(buf);

						if (len == -1){ //断开连接
							onDisconnected();
							return;
						}
						if (len == 0){ //没有消息接收
							return;
						}
						Logutils.logd("receive bytes len = "+len);
						byte[] content = new byte[len];
						//移动读指针
						buf.flip();
						//获取内容
						buf.get(content, 0, len);
						//获取message
						Message msg = Message.obtain();

						msg.what = MSG_RECEIVED;
						msg.obj = content;
						mUserChannel.getHandler().sendMessage(msg);
						//清空缓冲区
						buf.clear();
					}
					keys.remove(selectionKey);
				}
			} catch (IOException e) {
				e.printStackTrace();
				Logutils.logd("selector .....error");
			}

		}
	}

	private static ChatEngine mInstance;

	private Context mContext;

	/**
	 * 必须在主线成中调用
	 * @param context
	 * @return
	 */
	public static ChatEngine getInstance(Context context){
		if (Thread.currentThread().getId() != 1){
			throw new RuntimeException("must init ChatEngine in Main Thread");
		}

		if (mInstance == null){
			//刘德华
			synchronized (ChatEngine.class){
				if (mInstance == null){
					mInstance = new ChatEngine(context);
				}
			}
		}
		return mInstance;
	}


	public  void sendChatMessage(ChatMessage message){

		ChannelMessage cm = new ChannelMessage();
		cm.text = ChatMessage.parse2JsonObject(message).toString();
		cm.cmd = ProtocolHandler.CMD_CHAT;

		mUserChannel.sendChannelMessage(cm);
		//连接服务器
		if (mState == STATE_IDLE){
			connectServer();
		}

	}

	//当连接断开的时候
	public void onDisconnected(){
		Logutils.logd("onDisconnected");
		mState = STATE_IDLE;//设置连接状态
		mDispatcher.readFlag = false; //销毁线程
	}


	private void connectServer(){
		mDispatcher = new SelectorDispatcher();
		mDispatcher.start();
	}


}
