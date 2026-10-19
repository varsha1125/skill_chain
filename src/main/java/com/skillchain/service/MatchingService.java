package com.skillchain.service;

import com.skillchain.model.ReciprocalMatch;
import com.skillchain.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * SERVICE LAYER: SMART RECIPROCAL MATCHING ENGINE
 * ============================================================================
 * Discovers mutual peer exchange opportunities by identifying dual-conditional
 * intersections between student skill portfolios.
 */
public class MatchingService {

    private static final String RECIPROCAL_MATCH_SQL =
            "SELECT " +
            "    u_partner.user_id AS partner_id, " +
            "    u_partner.name AS partner_name, " +
            "    u_partner.email AS partner_email, " +
            "    s_offer.skill_id AS user_offered_id, " +
            "    s_offer.skill_name AS user_offered_name, " +
            "    s_offer.category AS user_offered_category, " +
            "    s_partner.skill_id AS partner_offered_id, " +
            "    s_partner.skill_name AS partner_offered_name, " +
            "    s_partner.category AS partner_offered_category " +
            "FROM user_skills my_teach " +
            "JOIN user_skills partner_learn " +
            "    ON my_teach.skill_id = partner_learn.skill_id " +
            "   AND my_teach.skill_type = 'TEACH' " +
            "   AND partner_learn.skill_type = 'LEARN' " +
            "   AND partner_learn.user_id != my_teach.user_id " +
            "JOIN user_skills partner_teach " +
            "    ON partner_teach.user_id = partner_learn.user_id " +
            "   AND partner_teach.skill_type = 'TEACH' " +
            "JOIN user_skills my_learn " +
            "    ON my_learn.skill_id = partner_teach.skill_id " +
            "   AND my_learn.user_id = my_teach.user_id " +
            "   AND my_learn.skill_type = 'LEARN' " +
            "JOIN users u_partner ON u_partner.user_id = partner_learn.user_id " +
            "JOIN skills s_offer ON s_offer.skill_id = my_teach.skill_id " +
            "JOIN skills s_partner ON s_partner.skill_id = partner_teach.skill_id " +
            "WHERE my_teach.user_id = ? " +
            "ORDER BY u_partner.name ASC";

    /**
     * Identifies all reciprocal matches for the given student.
     *
     * @param currentUserId The active student's user ID.
     * @return List of verified reciprocal matches.
     * @throws SQLException if a database query failure occurs.
     */
    public List<ReciprocalMatch> findReciprocalMatches(int currentUserId) throws SQLException {
        List<ReciprocalMatch> matches = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(RECIPROCAL_MATCH_SQL)) {

            stmt.setInt(1, currentUserId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int partnerId = rs.getInt("partner_id");
                    String partnerName = rs.getString("partner_name");
                    String partnerEmail = rs.getString("partner_email");

                    int offerId = rs.getInt("user_offered_id");
                    String offerName = rs.getString("user_offered_name");
                    String offerCat = rs.getString("user_offered_category");

                    int reqId = rs.getInt("partner_offered_id");
                    String reqName = rs.getString("partner_offered_name");
                    String reqCat = rs.getString("partner_offered_category");

                    matches.add(new ReciprocalMatch(
                            partnerId, partnerName, partnerEmail,
                            offerId, offerName, offerCat,
                            reqId, reqName, reqCat
                    ));
                }
            }
        }

        return matches;
    }

    /**
     * Quick count of reciprocal matches for the dashboard KPI metric card.
     *
     * @param currentUserId The active student's user ID.
     * @return Total count of mutual swap opportunities.
     * @throws SQLException if a database query failure occurs.
     */
    public int getReciprocalMatchCount(int currentUserId) throws SQLException {
        return findReciprocalMatches(currentUserId).size();
    }
}
