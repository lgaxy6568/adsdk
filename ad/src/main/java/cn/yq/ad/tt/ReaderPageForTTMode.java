package cn.yq.ad.tt;


import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.FilterWord;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import cn.yq.ad.ADStyle;
import cn.yq.ad.Adv_Type;
import cn.yq.ad.AdNativeResponse;
import cn.yq.ad.ShowModel;
import cn.yq.ad.impl.ClickModel;
import cn.yq.ad.impl.FailModel;
import cn.yq.ad.impl.PresentModel;
import cn.yq.ad.tt.config.TTUtil;
import cn.yq.ad.tt.utils.TToast;
import cn.yq.ad.util.AdSize;
import cn.yq.ad.util.SizeUtil;

/**
 * 内容_插屏
 */
public class ReaderPageForTTMode extends ADBaseImplByTT<TTNativeExpressAd> implements TTAdNative.NativeExpressAdListener {

    protected String getTAG(){
        return ReaderPageForTTMode.class.getSimpleName();
    }
    protected int getImgWidth() {
        return request_width;
    }

    protected int getImgHeight() {
        return request_height;
    }
    private WeakReference<ViewGroup> mAdContainerRef;
    private int request_width;
    private final int request_height = 0;
    protected final Map<String, View> view_map = new LinkedHashMap<>();
    //====================
    ReaderPageForTTMode(Activity act, String appId, String adId) {
        super(act, appId, adId);
        Log.e(getTAG(),"构造方法(),appId="+appId+",adId="+adId);
        try {
            AdSize sz = SizeUtil.getScreenSize(act);
            request_width = SizeUtil.px2dip(act,sz.getWidth());
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(request_width == 0){
                request_width = 360 ;
            }
        }
        request_width -= (20 + 20);
    }

    @Override
    public final AdNativeResponse getAdvertEntity(String from, Map<String, String> map) {
        synchronized (mQueue){
            final int sz = mQueue.size();
            if (sz <= 0) {
                Log.e(getTAG(), "getAdvertEntity("+from+"),mQueue.size() == 0,将要重新加载");
                startRequest("getAdvertEntity("+from+")_A");
                return null;
            }
            AdNativeResponse resp ;
            resp = mQueue.poll();
            final int freeSize = mQueue.size();
            String tmp_msg = resp != null ? "返回成功" : "返回失败";
            Log.e(getTAG(), "getAdvertEntity("+from+"),"+tmp_msg+",剩于广告条数="+freeSize );
            return resp;
        }
    }

    @Override
    public View getAdvertEntityView(View view, Object obj) {
        if(!(obj instanceof AdNativeResponse)){
            Log.e(getTAG(), "getAdvertEntityView(),obj="+obj);
            return null;
        }
        final AdNativeResponse nar = (AdNativeResponse)obj;
        TTNativeExpressAd ad = mNativeResponses.get(nar.getImageUrl());
        if(ad == null){
            Log.e(getTAG(), "getAdvertEntityView(),ad is null");
            return null;
        }
        ViewGroup vv = (ViewGroup) LayoutInflater.from(getAct()).inflate(R.layout.layout_adv_for_tt_pge_mode,null);

        Log.e(getTAG(), "getAdvertEntityView()");
        return vv;
    }

    @Override
    public void showTj(ShowModel sm) {
        super.showTj(sm);
        boolean tj_upload = false;
        if(show_view != null && show_ad != null) {
            ViewGroup mAdContainer = getAdContainer();
            if(mAdContainer != null) {
                mAdContainer.removeAllViews();
                mAdContainer.addView(show_view);
            }
            tj_upload = true;
            removeFirstKey();
        }
        Log.e(getTAG(), "showTj(),tj_upload="+tj_upload);
    }


    private View show_view;
    private TTNativeExpressAd show_ad;
    @Override
    public void show(View vv, Object obj) {
        Log.e(getTAG(), "show()");
        if (obj instanceof AdNativeResponse) {
            AdNativeResponse tmp_nar = (AdNativeResponse)obj;
            TTNativeExpressAd ad = mNativeResponses.get(tmp_nar.getImageUrl());
            if (ad == null) {
                Log.e(getTAG(),"show(),ad is null");
                return;
            }
            View view = view_map.get(String.valueOf(ad.hashCode()));
            if(view == null){
                Log.e(getTAG(),"show(),view is null");
                return;
            }
//            show_view = view;
//            show_ad = ad;
            this.mAdContainerRef = new WeakReference<>((ViewGroup)vv);
            ViewGroup mAdContainer = getAdContainer();
            if(mAdContainer != null) {
                mAdContainer.removeAllViews();
                mAdContainer.addView(view);
            }
            removeFirstKey();
        }
    }

