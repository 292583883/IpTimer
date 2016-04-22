package com.bowsky;

import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;

import java.io.FileInputStream;
import java.util.Properties;

public class DayuUtil {
	
	private static DayuUtil dayuUtil;
	private TaobaoClient client;
	private AlibabaAliqinFcSmsNumSendRequest req;
	
	public static DayuUtil getInstance(){
		if(dayuUtil == null){
			dayuUtil = new DayuUtil();
		}
		return dayuUtil;
	}
	
	private DayuUtil() {
		client = new DefaultTaobaoClient("https://eco.taobao.com/router/rest", "appid", "appkey");
		req = new AlibabaAliqinFcSmsNumSendRequest();
		req.setExtend("123456");
		req.setSmsType("normal");
		
		
	}
	
	public boolean send(String ...args){
		String signName = args[0];
		String templateCode = args[1];
		String phone = args[2];
		
		req.setSmsFreeSignName(signName);
		req.setSmsTemplateCode(templateCode);//"SMS_5545234"
		if (phone == null  || args == null) {
			return false;
		}
		switch(templateCode){
			case "SMS_5545228"://初始化-->验证码${code}，您正在尝试变更${product}重要信息，请妥善保管账户信息。
				req.setSmsParamString("{\"code\":\""+args[3]+"\",\"product\":\""+args[4]+"\"}");
				break;
			case "SMS_5545234"://欢迎
				req.setSmsParamString("{\"customer\":\""+args[3]+"\"}");
				break;
			case "SMS_5545231"://注册-->验证码${code}，您正在注册成为${product}用户，感谢您的支持！
				req.setSmsParamString("{\"code\":\""+args[3]+"\",\"product\":\""+args[4]+"\"}");
				break;
			case "SMS_5545229"://修改密码-->验证码${code}，您正在尝试修改${product}登录密码，请妥善保管账户信息。
				req.setSmsParamString("{\"code\":\""+args[3]+"\",\"product\":\""+args[4]+"\"}");
				break;
				//SMS_5545229
			case "SMS_5545230":
				req.setSmsParamString("{\"code\":\""+args[3]+"\",\"product\":\""+args[4]+"\",\"item\":\""+args[5]+"\"}");
				break;
			default:
				//TODO 成功兑换发送短信
				break;


		}
		
		req.setRecNum(phone);
		try {
			AlibabaAliqinFcSmsNumSendResponse res = client.execute(req);
			if(res == null || res.getResult() == null || res.getResult().getSuccess() == null){
				return false;
			}else if(res.getResult().getSuccess()){
				return true;
			}else{
				return false;
			}
		} catch (ApiException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	
	public static void main(String[] args){

	}
}
