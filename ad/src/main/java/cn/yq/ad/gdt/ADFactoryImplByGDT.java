package cn.yq.ad.gdt;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qq.e.comm.managers.GDTADManager;

import java.util.Map;

import cn.yq.ad.ADRunnable;
import cn.yq.ad.Adv_Type;
import cn.yq.ad.impl.BaseADFactoryImpl;
import cn.yq.ad.proxy.AdConfigs;

/**
 * Created by liguo on 2019/1/22.
 * desc
 */
public class ADFactoryImplByGDT extends BaseADFactoryImpl {
    @Override
    public void init(Context ctx, String... args) {
        GDTADManager.getInstance().initWith(ctx, AdConfigs.getAppIdByType(Adv_Type.gdt));
    }

    @Override
    public ADRunnable createSplashForGDT(Activity act, ViewGroup gdtContainer, TextView tvSkip, String appId, String posId){
        return new SplashForGDT(act,gdtContainer,tvSkip,appId,posId);
    }

    @Override
    public ADRunnable createGDTRewardVideo(Activity act, String appId, String adId, Map<String, Object> extra, String advPos) {
        return new RewardVideoForGDT(act,appId,adId,extra,advPos);
    }
}