    private ViewGroup getAdContainer(){
        return mAdContainerRef != null ? mAdContainerRef.get() : null;
    }

    private void removeFirstKey(){
        Set<String> keySet = mNativeResponses.keySet();
        if(keySet.size() >= 5){
            List<String> keyLst = new ArrayList<>(keySet);
           TTNativeExpressAd ad =  mNativeResponses.remove(keyLst.get(0));
           if(ad != null ){
               ad.destroy();
           }
        }

        Set<String> key_view_set = view_map.keySet();
        if(key_view_set.size() >= 5){
            List<String> keyLst = new ArrayList<>(key_view_set);
            view_map.remove(keyLst.get(0));
        }
    }

    @Override
    public void click(View view, Object obj) {
    }

    @Override
    public void load() {
        startRequest("load()");
    }

    @Override
    public void reload() {
        startRequest("reload()");
    }

    @Override
    public void destroy() {
        mNativeResponses.clear();
        while (true){
            AdNativeResponse nar = mQueue.poll();
            if(nar == null){
                break;
            }
            nar.destroy();
        }
        for (TTNativeExpressAd ad : mNativeResponses.values()){
            if(ad == null){
                continue;
            }
            try {
                ad.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mQueue.clear();
        view_map.clear();
        super.destroy();
    }

    private final Object ad_obj_lock = new Object();
    private final AtomicLong LOAD_INDEX = new AtomicLong(0);
    private final AtomicBoolean ab_is_loading = new AtomicBoolean(false);
    private final AtomicLong ab_last_load_time = new AtomicLong(0);
    private void logMethodCallStacktrace(String tag){
            RuntimeException re = new RuntimeException("here");
            re.fillInStackTrace();
            Log.e(tag,"Called:",re);
    }
    private void startRequest(String from) {
//        if(from.contains("load")){
//            logMethodCallStacktrace(getTAG());
//        }
        if(ab_is_loading.get()){
            long now = System.currentTimeMillis();
            long last_time = ab_last_load_time.get();
            if(last_time > 0 && (now - last_time) >= 1000 * 50){
                Log.e(getTAG(), "==========startRequest(),from="+from+",加载中,但已经超50s还没返回广告，所以可以继续去加载新的广告");
            }else {
                Log.e(getTAG(), "==========startRequest(),from="+from+",加载中,will return");
                return;
            }
        }
        synchronized (ad_obj_lock){
            LOAD_INDEX.incrementAndGet();
            Log.e(getTAG(), "==========startRequest(),index=" + LOAD_INDEX.get() + ",from=" + from + ",mainId=" + adId+",width="+request_width+",height="+request_height);
            TTAdManager tam = TTUtil.get().getAdManager();
            tam.setAppId(appId);
            TTAdNative mTTAdNative = tam.createAdNative(getWeakActivity());
            AdSlot adSlot = new AdSlot.Builder()
                    .setCodeId(adId)
                    .setSupportDeepLink(true)
                    .setExpressViewAcceptedSize(getImgWidth(), getImgHeight())
//                    .setImageAcceptedSize(getAcceptWidth(), getAcceptHeight())
                    .setAdCount(1) //请求广告数量为1到3条
                    .build();
            mTTAdNative.loadNativeExpressAd(adSlot,this);
            ab_is_loading.set(true);
            ab_last_load_time.set(System.currentTimeMillis());
        }
    }

    private void handOnError(int code, String message, String CUR_LOADING_ID) {
        FailModel fm = FailModel.toStr(code,message,CUR_LOADING_ID, Adv_Type.tt);
        Log.e(getTAG(),"onError(1),err_msg="+fm.toFullMsg());
        defaultCallback.onAdFailed(fm);
        //如果主ID请求失败，code=20001[没有广告了]，则用次ID去请求一次
        if(needRetry(Adv_Type.tt,code,message)){
            final String next_id = getNextId(adIdLst,CUR_LOADING_ID);
            Log.e(getTAG(),"onError(2),next_id="+next_id);
            if(next_id != null){
                startRequest("onError()");
            }
        }
    }

    @Override
    public void onError(int code, String message) {
        ab_is_loading.set(false);
        handOnError(code, "加载失败:"+message, adId);
    }

    @Override
    public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
        ab_is_loading.set(false);
        if (ads == null || ads.isEmpty()) {
            Log.e(getTAG(), "onNativeExpressAdLoad(),ads is empty,adId=" + adId);
            return;
        }
        final TTNativeExpressAd response = ads.get(0);
        if (response == null) {
            Log.e(getTAG(), "onNativeExpressAdLoad(),response is null,adId=" + adId);
            return;
        }

        String imgUrl = getTAG() + "_" + response.hashCode();
        AdNativeResponse nar = new AdNativeResponse(imgUrl, adId, Adv_Type.tt);
        int type = response.getInteractionType() == TTAdConstant.INTERACTION_TYPE_DOWNLOAD ? 1 : 0;
        nar.setAdvType(type);
        int img_mode = response.getImageMode();
        int ad_style = ADStyle.READER_PAGE_HORIZONTAL;
        if (img_mode == TTAdConstant.IMAGE_MODE_VERTICAL_IMG) {
            ad_style = ADStyle.READER_PAGE_VERTICAL;
        }
        nar.setAdvStyle(ad_style);

        mQueue.add(nar);
        mNativeResponses.put(nar.getImageUrl(), response);
        Log.e(getTAG(), "onNativeExpressAdLoad(),mQueue.size()=" + mQueue.size() + ",mNativeResponses.size()="+mNativeResponses.size()+",adId=" + adId);
        bindAdListener(response);
        response.render();
    }

    //绑定广告行为
    private void bindAdListener(final TTNativeExpressAd ad) {
        Log.e(getTAG(), "bindAdListener()");
        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            final AtomicInteger ab_st = new AtomicInteger(0);
            final AtomicLong mLastCallTime = new AtomicLong(0);
            @Override
            public void onAdClicked(View view, int type) {
                Log.e(getTAG(), "onAdClicked(),adId=" + adId+",type="+type);
                int m_type = type == TTAdConstant.INTERACTION_TYPE_DOWNLOAD ? 1 : 0;
                defaultCallback.onAdClick(ClickModel.getInstance(0,m_type,adId, Adv_Type.tt));
            }

            @Override
            public void onAdShow(View view, int type) {
                Log.e(getTAG(), "onAdShow(),展示成功,adId=" + adId+",type="+type);
                if(ab_st.get() == 1){
                    ab_st.set(2);
                }else {
                    long now = System.currentTimeMillis();
                    mLastCallTime.set(now);
                }
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                Log.e(getTAG(), "onRenderFail(),渲染失败,adId=" + adId+",code="+code+",msg="+msg);
                handOnError(code, "渲染失败:"+msg, adId);
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                Log.e(getTAG(), "onRenderSuccess(),渲染成功,adId=" + adId+",width="+width+",height="+height+",view.width="+view.getWidth()+",view.height="+view.getHeight());
                defaultCallback.onAdPresent(PresentModel.getInstance(adId, Adv_Type.tt));
                view_map.put(String.valueOf(ad.hashCode()),view);
                ab_st.set(1);
            }
        });
        //dislike设置
        bindDislike(ad, true);
        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD){
            return;
        }
        //可选，下载监听设置
        ad.setDownloadListener(new TTAppDownloadListener() {
            AtomicInteger ab_print_count = new AtomicInteger(0);
            @Override
            public void onIdle() {
//                TToast.show(NativeExpressActivity.this, "点击开始下载", Toast.LENGTH_LONG);
                Log.e(getTAG(), "onIdle(),【点击开始下载】,adId=" + adId);
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
//                if (!mHasShowDownloadActive) {
//                    mHasShowDownloadActive = true;
//                    TToast.show(NativeExpressActivity.this, "下载中，点击暂停", Toast.LENGTH_LONG);
//                }
                long progress = (long)(currBytes * 100f / totalBytes);
                if(ab_print_count.get() == 0 || progress >= 100){
                    ab_print_count.incrementAndGet();
                    String fileSize = (totalBytes * 1f / 1024) /1024 +"MB";
                    Log.e(getTAG(), "onDownloadActive(),【下载中】,adId=" + adId+",fileName="+fileName+",progress="+progress+",fileSize="+fileSize);
                }
            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                Log.e(getTAG(), "onDownloadPaused(),【下载暂停，点击继续】,adId=" + adId);
//                TToast.show(NativeExpressActivity.this, "下载暂停，点击继续", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
//                TToast.show(NativeExpressActivity.this, "下载失败，点击重新下载", Toast.LENGTH_LONG);
                long progress = (long)(currBytes * 100f / totalBytes);
                String fileSize = (totalBytes * 1f / 1024) /1024 +"MB";
                Log.e(getTAG(), "onDownloadFailed(),【下载失败，点击重新下载】,adId=" + adId+",fileName="+fileName+",appName="+appName+",progress="+progress+",fileSize="+fileSize);
            }

            @Override
            public void onInstalled(String fileName, String appName) {
//                TToast.show(NativeExpressActivity.this, "安装完成，点击图片打开", Toast.LENGTH_LONG);
                Log.e(getTAG(), "onInstalled(),【安装完成，点击图片打开】,adId=" + adId+",fileName="+fileName+",appName="+appName);
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
//                TToast.show(NativeExpressActivity.this, "点击安装", Toast.LENGTH_LONG);
                Log.e(getTAG(), "onDownloadFinished(),【点击安装】,adId=" + adId);
            }
        });
    }

    /**
     * 设置广告的不喜欢，开发者可自定义样式
     * @param ad
     * @param customStyle 是否自定义样式，true:样式自定义
     */
    private void bindDislike(TTNativeExpressAd ad, boolean customStyle) {
        Log.e(getTAG(), "bindDislike(),customStyle="+customStyle);
        Activity act = getWeakActivity();
        if(act == null || act.isDestroyed() || act.isFinishing()){
            Log.e(getTAG(), "bindDislike(),act == null");
            return;
        }
        if (customStyle) {
            //使用自定义样式
            List<FilterWord> words = ad.getFilterWords();
            if (words == null || words.isEmpty()) {
                Log.e(getTAG(), "bindDislike(),words == null");
                return;
            }

            final DislikeDialog dislikeDialog = new DislikeDialog(act, words);
            dislikeDialog.setOnDislikeItemClick((FilterWord filterWord) -> {
                //屏蔽广告
                //TToast.show(myWeakActivity.get(), "点击 " + filterWord.getName());
                //用户选择不喜欢原因后，移除广告展示
                handOnDisLike(filterWord.getName());
            });
            ad.setDislikeDialog(dislikeDialog);
            return;
        }
        //使用默认个性化模板中默认dislike弹出样式
        ad.setDislikeCallback(act, new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onSelected(int position, String value) {
                handOnDisLike(value);
            }

            @Override
            public void onCancel() {
                TToast.show(getAct(), "点击取消 ");
            }

            @Override
            public void onRefuse() {

            }
        });
        Log.e(getTAG(), "bindDislike(),end");
    }

    /** 处理用户关闭广告的逻辑 */
    private void handOnDisLike(final String reason){
        Log.e(getTAG(), "handOnDisLike(),begin,reason="+reason);
        ViewGroup mAdContainer = getAdContainer();
        if(mAdContainer != null) {
            Log.e(getTAG(), "handOnDisLike(),mAdContainer删除所有子View");
            mAdContainer.removeAllViews();
            ViewGroup vp = (ViewGroup) mAdContainer.getParent();
            int count = 0;
            while (vp != null){
                try {
                    if(vp.getId() == R.id.adv_card_root_layout){
                        //((ViewGroup)vp.getParent()).removeAllViews();
                        vp.setVisibility(View.INVISIBLE);
                        break;
                    }
                    vp = (ViewGroup) vp.getParent();
                } finally {
                    count ++;
                    if(count >= 10){
                        break;
                    }
                }
            }
//            mBannerContainer.postDelayed(()-> startRequest("handOnDisLike()"),1000 * 30);
        }else{
            Log.e(getTAG(), "handOnDisLike(),mAdContainer is null");
        }
        defaultCallback.onDisLike(PresentModel.getInstance(adId, Adv_Type.tt).put("dis_like_reason",reason));
        Log.e(getTAG(), "handOnDisLike(),end");
    }
}
