package cn.yq.ad;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Map;

import cn.yq.ad.gdt.ADFactoryImplByGDT;
import cn.yq.ad.tt.ADFactoryImplByTT;

/**
 * Created by liguo on 2018/10/17.
 * desc
 */
public class ADUtils {
    /** 广点通 */
    private static volatile ADFactory mFactory_gdt = null;
    private static volatile boolean inited_gdt = false;
    public static ADFactory getFactoryByGDT(){
        if(inited_gdt){
            return mFactory_gdt;
        }
        if(mFactory_gdt == null){
            synchronized (ADUtils.class){
                if(mFactory_gdt == null){
                    try {
                        mFactory_gdt = new ADFactoryImplByGDT();
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
            synchronized (ADUtils.class){
                if(mFactory_tt == null){
                    try {
                        mFactory_tt = new ADFactoryImplByTT();
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
        ADFactory factory = ADUtils.getFactoryByTT();
        if(factory == null){
            return null;
        }
        ADRunnable ar = factory.createSplashForTT(act,appId,adId,adContainer);
        ar.addCallback(callback);
        return ar;
    }
    public static ADRunnable getSplashADForGDT(Activity act, String appId, String adId, ViewGroup adContainer, ADCallback callback, TextView tvSkip){
        ADFactory factory = ADUtils.getFactoryByGDT();
        if(factory == null){
            return null;
        }
        ADRunnable ar = factory.createSplashForGDT(act,adContainer,tvSkip,appId,adId);
        ar.addCallback(callback);
        return ar;
    }

    public static void init(Context ctx){
        Context mCtx = ctx.getApplicationContext();
        try {
            ADFactory af = ADUtils.getFactoryByGDT();
            if(af != null){
                af.init(mCtx);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ADFactory af = ADUtils.getFactoryByTT();
            if(af != null){
                af.init(mCtx);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* 穿山甲~激励视频广告 */
    public static ADRunnable getTTRewardVideo(Activity act, String appId, String adId, Map<String, Object> extra, VideoADCallback cb) {
        ADFactory factory = ADUtils.getFactoryByTT();
        if(factory == null){
            return null;
        }
        ADRunnable ar = factory.createTTRewardVideo(act, appId, adId, extra);
        ar.addCallback(cb);
        return ar;
    }

    /* 广点通~激励视频广告 */
    public static ADRunnable getGDTRewardVideo(Activity act, String appId, String adId, Map<String, Object> extra, VideoADCallback cb) {
        ADFactory factory = ADUtils.getFactoryByGDT();
        if(factory == null){
            return null;
        }
        ADRunnable ar = factory.createGDTRewardVideo(act, appId, adId, extra);
        if(ar != null) {
            ar.addCallback(cb);
        }
        return ar;
    }
}