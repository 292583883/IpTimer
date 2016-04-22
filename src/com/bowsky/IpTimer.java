package com.bowsky;


import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 测试8ip地址是否连通
 * 
 * @author Administrator Created by Administrator on 2016/4/22.
 */
public class IpTimer
{

    /**
     * run
     * 
     * @param args
     *            默认
     */
    public static void main(String[] args)
    {
        final Timer timer = new Timer();
        TimerTask tt = new TimerTask()
        {
            @Override
            public void run()
            {
                System.out.printf("开始执行...");
                Properties properties = new Properties();
                try
                {
                properties.load(new FileInputStream("conf.properties"));
                properties.list(System.out);
                String host = properties.getProperty("ip");
                int timeout = Integer.parseInt(properties.getProperty("timeout")); // 超时应该在3钞以上
                    System.out.printf("host:"+host+" timeout="+timeout);
                    boolean status = InetAddress.getByName(host).isReachable(timeout); // 当返回值是true
                    if (status)
                    {
                        DayuUtil.getInstance().send("注册验证", "SMS_5545231", "18758917760", host,"连接成功");
                        System.out.printf("执行结束...");
                        timer.cancel();
                    }
                    else
                    {
                        System.out.printf("执行失败");
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
        timer.schedule(tt, 0, 30 * 60 * 1000);
    }
}
