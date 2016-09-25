package com.njlabs.amrita.aid.util.ark;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Security {
    public static String encrypt(String input, String key) {
        byte[] encrypted = null;
        try {
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            encrypted = cipher.doFinal(input.getBytes());
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return Base64.encode(encrypted);
    }

    public static String decrypt(String input, String key) {
        byte[] output = null;
        try {
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skey);
            output = cipher.doFinal(Base64.decode(input));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return new String(output != null ? output : new byte[0]);
    }
}