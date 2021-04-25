package cn.yq.ad.proxy;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
import cn.yq.ad.StatCallbackByKaiPing;
import cn.yq.ad.impl.ClickModel;
import cn.yq.ad.impl.DismissModel;
import cn.yq.ad.impl.ExtraKey;
import cn.yq.ad.impl.FailModel;
import cn.yq.ad.impl.PresentModel;
import cn.yq.ad.proxy.model.AdConstants;
import cn.yq.ad.proxy.model.AdRespItem;
import cn.yq.ad.proxy.model.AdResponse;
import cn.yq.ad.proxy.model.ExtraParams;
import cn.yq.ad.proxy.model.GetAdsResponse;
import cn.yq.ad.proxy.model.GetAdsResponseListApiResult;
import cn.yq.ad.self.AdFactoryImplBySelf;
import cn.yq.ad.util.AdGsonUtils;
import cn.yq.ad.util.AdLogUtils;
import cn.yq.ad.util.AdStringUtils;

public final class AdvProxyByKaiPing extends AdvProxyAbstract implements Runnable {

    private static final String TAG = AdvProxyByKaiPing.class.getSimpleName();
    private final AtomicBoolean is_time_out = new AtomicBoolean(false);

    private final ViewGroup adParentContainer;
    private ScheduledExecutorService es;
    private final ADCallback callback;
    private WeakReference<Activity> wrAct;
    private final Map<String, ADRunnable> adRunnableMap;
    private final TextView tvSkip;
    private final GetAdsResponseListApiResult result;
    private StatCallbackByKaiPing statCallback;
    private final ExtraParams extraParams;
    public AdvProxyByKaiPing setStatCallback(StatCallbackByKaiPing statCallback) {
        this.statCallback = statCallback;
        return this;
    }

    public AdvProxyByKaiPing(Activity act, ADCallback cb, ViewGroup adContainer, TextView tvSkip, GetAdsResponseListApiResult result,ExtraParams ep) {
        this.callback = cb;
        this.wrAct = new WeakReference<>(act);
        this.adParentContainer = adContainer;
        this.adRunnableMap = new LinkedHashMap<>();
        this.tvSkip = tvSkip;
        this.result = result;
        this.extraParams = ep;
        initAd();
    }

    public final Activity getSelfActivity(){
        return wrAct.get();
    }

    private boolean inited = false;

