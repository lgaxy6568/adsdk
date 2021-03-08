package cn.yq.ad.proxy;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import cn.yq.ad.ADRunnable;
import cn.yq.ad.impl.BaseADFactoryImpl;
import cn.yq.ad.proxy.splash.SplashForSelf;

public class AdFactoryImplBySelf extends BaseADFactoryImpl {
    @Override
    public void init(Context ctx, String... args) {

    }

    @Override
    public ADRunnable createSplashForSelf(Activity act, String appId, String adId, ViewGroup adContainer) {
        return new SplashForSelf(act,adContainer,appId,adId);
    }
}
