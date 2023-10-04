package com.example.demo.util;

public class DataUtil {
    private static final char KEY_ESCAPE = '\\';
    public static String likeSpecialToStr(String str) {
        str = str.trim();
        str = str.replace("_", KEY_ESCAPE + "_");
        str = str.replace("%", KEY_ESCAPE + "%");
        return str;
    }

    public static String makeLikeStr(String str) {
        if (isNullOrEmpty(str)) {
            return "%%";
        }
        return "%" + str + "%";
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().equals("");
    }
}
