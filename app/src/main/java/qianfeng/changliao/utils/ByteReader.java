package qianfeng.changliao.utils;

/**
 * Created by wukai on 15/12/8.
 */
public class ByteReader {


    /**
     * 读取字节数组
     * @param msg
     * @param off
     * @param len
     * @return
     */
    public static byte[] readBytes(byte[] msg,ReadOff off,int len){
        byte[] buf = new byte[len];
        System.arraycopy(msg,off.getValue(),buf,0,len);
        return buf;
    }


    /**
     * 读取8bit 整型
     * @param msg
     * @param ro
     * @return
     */
    public static int readUint8(byte[] msg,ReadOff ro){
        int off = ro.getValue();
        ro.setValue(off+1);
        return msg[off] &0xff ;
    }

    public static int readUint16(byte[] msg,ReadOff ro){
        int off = ro.getValue();

        int value = (msg[off]<<8&0x0000ff00)
                    |(msg[off+1]&0x000000ff);

        ro.setValue(off+2);
        return value;
    }

    public static int readUint32(byte[] msg,ReadOff ro){
        int off =ro.getValue();

        int value = (msg[off]<<24&0xff000000)
                    |(msg[off+1]<<16&0x00ff0000)
                    |(msg[off+2]<<8&0x0000ff00)
                    |(msg[off+3]&0x000000ff)

                ;
        ro.setValue(off+4);
        return value;
    }






}
