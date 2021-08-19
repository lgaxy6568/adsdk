package cn.yq.ad.bxm;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.dhcw.sdk.BDAdvanceRewardAd;
import com.dhcw.sdk.BDAdvanceRewardListener;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.yq.ad.ADCallback;
import cn.yq.ad.AdConf;
import cn.yq.ad.Adv_Type;
import cn.yq.ad.VideoADCallback;
import cn.yq.ad.impl.ADBaseImpl;
import cn.yq.ad.impl.ClickModel;
import cn.yq.ad.impl.DismissModel;
import cn.yq.ad.impl.FailModel;
import cn.yq.ad.impl.PresentModel;
import cn.yq.ad.impl.ShowParam;
import cn.yq.ad.util.AdLogUtils;

public class RewardVideoForBxm extends ADBaseImpl implements BDAdvanceRewardListener {

    @Override
    public List<ADCallback> getCallBackList() {
        return null;
    }

    private String L_TAG(){
        return "RewardVideoForBxm_"+adId;
    }

    private Map<String, Object> extra;

    private VideoADCallback videoADCallback;

    private final String appId ;
    private final String adId ;
    protected WeakReference<Activity> wrAct;
    private BDAdvanceRewardAd rewardVideoAD;
    RewardVideoForBxm(Activity activity, String appId, String adId, Map<String, Object> extra) {
        this.wrAct = new WeakReference<>(activity);
        this.appId = appId;
        this.adId = adId;
        this.extra = extra;
        Log.e(L_TAG(), "实例创建,appId="+appId+",adId="+adId);
    }
    private Activity getWeakActivity(){
        return wrAct.get();
    }

    private final AtomicBoolean isReady = new AtomicBoolean(false);
    @Override
    public void load() {
        if (getWeakActivity() == null || TextUtils.isEmpty(appId) || TextUtils.isEmpty(adId)) {
            Log.e(L_TAG(), "load(),adId is null");
            return;
        }
        Activity act = wrAct.get();
        if(act == null){
            Log.e(L_TAG(), "load(),act is null");
            return;
        }
        rewardVideoAD = new BDAdvanceRewardAd(act, adId);
        isReady.set(false);
        rewardVideoAD.setBdAdvanceRewardListener(this);
        rewardVideoAD.loadAD();
    }

    @Override
    public void destroy() {
        AdLogUtils.i(L_TAG(),"destroy()");
        if (videoADCallback != null) {
            videoADCallback = null;
        }
        if(extra != null){
            extra.clear();
        }
    }

    @Override
    public void addCallback(ADCallback callback) {
        if (callback instanceof VideoADCallback) {
            AdLogUtils.i(L_TAG(),"addCallback()");
            videoADCallback = (VideoADCallback) callback;
        }
    }

    @Override
    public void reload() {
        load();
    }

    @Override
    public final AdConf getCfg() {
        AdConf bd = new AdConf();
        bd.setAppId(appId);
        bd.setAdId(adId);
        bd.setAdRespItem(getAdParamItem());
        return bd;
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
        if (rewardVideoAD != null) {
            AdLogUtils.i(L_TAG(), "show(),开始展示");
            show_status = 1;
            boolean play = true;
            if(sm != null && sm.getMode() == 2){
                play = false;
            }
            if(play) {
                rewardVideoAD.showAd();
            }
        }else{
            show_status = 2;
            Log.e(L_TAG(), "show(),mTTRewardVideoAd is null.");
        }
        if(sm != null){
            sm.setStatus(show_status);
        }
    }

    @Override
    public final Adv_Type getAdvType() {
        return Adv_Type.bxm;
    }

    @Override
    public void onAdLoad() {
        isReady.set(true);
        AdLogUtils.i(L_TAG(),"onAdLoad()");
        if(videoADCallback != null) {
            videoADCallback.onAdPresent(PresentModel.getInstance(adId, getAdvType()).setAdRespItem(getAdParamItem()));
        }

    }

    @Override
    public void onPlayCompleted() {
        AdLogUtils.i(L_TAG(),"onPlayCompleted()");
        if(videoADCallback != null) {
            videoADCallback.onVideoPlayComplete(PresentModel.getInstance(adId, getAdvType()).setAdRespItem(getAdParamItem()));
        }
    }

    @Override
    public void onAdClose() {
        AdLogUtils.i(L_TAG(),"onAdClose()");
        if(videoADCallback != null) {
            videoADCallback.onAdDismissed(DismissModel.newInstance(adId, getAdvType()).setAdRespItem(getAdParamItem()));
        }
    }

    @Override
    public void onReward() {
        AdLogUtils.i(L_TAG(),"onReward()");
        PresentModel pm = PresentModel.getInstance(adId,getAdvType()).setAdRespItem(getAdParamItem());
        if(videoADCallback != null) {
            videoADCallback.onRewardVerify(true, pm);
        }
    }

    @Override
    public void onAdShow() {
        Log.e(L_TAG(), "onAdShow()");
        if (videoADCallback != null) {
            videoADCallback.onADExposed(PresentModel.getInstance(adId, getAdvType()).setAdRespItem(getAdParamItem()));
            videoADCallback.onVideoStartPlay(PresentModel.getInstance(adId, getAdvType()).setAdRespItem(getAdParamItem()));
        }
    }

    @Override
    public void onAdFailed() {
        AdLogUtils.e(L_TAG(),"onAdFailed()");
        if(videoADCallback != null) {
            videoADCallback.onAdFailed(FailModel.toStr(-1, "未知", adId, getAdvType()).setAdRespItem(getAdParamItem()));
        }
    }

    @Override
    public void onAdClicked() {
        AdLogUtils.i(L_TAG(),"onAdClicked()");
        if(videoADCallback != null) {
            videoADCallback.onAdClick(ClickModel.getInstance(1, -1, adId, getAdvType()).setAdRespItem(getAdParamItem()));
        }
    }
}
