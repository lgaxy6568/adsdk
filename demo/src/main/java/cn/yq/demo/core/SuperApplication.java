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
        AdConfigs.setAppIdByMS("104395");   //测试ID=101343
        AdConfigs.setDebugModel(true);
        //单独调试某个SDK，0：所有 | 1：广点通 | 2：穿山甲 | 3：百度 | 4：API | 5：美数 | 6：人工配置
        AdConstants.setDebugAdPlatform(6);
        ADUtils.init(this);
    }
}
