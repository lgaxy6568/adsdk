package cn.yq.demo.core;

import android.app.Application;

import cn.yq.ad.ADUtils;
import cn.yq.ad.proxy.AdConfigs;
import cn.yq.ad.proxy.model.AdConstants;

public class SuperApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //步骤2：初始化广告SDK
        AdConfigs.setAppIdByCSJ("5133118");
        AdConfigs.setAppIdByGDT("1111386088");
        AdConfigs.setDebugModel(true);
        AdConstants.setDebugAdPlatform(0);  // 1:gdt 2:csj
        ADUtils.init(this);
    }
}