    private final Map<String, AdRespItem> adAdvPosMap = new LinkedHashMap<>();
    private final Map<String, PresentModel> adPresentModeMap = new LinkedHashMap<>();
    private final Map<String, FailModel> adFailModeMap = new LinkedHashMap<>();
    private void initAd() {
        List<GetAdsResponse> dataLst = (result != null) ? result.getData() : null;
        if(dataLst == null || dataLst.size() == 0){
            AdLogUtils.w(TAG, "initAd(),dataLst is null");
            return;
        }
        final List<AdRespItem> apLst = new ArrayList<>();
        for (GetAdsResponse adsResponse : dataLst) {
            String location = adsResponse.getLocation();
            if(!AdConstants.LOCATION_BY_KAI_PING.equalsIgnoreCase(location)){
                continue;
            }
            List<AdResponse> ads = adsResponse.getAds();
            if(ads == null || ads.size() == 0){
                AdLogUtils.e(TAG, "initAd(),ads is empty");
                continue;
            }
            //60 广告占量
            int probability = adsResponse.getProbability();
            if(probability <= 0){
                AdLogUtils.e(TAG, "initAd(),probability=["+probability+"] < 0");
                continue;
            }
            int maxNum = Math.max(100,probability);
            //取值范围 [1 ~ maxNum]
            int ran = new Random().nextInt(maxNum) + 1;
            if(ran <= probability){
                //合法
            }else{
                AdLogUtils.e(TAG, "initAd(),probability=["+probability+"],ran="+ran+",本次不展示");
                //不合法
                continue;
            }
            final boolean isVip = extraParams != null && extraParams.isVip();
            for (AdResponse ad : ads) {
                if(ad == null){
                    continue;
                }
                if(ad.getWidget() <= 0){
                    continue;
                }
                final String adType = (""+ad.getType()).trim();
                //VIP用户跳过API广告及SDK广告
                if(isVip){
                    if(AdConstants.SDK_TYPE_BY_SDK.equalsIgnoreCase(adType)){
                        continue;
                    }
                    if(AdConstants.SDK_TYPE_BY_API.equalsIgnoreCase(adType)){
                        continue;
                    }
                }
                List<AdRespItem> tmpLst = ad.toLst();
                if (tmpLst.size() > 0) {
                    AdLogUtils.d(TAG, "initAd(),partnerKey="+ad.getAdPartnerKey()+",ad.size="+tmpLst.size());
                    for (AdRespItem item : tmpLst) {
                        if(item.getWidget() <= 0){
                            AdLogUtils.w(TAG, "initAd(),adType="+item.getAdv_type_name()+",adId="+ad.getAdPartnerAdId()+",ad.weight=0");
                            continue;
                        }
                        //DeepLink唤醒，如果未安装则跳过此广告
                        if(AdConstants.SDK_TYPE_BY_SELF.equalsIgnoreCase(adType)){
                            final String url = item.getUrl();
                            if(AdStringUtils.isNotEmpty(url)){
                                Uri uri = Uri.parse(url);
                                String action = uri.getQueryParameter("action");
                                if("OtherApp".equalsIgnoreCase(action)){
                                    String pkg = uri.getQueryParameter("pkg");
                                    //没有安装的则跳过
                                    if(!ADUtils.isAppInstalled(pkg,wrAct.get())){
                                        AdLogUtils.w(TAG, "initAd(),未安装["+pkg+"],adType="+item.getAdv_type_name()+",adId="+ad.getAdPartnerAdId());
                                        continue;
                                    }
                                }
                            }
                        }
                        apLst.add(item);
                    }
                }
            }
            break;
        }
        AdLogUtils.d(TAG, "initAd(),=============================================================");
        adAdvPosMap.clear();
        for (AdRespItem ap : apLst) {
            String app_id = ap.getAppId();
            String tmpIds = ap.getAdId();
            String ad_type = ap.getAdv_type_name();
            ADRunnable ar = null;
            if(ap.isNotValid()){
                AdLogUtils.e(TAG,"当前广告无效~");
                continue;
            }
            if(AdConfigs.isDebugModel()){
                boolean a = Adv_Type.gdt.name().equalsIgnoreCase(ad_type);   //广点通~全屏
                boolean b = Adv_Type.bai_du.name().equalsIgnoreCase(ad_type);   //百度~全屏
                boolean c = Adv_Type.tt.name().equalsIgnoreCase(ad_type);   //穿山甲~全屏
                boolean d = Adv_Type.api_magic_mobile.name().equalsIgnoreCase(ad_type);   //API~全屏
                boolean e = Adv_Type.self.name().equalsIgnoreCase(ad_type);   //运营配置~全屏
                if(AdConstants.is_test_gdt_adv()){
                    if(!a){
                        AdLogUtils.d(TAG, "initAd(),跳过_A,appId=" + app_id + ",tmpIds=" + tmpIds + ",weight=" + ap.getWeight()+",sort="+ap.getSort());
                        continue;
                    }
                }else if(AdConstants.is_test_tt_adv()){
                    if(!c){
                        AdLogUtils.d(TAG, "initAd(),跳过_B,appId=" + app_id + ",tmpIds=" + tmpIds + ",weight=" + ap.getWeight()+",sort="+ap.getSort());
                        continue;
                    }
                }else if(AdConstants.is_test_baidu_adv()){
                    if(!b){
                        AdLogUtils.d(TAG, "initAd(),跳过_C,appId=" + app_id + ",tmpIds=" + tmpIds + ",weight=" + ap.getWeight()+",sort="+ap.getSort());
                        continue;
                    }
                }else if(AdConstants.is_test_api_adv()){
                    if(!d){
                        AdLogUtils.d(TAG, "initAd(),跳过_C,appId=" + app_id + ",tmpIds=" + tmpIds + ",weight=" + ap.getWeight()+",sort="+ap.getSort());
                        continue;
                    }
                }else{
                    if(a || c || b || d || e){

                    }else{
                        AdLogUtils.d(TAG, "initAd(),跳过,appId=" + app_id + ",tmpIds=" + tmpIds + ",weight=" + ap.getWeight()+",sort="+ap.getSort());
                        continue;
                    }
                }
            }

            if (Adv_Type.tt.name().equalsIgnoreCase(ad_type)) {
                AdLogUtils.i(TAG, "initAd(),穿山甲,appId=" + app_id + ",tmpIds=" + tmpIds + ",weight=" + ap.getWeight()+",sort=" + ap.getSort());
                try {
                    ar = ADUtils.getSplashADForTT(wrAct.get(), app_id, tmpIds, adParentContainer, null);
                    if (ar != null) {
                        ADCallbackImpl cb = new ADCallbackImpl(Adv_Type.tt, ap);
                        ar.addCallback(cb);

                        Bundle bd = new Bundle();
                        bd.putInt(ExtraKey.KP_AD_REQUEST_TIME_OUT, REQUEST_TIME_OUT_BY_CSJ());
                        bd.putString(ExtraKey.KP_AD_CONFIG, AdGsonUtils.getGson().toJson(ap));
                        ar.setExtra(bd);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AdLogUtils.e(TAG, "initAd(),初始化出错~，errMsg=" + e.getMessage());
                }
            } else if (Adv_Type.gdt.name().equalsIgnoreCase(ad_type)) {
                AdLogUtils.i(TAG, "initAd(),广点通,appId=" + app_id + ",tmpIds=" + tmpIds + ",weight=" + ap.getWeight()+",sort=" + ap.getSort());
                ar = ADUtils.getSplashADForGDT(wrAct.get(), app_id, tmpIds, adParentContainer, null,tvSkip);
                if (ar != null) {
                    ADCallbackImpl cb = new ADCallbackImpl(Adv_Type.gdt,  ap);
                    ar.addCallback(cb);

                    Bundle bd = new Bundle();
                    bd.putInt(ExtraKey.KP_AD_REQUEST_TIME_OUT, REQUEST_TIME_OUT_BY_GDT());
                    bd.putString(ExtraKey.KP_AD_CONFIG, AdGsonUtils.getGson().toJson(ap));
                    ar.setExtra(bd);
                }
            } else if (Adv_Type.self.name().equalsIgnoreCase(ad_type)) {
                AdLogUtils.i(TAG, "initAd(),人工配置,appId=" + app_id + ",tmpIds=" + tmpIds + ",weight=" + ap.getWeight()+",sort=" + ap.getSort());
                ar = new AdFactoryImplBySelf().createSplashForSelf(wrAct.get(), app_id, tmpIds, adParentContainer);
                if (ar != null) {
                    ADCallbackImpl cb = new ADCallbackImpl(Adv_Type.self, ap);
                    ar.addCallback(cb);

                    Bundle bd = new Bundle();
                    bd.putInt(ExtraKey.KP_AD_REQUEST_TIME_OUT, REQUEST_TIME_OUT_BY_GDT());
                    bd.putString(ExtraKey.KP_AD_CONFIG, AdGsonUtils.getGson().toJson(ap));
                    ar.setExtra(bd);
                }
            }else{
                AdLogUtils.e(TAG, "initAd(),unknown advType="+ad_type);
            }
            if (ar != null) {
                String adId = ap.getAdId();
                adRunnableMap.put(adId,ar);
                adAdvPosMap.put(adId,ap);
            }
        }
        AdLogUtils.i(TAG, "initAd(),apLst.size()=" + apLst.size());

        if (es == null) {
            es = Executors.newScheduledThreadPool(1);
        }
        int sz = adRunnableMap.size();
        inited = sz > 0;
        AdLogUtils.i(TAG, "initAd(),adRunnableLst.size()=" + sz);

    }

    @Override
    public boolean isInited() {
        return inited;
    }

    private final AtomicLong start_load_time = new AtomicLong(0);

    @Override
    public final void load() {
        int sz = adRunnableMap.size();
        final long load_time_out = REQUEST_TIME_OUT_BY_TOTAL();
        if(sz > 0){
            for (ADRunnable ar : adRunnableMap.values()){
                if(ar == null) {
                    continue;
                }
                try {
                    AdLogUtils.i(TAG, "load(),开始加载["+ar.getAdvType()+"]广告,总超时时间="+load_time_out/1000+"秒");
                    ar.load();
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    String adId = "unknown";
                    Adv_Type adType = ar.getAdvType();
                    AdConf cf = ar.getCfg();
                    if(cf != null ){
                        adId = ""+cf.getAdId();
                    }
                    if(adType == null){
                        adType = Adv_Type.none;
                    }
                    if(statCallback != null) {
                        statCallback.callBackByOnAdStartLoad(adId, adType,ar.getCfg().getAdRespItem());
                    }
                }
            }
            es.schedule(this, load_time_out, TimeUnit.MILLISECONDS);
            start_load_time.set(System.currentTimeMillis());
        }
    }

    @Override
    public final void destroy() {
        super.destroy();
        for (ADRunnable adr : adRunnableMap.values()) {
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
        for (ADRunnable adr : adRunnableMap.values()) {
            adr.removeCallBack(callback);
        }
    }

    @Override
    public final void removeAll() {
        super.removeAll();
        for (ADRunnable adr : adRunnableMap.values()) {
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
        for (ADRunnable adr : adRunnableMap.values()) {
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
        for (ADRunnable adr : adRunnableMap.values()) {
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
    private volatile ADRunnable ar_last = null;

    private final AtomicInteger coming_count = new AtomicInteger(0);
    private final AtomicBoolean ab_checked = new AtomicBoolean(false);

    private void checkResult(final String from) {
        synchronized (tmp_lock) {
            coming_count.incrementAndGet();
            final int checked_count = coming_count.get();
            AdLogUtils.d(TAG, "checkResult(1),from="+from+",====================start======================,checked_count="+checked_count);
            if (callback == null) {
                AdLogUtils.d(TAG, "checkResult(2),callback is null,checked_count="+checked_count);
                return;
            }
            if (isTimeOut()) {
                AdLogUtils.d(TAG, "checkResult(3),已经超时了,will return,checked_count="+checked_count);
                return;
            }
            final int suc_size = adPresentModeMap.size();
            final int fai_size = adFailModeMap.size();
            final int ad_runnable_size = adRunnableMap.size();
            if (FROM_RUN.equalsIgnoreCase(from)) {
                long now = System.currentTimeMillis();
                long used_time = now - start_load_time.get();
                is_time_out.set(suc_size == 0);
                AdLogUtils.d(TAG, "checkResult(4_B),from=" + from + ",请求超时了,total_size=" + ad_runnable_size + ",suc_size=" + suc_size + ",fai_size=" + fai_size + ",checked_count=" + checked_count);
            }else {
                AdLogUtils.d(TAG, "checkResult(4_A),from=" + from + ",total_size=" + ad_runnable_size + ",suc_size=" + suc_size + ",fai_size=" + fai_size + ",checked_count=" + checked_count);
            }

            if(isTimeOut()){
                try {
                    AdRespItem runAp = adAdvPosMap.values().iterator().next();
                    Adv_Type at = Adv_Type.valueOf(runAp.getAdv_type_name());
                    callback.onAdFailed(FailModel.toStr(-1,"加载超时_1",runAp.getAdId(),at));
                    AdLogUtils.e(TAG, "checkResult(5_A),from=" + from + ",回调超时~已完成");
                } catch (Exception e) {
                    AdLogUtils.e(TAG, "checkResult(5_B),from=" + from + ",回调超时~出错！");
                }finally {
                    adParentContainer.removeAllViews();
                }
                return;
            }
            final boolean c1 = suc_size + fai_size >= ad_runnable_size ;
            final boolean c2 = FROM_RUN.equalsIgnoreCase(from) && suc_size > 0 ;
            if (ad_runnable_size > 0 && (c1 || c2)) {
                ab_checked.set(true);
                if (suc_size > 0) {
                    List<AdRespItem> tmpLst = new ArrayList<>();
                    for (PresentModel pm : adPresentModeMap.values()) {
                        AdRespItem ap = adAdvPosMap.get(pm.getAdId());
                        if(ap == null){
                            AdLogUtils.w(TAG,"ap is null,adId="+pm.getAdId());
                            continue;
                        }
                        tmpLst.add(ap);
                    }
                    if(AdConfigs.isDebugModel()) {
                        AdLogUtils.d(TAG, "checkResult(6_A),from=" + from + ",c1=" + c1 + ",c2=" + c2 + ",adPresentModeMap.size()=" + adPresentModeMap.size() + ",adAdvPosMap.size()=" + adAdvPosMap.size() + ",tmpLst=" + AdGsonUtils.getGson().toJson(tmpLst));
                    }
                    AdRespItem selectedAp = selectApBySortType(tmpLst);
                    final String sel_ad_id = selectedAp != null ? selectedAp.getAdId() : null;
                    final String sel_adv_type = selectedAp != null ? selectedAp.getAdv_type_name() : Adv_Type.none.name();
                    PresentModel pm = AdStringUtils.isNotEmpty(sel_ad_id) ? adPresentModeMap.get(sel_ad_id) : null;
                    if(pm != null) {
                        AdLogUtils.i(TAG, "checkResult(6_A_1),from=" + from + ",tmpLst.size()="+tmpLst.size()+",sel_ad_id="+sel_ad_id+",sel_adv_type="+sel_adv_type);
                        callback.onAdPresent(pm);
                        //切换到主线程去显示广告
                        AsyncTask.getTaskHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                ar_last = adRunnableMap.get(sel_ad_id);
                                if(ar_last != null) {
                                    if(statCallback != null){
                                        statCallback.callBackByOnAdAttachToWindow(pm);
                                    }
                                    ar_last.show(null, null);
                                }
                            }
                        });
                    }else{
                        AdLogUtils.d(TAG, "checkResult(6_A_2),from=" + from + ",tmpLst.size()="+tmpLst.size()+",sel_ad_id="+sel_ad_id+",selectedPresentMode is null");
                    }
                } else {
                    int sz = adFailModeMap.size();
                    FailModel fm;
                    if(sz > 0) {
                        fm = adFailModeMap.values().iterator().next();
                    }else{
                        AdRespItem runAp = adAdvPosMap.values().iterator().next();
                        Adv_Type at = Adv_Type.valueOf(runAp.getAdv_type_name());
                        fm = FailModel.toStr(-1,"加载超时_2",runAp.getAdId(),at);
                    }
                    AdLogUtils.d(TAG, "checkResult(6_B),from=" + from + ",adId=" + fm.getAdId()+",adv_type="+fm.getAdvType()+",err_code="+fm.getCode()+",err_msg="+fm.getMsg());
                    callback.onAdFailed(fm);
                }
            }else{
                AdLogUtils.d(TAG, "checkResult(7),from=" + from + ",未超时，等待下一个广告的返回");
            }
            AdLogUtils.d(TAG, "checkResult(8),from=" + from + ",====================end======================");
        }
    }

    public final boolean isTimeOut() {
        return is_time_out.get();
    }

    private final class ADCallbackImpl implements ADCallback {
        private Adv_Type advType;
        private final AdRespItem ap;

        public ADCallbackImpl(Adv_Type advType, AdRespItem ap) {
            this.advType = advType;
            this.ap = ap;
        }

        @Override
        public void onAdPresent(PresentModel result) {
            adPresentModeMap.put(ap.getAdId(),result);
            checkResult("onAdPresent(" + result.getAdvType() + "_"+result.getAdId()+")");
            if(statCallback != null) {
                statCallback.callBackByOnAdPresent(result);
            }
        }

        @Override
        public void onADExposed(PresentModel pm) {
            AdLogUtils.i(TAG, "onADExposed(" + pm.getAdvType() + "_"+pm.getAdId()+"),曝光成功");
            callback.onADExposed(pm);
            if(statCallback != null) {
                statCallback.callBackByOnADExposed(pm);
            }
        }

        @Override
        public void onAdFailed(FailModel fm) {
            adFailModeMap.put(ap.getAdId(),fm);
            if (isTimeOut()) {
                AdLogUtils.e(TAG, "onAdFailed(" + fm.getAdvType() + "_"+fm.getAdId()+"),已经超时了,msg="+fm.toFullMsg());
            } else {
                AdLogUtils.e(TAG, "onAdFailed(" + advType.name() + "_"+fm.getAdId()+"),msg="+fm.toFullMsg());
                checkResult("onAdFailed(" + advType.name() + ")");
            }
            if(statCallback != null) {
                statCallback.callBackByOnAdFailed(fm);
            }
        }

        @Override
        public void onAdClick(ClickModel result) {
            callback.onAdClick(result);
            if(statCallback != null) {
                statCallback.callBackByOnAdClick(result);
            }
        }

        @Override
        public void onAdDismissed(DismissModel dm) {
            callback.onAdDismissed(dm);
            if(statCallback != null) {
                statCallback.callBackByOnAdDismissed(dm);
            }
        }

        @Override
        public void onDisLike(PresentModel pm) {
            callback.onDisLike(pm);
            if(statCallback != null) {
                statCallback.callBackByOnDisLike(pm);
            }
        }

        @Override
        public void onAdSkip(PresentModel result) {
            callback.onAdSkip(result);
            if(statCallback != null) {
                statCallback.callBackByOnAdSkip(result);
            }
        }
    }


}
