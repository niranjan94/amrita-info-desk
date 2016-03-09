/*
 * Copyright (c) 2016. Niranjan Rajendran <niranjan94@yahoo.com>
 */

package com.njlabs.amrita.aid.gpms.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Relay implements Parcelable {
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
