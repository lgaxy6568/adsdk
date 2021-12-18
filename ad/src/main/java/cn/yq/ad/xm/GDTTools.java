package cn.yq.ad.xm;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bx.xmsdk.CampaignFragment;
import com.qq.e.ads.banner2.UnifiedBannerADListener;
import com.qq.e.ads.banner2.UnifiedBannerView;
import com.qq.e.ads.interstitial2.UnifiedInterstitialAD;
import com.qq.e.ads.interstitial2.UnifiedInterstitialADListener;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.ads.nativ.NativeExpressMediaListener;
import com.qq.e.ads.rewardvideo.RewardVideoAD;
import com.qq.e.ads.rewardvideo.RewardVideoADListener;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.util.AdError;

import java.util.List;
import java.util.Map;

/**
 * 广点通 加载激励视频,插屏工具类
 * 使用 广点通sdk版本号: 4.410.1280
 * 如果有报错,请根据 广点通官方文档修改
 */
public class GDTTools {

    public static final String TAG = "GDTTools";
    /**
     * 激励视频播放是否有效标识
     */
    private static boolean isPlaySuccess = false;
    /**
     * 激励视频ad
     */
    private static RewardVideoAD rewardVideoAD;
    /**
     * 插屏ad
     */
    private static UnifiedInterstitialAD unifiedInterstitialAD;
    /**
     * 底部banner广告
     */
    public static UnifiedBannerView unifiedBannerView;

    private static NativeExpressAD nativeExpressAD;
    private static NativeExpressADView nativeExpressADView;

    /**
     * 加载激励视频
     * @param context context
     * @param fragment 承载活动的 fragment
     * @param bean 参数
     */
    public static void loadGDTRewardVideo(Context context, CampaignFragment fragment, JsBridgeBean bean){
        String pid = bean.pid;
         rewardVideoAD = new RewardVideoAD(context, pid, new RewardVideoADListener() {
            @Override
            public void onADLoad() {
                Log.e(TAG, "onADLoad");
                rewardVideoAD.showAD();
                isPlaySuccess = false;
                if (fragment != null) {
                    fragment.setVideoLoad(bean.requestId);
                }
            }

            @Override
            public void onVideoCached() {
                Log.e(TAG, "onVideoCached");
            }

            @Override
            public void onADShow() {
                Log.e(TAG, "onADShow");
            }

            @Override
            public void onADExpose() {
                Log.e(TAG, "onADExpose");
                if (fragment != null) {
                    fragment.setVideoExposeComplete(bean.requestId);
                }
            }

            @Override
            public void onReward(Map<String, Object> map) {
                isPlaySuccess = true;
            }


            @Override
            public void onADClick() {
                Log.e(TAG, "onADClick");
                if (fragment != null) {
                    fragment.setVideoClickComplete(bean.requestId);
                }
            }

            @Override
            public void onVideoComplete() {
                Log.e(TAG, "onVideoComplete");
            }

            @Override
            public void onADClose() {
                Log.e(TAG, "onADClose");
                if (isPlaySuccess) {
                    if (fragment != null) {
                        fragment.setVideoClose(bean.requestId);
                    }
                } else {
                    if (fragment != null) {
                        fragment.setVideoSkip(bean.requestId);
                    }
                }
            }

            @Override
            public void onError(AdError adError) {
                Log.e(TAG, "onError:--->" + adError.getErrorMsg());
                if (fragment != null) {
                    fragment.setVideoError(bean.requestId, adError.getErrorCode(), adError.getErrorMsg());
                }
            }
        });
        rewardVideoAD.loadAD();
    }

    /**
     * 加载插屏广告
     * @param activity activity
     * @param fragment 承载活动的 fragment
     * @param bean 参数
     */
    public static void loadGDTInterActionAd(Activity activity, CampaignFragment fragment, JsBridgeBean bean) {
        String pid = bean.pid;
        unifiedInterstitialAD = new UnifiedInterstitialAD(activity, pid, new UnifiedInterstitialADListener() {
            @Override
            public void onADReceive() {
                unifiedInterstitialAD.show();
                if(fragment != null){
                    fragment.setVideoLoad(bean.requestId);
                }
            }

            @Override
            public void onVideoCached() {

            }

            @Override
            public void onNoAD(AdError adError) {
                Log.d("广点通-插屏失败",adError.getErrorCode()+""+adError.getErrorMsg()+"");
                if(fragment != null){
                    fragment.setVideoError(bean.requestId,adError.getErrorCode(),adError.getErrorMsg());
                }
            }

            @Override
            public void onADOpened() {

            }

            @Override
            public void onADExposure() {
                if(fragment != null){
                    fragment.setVideoExposeComplete(bean.requestId);
                }
            }

            @Override
            public void onADClicked() {
                if(fragment != null){
                    fragment.setVideoClickComplete(bean.requestId);
                }
            }

            @Override
            public void onADLeftApplication() {
            }

            @Override
            public void onADClosed() {
                unifiedInterstitialAD.close();
                if(fragment != null){
                    fragment.setVideoClose(bean.requestId);
                }
            }

            @Override
            public void onRenderSuccess() {

            }

            @Override
            public void onRenderFail() {

            }
        });
        unifiedInterstitialAD.loadAD();
    }

