package cn.yq.ad.bxm;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.dhcw.sdk.BDAdvanceCloseViewListener;
import com.dhcw.sdk.BDAdvanceFloatIconAd;
import com.dhcw.sdk.BDAdvanceFloatIconListener;
import com.dhcw.sdk.BDAppNativeOnClickListener;

import cn.yq.ad.AdConf;
import cn.yq.ad.Adv_Type;
import cn.yq.ad.impl.ADBaseImpl;
import cn.yq.ad.impl.ClickModel;
import cn.yq.ad.impl.DismissModel;
import cn.yq.ad.impl.FailModel;
import cn.yq.ad.impl.PresentModel;


/**
 * Created by liguo on 2018/10/17.
 * desc
 */
public class FloatAdForBXM extends ADBaseImpl implements BDAdvanceFloatIconListener, BDAdvanceCloseViewListener, BDAppNativeOnClickListener {
    private ViewGroup gdtContainer;
    private final String appId;
    private final String posId;
    private Activity act;
    BDAdvanceFloatIconAd ad = null;
    private static final String TAG = FloatAdForBXM.class.getSimpleName();
    public FloatAdForBXM(Activity act, ViewGroup gdtContainer, String appId, String posId) {
        this.gdtContainer = gdtContainer;
        this.appId = replaceTrim_R_N(appId);
        this.posId = replaceTrim_R_N(posId);
        this.act = act;
    }

    @Override
    public void onActivityClosed() {
        Log.e(TAG,"onActivityClosed()");
        defaultCallback.onAdDismissed(DismissModel.newInstance(posId, getAdvType()).setAdRespItem(getAdParamItem()));
    }

    @Override
    public void onAdShow() {
        Log.e(TAG,"onAdShow()");
        defaultCallback.onADExposed(PresentModel.getInstance(posId,getAdvType()).setAdRespItem(getAdParamItem()));
    }

    @Override
    public void onAdFailed() {
        FailModel fm = FailModel.toStr(-1,"未知",posId,getAdvType());
        Log.e(TAG,"onAdFailed(),err_msg="+fm.toFullMsg());
        defaultCallback.onAdFailed(fm.setAdRespItem(getAdParamItem()));
    }

    @Override
    public void onAdClicked() {
        Log.e(TAG,"onAdClicked()");
        defaultCallback.onAdClick(ClickModel.getInstance(0,-1,posId,getAdvType()).setAdRespItem(getAdParamItem()));
    }

    @Override
    public void load() {
        final int AD_TIME_OUT = getRequestTimeOutFromExtra();
        Log.e(TAG,"load(),appId=["+appId+"],adId=["+posId+"],超时时间="+AD_TIME_OUT);
        ad = new BDAdvanceFloatIconAd(act, gdtContainer,posId);
        ad.setBdAdvanceFloatIconListener(this);
        ad.setBdAppNativeOnClickListener(this);
        ad.setBdAdvanceCloseViewListener(this);
        ad.loadAd();
    }

    @Override
    public void onClosed() {
        Log.e(TAG, "onClosed()");
        defaultCallback.onAdDismissed(DismissModel.newInstance(posId, getAdvType()).setAdRespItem(getAdParamItem()));
    }

    @Override
    public void onClick(int type, String adid) {
        Log.e(TAG, "onClick(),type="+type+",adid="+adid);
//        defaultCallback.onAdClick(ClickModel.getInstance(0,-1,posId,getAdvType()).setAdRespItem(getAdParamItem()));
        //type=1 加载视频 type=2 播放视频
        if (type == 1) {
            //加载视频 绑定激励视频回调
            TestPlayVideo.getInstance().load(act, ad);
        } else if (type == 2) {
            //播放视频
            TestPlayVideo.getInstance().play(act);
        }
    }

    @Override
    public final Adv_Type getAdvType() {
        return Adv_Type.bxm;
    }

    @Override
    public final AdConf getCfg() {
        AdConf bd = new AdConf();
        bd.setAppId(appId);
        bd.setAdId(posId);
        bd.setAdRespItem(getAdParamItem());
        return bd;
    }

    @Override
    public void show(View inView, Object obj) {
        if(ad == null){
            Log.e(TAG, "show(),ad is null");
            return;
        }
        if(gdtContainer == null){
            Log.e(TAG, "show(),gdtContainer is null");
            return;
        }
        Log.e(TAG, "show()");
//        try {
//            ad.showAd(gdtContainer);
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e(TAG, "show(),errMsg="+e.getMessage());
//        }
    }

    @Override
    public void destroy() {
        super.destroy();
        Log.e(TAG, "destroy()");
        if (ad != null) {
            ad.destroyAd();
        }
    }
}
