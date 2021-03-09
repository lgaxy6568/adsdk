package cn.yq.ad.util;

public class AdStringUtils {
    public static boolean isEmpty(String s){
        return s == null || s.trim().length() == 0;
    }
    public static boolean isNotEmpty(String s){
        return s != null && s.trim().length() > 0;
    }
}
