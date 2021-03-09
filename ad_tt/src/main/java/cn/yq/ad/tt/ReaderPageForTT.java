package cn.yq.ad.tt;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTImage;
import com.bytedance.sdk.openadsdk.TTNativeAd;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import cn.yq.ad.ADCallback;
import cn.yq.ad.ADStyle;
import cn.yq.ad.Adv_Type;
import cn.yq.ad.AdNativeResponse;
import cn.yq.ad.ShowModel;
import cn.yq.ad.impl.ClickModel;
import cn.yq.ad.impl.FailModel;
import cn.yq.ad.impl.PresentModel;
import cn.yq.ad.tt.config.TTUtil;
import cn.yq.ad.tt.utils.TToast;

/**
 * 内容_插屏
 */
public class ReaderPageForTT extends ADBaseImplByTT<TTFeedAd> {

    protected String getTAG(){
        return ReaderPageForTT.class.getSimpleName();
    }
    protected int getAcceptWidth(){
        return 640;
    }
    protected int getAcceptHeight(){
        return 320;
    }
    protected final boolean onLoadSucNeedCallBack(){
        return true;
    }
    //====================
    ReaderPageForTT(Activity act, String appId, String adId) {
        super(act, appId, adId);
        Log.e(getTAG(),"构造方法(),appId="+appId+",adId="+adId);
    }

    /** 是否使用删除模式(如果对填充率要求非常严格的话，可以设置为true)
     * true：展示出来之后才会删除，在getAdvertEntityView()和show()方法时删除，在子类中需要确保在这两个方法中调用removeLastResp()方法
     * false：展示出来之前就会删除，即在getAdvertEntity()的时候就删除
     * */
    protected boolean NEED_USE_REMOVE_MODE(){
        return false;
    }
    private volatile AdNativeResponse tmpLastResp = null;
    private void setLastResp(AdNativeResponse resp) {
        if(NEED_USE_REMOVE_MODE()) {
            this.tmpLastResp = resp;
        }
    }

