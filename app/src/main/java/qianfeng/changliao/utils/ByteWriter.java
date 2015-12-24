package qianfeng.changliao.utils;

/**
 * Created by wukai on 15/12/8.
 */
public class ByteWriter {


    /**
     *
     * @param msg 消息内容
     * @param value
     * @param off
     * @return
     */
    public static int writeUint8(byte[] msg,int off,int value){
        msg[off] = (byte)value;
        return off+1;
    }

    public static int writeUint16(byte[] msg,int off,int value){

        msg[off] = (byte) (value>>8);
        //刘德华
        msg[off+1] = (byte)value;
        return off+2;
    }

    public static int writeUint32(byte[] msg,int off,int value){

        msg[off] = (byte) (value>>24);
        msg[off+1]= (byte) (value>>16);
        msg[off+2]= (byte) (value>>8);
        msg[off+3]= (byte) value;
        //
        return off+4;
    }


    public static int writeBytes(byte[] msg,int off,byte[] con){
        System.arraycopy(con,0,msg,off,con.length);
        return off+con.length;

    }




}

