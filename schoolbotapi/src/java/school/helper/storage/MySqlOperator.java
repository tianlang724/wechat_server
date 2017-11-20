package school.helper.storage;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class MySqlOperator {

    // 数据库表常量
    public static final String TABLE_USER_INFO = "user_info";
    public static final String TABLE_USER_ECARD = "user_ecard";
    private static final String URL_PREFIX = "jdbc:mysql://127.0.0.1/uestc";
    private static final String DRIVER_NAME = "com.mysql.jdbc.Driver";
//    private static final String SQL_USER = "root";
//    private static final String SQL_PASSWORD = "yxzby2j";
    private static final String SQL_USER = "root";
    private static final String SQL_PASSWORD = "chaoyue1314";
    public static final String TYPE_ECARD_BALANCE = "ecard_balance";
    public static final String TYPE_ECARD_EXPEND = "ecard_expend";
    public static final String TYPE_ECARD_RECHARGE = "ecard_recharge";

    public static final String TYPE_COURSE_CHOOSEN = "course_choosen";
    public static final String TYPE_COURSE_POINT = "course_point";

    public static final String TYPE_LIB_READING_HISTORY = "lib_history";

    public static final String TYPE_ECARD_ACCOUNT = "acct_ecard";
    public static final String TYPE_LIB_ACCOUNT = "acct_lib";
    public static final String TYPE_BBS_ACCOUNT = "acct_bbs";

    private Connection conn;
    private PreparedStatement pst;
    private static final Logger log = LoggerFactory.getLogger(MySqlOperator.class);


    public UserPrivacyInfo getUserPrivacyFromDB(String id, String type) {
        String username = "";
        String password = "";
        UserPrivacyInfo result = null;
        conn = null;
        try {
            Class.forName(DRIVER_NAME);
            conn = DriverManager.getConnection(URL_PREFIX, SQL_USER, SQL_PASSWORD);
            pst = conn.prepareCall("SELECT * FROM user_info WHERE user_id='" + id + "'");
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                switch (type) {
                    case TYPE_ECARD_ACCOUNT: {
                        username = rs.getString(2);
                        password = rs.getString(3);
                        break;
                    }
                    case TYPE_LIB_ACCOUNT: {
                        username = rs.getString(4);
                        password = rs.getString(5);
                        break;
                    }
                    case TYPE_BBS_ACCOUNT: {
                        username = rs.getString(6);
                        password = rs.getString(7);
                        break;
                    }
                    default:
                        break;
                }
            }
            result = new UserPrivacyInfo(username, password);
            pst.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Exception: SQLException");
        } catch (ClassNotFoundException e) {
            System.out.println("Exception: ClassNotFoundException");
        }
        return result;
    }

    public void updateEcardPrivacyInfoFromDB(String id, String username, String password) {
        conn = null;
        try {
            Class.forName(DRIVER_NAME);
            conn = DriverManager.getConnection(URL_PREFIX, SQL_USER, SQL_PASSWORD);
            pst = conn.prepareCall("SELECT * FROM user_info WHERE user_id='" + id + "'");
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                pst.close();
                pst = conn.prepareCall("UPDATE user_info set ecard_id='" + username + "'" +", ecard_password='" + password + "'" + " WHERE user_id='" + id + "'");
                pst.executeUpdate();
                pst.close();
            }
            else {
                pst = conn.prepareCall("INSERT INTO user_info (user_id, ecard_id, ecard_password, lib_id, lib_password, bbs_id, bbs_password) VALUES (?, ?, ?, ?, ?, ?, ?)");
                pst.setString(1, id);
                pst.setString(2, username);
                pst.setString(3, password);
                pst.setString(4, "");
                pst.setString(5, "");
                pst.setString(6,"");
                pst.setString(7, "");
                pst.executeUpdate();
            }
            pst.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Exception: SQLException");
        } catch (ClassNotFoundException e) {
            System.out.println("Exception: ClassNotFoundException");
        }
    }

    public void updateLibPrivacyInfoFromDB(String id, String username, String password) {
        conn = null;
        try {
            Class.forName(DRIVER_NAME);
            conn = DriverManager.getConnection(URL_PREFIX, SQL_USER, SQL_PASSWORD);
            pst = conn.prepareCall("SELECT * FROM user_info WHERE user_id='" + id + "'");
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                pst.close();
                pst = conn.prepareCall("UPDATE user_info set lib_id='" + username + "'" +", lib_password='" + password + "'" + " WHERE user_id='" + id + "'");
                pst.executeUpdate();
                pst.close();
            }
            else {
                pst = conn.prepareCall("INSERT INTO user_info (user_id, ecard_id, ecard_password, lib_id, lib_password, bbs_id, bbs_password) VALUES (?, ?, ?, ?, ?, ?, ?)");
                pst.setString(1, id);
                pst.setString(2, "");
                pst.setString(3, "");
                pst.setString(4, username);
                pst.setString(5, password);
                pst.setString(6,"");
                pst.setString(7, "");
                pst.executeUpdate();
            }
            pst.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Exception: SQLException");
        } catch (ClassNotFoundException e) {
            System.out.println("Exception: ClassNotFoundException");
        }
    }

    public void updateUserEcardBalance(String id, String balance) {
        conn = null;
        try {
            Class.forName(DRIVER_NAME);
            conn = DriverManager.getConnection(URL_PREFIX, SQL_USER, SQL_PASSWORD);
            pst = conn.prepareCall("SELECT * FROM user_ecard WHERE user_id='" + id + "'");
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                pst.close();
                pst = conn.prepareCall("UPDATE user_ecard set balance='" + balance + "'" +" WHERE user_id='" + id + "'");
                pst.executeUpdate();
            }
            else {
                pst = conn.prepareCall("INSERT INTO user_ecard (user_id, balance, expend, recharge) VALUES (?, ?, ?, ?)");
                pst.setString(1, id);
                pst.setString(2, balance);
                pst.setString(3, "");
                pst.setString(4, "");
                pst.executeUpdate();
            }
            pst.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Exception: SQLException");
        } catch (ClassNotFoundException e) {
            System.out.println("Exception: ClassNotFoundException");
        }
    }

    public String getUserEcardInfo(String id, String type) {
        String result = "";
        conn = null;
        try {
            Class.forName(DRIVER_NAME);
            conn = DriverManager.getConnection(URL_PREFIX, SQL_USER, SQL_PASSWORD);
            pst = conn.prepareCall("SELECT * FROM user_ecard WHERE user_id='" + id + "'");
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                switch (type) {
                    case TYPE_ECARD_BALANCE: {
                        result = rs.getString(2);
                        break;
                    }
                    case TYPE_ECARD_EXPEND: {
                        result = rs.getString(3);
                        break;
                    }
                    case TYPE_ECARD_RECHARGE: {
                        result = rs.getString(4);
                        break;
                    }
                    default:
                        break;
                }
            }
            pst.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Exception: SQLException");
        } catch (ClassNotFoundException e) {
            System.out.println("Exception: ClassNotFoundException");
        }
        return result;
    }

    public void updateUserEcardExpend(String id, String expend) {
        conn = null;
        try {
            Class.forName(DRIVER_NAME);
            conn = DriverManager.getConnection(URL_PREFIX, SQL_USER, SQL_PASSWORD);
            pst = conn.prepareCall("SELECT * FROM user_ecard WHERE user_id='" + id + "'");
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                pst.close();
                pst = conn.prepareCall("UPDATE user_ecard set expend='" + expend + "'" +" WHERE user_id='" + id + "'");
                pst.executeUpdate();
            }
            else {
                pst = conn.prepareCall("INSERT INTO user_ecard (user_id, balance, expend, recharge) VALUES (?, ?, ?, ?)");
                pst.setString(1, id);
                pst.setString(2, "");
                pst.setString(3, expend);
                pst.setString(4, "");
                pst.executeUpdate();
            }
            pst.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Exception: SQLException");
        } catch (ClassNotFoundException e) {
            System.out.println("Exception: ClassNotFoundException");
        }
    }

    public void updateUserEcardRecharge(String id, String recharge) {
        conn = null;
        try {
            Class.forName(DRIVER_NAME);
            conn = DriverManager.getConnection(URL_PREFIX, SQL_USER, SQL_PASSWORD);
            pst = conn.prepareCall("SELECT * FROM user_ecard WHERE user_id='" + id + "'");
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                pst.close();
                pst = conn.prepareCall("UPDATE user_ecard set recharge='" + recharge + "'" +" WHERE user_id='" + id + "'");
                pst.executeUpdate();
            }
            else {
                pst = conn.prepareCall("INSERT INTO user_ecard (user_id, balance, expend, recharge) VALUES (?, ?, ?, ?)");
                pst.setString(1, id);
                pst.setString(2, "");
                pst.setString(3, "");
                pst.setString(4, recharge);
                pst.executeUpdate();
            }
            pst.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Exception: SQLException");
        } catch (ClassNotFoundException e) {
            System.out.println("Exception: ClassNotFoundException");
        }
    }

    public void updateLibReadingHistory(String id, String history) {
        conn = null;
        try {
            Class.forName(DRIVER_NAME);
            conn = DriverManager.getConnection(URL_PREFIX, SQL_USER, SQL_PASSWORD);
            pst = conn.prepareCall("SELECT * FROM user_lib WHERE user_id='" + id + "'");
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                pst.close();
                pst = conn.prepareCall("UPDATE user_lib set history='" + history + "'" +" WHERE user_id='" + id + "'");
                pst.executeUpdate();
            }
            else {
                pst = conn.prepareCall("INSERT INTO user_lib (user_id, history) VALUES (?, ?)");
                pst.setString(1, id);
                pst.setString(2, history);
                pst.executeUpdate();
            }
            pst.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Exception: SQLException");
        } catch (ClassNotFoundException e) {
            System.out.println("Exception: ClassNotFoundException");
        }
    }

    public String getLibInfo(String id, String type) {
        String result = "";
        conn = null;
        try {
            Class.forName(DRIVER_NAME);
            conn = DriverManager.getConnection(URL_PREFIX, SQL_USER, SQL_PASSWORD);
            pst = conn.prepareCall("SELECT * FROM user_lib WHERE user_id='" + id + "'");
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                switch (type) {
                    case TYPE_LIB_READING_HISTORY: {
                        result = rs.getString(2);
                        break;
                    }
                    default:
                        break;
                }
            }
            pst.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Exception: SQLException");
        } catch (ClassNotFoundException e) {
            System.out.println("Exception: ClassNotFoundException");
        }
        return result;
    }


    public void updateCoursePoint(String id, String point) {
        conn = null;
        try {
            Class.forName(DRIVER_NAME);
            conn = DriverManager.getConnection(URL_PREFIX, SQL_USER, SQL_PASSWORD);
            pst = conn.prepareCall("SELECT * FROM user_course WHERE user_id='" + id + "'");
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                pst.close();
                pst = conn.prepareCall("UPDATE user_course set point='" + point + "'" +" WHERE user_id='" + id + "'");
                pst.executeUpdate();
            }
            else {
                pst = conn.prepareCall("INSERT INTO user_course (user_id, choosen, point) VALUES (?, ?, ?)");
                pst.setString(1, id);
                pst.setString(2, "");
                pst.setString(3, point);
                pst.executeUpdate();
            }
            pst.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Exception: SQLException" + e.toString());
        } catch (ClassNotFoundException e) {
            System.out.println("Exception: ClassNotFoundException");
        }
    }

    public void updateChoosenCourse(String id, String choosen) {
        conn = null;
        try {
            Class.forName(DRIVER_NAME);
            conn = DriverManager.getConnection(URL_PREFIX, SQL_USER, SQL_PASSWORD);
            pst = conn.prepareCall("SELECT * FROM user_course WHERE user_id='" + id + "'");
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                pst.close();
                pst = conn.prepareCall("UPDATE user_course set choosen='" + choosen + "'" +" WHERE user_id='" + id + "'");
                pst.executeUpdate();
            }
            else {
                pst = conn.prepareCall("INSERT INTO user_course (user_id, choosen, point) VALUES (?, ?, ?)");
                pst.setString(1, id);
                pst.setString(2, choosen);
                pst.setString(3, "");
                pst.executeUpdate();
            }
            pst.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Exception: SQLException" + e.toString());
        } catch (ClassNotFoundException e) {
            System.out.println("Exception: ClassNotFoundException");
        }
    }

    public String getCourseInfo(String id, String type) {
        String result = "";
        conn = null;
        try {
            Class.forName(DRIVER_NAME);
            conn = DriverManager.getConnection(URL_PREFIX, SQL_USER, SQL_PASSWORD);
            pst = conn.prepareCall("SELECT * FROM user_course WHERE user_id='" + id + "'");
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                switch (type) {
                    case TYPE_COURSE_CHOOSEN: {
                        result = rs.getString(2);
                        break;
                    }
                    case TYPE_COURSE_POINT: {
                        result = rs.getString(3);
                        break;
                    }
                    default:
                        break;
                }
            }
            pst.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Exception: SQLException");
        } catch (ClassNotFoundException e) {
            System.out.println("Exception: ClassNotFoundException");
        }
        return result;
    }


    public static void main(String[] args) {
        MySqlOperator op = new MySqlOperator();
//        op.updateUserEcardBalance("1000","100000");
        test2();
    }


    public static void test2() {
        MySqlOperator op = new MySqlOperator();
//        op.updateUserEcardInfo("1000", "50", "k12345678", "k12345678");
        op.updateEcardPrivacyInfoFromDB("2000", "hahah", "hahha");
        op.updateEcardPrivacyInfoFromDB("1000", "yuyu", "yuyu");
    }
    public static void test1() {
        MySqlOperator op = new MySqlOperator();
//        UserPrivacyInfo userPrivacyInfo = op.getUserPrivacyInfoFromDB("1000");
//        log.info("username" + userPrivacyInfo.getUsername());
//        log.info("password" + userPrivacyInfo.getPassword());
    }
}
