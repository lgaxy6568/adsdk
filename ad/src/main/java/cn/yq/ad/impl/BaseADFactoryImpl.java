package cn.yq.ad.impl;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Map;

import cn.yq.ad.ADFactory;
import cn.yq.ad.ADRunnable;

/**
 * Created by liguo on 2019/1/22.
 * desc
 */
public abstract class BaseADFactoryImpl implements ADFactory {

    @Override
    public ADRunnable createSplashForGDT(Activity act, ViewGroup gdtContainer, TextView tvSkip, String appId, String posId) {
        return null;
    }

    @Override
    public ADRunnable createSplashForTT(Activity act, String appId, String adId, ViewGroup adContainer) {
        return null;
    }

    @Override
    public ADRunnable createSplashForSelf(Activity act, String appId, String adId, ViewGroup adContainer) {
        return null;
    }

    @Override
    public ADRunnable createTTRewardVideo(Activity act, String appId, String adId, Map<String, Object> extra) {
        return null;
    }

    @Override
    public ADRunnable createGDTRewardVideo(Activity act, String appId, String adId, Map<String, Object> extra) {
        return null;
    }
}
