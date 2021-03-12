package cn.yq.ad;

import androidx.annotation.NonNull;

import cn.yq.ad.impl.PresentModel;

public interface VideoADCallback extends ADCallback {
    //加载前准备
    void onPreLoad();

    //开始播放
    void onVideoStartPlay(@NonNull PresentModel model);

    //播放完成
    void onVideoPlayComplete(@NonNull PresentModel model);

    //是否可以领取奖励
    void onRewardVerify(boolean rewardVerify, PresentModel model);
}
