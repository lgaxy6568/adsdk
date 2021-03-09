package cn.yq.ad.tt;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import cn.yq.ad.ADRunnable;
import cn.yq.ad.impl.BaseADFactoryImpl;
import cn.yq.ad.tt.config.TTUtil;

/**
 * Created by liguo on 2018/10/17.
 * desc
 */
public class ADFactoryImplByTT extends BaseADFactoryImpl {
    @Override
    public void init(Context ctx, String... args) {
        TTUtil.get().init(ctx);
    }

    @Override
    public ADRunnable createSplashForTT(Activity act, String appId, String adId, ViewGroup adContainer) {
        return new SplashForTT(act, appId, adId, adContainer);
    }
}
