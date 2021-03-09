package cn.yq.ad.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ADHelper {
    public static String replaceTrim_R_N(String str){
        if(str == null)
            return "";
        return str.replaceAll("\\s","");
    }
    public static boolean isEmpty(String s){
        return s == null || s.trim().length() == 0;
    }
    public static boolean isNotEmpty(String s){
        return s != null && s.trim().length() > 0;
    }
    public static String appendParams(String url, Map<String, String> params){
        StringBuilder sb = new StringBuilder(url);
        if(params != null && params.size() > 0){
            Uri uri = Uri.parse(url);
            String query = uri.getQuery();
            boolean hasQuery = false;
            if(query != null && query.trim().length() > 0){
                hasQuery=true;
            }
            for (String key : params.keySet()) {
                String value = params.get(key);
                if(value == null) {
                    continue;
                }
                if(hasQuery){
                    sb.append("&");
                }else{
                    sb.append("?");
                    hasQuery=true;
                }
//                try {
//                    value = Uri.encode(value);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                sb.append(key).append("=").append(value);
            }
        }
        return sb.toString();
    }

    public static Activity getActivityFromView(View view) {
        if(view == null){
            return null;
        }
        int count = 0;
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            try {
                context = ((ContextWrapper) context).getBaseContext();
            } catch (Exception e) {
                break;
            }
            count ++;
            if(count > 20){
                break;
            }
        }
        return null;
    }
    public static long getMillisByDateStr(String dateStr,String pattern){
        if(AdStringUtils.isEmpty(dateStr)){
            throw new IllegalArgumentException("dateStr is null");
        }
        long mill = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.CHINA);
            mill = Objects.requireNonNull(sdf.parse(dateStr)).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mill;
    }
}
