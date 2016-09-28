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

package com.njlabs.amrita.aid.gpms.client;

import com.njlabs.amrita.aid.gpms.responses.HistoryResponse;
import com.njlabs.amrita.aid.gpms.responses.InfoResponse;
import com.njlabs.amrita.aid.gpms.responses.LoginResponse;
import com.njlabs.amrita.aid.gpms.responses.PendingResponse;
import com.njlabs.amrita.aid.util.okhttp.responses.SuccessResponse;

import org.joda.time.DateTime;

public class GpmsSlave {

    public static void applyDayPass(final Gpms gpms, String rollNo, String password, final DateTime fromDate, final String occasion, final String reason, final SuccessResponse successResponse) {

        gpms.login(rollNo, password, new InfoResponse() {
            @Override
            public void onSuccess(String regNo, String name, String hostel, String roomNo, String mobile, String email, String photoUrl, String numPasses) {
                gpms.applyDayPass(fromDate, occasion, reason, successResponse);
            }

            @Override
            public void onFailedAuthentication() {
                successResponse.onFailure(new Exception("failed_authentication"));
            }

            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(Throwable throwable) {
                successResponse.onFailure(throwable);
            }
        });
    }

    public static void applyHomePass(final Gpms gpms, String rollNo, String password, final DateTime fromDate, final DateTime toDate, final String occasion, final String reason, final SuccessResponse successResponse) {
        gpms.login(rollNo, password, new InfoResponse() {
            @Override
            public void onSuccess(String regNo, String name, String hostel, String roomNo, String mobile, String email, String photoUrl, String numPasses) {
                gpms.applyHomePass(fromDate, toDate, occasion, reason, successResponse);
            }

            @Override
            public void onFailedAuthentication() {
                successResponse.onFailure(new Exception("failed_authentication"));
            }

            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(Throwable throwable) {
                successResponse.onFailure(throwable);
            }
        });
    }


    public static void getPendingPasses(final Gpms gpms, String rollNo, String password, final PendingResponse pendingResponse) {
        gpms.basicLogin(rollNo, password, new LoginResponse() {
            @Override
            public void onFailedAuthentication() {
                pendingResponse.onFailure(new Exception("failed_authentication"));
            }

            @Override
            public void onSuccess() {
                gpms.getPendingPasses(pendingResponse);
            }

            @Override
            public void onFailure(Throwable throwable) {
                pendingResponse.onFailure(throwable);
            }
        });
    }

    public static void cancelPass(final Gpms gpms, String rollNo, String password, final String requestId, final SuccessResponse successResponse) {
        gpms.basicLogin(rollNo, password, new LoginResponse() {
            @Override
            public void onFailedAuthentication() {
                successResponse.onFailure(new Exception("failed_authentication"));
            }

            @Override
            public void onSuccess() {
                gpms.cancelPass(requestId, successResponse);
            }

            @Override
            public void onFailure(Throwable throwable) {
                successResponse.onFailure(throwable);
            }
        });
    }

    public static void getPassesHistory(final Gpms gpms, String rollNo, String password, final HistoryResponse historyResponse) {
        gpms.basicLogin(rollNo, password, new LoginResponse() {
            @Override
            public void onFailedAuthentication() {
                historyResponse.onFailure(new Exception("failed_authentication"));
            }

            @Override
            public void onSuccess() {
                gpms.getPassesHistory(historyResponse);
            }

            @Override
            public void onFailure(Throwable throwable) {
                historyResponse.onFailure(throwable);
            }
        });

    }
}
