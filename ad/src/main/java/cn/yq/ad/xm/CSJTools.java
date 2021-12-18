package cn.yq.ad.xm;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bx.xmsdk.CampaignFragment;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;

import java.util.List;

/**
 * 穿山甲 加载激励视频,插屏工具类
 * 使用 穿山甲sdk版本号: 3.5.0.6
 * 如果有报错,请根据 穿山甲官方文档修改
 */
public class CSJTools {

    public static final String TAG = "CSJTools";
    /**
     * 激励视频播放是否有效标识
     */
    private static boolean isPlaySuccess = false;
    /**
     * 激励视频ad
     */
    private static TTRewardVideoAd mttRewardVideoAd;
    /**
     * 插屏ad
     */
    private static TTFullScreenVideoAd ttFullScreenVideoAd;
    private static TTNativeExpressAd nativeExpressAd;

    /**
     * 加载穿山甲激励视频广告
     * @param activity activity
     * @param fragment 承载活动的 fragment
     * @param bean  传递的参数
     */
    public static void loadBytedanceAd(Activity activity, CampaignFragment fragment, JsBridgeBean bean){

        String pid = bean.pid;
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(pid) // 这里要替换成 bean.pid
                .setAdCount(1)
                //个性化模板广告需要设置期望个性化模板广告的大小,单位dp,激励视频场景，只要设置的值大于0即可
                .setExpressViewAcceptedSize(500,500)
                .setImageAcceptedSize(1080, 1920)
                .build();

        TTAdNative mTTAdNative= TTAdSdk.getAdManager().createAdNative(activity.getApplicationContext());

        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.d(TAG,"穿山甲-加载失败"+code+"===="+message);
                if(fragment != null){
                    fragment.setVideoError(bean.requestId,code,message);
                }
            }

            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
                if(fragment != null){
                    fragment.setVideoLoad(bean.requestId);
                }
                Log.d(TAG,"穿山甲-加载成功");
                isPlaySuccess = false;
                mttRewardVideoAd = ad;
                bindBytedanceVideoAdListener(fragment,bean,mttRewardVideoAd);
                mttRewardVideoAd.showRewardVideoAd(activity);
            }

            @Override
            public void onRewardVideoCached() {

            }

            @Override
            public void onRewardVideoCached(TTRewardVideoAd ttRewardVideoAd) {

            }
        });

    }

    /**
     * 穿山甲激励视频绑定监听事件
     */
    private static void bindBytedanceVideoAdListener(CampaignFragment fragment,JsBridgeBean bean,TTRewardVideoAd ad){
        ad.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {

            @Override
            public void onAdShow() {
                Log.d(TAG,"穿山甲-曝光");
                if(fragment != null){
                    fragment.setVideoExposeComplete(bean.requestId);
                }
            }

            @Override
            public void onAdVideoBarClick() {
                Log.d(TAG,"穿山甲-点击");
                if(fragment != null){
                    fragment.setVideoClickComplete(bean.requestId);
                }
            }

            @Override
            public void onVideoComplete() {
                isPlaySuccess = true;
            }

            @Override
            public void onAdClose() {
                if (isPlaySuccess){
                    if (fragment != null){
                        fragment.setVideoClose(bean.requestId);
                    }
                }else {
                    if (fragment != null){
                        fragment.setVideoSkip(bean.requestId);
                    }
                }
            }

            @Override
            public void onVideoError() {
                Log.d(TAG,"穿山甲-视频失败");
                if (fragment != null){
                    fragment.setVideoError(bean.requestId,0,"msg");
                }
            }

            @Override
            public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName, int errorCode, String errorMsg) {
                Log.d(TAG,"穿山甲-验证");
                isPlaySuccess = rewardVerify;
            }

            @Override
            public void onSkippedVideo() {

            }
        });
    }

    /**
     * 加载插屏广告
     * @param activity activity
     * @param fragment 承载活动的 fragment
     * @param bean  传递的参数
     */
    public static void loadCSJInterActionAd(Activity activity,CampaignFragment fragment, JsBridgeBean bean) {
        String pid = bean.pid;
        TTAdNative mTTAdNative = TTAdSdk.getAdManager().createAdNative(activity);
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(pid)
                //模板广告需要设置期望个性化模板广告的大小,单位dp,激励视频场景，只要设置的值大于0即可
                .setExpressViewAcceptedSize(500,500)
                .setSupportDeepLink(true)
                .setOrientation(TTAdConstant.VERTICAL)//必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                .build();

        mTTAdNative.loadFullScreenVideoAd(adSlot, new TTAdNative.FullScreenVideoAdListener() {

            @Override
            public void onFullScreenVideoCached(TTFullScreenVideoAd ttFullScreenVideoAd) {

            }

            @Override
            public void onError(int code, String message) {
                if(fragment != null){
                    fragment.setVideoError(bean.requestId,code,message);
                }
                Log.d("穿山甲-插屏失败",code+""+message+"");
            }

            @Override
            public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ad) {
                ttFullScreenVideoAd = ad;

                if(fragment != null){
                    fragment.setVideoLoad(bean.requestId);
                }
                ad.setFullScreenVideoAdInteractionListener(new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {
                    @Override
                    public void onAdShow() {
                        if(fragment != null){
                            fragment.setVideoExposeComplete(bean.requestId);
                        }
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        if(fragment != null){
                            fragment.setVideoClickComplete(bean.requestId);
                        }
                    }

                    @Override
                    public void onVideoComplete() {

                    }

                    @Override
                    public void onAdClose() {
                        if(fragment != null){
                            fragment.setVideoClose(bean.requestId);
                        }
                    }

                    @Override
                    public void onSkippedVideo() {
                        if(fragment != null){
                            fragment.setVideoSkip(bean.requestId);
                        }
                    }
                });

                ad.showFullScreenVideoAd(activity, TTAdConstant.RitScenes.GAME_GIFT_BONUS, null);
            }

            @Override
            public void onFullScreenVideoCached() {
                if (ttFullScreenVideoAd != null) {
                    //展示广告，并传入广告展示的场景
                    ttFullScreenVideoAd = null;
                } else {
                    Toast.makeText( activity, "请先加载广告", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 加载穿山甲banner广告
     * @param fragment
     * @param bean
     */
    public static void loadCSJBannerAd(Activity activity, ViewGroup containerView, CampaignFragment fragment, JsBridgeBean bean) {
        TTAdNative mTTAdNative = TTAdSdk.getAdManager().createAdNative(activity.getApplicationContext());
        String pid = bean.pid;
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(pid) //广告位id
                .setSupportDeepLink(true)
                .setAdCount(1) //请求广告数量为1到3条
                .setExpressViewAcceptedSize(600,0) //期望模板广告view的size,单位dp
                .build();

        mTTAdNative.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            //请求失败回调
            @Override
            public void onError(int code, String message) {
                if (fragment != null){
                    fragment.setBannerError(bean.requestId);
                }
            }

            //请求成功回调
            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.size() == 0) {
                    return;
                }
                if (fragment != null){
                    fragment.setBannerLoad(bean.requestId);
                }
                nativeExpressAd = ads.get(0);
                nativeExpressAd.setSlideIntervalTime(30 * 1000);//设置轮播间隔 ms,不调用则不进行轮播展示
                bindBytedanceBannerAdListener(activity,containerView,fragment, bean, nativeExpressAd);
                nativeExpressAd.render();//调用render开始渲染广告
            }
        });

    }

    /**
     * 绑定穿山甲banner类型监听
     * @param fragment
     * @param bean
     * @param ad
     */
    private static void bindBytedanceBannerAdListener(Activity activity,ViewGroup containerView,CampaignFragment fragment,JsBridgeBean bean, TTNativeExpressAd ad) {
        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override
            public void onAdClicked(View view, int type) {
                if (fragment != null){
                    fragment.setBannerClick(bean.requestId);
                }
            }

            @Override
            public void onAdShow(View view, int type) {

            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                if (fragment != null){
                    fragment.setBannerError(bean.requestId);
                }
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                if (fragment != null){
                    fragment.setBannerExpose(bean.requestId);
                }
                //在渲染成功回调时展示广告，提升体验
                containerView.removeAllViews();
                containerView.addView(view);
            }
        });

        //dislike设置
        bindDislike(activity,containerView,fragment,bean,ad);

        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD){
            return;
        }

    }

    /**
     * 穿山甲banner广告关闭事件绑定
     * @param fragment
     * @param bean
     * @param ad
     */
    private static void bindDislike(Activity activity,ViewGroup containerView,CampaignFragment fragment,JsBridgeBean bean,TTNativeExpressAd ad) {

        //使用默认个性化模板中默认dislike弹出样式
        ad.setDislikeCallback(activity, new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onShow() {

            }

            @Override
            public void onSelected(int i, String s, boolean b) {
                //用户选择不喜欢原因后，移除广告展示
                if (fragment != null){
                    fragment.setBannerClose(bean.requestId);
                }
                containerView.removeAllViews();
            }

            @Override
            public void onCancel() {

            }
        });
    }

}
