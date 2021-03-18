package cn.yq.ad.self;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.yq.ad.AdConf;
import cn.yq.ad.Adv_Type;
import cn.yq.ad.R;
import cn.yq.ad.impl.ADBaseImpl;
import cn.yq.ad.impl.ClickModel;
import cn.yq.ad.impl.DismissModel;
import cn.yq.ad.impl.FailModel;
import cn.yq.ad.impl.PresentModel;
import cn.yq.ad.proxy.model.AdRespItem;
import cn.yq.ad.util.AdLogUtils;

public class SplashForSelf extends ADBaseImpl implements View.OnClickListener {
    private ViewGroup gdtContainer;
    private final String appId;
    private final String posId;
    private Activity act;
    private static final String TAG = SplashForSelf.class.getSimpleName();

    public SplashForSelf(Activity act, ViewGroup gdtContainer, String appId, String posId) {
        this.gdtContainer = gdtContainer;
        this.appId = replaceTrim_R_N(appId);
        this.posId = replaceTrim_R_N(posId);
        this.act = act;
        this.rootView = LayoutInflater.from(act).inflate(R.layout.layout_splash_for_self,null);
        this.layoutSplashForSelfIv = rootView.findViewById(R.id.layout_splash_for_self_iv);
        this.layoutSplashForSkipView = rootView.findViewById(R.id.layout_splash_for_skip_view);
    }

    private TextView layoutSplashForSkipView;
    private ImageView layoutSplashForSelfIv;
    private View rootView;
    @Override
    public void onClick(View v) {
        if(v == layoutSplashForSkipView){
            isSkiped.set(true);
            defaultCallback.onAdSkip(PresentModel.getInstance(posId, getAdvType()).setAdRespItem(getAdParamItem()));
            return;
        }
        ClickModel cm = ClickModel.getInstance(0, -1, posId, getAdvType());
        cm.setAdRespItem(getAdParamItem());
        defaultCallback.onAdClick(cm);
    }

    @Override
    public void load() {
        AdRespItem param = getAdParamItem();
        if(param == null){
            AdLogUtils.i(TAG, "load(),param is null");
            return;
        }
        AdLogUtils.i(TAG, "load()");
        layoutSplashForSelfIv.setOnClickListener(this);
        layoutSplashForSkipView.setOnClickListener(this);
        Glide.with(act).load(param.getImageUrl()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                FailModel fm = FailModel.toStr(-1,"图片加载失败~",posId,getAdvType());
                Log.e(TAG,"onLoadFailed(),err_msg="+fm.toFullMsg());
                defaultCallback.onAdFailed(fm.setAdRespItem(getAdParamItem()));
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                Log.e(TAG,"onResourceReady()");
                layoutSplashForSelfIv.setImageDrawable(resource);
                defaultCallback.onAdPresent(PresentModel.getInstance(posId, getAdvType()).setAdRespItem(getAdParamItem()));
                return false;
            }
        }).preload();
    }

    @Override
    public Adv_Type getAdvType() {
        return Adv_Type.self;
    }

    @Override
    public final AdConf getCfg() {
        AdConf bd = new AdConf();
        bd.setAppId(appId);
        bd.setAdId(posId);
        bd.setAdRespItem(getAdParamItem());
        return bd;
    }

    private AdShowTimer adShowTimer;
    private final AtomicBoolean isSkiped = new AtomicBoolean(false);
    private final AtomicBoolean AD_VIEW_IS_ADD_FLAG = new AtomicBoolean(false);
    @Override
    public void show(View inView, Object obj) {
        if (gdtContainer == null) {
            Log.e(TAG, "show(),gdtContainer is null");
            return;
        }
        if(AD_VIEW_IS_ADD_FLAG.get()){
            return;
        }
        Log.e(TAG, "show()");
        ViewParent vp = rootView.getParent();
        if(vp instanceof ViewGroup){
            ((ViewGroup) vp).removeAllViews();
        }
        gdtContainer.addView(rootView);
        AD_VIEW_IS_ADD_FLAG.set(true);
        defaultCallback.onADExposed(PresentModel.getInstance(posId, getAdvType()).setAdRespItem(getAdParamItem()));
        if(adShowTimer != null){
            adShowTimer.cancel();
        }
        gdtContainer.post(() -> {
            int maxMillis = 6000;
            adShowTimer = new AdShowTimer(maxMillis,1000,this);
            updateSkipViewText(maxMillis/1000);
            adShowTimer.start();
        });

    }

    private static final String SKIP_TEXT = "%d s 跳过";
    private void updateSkipViewText(int num) {
        if(isSkiped.get()){
            return;
        }
        try {
            layoutSplashForSkipView.setText(String.format(Locale.getDefault(), SKIP_TEXT, num));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class AdShowTimer extends CountDownTimer{
        private final WeakReference<SplashForSelf> wrAdImpl;

        /** 是否已调用了Dismissed()方法 */
        private boolean isCalledDismissedMethod = false;
        public AdShowTimer(long millisInFuture, long countDownInterval,SplashForSelf splashForSelf) {
            super(millisInFuture, countDownInterval);
            this.wrAdImpl = new WeakReference<>(splashForSelf);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int num = (int)(millisUntilFinished / 1000L);
            SplashForSelf sfs =  wrAdImpl.get();
            if(sfs == null){
                return;
            }
            sfs.updateSkipViewText(num);
            if(sfs.isSkiped.get()){
                if(!isCalledDismissedMethod){
                    isCalledDismissedMethod = true;
                    sfs.defaultCallback.onAdDismissed(DismissModel.newInstance(sfs.posId, sfs.getAdvType()).setAdRespItem(sfs.getAdParamItem()));
                }
                cancel();
            }
        }

        @Override
        public void onFinish() {
            SplashForSelf sfs =  wrAdImpl.get();
            if(sfs == null){
                return;
            }
            if(!isCalledDismissedMethod){
                isCalledDismissedMethod = true;
                sfs.defaultCallback.onAdDismissed(DismissModel.newInstance(sfs.posId, sfs.getAdvType()).setAdRespItem(sfs.getAdParamItem()));
            }
        }
    }
}
