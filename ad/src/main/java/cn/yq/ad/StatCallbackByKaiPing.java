package cn.yq.ad;

import cn.yq.ad.impl.ClickModel;
import cn.yq.ad.impl.DismissModel;
import cn.yq.ad.impl.FailModel;
import cn.yq.ad.impl.PresentModel;
import cn.yq.ad.proxy.model.AdRespItem;

public interface StatCallbackByKaiPing {
    void callBackByOnAdStartLoad(String adId, Adv_Type adType, AdRespItem item);

    void callBackByOnAdPresent(PresentModel pm);

    void callBackByOnAdFailed(FailModel fm);

    void callBackByOnADExposed(PresentModel pm);

    void callBackByOnAdClick(ClickModel cm);

    void callBackByOnAdDismissed(DismissModel dm);

    void callBackByOnAdSkip(PresentModel pm);

    void callBackByOnDisLike(PresentModel pm);

}
