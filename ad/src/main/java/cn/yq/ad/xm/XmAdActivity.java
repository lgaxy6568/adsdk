package cn.yq.ad.xm;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.bx.xmsdk.CampaignCallback;
import com.bx.xmsdk.CampaignFragment;
import com.google.gson.Gson;

import cn.yq.ad.R;
import cn.yq.ad.proxy.AdConfigs;


public class XmAdActivity extends AppCompatActivity {

    private static final String TAG = "Main2Activity";
    private ViewGroup bannerMiddle, bannerBottom;
    private CampaignFragment campaignFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xm);

        bannerMiddle = findViewById(R.id.bannerMiddle);
        bannerBottom = findViewById(R.id.bannerBottom);
        initView();

    }

    private void initView() {
        // TODO: 2021/9/22 demo中 为了方便测试,写死用户id.app对接时注意替换
        String userID = AdConfigs.getExtParams().getUsrId();
        CampaignFragment fragment = CampaignFragment.newInstance(userID);
        // TODO: 2021/9/22 替换成app自己的placeID.如果没有,请咨询小满运营同学
        fragment.setPlaceId(getIntent().getStringExtra("placeId"));
        //根据自身广告平台接入情况，加载对应的广告平台广告
        //1穿山甲，2广点通，3快手，4百度。英文逗号分割。必传 adSources 媒体按自己当前版本对接的广告平台传
        fragment.setAdSources("1,2,3,4");
        fragment.setCallback(new CampaignCallback() {
            @Override
            public void showAd(String s) {
                //根据bean.adType来判断展示广告的平台 1-穿山甲 2-广点通 3-快手
                //pid: 对应平台的代码位
                JsBridgeBean bean = new Gson().fromJson(s, JsBridgeBean.class);
                Log.d(TAG, "展示激励视频广告(必须实现)");
                switch (bean.adType) {
                    case "1"://穿山甲激励视频
                        // TODO: 2021/9/22 为了方便测试,demo中写死.媒体对接注意删除这行代码.
                        bean.pid = "945734106";
                        CSJTools.loadBytedanceAd(XmAdActivity.this, campaignFragment, bean);
                        break;
                    case "2"://广点通激励视频
                        // TODO: 2021/9/22 为了方便测试,demo中写死.媒体对接注意删除这行代码.
                        bean.pid = "7051968206816081";
                        GDTTools.loadGDTRewardVideo(XmAdActivity.this, campaignFragment, bean);
                        break;
                    case "19"://穿山甲插屏
                        // TODO: 2021/9/22 为了方便测试,demo中写死.媒体对接注意删除这行代码.
                        bean.pid = "946152897";
                        CSJTools.loadCSJInterActionAd(XmAdActivity.this, campaignFragment, bean);
                        break;
                    case "20"://广点通插屏
                        // TODO: 2021/9/22 为了方便测试,demo中写死.媒体对接注意删除这行代码.
                        bean.pid = "7021791029351348";
                        GDTTools.loadGDTInterActionAd(XmAdActivity.this, campaignFragment, bean);
                        break;
                }

            }

            @Override
            public void showBanner(String params) {
                Log.d(TAG, "展示banner类型的广告(可选)");
                Toast.makeText(XmAdActivity.this, params, Toast.LENGTH_LONG).show();
                JsBridgeBean bean = new Gson().fromJson(params, JsBridgeBean.class);
                if (TextUtils.isEmpty(bean.pid)) {
                    return;
                }
                switch (bean.adType) {
                    case "4"://穿山甲底部Banner
                        // TODO: 2021/9/22 为了方便测试,demo中写死.媒体对接注意删除这行代码.
                        bean.pid = "945853767";
                        CSJTools.loadCSJBannerAd(XmAdActivity.this, bannerBottom, fragment, bean);
                        break;
                    case "5"://广点通底部Banner
                        // TODO: 2021/9/22 为了方便测试,demo中写死.媒体对接注意删除这行代码.
                        bean.pid = "b6037baf1d7219";
                        GDTTools.loadGDTBannerAd(XmAdActivity.this, bannerBottom, fragment, bean);
                        break;
                    case "13"://穿山甲横幅
                        // TODO: 2021/9/22 为了方便测试,demo中写死.媒体对接注意删除这行代码.
                        bean.pid = "945853767";
                        CSJTools.loadCSJBannerAd(XmAdActivity.this, bannerMiddle, fragment, bean);
                        break;
                    case "14"://广点通横幅
                        // TODO: 2021/9/22 为了方便测试,demo中写死.媒体对接注意删除这行代码.
                        bean.pid = "9002705700827826";
                        GDTTools.loadGDTNativeExpressAd(XmAdActivity.this, bannerMiddle, fragment, bean);
                        break;
                }

            }

            @Override
            public void hideBanner(String params) {
                Log.d(TAG, "隐藏banner类型的广告,和展示banner类型的广告配套使用");
                if (bannerBottom.getChildCount() > 0) {
                    bannerBottom.removeAllViews();
                }
                if (bannerMiddle.getChildCount() > 0) {
                    bannerMiddle.removeAllViews();
                }
                if (GDTTools.unifiedBannerView != null) {
                    GDTTools.unifiedBannerView.destroy();
                    GDTTools.unifiedBannerView = null;
                }
            }

            @Override
            public void openPage(String s, String s1) {
                Log.d(TAG, "打开页面,页面链接为:" + s + "\n" + "回调为:" + s1);
            }

            @Override
            public void onReceivedTitle(String s) {
                Log.d(TAG, "活动标题:" + s);
            }

            @Override
            public void onProgressChanged(int i) {
                Log.d(TAG, "加载进度:" + i);
            }
        });

        campaignFragment = fragment;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frame_layout, fragment);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
//        campaignFragment.openNfGame("","");
        Toast.makeText(XmAdActivity.this,"按下返回键onBackPressed",Toast.LENGTH_LONG).show();
        if (campaignFragment != null) {
            campaignFragment.backButtonClick(new CampaignFragment.CallBack() {
                @Override
                public void onSuccess(String jsonData) {

                    XmAdActivity.super.onBackPressed();
                }

                @Override
                public void onFailure(String code, String errmsg) {

                }
            });
        } else {
            super.onBackPressed();
        }
    }
}
