package cn.yq.ad.proxy.model;

import java.util.concurrent.atomic.AtomicInteger;

import cn.yq.ad.proxy.AdConfigs;

public class AdConstants {
    /**
     * 0：none | 1：广点通 | 2：穿山甲 | 3：百度 | 4：API | 5：美数
     */
    private static final AtomicInteger ab_test_ad_platform = new AtomicInteger(0);
    public static final String LOCATION_BY_KAI_PING = "KP";
    public static final String LOCATION_BY_JLSP = "JLSP";

    public static final String PARTNER_KEY_BY_TT = "CSJ";
    public static final String PARTNER_KEY_BY_GDT = "GDT";
    public static final String PARTNER_KEY_BY_MS = "MS";

    public static final String SDK_TYPE_BY_SELF = "Config";
    public static final String SDK_TYPE_BY_SDK = "SDK";
    public static final String SDK_TYPE_BY_API = "API";

    public static void setDebugAdPlatform(int platform){
        ab_test_ad_platform.set(platform);
    }
    public static boolean is_test_api_adv() {
        if (AdConfigs.isDebugModel()) {
            return ab_test_ad_platform.get() == 4;
        }
        return false;
    }

    public static boolean is_test_gdt_adv() {
        if (AdConfigs.isDebugModel()) {
            return ab_test_ad_platform.get() == 1;
        }
        return false;
    }

    public static boolean is_test_tt_adv() {
        if (AdConfigs.isDebugModel()) {
            return ab_test_ad_platform.get() == 2;
        }
        return false;
    }

    public static boolean is_test_baidu_adv() {
        if (AdConfigs.isDebugModel()) {
            return ab_test_ad_platform.get() == 3;
        }
        return false;
    }

    public static boolean is_test_ms_adv() {
        if (AdConfigs.isDebugModel()) {
            return ab_test_ad_platform.get() == 5;
        }
        return false;
    }
}
