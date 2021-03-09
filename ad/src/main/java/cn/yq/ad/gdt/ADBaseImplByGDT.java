package cn.yq.ad.gdt;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.MediaView;
import com.qq.e.ads.nativ.NativeADEventListener;
import com.qq.e.ads.nativ.NativeADMediaListener;
import com.qq.e.ads.nativ.NativeADUnifiedListener;
import com.qq.e.ads.nativ.NativeUnifiedAD;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.qq.e.ads.nativ.widget.NativeAdContainer;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.util.AdError;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.yq.ad.ADStyle;
import cn.yq.ad.Adv_Type;
import cn.yq.ad.AdConf;
import cn.yq.ad.AdNativeResponse;
import cn.yq.ad.ShowModel;
import cn.yq.ad.impl.ADBaseImpl;
import cn.yq.ad.impl.ADCallBackImpl;
import cn.yq.ad.impl.ClickModel;
import cn.yq.ad.impl.FailModel;
import cn.yq.ad.impl.PresentModel;
import cn.yq.ad.util.AdNetworkUtils;

/**
 * Created by liguo on 2018/10/17.
 * 文档地址：https://developers.adnet.qq.com/doc/android/access_doc
 * 错误码地址1：https://developers.adnet.qq.com/doc/android/union/union_debug#sdk%20%E9%94%99%E8%AF%AF%E7%A0%81
 * 错误码地址2：https://developers.adnet.qq.com/backend/error_code.html
 * desc
 */
public class ADBaseImplByGDT extends ADBaseImpl implements NativeADUnifiedListener {

    final String DEFAULT_TITLE = "腾讯广告";
    protected WeakReference<Activity> act;
    final String appId;
    final String adId;

    private NativeUnifiedAD nativeAD;

    ADBaseImplByGDT(Activity act, String appId, String adId) {
        this.act = new WeakReference<>(act);
        this.appId = appId;
        this.adId = adId;
    }

    @Override
    public void showTj(ShowModel sm) {
        if(sm != null) {
            sm.setAdv_type(Adv_Type.gdt);
        }
    }

