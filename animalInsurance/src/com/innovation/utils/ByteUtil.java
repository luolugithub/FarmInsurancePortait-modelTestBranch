package com.innovation.utils;

/**
 * Created by Luolu on 2018/11/16.
 * InnovationAI
 * luolu@innovationai.cn
 */

import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * In Java, it don't support unsigned int, so we use char to replace uint8.
 * The range of byte is [-128,127], and the range of char is [0,65535].
 * So the byte could used to store the uint8.
 * (We assume that the String could be mapped to assic)
 * @author afunx
 *
 */
public class ByteUtil {


    public static char[] convertBytes2Uint8s(byte[] bytes) {
        int len = bytes.length;
        char[] uint8s = new char[len];
        for (int i = 0; i < len; i++) {
            uint8s[i] = convertByte2Uint8(bytes[i]);
        }
        return uint8s;
    }

    public static char convertByte2Uint8(byte b) {
        // char will be promoted to int for char don't support & operator
        // & 0xff could make negatvie value to positive
        return (char) (b & 0xff);
    }

}
