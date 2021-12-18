package cn.yq.ad.self;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
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
    private final ViewGroup gdtContainer;
    private final String appId;
    private final String posId;
    private final WeakReference<Activity> wrAct;
    private static final String TAG = SplashForSelf.class.getSimpleName();

    private final TextView layoutSplashForSkipView;
    private final SelfImageView layoutSplashForSelfIv;
    private final View rootView;
    public SplashForSelf(Activity act, ViewGroup gdtContainer, String appId, String posId) {
        this.gdtContainer = gdtContainer;
        this.appId = replaceTrim_R_N(appId);
        this.posId = replaceTrim_R_N(posId);
        this.wrAct = new WeakReference<>(act);
        this.rootView = LayoutInflater.from(act).inflate(R.layout.layout_splash_for_self,null);
        this.layoutSplashForSelfIv = rootView.findViewById(R.id.layout_splash_for_self_iv);
        this.layoutSplashForSkipView = rootView.findViewById(R.id.layout_splash_for_skip_view);
    }

    private boolean checkActIsDestroyed(){
        Activity act = wrAct.get();
        if(act == null){
            return true;
        }
        if(act.isDestroyed() || act.isFinishing()){
            return true;
        }
        return false;
    }

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
        Activity act = wrAct.get();
        if(act != null){
            Context ctx = act.getApplicationContext();
            if(ctx != null){
                Glide.with(ctx).load(param.getImageUrl())
                        .listener(new TempRequestListener(this))
                        .preload();
            }
        }
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
        Log.i(TAG, "show()");
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

    @Override
    public void destroy() {
        super.destroy();
        clearDrawable();
    }

    private void clearDrawable(){
        layoutSplashForSelfIv.destroy();
        layoutSplashForSelfIv.setImageDrawable(null);
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
        private boolean mDismissFlag = false;
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
            if(sfs.checkActIsDestroyed()){
                sfs.clearDrawable();
                return;
            }
            sfs.updateSkipViewText(num);
            if(sfs.isSkiped.get()){
                sfs.clearDrawable();
                if(!isDismissed()){
                    setDismissed(true);
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
            if(sfs.checkActIsDestroyed()){
                sfs.clearDrawable();
                return;
            }
            if(!isDismissed()){
                setDismissed(true);
                sfs.clearDrawable();
                sfs.defaultCallback.onAdDismissed(DismissModel.newInstance(sfs.posId, sfs.getAdvType()).setAdRespItem(sfs.getAdParamItem()));
            }
        }

        private boolean isDismissed(){
            return mDismissFlag;
        }
        private void setDismissed(Boolean bool){
            this.mDismissFlag = bool;
        }

    }

    private static class TempRequestListener implements RequestListener<Drawable>{
        private final WeakReference<SplashForSelf> wr;

        public TempRequestListener(SplashForSelf self) {
            this.wr = new WeakReference<>(self);
        }

        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            SplashForSelf self = wr.get();
            if(self != null){
                FailModel fm = FailModel.toStr(-1,"图片加载失败~",self.posId,self.getAdvType());
                Log.e(TAG,"onLoadFailed(),err_msg="+fm.toFullMsg());
                self.defaultCallback.onAdFailed(fm.setAdRespItem(self.getAdParamItem()));
            }

            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            Log.i(TAG,"onResourceReady()");
            SplashForSelf self = wr.get();
            if(self != null && resource != null){
                if(resource instanceof BitmapDrawable){
                    BitmapDrawable bd = (BitmapDrawable)resource;
                    Bitmap bmp = bd.getBitmap();
                    if(bmp == null || bmp.isRecycled()){

                    }else{
                        self.layoutSplashForSelfIv.setImageDrawable(resource);
                        self.defaultCallback.onAdPresent(PresentModel.getInstance(self.posId, self.getAdvType()).setAdRespItem(self.getAdParamItem()));
                    }
                }
            }

            return false;
        }
    }
}
