package cn.yq.ad.ms;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.meishu.sdk.core.ad.splash.ISplashAd;
import com.meishu.sdk.core.ad.splash.SplashAdListener;
import com.meishu.sdk.core.ad.splash.SplashAdLoader;
import com.meishu.sdk.core.loader.AdPlatformError;
import com.meishu.sdk.core.loader.InteractionListener;
import com.meishu.sdk.core.utils.LogUtil;

import cn.yq.ad.AdConf;
import cn.yq.ad.Adv_Type;
import cn.yq.ad.impl.ADBaseImpl;
import cn.yq.ad.impl.ClickModel;
import cn.yq.ad.impl.DismissModel;
import cn.yq.ad.impl.FailModel;
import cn.yq.ad.impl.PresentModel;

/**
 * SDK文档地址：https://git.adxdata.com/meishu/sdk-android-demo/-/wikis/home
 * 1003907 普通网页
 * 100424024 下载 开屏（640x960）
 * 100424025 Deeplink 唤起
 */
public class SplashForMS extends ADBaseImpl implements SplashAdListener,InteractionListener {
    private ViewGroup adContainer;
    private final String appId;
    private final String posId;
    private Activity act;
    private static final String TAG = SplashForMS.class.getSimpleName();

    public SplashForMS(Activity act, ViewGroup adContainer, String appId, String posId) {
        this.adContainer = adContainer;
        this.appId = replaceTrim_R_N(appId);
        this.posId = replaceTrim_R_N(posId);
        this.act = act;
    }

    @Override
    public AdConf getCfg() {
        AdConf bd = new AdConf();
        bd.setAppId(appId);
        bd.setAdId(posId);
        bd.setAdRespItem(getAdParamItem());
        return bd;
    }

    @Override
    public void load() {
        SplashAdLoader splashAdLoader = new SplashAdLoader(act, adContainer, posId, this, 3000);
        splashAdLoader.loadAdOnly();
        LogUtil.i(TAG,"load()");
    }

    @SuppressWarnings("ALL")
    @Override
    public void show(View view, Object obj) {
        super.show(view, obj);
        LogUtil.i(TAG,"show()");
        if(ad == null){
            return;
        }
        ad.setInteractionListener(this);
        ad.showAd(adContainer);
    }

    @Override
    public Adv_Type getAdvType() {
        return Adv_Type.ms;
    }

    //==========广告回调============

    @SuppressWarnings("all")
    private ISplashAd ad = null;

    @Override
    public void onAdClicked() {
        LogUtil.i(TAG,"onAdClicked()");
        defaultCallback.onAdClick(ClickModel.getInstance(0,-1,posId,getAdvType()).setAdRespItem(getAdParamItem()));
    }

    /**
     * 广告被展示时回调（非曝光回调），主要为了解决，广点通曝光太晚的问题
     * @param iSplashAd
     */
    @Override
    public void onAdPresent(ISplashAd iSplashAd) {
        LogUtil.i(TAG,"onAdPresent()");
    }

    /**
     * 开屏广告手动点击跳过时回调 不支持百度、广点通
     * @param iSplashAd
     */
    @Override
    public void onAdSkip(ISplashAd iSplashAd) {
        LogUtil.i(TAG,"onAdSkip()");
        defaultCallback.onAdSkip(PresentModel.getInstance(posId, getAdvType()).setAdRespItem(getAdParamItem()));
    }

    /**
     * 开屏广告倒计时结束时回调  不支持百度、广点通
     * @param iSplashAd
     */
    @Override
    public void onAdTimeOver(ISplashAd iSplashAd) {
        LogUtil.i(TAG,"onAdTimeOver()");
    }

    /**
     * 剩余时间回调
     * @param millis
     */
    @Override
    public void onAdTick(long millis) {
        LogUtil.i(TAG,"onAdTick()");
    }

    /**
     * 广告加载成功回调，其中ISplashAd为具体的广告处理对象，其中展示广告调用showAd()方法
     * @param iSplashAd
     */
    @Override
    public void onAdLoaded(ISplashAd iSplashAd) {
        LogUtil.i(TAG,"onAdLoaded()");
        this.ad = iSplashAd;
        defaultCallback.onAdPresent(PresentModel.getInstance(posId, getAdvType()).setAdRespItem(getAdParamItem()));
    }

    /**
     * 广告加载出错
     */
    @Override
    public void onAdError() {
        LogUtil.e(TAG,"onAdError(),posId="+posId);
        FailModel fm = FailModel.toStr(-1,"",posId,getAdvType());
        defaultCallback.onAdFailed(fm.setAdRespItem(getAdParamItem()));
    }

    /**
     * 广告曝光回调
     */
    @Override
    public void onAdExposure() {
        LogUtil.e(TAG,"onAdExposure()");
        defaultCallback.onADExposed(PresentModel.getInstance(posId, getAdvType()).setAdRespItem(getAdParamItem()));
    }

    @Override
    public void onAdClosed() {
        LogUtil.e(TAG,"onAdClosed()");
        defaultCallback.onAdDismissed(DismissModel.newInstance(posId, getAdvType()).setAdRespItem(getAdParamItem()));
    }

    /**
     * 第三方平台出错回调
     * @param err
     */
    @Override
    public void onAdPlatformError(AdPlatformError err) {
        LogUtil.e(TAG,"onAdPlatformError()");
        int errCode = -1;
        String errMsg = "第三方平台回调出错";
        if(err != null){
            Integer errCodeObj = err.getCode();
            if(errCodeObj != null){
                errCode = errCodeObj;
            }
            errMsg = err.getPlatform()+"_"+err.getMessage();
        }

        FailModel fm = FailModel.toStr(errCode,errMsg,posId,getAdvType());
        defaultCallback.onAdFailed(fm.setAdRespItem(getAdParamItem()));
    }
}
