package cn.yq.adsdk;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.reflect.TypeToken;

import cn.yq.ad.ADRunnable;
import cn.yq.ad.impl.AbstractADCallback;
import cn.yq.ad.impl.ClickModel;
import cn.yq.ad.impl.DismissModel;
import cn.yq.ad.impl.FailModel;
import cn.yq.ad.impl.PresentModel;
import cn.yq.ad.proxy.AdConfigs;
import cn.yq.ad.proxy.AsyncTask;
import cn.yq.ad.proxy.model.GetAdsModel;
import cn.yq.ad.proxy.model.GetAdsResponseListApiResult;
import cn.yq.ad.util.AdLogUtils;
import cn.yq.ad.util.AdGsonUtils;
import cn.yq.adsdk.http.OKHttpUtil;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class SplashAdActivity extends AppCompatActivity implements AbstractADCallback {
    private static final String TAG = SplashAdActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_ad);
        // FIXME: 2021/3/8 注意事项
        /*
         * （1）确认包名及签名正确
         */
        loadAdConfigs();
    }

    /**
     * 步骤1：加载广告配置~
     */
    private void loadAdConfigs(){
        AdConfigs.setDebugModel(true);
        new AsyncTask<GetAdsResponseListApiResult>(){
            @Override
            public GetAdsResponseListApiResult doInBackground() throws Exception {
                final String url = "https://adservice.sxyj.com/api/Ad/GetAds";
                GetAdsModel am = new GetAdsModel();
                am.setAppId("ecb693649cef10af");
                am.setVersion("1.1.3");
                am.setBrand("oppo");
                String ps = AdGsonUtils.getGson().toJson(am);
                Request req = new Request.Builder().url(url).post(RequestBody.create(ps,MediaType.parse("application/json"))).build();
                return OKHttpUtil.execute(req,new TypeToken<GetAdsResponseListApiResult>(){}.getType());
            }

            @Override
            protected void onException(Exception e) {
                super.onException(e);
                AdLogUtils.e(TAG,"onException(),errMsg="+e.getMessage());
            }

            @Override
            protected void onSuccess(GetAdsResponseListApiResult result) {
                super.onSuccess(result);
                AdLogUtils.i(TAG,"onSuccess(),result="+ AdGsonUtils.getGson().toJson(result));
                startLoadAd(result);
            }

        }.execute();
    }

    private void startLoadAd(GetAdsResponseListApiResult result){
        //步骤2：初始化广告SDK
        AdConfigs.setAppIdByCSJ("5133118");
        AdConfigs.setAppIdByGDT("1111386088");
        new InitAdFactoryTask(this).run();

        //步骤3：开始加载广告
        ViewGroup adContainer = findViewById(R.id.act_ad_container_view);
        ADRunnable ad = new SplashAD(this,this,adContainer,null,result);
        ad.load();
    }

    @Override
    public void onAdPresent(@NonNull PresentModel result) {
        AdLogUtils.i(TAG,"onAdPresent()");
    }

    @Override
    public void onAdFailed(@NonNull FailModel result) {
        AdLogUtils.e(TAG,"onAdFailed()");
    }

    @Override
    public void onAdClick(@NonNull ClickModel result) {
        AdLogUtils.d(TAG,"onAdClick()");
    }

    @Override
    public void onAdDismissed(DismissModel dm) {
        AdLogUtils.d(TAG,"onAdDismissed()");
    }

    @Override
    public void onADExposed(PresentModel pm) {
        AdLogUtils.i(TAG,"onADExposed()");
    }

    @Override
    public void onDisLike(PresentModel pm) {
        AdLogUtils.d(TAG,"onDisLike()");
    }

    @Override
    public void onAdSkip(PresentModel pm) {
        AdLogUtils.d(TAG,"onAdSkip()");
    }
}
