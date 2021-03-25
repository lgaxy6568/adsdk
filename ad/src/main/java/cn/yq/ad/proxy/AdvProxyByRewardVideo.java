package cn.yq.ad.proxy;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import cn.yq.ad.ADCallback;
import cn.yq.ad.ADRunnable;
import cn.yq.ad.ADUtils;
import cn.yq.ad.AdConf;
import cn.yq.ad.AdNativeResponse;
import cn.yq.ad.Adv_Type;
import cn.yq.ad.StatCallbackByRewardVideo;
import cn.yq.ad.VideoADCallback;
import cn.yq.ad.impl.ClickModel;
import cn.yq.ad.impl.DismissModel;
import cn.yq.ad.impl.ExtraKey;
import cn.yq.ad.impl.FailModel;
import cn.yq.ad.impl.PresentModel;
import cn.yq.ad.proxy.model.AdConstants;
import cn.yq.ad.proxy.model.AdRespItem;
import cn.yq.ad.proxy.model.AdResponse;
import cn.yq.ad.proxy.model.GetAdsResponse;
import cn.yq.ad.proxy.model.GetAdsResponseListApiResult;
import cn.yq.ad.util.AdGsonUtils;
import cn.yq.ad.util.AdLogUtils;

public final class AdvProxyByRewardVideo extends AdvProxyAbstract implements Runnable {

    private static final String TAG = AdvProxyByRewardVideo.class.getSimpleName();
    private final AtomicBoolean is_time_out = new AtomicBoolean(false);

    private volatile ScheduledExecutorService es;
    private final VideoADCallback callback;
    private final WeakReference<Activity> wrAct;
    private final List<ADRunnable> adRunnableLst;
    private final GetAdsResponseListApiResult result;
    private final Map<String, Object> extra;
    private final String advPos;
    private StatCallbackByRewardVideo statCallback;

    public AdvProxyByRewardVideo setStatCallback(StatCallbackByRewardVideo statCallback) {
        this.statCallback = statCallback;
        return this;
    }

    public AdvProxyByRewardVideo(Activity act, VideoADCallback cb, GetAdsResponseListApiResult result, Map<String, Object> extra,String advPos) {
        callback = cb;
        wrAct = new WeakReference<>(act);
        adRunnableLst = new ArrayList<>();
        this.result = result;
        this.extra = extra;
        this.advPos = advPos;
        initAd();
    }

    private volatile PresentModel present_result_all = null;
    private volatile FailModel err_result_all = null;

    private boolean inited = false;

