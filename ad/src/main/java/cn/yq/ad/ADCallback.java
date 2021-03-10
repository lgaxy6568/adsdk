package cn.yq.ad;

import androidx.annotation.NonNull;

import cn.yq.ad.impl.ClickModel;
import cn.yq.ad.impl.DismissModel;
import cn.yq.ad.impl.FailModel;
import cn.yq.ad.impl.PresentModel;

/**
 * Created by liguo on 2018/10/16.
 * desc
 */
public interface ADCallback {

    /** 广告加载成功 */
    void onAdPresent(@NonNull PresentModel result);

    /** 广告加载失败 */
    void onAdFailed(@NonNull FailModel result);

    /** 广告点击 */
    void onAdClick(@NonNull ClickModel result);

    /** 广告关闭 */
    void onAdDismissed(@NonNull DismissModel dm);

    /** 广告曝光成功 */
    void onADExposed(@NonNull PresentModel pm);

    /** 用户点击不喜欢按钮 */
    void onDisLike(@NonNull PresentModel pm);

    /** 跳过 */
    void onAdSkip(@NonNull PresentModel pm);

}
