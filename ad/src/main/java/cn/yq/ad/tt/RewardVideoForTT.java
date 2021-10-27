package cn.yq.ad.tt;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import cn.yq.ad.ADCallback;
import cn.yq.ad.Adv_Type;
import cn.yq.ad.VideoADCallback;
import cn.yq.ad.impl.ADBaseImpl;
import cn.yq.ad.impl.ClickModel;
import cn.yq.ad.impl.DismissModel;
import cn.yq.ad.impl.FailModel;
import cn.yq.ad.impl.PresentModel;
import cn.yq.ad.impl.ShowParam;
import cn.yq.ad.tt.config.TTUtil;

public class RewardVideoForTT extends ADBaseImplByTT<TTAdNative> {

    @Override
    public List<ADCallback> getCallBackList() {
        return null;
    }

    private String L_TAG(){
        return "RewardVideoForTT_"+adId+"_"+hashCode();
    }

    private Map<String, Object> extra;

    private VideoADCallback videoADCallback;

    private TTAdNative mTTAdNative;

    private TTRewardVideoAd mTTRewardVideoAd;

    private String mUUID;

    RewardVideoForTT(Activity activity, String appId, String adId, Map<String, Object> extra) {
        super(activity,appId,adId);
        this.extra = extra;
        TTAdManager tam = TTUtil.get().getAdManager();
        final Context ctx = ADBaseImpl.getContextFromActivity(activity);
//        tam.requestPermissionIfNecessary(ctx);
        mTTAdNative = tam.createAdNative(ctx);
        Log.e(L_TAG(), "实例创建,appId="+appId+",adId="+adId);
    }

    @Override
    public void load() {
        if (getWeakActivity() == null || TextUtils.isEmpty(appId) || TextUtils.isEmpty(adId)) {
            Log.e(L_TAG(), "load(),adId is null");
            return;
        }
        if (mTTAdNative == null) {
            Log.e(L_TAG(), "load(),mTTAdNative is null");
            return;
        }
        String uid = "";
        String mediaExtra;
        if (extra == null) {
            extra = new HashMap<>();
        } else {
            uid = (String) extra.get("userID");
        }
        mUUID = UUID.randomUUID().toString();
        if (!TextUtils.isEmpty(mUUID)) {
            extra.put("orderId", mUUID);
        }
        JSONObject jsonObject = new JSONObject(extra);
        mediaExtra = jsonObject.toString();
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(adId)
                .setSupportDeepLink(true)
                .setAdCount(1)
                .setImageAcceptedSize(1080, 1920)
//                .setRewardName(advPos)
                .setRewardAmount(1)
                .setUserID(uid)
                .setMediaExtra(mediaExtra)
                .setOrientation(TTAdConstant.VERTICAL)
                .build();
        Log.e(L_TAG(), "load(),appId="+appId+",adId="+adId+",uid="+uid+",uuid="+mUUID);
        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            private final AtomicInteger ab_load_status = new AtomicInteger(0);
            TTRewardVideoAd mTmpVideo = null;
            @Override
            public void onError(int i, String s) {
                ab_load_status.set(1);
                Log.e(L_TAG(), "onError(),i="+i+",s=" + s);
                if (videoADCallback != null) {
                    FailModel fm = FailModel.toStr(i, s, adId, Adv_Type.tt);
                    videoADCallback.onAdFailed(fm);
                }
            }

            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ttRewardVideoAd) {
                if(ab_load_status.get() ==1){
                    Log.e(L_TAG(), "onRewardVideoAdLoad(), 已经报错了，will return");
                    return;
                }
                if (getWeakActivity() == null) {
                    Log.e(L_TAG(), "onRewardVideoAdLoad(), act is null");
                    return;
                }
                if (videoADCallback == null) {
                    Log.e(L_TAG(), "onRewardVideoAdLoad(),videoADCallback is null");
                    return;
                }
                mTmpVideo = ttRewardVideoAd;
                Log.e(L_TAG(), "onRewardVideoAdLoad()");
            }

            @Override
            public void onRewardVideoCached(TTRewardVideoAd ttRewardVideoAd) {
                if(ab_load_status.get() ==1){
                    Log.e(L_TAG(), "onRewardVideoCached(), 已经报错了，will return");
                    return;
                }
                if (videoADCallback == null) {
                    Log.e(L_TAG(), "onRewardVideoCached(),videoADCallback is null");
                    return;
                }
                Log.e(L_TAG(), "onRewardVideoCached()");
                setCallbackToReward(mTmpVideo);
                videoADCallback.onAdPresent(PresentModel.getInstance(adId, Adv_Type.tt).setData(extra));
            }

