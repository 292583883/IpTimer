package com.bowsky;

import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;

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
		client = new DefaultTaobaoClient("https://eco.taobao.com/router/rest", "23321661", "73e6254b87d12cfff925b0cba5151cf7");
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
			case "SMS_5545230"://活动兑换商品-->验证码${code}，您正在参加${product}的${item}活动，请确认系本人申请。
				req.setSmsParamString("{\"code\":\""+args[3]+"\",\"product\":\""+args[4]+"\",\"item\":\""+args[5]+"\"}");
				break;
			case "SMS_8000018"://尊敬的${name}，您已成功兑换商品，共消耗积分${score}
				req.setSmsParamString("{\"name\":\""+args[3]+"\",\"score\":\""+args[4]+"\"}");
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
		getInstance().send("变更验证","SMS_5545228","18758917760", "123456","18758917760");
		
		getInstance().send("注册验证","SMS_5545231","18758917760", "123456","亿畅网门店系统");
		getInstance().send("变更验证","SMS_5545229","18758917760", "123456","18758917760");
		
		getInstance().send("活动验证","SMS_5545230","18758917760", "123456","亿畅网积分兑换","商品概述");

		getInstance().send("活动验证","SMS_5545230","18758917760", "","亿品商城积分兑换","共消耗积分");
		//getInstance().send("SMS_5545228","18758917760", "hello");
	}
}
