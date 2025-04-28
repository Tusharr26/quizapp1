package com.quizapp.dao;

import com.quizapp.model.Answer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AnswerDAO extends BaseDAO {

    public int createAnswer(Answer answer) throws SQLException {
        String sql = "INSERT INTO answers (attempt_id, question_id, option_id) VALUES (?, ?, ?)";
        return executeUpdateAndGetGeneratedKey(sql, answer.getAttemptId(), answer.getQuestionId(), 
                answer.getOptionId());
    }
    
    public Answer getAnswerById(Long id) throws SQLException {
        String sql = "SELECT * FROM answers WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Answer answer = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                answer = extractAnswerFromResultSet(rs);
            }
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return answer;
    }
    
    public List<Answer> getAnswersByAttemptId(Long attemptId) throws SQLException {
        String sql = "SELECT * FROM answers WHERE attempt_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Answer> answers = new ArrayList<>();
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, attemptId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                answers.add(extractAnswerFromResultSet(rs));
            }
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return answers;
    }
    
    public Answer getAnswerByAttemptAndQuestion(Long attemptId, Long questionId) throws SQLException {
        String sql = "SELECT * FROM answers WHERE attempt_id = ? AND question_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Answer answer = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, attemptId);
            stmt.setLong(2, questionId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                answer = extractAnswerFromResultSet(rs);
            }
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return answer;
    }
    
    public void updateAnswer(Answer answer) throws SQLException {
        String sql = "UPDATE answers SET attempt_id = ?, question_id = ?, option_id = ? WHERE id = ?";
        executeUpdate(sql, answer.getAttemptId(), answer.getQuestionId(), answer.getOptionId(), answer.getId());
    }
    
    public void deleteAnswer(Long id) throws SQLException {
        String sql = "DELETE FROM answers WHERE id = ?";
        executeUpdate(sql, id);
    }
    
    public void deleteAnswersByAttemptId(Long attemptId) throws SQLException {
        String sql = "DELETE FROM answers WHERE attempt_id = ?";
        executeUpdate(sql, attemptId);
    }
    
    private Answer extractAnswerFromResultSet(ResultSet rs) throws SQLException {
        Answer answer = new Answer();
        answer.setId(rs.getLong("id"));
        answer.setAttemptId(rs.getLong("attempt_id"));
        answer.setQuestionId(rs.getLong("question_id"));
        answer.setOptionId(rs.getLong("option_id"));
        return answer;
    }
}