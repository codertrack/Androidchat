package qianfeng.changliao.net.socket.protocol;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by wukai on 15/12/10.
 */
public class ChatMessage {

    //消息来源的用户id
    public String fromUserid;
    //消息接收地址
    public String toUserid;
    //消息发送时间
    public String time;
    //消息内容
    public String text;

    public static final ChatMessage initWithJsonObject(JSONObject json){
        if (json == null){
            System.out.println("接受json格式错误");
        }
        ChatMessage cm = new ChatMessage();
        cm.fromUserid = json.getString("from_userid");
        cm.toUserid = json.getString("to_userid");
        cm.time = json.getString("send_time");
        cm.text = json.getString("text");
        return cm;
    }


    /**
     *
     * @param message
     * @return
     */
    public static final JSONObject parse2JsonObject(ChatMessage message){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("from_userid",message.fromUserid);
        jsonObject.put("to_userid",message.toUserid);
        jsonObject.put("send_time",message.time);
        jsonObject.put("text",message.text);
        return jsonObject;
    }



}
