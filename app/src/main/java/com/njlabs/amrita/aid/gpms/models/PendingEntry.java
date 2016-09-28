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

public class PendingEntry {

    @SerializedName("id")
    private String id;

    @SerializedName("applied_from")
    private String appliedFrom;

    @SerializedName("applied_till")
    private String appliedTill;

    @SerializedName("pass_type")
    private String passType;

    @SerializedName("requested_with")
    private String requestedWith;

    @SerializedName("approval_status")
    private String approvalStatus;

    @SerializedName("cfw_approval")
    private String cfwApproval;

    @SerializedName("status")
    private String status;

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
