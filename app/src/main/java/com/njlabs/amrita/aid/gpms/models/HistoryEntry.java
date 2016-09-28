/*
 * MIT License
 *
 * Copyright (c) 2016 Niranjan Rajendran
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
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