    /**
     * 确定每一个子类的getAdvertEntityView()方法都得调用
     */
    protected final void removeLastResp() {
        if(NEED_USE_REMOVE_MODE()){
            if (tmpLastResp != null) {
                boolean removed = mQueue.remove(tmpLastResp);
                Log.e(getTAG(), "getAdvertEntityView(),removed=" + removed + ", lastResp=" + tmpLastResp.getImageUrl());
                if (removed) {
                    tmpLastResp = null;
                }
            }
        }
    }
    @Override
    public final AdNativeResponse getAdvertEntity(String from, Map<String, String> map) {
        synchronized (mQueue){
            final int sz = mQueue.size();
            if (sz <= 0) {
                Log.e(getTAG(), "getAdvertEntity("+from+"),mQueue.size() == 0,将要重新加载");
                startRequest("getAdvertEntity("+from+")");
                return null;
            }
            AdNativeResponse resp ;
            if(NEED_USE_REMOVE_MODE()){
                resp = mQueue.peek();
            }else {
                resp = mQueue.poll();
            }
            final int freeSize = mQueue.size();
            if(resp != null){
                Log.e(getTAG(), "getAdvertEntity("+from+"),剩于广告条数="+freeSize+",resp.title=" + resp.getTitle());
            }else{
                Log.e(getTAG(), "getAdvertEntity("+from+"),剩于广告条数="+freeSize+",resp is null");
            }
//            if(freeSize <= 0) {
//                startRequest("getAdvertEntity("+from+")");
//            }
            setLastResp(resp);
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
        TTFeedAd ad = mNativeResponses.get(nar.getImageUrl());
        if(ad == null){
            Log.e(getTAG(), "getAdvertEntityView(),ad is null");
            return null;
        }
        ViewGroup vv = null;
//        String tt_adv_type = "";
//        if (ad.getImageMode() == TTAdConstant.IMAGE_MODE_SMALL_IMG) {
//            tt_adv_type = "单图";
//        } else if (ad.getImageMode() == TTAdConstant.IMAGE_MODE_LARGE_IMG) {
//            tt_adv_type = "大图";
//        } else if (ad.getImageMode() == TTAdConstant.IMAGE_MODE_GROUP_IMG) {
//            tt_adv_type = "组图";
//        } else if (ad.getImageMode() == TTAdConstant.IMAGE_MODE_VIDEO) {
//            tt_adv_type = "视频";
//        }else if(ad.getImageMode() == TTAdConstant.IMAGE_MODE_VERTICAL_IMG){
//            tt_adv_type = "竖图";
//        }
        int img_mode = ad.getImageMode();
        if(img_mode == TTAdConstant.IMAGE_MODE_VERTICAL_IMG){
            vv = (ViewGroup) LayoutInflater.from(getAct()).inflate(R.layout.layout_adv_for_tt_pge_ver,null);
        }else{
            vv = (ViewGroup) LayoutInflater.from(getAct()).inflate(R.layout.layout_adv_for_tt_pge,null);
        }
        bindData(vv,ad,nar);
        Log.e(getTAG(), "getAdvertEntityView()");
        removeLastResp();
        return vv;
    }

//    NativeAdResponse show_tmp_nar;
//    View show_view;
    @Override
    public void showTj(ShowModel sm) {
        super.showTj(sm);
        boolean tj_upload = false;
//        if(show_view != null && show_tmp_nar != null){
//            show_imp(show_view,show_tmp_nar);
//            tj_upload = true;
//        }
        Log.e(getTAG(), "showTj(),tj_upload="+tj_upload);
    }

    @Override
    public void show(View vv, Object obj) {
        Log.e(getTAG(), "show()");
        if (obj instanceof AdNativeResponse) {
//            show_tmp_nar = (NativeAdResponse)obj;
//            show_view = vv;
            show_imp(vv,(AdNativeResponse)obj);
        }
    }

    private void removeFirstKey(){
        Set<String> keySet = mNativeResponses.keySet();
        if(keySet.size() > 20){
            List<String> keyLst = new ArrayList<>(keySet);
            mNativeResponses.remove(keyLst.get(0));
        }
    }

    @Override
    public void click(View view, Object obj) {
//        if (obj instanceof NativeAdResponse) {
//            NativeAdResponse nar = (NativeAdResponse) obj;
//            TTFeedAd dataRef = mNativeResponses.get(nar.getImageUrl());
//            adCallback.onAdClick(nar);
//            if (dataRef != null) {
//                Log.e(TAG,"click(),title="+dataRef.getTitle());
//            }
//        }
    }

    @Override
    public void load() {
        //【分多次请求，每次请求一条】 or 【一次请求多条】
        int rq = getRequestCount();
        for (int i = 1;i <= rq; i++) {
            startRequest("load("+i+")");
        }
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
        mQueue.clear();
        setLastResp(null);
        super.destroy();
    }

    private final Object ad_obj_lock = new Object();
    private final AtomicLong LOAD_INDEX = new AtomicLong(0);
    private void startRequest(String from) {
        synchronized (ad_obj_lock){
            LOAD_INDEX.incrementAndGet();
            Log.e(getTAG(),"==========startRequest(),index="+LOAD_INDEX.get()+",from="+from+",mainId="+adId);
            TTAdManager tam = TTUtil.get().getAdManager();
            tam.setAppId(appId);
            TTAdNative mTTAdNative = tam.createAdNative(getWeakActivity());
            AdSlot adSlot = new AdSlot.Builder()
                    .setCodeId(adId)
                    .setSupportDeepLink(true)
                    .setImageAcceptedSize(getAcceptWidth(), getAcceptHeight())
                    .setAdCount(1) //请求广告数量为1到3条
                    .build();
            mTTAdNative.loadFeedAd(adSlot, new TTAdNative.FeedAdListener() {
                @Override
                public void onError(int code, String message) {
                    handOnError(code,message,adId);
                }

                @Override
                public void onFeedAdLoad(List<TTFeedAd> ads) {
//                    ad_is_loading.set(false);
                    if (ads == null || ads.isEmpty()) {
                        Log.e(getTAG(), "onFeedAdLoad(),ads is empty,adId="+adId);
                        return;
                    }
                    Log.e(getTAG(), "onFeedAdLoad(),ads.size()="+ads.size()+",adId="+adId);
                    List<AdNativeResponse> lst = new ArrayList<>();
                    //信息流广告的样式，有大图、小图、组图和视频，通过ad.getImageMode()来判断
                    for (TTFeedAd response : ads) {
                        if(response == null){
                            continue;
                        }

//                        Log.e(getTAG(), "onFeedAdLoad(),adId="+adId);
                        List<TTImage> ttList = response.getImageList();
                        if(ttList == null || ttList.size() == 0){
                            continue;
                        }
//                    response.setActivityForDownloadApp(wrAct.get());
                        String imgUrl = ttList.get(0).getImageUrl();
                        String iconUrl = response.getIcon().getImageUrl();
                        AdNativeResponse nar = new AdNativeResponse(imgUrl,adId, Adv_Type.tt);
                        int type = response.getInteractionType() == TTAdConstant.INTERACTION_TYPE_DOWNLOAD ? 1 : 0;
                        nar.setAdvType(type);
                        int img_mode = response.getImageMode();
//                        int img_w = response.getIcon().getWidth();
//                        int img_h = response.getIcon().getHeight();
                        int ad_style = ADStyle.READER_PAGE_HORIZONTAL;
                        if(img_mode == TTAdConstant.IMAGE_MODE_VERTICAL_IMG){
//                            Log.e(getTAG(),"onFeedAdLoad(),竖图,img_w="+img_w+",img_h="+img_h);
                            ad_style = ADStyle.READER_PAGE_VERTICAL;
                        }else if(img_mode == TTAdConstant.IMAGE_MODE_VIDEO || img_mode == TTAdConstant.IMAGE_MODE_VIDEO_VERTICAL){
//                            Log.e(getTAG(),"onFeedAdLoad(),视频,img_w="+img_w+",img_h="+img_h);
                        }else if(img_mode == TTAdConstant.IMAGE_MODE_GROUP_IMG){
//                            Log.e(getTAG(),"onFeedAdLoad(),组图,img_w="+img_w+",img_h="+img_h);
                        }else if(img_mode == TTAdConstant.IMAGE_MODE_LARGE_IMG){
//                            Log.e(getTAG(),"onFeedAdLoad(),大图,img_w="+img_w+",img_h="+img_h);
                        }else if(img_mode == TTAdConstant.IMAGE_MODE_SMALL_IMG){
//                            Log.e(getTAG(),"onFeedAdLoad(),小图,img_w="+img_w+",img_h="+img_h);
                        }
                        nar.setAdvStyle(ad_style);
                        nar.setIcon(iconUrl);
                        nar.setTitle(response.getTitle());
                        nar.setDesc(response.getDescription());
                        if(ttList.size() > 1){
                            List<String> tmpLst = new ArrayList<>();
                            for (TTImage ttImage : ttList) {
                                tmpLst.add(ttImage.getImageUrl());
                            }
                            nar.setImgUrlLst(tmpLst);
                        }
                        mQueue.add(nar);
                        lst.add(nar);
                        mNativeResponses.put(nar.getImageUrl(),response);
                    }
                    if(onLoadSucNeedCallBack()) {
                        defaultCallback.onAdPresent(PresentModel.getInstance(adId, Adv_Type.tt).setData(lst));
                    }
                }
            });
        }
    }

    protected boolean needAddToCreateView(){
        return true;
    }

    private void handOnError(int code, String message, String CUR_LOADING_ID) {
//        ad_is_loading.set(false);
        FailModel fm = FailModel.toStr(code,message,CUR_LOADING_ID,Adv_Type.tt);
        Log.e(getTAG(),"onError(1),err_msg="+fm.toFullMsg());
        defaultCallback.onAdFailed(fm);
        //如果主ID请求失败，code=20001[没有广告了]，则用次ID去请求一次
        if(needRetry(Adv_Type.tt,code,message)){
            load_fail_time.set(System.currentTimeMillis());
            final String next_id = getNextId(adIdLst,CUR_LOADING_ID);
            Log.e(getTAG(),"onError(2),next_id="+next_id);
            if(next_id != null){
                startRequest("onError()");
            }
        }
    }

    //==================bindEvent=================
    protected final void bindData(ViewGroup convertView, TTFeedAd ad, final AdNativeResponse nar) {
        if(ad == null){
            Log.e(getTAG(), "bindData(),ad is null");
            return;
        }
        final Context mContext = getWeakActivity();
        //可以被点击的view, 也可以把convertView放进来意味item可被点击
        List<View> clickViewList = new ArrayList<>();
        clickViewList.add(convertView);
        //触发创意广告的view（点击下载或拨打电话）
        List<View> creativeViewList = new ArrayList<>();
//        creativeViewList.add(adViewHolder.mCreativeButton);
        if(needAddToCreateView()) {
            //如果需要点击图文区域也能进行下载或者拨打电话动作，请将图文区域的view传入
            creativeViewList.add(convertView);
        }

        if(nar != null){
            List<View> vsList = nar.getViewList();
            if(vsList != null && vsList.size() > 0){
                clickViewList.addAll(vsList);
                creativeViewList.addAll(vsList);
            }
        }
        //重要! 这个涉及到广告计费，必须正确调用。convertView必须使用ViewGroup。
        ad.registerViewForInteraction(convertView, clickViewList, creativeViewList, new MyNativeAdListener(nar,adId,getTAG(),defaultCallback));
//        adViewHolder.mTitle.setText(ad.getTitle()); //title为广告的简单信息提示
//        adViewHolder.mDescription.setText(ad.getDescription()); //description为广告的较长的说明
//        adViewHolder.mSource.setText(ad.getSource() == null ? "广告来源" : ad.getSource());
//        TTImage icon = ad.getIcon();
//        if (icon != null && icon.isValid()) {
//            ImageOptions options = new ImageOptions();
//            mAQuery.id(adViewHolder.mIcon).image(icon.getImageUrl(), options);
//        }
//        Button adCreativeButton = adViewHolder.mCreativeButton;
        switch (ad.getInteractionType()) {
            case TTAdConstant.INTERACTION_TYPE_DOWNLOAD:
                //如果初始化ttAdManager.createAdNative(getApplicationContext())没有传入activity 则需要在此传activity，否则影响使用Dislike逻辑
//                  ad.setActivityForDownloadApp(getAct());
//                adCreativeButton.setVisibility(View.VISIBLE);
//                adViewHolder.mStopButton.setVisibility(View.VISIBLE);
//                adViewHolder.mRemoveButton.setVisibility(View.VISIBLE);
//                bindDownloadListener(adCreativeButton, adViewHolder, ad);
//                //绑定下载状态控制器
//                bindDownLoadStatusController(adViewHolder, ad);
                break;
            case TTAdConstant.INTERACTION_TYPE_DIAL:
//                adCreativeButton.setVisibility(View.VISIBLE);
//                adCreativeButton.setText("立即拨打");
//                adViewHolder.mStopButton.setVisibility(View.GONE);
//                adViewHolder.mRemoveButton.setVisibility(View.GONE);
                break;
            case TTAdConstant.INTERACTION_TYPE_LANDING_PAGE:
            case TTAdConstant.INTERACTION_TYPE_BROWSER:
//                    adCreativeButton.setVisibility(View.GONE);
//                adCreativeButton.setVisibility(View.VISIBLE);
//                adCreativeButton.setText("查看详情");
//                adViewHolder.mStopButton.setVisibility(View.GONE);
//                adViewHolder.mRemoveButton.setVisibility(View.GONE);
                break;
            default:
//                adCreativeButton.setVisibility(View.GONE);
//                adViewHolder.mStopButton.setVisibility(View.GONE);
//                adViewHolder.mRemoveButton.setVisibility(View.GONE);
                TToast.show(mContext, "交互类型异常");
        }
    }

//    private void bindDownLoadStatusController(AdViewHolder adViewHolder, final TTFeedAd ad) {
//        final DownloadStatusController controller = ad.getDownloadStatusController();
//        adViewHolder.mStopButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (controller != null) {
//                    controller.changeDownloadStatus();
//                    TToast.show(mContext, "改变下载状态");
//                    Log.d(TAG, "改变下载状态");
//                }
//            }
//        });
//
//        adViewHolder.mRemoveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (controller != null) {
//                    controller.cancelDownload();
//                    TToast.show(mContext, "取消下载");
//                    Log.d(TAG, "取消下载");
//                }
//            }
//        });
//    }

//    private void bindDownloadListener(final Button adCreativeButton, final AdViewHolder adViewHolder, TTFeedAd ad) {
//        TTAppDownloadListener downloadListener = new TTAppDownloadListener() {
//            @Override
//            public void onIdle() {
//                if (!isValid()) {
//                    return;
//                }
//                adCreativeButton.setText("开始下载");
//                adViewHolder.mStopButton.setText("开始下载");
//            }
//
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
//                if (!isValid()) {
//                    return;
//                }
//                if (totalBytes <= 0L) {
//                    adCreativeButton.setText("下载中 percent: 0");
//                } else {
//                    adCreativeButton.setText("下载中 percent: " + (currBytes * 100 / totalBytes));
//                }
//                adViewHolder.mStopButton.setText("下载中");
//            }
//
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
//                if (!isValid()) {
//                    return;
//                }
//                if (totalBytes <= 0L) {
//                    adCreativeButton.setText("下载暂停 percent: 0");
//                } else {
//                    adCreativeButton.setText("下载暂停 percent: " + (currBytes * 100 / totalBytes));
//                }
//                adViewHolder.mStopButton.setText("下载暂停");
//            }
//
//            @Override
//            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
//                if (!isValid()) {
//                    return;
//                }
//                adCreativeButton.setText("重新下载");
//                adViewHolder.mStopButton.setText("重新下载");
//            }
//
//            @Override
//            public void onInstalled(String fileName, String appName) {
//                if (!isValid()) {
//                    return;
//                }
//                adCreativeButton.setText("点击打开");
//                adViewHolder.mStopButton.setText("点击打开");
//            }
//
//            @Override
//            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
//                if (!isValid()) {
//                    return;
//                }
//                adCreativeButton.setText("点击安装");
//                adViewHolder.mStopButton.setText("点击安装");
//            }
//
//            @SuppressWarnings("BooleanMethodIsAlwaysInverted")
//            private boolean isValid() {
//                return mTTAppDownloadListenerMap.get(adViewHolder) == this;
//            }
//        };
//        //一个ViewHolder对应一个downloadListener, isValid判断当前ViewHolder绑定的listener是不是自己
//        ad.setDownloadListener(downloadListener); // 注册下载监听器
//        mTTAppDownloadListenerMap.put(adViewHolder, downloadListener);
//    }

    private final AtomicLong load_fail_time = new AtomicLong(0);
    private void show_imp(View vv, AdNativeResponse tmp_nar){
        removeLastResp();
        TTFeedAd ad = mNativeResponses.get(tmp_nar.getImageUrl());
        if (ad == null) {
            Log.e(getTAG(),"show(),ad is null");
            return;
        }

//        ViewGroup childLayout = vv.findViewById(R.id.layout_adv_for_tt_image_view_layout);
//        View adView = ad.getAdView();
//        if (adView != null) {
//            childLayout.addView(adView);
//        }else{
//            final ImageView iv_image = vv.findViewById(R.id.layout_adv_for_tt_image_view);
//            String imgUrl = tmp_nar.getImageUrl();
//            if(imgUrl != null && imgUrl.trim().length() > 0){
//                PicassoUtil.show(iv_image,imgUrl);
//            }
//        }
//
//        final ImageView iv_icon = vv.findViewById(R.id.layout_adv_for_tt_icon_iv);
//        updateBtnText(vv,tmp_nar);
//
//        //设置标题
//        String str_title = ad.getDescription();
//        if(str_title == null || str_title.trim().length() == 0){
//            str_title = ad.getTitle();
//        }
//        TextView tv_title = vv.findViewById(R.id.layout_adv_for_tt_tv);
//        tv_title.setText(str_title);
//
//        //设置描述
//        String str_desc = ad.getTitle();
//        if(str_desc == null || str_desc.trim().length() == 0){
//            str_desc = DEFAULT_TITLE;
//        }
//        TextView tv_desc = vv.findViewById(R.id.layout_adv_for_tt_tv_desc);
//        tv_desc.setText(str_desc);
//
//        String iconUrl =tmp_nar.getIcon();
//        if(iconUrl != null && iconUrl.trim().length() > 0){
//            PicassoUtil.show(iv_icon,iconUrl);
//        }
        removeFirstKey();
    }

    private static class MyNativeAdListener implements TTNativeAd.AdInteractionListener{
        private final AdNativeResponse nar;
        private final String adId;
        private final String TAG;
        private final WeakReference<ADCallback> wr;
        private final AtomicBoolean ab_show_suc = new AtomicBoolean(false);
        public MyNativeAdListener(AdNativeResponse nar, String adId, String TAG, ADCallback cb) {
            this.nar = nar;
            this.adId = adId;
            this.TAG = TAG;
            this.wr = new WeakReference<>(cb);
        }

        @Override
        public void onAdClicked(View view, TTNativeAd ad) {
            if (ad != null) {
                Log.e(TAG, "onAdClicked(),ad.title="+ad.getTitle());
                int advType = ad.getInteractionType() == TTAdConstant.INTERACTION_TYPE_DOWNLOAD ? 1 : 0;
                ADCallback adCallback = wr.get();
                if(adCallback != null) {
                    adCallback.onAdClick(ClickModel.getInstance(0, advType, adId, Adv_Type.tt).setData(nar));
                }
            }
        }

        @Override
        public void onAdCreativeClick(View view, TTNativeAd ad) {
            if (ad != null) {
                Log.e(TAG, "onAdCreativeClick(),ad.title="+ad.getTitle());
                int advType = ad.getInteractionType() == TTAdConstant.INTERACTION_TYPE_DOWNLOAD ? 1 : 0;
                ADCallback adCallback = wr.get();
                if(adCallback != null) {
                    adCallback.onAdClick(ClickModel.getInstance(0, advType, adId, Adv_Type.tt).setData(nar));
                }
            }
        }

        @Override
        public void onAdShow(TTNativeAd ad) {
            if (ad != null) {
                Log.e(TAG, "onAdShow(),ad.name="+ad.getTitle());
                if(ab_show_suc.get()){

                }else{
                    ADCallback adCallback = wr.get();
                    if(adCallback != null) {
                        Map<String, String> ext = new HashMap<>();
                        ext.put("adTitle",ad.getTitle());
                        adCallback.onADExposed(PresentModel.getInstance(adId, Adv_Type.tt).setExtMap(ext));
                    }
                }
                ab_show_suc.set(true);
            }
        }
    }
}
