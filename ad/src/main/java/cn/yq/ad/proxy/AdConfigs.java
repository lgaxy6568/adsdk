package cn.yq.ad.proxy;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import cn.yq.ad.Adv_Type;

public class AdConfigs {

    private static final AtomicReference<String> APP_ID_BY_CSJ = new AtomicReference<>("5133118");
    private static final AtomicReference<String> APP_ID_BY_GDT = new AtomicReference<>("1111386088");
    private static final AtomicReference<String> APP_ID_BY_MS = new AtomicReference<>("104395");
    private static final AtomicReference<String> APP_ID_BY_BXM = new AtomicReference<>("020c269c50a64688ae6c204dd83f572f");
    public static String getAppIdByType(Adv_Type at){
        if(at == Adv_Type.gdt){
            return APP_ID_BY_GDT.get();
        }
        if(at == Adv_Type.tt){
            return APP_ID_BY_CSJ.get();
        }
        if(at == Adv_Type.ms){
            return APP_ID_BY_MS.get();
        }
        if(at == Adv_Type.bxm){
            return APP_ID_BY_BXM.get();
        }
        return "unknown_"+at.name();
    }
    public static void setAppIdByCSJ(String appId){
        APP_ID_BY_CSJ.set(appId);
    }
    public static void setAppIdByGDT(String appId){
        APP_ID_BY_GDT.set(appId);
    }
    public static void setAppIdByMS(String appId){
        APP_ID_BY_MS.set(appId);
    }
    public static void setAppIdByBXM(String appId){
        APP_ID_BY_BXM.set(appId);
    }

    /** 是否为Debug模式 */
    private static final AtomicBoolean ab_is_debug = new AtomicBoolean(false);

    public static void setDebugModel(boolean isDebug) {
        ab_is_debug.set(isDebug);
    }

    public static boolean isDebugModel() {
        return ab_is_debug.get();
    }

    /** 广告加载总超时时间 */
    private static int requestTimeOutByTotal = 5;

    public static int getRequestTimeOutByTotal() {
        return requestTimeOutByTotal;
    }

    public static void setRequestTimeOutByTotal(int timeOut) {
        AdConfigs.requestTimeOutByTotal = timeOut;
    }

    /** 超时时间~广点通 */
    private static int adLoadTimeOutByGDT = 5;

    public static int getAdLoadTimeOutByGDT() {
        return adLoadTimeOutByGDT;
    }

    public static void setAdLoadTimeOutByGDT(int adLoadTimeOutByGDT) {
        AdConfigs.adLoadTimeOutByGDT = adLoadTimeOutByGDT;
    }

    /** 超时时间~穿山甲 */
    private static int adLoadTimeOutByCSJ = 5;

    public static int getAdLoadTimeOutByCSJ() {
        return adLoadTimeOutByCSJ;
    }

    public static void setAdLoadTimeOutByCSJ(int adLoadTimeOutByCSJ) {
        AdConfigs.adLoadTimeOutByCSJ = adLoadTimeOutByCSJ;
    }

    /**【开屏广告~SDK类型广告的排序方式】（0：优先级排序 ，1：权重排序）*/
    private static int adSdkSortType = 0;

    public static int getAdSdkSortType() {
        return adSdkSortType;
    }

    public static void setAdSdkSortType(int adSdkSortType) {
        AdConfigs.adSdkSortType = adSdkSortType;
    }
}
