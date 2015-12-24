package qianfeng.changliao.net.socket.protocol;


import qianfeng.changliao.utils.ByteWriter;
import qianfeng.changliao.utils.Logutils;

/**
 * Created by wukai on 15/12/8.
 * 消息桢
 * 刘德华
 *  \-version  1byte-\-type- 1byte\-sequnce- 4byte\-conlen 4byte-\-crc 1byte-\
 *
 */

public class ProtocolFrame implements Comparable<ProtocolFrame> {


    public int priority = 0;
    //消息帧头长度
    public static final int FRAME_HEAD_LEN = 11;

    //消息正文
    public byte[] content;

    //消息长度 4byte
    public int conLen;

    //桢序 4byte
    public int sequnce;

    //消息类型 确认消息,聊天内容,系统消息 1byte
    public int frameType;

    //协议版本号 默认是1 1byte
    public int version;

    //把消息转化为字节数组
    public static byte[] getFrameBytes(ProtocolFrame frame){
        //消息帧的长度=消息帧头长度+正文长度
        byte[] msg = new byte[FRAME_HEAD_LEN+frame.content.length];
        int off = 0;

        //写入帧协议版本
        off = ByteWriter.writeUint8(msg, off, frame.version); //1
        //写入帧类型
        off = ByteWriter.writeUint8(msg,off,frame.frameType);//1
        //写入帧序号
        off = ByteWriter.writeUint32(msg,off,frame.sequnce);//4
        //写入帧内容长度
        off = ByteWriter.writeUint32(msg,off,frame.content.length);//4

        int crc =getCheckCrc(msg);
        Logutils.logd("write crc-->"+crc);
        //写入帧校验指
        off = ByteWriter.writeUint8(msg, off, crc);//1

        off = ByteWriter.writeBytes(msg, off, frame.content);
        Logutils.logd("off->"+off+"content len->"+frame.content.length);
        return msg;
    }

    public static int getCheckCrc(byte[] msg){
        return msg[0]^msg[1]^msg[2]^msg[3]^msg[4]^msg[5]^msg[6]^msg[7]^msg[8]^msg[9];
    }


    //把消息转化为字节数组
    public static byte[] getAckFrameBytes(ProtocolFrame frame){
        //消息帧的长度=消息帧头长度+正文长度

        byte[] msg = new byte[FRAME_HEAD_LEN];
        int off = 0;

        //写入帧协议版本
        off = ByteWriter.writeUint8(msg,off,frame.version);
        //写入帧类型
        off = ByteWriter.writeUint8(msg,off,frame.frameType);
        //写入帧序号
        off = ByteWriter.writeUint32(msg,off,frame.sequnce);
        //写入帧内容长度
        off = ByteWriter.writeUint32(msg,off,0);

        int crc =getCheckCrc(msg);
        //写入帧校验指
        off = ByteWriter.writeUint8(msg,off,crc);
        return msg;
    }

    @Override
    public int compareTo(ProtocolFrame another) {
        return another.priority-priority;
    }
}
