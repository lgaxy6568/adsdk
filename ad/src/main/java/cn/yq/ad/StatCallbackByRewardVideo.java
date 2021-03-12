package cn.yq.ad;

import cn.yq.ad.impl.PresentModel;

public interface StatCallbackByRewardVideo extends StatCallbackByKaiPing {

    void callBackByOnPreLoad();

    void callBackOnVideoStartPlay(PresentModel model);

    void callBackOnVideoPlayComplete(PresentModel model);

    void callBackOnRewardVerify(boolean rewardVerify, PresentModel model);
}