    List<AdRespItem> adApLst = null;
    private void initAd() {
        List<GetAdsResponse> dataLst = (result != null) ? result.getData() : null;
        if(dataLst == null || dataLst.size() == 0){
            AdLogUtils.e(TAG, "initAd(),dataLst is null");
            return;
        }
        final List<AdRespItem> oldLst = new ArrayList<>();
        for (GetAdsResponse adsResponse : dataLst) {
            String location = adsResponse.getLocation();
            if(!AdConstants.LOCATION_BY_JLSP.equalsIgnoreCase(location)){
                AdLogUtils.e(TAG,"当前配置不是激励视频~,location="+location);
                continue;
            }
            List<AdResponse> ads = adsResponse.getAds();
            if(ads == null || ads.size() == 0){
                continue;
            }
            //60 广告占量
            int probability = adsResponse.getProbability();
            if(probability <= 0){
                continue;
            }
            int maxNum = Math.max(100,probability);
            //取值范围 [1 ~ maxNum]
            int ran = new Random().nextInt(maxNum) + 1;
            if(ran <= probability){
                //合法
            }else{
                //不合法
                continue;
            }
            for (AdResponse ad : ads) {
                List<AdRespItem> tmpLst = ad.toLst();
                if (tmpLst.size() > 0) {
                    AdLogUtils.e(TAG, "initAd(),kp.getApLst.size()="+tmpLst.size());
                    for (AdRespItem item : tmpLst) {
                        //item.setKpSizeType(AdRespItem.KP_1);
                        if(item.getWidget() <= 0){
                            continue;
                        }
                        oldLst.add(item);
                    }
                }
            }
            break;
        }
        AdLogUtils.e(TAG, "initAd(),排序前,oldLst.size()="+oldLst.size());
        for (AdRespItem ap : oldLst) {
            AdLogUtils.e(TAG, "initAd(),(" + ap.getAdId() + "," + ap.getWeight()+",advType="+ap.getAdv_type_name()+",kpSizeType="+ap.getKpSizeType()+",pri="+ap.getPri()+")");
        }
        AdLogUtils.e(TAG, "initAd(),=============================================================");
        List<AdRespItem> apLst = sortLst(oldLst);
        AdLogUtils.e(TAG, "initAd(),排序后,apLst.size()="+apLst.size());
        for (AdRespItem ap : apLst) {
            AdLogUtils.e(TAG, "initAd(),(" + ap.getAdId() + "," + ap.getWeight()+",advType="+ap.getAdv_type_name()+",kpSizeType="+ap.getKpSizeType()+",pri="+ap.getPri()+")");
        }
        AdLogUtils.e(TAG, "initAd(),=============================================================");
        adApLst = new ArrayList<>();
        for (AdRespItem ap : apLst) {
            String app_id = ap.getAppId();
            String tmpIds = ap.getAdId();
            String ad_type = ap.getAdv_type_name();
            ADRunnable ar = null;
            if(AdConfigs.isDebugModel()){
                boolean a = Adv_Type.gdt.name().equalsIgnoreCase(ad_type);   //广点通~全屏
                boolean b = Adv_Type.bai_du.name().equalsIgnoreCase(ad_type);   //百度~全屏
                boolean c = Adv_Type.tt.name().equalsIgnoreCase(ad_type);   //穿山甲~全屏
                boolean d = Adv_Type.api_magic_mobile.name().equalsIgnoreCase(ad_type);   //API~全屏
                if(AdConstants.is_test_gdt_adv()){
                    if(!a){
                        AdLogUtils.e(TAG, "initAd(),跳过_A,appId=" + app_id + ",tmpIds=" + tmpIds + ",weight=" + ap.getWeight());
                        continue;
                    }
                }else if(AdConstants.is_test_tt_adv()){
                    if(!c){
                        AdLogUtils.e(TAG, "initAd(),跳过_B,appId=" + app_id + ",tmpIds=" + tmpIds + ",weight=" + ap.getWeight());
                        continue;
                    }
                }else if(AdConstants.is_test_baidu_adv()){
                    if(!b){
                        AdLogUtils.e(TAG, "initAd(),跳过_C,appId=" + app_id + ",tmpIds=" + tmpIds + ",weight=" + ap.getWeight());
                        continue;
                    }
                }else if(AdConstants.is_test_api_adv()){
                    if(!d){
                        AdLogUtils.e(TAG, "initAd(),跳过_C,appId=" + app_id + ",tmpIds=" + tmpIds + ",weight=" + ap.getWeight());
                        continue;
                    }
                }else{
                    if(a || c || b || d){

                    }else{
                        AdLogUtils.e(TAG, "initAd(),跳过,appId=" + app_id + ",tmpIds=" + tmpIds + ",weight=" + ap.getWeight());
                        continue;
                    }
                }
            }

            if (Adv_Type.tt.name().equalsIgnoreCase(ad_type)) {
                AdLogUtils.e(TAG, "initAd(),穿山甲,appId=" + app_id + ",tmpIds=" + tmpIds + ",weight=" + ap.getWeight());
                try {
                    ar = ADUtils.getTTRewardVideo(wrAct.get(), app_id, tmpIds, extra, null,advPos);
                    if (ar != null) {
                        ADCallbackImpl cb = new ADCallbackImpl(adRunnableLst.size(), Adv_Type.tt);
                        ar.addCallback(cb);

                        Bundle bd = new Bundle();
                        bd.putInt(ExtraKey.KP_AD_REQUEST_TIME_OUT, REQUEST_TIME_OUT_BY_GDT());
                        bd.putString(ExtraKey.KP_AD_CONFIG, AdGsonUtils.getGson().toJson(ap));
                        ar.setExtra(bd);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AdLogUtils.e(TAG, "initAd(),初始化出错~，errMsg=" + e.getMessage());
                }
            } else if (Adv_Type.gdt.name().equalsIgnoreCase(ad_type)) {
                AdLogUtils.e(TAG, "initAd(),广点通,appId=" + app_id + ",tmpIds=" + tmpIds + ",weight=" + ap.getWeight());
                ar = ADUtils.getGDTRewardVideo(wrAct.get(), app_id, tmpIds, extra, null,advPos);
                if (ar != null) {
                    ADCallbackImpl cb = new ADCallbackImpl(adRunnableLst.size(), Adv_Type.gdt);
                    ar.addCallback(cb);

                    Bundle bd = new Bundle();
                    bd.putInt(ExtraKey.KP_AD_REQUEST_TIME_OUT, REQUEST_TIME_OUT_BY_GDT());
                    bd.putString(ExtraKey.KP_AD_CONFIG, AdGsonUtils.getGson().toJson(ap));
                    ar.setExtra(bd);
                }
            } else{
                AdLogUtils.e(TAG, "initAd(),unknown advType="+ad_type);
            }
            if (ar != null) {
                adRunnableLst.add(ar);
                adApLst.add(ap);
            }
        }
        AdLogUtils.e(TAG, "initAd(),apLst.size()=" + apLst.size());

        if (es == null) {
            es = Executors.newScheduledThreadPool(1);
        }
        int sz = adRunnableLst.size();
        inited = sz > 0;
        AdLogUtils.e(TAG, "initAd(),adRunnableLst.size()=" + sz);

    }

    @Override
    public boolean isInited() {
        return inited;
    }

    private final AtomicLong start_load_time = new AtomicLong(0);
    private final AtomicInteger ad_load_index = new AtomicInteger(0);

    @Override
    public final void load() {
        if(adRunnableLst.size() > 0){
            ADRunnable ar = adRunnableLst.get(ad_load_index.get());
            ar.load();
            long load_time_out = REQUEST_TIME_OUT_BY_TOTAL();
            es.schedule(this, load_time_out, TimeUnit.MILLISECONDS);
            start_load_time.set(System.currentTimeMillis());
            AdLogUtils.e(TAG, "load(),开始加载广告,超时时间="+load_time_out+"毫秒");

            String adId = "unknown";
            Adv_Type adType = ar.getAdvType();
            AdConf cf = ar.getCfg();
            if(cf != null ){
                adId = ""+cf.getAdId();
            }
            if(adType == null){
                adType = Adv_Type.none;
            }
            AdRespItem adResp = ar.getCfg().getAdRespItem();
            if(statCallback != null) {
                statCallback.callBackByOnAdStartLoad(adId, adType, adResp);
            }

        }
    }

    @Override
    public final void destroy() {
        super.destroy();
        for (ADRunnable adr : adRunnableLst) {
            try {
                adr.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public final void removeCallBack(ADCallback callback) {
        super.removeCallBack(callback);
        for (ADRunnable adr : adRunnableLst) {
            adr.removeCallBack(callback);
        }
    }

    @Override
    public final void removeAll() {
        super.removeAll();
        for (ADRunnable adr : adRunnableLst) {
            adr.removeAll();
        }
    }

    @Override
    public final Adv_Type getAdvType() {
        return ar_last == null ? Adv_Type.none : ar_last.getAdvType();
    }

    @Override
    public final void show(View view, Object obj) {
        super.show(view, obj);
        if (ar_last == null) {
            return;
        }
        ar_last.show(view, obj);
    }

    @Override
    public final AdNativeResponse getAdvertEntity(String from, Map<String, String> map) {
        if (ar_last == null) {
            return null;
        }
        return ar_last.getAdvertEntity(from, map);
    }

    @Override
    public final View getAdvertEntityView(View view, Object obj) {
        if (ar_last == null) {
            return null;
        }
        return ar_last.getAdvertEntityView(view, obj);
    }

    @Override
    public final List<ADCallback> getCallBackList() {
        if (ar_last == null) {
            return null;
        }
        return ar_last.getCallBackList();
    }

    @Override
    public final void setExtra(Bundle bd) {
        for (ADRunnable adr : adRunnableLst) {
            adr.setExtra(bd);
        }
    }

    @Override
    public final Bundle getExtra() {
        if (ar_last == null) {
            return null;
        }
        return ar_last.getExtra();
    }

    @Override
    public final void reload() {
        for (ADRunnable adr : adRunnableLst) {
            try {
                adr.reload();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public final AdConf getCfg() {
        if (ar_last == null) {
            return null;
        }
        return ar_last.getCfg();
    }

//====================================================

    private final String FROM_RUN = "run";
    @Override
    public final void run() {
        //说明已经有广告返回了
        synchronized (tmp_lock) {
            if (ar_last != null || ab_checked.get()) {
                return;
            }
        }
        checkResult(FROM_RUN);
    }

    private final Object tmp_lock = new Object();
    private volatile ADRunnable ar_last;

    private final AtomicInteger coming_count = new AtomicInteger(0);
    private final AtomicBoolean ab_checked = new AtomicBoolean(false);

    private final void checkResult(final String from) {
        synchronized (tmp_lock) {
            coming_count.incrementAndGet();
            if (isTimeOut()) {
                AdLogUtils.e(TAG, "checkResult(),已经超时了,will return");
                return;
            }
            AdLogUtils.e(TAG, "checkResult(0),from=" + from + ",====================start======================");
            long now = System.currentTimeMillis();
            //1：广告加载完成  2：超时
            int ST_LOAD = 0;
            //说明所有广告加载成功
            if (ar_last != null) {
                ST_LOAD = 1;
            } else {
                long used_time = now - start_load_time.get();
                if (FROM_RUN.equalsIgnoreCase(from)) {
                    ST_LOAD = 2;
                    is_time_out.set(true);
                    AdLogUtils.e(TAG, "checkResult(1),from=" + from + ",请求超时,used_time="+used_time);
                } else {
                    if (hasNextAdRunnable()) {
                        AdLogUtils.e(TAG, "checkResult(2),from=" + from + ",请求未超时，继续等待下一个广告的回调,used_time="+used_time);
                    } else {
                        AdLogUtils.e(TAG, "checkResult(3),from=" + from + ",请求未超时，但没有广告了,used_time"+used_time);
                        ST_LOAD = 3;
                    }
                }
            }
            AdLogUtils.e(TAG, "checkResult(4),from=" + from + ",coming_count=" + coming_count.get() + ",ST_LOAD=" + ST_LOAD);
            if (ST_LOAD > 0) {
                ab_checked.set(true);
                if (ST_LOAD == 1) {
                    if (callback != null) {
                        callback.onAdPresent(present_result_all);
                    }
                } else {  //超时了 或者是没有下一家广告了
                    if (err_result_all == null) {
                        String err_msg = is_time_out.get() ? "加载超时" : "广告SDK还没返回";
                        AdLogUtils.e(TAG, "checkResult(5),err_msg="+err_msg);
                        if (callback != null) {
                            try {
                                AdRespItem runAp = adApLst.get(ad_load_index.get());
                                Adv_Type at = Adv_Type.valueOf(runAp.getAdv_type_name());
                                callback.onAdFailed(FailModel.toStr(-1,err_msg,runAp.getAdId(),at));
                            } catch (Exception e) {
                                AdLogUtils.e(TAG, "checkResult(7),from=" + from + ",回调时出错！");
                            }
                        }
                    } else if (err_result_all != null) {
                        AdLogUtils.e(TAG, "checkResult(6),err_msg="+err_result_all.toFullMsg());
                        if (callback != null) {
                            callback.onAdFailed(err_result_all);
                        }
                    }
                }
            }
            AdLogUtils.e(TAG, "checkResult(0),from=" + from + ",====================end======================");
        }
    }

    public final boolean isTimeOut() {
        return is_time_out.get();
    }

    private class ADCallbackImpl implements VideoADCallback {
        private int runnableIndex;
        private Adv_Type advType;

        public ADCallbackImpl(int runnableIndex, Adv_Type advType) {
            this.runnableIndex = runnableIndex;
            this.advType = advType;
        }

        @Override
        public void onAdPresent(PresentModel result) {
            ar_last = adRunnableLst.get(runnableIndex);
            present_result_all = result;
            checkResult("onAdPresent(" + result.getAdvType() + "_"+result.getAdId()+")");
            if(statCallback != null) {
                statCallback.callBackByOnAdPresent(result);
            }
        }

        @Override
        public void onADExposed(PresentModel pm) {
            AdLogUtils.e(TAG, "onADExposed(" + pm.getAdvType() + "_"+pm.getAdId()+"),曝光成功");
            if(statCallback != null) {
                statCallback.callBackByOnADExposed(pm);
            }
            callback.onADExposed(pm);
        }

        @Override
        public void onAdFailed(FailModel fm) {
            if(statCallback != null) {
                statCallback.callBackByOnAdFailed(fm);
            }
            err_result_all = fm;
            if (isTimeOut()) {
                AdLogUtils.e(TAG, "onAdFailed(" + fm.getAdvType() + "_"+fm.getAdId()+"),已经超时了,msg="+fm.toFullMsg());
            } else {
                if (hasNextAdRunnable()) {
                    int nextIndex = ad_load_index.incrementAndGet();
                    AdLogUtils.e(TAG, "onAdFailed(" + advType.name() + "_"+fm.getAdId()+"),开始加载下一个广告,nextIndex=" + nextIndex);
                    ADRunnable ar = adRunnableLst.get(nextIndex);
                    if(ar != null) {
                        if(statCallback != null) {
                            String adId = "unknown";
                            Adv_Type adType = ar.getAdvType();
                            AdConf cf = ar.getCfg();
                            if(cf != null ){
                                adId = ""+cf.getAdId();
                            }
                            if(adType == null){
                                adType = Adv_Type.none;
                            }
                            AdRespItem adResp = ar.getCfg().getAdRespItem();
                            statCallback.callBackByOnAdStartLoad(adId, adType, adResp);
                        }
                        ar.load();
                    }
                } else {
                    AdLogUtils.e(TAG, "onAdFailed(" + advType.name() + "_"+fm.getAdId()+"),没有下一个广告,ad_load_index=" + ad_load_index.get());
                    checkResult("onAdFailed(" + advType.name() + ")");
                }
            }

        }

        @Override
        public void onAdClick(ClickModel result) {
            AdLogUtils.e(TAG, "onAdClick(),result="+result.getInfo());
            if(statCallback != null) {
                statCallback.callBackByOnAdClick(result);
            }
            callback.onAdClick(result);
        }

        @Override
        public void onAdDismissed(DismissModel dm) {
            AdLogUtils.e(TAG, "onAdDismissed(),result="+dm.getInfo());
            if(statCallback != null) {
                statCallback.callBackByOnAdDismissed(dm);
            }
            callback.onAdDismissed(dm);
        }

        @Override
        public void onDisLike(PresentModel pm) {
            AdLogUtils.e(TAG, "onDisLike(),pm="+pm.getInfo());
            if(statCallback != null) {
                statCallback.callBackByOnDisLike(pm);
            }
            callback.onDisLike(pm);
        }

        @Override
        public void onAdSkip(PresentModel pm) {
            AdLogUtils.e(TAG, "onAdSkip(),pm="+pm.getInfo());
            if(statCallback != null) {
                statCallback.callBackByOnAdSkip(pm);
            }
            callback.onAdSkip(pm);
        }

        @Override
        public void onVideoStartPlay(@NonNull PresentModel model) {
            AdLogUtils.e(TAG, "onVideoStartPlay(),pm="+model.getInfo());
            if(statCallback != null) {
                statCallback.callBackOnVideoStartPlay(model);
            }
            callback.onVideoStartPlay(model);
        }

        @Override
        public void onVideoPlayComplete(@NonNull PresentModel model) {
            AdLogUtils.e(TAG, "onVideoPlayComplete(),pm="+model.getInfo());
            if(statCallback != null) {
                statCallback.callBackOnVideoPlayComplete(model);
            }
            callback.onVideoPlayComplete(model);
        }

        @Override
        public void onRewardVerify(boolean rewardVerify, PresentModel model) {
            AdLogUtils.e(TAG, "onRewardVerify(),pm="+model.getInfo());
            if(statCallback != null) {
                statCallback.callBackOnRewardVerify(rewardVerify, model);
            }
            callback.onRewardVerify(rewardVerify,model);
        }
    }

    private final boolean hasNextAdRunnable() {
        return ad_load_index.get() + 1 <= (adRunnableLst.size() - 1);
    }

    //======================================================================================



}
