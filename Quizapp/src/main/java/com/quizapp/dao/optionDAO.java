package com.quizapp.dao;

import com.quizapp.model.Option;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class optionDAO extends BaseDAO {

    public int createOption(Option option) throws SQLException {
        String sql = "INSERT INTO options (question_id, option_text, is_correct) VALUES (?, ?, ?)";
        return executeUpdateAndGetGeneratedKey(sql, option.getQuestionId(), option.getOptionText(), 
                option.isCorrect());
    }
    
    public Option getOptionById(Long id) throws SQLException {
        String sql = "SELECT * FROM options WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Option option = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                option = extractOptionFromResultSet(rs);
            }
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return option;
    }
    
    public List<Option> getOptionsByQuestionId(Long questionId) throws SQLException {
        String sql = "SELECT * FROM options WHERE question_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Option> options = new ArrayList<>();
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, questionId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                options.add(extractOptionFromResultSet(rs));
            }
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return options;
    }
    
    public void updateOption(Option option) throws SQLException {
        String sql = "UPDATE options SET question_id = ?, option_text = ?, is_correct = ? WHERE id = ?";
        executeUpdate(sql, option.getQuestionId(), option.getOptionText(), 
                option.isCorrect(), option.getId());
    }
    
    public void deleteOption(Long id) throws SQLException {
        String sql = "DELETE FROM options WHERE id = ?";
        executeUpdate(sql, id);
    }
    
    public void deleteOptionsByQuestionId(Long questionId) throws SQLException {
        String sql = "DELETE FROM options WHERE question_id = ?";
        executeUpdate(sql, questionId);
    }
    
    private Option extractOptionFromResultSet(ResultSet rs) throws SQLException {
        Option option = new Option();
        option.setId(rs.getLong("id"));
        option.setQuestionId(rs.getLong("question_id"));
        option.setOptionText(rs.getString("option_text"));
        option.setCorrect(rs.getBoolean("is_correct"));
        return option;
    }
}