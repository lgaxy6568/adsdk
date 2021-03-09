package cn.yq.ad.impl;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import cn.yq.ad.ADCallback;
import cn.yq.ad.ADRunnable;
import cn.yq.ad.Adv_Type;
import cn.yq.ad.AdConf;
import cn.yq.ad.AdNativeResponse;
import cn.yq.ad.ShowModel;

/**
 * Created by liguo on 2018/10/17.
 * desc
 */
public abstract class ADBaseImpl implements ADRunnable{
    public static final long interval_time = 5000;
    private final AtomicLong last_load_time = new AtomicLong(0);
    public final long getLastLoadTime(){
        return last_load_time.get();
    }
    public final void setLastLoadTime(){
        last_load_time.set(System.currentTimeMillis());
    }
    public final boolean canLoad(){
        return System.currentTimeMillis() - getLastLoadTime() >= interval_time;
    }
    protected final ADCallBackImpl defaultCallback = new ADCallBackImpl() ;
    @Override
    public void addCallback(ADCallback callback) {
        if(callback == null){
            return;
        }
        this.defaultCallback.addCallBack(callback);
    }

    @Override
    public List<ADCallback> getCallBackList() {
        return defaultCallback.getCbList();
    }

    @Override
    public void removeCallBack(ADCallback callback) {
        defaultCallback.removeCallBack(callback);
    }

    @Override
    public void removeAll() {
        defaultCallback.removeAll();
    }

    private final Bundle bd = new Bundle();
    @Override
    public void setExtra(Bundle bd) {
        if(bd == null){
            return;
        }
        this.bd.putAll(bd);
    }

    @Override
    public void showTj(ShowModel sm) {

    }

    @Override
    public Bundle getExtra() {
        return this.bd;
    }

    @Override
    public void reload() {

    }

    @Override
    public void show(View view, Object obj) {

    }

    //客户端回调广告SDK的方法
    @Override
    public void click(View view, Object extra) {

    }

    @Override
    public AdNativeResponse getAdvertEntity(String from, Map<String, String> map) {
        return null;
    }

    @Override
    public View getAdvertEntityView(View view, Object obj) {
        return null;
    }

    @Override
    public AdConf getCfg() {
        return null;
    }

    public void setLayoutParamsByPX(View iv, int widthPX, int heightPX) {
        ViewGroup.LayoutParams lp = iv.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(widthPX, heightPX);
        } else {
            lp.width = widthPX;
            lp.height = heightPX;
        }

        iv.setLayoutParams(lp);
    }

   public final void updateBtnText(View vv, AdNativeResponse tmp_nar){
        if(vv == null || tmp_nar == null){
            return;
        }
    }

    @Override
    public void destroy() {
        Log.e(getClass().getSimpleName(),"destroy()");
        removeAll();
    }

    public static Context getContextFromActivity(Activity act){
        Context ctx = act.getApplicationContext();
        return ctx;
    }
    public static Context getContextFromView(View view){
        Context ctx = view.getContext().getApplicationContext();
        return ctx;
    }

    public static final String SPLIT_TAG = "#";
    /** 把列表中的内容拼成一个字符串 */
    public static String convertToStr(List<String> lst) {
        if (lst == null || lst.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        try {
            Map<String, String> tmpMap = new HashMap<>();
            int len = lst.size();
            for (int i = 0; i < len; i++) {
                String tmpID = lst.get(i);
                if(tmpID == null || tmpID.trim().length() == 0 || tmpMap.containsKey(tmpID)){
                    continue;
                }
                if(tmpMap.size() > 0){
                    sb.append(SPLIT_TAG);
                }
                sb.append(tmpID);
                tmpMap.put(tmpID,""+i);
            }
        } catch (Exception e) {
        }
        return sb.toString();
    }
    public static String getNextId(List<String> adIdLst, String cur_load_id){
        if(adIdLst == null || adIdLst.size() == 0 || cur_load_id == null || cur_load_id.trim().length() == 0){
            return null;
        }
        final int id_lst_size = adIdLst.size();
        int found_index = -1;
        for (int i = 0; i< id_lst_size; i++) {
            String s = adIdLst.get(i);
            if(s == null || s.trim().length() == 0){
                continue;
            }
            s = s.trim();
            if(s.equalsIgnoreCase(cur_load_id)){
                found_index = i;
                break;
            }
        }
        if(found_index == -1){
            return null;
        }
        if(found_index == id_lst_size-1){
            return null;
        }
        return adIdLst.get(found_index + 1);
    }

    public static List<String> convertToLst(String str) {
        if (str == null || str.trim().length() == 0) {
            return null;
        }
        List<String> tmpLst = new ArrayList<>();
        try {
            String[] arr = str.split(SPLIT_TAG);
            for (String s : arr) {
                if(s == null){
                    continue;
                }
                String tt = s.trim();
                if(tt.length() == 0){
                    continue;
                }
                tmpLst.add(tt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tmpLst;
    }

    public boolean needRetry(Adv_Type at,int code, String err_msg){
        //-13 为自定义的超时code
//        if(at == Adv_Type.tt){
//            return (code == 20001 || code == 40006 || code == 40019 || code == -13);
//        }else if(at == Adv_Type.gdt){
//            return (err_msg.contains("102006") || err_msg.contains("100007") || (err_msg.contains("100133") || (err_msg.contains("100135")) || code == 5010 || code == 5004) );
//        }else{
//            return false;
//        }
        return true;

    }

    @Override
    public void resume(Object obj) {

    }

    @Override
    public void pause(Object obj) {

    }

    @Override
    public int getDataSize() {
        return 0;
    }

    @Override
    public final int getRequestCount() {
        return 1;
    }

    public final String getMaxStr(String s1, String s2){
        int lengthA = 0,lengthB=0;
        if(s1 != null){
            lengthA = s1.length();
        }
        if(s2 != null){
            lengthB = s2.length();
        }
        if(lengthA >= lengthB){
            return s1;
        }else{
            return s2;
        }
    }

    public final String getMinStr(String s1, String s2){
        int lengthA = 0,lengthB=0;
        if(s1 != null){
            lengthA = s1.length();
        }
        if(s2 != null){
            lengthB = s2.length();
        }
        if(lengthA < lengthB){
            return s1;
        }else{
            return s2;
        }
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
                if(value == null || value.trim().length() == 0)
                    continue;
                if(hasQuery){
                    sb.append("&");
                }else{
                    sb.append("?");
                    hasQuery=true;
                }
                try {
                    value = Uri.encode(value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sb.append(key).append("=").append(value);
            }
        }
        return sb.toString();
    }

    public final boolean isEmpty(String s){
        return s == null || s.trim().length() == 0;
    }
    public final boolean isNotEmpty(String s){
        return s != null && s.trim().length() > 0;
    }

    public final String replaceTrim_R_N(String str){
        if(str == null)
            return "";
        return str.replaceAll("\\s","");
    }

    public final int getRequestTimeOutFromExtra(){
        int tmpTimeout = 0;
        try {
            Bundle bd = getExtra();
            if(bd != null){
                tmpTimeout = bd.getInt(ExtraKey.KP_AD_REQUEST_TIME_OUT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(tmpTimeout <= 0){
            return 5000;
        }
        if(tmpTimeout < 3000){
            return 3000;
        }
        if(tmpTimeout > 6000){
            return 6000;
        }
        return tmpTimeout;
    }
}
