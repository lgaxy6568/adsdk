package cn.yq.ad;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by liguo on 2018/10/17.
 * desc
 */
public class ADUtil {
    /** 广点通 */
    private static volatile ADFactory mFactory_gdt = null;
    private static volatile boolean inited_gdt = false;
    public static ADFactory getFactoryByGDT(){
        if(inited_gdt){
            return mFactory_gdt;
        }
        if(mFactory_gdt == null){
            synchronized (ADUtil.class){
                if(mFactory_gdt == null){
                    try {
                        Class<?> cls_var = Class.forName("cn.yq.ad.gdt.ADFactoryImplByGDT");
                        mFactory_gdt = (ADFactory) cls_var.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        inited_gdt = true;
                    }
                }
            }
        }
        return mFactory_gdt;
    }

    /** 穿山甲 */
    private static volatile ADFactory mFactory_tt = null;
    private static volatile boolean inited_tt = false;
    public static ADFactory getFactoryByTT(){
        if(inited_tt){
            return mFactory_tt;
        }
        if(mFactory_tt == null){
            synchronized (ADUtil.class){
                if(mFactory_tt == null){
                    try {
                        Class<?> cls_var = Class.forName("cn.yq.ad.tt.ADFactoryImplByTT");
                        mFactory_tt = (ADFactory) cls_var.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        inited_tt = true;
                    }
                }
            }
        }
        return mFactory_tt;
    }

    public static boolean textEmpty(String text) {
        return text == null || text.trim().length() == 0;
    }

    public static ADRunnable getSplashADForTT(Activity act, String appId, String adId, ViewGroup adContainer, ADCallback callback){
        ADFactory factory = ADUtil.getFactoryByTT();
        if(factory == null){
            return null;
        }
        ADRunnable ar = factory.createSplashForTT(act,appId,adId,adContainer);
        ar.addCallback(callback);
        return ar;
    }
    public static ADRunnable getSplashADForGDT(Activity act, String appId, String adId, ViewGroup adContainer, ADCallback callback, TextView tvSkip){
        ADFactory factory = ADUtil.getFactoryByGDT();
        if(factory == null){
            return null;
        }
        ADRunnable ar = factory.createSplashForGDT(act,adContainer,tvSkip,appId,adId);
        ar.addCallback(callback);
        return ar;
    }
}