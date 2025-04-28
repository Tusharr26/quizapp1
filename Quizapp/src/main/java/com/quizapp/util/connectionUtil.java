package com.quizapp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class connectionUtil {
    private static final String DB_PROPERTIES_FILE = "database.properties";
    private static String url;
    private static String username;
    private static String password;
    
    static {
        try {
            // Load database properties
            Properties props = new Properties();
            InputStream inputStream = connectionUtil.class.getClassLoader().getResourceAsStream(DB_PROPERTIES_FILE);
            props.load(inputStream);
            
            // Load the JDBC driver
            Class.forName(props.getProperty("driver"));
            
            // Set connection properties
            url = props.getProperty("url");
            username = props.getProperty("username");
            password = props.getProperty("password");
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
    
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}