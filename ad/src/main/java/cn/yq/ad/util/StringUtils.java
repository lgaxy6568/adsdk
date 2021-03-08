package cn.yq.ad.util;

public class StringUtils {
    public static boolean isEmpty(String s){
        return s == null || s.trim().length() == 0;
    }
    public static boolean isNotEmpty(String s){
        return s != null && s.trim().length() > 0;
    }
}
