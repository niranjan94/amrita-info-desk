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

import android.os.Parcel;
import android.os.Parcelable;

public class Relay implements Parcelable {
    public static final Creator<Relay> CREATOR = new Creator<Relay>() {
        @Override
        public Relay createFromParcel(Parcel in) {
            return new Relay(in);
        }

        @Override
        public Relay[] newArray(int size) {
            return new Relay[size];
        }
    };
    private String identifier;
    private int signalLevel;

    public Relay(String identifier, int signalLevel) {
        this.identifier = identifier;
        this.signalLevel = signalLevel;
    }

    protected Relay(Parcel in) {
        identifier = in.readString();
        signalLevel = in.readInt();
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public int getSignalLevel() {
        return signalLevel;
    }

    public void setSignalLevel(int signalLevel) {
        this.signalLevel = signalLevel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(identifier);
        dest.writeInt(signalLevel);
    }
}
