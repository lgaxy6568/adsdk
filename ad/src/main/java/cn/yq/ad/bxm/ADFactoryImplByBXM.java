package cn.yq.ad.bxm;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import com.dhcw.sdk.manager.BDAdvanceConfig;
import com.dhcw.sdk.manager.BDManager;

import java.util.Map;

import cn.yq.ad.ADRunnable;
import cn.yq.ad.Adv_Type;
import cn.yq.ad.impl.BaseADFactoryImpl;
import cn.yq.ad.proxy.AdConfigs;

/**
 * Created by liguo on 2021/08/19.
 * desc
 */
public class ADFactoryImplByBXM extends BaseADFactoryImpl {
    @Override
    public void init(Context ctx, String... args) {
        BDAdvanceConfig.getInstance()
                .setAppName("倒数321")
                .setDebug(AdConfigs.isDebugModel());
        String appId = AdConfigs.getAppIdByType(Adv_Type.bxm);
        BDManager.getStance().init(ctx, appId);
    }

    @Override
    public ADRunnable createBXMRewardVideo(Activity act, String appId, String adId, Map<String, Object> extra) {
        return new RewardVideoForBxm(act, appId, adId, extra);
    }

    @Override
    public ADRunnable createFloatAdForBXM(Activity act, String appId, String adId, ViewGroup adContainer) {
        return new FloatAdForBXM(act, adContainer,appId,adId);
    }
}