    public String getTAG(){
       return ADBaseImplByGDT.class.getSimpleName();
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
            AdNativeResponse resp = mQueue.poll();
            final int freeSize = mQueue.size();
            if(resp != null){
                Log.e(getTAG(), "getAdvertEntity("+from+"),成功，剩于广告条数="+freeSize+",resp.title=" + resp.getTitle());
            }else{
                Log.e(getTAG(), "getAdvertEntity("+from+"),失败，剩于广告条数="+freeSize);
            }
            return resp;
        }

    }

    private NativeUnifiedADData mLastAd = null;

    protected final List<View> toClickViewList(View clickView){
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(clickView);
        return clickableViews;
    }
    /** clickView 必须是在 NativeAdContainer 以内的View才有效 */
    final void renderAd(Context ctx, final NativeUnifiedADData ad, NativeAdContainer mContainer, View clickView) {
        renderAd(ctx, ad, mContainer, null, toClickViewList(clickView), null);
    }

    final void renderAd(Context ctx, final NativeUnifiedADData ad, NativeAdContainer mContainer, View actionView, View clickView) {
        renderAd(ctx, ad, mContainer, actionView, toClickViewList(clickView), null);
    }

    final void renderAd(Context ctx, final NativeUnifiedADData ad, NativeAdContainer mContainer, View actionView, List<View> clickViewLst, MediaView mediaView){
        if(mLastAd != null){
            try {
                String hash_code = String.valueOf(mLastAd.hashCode());
                String imgUrl = hashCode_imgUrl.get(hash_code);
                NativeUnifiedADData oldAdData = null;
                if(imgUrl != null){
                    oldAdData = mNativeResponses.get(imgUrl);
                }
                if(oldAdData != null){
                    mNativeResponses.remove(imgUrl);
                    if(oldAdData != mLastAd) {
                        oldAdData.destroy();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mLastAd.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mLastAd = null;
        }
        mLastAd = ad;
        List<View> clickableViews = new ArrayList<>();
        if (clickViewLst != null && clickViewLst.size() > 0) {
            clickableViews.addAll(clickViewLst);
        }
        if (actionView != null) {
            clickableViews.add(actionView);
        }
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(0, 0);
        ad.bindAdToView(ctx, mContainer, layoutParams, clickableViews);
//        final NativeAdResponse nar = cloneAdData(ad);
        ad.setNativeAdEventListener(new MyNativeADEventListener(ad,adId,getTAG(),defaultCallback));

        if (ad.getAdPatternType() == AdPatternType.NATIVE_VIDEO && mediaView != null) {

            VideoOption videoOption = getVideoOption(AdNetworkUtils.isWifi(act.get())
                    ? VideoOption.AutoPlayPolicy.WIFI : VideoOption.AutoPlayPolicy.ALWAYS);

            ad.bindMediaView(mediaView, videoOption, new NativeADMediaListener() {
                @Override
                public void onVideoInit() {
                    Log.d(getTAG(), "onVideoInit");
                }

                @Override
                public void onVideoLoading() {
                    Log.d(getTAG(), "onVideoLoading");
                }

                @Override
                public void onVideoReady() {
                    Log.d(getTAG(), "onVideoReady");
                }

                @Override
                public void onVideoLoaded(int videoDuration) {
                    Log.d(getTAG(), "onVideoLoaded: " + videoDuration);

                }

                @Override
                public void onVideoStop() {
                    Log.d(getTAG(), "onVideoStop(): ");
                }

                @Override
                public void onVideoClicked() {
                    Log.d(getTAG(), "onVideoClicked(): ");
                }

                @Override
                public void onVideoStart() {
                    Log.d(getTAG(), "onVideoStart");
                }

                @Override
                public void onVideoPause() {
                    Log.d(getTAG(), "onVideoPause");
                }

                @Override
                public void onVideoResume() {
                    Log.d(getTAG(), "onVideoResume");
                }

                @Override
                public void onVideoCompleted() {
                    Log.d(getTAG(), "onVideoCompleted");
                }

                @Override
                public void onVideoError(AdError error) {
                    Log.d(getTAG(), "onVideoError: " + error.getErrorCode() + "," + error.getErrorMsg());
                }
            });
        }

        if (actionView instanceof TextView) {
            updateAdAction((TextView) actionView, ad);
        }
    }

    @Override
    public final void click(View view, Object obj) {
//        if (obj instanceof NativeAdResponse) {
//            NativeAdResponse nar = (NativeAdResponse) obj;
//            NativeUnifiedADData dataRef = mNativeResponses.get(nar.getImageUrl());
//            adCallback.onAdClick(nar);
//            if (dataRef != null) {
//                Log.e(getTAG(),"click(),title="+dataRef.getTitle());
//                dataRef.onClicked(view);
//            }
//        }
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
        if(mLastAd != null){
            try {
                mLastAd.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mLastAd = null;
        }
        if(mNativeResponses.size() > 0){
            Collection<NativeUnifiedADData> dataLst = mNativeResponses.values();
            for (NativeUnifiedADData ad : dataLst){
                try {
                    ad.destroy();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        while (true){
            AdNativeResponse nar = mQueue.poll();
            if(nar == null){
                break;
            }
            nar.destroy();
        }

        mNativeResponses.clear();
        hashCode_imgUrl.clear();
        mQueue.clear();
        super.destroy();
    }
    private void startRequest(String from) {
        if(nativeAD == null) {
            nativeAD = new NativeUnifiedAD(getContextFromActivity(act.get()) , appId, adId, this);
//            nativeAD.setCategories("asdXXX");
            nativeAD.setMaxVideoDuration(30);   //5~60之间
        }
        Log.e(getTAG(),"startRequest(),from="+from+",appId="+appId+",adId="+adId);
        nativeAD.loadData(getRequestCount());
    }

    final Map<String, NativeUnifiedADData> mNativeResponses = new HashMap<>();
    final Map<String, String> hashCode_imgUrl = new HashMap<>();
    protected final Queue<AdNativeResponse> mQueue = new LinkedBlockingQueue<>();
    @Override
    public final Adv_Type getAdvType() {
        return Adv_Type.gdt;
    }

    //==================================

    @Override
    public AdConf getCfg() {
        AdConf bd = new AdConf();
        bd.setAppId(appId);
        bd.setAdId(adId);
        return bd;
    }

    @Override
    public void onNoAD(AdError adError) {
        try {
            FailModel fm = FailModel.toStr(adError.getErrorCode(),adError.getErrorMsg(),adId,Adv_Type.gdt);
            Log.e(getTAG(),"onNoAD(),err_msg="+fm.toFullMsg());
            defaultCallback.onAdFailed(fm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onADLoaded(List<NativeUnifiedADData> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        List<AdNativeResponse> lst = new ArrayList<>();
        for (NativeUnifiedADData response : list) {
            Map<String, String> pp = new HashMap<>();
            String hash_code = String.valueOf(response.hashCode());
            pp.put("hash_code",hash_code);
            String imgUrl = appendParams(response.getImgUrl(),pp);
            AdNativeResponse nar = cloneAdData(response,imgUrl);

            mQueue.add(nar);
            lst.add(nar);
            mNativeResponses.put(nar.getImageUrl(), response);
            hashCode_imgUrl.put(hash_code,nar.getImageUrl());
        }
        Log.e(getTAG(), "onADLoaded(),mQueue.size()=" + mQueue.size());
        defaultCallback.onAdPresent(PresentModel.getInstance(adId,Adv_Type.gdt).setData(lst));
    }

    @Override
    public void resume(Object obj) {
        super.resume(obj);
        Log.e(getTAG(),"resume(),mLastAd="+mLastAd);
        if(mLastAd != null){
            mLastAd.resume();
        }
    }

    private AdNativeResponse cloneAdData(NativeUnifiedADData data, String imgUrl) {
        String iconUrl = data.getIconUrl();
        AdNativeResponse nar = new AdNativeResponse(imgUrl,adId,Adv_Type.gdt);
        //ad.getAdPatternType() == AdPatternType.NATIVE_VIDEO 视频类型广告
        int img_w = data.getPictureWidth();
        int img_h = data.getPictureHeight();
        if(img_h > img_w){
            nar.setAdvStyle(ADStyle.READER_PAGE_VERTICAL);
            Log.e(getTAG(),"onFeedAdLoad(),竖图,img_w="+img_w+",img_h="+img_h + ",type=" + data.getAdPatternType()+",img_url="+imgUrl);
        }else{
            nar.setAdvStyle(ADStyle.READER_PAGE_HORIZONTAL);
            Log.e(getTAG(),"onFeedAdLoad(),横图,img_w="+img_w+",img_h="+img_h + ",type=" + data.getAdPatternType()+",img_url="+imgUrl);
        }

        nar.setIcon(iconUrl);
        nar.setTitle(data.getTitle());
        nar.setDesc(data.getDesc());
        nar.setAdvType(data.isAppAd() ? 1 : 0); //落地页类型：1：下载 0：其它
        return nar;
    }

    private static VideoOption getVideoOption(int mPlayNetwork) {
        VideoOption.Builder builder = new VideoOption.Builder();
        builder.setAutoPlayPolicy(mPlayNetwork);
        builder.setAutoPlayMuted(true);
        return builder.build();
    }

    private void updateAdAction(TextView button, NativeUnifiedADData ad) {
        if (!ad.isAppAd()) {
            button.setText("查看详情");
            return;
        }
        switch (ad.getAppStatus()) {
            case 0:
                button.setText("立即下载");
                break;
            case 1:
                button.setText("启动");
                break;
            case 2:
                button.setText("更新");
                break;
            case 4:
                String txt = ad.getProgress() + "%";
                button.setText(txt);
                break;
            case 8:
                button.setText("安装");
                break;
            case 16:
                button.setText("下载失败，重新下载");
                break;
            default:
                button.setText("查看详情");
                break;
        }
    }

    @Override
    public int getDataSize() {
        return mQueue.size();
    }

    private static class MyNativeADEventListener implements NativeADEventListener{
        private final NativeUnifiedADData mLastAd;
        private final String adId;
        private final String TAG;
        private final WeakReference<ADCallBackImpl> wrCallback;

        private final AtomicBoolean ab_show_suc = new AtomicBoolean(false);
        public MyNativeADEventListener(NativeUnifiedADData mLastAd, String adId, String TAG, ADCallBackImpl callback) {
            this.mLastAd = mLastAd;
            this.adId = adId;
            this.TAG = TAG;
            this.wrCallback = new WeakReference<>(callback);
        }

        @Override
        public void onADExposed() {
            String title = mLastAd != null ? mLastAd.getTitle() : "null";
            Log.e(TAG, "onADExposed(),adTitle="+title);
            if(ab_show_suc.get()){

            }else{
                ADCallBackImpl adCallback = wrCallback.get();
                if(adCallback != null) {
                    Map<String, String> ext = new HashMap<>();
                    ext.put("adTitle",title);
                    adCallback.onADExposed(PresentModel.getInstance(adId,Adv_Type.gdt).setExtMap(ext));
                }
            }
            ab_show_suc.set(true);
        }

        @Override
        public void onADClicked() {
            String title = mLastAd != null ? mLastAd.getTitle() : "null";
            Log.e(TAG, "onADClicked(),adTitle="+title);
            int adType = mLastAd.isAppAd() ? 1 : 2;
            ADCallBackImpl adCallback = wrCallback.get();
            if(adCallback != null) {
                adCallback.onAdClick(ClickModel.getInstance(0, adType, adId, Adv_Type.gdt));
            }
        }

        @Override
        public void onADError(AdError adError) {
            FailModel fm = FailModel.toStr(adError.getErrorCode(),adError.getErrorMsg(),adId,Adv_Type.gdt);
            Log.e(TAG,"onADError(),err_msg="+fm.toFullMsg());
            ADCallBackImpl adCallback = wrCallback.get();
            if(adCallback != null) {
                adCallback.onAdFailed(fm);
            }
        }

        @Override
        public void onADStatusChanged() {
            Log.d(TAG, "onADStatusChanged()");
        }
    }
}
