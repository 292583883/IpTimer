package com.bowsky;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;


/**
 * Jsoup Dmeo
 * 
 * @author Administrator Created by Administrator on 2016/4/22.
 */
public class JsoupDemo
{
    /**
     * run
     * 
     * @param args
     *            参数
     */
    public static void main(String[] args)
        throws IOException
    {

        Document doc = Jsoup.connect("http://gs.shang800.com/1/1.shtml").userAgent("Mozilla").get();
        String title = doc.title();
        System.out.printf(""+title);
        System.out.printf(""+doc.getElementsByTag("tbody"));

    }
}
