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

package com.njlabs.amrita.aid.util.ark;

/***************************************************************
 - moved to the net.matuschek.util tree by Daniel Matuschek
 - replaced deprecated getBytes() method in method decode
 - added String encode(String) method to encode a String to
 base64
 ***************************************************************/

/**
 * Base64 encoder/decoder.  Does not stream, so be careful with
 * using large amounts of data
 *
 * @author Nate Sammons
 * @author Daniel Matuschek
 * @version $Id: Base64.java,v 1.4 2001/04/17 10:09:27 matuschd Exp $
 */
public class Base64 {

    private Base64() {
        super();
    }

    /**
     *  Encode some data and return a String.
     */
    public static String encode(byte[] d) {
        if (d == null) return null;
        byte data[] = new byte[d.length + 2];
        System.arraycopy(d, 0, data, 0, d.length);
        byte dest[] = new byte[(data.length / 3) * 4];

        // 3-byte to 4-byte conversion
        for (int sidx = 0, didx = 0; sidx < d.length; sidx += 3, didx += 4) {
            dest[didx] = (byte) ((data[sidx] >>> 2) & 077);
            dest[didx + 1] = (byte) ((data[sidx + 1] >>> 4) & 017 |
                    (data[sidx] << 4) & 077);
            dest[didx + 2] = (byte) ((data[sidx + 2] >>> 6) & 003 |
                    (data[sidx + 1] << 2) & 077);
            dest[didx + 3] = (byte) (data[sidx + 2] & 077);
        }

        // 0-63 to ascii printable conversion
        for (int idx = 0; idx < dest.length; idx++) {
            if (dest[idx] < 26) dest[idx] = (byte) (dest[idx] + 'A');
            else if (dest[idx] < 52) dest[idx] = (byte) (dest[idx] + 'a' - 26);
            else if (dest[idx] < 62) dest[idx] = (byte) (dest[idx] + '0' - 52);
            else if (dest[idx] < 63) dest[idx] = (byte) '+';
            else dest[idx] = (byte) '/';
        }

        // add padding
        for (int idx = dest.length - 1; idx > (d.length * 4) / 3; idx--) {
            dest[idx] = (byte) '=';
        }
        return new String(dest);
    }

    /**
     * Encode a String using Base64 using the default platform encoding
     **/
    public static String encode(String s) {
        return encode(s.getBytes());
    }

    /**
     *  Decode data and return bytes.
     */
    public static byte[] decode(String str) {
        if (str == null) return null;
        byte data[] = str.getBytes();
        return decode(data);
    }

    /**
     *  Decode data and return bytes.  Assumes that the data passed
     *  in is ASCII text.
     */
    public static byte[] decode(byte[] data) {
        int tail = data.length;
        while (data[tail - 1] == '=') tail--;
        byte dest[] = new byte[tail - data.length / 4];

        // ascii printable to 0-63 conversion
        for (int idx = 0; idx < data.length; idx++) {
            if (data[idx] == '=') data[idx] = 0;
            else if (data[idx] == '/') data[idx] = 63;
            else if (data[idx] == '+') data[idx] = 62;
            else if (data[idx] >= '0' && data[idx] <= '9')
                data[idx] = (byte) (data[idx] - ('0' - 52));
            else if (data[idx] >= 'a' && data[idx] <= 'z')
                data[idx] = (byte) (data[idx] - ('a' - 26));
            else if (data[idx] >= 'A' && data[idx] <= 'Z')
                data[idx] = (byte) (data[idx] - 'A');
        }

        // 4-byte to 3-byte conversion
        int sidx, didx;
        for (sidx = 0, didx = 0; didx < dest.length - 2; sidx += 4, didx += 3) {
            dest[didx] = (byte) (((data[sidx] << 2) & 255) |
                    ((data[sidx + 1] >>> 4) & 3));
            dest[didx + 1] = (byte) (((data[sidx + 1] << 4) & 255) |
                    ((data[sidx + 2] >>> 2) & 017));
            dest[didx + 2] = (byte) (((data[sidx + 2] << 6) & 255) |
                    (data[sidx + 3] & 077));
        }
        if (didx < dest.length) {
            dest[didx] = (byte) (((data[sidx] << 2) & 255) |
                    ((data[sidx + 1] >>> 4) & 3));
        }
        if (++didx < dest.length) {
            dest[didx] = (byte) (((data[sidx + 1] << 4) & 255) |
                    ((data[sidx + 2] >>> 2) & 017));
        }
        return dest;
    }

    /**
     *  A simple test that encodes and decodes the first commandline argument.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: Base64 string");
            System.exit(0);
        }
        try {
            String e = Base64.encode(args[0].getBytes());
            String d = new String(Base64.decode(e));
            System.out.println("Input   = '" + args[0] + "'");
            System.out.println("Encoded = '" + e + "'");
            System.out.println("Decoded = '" + d + "'");
        } catch (Exception x) {
            x.printStackTrace();
        }
    }
}