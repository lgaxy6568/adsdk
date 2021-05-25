package cn.yq.demo.core;

import android.app.Application;

import cn.yq.ad.ADUtils;
import cn.yq.ad.proxy.AdConfigs;
import cn.yq.ad.proxy.model.AdConstants;

public class SuperApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //初始化广告SDK
        AdConfigs.setAppIdByCSJ("5133118");
        AdConfigs.setAppIdByGDT("1111386088");
        AdConfigs.setAppIdByMS("101343");   // TODO: 2021/5/25 WAIT_LIGUO 替换成正式的APPID ，测试=101343
        AdConfigs.setDebugModel(true);
        //单独调试某个SDK，0：调试所有，1：只调试广点通，2：只调试穿山甲
        AdConstants.setDebugAdPlatform(5);  // 1:gdt 2:csj
        ADUtils.init(this);
    }
}
