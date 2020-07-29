package com.thc.platform.modules.zhilo;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import sun.security.provider.MD4;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ResultClass {
    public static void main(String[] args) throws UnsupportedEncodingException {
        String str =new String("73.25".getBytes(StandardCharsets.UTF_8));
        for (int i = 0; i < 100000000; i++) {
            str = md2(str);
            if (i == 100000000-1){
                System.out.println(str);
            }
        }

    }
    private static String md5(String src) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(src.getBytes());
            return HexBin.encode(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    private static String md2(String src) {
        try {
            MessageDigest md2 = MessageDigest.getInstance("MD2");
            byte[] digest = md2.digest(src.getBytes());
            return HexBin.encode(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * md4 jdk 本身不提供，这里使用sun
     */
    private static String md4(String src) {
        try {
            MessageDigest md4 = MD4.getInstance();
            byte[] digest = md4.digest(src.getBytes());
            return HexBin.encode(digest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
