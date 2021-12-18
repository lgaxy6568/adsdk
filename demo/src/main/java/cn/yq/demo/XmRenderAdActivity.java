package cn.yq.demo;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

import cn.yq.ad.ADCallback;
import cn.yq.ad.ADRunnable;
import cn.yq.ad.ADUtils;
import cn.yq.ad.impl.ClickModel;
import cn.yq.ad.impl.DismissModel;
import cn.yq.ad.impl.FailModel;
import cn.yq.ad.impl.PresentModel;
import cn.yq.ad.proxy.AdConfigs;
import cn.yq.ad.proxy.AsyncTask;
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
 * 【小满广告】测试
 */
public class XmRenderAdActivity extends AppCompatActivity implements ADCallback {
    private static final String TAG = XmRenderAdActivity.class.getSimpleName();
    private static final String TAG_STAT = "STAT_FLOAT";
    private ViewGroup adContainer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_float);
        adContainer = findViewById(R.id.fl_ad_container);
        loadAdConfigs();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adr != null) {
            adr.resume(null);
        }
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
                am.setVersion("1.2.6");
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
    ADRunnable adr = null;
    private void startLoadAd(GetAdsResponseListApiResult result){
        //步骤3：开始加载广告
        // TODO: 2021/12/7 确认广告ID WAIT_LIGUO
        String adId = "8860";
        adr = ADUtils.getRenderAdByXM(this,adContainer,"",adId,this);
        if(adr != null) {
            adr.load();
        }
    }

    @Override
    public void onAdPresent(@NonNull PresentModel result) {
        AdLogUtils.i(TAG,"onAdPresent(),广告加载成功,adId="+result.getAdId()+",adType="+result.getAdvType());
    }

    @Override
    public void onAdFailed(@NonNull FailModel result) {
        AdLogUtils.e(TAG,"onAdFailed(),广告加载失败,adId="+result.getAdId()+",adType="+result.getAdvType());
        ToastUtils.showShort("加载失败(),errMsg="+result.getMsg());
    }

    @Override
    public void onAdClick(@NonNull ClickModel result) {
        AdLogUtils.i(TAG,"onAdClick(),广告点击,adId="+result.getAdId()+",adType="+result.getAdvType());
        Object data = result.getData();
        Map<String,String> extMap = result.getExtMap();

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

}
