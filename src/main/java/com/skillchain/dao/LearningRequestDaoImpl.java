package com.skillchain.dao;

import com.skillchain.model.LearningRequest;
import com.skillchain.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * JDBC DATA ACCESS IMPLEMENTATION: LEARNING REQUESTS
 * ============================================================================
 * Implements LearningRequestDao using JDBC PreparedStatement and try-with-resources.
 */
public class LearningRequestDaoImpl implements LearningRequestDao {

    private static final String BASE_SELECT_SQL =
            "SELECT lr.request_id, lr.sender_id, u_send.name AS sender_name, " +
            "       lr.receiver_id, u_recv.name AS receiver_name, " +
            "       lr.offered_skill_id, s_off.skill_name AS offered_skill_name, " +
            "       lr.requested_skill_id, s_req.skill_name AS requested_skill_name, " +
            "       lr.status, lr.created_at " +
            "FROM learning_requests lr " +
            "JOIN users u_send ON lr.sender_id = u_send.user_id " +
            "JOIN users u_recv ON lr.receiver_id = u_recv.user_id " +
            "JOIN skills s_off ON lr.offered_skill_id = s_off.skill_id " +
            "JOIN skills s_req ON lr.requested_skill_id = s_req.skill_id ";

    @Override
    public LearningRequest save(LearningRequest request) throws SQLException {
        final String sql = "INSERT INTO learning_requests (sender_id, receiver_id, offered_skill_id, requested_skill_id, status) " +
                           "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, request.getSenderId());
            stmt.setInt(2, request.getReceiverId());
            stmt.setInt(3, request.getOfferedSkillId());
            stmt.setInt(4, request.getRequestedSkillId());
            stmt.setString(5, request.getStatus() != null ? request.getStatus() : "PENDING");

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    request.setRequestId(rs.getInt(1));
                }
            }
            if (request.getCreatedAt() == null) {
                request.setCreatedAt(LocalDateTime.now());
            }
            return request;
        }
    }

    @Override
    public List<LearningRequest> findIncomingByUserId(int receiverId) throws SQLException {
        final String sql = BASE_SELECT_SQL + "WHERE lr.receiver_id = ? ORDER BY lr.created_at DESC";
        return executeQuery(sql, receiverId);
    }

    @Override
    public List<LearningRequest> findOutgoingByUserId(int senderId) throws SQLException {
        final String sql = BASE_SELECT_SQL + "WHERE lr.sender_id = ? ORDER BY lr.created_at DESC";
        return executeQuery(sql, senderId);
    }

    @Override
    public boolean updateStatus(int requestId, String newStatus) throws SQLException {
        final String sql = "UPDATE learning_requests SET status = ? WHERE request_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, requestId);

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public int countPendingIncoming(int receiverId) throws SQLException {
        final String sql = "SELECT COUNT(*) FROM learning_requests WHERE receiver_id = ? AND status = 'PENDING'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, receiverId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    @Override
    public boolean existsActiveRequest(int senderId, int receiverId, int offeredSkillId, int requestedSkillId) throws SQLException {
        final String sql = "SELECT 1 FROM learning_requests " +
                           "WHERE sender_id = ? AND receiver_id = ? " +
                           "  AND offered_skill_id = ? AND requested_skill_id = ? " +
                           "  AND status IN ('PENDING', 'ACCEPTED') LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            stmt.setInt(3, offeredSkillId);
            stmt.setInt(4, requestedSkillId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private List<LearningRequest> executeQuery(String sql, int userId) throws SQLException {
        List<LearningRequest> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int reqId = rs.getInt("request_id");
                    int sId = rs.getInt("sender_id");
                    String sName = rs.getString("sender_name");
                    int rId = rs.getInt("receiver_id");
                    String rName = rs.getString("receiver_name");
                    int offId = rs.getInt("offered_skill_id");
                    String offName = rs.getString("offered_skill_name");
                    int reqSkillId = rs.getInt("requested_skill_id");
                    String reqSkillName = rs.getString("requested_skill_name");
                    String status = rs.getString("status");
                    Timestamp ts = rs.getTimestamp("created_at");
                    LocalDateTime createdAt = ts != null ? ts.toLocalDateTime() : LocalDateTime.now();

                    list.add(new LearningRequest(reqId, sId, sName, rId, rName, offId, offName, reqSkillId, reqSkillName, status, createdAt));
                }
            }
        }
        return list;
    }
}
