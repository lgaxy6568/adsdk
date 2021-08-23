package cn.yq.ad.bxm;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dhcw.sdk.BDAdvanceNativeRenderAd;
import com.dhcw.sdk.BDAdvanceNativeRenderItem;
import com.dhcw.sdk.BDAdvanceNativeRenderListener;
import com.dhcw.sdk.BDAppNativeOnClickListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.yq.ad.AdConf;
import cn.yq.ad.Adv_Type;
import cn.yq.ad.R;
import cn.yq.ad.impl.ADBaseImpl;
import cn.yq.ad.impl.ClickModel;
import cn.yq.ad.impl.DismissModel;
import cn.yq.ad.impl.FailModel;
import cn.yq.ad.impl.PresentModel;
import cn.yq.ad.util.AdLogUtils;


/**
 * Created by liguo on 2018/10/17.
 * desc
 */
public class RenderAdForBXM extends ADBaseImpl implements BDAdvanceNativeRenderListener,BDAppNativeOnClickListener, BDAdvanceNativeRenderListener.AdInteractionListener {
    private ViewGroup gdtContainer;
    private final String appId;
    private final String posId;
    private WeakReference<Activity> wrAct;
    private BDAdvanceNativeRenderAd ad = null;
    private static final String TAG = RenderAdForBXM.class.getSimpleName();
    public RenderAdForBXM(Activity act, ViewGroup gdtContainer, String appId, String posId) {
        this.gdtContainer = gdtContainer;
        this.appId = replaceTrim_R_N(appId);
        this.posId = replaceTrim_R_N(posId);
        this.wrAct = new WeakReference<>(act);
    }

    private boolean isValidAct(Activity act){
        if(act == null || act.isFinishing() || act.isDestroyed()){
            return false;
        }
        return true;
    }

    @Override
    public void load() {
        final int AD_TIME_OUT = getRequestTimeOutFromExtra();
        Log.e(TAG,"load(),appId=["+appId+"],adId=["+posId+"],超时时间="+AD_TIME_OUT);
        Activity act = wrAct.get();
        if(isValidAct(act)) {
            ad = new BDAdvanceNativeRenderAd(act, gdtContainer, posId);
            ad.setBdAdvanceNativeRenderListener(this);
            ad.loadAD();
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
    }

    @Override
    public void resume(Object obj) {
        super.resume(obj);
        if (bdAdvanceNativeRenderItem != null) {
            bdAdvanceNativeRenderItem.resume();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (bdAdvanceNativeRenderItem != null) {
            bdAdvanceNativeRenderItem.destroy();
        }
    }

    private BDAdvanceNativeRenderItem bdAdvanceNativeRenderItem;
    @Override
    public void onLoadAd(List<BDAdvanceNativeRenderItem> advanceFeedItemList) {
        defaultCallback.onAdPresent(PresentModel.getInstance(posId,getAdvType()).setAdRespItem(getAdParamItem()));
        bdAdvanceNativeRenderItem = advanceFeedItemList.get(0);
        setFeedAd(bdAdvanceNativeRenderItem);
        bindListener(bdAdvanceNativeRenderItem);
    }

    @Override
    public void onAdFailed() {
        FailModel fm = FailModel.toStr(-1,"未知",posId,getAdvType());
        Log.e(TAG,"onAdFailed(),err_msg="+fm.toFullMsg());
        defaultCallback.onAdFailed(fm.setAdRespItem(getAdParamItem()));
    }

    private void setFeedAd(BDAdvanceNativeRenderItem bdAdvanceFeedItem) {
        gdtContainer.removeAllViews();
        Activity act = wrAct.get();
        if(isValidAct(act)) {
            View inflateView = LayoutInflater.from(act).inflate(R.layout.bxm_layout_render_ad, gdtContainer);

            ImageView ivExpressAd = inflateView.findViewById(R.id.bxm_render_left_iv);
            TextView tvExpressTitle = inflateView.findViewById(R.id.bxm_render_title_tv);
//            TextView tvExpressSubTitle = inflateView.findViewById(R.id.bxm_tv_express_subtitle);

            String imgUrl = "";
            List<String> imgLst = bdAdvanceFeedItem.getImageList();
            if(imgLst != null && imgLst.size() > 0){
                imgUrl = imgLst.get(0);
            }
            AdLogUtils.i(TAG,"setFeedAd(),imgUrl="+imgUrl);
            if(imgUrl != null && imgUrl.trim().length() > 0){
                Glide.with(act).load(imgUrl).into(ivExpressAd);
            }

            tvExpressTitle.setText(bdAdvanceFeedItem.getDescription());
//            tvExpressSubTitle.setText(bdAdvanceFeedItem.getTitle());
        }
    }

    private void bindListener(final BDAdvanceNativeRenderItem bdAdvanceFeedItem) {
        //如果是自定义button
        List<View> clickViewList = new ArrayList<>();
        clickViewList.add(gdtContainer);
        bdAdvanceFeedItem.registerViewForInteraction(gdtContainer, clickViewList,this);
        //如果不需要本地播放视频，请不要设置
        bdAdvanceFeedItem.registerBdAppNativeOnClickListener(this);
    }

    @Override
    public void onActivityClosed() {
        Log.e(TAG,"onActivityClosed()");
        defaultCallback.onAdDismissed(DismissModel.newInstance(posId, getAdvType()).setAdRespItem(getAdParamItem()));
    }

    @Override
    public void onClick(int type, String adid) {
        Log.e(TAG, "onClick(),type="+type+",adid="+adid);
        ClickModel cm = ClickModel.getInstance(0,-1,posId,getAdvType()).setAdRespItem(getAdParamItem());
        Map<String,String> mmp = new LinkedHashMap<>();
        mmp.put("type",String.valueOf(type));
        cm.setExtMap(mmp);
        cm.setData(bdAdvanceNativeRenderItem);
        defaultCallback.onAdClick(cm);
    }

    @Override
    public void onAdClicked(View view) {
        Log.e(TAG,"onAdClicked()");
        defaultCallback.onAdClick(ClickModel.getInstance(0,-1,posId,getAdvType()).setAdRespItem(getAdParamItem()));
    }

    @Override
    public void onAdShow() {
        Log.e(TAG,"onAdShow()");
        defaultCallback.onADExposed(PresentModel.getInstance(posId,getAdvType()).setAdRespItem(getAdParamItem()));
    }
}
