package com.quizapp.dao;

import com.quizapp.model.QuizAttempt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuizAttemptDAO extends BaseDAO {

    public int createQuizAttempt(QuizAttempt attempt) throws SQLException {
        String sql = "INSERT INTO quiz_attempts (user_id, quiz_id, start_time, end_time, score) VALUES (?, ?, ?, ?, ?)";
        return executeUpdateAndGetGeneratedKey(sql, attempt.getUserId(), attempt.getQuizId(), 
                attempt.getStartTime(), attempt.getEndTime(), attempt.getScore());
    }
    
    public QuizAttempt getQuizAttemptById(Long id) throws SQLException {
        String sql = "SELECT * FROM quiz_attempts WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        QuizAttempt attempt = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                attempt = extractQuizAttemptFromResultSet(rs);
            }
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return attempt;
    }
    
    public List<QuizAttempt> getQuizAttemptsByUserId(Long userId) throws SQLException {
        String sql = "SELECT * FROM quiz_attempts WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<QuizAttempt> attempts = new ArrayList<>();
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, userId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                attempts.add(extractQuizAttemptFromResultSet(rs));
            }
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return attempts;
    }
    
    public List<QuizAttempt> getQuizAttemptsByQuizId(Long quizId) throws SQLException {
        String sql = "SELECT * FROM quiz_attempts WHERE quiz_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<QuizAttempt> attempts = new ArrayList<>();
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, quizId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                attempts.add(extractQuizAttemptFromResultSet(rs));
            }
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return attempts;
    }
    
    public QuizAttempt getLatestAttemptByUserAndQuiz(Long userId, Long quizId) throws SQLException {
        String sql = "SELECT * FROM quiz_attempts WHERE user_id = ? AND quiz_id = ? ORDER BY start_time DESC LIMIT 1";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        QuizAttempt attempt = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, userId);
            stmt.setLong(2, quizId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                attempt = extractQuizAttemptFromResultSet(rs);
            }
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return attempt;
    }
    
    public void updateQuizAttempt(QuizAttempt attempt) throws SQLException {
        String sql = "UPDATE quiz_attempts SET user_id = ?, quiz_id = ?, start_time = ?, end_time = ?, score = ? WHERE id = ?";
        executeUpdate(sql, attempt.getUserId(), attempt.getQuizId(), attempt.getStartTime(), 
                attempt.getEndTime(), attempt.getScore(), attempt.getId());
    }
    
    public void deleteQuizAttempt(Long id) throws SQLException {
        String sql = "DELETE FROM quiz_attempts WHERE id = ?";
        executeUpdate(sql, id);
    }
    
    private QuizAttempt extractQuizAttemptFromResultSet(ResultSet rs) throws SQLException {
        QuizAttempt attempt = new QuizAttempt();
        attempt.setId(rs.getLong("id"));
        attempt.setUserId(rs.getLong("user_id"));
        attempt.setQuizId(rs.getLong("quiz_id"));
        attempt.setStartTime(rs.getTimestamp("start_time"));
        attempt.setEndTime(rs.getTimestamp("end_time"));
        attempt.setScore(rs.getDouble("score"));
        return attempt;
    }
}