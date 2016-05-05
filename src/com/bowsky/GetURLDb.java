package com.bowsky;

import com.qiniu.storage.model.FileInfo;

import java.io.File;
import java.sql.*;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/5/5.
 */
public class GetURLDb
{

    private ResultSet rs = null;

    private ResultSetMetaData rsmd = null;

    public GetURLDb()
    {

        try
        {
            Connection cn = DataBase.getConnection("jeesite");
            Statement st = cn.createStatement();
            rs = st.executeQuery("select * from ecs_goods");
            rsmd = rs.getMetaData();
        }
        catch (Exception ex)
        {}
    }

    public void insert(boolean isUpdate)
    {
        try {
            Connection   cn =  DataBase.getConnection("jeesite");
            PreparedStatement st = cn.prepareStatement("select * from t_product"); //createStatement();
            rs = st.executeQuery();
            while(rs.next()){
                String productHTML = rs.getString("productHTML");
                if(productHTML == null) continue;
                //String s = "<p>Image 1:<img width=\"199\" src=\"_image/12/label\" alt=\"\"/> Image 2: <img width=\"199\" src=\"_image/12/label\" alt=\"\"/><img width=\"199\" src=\"_image/12/label\" alt=\"\"/></p>";
                Pattern p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");//<img[^<>]*src=[\'\"]([0-9A-Za-z.\\/]*)[\'\"].(.*?)>");
                Matcher m = p.matcher(productHTML);
                //System.out.println(m.find());
                //System.out.println(m.groupCount());
                while(m.find()){
                    //System.out.println(m.group()+"-------------↓↓↓↓↓↓");
                    //System.out.println(m.group(1));
                   String originUrl =  m.group(1) ;
                    if (!isUpdate){
                        st = cn.prepareStatement("select * from images where `origin_url`= ?");
                        st.setString(1,originUrl);
                        ResultSet rs1 = st.executeQuery();
                        if(!rs1.next())
                        {

                            System.out.printf("插入");
                            st = cn.prepareStatement("INSERT INTO `images` (`origin_url`) VALUES (?)");
                            st.setString(1,originUrl);
                            st.executeUpdate();
                        }
                    }else
                    {
                        System.out.println("-->"+m.group(1));
                        st = cn.prepareStatement("select * from images where `name`= ? and `qiniu_url` IS NULL;");
                        st.setString(1,originUrl.substring(originUrl.lastIndexOf("/") + 1));
                        ResultSet rs1 = st.executeQuery();
                        if(rs1.next())
                        {
                            System.out.printf("更新");
                            st = cn.prepareStatement("update `images` set `origin_url` =? where id = ?");
                            st.setString(1,m.group(1));
                            st.setInt(2,rs1.getInt("id"));
                            st.executeUpdate();
                        }
                    }

                }
                //PicTool.ImageRequest(id, "http://www.ycepin.com/ecs/"+image);
                //System.out.printf("productHTML"+productHTML);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update()
    {
        try {
            Connection   cn =  DataBase.getConnection("jeesite");
            PreparedStatement st = cn.prepareStatement("select * from images where `qiniu_url` IS NULL;"); //createStatement();
            rs = st.executeQuery();

            while(rs.next()){
                int id = rs.getInt("id");
                String originUrl = rs.getString("origin_url");
                File file = PicTool.ImageFile(originUrl);
                if (file == null) continue;
                Thread t =  new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            QiniuUtil qiniuUtil = new QiniuUtil();
                            String qiniuUrl = qiniuUtil.upload(file);
                            if (!qiniuUrl.equals("")) {
                                System.out.printf("更新");
                                PreparedStatement st1 = cn.prepareStatement("update `images` set `qiniu_url`= ? where `id` = ?");
                                st1.setString(1, qiniuUrl);
                                st1.setInt(2, id);
                                st1.executeUpdate();

                            }
                        }catch (Exception e)
                        {
                            System.out.printf(""+e.getMessage());
                        }
                    }
                });
                t.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args)
    {
        GetURLDb db = new GetURLDb();
        //db.insert(true);
        db.update();
        //db.updateName();
        //db.list();

    }

    private void updateName() {
        try {
            Connection   cn =  DataBase.getConnection("jeesite");
            PreparedStatement st = cn.prepareStatement("select * from images"); //createStatement();
            rs = st.executeQuery();
            while(rs.next()){
                int id = rs.getInt("id");
                String originUrl = rs.getString("origin_url");
                System.out.printf("更新名字");
                st = cn.prepareStatement("update `images` set `name`= ? where `id` = ?");
                st.setString(1,originUrl.substring(originUrl.lastIndexOf("/") + 1));
                st.setInt(2,id);
                st.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void list() {
        QiniuUtil qiniuUtil = new QiniuUtil();
        FileInfo[] items = qiniuUtil.list();
        for (FileInfo info : items){
            System.out.printf("info:"+info.key);
            queryByName(info.key);
        }

    }

    private void queryByName(String key) {
        try {
            Connection   cn =  DataBase.getConnection("jeesite");
            PreparedStatement st = cn.prepareStatement("select * from images where `name`= ?"); //createStatement();
            st.setString(1,key);
            rs = st.executeQuery();
            while(rs.next()){
                int id = rs.getInt("id");
                System.out.printf("更新");
                st = cn.prepareStatement("update `images` set `qiniu_url`= ? where `name` = ?");
                st.setString(1,QiniuUtil.baseUrl+key);
                st.setString(2,key);
                st.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
