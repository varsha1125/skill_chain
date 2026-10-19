package com.skillchain.service;

import com.skillchain.dao.LearningRequestDao;
import com.skillchain.dao.LearningRequestDaoImpl;
import com.skillchain.dao.UserDao;
import com.skillchain.dao.UserDaoImpl;
import com.skillchain.model.ActivePartnership;
import com.skillchain.model.AppNotification;
import com.skillchain.model.LearningRequest;
import com.skillchain.model.PeerStudent;
import com.skillchain.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * SERVICE LAYER: EXCHANGE REQUESTS, PARTNERSHIPS & NOTIFICATIONS
 * ============================================================================
 * Coordinates creating, listing, accepting, and tracking peer invitations,
 * active study partnerships, peer student directory, and notification events.
 */
public class RequestService {

    private final LearningRequestDao requestDao;
    private final UserDao userDao;

    public RequestService() {
        this(new LearningRequestDaoImpl(), new UserDaoImpl());
    }

    public RequestService(LearningRequestDao requestDao, UserDao userDao) {
        this.requestDao = requestDao;
        this.userDao = userDao;
    }

    /**
     * Sends a new skill exchange request to a peer.
     *
     * @param senderId         Sending student ID
     * @param receiverId       Target classmate ID
     * @param offeredSkillId   Skill the sender offers to teach
     * @param requestedSkillId Skill the sender wishes to learn
     * @return Persisted LearningRequest
     * @throws IllegalStateException if a duplicate active request exists
     * @throws SQLException          if a database error occurs
     */
    public LearningRequest sendRequest(int senderId, int receiverId, int offeredSkillId, int requestedSkillId)
            throws SQLException, IllegalStateException {

        if (senderId == receiverId) {
            throw new IllegalArgumentException("Cannot send a skill swap request to yourself.");
        }

        if (requestDao.existsActiveRequest(senderId, receiverId, offeredSkillId, requestedSkillId)) {
            throw new IllegalStateException("An active exchange proposal already exists with this partner for these skills.");
        }

        LearningRequest req = new LearningRequest();
        req.setSenderId(senderId);
        req.setReceiverId(receiverId);
        req.setOfferedSkillId(offeredSkillId);
        req.setRequestedSkillId(requestedSkillId);
        req.setStatus("PENDING");

        return requestDao.save(req);
    }

    public List<LearningRequest> getIncomingRequests(int userId) throws SQLException {
        return requestDao.findIncomingByUserId(userId);
    }

    public List<LearningRequest> getOutgoingRequests(int userId) throws SQLException {
        return requestDao.findOutgoingByUserId(userId);
    }

    public boolean acceptRequest(int requestId) throws SQLException {
        return requestDao.updateStatus(requestId, "ACCEPTED");
    }

    public boolean rejectRequest(int requestId) throws SQLException {
        return requestDao.updateStatus(requestId, "REJECTED");
    }

    public int getPendingIncomingCount(int userId) throws SQLException {
        return requestDao.countPendingIncoming(userId);
    }

    /**
     * Returns student peers on campus along with the skills they teach and seek to learn.
     */
    public List<PeerStudent> getPeerDirectory(int excludeUserId) throws SQLException {
        return userDao.findAllPeersWithSkills(excludeUserId);
    }

