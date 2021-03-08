package cn.yq.ad;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by liguo on 2018/10/17.
 * desc
 */
public interface ADFactory {
    void init(Context ctx, String... args);
    /* 开屏广告 */
    ADRunnable createSplashForGDT(Activity act, ViewGroup gdtContainer, TextView tvSkip, String appId, String posId);
    ADRunnable createSplashForTT(Activity act, String appId, String adId, ViewGroup adContainer);
    ADRunnable createSplashForSelf(Activity act, String appId, String adId, ViewGroup adContainer);
}
