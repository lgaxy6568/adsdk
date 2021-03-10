package cn.yq.ad.proxy;

import android.app.Activity;
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
import cn.yq.ad.util.AdStringUtils;

public class AdvProxyByKaiPin2 extends AdvProxyByKaiPinAbstract implements Runnable {

    private static final String TAG = AdvProxyByKaiPin2.class.getSimpleName();
    private final AtomicBoolean is_time_out = new AtomicBoolean(false);

    private final ViewGroup adParentContainer;
    private volatile ScheduledExecutorService es;
    private final ADCallback callback;
    private WeakReference<Activity> wrAct;
    private final Map<String, ADRunnable> adRunnableMap;
    private TextView tvSkip;
    private final GetAdsResponseListApiResult result;
    public AdvProxyByKaiPin2(Activity act, ADCallback cb, ViewGroup adContainer, TextView tvSkip,GetAdsResponseListApiResult result) {
        callback = cb;
        wrAct = new WeakReference<>(act);
        this.adParentContainer = adContainer;
        adRunnableMap = new LinkedHashMap<>();
        this.tvSkip = tvSkip;
        this.result = result;
        initAd();
    }

    public Activity getSelfActivity(){
        return wrAct.get();
    }

    private boolean inited = false;

    final Map<String, AdRespItem> adAdvPosMap = new LinkedHashMap<>();
    final Map<String, PresentModel> adPresentModeMap = new LinkedHashMap<>();
    final Map<String, FailModel> adFailModeMap = new LinkedHashMap<>();
    private void initAd() {
        List<GetAdsResponse> dataLst = (result != null) ? result.getData() : null;
        if(dataLst == null || dataLst.size() == 0){
            AdLogUtils.w(TAG, "initAd(),dataLst is null");
            return;
        }
        final List<AdRespItem> apLst = new ArrayList<>();
        for (GetAdsResponse adsResponse : dataLst) {
            String page = adsResponse.getPage();
            if(!AdConstants.PAGE_BY_KAI_PING.equalsIgnoreCase(page)){
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
            for (AdResponse ad : ads) {
                List<AdRespItem> tmpLst = ad.toLst();
                if (tmpLst.size() > 0) {
                    AdLogUtils.d(TAG, "initAd(),partnerKey="+ad.getAdPartnerKey()+",ad.size="+tmpLst.size());
                    for (AdRespItem item : tmpLst) {
                        //item.setKpSizeType(AdRespItem.KP_1);
                        if(item.getWidget() <= 0){
                            AdLogUtils.w(TAG, "initAd(),adType="+item.getAdv_type_name()+",adId="+ad.getAdPartnerAdId()+",ad.weight=0");
                            continue;
                        }
                        apLst.add(item);
                    }
                }
            }
            break;
        }
        AdLogUtils.d(TAG, "initAd(),=============================================================");
//        adApLst = new ArrayList<>();
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
                        ADCallbackImpl cb = new ADCallbackImpl(Adv_Type.tt, PAGE_NAME, ap);
                        ar.addCallback(cb);

                        Bundle bd = new Bundle();
                        bd.putString(ExtraKey.KP_AD_SIZE_TYPE_KEY,ap.getKpSizeTypeDesc());
                        bd.putInt(ExtraKey.KP_AD_REQUEST_TIME_OUT, REQUEST_TIME_OUT_BY_CSJ());
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
                    ADCallbackImpl cb = new ADCallbackImpl(Adv_Type.gdt, PAGE_NAME, ap);
                    ar.addCallback(cb);

                    Bundle bd = new Bundle();
                    bd.putString(ExtraKey.KP_AD_SIZE_TYPE_KEY,ap.getKpSizeTypeDesc());
                    bd.putInt(ExtraKey.KP_AD_REQUEST_TIME_OUT, REQUEST_TIME_OUT_BY_GDT());
                    ar.setExtra(bd);
                }
            } else if (Adv_Type.self.name().equalsIgnoreCase(ad_type)) {
                AdLogUtils.i(TAG, "initAd(),人工配置,appId=" + app_id + ",tmpIds=" + tmpIds + ",weight=" + ap.getWeight()+",sort=" + ap.getSort());
                ar = new AdFactoryImplBySelf().createSplashForSelf(wrAct.get(), app_id, tmpIds, adParentContainer);
                if (ar != null) {
                    ADCallbackImpl cb = new ADCallbackImpl(Adv_Type.self, PAGE_NAME, ap);
                    ar.addCallback(cb);

                    Bundle bd = new Bundle();
                    bd.putString(ExtraKey.KP_AD_SIZE_TYPE_KEY,ap.getKpSizeTypeDesc());
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
//    private final AtomicInteger ad_load_index = new AtomicInteger(0);

    @Override
    public void load() {
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
                    callBackByOnAdStartLoad(adId,adType);
                    //uploadToUmeng(adType,adId, Adv_Status.start,null);
                }
            }
            es.schedule(this, load_time_out, TimeUnit.MILLISECONDS);
            start_load_time.set(System.currentTimeMillis());
        }
    }

    @Override
    public void destroy() {
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
    public void removeCallBack(ADCallback callback) {
        super.removeCallBack(callback);
        for (ADRunnable adr : adRunnableMap.values()) {
            adr.removeCallBack(callback);
        }
    }

    @Override
    public void removeAll() {
        super.removeAll();
        for (ADRunnable adr : adRunnableMap.values()) {
            adr.removeAll();
        }
    }

    @Override
    public Adv_Type getAdvType() {
        return ar_last == null ? Adv_Type.none : ar_last.getAdvType();
    }

    @Override
    public void show(View view, Object obj) {
        super.show(view, obj);
        if (ar_last == null) {
            return;
        }
        ar_last.show(view, obj);
    }

    @Override
    public void click(View view, Object extra) {
        super.click(view, extra);
        if (ar_last == null) {
            return;
        }
        ar_last.click(view, extra);
    }

    @Override
    public AdNativeResponse getAdvertEntity(String from, Map<String, String> map) {
        if (ar_last == null) {
            return null;
        }
        return ar_last.getAdvertEntity(from, map);
    }

    @Override
    public View getAdvertEntityView(View view, Object obj) {
        if (ar_last == null) {
            return null;
        }
        return ar_last.getAdvertEntityView(view, obj);
    }

    @Override
    public List<ADCallback> getCallBackList() {
        if (ar_last == null) {
            return null;
        }
        return ar_last.getCallBackList();
    }

    @Override
    public void setExtra(Bundle bd) {
        for (ADRunnable adr : adRunnableMap.values()) {
            adr.setExtra(bd);
        }
    }

    @Override
    public Bundle getExtra() {
        if (ar_last == null) {
            return null;
        }
        return ar_last.getExtra();
    }

    @Override
    public void reload() {
        for (ADRunnable adr : adRunnableMap.values()) {
            try {
                adr.reload();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public AdConf getCfg() {
        if (ar_last == null) {
            return null;
        }
        return ar_last.getCfg();
    }

    //====================================================

    private final String FROM_RUN = "run";
    @Override
    public void run() {
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
    private volatile List<ADRunnable> arLoadSucLst;

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
                            continue;
                        }
                        tmpLst.add(ap);
                    }
                    AdLogUtils.d(TAG, "checkResult(6_A),from=" + from + ",c1="+c1+",c2="+c2+",tmpLst="+ AdGsonUtils.getGson().toJson(tmpLst));
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

    public boolean isTimeOut() {
        return is_time_out.get();
    }

    private class ADCallbackImpl implements ADCallback {
        private Adv_Type advType;
        private String pageName;
        private final AdRespItem ap;

        private final AtomicBoolean AB_IS_GDT_FULLSCREEN;
        public ADCallbackImpl(Adv_Type advType, String pageName, AdRespItem ap) {
            this.advType = advType;
            this.pageName = pageName;
            this.ap = ap;
            AB_IS_GDT_FULLSCREEN = new AtomicBoolean(advType == Adv_Type.gdt);
        }

        @Override
        public void onAdPresent(PresentModel result) {
            adPresentModeMap.put(ap.getAdId(),result);
            checkResult("onAdPresent(" + result.getAdvType() + "_"+result.getAdId()+")");
            callBackByOnAdPresent(result);
//            Map<String, Object> tmp = SFHelper.INSTANCE.createMap(result.getAdId(),result.getAdv_Type(),true,null);
//
//            if(extraMap != null && extraMap.size() > 0) {
//                tmp.putAll(extraMap);
//            }
//            GuideActivity.Companion.tj_load_status_for_all(TARGET_NAME, StatActionType.access,pageName, true, tmp);
        }

        @Override
        public void onADExposed(PresentModel pm) {
            AdLogUtils.i(TAG, "onADExposed(" + pm.getAdvType() + "_"+pm.getAdId()+"),曝光成功");
            callback.onADExposed(pm);
//            Map<String, Object> tmp = SFHelper.INSTANCE.createMap(pm.getAdId(),pm.getAdv_Type(),true,null);
//            if(extraMap != null && extraMap.size() > 0) {
//                tmp.putAll(extraMap);
//            }
//            GuideActivity.Companion.tj_load_status_for_all(TARGET_NAME, StatActionType.view,pageName, true,tmp);
//            if(AB_IS_GDT_FULLSCREEN.get()){
//                addKaiPinStatus(ap.getAdId(),new KaiPinStatus(2));
//            }
            callBackByOnADExposed(pm);
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
//            Map<String, Object> tmp = SFHelper.INSTANCE.createMap(fm.getAdId(),fm.getAdv_Type(),false,fm.toFullMsg());
//            if(extraMap != null && extraMap.size() > 0) {
//               tmp.putAll(extraMap);
//            }
//            GuideActivity.Companion.tj_load_status_for_all(TARGET_NAME,StatActionType.access,pageName, false, tmp);
//            if(AB_IS_GDT_FULLSCREEN.get()){
//                addKaiPinStatus(ap.getAdId(),new KaiPinStatus(3));
//            }
            callBackByOnAdFailed(fm);
        }

        @Override
        public void onAdClick(ClickModel result) {
//            if(result != null) {
//                uploadToUmeng(result.getAdv_Type(), result.getAdId(),Adv_Status.click,null);
//            }
//            Map<String, Object> tmp = SFHelper.INSTANCE.createMap(result.getAdId(),result.getAdv_Type(),true,null);
//            if(extraMap != null && extraMap.size() > 0) {
//                tmp.putAll(extraMap);
//            }
//            GuideActivity.Companion.tj_load_status_for_all(TARGET_NAME, StatActionType.click,pageName, true, tmp);
            callback.onAdClick(result);
            callBackByOnAdClick(result);
        }

        @Override
        public void onAdDismissed(DismissModel dm) {
            callback.onAdDismissed(dm);
            callBackByOnAdDismissed(dm);
        }

        @Override
        public void onDisLike(PresentModel pm) {
            callback.onDisLike(pm);
            callBackByOnDisLike(pm);
        }

        @Override
        public void onAdSkip(PresentModel result) {
//            Map<String, Object> tmp = SFHelper.INSTANCE.createMap(result.getAdId(),result.getAdv_Type(),true,null);
//            if(extraMap != null && extraMap.size() > 0) {
//                tmp.putAll(extraMap);
//            }
//            GuideActivity.Companion.tj_load_status_for_all(TARGET_NAME, StatActionType.click,pageName, true, tmp);
            callback.onAdSkip(result);
            callBackByOnAdSkip(result);
        }
    }


    public void callBackByOnAdStartLoad(String adId,Adv_Type adType){

    }

    public void callBackByOnAdPresent(PresentModel pm){

    }

    public void callBackByOnAdFailed(FailModel fm){

    }

    public void callBackByOnADExposed(PresentModel pm){

    }

    public void callBackByOnAdClick(ClickModel cm){

    }

    public void callBackByOnAdDismissed(DismissModel dm){

    }

    public void callBackByOnAdSkip(PresentModel pm){

    }

    public void callBackByOnDisLike(PresentModel pm){

    }
}
