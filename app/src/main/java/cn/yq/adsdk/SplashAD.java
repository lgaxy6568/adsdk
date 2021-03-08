package cn.yq.adsdk;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.yq.ad.impl.AbstractADCallback;
import cn.yq.ad.impl.ClickModel;
import cn.yq.ad.impl.DismissModel;
import cn.yq.ad.impl.FailModel;
import cn.yq.ad.impl.PresentModel;
import cn.yq.ad.proxy.AdvProxyByKaiPin2;
import cn.yq.ad.proxy.model.GetAdsResponseListApiResult;

public class SplashAD extends AdvProxyByKaiPin2 {
    public SplashAD(Activity act, AbstractADCallback cb, ViewGroup adContainer, TextView tvSkip, GetAdsResponseListApiResult result) {
        super(act, cb, adContainer, tvSkip, result);
    }

    @Override
    public void callBackByOnAdPresent(PresentModel pm) {
        super.callBackByOnAdPresent(pm);
    }

    @Override
    public void callBackByOnAdFailed(FailModel fm) {
        super.callBackByOnAdFailed(fm);
    }

    @Override
    public void callBackByOnADExposed(PresentModel pm) {
        super.callBackByOnADExposed(pm);
    }

    @Override
    public void callBackByOnAdClick(ClickModel cm) {
        super.callBackByOnAdClick(cm);
    }

    @Override
    public void callBackByOnAdDismissed(DismissModel dm) {
        super.callBackByOnAdDismissed(dm);
    }

    @Override
    public void callBackByOnAdSkip(PresentModel pm) {
        super.callBackByOnAdSkip(pm);
    }

    @Override
    public void callBackByOnDisLike(PresentModel pm) {
        super.callBackByOnDisLike(pm);
    }
}
