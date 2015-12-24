package qianfeng.changliao.net.http;

/**
 * Created by wukai on 15/12/14.
 */
public class HttpApi {

	private static final String API_ROOT = "http://192.168.57.1:8080";
	//登录
	public static final String API_LOGIN = API_ROOT+"/user/login";
	public static final String API_REG = API_ROOT+"/user/register";
	//获取好友
	public static final String API_ALL_USER = API_ROOT+"/user/friends";



}
