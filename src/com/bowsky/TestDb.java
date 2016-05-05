package com.bowsky;


import java.sql.*;


/**
 * Created by Administrator on 2016/4/23.
 */
public class TestDb
{

    private ResultSet rs = null;

    private ResultSetMetaData rsmd = null;

    public TestDb()
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

    public void run()
    {
        try {
            Connection   cn =  DataBase.getConnection("jeesite");
            Statement st = cn.createStatement();
            rs = st.executeQuery("select * from t_product");
        while(rs.next()){
            String id = rs.getString("id");
            String image = rs.getString("picture");
            System.out.println(id+" "+image) ;
            PicTool.ImageRequest(id, "http://www.ycepin.com/ecs/"+image);

        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args)
    {
        TestDb db = new TestDb();
        db.run();


    }
}
