package qianfeng.changliao.net.socket;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import qianfeng.changliao.app.UserManager;
import qianfeng.changliao.net.socket.protocol.ChannelBuffer;
import qianfeng.changliao.net.socket.protocol.FrameType;
import qianfeng.changliao.net.socket.protocol.ProtocolHandler;
import qianfeng.changliao.net.socket.protocol.ProtocolFrame;
import qianfeng.changliao.utils.ByteReader;
import qianfeng.changliao.utils.Logutils;
import qianfeng.changliao.utils.ReadOff;

/**
 * Created by wukai on 15/12/10.
 */
public class UserChannel implements Handler.Callback {


	public SocketChannel mSocketChannel;

	public String userid;

	public Handler mHandler;

	public HandlerThread mMessageLooper;

	public Queue<ProtocolFrame> mFrames;


	public volatile ChannelBuffer mBuffer;

	public int sequnce= 0;

	private Context mContext;

	//协议栈处理
	public ProtocolHandler mProHandler;
	//缓存读取标记
	private ReadOff readOff = new ReadOff(0);


	public UserChannel(Context context){
		mContext =context;
		mFrames = new PriorityQueue<>();
		mBuffer = new ChannelBuffer(1024*10);
		mProHandler = new ProtocolHandler(mContext);

	}

	@Override
	public boolean handleMessage(Message msg) {
		if (msg.what == ChatEngine.MSG_RECEIVED){
			onReceivedData((byte[]) msg.obj);
		} else if (msg.what == ChatEngine.MSG_CONN_LOST){

		}else if (msg.what == ChatEngine.MSG_CONNECTED){
			//初始化服务器通道
			sendInitMessage();
		}

		//return true 终止handleMessage方法的处理
		return true;
	}


	public void onReceivedData(byte[] data){

		synchronized (mBuffer){
			if (mBuffer.writeData(data)){
				processFrame();
			};
		}

	}

	//处理协议
	public void processFrame(){

		try {

			byte[] frameHead = mBuffer.readData(ProtocolFrame.FRAME_HEAD_LEN);
			while (frameHead != null){
				ProtocolFrame pf = new ProtocolFrame();
				pf.version = ByteReader.readUint8(frameHead, readOff);
				pf.frameType = ByteReader.readUint8(frameHead, readOff);
				pf.sequnce = ByteReader.readUint32(frameHead, readOff);
				pf.conLen = ByteReader.readUint32(frameHead, readOff);
				int crc = ByteReader.readUint8(frameHead, readOff);
				//重置偏移量标记
				readOff.setValue(0);
			//刘德华
				Logutils.logd("received:frame :" +
							"version->" + pf.version +
							"/sequnce->" + pf.sequnce +
							"/frametype->" + pf.frameType +
							"/conLen->" + pf.conLen+
								"/from crc->"+crc
			);

				if (crc != (ProtocolFrame.getCheckCrc(frameHead)&0xff)) {

					System.out.println("校验位错误");
					//移动帧头长度
					mBuffer.moveReadPostion(ProtocolFrame.FRAME_HEAD_LEN);
				} else if (pf.frameType == FrameType.ACK) {
					//移动帧头长度
					System.out.println("确认帧 sequnce->"+sequnce);
					mBuffer.moveReadPostion(ProtocolFrame.FRAME_HEAD_LEN);
					//帧序递增1
					sequnce++;
					if (sequnce == 1000){
					sequnce = 0;
					}

					sendNextFrame();

				}else if (pf.frameType == FrameType.USER_MSG){
					//读取内容长度
					int available = mBuffer.getAvailableReadLen();
					//如果内容字节不够则不处理指针不移动
					if (available<pf.conLen+ProtocolFrame.FRAME_HEAD_LEN)return;
					//移动枕头长度的距离
					mBuffer.moveReadPostion(ProtocolFrame.FRAME_HEAD_LEN);
					sendAckFrame(pf.sequnce);
					byte[] content = mBuffer.readData(pf.conLen);
					//移动内容长度的距离
					mBuffer.moveReadPostion(pf.conLen);
					ChannelMessage cm =ChannelMessage.Bytes2Message(content);
					mProHandler.handleCmd(cm.cmd,cm.text);
			}

				frameHead = mBuffer.readData(ProtocolFrame.FRAME_HEAD_LEN);
		}

	} catch (Exception e) {
		e.printStackTrace();
	}

	}

	/**
	 * 发送下一帧数据
	 */
	private void sendNextFrame(){

		synchronized (mFrames){
				ProtocolFrame pf = mFrames.poll();
				if (pf == null)return;
				pf.sequnce = sequnce;
			    Logutils.logd("sequnce->"+sequnce);
				pf.version =1;
				//刘德华
			try {
				byte[] msg  = ProtocolFrame.getFrameBytes(pf);
				//刘德华
				int len = mSocketChannel.write(ByteBuffer.wrap(msg));
				mSocketChannel.socket().getOutputStream().flush();

				Logutils.logd("send bytes count="+len);
			} catch (IOException e) {
				e.printStackTrace();
				Logutils.logd("send Error.....");
			}
		}
	}


	public Handler getHandler(){
		return  mHandler;
	}


	//初始化链接通道
	public void initChannel(SocketChannel socketChannel){
		mSocketChannel = socketChannel;
		if (mMessageLooper != null &&mMessageLooper.isAlive())return;
		mMessageLooper = new HandlerThread("Userchannel");
		mMessageLooper.start();

		Looper looper = mMessageLooper.getLooper();
		mHandler = new Handler(looper,this);
	}

	/**
	 * 发送确认帧
	 */
	private void sendAckFrame(int seq){
		ProtocolFrame pf = new ProtocolFrame();
		pf.version = 1;
		pf.frameType = FrameType.ACK;
		pf.sequnce =seq;
		try {

			byte[] msg  = ProtocolFrame.getAckFrameBytes(pf);
			mSocketChannel.write(ByteBuffer.wrap(msg));
			mSocketChannel.socket().getOutputStream().flush();

			Logutils.logd("client -send Ack...");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(ProtocolFrame pf){
		synchronized (mFrames){
			//刘德华

			mFrames.add(pf);
			if (mSocketChannel != null && mSocketChannel.isConnected()) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						sendNextFrame();
					}
				});
			}

		}
	}



	/**
	 *
	 */
	public  void sendInitMessage(){
		//刘德华
		ChannelMessage cm = new ChannelMessage();
		cm.cmd = ProtocolHandler.CMD_INIT;
		cm.text = UserManager.getCurrentUserid(mContext);
		//sendChannelMessage(cm);
		//封装底层协议消息
		ProtocolFrame pf = new ProtocolFrame();
		//协议版本号
		pf.version = 1;
		//帧类型
		pf.frameType = FrameType.USER_MSG;
		pf.content = ChannelMessage.Message2Bytes(cm);

		pf.priority = 1;
		sendMessage(pf);
	}


	public void sendChannelMessage(ChannelMessage message){
		//封装底层协议消息
		ProtocolFrame pf = new ProtocolFrame();
		//协议版本号
		pf.version = 1;
		//帧类型
		pf.frameType = FrameType.USER_MSG;
		pf.content = ChannelMessage.Message2Bytes(message);
		sendMessage(pf);
	}

}
