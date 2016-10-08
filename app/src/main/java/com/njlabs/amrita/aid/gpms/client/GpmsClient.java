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

import android.content.Context;

import com.njlabs.amrita.aid.util.Identifier;
import com.njlabs.amrita.aid.util.okhttp.Client;
import com.njlabs.amrita.aid.util.okhttp.extras.PersistentCookieStore;

import java.net.URLEncoder;

public class GpmsClient extends Client {

    public String PROXY_URL = "https://aid.codezero.xyz/wormhole/open.php?warp-factor=9.6";
    public String COOKIE_FILE = PersistentCookieStore.GPMS_COOKIE_PREFS;
    private Context context;

    public GpmsClient(Context context) {
        super(context);
        this.context = context;
        this.BASE_URL = "http://gpms.ettimadai.net/gpis/student";
    }

    public GpmsClient(Context context, String COOKIE_FILE) {
        this(context);
        this.context = context;
        this.COOKIE_FILE = COOKIE_FILE;
    }

    @Override
    protected String getAbsoluteUrl(String relativeUrl) {
        if (isProxyOn()) {
            return PROXY_URL + "&u=" + URLEncoder.encode(BASE_URL + relativeUrl);
        } else {
            return getUnproxiedUrl(relativeUrl);
        }

    }

    public String getUnproxiedUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    @Override
    protected String getCookieFile() {
        return COOKIE_FILE;
    }

    @Override
    protected boolean setLoggingEnabled() {
        return false;
    }

    @Override
    protected boolean shouldVerifySSL() {
        return false;
    }

    protected boolean isProxyOn() {
        return !Identifier.isConnectedToAmrita(this.context);
    }
}
