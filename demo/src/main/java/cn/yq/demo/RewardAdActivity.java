package cn.yq.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.reflect.TypeToken;

import cn.yq.ad.Adv_Type;
import cn.yq.ad.StatCallbackByRewardVideo;
import cn.yq.ad.VideoADCallback;
import cn.yq.ad.impl.ClickModel;
import cn.yq.ad.impl.DismissModel;
import cn.yq.ad.impl.FailModel;
import cn.yq.ad.impl.PresentModel;
import cn.yq.ad.impl.ShowParam;
import cn.yq.ad.proxy.AdConfigs;
import cn.yq.ad.proxy.AdvProxyByRewardVideo;
import cn.yq.ad.proxy.AsyncTask;
import cn.yq.ad.proxy.model.AdConstants;
import cn.yq.ad.proxy.model.AdRespItem;
import cn.yq.ad.proxy.model.GetAdsModel;
import cn.yq.ad.proxy.model.GetAdsResponseListApiResult;
import cn.yq.ad.util.AdGsonUtils;
import cn.yq.ad.util.AdLogUtils;
import cn.yq.demo.http.OKHttpUtil;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 【激励视频广告】测试
 */
public class RewardAdActivity extends AppCompatActivity implements VideoADCallback, StatCallbackByRewardVideo {
    private static final String TAG = RewardAdActivity.class.getSimpleName();
    private static final String TAG_STAT = "STAT_KAI_PING";
    private GetAdsResponseListApiResult apiResult;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_reward);
        loadAdConfigs();
    }

    public void handBtnStartReward(View vv){
        if(apiResult == null){
            Toast.makeText(this,"apiResult is null",Toast.LENGTH_LONG).show();
            return;
        }
        startLoadAd(apiResult);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(adr != null){
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
                apiResult = result;
            }

        }.execute();
    }

    private AdvProxyByRewardVideo adr = null;
    private void startLoadAd(GetAdsResponseListApiResult result){
        //步骤3：开始加载广告
        if(adr != null){
            adr.destroy();
        }
       adr = new AdvProxyByRewardVideo(this,this,result,null,AdConstants.LOCATION_BY_JLSP);
       adr.setStatCallback(this);
       adr.load();
    }

    @Override
    public void onAdPresent(@NonNull PresentModel result) {
        AdLogUtils.i(TAG,"onAdPresent(),广告加载成功,adId="+result.getAdId()+",adType="+result.getAdvType());
        ShowParam sp = new ShowParam(1);
        adr.show(null,sp);
    }

    @Override
    public void onAdFailed(@NonNull FailModel result) {
        Toast.makeText(this, "广告加载失败~", Toast.LENGTH_SHORT).show();
        AdLogUtils.e(TAG,"onAdFailed(),广告加载失败,adId="+result.getAdId()+",adType="+result.getAdvType()+",errMsg="+result.toFullMsg());
    }

    @Override
    public void onAdClick(@NonNull ClickModel result) {
        AdLogUtils.i(TAG,"onAdClick(),广告点击,adId="+result.getAdId()+",adType="+result.getAdvType());
    }

    @Override
    public void onAdDismissed(DismissModel result) {
        AdLogUtils.i(TAG,"onAdDismissed(),广告页关闭,adId="+result.getAdId()+",adType="+result.getAdvType());
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
    }

    @Override
    public void onVideoStartPlay(@NonNull PresentModel model) {
        AdLogUtils.i(TAG,"onVideoStartPlay(),result="+model.getInfo());
    }

    @Override
    public void onVideoPlayComplete(@NonNull PresentModel model) {
        AdLogUtils.i(TAG,"onVideoPlayComplete(),result="+model.getInfo());
    }

    @Override
    public void onRewardVerify(boolean rewardVerify, PresentModel model) {
        AdLogUtils.i(TAG,"onRewardVerify(),rewardVerify="+rewardVerify+",result="+model.getInfo());
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
    public void callBackOnVideoStartPlay(PresentModel model) {
        AdLogUtils.i(TAG_STAT,"callBackOnVideoStartPlay(),广告开始播放,model="+model.getInfo());
    }

    @Override
    public void callBackOnVideoPlayComplete(PresentModel model) {
        AdLogUtils.i(TAG_STAT,"callBackOnVideoPlayComplete(),广告播放完成,model="+model.getInfo());
    }

    @Override
    public void callBackOnRewardVerify(boolean rewardVerify, PresentModel model) {
        AdLogUtils.i(TAG_STAT,"callBackOnRewardVerify(),本次广告观看是否有效,rewardVerify="+rewardVerify+",model="+model.getInfo());
    }

    @Override
    public void callBackByOnAdAttachToWindow(PresentModel pm) {
        AdLogUtils.i(TAG_STAT,"callBackByOnAdAttachToWindow(),添加广告至窗口,pm="+pm.getInfo());
    }

    //================================埋点统计回调 end=============================
}
