package cn.yq.ad.impl;

import androidx.annotation.NonNull;

/**
 * Created by liguo on 2019/1/26.
 * desc
 */
public interface AbstractADCallback {
    /** 广告加载成功 */
    void onAdPresent(@NonNull PresentModel result);

    /** 广告加载失败 */
    void onAdFailed(@NonNull FailModel result);

    /** 广告点击 */
    void onAdClick(@NonNull ClickModel result);

    /** 广告关闭 */
    void onAdDismissed(DismissModel dm);

    /** 广告曝光成功 */
    void onADExposed(PresentModel pm);

    /** 用户点击不喜欢按钮 */
    void onDisLike(PresentModel pm);

    /** 跳过 */
    void onAdSkip(PresentModel pm);
}
