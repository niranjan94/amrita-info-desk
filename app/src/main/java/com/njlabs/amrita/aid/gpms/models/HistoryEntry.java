/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.gpms.models;

import com.google.gson.annotations.SerializedName;

public class HistoryEntry {

    @SerializedName("departure_time")
    private String departureTime;

    @SerializedName("arrival_time")
    private String arrivalTime;

    @SerializedName("num_days")
    private String numDays;

    @SerializedName("actual_num_days")
    private String actualNumDays;

    @SerializedName("pass_type")
    private String passType;

    @SerializedName("occasion")
    private String occasion;

    @SerializedName("approval_status")
    private String approvalStatus;

    @SerializedName("cfw_approval")
    private String cfwApproval;

    @SerializedName("gate_approval")
    private String gateApproval;

    @SerializedName("cancellation")
    private String cancellation;

    public HistoryEntry() {
    }

    public HistoryEntry(String actualNumDays, String approvalStatus, String arrivalTime, String cancellation, String cfwApproval, String departureTime, String gateApproval, String numDays, String occasion, String passType) {
        this.actualNumDays = actualNumDays;
        this.approvalStatus = approvalStatus;
        this.arrivalTime = arrivalTime;
        this.cancellation = cancellation;
        this.cfwApproval = cfwApproval;
        this.departureTime = departureTime;
        this.gateApproval = gateApproval;
        this.numDays = numDays;
        this.occasion = occasion;
        this.passType = passType;
    }

    public String getActualNumDays() {
        return actualNumDays;
    }

    public void setActualNumDays(String actualNumDays) {
        this.actualNumDays = actualNumDays;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getCancellation() {
        return cancellation;
    }

    public void setCancellation(String cancellation) {
        this.cancellation = cancellation;
    }

    public String getCfwApproval() {
        return cfwApproval;
    }

    public void setCfwApproval(String cfwApproval) {
        this.cfwApproval = cfwApproval;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getGateApproval() {
        return gateApproval;
    }

    public void setGateApproval(String gateApproval) {
        this.gateApproval = gateApproval;
    }

    public String getNumDays() {
        return numDays;
    }

    public void setNumDays(String numDays) {
        this.numDays = numDays;
    }

    public String getOccasion() {
        return occasion;
    }

    public void setOccasion(String occasion) {
        this.occasion = occasion;
    }

    public String getPassType() {
        return passType;
    }

    public void setPassType(String passType) {
        this.passType = passType;
    }
}
