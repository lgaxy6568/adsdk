package cn.yq.ad.tt;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.CSJAdError;
import com.bytedance.sdk.openadsdk.CSJSplashAd;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import cn.yq.ad.AdConf;
import cn.yq.ad.Adv_Type;
import cn.yq.ad.impl.ADBaseImpl;
import cn.yq.ad.impl.ClickModel;
import cn.yq.ad.impl.DismissModel;
import cn.yq.ad.impl.FailModel;
import cn.yq.ad.impl.PresentModel;
import cn.yq.ad.proxy.model.AdRespItem;
import cn.yq.ad.tt.config.TTUtil;
import cn.yq.ad.util.ADHelper;
import cn.yq.ad.util.AdSize;
import cn.yq.ad.util.SizeUtil;

/**
 * 穿山甲全屏~开屏
 * Created by liguo on 2018/10/16.
 * desc
 */
public class SplashForCSJ extends ADBaseImpl {
    private final String TAG = SplashForCSJ.class.getSimpleName();
    private final String adId;
    protected final String ids;
    private Context act;
    private final String appId;
    private WeakReference<ViewGroup> mSplashContainer;
    private CSJSplashAd respAd;
    SplashForCSJ(Activity act, String appId, String tmpAdId, ViewGroup ad_container) {
        this.act = getContextFromActivity(act);
        this.appId = replaceTrim_R_N(appId);
        this.ids = replaceTrim_R_N(tmpAdId);
        if(tmpAdId == null || tmpAdId.trim().length() == 0){
            throw new IllegalArgumentException("SplashForTT(),adId is empty");
        }
        this.adId = replaceTrim_R_N(tmpAdId);
        Log.e(TAG, "SplashForTT(),this.adId="+this.adId+",appId="+appId);
        this.mSplashContainer = new WeakReference<>(ad_container);
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
        int widthDp = SizeUtil.px2dip(act,accept_width);
        int heightDp = SizeUtil.px2dip(act,accept_height);
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(adId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(accept_width, accept_height)
                .setExpressViewAcceptedSize(widthDp,heightDp)
//                .setSplashButtonType(TTAdConstant.SPLASH_BUTTON_TYPE_DOWNLOAD_BAR)
//                .setDownloadType(TTAdConstant.DOWNLOAD_TYPE_POPUP)
                .build();
        TTAdManager tam = TTUtil.get().getAdManager();
//        tam.setAppId(appId);
//        tam.requestPermissionIfNecessary(act);
        TTAdNative mTTAdNative = tam.createAdNative(act);
        //step4:请求广告，调用开屏广告异步请求接口，对请求回调的广告作渲染处理
        call_back_count.set(1);
        final int AD_TIME_OUT = getRequestTimeOutFromExtra();
        Log.e(TAG, "load(),accept_width="+accept_width+",accept_height="+accept_height+",超时时间="+AD_TIME_OUT);
        mTTAdNative.loadSplashAd(adSlot,new TempAdListener(this,adId,TAG,getAdParamItem()), AD_TIME_OUT);
    }

    @Override
    public Adv_Type getAdvType() {
        return Adv_Type.tt;
    }

    private static class TempAdListener implements TTAdNative.CSJSplashAdListener{
        private final String randomUUID;
        private final Map<String,String> mmp;
        private final String adId;
        private final String TAG;
        private final AdRespItem adRespItem;
        private final WeakReference<SplashForCSJ> wrTT;
        public TempAdListener(SplashForCSJ tt, String adId, String tag, AdRespItem item) {
            this.wrTT = new WeakReference<>(tt);
            this.adId = adId;
            this.TAG = tag;
            this.adRespItem = item;
            randomUUID = UUID.randomUUID().toString();
            mmp = new LinkedHashMap<>();
            mmp.put(randomUUID,randomUUID);
        }

        private SplashForCSJ getTT(){
            return wrTT.get();
        }

        /**
         * 广告物料、素材加载成功回调
         */
        @Override
        public void onSplashLoadSuccess() {

        }

        /**
         * 广告物料、素材加载失败或超时回调
         * 如果媒体想判断是否是超时回调可以通过  csjAdError.getCode() == 23进行判断
         * code为23则为超时回调
         * @param csjAdError
         */
        @Override
        public void onSplashLoadFail(CSJAdError csjAdError) {
            int code = csjAdError.getCode();
            String message = csjAdError.getMsg();
            FailModel fm= FailModel.toStr(code,"ERROR_素材加载_"+message,adId, Adv_Type.tt);
            Log.e(TAG, "onSplashLoadFail(),err_msg="+fm.toFullMsg());
            if(mmp.containsKey(randomUUID)) {
                mmp.remove(randomUUID);
                SplashForCSJ tt = getTT();
                if(tt != null) {
                    tt.defaultCallback.onAdFailed(fm.setAdRespItem(adRespItem));
                }
            }else{
                Log.e(TAG,"onSplashLoadFail(),已经回调过了onTimeout()方法");
            }
        }

        /**
         * 广告渲染失败或超时回调
         * 如果媒体想判断是否是超时回调可以通过  csjAdError.getCode() == 23进行判断
         * code为23则为超时回调
         * @param csjAdError
         */
        @Override
        public void onSplashRenderFail(CSJSplashAd csjSplashAd, CSJAdError csjAdError) {
            int code = csjAdError.getCode();
            String message = csjAdError.getMsg();
            FailModel fm= FailModel.toStr(code,"ERROR_素材渲染_"+message,adId, Adv_Type.tt);
            Log.e(TAG, "onSplashRenderFail(),err_msg="+fm.toFullMsg());
            if(mmp.containsKey(randomUUID)) {
                mmp.remove(randomUUID);
                SplashForCSJ tt = getTT();
                if(tt != null) {
                    tt.defaultCallback.onAdFailed(fm.setAdRespItem(adRespItem));
                }
            }else{
                Log.e(TAG,"onSplashRenderFail(),已经回调过了onTimeout()方法");
            }
        }

        @Override
        public void onSplashRenderSuccess(final CSJSplashAd ad) {
            Log.e(TAG, "onSplashAdLoad(),开屏广告加载成功");
            if (ad == null) {
                Log.e(TAG, "onSplashAdLoad(),ad is null , will return");
                return;
            }
            final SplashForCSJ tt = getTT();
            if(tt == null) {
                return;
            }
            tt.respAd = ad;
            //检查Activity是否已销毁
            ViewGroup tmpAdContainer = tt.mSplashContainer.get();
            Activity act = ADHelper.getActivityFromView(tmpAdContainer);
            if(act != null && (act.isFinishing() || act.isDestroyed())){
                FailModel fm = FailModel.toStr(-2,"加载成功_但Activity已销毁~",adId,Adv_Type.tt);
                Log.e(TAG, "onSplashAdLoad()"+fm.toFullMsg());
                tt.defaultCallback.onAdFailed(fm.setAdRespItem(adRespItem));
                return;
            }
            //设置不开启开屏广告倒计时功能以及不显示跳过按钮,如果这么设置，您需要自定义倒计时逻辑
            //ad.setNotAllowSdkCountdown();
            tt.defaultCallback.onAdPresent(PresentModel.getInstance(adId, Adv_Type.tt).setData(ad).setAdRespItem(adRespItem));
            //设置SplashView的交互监听器
            ad.setSplashAdListener(new CSJSplashAd.SplashAdListener() {
//                @Override
//                public void onAdClicked(View view, int type) {
//                    Log.e(TAG, "onAdClicked,type="+type);
//                    int adType = type == 4 ? 1 : 2;
//                    tt.defaultCallback.onAdClick(ClickModel.getInstance(0,adType,adId,Adv_Type.tt).setAdRespItem(adRespItem));
//                }
//
//                @Override
//                public void onAdShow(View view, int type) {
//                    Log.e(TAG, "onAdShow,type="+type);
//                    if(tt.call_back_count.get() == 1) {
//                        tt.call_back_count.set(0);
//                        tt.defaultCallback.onADExposed(PresentModel.getInstance(adId, Adv_Type.tt).setAdRespItem(adRespItem));
//                    }
//                }
//
//                @Override
//                public void onAdSkip() {
//                    Log.e(TAG, "onAdSkip");
//                    tt.defaultCallback.onAdSkip(PresentModel.getInstance(adId, Adv_Type.tt).setAdRespItem(adRespItem));
//                }
//
//                @Override
//                public void onAdTimeOver() {
//                    Log.e(TAG, "onAdTimeOver");
//                    tt.defaultCallback.onAdDismissed(DismissModel.newInstance(adId,Adv_Type.tt,2).setAdRespItem(adRespItem));
//                }


                @Override
                public void onSplashAdShow(CSJSplashAd csjSplashAd) {
                    Log.i(TAG, "onSplashAdShow(),type="+csjSplashAd.getInteractionType());
                    if(tt.call_back_count.get() == 1) {
                        tt.call_back_count.set(0);
                        tt.defaultCallback.onADExposed(PresentModel.getInstance(adId, Adv_Type.tt).setAdRespItem(adRespItem));
                    }
                }

                @Override
                public void onSplashAdClick(CSJSplashAd csjSplashAd) {
                    int type = csjSplashAd.getInteractionType();
                    Log.i(TAG, "onSplashAdClick(),type="+type);
                    int adType = type == 4 ? 1 : 2;
                    tt.defaultCallback.onAdClick(ClickModel.getInstance(0,adType,adId,Adv_Type.tt).setAdRespItem(adRespItem));
                }

                @Override
                public void onSplashAdClose(CSJSplashAd csjSplashAd, int closeType) {
                    Log.i(TAG, "onSplashAdClose(),closeType="+closeType);
                    tt.defaultCallback.onAdDismissed(DismissModel.newInstance(adId,Adv_Type.tt,2).setAdRespItem(adRespItem));
                }
            });
        }
    }

    @Override
    public AdConf getCfg() {
        AdConf bd = new AdConf();
        bd.setAppId(appId);
        bd.setAdId(adId);
        bd.setAdRespItem(getAdParamItem());
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
        ViewGroup vg = mSplashContainer.get();
        if(vg == null){
            Log.e(TAG, "show(),vg is null");
            return;
        }
        Log.e(TAG, "show()");
        //获取SplashView
        try {
            View view = respAd.getSplashView();
            if(vg.getChildCount() > 0) {
                vg.removeAllViews();
            }
            //把SplashView 添加到ViewGroup中,注意开屏广告view：width >=70%屏幕宽；height >=50%屏幕宽
            vg.addView(view);
        } catch (Exception e) {
            Log.e(TAG, "show(),errMsg="+e.getMessage());
            e.printStackTrace();
        }
    }
}
