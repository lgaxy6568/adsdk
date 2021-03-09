package cn.yq.ad.tt;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTSplashAd;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import cn.yq.ad.Adv_Type;
import cn.yq.ad.AdConf;
import cn.yq.ad.impl.ADBaseImpl;
import cn.yq.ad.impl.ClickModel;
import cn.yq.ad.impl.DismissModel;
import cn.yq.ad.impl.ExtraKey;
import cn.yq.ad.impl.FailModel;
import cn.yq.ad.impl.PresentModel;
import cn.yq.ad.tt.config.TTUtil;
import cn.yq.ad.tt.utils.TToast;
import cn.yq.ad.util.ADHelper;
import cn.yq.ad.util.AdSize;
import cn.yq.ad.util.SizeUtil;

/**
 * 穿山甲全屏~开屏
 * Created by liguo on 2018/10/16.
 * desc
 */
public class SplashForTT extends ADBaseImpl {
    private final String TAG = SplashForTT.class.getSimpleName();
    private final String adId;
    protected final String ids;
    private Context act;
    private final String appId;
    private ViewGroup mSplashContainer;
    private TTSplashAd respAd;
    SplashForTT(Activity act, String appId, String tmpAdId, ViewGroup ad_container) {
        this.act = getContextFromActivity(act);
        this.appId = appId;
        this.ids = tmpAdId;
        if(tmpAdId == null || tmpAdId.trim().length() == 0){
            throw new IllegalArgumentException("SplashForTT(),adId is empty");
        }
        this.adId = tmpAdId;
        Log.e(TAG, "SplashForTT(),this.adId="+this.adId+",appId="+appId);
        this.mSplashContainer = ad_container;
    }
    private final AtomicInteger call_back_count = new AtomicInteger(0);
    /**
     * 加载开屏广告
     */
    public void load() {
        //step3:创建开屏广告请求参数AdSlot,具体参数含义参考文档
        AdSize sz = SizeUtil.getScreenSize(act);
        final int accept_width = sz.getWidth();
        final int accept_height = sz.getHeight();
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(adId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(accept_width, accept_height)
                .build();
        TTAdManager tam = TTUtil.get().getAdManager();
//        tam.setAppId(appId);
//        tam.requestPermissionIfNecessary(act);
        TTAdNative mTTAdNative = tam.createAdNative(act);
        //step4:请求广告，调用开屏广告异步请求接口，对请求回调的广告作渲染处理
        call_back_count.set(1);
        final int AD_TIME_OUT = getRequestTimeOutFromExtra();
        Log.e(TAG, "load(),accept_width="+accept_width+",accept_height="+accept_height+",kp_size_type="+getAdvSizeType()+",超时时间="+AD_TIME_OUT);
        mTTAdNative.loadSplashAd(adSlot,new TempAdListener(), AD_TIME_OUT);
    }

    @Override
    public Adv_Type getAdvType() {
        return Adv_Type.tt;
    }
    private void showToast(String msg) {
        TToast.show(act, msg);
    }

    private String getAdvSizeType(){
        Bundle bd = getExtra();
        if(bd != null && bd.containsKey(ExtraKey.KP_AD_SIZE_TYPE_KEY)){
            return bd.getString(ExtraKey.KP_AD_SIZE_TYPE_KEY);
        }
        return ExtraKey.KP_AD_SIZE_TYPE_VALUE_QUAN_PING;
    }
    private class TempAdListener implements TTAdNative.SplashAdListener{
        private final String randomUUID;
        private final Map<String,String> mmp;
        public TempAdListener() {
            randomUUID = UUID.randomUUID().toString();
            mmp = new LinkedHashMap<>();
            mmp.put(randomUUID,randomUUID);
        }

        @Override
        public void onError(int code, String message) {
            FailModel fm= FailModel.toStr(code,"ERROR_"+message,adId, Adv_Type.tt).put(ExtraKey.KP_AD_SIZE_TYPE_KEY,getAdvSizeType());
            Log.e(TAG, "onError(),err_msg="+fm.toFullMsg());
            if(mmp.containsKey(randomUUID)) {
                mmp.remove(randomUUID);
                defaultCallback.onAdFailed(fm);
            }else{
                Log.e(TAG,"onError(),已经回调过了onTimeout()方法");
            }
        }

        @Override
        public void onTimeout() {
            FailModel fm = FailModel.toStr(-1,"TIMEOUT_广告加载超时",adId,Adv_Type.tt).put(ExtraKey.KP_AD_SIZE_TYPE_KEY,getAdvSizeType());
            Log.e(TAG, "onTimeout(),err_msg="+fm.toFullMsg());
            if(mmp.containsKey(randomUUID)) {
                mmp.remove(randomUUID);
                defaultCallback.onAdFailed(fm);
            }else{
                Log.e(TAG,"onTimeout(),已经回调过了onError()方法");
            }
        }

        @Override
        public void onSplashAdLoad(final TTSplashAd ad) {
            Log.e(TAG, "onSplashAdLoad(),开屏广告加载成功");
            if (ad == null) {
                Log.e(TAG, "onSplashAdLoad(),ad is null , will return");
                return;
            }
            respAd = ad;
            //检查Activity是否已销毁
            Activity act = ADHelper.getActivityFromView(mSplashContainer);
            if(act != null && (act.isFinishing() || act.isDestroyed())){
                FailModel fm = FailModel.toStr(-2,"加载成功_但Activity已销毁~",adId,Adv_Type.tt).put(ExtraKey.KP_AD_SIZE_TYPE_KEY,getAdvSizeType());
                Log.e(TAG, "onSplashAdLoad()"+fm.toFullMsg());
                defaultCallback.onAdFailed(fm);
                return;
            }
            //设置不开启开屏广告倒计时功能以及不显示跳过按钮,如果这么设置，您需要自定义倒计时逻辑
            //ad.setNotAllowSdkCountdown();
            defaultCallback.onAdPresent(PresentModel.getInstance(adId, Adv_Type.tt).setData(ad).put(ExtraKey.KP_AD_SIZE_TYPE_KEY,getAdvSizeType()));
            //设置SplashView的交互监听器
            ad.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
                @Override
                public void onAdClicked(View view, int type) {
                    Log.e(TAG, "onAdClicked,type="+type);
                    showToast("开屏广告点击");
                    int adType = type == 4 ? 1 : 2;
                    defaultCallback.onAdClick(ClickModel.getInstance(0,adType,adId,Adv_Type.tt).put(ExtraKey.KP_AD_SIZE_TYPE_KEY,getAdvSizeType()));
                }

                @Override
                public void onAdShow(View view, int type) {
                    Log.e(TAG, "onAdShow,type="+type);
                    showToast("开屏广告展示");
                    if(call_back_count.get() == 1) {
                        call_back_count.set(0);
                        defaultCallback.onADExposed(PresentModel.getInstance(adId, Adv_Type.tt).put(ExtraKey.KP_AD_SIZE_TYPE_KEY,getAdvSizeType()));
                    }
                }

                @Override
                public void onAdSkip() {
                    Log.e(TAG, "onAdSkip");
                    showToast("开屏广告跳过");
//                        goToMainActivity();
                    defaultCallback.onAdSkip(PresentModel.getInstance(adId, Adv_Type.tt).put(ExtraKey.KP_AD_SIZE_TYPE_KEY,getAdvSizeType()));
                }

                @Override
                public void onAdTimeOver() {
                    Log.e(TAG, "onAdTimeOver");
                    showToast("开屏广告倒计时结束");
//                        goToMainActivity();
                    defaultCallback.onAdDismissed(DismissModel.newInstance(adId,Adv_Type.tt,2));
                }
            });
        }
    }

    @Override
    public AdConf getCfg() {
        AdConf bd = new AdConf();
        bd.setAppId(appId);
        bd.setAdId(adId);
        return bd;
    }

    @Override
    public void show(View inView, Object obj) {
        //super.show(view, obj);
        if(respAd == null){
            Log.e(TAG, "show(),respAd is null");
            return;
        }
        if(mSplashContainer == null){
            Log.e(TAG, "show(),mSplashContainer is null");
            return;
        }
        Log.e(TAG, "show()");
        //获取SplashView
        try {
            View view = respAd.getSplashView();
            if(mSplashContainer.getChildCount() > 0) {
                mSplashContainer.removeAllViews();
            }
            //把SplashView 添加到ViewGroup中,注意开屏广告view：width >=70%屏幕宽；height >=50%屏幕宽
            mSplashContainer.addView(view);
        } catch (Exception e) {
            Log.e(TAG, "show(),errMsg="+e.getMessage());
            e.printStackTrace();
        }
    }
}