            @Override
            public void onRewardVideoCached() {
                Log.e(L_TAG(), "onRewardVideoCached()");
            }
        });
    }

    private void setCallbackToReward(TTRewardVideoAd ttRewardVideoAd) {
        if (ttRewardVideoAd == null) {
            Log.e(L_TAG(), "setCallbackToReward mTTRewardVideoAd is null");
            return;
        }
        TTRewardVideoAd.RewardAdInteractionListener tmpListener = createTmpListener();
        // FIXME: 2020/3/25 特别注意 最后的会把前面的覆盖掉
        ttRewardVideoAd.setRewardAdInteractionListener(tmpListener);
        mTTRewardVideoAd = ttRewardVideoAd;
        Log.e(L_TAG(), "setCallbackToReward(),tmpListener="+tmpListener.hashCode()+",mTTRewardVideoAd="+mTTRewardVideoAd.hashCode());
    }

    @Override
    public void destroy() {
        if (videoADCallback != null) {
            videoADCallback = null;
        }
        if(extra != null){
            extra.clear();
        }
    }
    private TTRewardVideoAd.RewardAdInteractionListener createTmpListener(){
        TTRewardVideoAd.RewardAdInteractionListener tmpListener = new TTRewardVideoAd.RewardAdInteractionListener() {
            @Override
            public void onAdShow() {
                Log.e(L_TAG(), "onAdShow()");
                if (videoADCallback != null) {
                    videoADCallback.onADExposed(PresentModel.getInstance(adId, Adv_Type.tt));
                    videoADCallback.onVideoStartPlay(PresentModel.getInstance(adId, Adv_Type.tt));
                }
            }

            @Override
            public void onAdVideoBarClick() {
                Log.e(L_TAG(), "onAdVideoBarClick()");
                if (videoADCallback != null) {
                    videoADCallback.onAdClick(ClickModel.getInstance(0,2, adId,Adv_Type.tt));
                }
            }

            @Override
            public void onAdClose() {
                Log.e(L_TAG(), "onAdClose()");
                if (videoADCallback != null) {
                    videoADCallback.onAdDismissed(DismissModel.newInstance(adId,Adv_Type.tt));
                }
            }

            @Override
            public void onVideoComplete() {
                Log.e(L_TAG(), "onVideoComplete()");
                if (videoADCallback != null) {
                    videoADCallback.onVideoPlayComplete(PresentModel.getInstance(adId, Adv_Type.tt));
                }
                mTTRewardVideoAd = null;
            }

            @Override
            public void onVideoError() {
                Log.e(L_TAG(), "onVideoError()");
                mTTRewardVideoAd = null;
            }

//            @Override
//            public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName) {
//                if (videoADCallback != null) {
//                    PresentModel presentModel = PresentModel.getInstance(adId, Adv_Type.tt);
//                    presentModel.setData(mUUID);
//                    videoADCallback.onRewardVerify(rewardVerify, presentModel);
//                }
//                Log.e(L_TAG(), "onRewardVerify:" + rewardVerify + ",rewardName:" + rewardName);
//            }

            @Override
            public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName, int errorCode, String errorMsg) {
                if (videoADCallback != null) {
                    PresentModel presentModel = PresentModel.getInstance(adId, Adv_Type.tt);
                    presentModel.setData(mUUID);
                    videoADCallback.onRewardVerify(rewardVerify, presentModel);
                }
                Log.e(L_TAG(), "onRewardVerify:" + rewardVerify + ",rewardName:" + rewardName);
            }

            @Override
            public void onSkippedVideo() {
                Log.e(L_TAG(), "onSkippedVideo()");
            }
        };
        return tmpListener;
    }

    @Override
    public void addCallback(ADCallback callback) {
        if (callback instanceof VideoADCallback) {
            videoADCallback = (VideoADCallback) callback;
            if(mTTRewardVideoAd != null) {
                mTTRewardVideoAd.setRewardAdInteractionListener(createTmpListener());
            }
        }
    }

    @Override
    public void reload() {
        load();
    }


    @Override
    public void show(View view, Object obj) {
        if (getWeakActivity() == null) {
            Log.e(L_TAG(), "show(),wrActivity is null.");
            return;
        }
        int show_status;
        ShowParam sm = null;
        if(obj instanceof ShowParam){
            sm = (ShowParam)obj;
        }
        if (mTTRewardVideoAd != null) {
            Log.e(L_TAG(), "show(),开始展示");
            show_status = 1;
            boolean play = true;
            if(sm != null && sm.getMode() == 2){
                play = false;
            }
            if(play) {
                mTTRewardVideoAd.showRewardVideoAd(getWeakActivity());
            }
        }else{
            show_status = 2;
            Log.e(L_TAG(), "show(),mTTRewardVideoAd is null.");
        }
        if(sm != null){
            sm.setStatus(show_status);
        }
    }

    private void logMethodCallStacktrace(String tag){
            RuntimeException re = new RuntimeException("here");
            re.fillInStackTrace();
            Log.e(tag,"Called:",re);
    }
}
