package cn.yq.ad;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.yq.ad.bxm.ADFactoryImplByBXM;
import cn.yq.ad.gdt.ADFactoryImplByGDT;
import cn.yq.ad.ms.ADFactoryImplByMS;
import cn.yq.ad.tt.ADFactoryImplByTT;
import cn.yq.ad.util.AdStringUtils;

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

    /** 美数 */
    private static volatile ADFactory mFactory_ms = null;
    public static ADFactory getFactoryByMS(){
        if(mFactory_ms != null){
            return mFactory_ms;
        }
        if(mFactory_ms == null){
            synchronized (ADUtils.class){
                if(mFactory_ms == null){
                    try {
                        mFactory_ms = new ADFactoryImplByMS();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return mFactory_ms;
    }

    /** 变现猫 */
    private static volatile ADFactory mFactory_bxm = null;
    private static final AtomicBoolean inited_bxm = new AtomicBoolean(false);
    public static ADFactory getFactoryByBXM(){
        if(inited_bxm.get()){
            return mFactory_bxm;
        }
        if(mFactory_bxm == null){
            synchronized (ADUtils.class){
                if(mFactory_bxm == null){
                    try {
                        mFactory_bxm = new ADFactoryImplByBXM();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        inited_bxm.set(true);
                    }
                }
            }
        }
        return mFactory_bxm;
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
        try {
            ADFactory af = ADUtils.getFactoryByMS();
            if(af != null){
                af.init(mCtx);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ADFactory af = ADUtils.getFactoryByBXM();
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
        if(ar != null && cb != null) {
            ar.addCallback(cb);
        }
        return ar;
    }

    /* 广点通~激励视频广告 */
    public static ADRunnable getGDTRewardVideo(Activity act, String appId, String adId, Map<String, Object> extra, VideoADCallback cb) {
        ADFactory factory = ADUtils.getFactoryByGDT();
        if(factory == null){
            return null;
        }
        ADRunnable ar = factory.createGDTRewardVideo(act, appId, adId, extra);
        if(ar != null && cb != null) {
            ar.addCallback(cb);
        }
        return ar;
    }

    /* 变现猫~激励视频广告 */
    public static ADRunnable getBXMRewardVideo(Activity act, String appId, String adId, Map<String, Object> extra, VideoADCallback cb) {
        ADFactory factory = ADUtils.getFactoryByBXM();
        if(factory == null){
            return null;
        }
        ADRunnable ar = factory.createBXMRewardVideo(act, appId, adId, extra);
        if(ar != null && cb != null) {
            ar.addCallback(cb);
        }
        return ar;
    }

    /* 变现猫~浮标广告 */
    public static ADRunnable getFloatAdByBXM(Activity act, ViewGroup adContainer,String appId, String adId, ADCallback cb) {
        ADFactory factory = ADUtils.getFactoryByBXM();
        if(factory == null){
            return null;
        }
        ADRunnable ar = factory.createFloatAdForBXM(act, appId, adId, adContainer);
        if(ar != null && cb != null) {
            ar.addCallback(cb);
        }
        return ar;
    }

    public static boolean isAppInstalled(final String pkgName,Context ctx) {
        if (AdStringUtils.isEmpty(pkgName)) {
            return false;
        }
        if(ctx == null){
            return false;
        }

        try {
            PackageManager pm = ctx.getPackageManager();
            if(pm == null){
                return false;
            }
            return pm.getApplicationInfo(pkgName, 0).enabled;
        } catch (Exception e) {
            return false;
        }
    }
}