    /**
     * Returns all verified, active learning partnerships (where proposal status is ACCEPTED).
     */
    public List<ActivePartnership> getActivePartnerships(int userId) throws SQLException {
        final String sql =
                "SELECT lr.request_id, " +
                "       CASE WHEN lr.sender_id = ? THEN lr.receiver_id ELSE lr.sender_id END AS partner_id, " +
                "       CASE WHEN lr.sender_id = ? THEN u_recv.name ELSE u_send.name END AS partner_name, " +
                "       CASE WHEN lr.sender_id = ? THEN u_recv.email ELSE u_send.email END AS partner_email, " +
                "       CASE WHEN lr.sender_id = ? THEN s_off.skill_name ELSE s_req.skill_name END AS you_teach, " +
                "       CASE WHEN lr.sender_id = ? THEN s_req.skill_name ELSE s_off.skill_name END AS partner_teaches, " +
                "       lr.status, lr.created_at " +
                "FROM learning_requests lr " +
                "JOIN users u_send ON lr.sender_id = u_send.user_id " +
                "JOIN users u_recv ON lr.receiver_id = u_recv.user_id " +
                "JOIN skills s_off ON lr.offered_skill_id = s_off.skill_id " +
                "JOIN skills s_req ON lr.requested_skill_id = s_req.skill_id " +
                "WHERE (lr.sender_id = ? OR lr.receiver_id = ?) AND lr.status = 'ACCEPTED' " +
                "ORDER BY lr.created_at DESC";

        List<ActivePartnership> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);
            stmt.setInt(4, userId);
            stmt.setInt(5, userId);
            stmt.setInt(6, userId);
            stmt.setInt(7, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp("created_at");
                    list.add(new ActivePartnership(
                            rs.getInt("request_id"),
                            rs.getInt("partner_id"),
                            rs.getString("partner_name"),
                            rs.getString("partner_email"),
                            rs.getString("you_teach"),
                            rs.getString("partner_teaches"),
                            rs.getString("status"),
                            ts != null ? ts.toLocalDateTime() : LocalDateTime.now()
                    ));
                }
            }
        }
        return list;
    }

    /**
     * Retrieves all notification events for the student, including:
     * - Accepted exchange proposals from partners
     * - Incoming pending proposals awaiting review
     */
    public List<AppNotification> getNotifications(int userId) throws SQLException {
        List<AppNotification> notifications = new ArrayList<>();

        // 1. Accepted proposals (your sent invitation was accepted by classmate)
        final String acceptedSql =
                "SELECT lr.request_id, u_recv.name AS partner_name, u_recv.email AS partner_email, " +
                "       s_off.skill_name AS off_skill, s_req.skill_name AS req_skill, lr.created_at " +
                "FROM learning_requests lr " +
                "JOIN users u_recv ON lr.receiver_id = u_recv.user_id " +
                "JOIN skills s_off ON lr.offered_skill_id = s_off.skill_id " +
                "JOIN skills s_req ON lr.requested_skill_id = s_req.skill_id " +
                "WHERE lr.sender_id = ? AND lr.status = 'ACCEPTED' " +
                "ORDER BY lr.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(acceptedSql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String pName = rs.getString("partner_name");
                    String pEmail = rs.getString("partner_email");
                    String offSkill = rs.getString("off_skill");
                    String reqSkill = rs.getString("req_skill");
                    Timestamp ts = rs.getTimestamp("created_at");

                    notifications.add(new AppNotification(
                            rs.getInt("request_id"),
                            AppNotification.NotificationType.ACCEPTED,
                            "Exchange Proposal Accepted! 🎉",
                            pName + " (" + pEmail + ") accepted your request! You teach " + offSkill + " ⇄ they teach " + reqSkill + ".",
                            pName, pEmail,
                            ts != null ? ts.toLocalDateTime() : LocalDateTime.now()
                    ));
                }
            }
        }

        // 2. Incoming pending proposals (classmate sent you an exchange request)
        final String reqSql =
                "SELECT lr.request_id, u_send.name AS partner_name, u_send.email AS partner_email, " +
                "       s_off.skill_name AS off_skill, s_req.skill_name AS req_skill, lr.created_at " +
                "FROM learning_requests lr " +
                "JOIN users u_send ON lr.sender_id = u_send.user_id " +
                "JOIN skills s_off ON lr.offered_skill_id = s_off.skill_id " +
                "JOIN skills s_req ON lr.requested_skill_id = s_req.skill_id " +
                "WHERE lr.receiver_id = ? AND lr.status = 'PENDING' " +
                "ORDER BY lr.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(reqSql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String pName = rs.getString("partner_name");
                    String pEmail = rs.getString("partner_email");
                    String offSkill = rs.getString("off_skill");
                    String reqSkill = rs.getString("req_skill");
                    Timestamp ts = rs.getTimestamp("created_at");

                    notifications.add(new AppNotification(
                            rs.getInt("request_id"),
                            AppNotification.NotificationType.REQUEST_RECEIVED,
                            "New Skill Exchange Proposal! 📬",
                            pName + " (" + pEmail + ") wants to learn " + reqSkill + " from you and offers to teach " + offSkill + ".",
                            pName, pEmail,
                            ts != null ? ts.toLocalDateTime() : LocalDateTime.now()
                    ));
                }
            }
        }

        notifications.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        return notifications;
    }
}
