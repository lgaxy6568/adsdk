package cn.yq.ad.gdt;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qq.e.ads.cfg.MultiProcessFlag;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;

import java.util.Locale;

import cn.yq.ad.Adv_Type;
import cn.yq.ad.AdConf;
import cn.yq.ad.impl.ADBaseImpl;
import cn.yq.ad.impl.ClickModel;
import cn.yq.ad.impl.DismissModel;
import cn.yq.ad.impl.ExtraKey;
import cn.yq.ad.impl.FailModel;
import cn.yq.ad.impl.PresentModel;


/**
 * Created by liguo on 2018/10/17.
 * desc
 */
public class SplashForGDT extends ADBaseImpl {
    private ViewGroup gdtContainer;
    private TextView tvSkip;
    private final String appId;
    private final String posId;
    private Activity act;
    SplashAD ad = null;
    private static final String TAG = SplashForGDT.class.getSimpleName();
    private int valid_height;
    SplashForGDT(Activity act, ViewGroup gdtContainer, TextView tvSkip, String appId, String posId) {
        this.gdtContainer = gdtContainer;
        this.tvSkip = tvSkip;
        this.appId = replaceTrim_R_N(appId);
        this.posId = replaceTrim_R_N(posId);
        this.act = act;
        try {
            this.valid_height = act.getResources().getDimensionPixelSize(R.dimen.gdt_ad_min_valid_height);
        } catch (Exception e) {
            e.printStackTrace();
            this.valid_height = 0;
        }
    }

    private String getAdvSizeType(){
        Bundle bd = getExtra();
        if(bd != null && bd.containsKey(ExtraKey.KP_AD_SIZE_TYPE_KEY)){
            return bd.getString(ExtraKey.KP_AD_SIZE_TYPE_KEY);
        }
        return ExtraKey.KP_AD_SIZE_TYPE_VALUE_QUAN_PING;
    }

    @Override
    public void load() {
        Log.e(TAG,"load(),appId=["+appId+"],adId=["+posId+"]");
        MultiProcessFlag.setMultiProcess(true);
        try {
            gdtContainer.bringToFront();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final int AD_TIME_OUT = getRequestTimeOutFromExtra();
        Log.e(TAG, "load(),kp_size_type="+getAdvSizeType()+",超时时间="+AD_TIME_OUT);
        ad = new SplashAD(act, tvSkip, posId, a_splashAdListener , AD_TIME_OUT);
        ad.fetchAdOnly();
    }

   final SplashADListener a_splashAdListener = new SplashADListener(){
       long lastAdTick = 0;
       @Override
       public void onADDismissed() {
           Log.e(TAG,"onADDismissed(),lastAdTick="+lastAdTick);
           defaultCallback.onAdDismissed(DismissModel.newInstance(posId, Adv_Type.gdt).put(ExtraKey.KP_AD_SIZE_TYPE_KEY,getAdvSizeType()));
       }

       @Override
       public void onNoAD(AdError adError) {
           FailModel fm = FailModel.toStr(adError.getErrorCode(),adError.getErrorMsg(),posId,Adv_Type.gdt);
           fm.put(ExtraKey.KP_AD_SIZE_TYPE_KEY,getAdvSizeType());
           Log.e(TAG,"onNoAD(),err_msg="+fm.toFullMsg());
           defaultCallback.onAdFailed(fm);
       }

       @Override
       public void onADPresent() {
           Log.e(TAG,"onADPresent(),valid_height="+valid_height);
       }

       @Override
       public void onADClicked() {
           Log.e(TAG,"onADClicked()");
           defaultCallback.onAdClick(ClickModel.getInstance(0,-1,posId,Adv_Type.gdt).put(ExtraKey.KP_AD_SIZE_TYPE_KEY,getAdvSizeType()));
       }

       @Override
       public void onADTick(long l) {
           lastAdTick = Math.round(l / 1000f);
           Log.e(TAG,"onADTick(),lastAdTick="+lastAdTick);
           if(tvSkip != null){
               String SKIP_TEXT = "%d s 跳过";
               String txt = String.format(Locale.getDefault(),SKIP_TEXT, Math.round(l / 1000f));
               tvSkip.setText(txt);
           }
       }

       @Override
       public void onADExposure() {
           Log.e(TAG,"onADExposure(),暴光成功");
           defaultCallback.onADExposed(PresentModel.getInstance(posId,Adv_Type.gdt).put(ExtraKey.KP_AD_SIZE_TYPE_KEY,getAdvSizeType()));
       }

       @Override
       public void onADLoaded(long l) {
           int gdt_container_height = gdtContainer != null ? gdtContainer.getHeight() : 0;
           Log.e(TAG,"onADLoaded(),广告加载成功,valid_height="+valid_height+",gdt_container_height="+gdt_container_height);
           defaultCallback.onAdPresent(PresentModel.getInstance(posId, Adv_Type.gdt).put(ExtraKey.KP_AD_SIZE_TYPE_KEY,getAdvSizeType()));
       }
   };

    @Override
    public Adv_Type getAdvType() {
        return Adv_Type.gdt;
    }

    @Override
    public AdConf getCfg() {
        AdConf bd = new AdConf();
        bd.setAppId(appId);
        bd.setAdId(posId);
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
        try {
            ad.showAd(gdtContainer);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "show(),errMsg="+e.getMessage());
        }
    }
}
