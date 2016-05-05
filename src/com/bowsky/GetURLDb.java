package com.bowsky;

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

    public void insert()
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
                System.out.println(m.groupCount());
                while(m.find()){
                    //System.out.println(m.group()+"-------------↓↓↓↓↓↓");
                    //System.out.println(m.group(1));
                    st = cn.prepareStatement("select * from images where `origin_url`= ?");
                    st.setString(1,m.group(1));
                    rs = st.executeQuery();
                    if(!rs.next())
                    {

                        System.out.printf("插入");
                        st = cn.prepareStatement("INSERT INTO `images` (`origin_url`) VALUES (?)");
                        st.setString(1,m.group(1));
                        st.executeUpdate();
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
            QiniuUtil qiniuUtil = new QiniuUtil();
            while(rs.next()){
                int id = rs.getInt("id");
                String originUrl = rs.getString("origin_url");
                String qiniuUrl = qiniuUtil.upload(originUrl);
                if (!qiniuUrl.equals(""))
                {
                    System.out.printf("更新");
                    st = cn.prepareStatement("update `images` set `qiniu_url`= ? where `id` = ?");
                    st.setString(1,qiniuUrl);
                    st.setInt(2,id);
                    st.executeUpdate();

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args)
    {
        GetURLDb db = new GetURLDb();
        //db.insert();
        db.update();

    }
}
