package qianfeng.changliao.net.socket.protocol;

/**
 * Created by wukai on 15/12/8.
 */
public class FrameType {
    //确认帧
    public static final int ACK = 1;
    //消息桢
    public static final int USER_MSG = ACK+1;
    //用户给系统发的消息,比如上线
    public static final int SYS_MSG = USER_MSG+1;


}
