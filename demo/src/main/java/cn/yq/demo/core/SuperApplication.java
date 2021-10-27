package cn.yq.demo.core;

import android.app.Application;

import com.blankj.utilcode.util.CrashUtils;

import cn.yq.ad.ADUtils;
import cn.yq.ad.proxy.AdConfigs;
import cn.yq.ad.proxy.model.AdConstants;

public class SuperApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        CrashUtils.init();
        //初始化广告SDK
        AdConfigs.setAppIdByCSJ("5228676"); //5133118
        AdConfigs.setAppIdByGDT("1111386088");
        AdConfigs.setAppIdByMS("104395");   //测试ID=101343
        AdConfigs.setAppIdByBXM("020c269c50a64688ae6c204dd83f572f");
        AdConfigs.setDebugModel(true);
        //单独调试某个SDK，0：所有 | 1：广点通 | 2：穿山甲 | 3：百度 | 4：API | 5：美数 | 6：人工配置
        AdConstants.setDebugAdPlatform(0);
        ADUtils.init(this);

    }
}
