/*
 *	This file is part of dhcp4java, a DHCP API for the Java language.
 *	(c) 2006 Stephan Hadinger
 *
 *	This library is free software; you can redistribute it and/or
 *	modify it under the terms of the GNU Lesser General Public
 *	License as published by the Free Software Foundation; either
 *	version 2.1 of the License, or (at your option) any later version.
 *
 *	This library is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *	Lesser General Public License for more details.
 *
 *	You should have received a copy of the GNU Lesser General Public
 *	License along with this library; if not, write to the Free Software
 *	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.dhcp4java.test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.BitSet;

/**
 * Number in hexadecimal format are used throughout Freenet.
 * 
 * <p>Unless otherwise stated, the conventions follow the rules outlined in the 
 * Java Language Specification.</p>
 * 
 *  <at> author syoung
 */
public class HexUtils {
//	private static boolean logDEBUG =Logger.logger.shouldLog(Logger.DEBUG,HexUtil.class);

    private HexUtils() {
    	throw new UnsupportedOperationException();
    }

    /**
     * Converts a byte array into a string of upper case hex chars.
     *
     *  <at> param bs
     *            A byte array
     *  <at> param off
     *            The index of the first byte to read
     *  <at> param length
     *            The number of bytes to read.
     *  <at> return the string of hex chars.
     */
    public static String bytesToHex(byte[] bs, int off, int length) {
        StringBuffer sb = new StringBuffer(length * 2);
        bytesToHexAppend(bs, off, length, sb);
        return sb.toString();
    }

    public static void bytesToHexAppend(
        byte[] bs,
        int off,
        int length,
        StringBuffer sb) {
        sb.ensureCapacity(sb.length() + length * 2);
        for (int i = off; i < (off + length) && i < bs.length; i++) {
            sb.append(Character.forDigit((bs[i] >>> 4) & 0xf, 16))
              .append(Character.forDigit( bs[i]        & 0xf, 16));
        }
    }

    public static String bytesToHex(byte[] bs) {
        return bytesToHex(bs, 0, bs.length);
    }

    public static byte[] hexToBytes(String s) {
        return hexToBytes(s, 0);
    }

    public static byte[] hexToBytes(String s, int off) {
        byte[] bs = new byte[off + (1 + s.length()) / 2];
        hexToBytes(s, bs, off);
        return bs;
    }

    /**
     * Converts a String of hex characters into an array of bytes.
     *
     *  <at> param s
     *            A string of hex characters (upper case or lower) of even
     *            length.
     *  <at> param out
     *            A byte array of length at least s.length()/2 + off
     *  <at> param off
     *            The first byte to write of the array
     */
    public static void hexToBytes(String s, byte[] out, int off)
        throws NumberFormatException, IndexOutOfBoundsException {
        int slen = s.length();
        if ((slen % 2) != 0) {
            s = '0' + s;
        }

        if (out.length < off + slen / 2) {
            throw new IndexOutOfBoundsException(
                "Output buffer too small for input (" + out.length + '<' + off + slen / 2 + ')');
        }

        // Safe to assume the string is even length
        for (int i = 0; i < slen; i += 2) {
            byte b1 = (byte) Character.digit(s.charAt(i),     16);
            byte b2 = (byte) Character.digit(s.charAt(i + 1), 16);

            if (b1 < 0 || b2 < 0) {
                throw new NumberFormatException();
            }
            out[off + i / 2] = (byte) (b1 << 4 | b2);
        }
    }

    /**
     * Pack the bits in ba into a byte[].
     */
    public static byte[] bitsToBytes(BitSet ba, int size) {
        int    bytesAlloc = countBytesForBits(size);
        byte[] b          = new byte[bytesAlloc];

        for (int i = 0; i < b.length; i++) {
            short s = 0;

            for (int j = 0; j < 8; j++) {
                int     idx = i * 8 + j;
                boolean val = (idx <= size && ba.get(idx));

                s |= (val ? (1 << j) : 0);
            }

            if (s > 255) {
                throw new IllegalStateException("WTF? s = " + s);
            }

            b[i] = (byte) s;
        }
        return b;
    }

    /**
     * Pack the bits in ba into a byte[] then convert that
     * to a hex string and return it.
     */
    public static String bitsToHexString(BitSet ba, int size) {
        return bytesToHex(bitsToBytes(ba, size));
    }

    /**
     *  <at> return the number of bytes required to represent the
     * bitset
     */
    public static int countBytesForBits(int size) {
        // Brackets matter here! == takes precedence over the rest
        return (size/8) + ((size % 8) == 0 ? 0:1);
    }

    /**
     * Read bits from a byte array into a bitset
     *  <at> param b the byte[] to read from
     *  <at> param ba the bitset to write to
     */
    public static void bytesToBits(byte[] b, BitSet ba, int maxSize) {
        int x = 0;

        for (byte aB : b) {
            int j = 0;

            while (j < 8 && x <= maxSize) {
                int     mask  = 1 << j;
                boolean value = (mask & aB) != 0;

                ba.set(x, value);
                x++;
                j++;
            }
        }
    }

    /**
     * Read a hex string of bits and write it into a bitset
     *  <at> param s hex string of the stored bits
     *  <at> param ba the bitset to store the bits in
     *  <at> param length the maximum number of bits to store
     */
    public static void hexToBits(String s, BitSet ba, int length) {
        byte[] b = hexToBytes(s);
        bytesToBits(b, ba, length);
    }

    /**
     * Write a (reasonably short) BigInteger to a stream.
     *  <at> param integer the BigInteger to write
     *  <at> param out the stream to write it to
     */
    public static void writeBigInteger(BigInteger integer, DataOutputStream out) throws IOException {
        if (integer.signum() == -1) {
            //dump("Negative BigInteger", Logger.ERROR, true);
            throw new IllegalStateException("Negative BigInteger!");
        }
        byte[] buf = integer.toByteArray();
        if (buf.length > Short.MAX_VALUE) {
            throw new IllegalStateException("Too long: " + buf.length);
        }
        out.writeShort((short)buf.length);
        out.write(buf);
    }

    /**
     * Read a (reasonably short) BigInteger from a DataInputStream
     *  <at> param dis the stream to read from
     *  <at> return a BigInteger
     */
    public static BigInteger readBigInteger(DataInputStream dis) throws IOException {
        short i = dis.readShort();
        if(i < 0) {
            throw new IOException("Invalid BigInteger length: " + i);
        }
        byte[] buf = new byte[i];
        dis.readFully(buf);
        return new BigInteger(1,buf);
    }

    /**
     * Turn a BigInteger into a hex string.
     * BigInteger.toString(16) NPEs on Sun JDK 1.4.2_05. :<
     * The bugs in their Big* are getting seriously irritating...
     */
    public static String biToHex(BigInteger bi) {
        return bytesToHex(bi.toByteArray());
    }
}