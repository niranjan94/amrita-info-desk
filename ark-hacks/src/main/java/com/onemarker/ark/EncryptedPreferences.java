package com.onemarker.ark;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public class EncryptedPreferences extends ObscuredSharedPreferences {

    public EncryptedPreferences(Context context, SharedPreferences delegate) {
        super(context, delegate);
    }

    @Override
    protected char[] getSpecialCode() {
        return "md55266b0a0344b829ad01bcad2ea1dfddc".toCharArray();
    }

    @Override
    public Set<String> getStringSet(String s, Set<String> strings) {
        return null;
    }
}
