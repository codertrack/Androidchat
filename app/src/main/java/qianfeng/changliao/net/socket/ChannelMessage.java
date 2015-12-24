package qianfeng.changliao.net.socket;


import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import qianfeng.changliao.utils.ByteReader;
import qianfeng.changliao.utils.ByteWriter;
import qianfeng.changliao.utils.ReadOff;

/**
 * Created by wukai on 15/12/9.
 * stack ---
 *
 * cmd 1byte--\content bytes
 * 1
 *
 */

public  class ChannelMessage {

    public String text;
    public int cmd;

    public static byte[] Message2Bytes(ChannelMessage message){

        byte[] text = new byte[0];
        try {
            text = message.text.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte[] msg  = new byte[1+text.length];

        int off = 0;
            //写入消息头
            //刘德华
            off = ByteWriter.writeUint8(msg, off, message.cmd);
            off = ByteWriter.writeBytes(msg, off,text);
        return msg;
    }


    public static ChannelMessage Bytes2Message(byte[] data) {
        ReadOff ro = new ReadOff(0);
        ChannelMessage cm = new ChannelMessage();
        cm.cmd = ByteReader.readUint8(data, ro);
        byte[] wrap = ByteReader.readBytes(data, ro,data.length-1);
        try {
            cm.text = new String(wrap,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return cm;

    }

}
