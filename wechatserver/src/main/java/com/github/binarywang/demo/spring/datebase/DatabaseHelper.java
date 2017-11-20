package com.github.binarywang.demo.spring.datebase;


import java.sql.*;


public class DatabaseHelper {


    /** 这个mysqlConnection只是为了用来从里面读一个PreparedStatement，不会往里面写数据，因此没有线程安全问题，可以作为一个全局变量 */

    private static Connection getConnection()
    {
        Connection con = null;
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            con= DriverManager.getConnection("jdbc:mysql://127.0.0.1/uestc",
                    "root",
                    "chaoyue1314");
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return con;
    }
    public static String queryDatabase(String tablename,String queryItem,String userId)
    {
        Connection connection=null;
        PreparedStatement ps = null;
        try{
            connection = DBConnection.getConnection();
            try {
                StringBuilder sb=new StringBuilder();
                sb.append("select * from uestc.");
                sb.append(tablename);
                sb.append(" where user_id=\'");
                sb.append(userId);
                sb.append("\'");
                ps = connection.prepareStatement(sb.toString());
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    String ret=rs.getString(queryItem);
                    System.out.println(userId+":"+queryItem+":"+ret);
                    if(ret==null||ret.isEmpty()){
                        return null;
                    }
                    return ret;
                }
                System.out.println(userId+" ecard null");
                return null;

            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }finally {
            try {
                connection.close();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
    public static boolean queryPassword(String queryItem,String userId)
    {
        Connection connection =null;
        PreparedStatement ps = null;
        try {
            connection=DatabaseHelper.getConnection();
            StringBuilder sb=new StringBuilder();
            sb.append("select * from user_info where user_id=\'");
            sb.append(userId);
            sb.append("\'");
            ps = connection.prepareStatement(sb.toString());
            ResultSet rs = ps.executeQuery();
            while(rs.next()){

                String id=rs.getString(queryItem+"_id");
                String password=rs.getString(queryItem+"_password");
                System.out.println(userId+":"+queryItem+" "+id+" "+password);
                if(id==null||password==null||id.isEmpty()||password.isEmpty()){
                    System.out.println("no id or password");
                    return false;
                }
                return true;
            }
            //System.out.println(userId+":"+queryItem+" "+": password null");
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }finally {
            try {
                connection.close();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

    }
}
