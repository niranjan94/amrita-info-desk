/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.gpms.client;

import com.njlabs.amrita.aid.gpms.responses.HistoryResponse;
import com.njlabs.amrita.aid.gpms.responses.InfoResponse;
import com.njlabs.amrita.aid.gpms.responses.PendingResponse;
import com.njlabs.amrita.aid.util.okhttp.responses.SuccessResponse;

import org.joda.time.DateTime;

public interface AbstractGpms {
    void login(String rollNo, String password, final InfoResponse infoResponse);

    void applyDayPass(final DateTime fromDate, final String occasion, final String reason, final SuccessResponse successResponse);

    void applyHomePass(final DateTime fromDate, final DateTime toDate, final String occasion, final String reason, final SuccessResponse successResponse);

    void getPendingPasses(final PendingResponse pendingResponse);

    void cancelPass(String requestId, final SuccessResponse successResponse);

    void getPassesHistory(final HistoryResponse historyResponse);

    String getStudentName();

    String getStudentRollNo();

    void logout();
}
