package cn.yq.demo;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.reflect.TypeToken;
import com.meishu.sdk.core.utils.LogUtil;

import java.util.concurrent.atomic.AtomicBoolean;

import cn.yq.ad.ADCallback;
import cn.yq.ad.Adv_Type;
import cn.yq.ad.StatCallbackByKaiPing;
import cn.yq.ad.impl.ClickModel;
import cn.yq.ad.impl.DismissModel;
import cn.yq.ad.impl.FailModel;
import cn.yq.ad.impl.PresentModel;
import cn.yq.ad.proxy.AdConfigs;
import cn.yq.ad.proxy.AdvProxyByKaiPing;
import cn.yq.ad.proxy.AsyncTask;
import cn.yq.ad.proxy.model.AdRespItem;
import cn.yq.ad.proxy.model.ExtraParams;
import cn.yq.ad.proxy.model.GetAdsModel;
import cn.yq.ad.proxy.model.GetAdsResponseListApiResult;
import cn.yq.ad.util.AdGsonUtils;
import cn.yq.ad.util.AdLogUtils;
import cn.yq.demo.http.OKHttpUtil;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 【开屏广告】测试
 */
public class SplashAdActivity extends AppCompatActivity implements ADCallback, StatCallbackByKaiPing {
    private static final String TAG = SplashAdActivity.class.getSimpleName();
    private static final String TAG_STAT = "STAT_KAI_PING";
    private ViewGroup adContainer;
    private static final AtomicBoolean abUseTestMode = new AtomicBoolean(false);
    public static String getConfigUrl(){
        final String url;
        if(abUseTestMode.get()){
            url = "http://adservice-test.sxyj.com/api/Ad/GetAds";
        }else{
            url = "https://adservice.sxyj.com/api/Ad/GetAds";
        }
        return url;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_splash);
        adContainer = findViewById(R.id.act_ad_splash_container_view);
        loadAdConfigs();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(adr != null) {
            adr.destroy();
        }
    }

    /**
     * 步骤1：加载广告配置~
     */
    private void loadAdConfigs(){
        AdConfigs.setDebugModel(true);
        new AsyncTask<GetAdsResponseListApiResult>(){
            @Override
            public GetAdsResponseListApiResult doInBackground() throws Exception {
                final String url = SplashAdActivity.getConfigUrl();
                GetAdsModel am = new GetAdsModel();
                am.setAppId("ecb693649cef10af");
                am.setVersion("1.2.9");
                am.setBrand("normol");
                String ps = AdGsonUtils.getGson().toJson(am);
                Request req = new Request.Builder().url(url).post(RequestBody.create(ps,MediaType.parse("application/json"))).build();
                return OKHttpUtil.execute(req,new TypeToken<GetAdsResponseListApiResult>(){}.getType());
            }

            @Override
            protected void onException(Exception e) {
                super.onException(e);
                AdLogUtils.e(TAG,"onException(),errMsg="+e.getMessage());
                Toast.makeText(getApplicationContext(),"广告配置加载失败",Toast.LENGTH_LONG).show();
            }

            @Override
            protected void onSuccess(GetAdsResponseListApiResult result) {
                super.onSuccess(result);
                AdLogUtils.i(TAG,"onSuccess(),result="+ AdGsonUtils.getGson().toJson(result));
                startLoadAd(result);
            }

        }.execute();
    }
    AdvProxyByKaiPing adr = null;
    private void startLoadAd(GetAdsResponseListApiResult result){
        //步骤3：开始加载广告
        ExtraParams extraParams = new ExtraParams();
        extraParams.setVip(false);
        adr = new AdvProxyByKaiPing(this,this,adContainer,null,result,extraParams);
        adr.setStatCallback(this);
        if(adr.isInited()){

        }else{
            LogUtil.e(TAG,"startLoadAd(),广告SDK初始化失败,errMsg="+adr.getErrMsg());
        }
        adr.load();
    }

    @Override
    public void onAdPresent(@NonNull PresentModel result) {
        AdLogUtils.i(TAG,"onAdPresent(),广告加载成功,adId="+result.getAdId()+",adType="+result.getAdvType());
    }

    @Override
    public void onAdFailed(@NonNull FailModel result) {
        AdLogUtils.e(TAG,"onAdFailed(),广告加载失败,adId="+result.getAdId()+",adType="+result.getAdvType());
        finish();
    }

    @Override
    public void onAdClick(@NonNull ClickModel result) {
        AdLogUtils.i(TAG,"onAdClick(),广告点击,adId="+result.getAdId()+",adType="+result.getAdvType());
    }

    @Override
    public void onAdDismissed(DismissModel result) {
        AdLogUtils.i(TAG,"onAdDismissed(),广告页关闭,adId="+result.getAdId()+",adType="+result.getAdvType());
        finish();
    }

    @Override
    public void onADExposed(PresentModel result) {
        AdLogUtils.i(TAG,"onADExposed(),广告曝光成功,adId="+result.getAdId()+",adType="+result.getAdvType());
    }

    @Override
    public void onDisLike(PresentModel result) {
        AdLogUtils.i(TAG,"onDisLike(),用户点击了不喜欢,adId="+result.getAdId()+",adType="+result.getAdvType());
    }

    @Override
    public void onAdSkip(PresentModel result) {
        AdLogUtils.i(TAG,"onAdSkip(),用户点击了跳过,adId="+result.getAdId()+",adType="+result.getAdvType());
        finish();
    }

    //================================埋点统计回调 begin=============================

    @Override
    public void callBackByOnAdStartLoad(String adId, Adv_Type adType, AdRespItem item) {
        AdLogUtils.i(TAG_STAT,"callBackByOnAdStartLoad(),开始加载广告,adId="+adId+",adType="+adType+",item.sort="+item.getSort());
    }

    @Override
    public void callBackByOnAdPresent(PresentModel pm) {
        AdLogUtils.i(TAG_STAT,"callBackByOnAdPresent(),广告加载成功,pm="+pm.getInfo());
    }

    @Override
    public void callBackByOnAdFailed(FailModel fm) {
        AdLogUtils.e(TAG_STAT,"callBackByOnAdFailed(),广告加载失败,fm="+fm.getInfo());
    }

    @Override
    public void callBackByOnADExposed(PresentModel pm) {
        AdLogUtils.i(TAG_STAT,"callBackByOnADExposed(),广告曝光成功,pm="+pm.getInfo());
    }

    @Override
    public void callBackByOnAdClick(ClickModel cm) {
        AdLogUtils.i(TAG_STAT,"callBackByOnAdClick(),点击了广告,cm="+cm.getInfo());
    }

    @Override
    public void callBackByOnAdDismissed(DismissModel dm) {
        AdLogUtils.i(TAG_STAT,"callBackByOnAdDismissed(),广告已关闭,dm="+dm.getInfo());
    }

    @Override
    public void callBackByOnAdSkip(PresentModel pm) {
        AdLogUtils.i(TAG_STAT,"callBackByOnAdSkip(),点击了跳过,pm="+pm.getInfo());
    }

    @Override
    public void callBackByOnDisLike(PresentModel pm) {
        AdLogUtils.i(TAG_STAT,"callBackByOnDisLike(),点击了不喜欢,pm="+pm.getInfo());
    }

    @Override
    public void callBackByOnAdAttachToWindow(PresentModel pm) {
        AdLogUtils.i(TAG_STAT,"callBackByOnAdAttachToWindow(),添加广告至窗口,pm="+pm.getInfo());
    }

    //================================埋点统计回调 end=============================
}
