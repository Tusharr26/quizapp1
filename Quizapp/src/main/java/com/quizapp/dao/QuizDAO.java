package com.quizapp.dao;

import com.quizapp.model.Quiz;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuizDAO extends BaseDAO {

    public int createQuiz(Quiz quiz) throws SQLException {
        String sql = "INSERT INTO quizzes (title, description, creator_id, time_limit, is_published) VALUES (?, ?, ?, ?, ?)";
        return executeUpdateAndGetGeneratedKey(sql, quiz.getTitle(), quiz.getDescription(), 
                quiz.getCreatorId(), quiz.getTimeLimit(), quiz.isPublished());
    }
    
    public Quiz getQuizById(Long id) throws SQLException {
        String sql = "SELECT * FROM quizzes WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Quiz quiz = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                quiz = extractQuizFromResultSet(rs);
            }
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return quiz;
    }
    
    public List<Quiz> getAllQuizzes() throws SQLException {
        String sql = "SELECT * FROM quizzes";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Quiz> quizzes = new ArrayList<>();
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                quizzes.add(extractQuizFromResultSet(rs));
            }
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return quizzes;
    }
    
    public List<Quiz> getQuizzesByCreatorId(Long creatorId) throws SQLException {
        String sql = "SELECT * FROM quizzes WHERE creator_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Quiz> quizzes = new ArrayList<>();
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, creatorId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                quizzes.add(extractQuizFromResultSet(rs));
            }
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return quizzes;
    }
    
    public List<Quiz> getPublishedQuizzes() throws SQLException {
        String sql = "SELECT * FROM quizzes WHERE is_published = true";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Quiz> quizzes = new ArrayList<>();
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                quizzes.add(extractQuizFromResultSet(rs));
            }
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return quizzes;
    }
    
    public void updateQuiz(Quiz quiz) throws SQLException {
        String sql = "UPDATE quizzes SET title = ?, description = ?, time_limit = ?, is_published = ? WHERE id = ?";
        executeUpdate(sql, quiz.getTitle(), quiz.getDescription(), quiz.getTimeLimit(), 
                quiz.isPublished(), quiz.getId());
    }
    
    public void deleteQuiz(Long id) throws SQLException {
        String sql = "DELETE FROM quizzes WHERE id = ?";
        executeUpdate(sql, id);
    }
    
    private Quiz extractQuizFromResultSet(ResultSet rs) throws SQLException {
        Quiz quiz = new Quiz();
        quiz.setId(rs.getLong("id"));
        quiz.setTitle(rs.getString("title"));
        quiz.setDescription(rs.getString("description"));
        quiz.setCreatorId(rs.getLong("creator_id"));
        quiz.setTimeLimit(rs.getInt("time_limit"));
        quiz.setPublished(rs.getBoolean("is_published"));
        quiz.setCreatedAt(rs.getTimestamp("created_at"));
        return quiz;
    }
}