package cn.yq.ad.ms;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import com.meishu.sdk.core.AdSdk;
import com.meishu.sdk.core.MSAdConfig;

import cn.yq.ad.ADRunnable;
import cn.yq.ad.Adv_Type;
import cn.yq.ad.impl.BaseADFactoryImpl;
import cn.yq.ad.proxy.AdConfigs;

/**
 * Created by liguo on 2019/1/22.
 * desc
 */
public class ADFactoryImplByMS extends BaseADFactoryImpl {
    @Override
    public void init(Context ctx, String... args) {
        MSAdConfig sdkConfig = new MSAdConfig.Builder()
                .appId(AdConfigs.getAppIdByType(Adv_Type.ms))
                .isTest(false)       //测试环境
                .enableDebug(false)  //开启DEBUG模式，打印内部LOG
                .downloadConfirm(MSAdConfig.DOWNLOAD_CONFIRM_AUTO)  //下载提示模式
                .build();

        AdSdk.init(ctx, sdkConfig);

    }

    @Override
    public ADRunnable createSplashForMS(Activity act, String appId, String adId, ViewGroup adContainer) {
        return new SplashForMS(act,adContainer,appId,adId);
    }
}
