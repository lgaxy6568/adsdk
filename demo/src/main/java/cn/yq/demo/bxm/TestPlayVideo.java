package cn.yq.demo.bxm;

import android.app.Activity;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.dhcw.sdk.BDAdvanceBaseAppNative;
import com.meishu.sdk.core.utils.LogUtil;

public class TestPlayVideo {
    public static TestPlayVideo getNewInstance(){
        return new TestPlayVideo();
    }
    private static final String TAG = TestPlayVideo.class.getSimpleName();
    // FIXME: 2021/8/19 WAIT_LIGUO 这里面的广告ID用服务器返回的,由外部传入
    final String appid = "5133118"; // "5060085";
    final String adspotid = "946566999";    // "945132252";

    //一、加载穿山甲激励视频
    //BDAdvanceBaseAppNative 绑定激励视频回调
    public void load(final Activity activity, final BDAdvanceBaseAppNative appNative) {

        try {
            //初始化
            TTAdSdk.init(activity, new TTAdConfig.Builder()
                    .appId(appid)
                    .useTextureView(true) //使用TextureView控件播放视频,默认为SurfaceView,当有SurfaceView冲突的场景，可以使用TextureView
                    .appName("appName")
                    .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)
                    .allowShowNotify(true) //是否允许sdk展示通知栏提示
                    .allowShowPageWhenScreenLock(true) //是否在锁屏场景支持展示广告落地页
                    .debug(false) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                    .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI, TTAdConstant.NETWORK_STATE_4G) //允许直接下载的网络状态集合
                    .supportMultiProcess(true)//是否支持多进程
                    .needClearTaskReset()
                    .build());

            //创建TTAdNative对象
            TTAdNative adNative = TTAdSdk.getAdManager().createAdNative(activity);

            //模版渲染请求AdSlot
            AdSlot adSlot = new AdSlot.Builder()
                    .setCodeId(adspotid)
                    .setSupportDeepLink(true)
                    .setAdCount(1)
                    .setRewardName("coin")
                    .setRewardAmount(1)
                    .setExpressViewAcceptedSize(500,500)//个性化模板广告需要设置期望个性化模板广告的大小,单位dp,激励视频场景，只要设置的值大于0即可。仅模板广告需要设置此参数
                    .setImageAcceptedSize(1080, 1920)
                    .setOrientation(TTAdConstant.VERTICAL)
                    .setMediaExtra("media_extra")
                    .setUserID("user123")
                    .build();

            //加载激励视频广告
            adNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
                @Override
                public void onError(int i, String s) {
                    LogUtil.e(TAG,"onError(),code="+i+",msg="+s);
                    appNative.onError(i);
                }

                @Override
                public void onRewardVideoAdLoad(TTRewardVideoAd ttRewardVideoAd) {
                    LogUtil.e(TAG,"onRewardVideoAdLoad()");
                    if(ttRewardVideoAd==null){
                        appNative.onError(0);
                    }else{
                        bindRewardListener(ttRewardVideoAd, appNative);
                        appNative.onADLoad();
                    }
                }

                @Override
                public void onRewardVideoCached() {
                    LogUtil.e(TAG,"onRewardVideoCached()");
                }

                @Override
                public void onRewardVideoCached(TTRewardVideoAd ttRewardVideoAd) {
                    LogUtil.e(TAG,"onRewardVideoCached()");
                }
            });
        } catch (Throwable e) {
            LogUtil.e(TAG,"load(),err="+e.getMessage());
            appNative.onError(0);
        }
    }


    //二、绑定穿山甲激励视频回调
    //BDAdvanceBaseAppNative 绑定激励视频回调
    TTRewardVideoAd mttRewardVideoAd;
    private void bindRewardListener(TTRewardVideoAd ttRewardVideoAd, final BDAdvanceBaseAppNative appNative) {
        mttRewardVideoAd = ttRewardVideoAd;
        mttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {
            @Override
            public void onAdShow() {
                LogUtil.e(TAG,"onAdShow()");
                appNative.onADShow();
            }

            @Override
            public void onAdVideoBarClick() {
                LogUtil.e(TAG,"onAdVideoBarClick()");
                appNative.onADClick();
            }

            @Override
            public void onAdClose() {
                LogUtil.e(TAG,"onAdClose()");
                appNative.onADClose();
            }

            @Override
            public void onVideoComplete() {
                LogUtil.e(TAG,"onVideoComplete()");
                appNative.onVideoComplete();
            }

            @Override
            public void onVideoError() {
                LogUtil.e(TAG,"onVideoError()");
                appNative.onError(0);
            }

            @Override
            public void onRewardVerify(boolean b, int i, String s, int i1, String s1) {
                LogUtil.e(TAG,"onRewardVerify()");
                appNative.onReward();
            }

            @Override
            public void onSkippedVideo() {
                LogUtil.e(TAG,"onSkippedVideo()");
                appNative.onSkipped();
            }
        });

    }

    //三、播放穿山甲激励视频
    public void play(Activity activity) {
        if(mttRewardVideoAd!=null){
            mttRewardVideoAd.showRewardVideoAd(activity);
        }
    }
}
