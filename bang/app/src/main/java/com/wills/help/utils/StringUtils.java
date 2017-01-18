package com.wills.help.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * com.wills.help.utils
 * Created by lizhaoyong
 * 2016/12/12.
 */

public class StringUtils {

    /**
     * MD5
     * @param string
     * @return
     */
    public static String getMD5(String string){
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            try {
                messageDigest.update(string.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] bytes  = messageDigest.digest();
        StringBuffer buffer = new StringBuffer();
        for (int i =0;i<bytes.length;i++){
            if (Integer.toHexString(0xFF & bytes[i]).length() == 1)
                buffer.append("0").append(Integer.toHexString(0xFF & bytes[i]));
            else
                buffer.append(Integer.toHexString(0xFF & bytes[i]));
        }
        return buffer.toString();
    }

    /**
     * 判断手机号码
     * @param phoneNumber
     * @return
     */
    public static boolean availablePhone(String phoneNumber) {
        Pattern pattern = Pattern.compile("^((13[0-9])|(15[0-9])|(18[0-9])|(17[0-9])|(14[0-9]))\\d{8}$");
        Matcher m = pattern.matcher(phoneNumber);
        return m.matches();
    }

    /***
     * 判断字符串是否为空
     * @param text
     * @return
     */
    public static boolean isNullOrEmpty(String text) {
        if (text == null || "".equals(text.trim()) || text.trim().length() == 0
                || "null".equals(text.trim())) {
            return true;
        } else {
            return false;
        }
    }
}
