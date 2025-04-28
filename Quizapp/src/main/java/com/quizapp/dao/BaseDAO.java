package com.quizapp.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class BaseDAO {
    
    protected Connection getConnection() throws SQLException {
        // Replace these with your actual database connection details
        String url = "jdbc:mysql://localhost:3306/quizapp";
        String username = "root";
        String password = "tushar";
        
        return DriverManager.getConnection(url, username, password);
    }
    
    protected void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    protected void executeUpdate(String sql, Object... params) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            
            stmt.executeUpdate();
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    protected int executeUpdateAndGetGeneratedKey(String sql, Object... params) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int generatedKey = -1;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            
            stmt.executeUpdate();
            rs = stmt.getGeneratedKeys();
            
            if (rs.next()) {
                generatedKey = rs.getInt(1);
            }
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return generatedKey;
    }
}