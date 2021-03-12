package cn.yq.ad.gdt;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.qq.e.ads.rewardvideo.RewardVideoAD;
import com.qq.e.ads.rewardvideo.RewardVideoADListener;
import com.qq.e.ads.rewardvideo.ServerSideVerificationOptions;
import com.qq.e.comm.util.AdError;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

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
import cn.yq.ad.util.AdGsonUtils;
import cn.yq.ad.util.AdLogUtils;

public class RewardVideoForGDT extends ADBaseImpl implements RewardVideoADListener {

    @Override
    public List<ADCallback> getCallBackList() {
        return null;
    }

    private String L_TAG(){
        return "RewardVideoForGDT_"+adId+"_"+hashCode();
    }

    private Map<String, Object> extra;

    private VideoADCallback videoADCallback;

    private final String appId ;
    private final String adId ;
    private final String advPos ;
    protected WeakReference<Activity> wrAct;
    private RewardVideoAD rewardVideoAD;
    RewardVideoForGDT(Activity activity, String appId, String adId, Map<String, Object> extra, String advPos) {
        this.wrAct = new WeakReference<>(activity);
        this.appId = appId;
        this.adId = adId;
        this.extra = extra;
        this.advPos = advPos;
        Log.e(L_TAG(), "实例创建,appId="+appId+",adId="+adId+",advPos="+advPos);
        boolean volumeOn = false;   //是否开启声音
        rewardVideoAD = new RewardVideoAD(activity, adId, this, volumeOn);
    }
    private Activity getWeakActivity(){
        return wrAct.get();
    }

    @Override
    public void load() {
        if (getWeakActivity() == null || TextUtils.isEmpty(appId) || TextUtils.isEmpty(adId)) {
            Log.e(L_TAG(), "load(),adId is null");
            return;
        }
        if (rewardVideoAD == null) {
            Log.e(L_TAG(), "load(),mTTAdNative is null");
            return;
        }
        if (videoADCallback != null) {
            videoADCallback.onPreLoad();
        }

        ServerSideVerificationOptions options = new ServerSideVerificationOptions.Builder()
                .setCustomData("APP's custom data") // 设置激励视频服务端验证的自定义信息
                .setUserId("APP's user id for server verify") // 设置服务端验证的用户信息
                .build();
        rewardVideoAD.setServerSideVerificationOptions(options);
        rewardVideoAD.loadAD();
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

    @Override
    public void addCallback(ADCallback callback) {
        if (callback instanceof VideoADCallback) {
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
            Log.e(L_TAG(), "show(),开始展示,advPos="+advPos);
            show_status = 1;
            boolean play = true;
            if(sm != null && sm.getMode() == 2){
                play = false;
            }
            if(play) {
                rewardVideoAD.showAD(getWeakActivity());
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
        return Adv_Type.gdt;
    }

    @Override
    public void onADLoad() {
        AdLogUtils.i(L_TAG(),"onADLoad()");
    }

    @Override
    public void onVideoCached() {
        AdLogUtils.i(L_TAG(),"onVideoCached()");
        videoADCallback.onAdPresent(PresentModel.getInstance(adId,getAdvType()).setAdRespItem(getAdParamItem()));
    }

    @Override
    public void onADShow() {
        AdLogUtils.i(L_TAG(),"onADShow()");
    }

    @Override
    public void onADExpose() {
        AdLogUtils.i(L_TAG(),"onADExpose()");
        videoADCallback.onADExposed(PresentModel.getInstance(adId,getAdvType()).setAdRespItem(getAdParamItem()));
    }

    @Override
    public void onReward(Map<String, Object> map) {
        String mapStr = (map != null && map.size() > 0) ? AdGsonUtils.getGson().toJson(map) : "null";
        AdLogUtils.i(L_TAG(),"onReward(),mapStr="+mapStr);
        PresentModel pm = PresentModel.getInstance(adId,getAdvType()).setAdRespItem(getAdParamItem());
        videoADCallback.onRewardVerify(true,pm);
    }

    @Override
    public void onADClick() {
        AdLogUtils.i(L_TAG(),"onADClick()");
        videoADCallback.onAdClick(ClickModel.getInstance(1,-1,adId,getAdvType()).setAdRespItem(getAdParamItem()));
    }

    @Override
    public void onVideoComplete() {
        AdLogUtils.i(L_TAG(),"onVideoComplete()");
        videoADCallback.onVideoPlayComplete(PresentModel.getInstance(adId,getAdvType()).setAdRespItem(getAdParamItem()));
    }

    @Override
    public void onADClose() {
        AdLogUtils.i(L_TAG(),"onADClose()");
        videoADCallback.onAdDismissed(DismissModel.newInstance(adId,getAdvType()).setAdRespItem(getAdParamItem()));
    }

    @Override
    public void onError(AdError adError) {
        AdLogUtils.i(L_TAG(),"onError()");
        videoADCallback.onAdFailed(FailModel.toStr(adError.getErrorCode(),adError.getErrorMsg(),adId,getAdvType()).setAdRespItem(getAdParamItem()));
    }

}
