package com.skillchain.model;

import java.time.LocalDateTime;

/**
 * ============================================================================
 * JAVA OOP CONCEPT: CLASSES & ENCAPSULATION
 * ============================================================================
 * Represents a reciprocal skill exchange proposal between two students.
 */
public class LearningRequest {

    /**
     * Meaningful Array Usage:
     * Fixed set of possible statuses for a learning request.
     */
    public static final String[] STATUS_ARRAY = new String[]{"PENDING", "ACCEPTED", "REJECTED"};

    private int requestId;
    private int senderId;
    private String senderName;
    private int receiverId;
    private String receiverName;
    private int offeredSkillId;
    private String offeredSkillName;
    private int requestedSkillId;
    private String requestedSkillName;
    private String status; // PENDING, ACCEPTED, REJECTED
    private LocalDateTime createdAt;

    public LearningRequest() {
    }

    public LearningRequest(int requestId, int senderId, String senderName, int receiverId, String receiverName,
                           int offeredSkillId, String offeredSkillName, int requestedSkillId, String requestedSkillName,
                           String status, LocalDateTime createdAt) {
        this.requestId = requestId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.offeredSkillId = offeredSkillId;
        this.offeredSkillName = offeredSkillName;
        this.requestedSkillId = requestedSkillId;
        this.requestedSkillName = requestedSkillName;
        this.status = status;
        this.createdAt = createdAt;
    }

    // ========================================================================
    // ENCAPSULATION: Getters and Setters
    // ========================================================================

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public int getOfferedSkillId() {
        return offeredSkillId;
    }

    public void setOfferedSkillId(int offeredSkillId) {
        this.offeredSkillId = offeredSkillId;
    }

    public String getOfferedSkillName() {
        return offeredSkillName;
    }

    public void setOfferedSkillName(String offeredSkillName) {
        this.offeredSkillName = offeredSkillName;
    }

    public int getRequestedSkillId() {
        return requestedSkillId;
    }

    public void setRequestedSkillId(int requestedSkillId) {
        this.requestedSkillId = requestedSkillId;
    }

    public String getRequestedSkillName() {
        return requestedSkillName;
    }

    public void setRequestedSkillName(String requestedSkillName) {
        this.requestedSkillName = requestedSkillName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
