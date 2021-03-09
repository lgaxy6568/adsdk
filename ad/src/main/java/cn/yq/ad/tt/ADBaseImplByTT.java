package cn.yq.ad.tt;

import android.app.Activity;
import android.content.Context;


import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import cn.yq.ad.Adv_Type;
import cn.yq.ad.AdConf;
import cn.yq.ad.AdNativeResponse;
import cn.yq.ad.ShowModel;
import cn.yq.ad.impl.ADBaseImpl;

/**
 * Created by liguo on 2018/12/10.
 * 文档地址：https://www.pangle.cn/union/media/union/download/detail?id=3&osType=android
 * desc
 */
public abstract class ADBaseImplByTT<T> extends ADBaseImpl {

    public final String DEFAULT_TITLE = "今日头条";
    protected final String appId;
    protected final String adId;
    protected final Context wrAct;
    private final WeakReference<Activity> weakReferenceAct;
    protected List<String> adIdLst;
    protected final String ids;
    ADBaseImplByTT(Activity act, String appId, String adId) {
        if(adId != null){
            adId = adId.trim();
        }
        if(appId != null){
            appId = appId.trim();
        }
        this.weakReferenceAct = new WeakReference<>(act);
        this.appId = appId;
        this.ids = adId;
        this.adId = adId;
        this.wrAct = getContextFromActivity(act);
    }

    @Override
    public void showTj(ShowModel sm) {
        if(sm != null) {
            sm.setAdv_type(Adv_Type.tt);
        }
    }

    public final Context getAct(){
        return wrAct;
    }
    public final Activity getWeakActivity(){
        return weakReferenceAct.get();
    }

    protected final Map<String, T> mNativeResponses = new LinkedHashMap<>();
    protected final Queue<AdNativeResponse> mQueue = new LinkedBlockingQueue<>();

    @Override
    public final Adv_Type getAdvType() {
        return Adv_Type.tt;
    }

    @Override
    public void destroy() {
        super.destroy();
        mNativeResponses.clear();
        mQueue.clear();
    }
    @Override
    public AdConf getCfg() {
        AdConf bd = new AdConf();
        bd.setAppId(appId);
        bd.setAdId(adId);
        bd.setAdIdLst(adIdLst);
        return bd;
    }

    @Override
    public int getDataSize() {
        return mQueue != null ? mQueue.size() : 0;
    }

}
