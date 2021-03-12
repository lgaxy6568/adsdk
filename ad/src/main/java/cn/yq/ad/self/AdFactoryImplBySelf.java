package cn.yq.ad.self;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import cn.yq.ad.ADRunnable;
import cn.yq.ad.impl.BaseADFactoryImpl;

public class AdFactoryImplBySelf extends BaseADFactoryImpl {
    @Override
    public void init(Context ctx, String... args) {

    }

    @Override
    public ADRunnable createSplashForSelf(Activity act, String appId, String adId, ViewGroup adContainer) {
        return new SplashForSelf(act,adContainer,appId,adId);
    }
}
