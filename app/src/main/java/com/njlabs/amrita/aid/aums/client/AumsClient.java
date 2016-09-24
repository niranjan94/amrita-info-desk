/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.aums.client;


import android.content.Context;

import com.njlabs.amrita.aid.util.okhttp.Client;
import com.njlabs.amrita.aid.util.okhttp.extras.PersistentCookieStore;

class AumsClient extends Client {

    AumsClient(Context context) {
        super(context);
    }

    @Override
    protected String getCookieFile() {
        return PersistentCookieStore.AUMS_COOKIE_PREFS;
    }

    @Override
    protected boolean setLoggingEnabled() {
        return false;
    }

    @Override
    protected boolean shouldVerifySSL() {
        return true;
    }

}
