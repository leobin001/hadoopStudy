package com.kkb.hive;

import java.sql.*;

public class HiveJDBC {
    private static String url = "jdbc:hive2://node03:10000/myhive";

    public static void main(String[] args) throws Exception {
        Class.forName("org.apache.hive.jdbc.HiveDriver");

        Connection connection = DriverManager.getConnection(url, "hadoop", "");
        String sql = "select * from stu";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                System.out.println(id + "\t" + name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