    public static void  loadGDTBannerAd(Activity activity, ViewGroup containerView, CampaignFragment fragment, JsBridgeBean bean){
        String pid = bean.pid;
        unifiedBannerView = new UnifiedBannerView(activity, pid, new UnifiedBannerADListener() {
            @Override
            public void onNoAD(AdError adError) {
                Log.e(TAG, "广点通banner失败: onNoAD");
                if (fragment != null){
                    fragment.setBannerError(bean.requestId);
                }
            }

            @Override
            public void onADReceive() {
                Log.e(TAG, "广告加载成功回调: onADReceive");
                if (fragment != null){
                    fragment.setBannerLoad(bean.requestId);
                }
            }

            @Override
            public void onADExposure() {
                Log.e(TAG, "广告曝光: onADExposure");
                if (fragment != null){
                    fragment.setBannerExpose(bean.requestId);
                }
            }

            @Override
            public void onADClosed() {
                Log.e(TAG, "广告关闭: onADClosed");
                if (fragment != null){
                    fragment.setBannerClose(bean.requestId);
                }
            }

            @Override
            public void onADClicked() {
                Log.e(TAG, "广告点击: onADClicked");
                if (fragment != null){
                    fragment.setBannerClick(bean.requestId);
                }
            }

            @Override
            public void onADLeftApplication() {
                Log.e(TAG, "广告点击离开 APP : onADLeftApplication");
            }

            @Override
            public void onADOpenOverlay() {
                Log.e(TAG, "广告打开浮层: onADOpenOverlay");
            }

            @Override
            public void onADCloseOverlay() {
                Log.e(TAG, "广告浮层关闭: onADCloseOverlay");
            }
        });

        containerView.addView(unifiedBannerView, getUnifiedBannerLayoutParams(activity));
        unifiedBannerView.loadAD();
    }

    /**
     * banner2.0规定banner宽高比应该为6.4:1 , 开发者可自行设置符合规定宽高比的具体宽度和高度值
     */
    private static FrameLayout.LayoutParams getUnifiedBannerLayoutParams(Activity activity) {
        Point screenSize = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(screenSize);
        return new FrameLayout.LayoutParams(screenSize.x,  Math.round(screenSize.x / 6.4F));
    }


    /**
     * 加载广点通横幅广告
     * @param fragment
     * @param bean
     */
    public static void loadGDTNativeExpressAd(Activity activity,ViewGroup containerView,CampaignFragment fragment, JsBridgeBean bean) {

        //创建广告
        nativeExpressAD = new NativeExpressAD(activity, new ADSize(340, ADSize.AUTO_HEIGHT), bean.pid, new NativeExpressAD.NativeExpressADListener() {
            @Override
            public void onADLoaded(List<NativeExpressADView> list) {

                Log.d("广点通横幅","加载");
                if (nativeExpressADView != null) {
                    nativeExpressADView.destroy();
                }

                nativeExpressADView = list.get(0);
                if (nativeExpressADView.getBoundData().getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
                    nativeExpressADView.setMediaListener(mediaListener);
                }
                if (containerView.getVisibility() != View.VISIBLE) {
                    containerView.setVisibility(View.VISIBLE);
                }

                nativeExpressADView.render();
                if (containerView.getChildCount() > 0) {
                    containerView.removeAllViews();
                }

                containerView.addView(nativeExpressADView);
                if (fragment != null){
                    fragment.setBannerLoad(bean.requestId);
                }
            }

            @Override
            public void onRenderFail(NativeExpressADView nativeExpressADView) {
                Log.d("广点通横幅","失败");
                if (fragment != null){
                    fragment.setBannerError(bean.requestId);
                }
            }

            @Override
            public void onRenderSuccess(NativeExpressADView nativeExpressADView) {

            }

            @Override
            public void onADExposure(NativeExpressADView nativeExpressADView) {
                Log.d("广点通横幅","曝光");
                if (fragment != null){
                    fragment.setBannerExpose(bean.requestId);
                }
            }

            @Override
            public void onADClicked(NativeExpressADView nativeExpressADView) {
                Log.d("广点通横幅","点击");
                if (fragment != null){
                    fragment.setBannerClick(bean.requestId);
                }
            }

            @Override
            public void onADClosed(NativeExpressADView nativeExpressADView) {
                Log.d("广点通横幅","关闭");
                if (fragment != null){
                    fragment.setBannerClose(bean.requestId);
                }
                if (containerView != null && containerView.getChildCount() > 0) {
                    containerView.removeAllViews();
                    containerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onADLeftApplication(NativeExpressADView nativeExpressADView) {

            }

            @Override
            public void onADOpenOverlay(NativeExpressADView nativeExpressADView) {

            }

            @Override
            public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {

            }

            @Override
            public void onNoAD(AdError adError) {
                Log.d("广点通横幅","失败");
                if (fragment != null){
                    fragment.setBannerError(bean.requestId);
                }
            }
        });
        nativeExpressAD.loadAD(1);
    }

    private static NativeExpressMediaListener mediaListener = new NativeExpressMediaListener() {
        @Override
        public void onVideoInit(NativeExpressADView nativeExpressADView) {
        }

        @Override
        public void onVideoLoading(NativeExpressADView nativeExpressADView) {
            Log.i(TAG, "onVideoLoading");
        }

        @Override
        public void onVideoCached(NativeExpressADView nativeExpressADView) {

        }

        @Override
        public void onVideoReady(NativeExpressADView nativeExpressADView, long l) {
            Log.i(TAG, "onVideoReady");
        }

        @Override
        public void onVideoStart(NativeExpressADView nativeExpressADView) {

        }

        @Override
        public void onVideoPause(NativeExpressADView nativeExpressADView) {

        }

        @Override
        public void onVideoComplete(NativeExpressADView nativeExpressADView) {

        }

        @Override
        public void onVideoError(NativeExpressADView nativeExpressADView, AdError adError) {
            Log.i(TAG, "onVideoError");
        }

        @Override
        public void onVideoPageOpen(NativeExpressADView nativeExpressADView) {
            Log.i(TAG, "onVideoPageOpen");
        }

        @Override
        public void onVideoPageClose(NativeExpressADView nativeExpressADView) {
            Log.i(TAG, "onVideoPageClose");
        }
    };

}
