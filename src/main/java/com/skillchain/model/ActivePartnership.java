package com.skillchain.model;

import java.time.LocalDateTime;

/**
 * ============================================================================
 * JAVA OOP CONCEPT: CLASSES & ENCAPSULATION
 * ============================================================================
 * Represents an ongoing, active learning partnership established between
 * two students whose skill exchange proposal was accepted.
 */
public class ActivePartnership {

    private int requestId;
    private int partnerId;
    private String partnerName;
    private String partnerEmail;
    private String youTeachSkill;
    private String partnerTeachesSkill;
    private String status;
    private LocalDateTime establishedDate;

    public ActivePartnership() {
    }

    public ActivePartnership(int requestId, int partnerId, String partnerName, String partnerEmail,
                             String youTeachSkill, String partnerTeachesSkill, String status,
                             LocalDateTime establishedDate) {
        this.requestId = requestId;
        this.partnerId = partnerId;
        this.partnerName = partnerName;
        this.partnerEmail = partnerEmail;
        this.youTeachSkill = youTeachSkill;
        this.partnerTeachesSkill = partnerTeachesSkill;
        this.status = status;
        this.establishedDate = establishedDate;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(int partnerId) {
        this.partnerId = partnerId;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getPartnerEmail() {
        return partnerEmail;
    }

    public void setPartnerEmail(String partnerEmail) {
        this.partnerEmail = partnerEmail;
    }

    public String getYouTeachSkill() {
        return youTeachSkill;
    }

    public void setYouTeachSkill(String youTeachSkill) {
        this.youTeachSkill = youTeachSkill;
    }

    public String getPartnerTeachesSkill() {
        return partnerTeachesSkill;
    }

    public void setPartnerTeachesSkill(String partnerTeachesSkill) {
        this.partnerTeachesSkill = partnerTeachesSkill;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getEstablishedDate() {
        return establishedDate;
    }

    public void setEstablishedDate(LocalDateTime establishedDate) {
        this.establishedDate = establishedDate;
    }
}
