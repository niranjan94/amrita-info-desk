/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.gpms.models;

public class PendingEntry {
    String id;
    String appliedFrom;
    String appliedTill;
    String passType;
    String requestedWith;
    String approvalStatus;
    String cfwApproval;
    String status;

    public PendingEntry() {
    }

    public PendingEntry(String appliedFrom, String appliedTill, String approvalStatus, String cfwApproval, String id, String passType, String requestedWith, String status) {
        this.appliedFrom = appliedFrom;
        this.appliedTill = appliedTill;
        this.approvalStatus = approvalStatus;
        this.cfwApproval = cfwApproval;
        this.id = id;
        this.passType = passType;
        this.requestedWith = requestedWith;
        this.status = status;
    }

    public String getAppliedFrom() {
        return appliedFrom;
    }

    public void setAppliedFrom(String appliedFrom) {
        this.appliedFrom = appliedFrom;
    }

    public String getAppliedTill() {
        return appliedTill;
    }

    public void setAppliedTill(String appliedTill) {
        this.appliedTill = appliedTill;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getCfwApproval() {
        return cfwApproval;
    }

    public void setCfwApproval(String cfwApproval) {
        this.cfwApproval = cfwApproval;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassType() {
        return passType;
    }

    public void setPassType(String passType) {
        this.passType = passType;
    }

    public String getRequestedWith() {
        return requestedWith;
    }

    public void setRequestedWith(String requestedWith) {
        this.requestedWith = requestedWith;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
