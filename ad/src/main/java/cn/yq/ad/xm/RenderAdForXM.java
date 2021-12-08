package cn.yq.ad.xm;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bx.xmsdk.XMSdk;
import com.bx.xmsdk.bean.MaterialBean;
import com.bx.xmsdk.util.MaterialTm;

import java.lang.ref.WeakReference;

import cn.yq.ad.AdConf;
import cn.yq.ad.Adv_Type;
import cn.yq.ad.R;
import cn.yq.ad.impl.ADBaseImpl;
import cn.yq.ad.impl.ClickModel;
import cn.yq.ad.impl.FailModel;
import cn.yq.ad.impl.PresentModel;
import cn.yq.ad.util.AdStringUtils;


/**
 * Created by liguo on 2018/10/17.
 * desc
 */
public class RenderAdForXM extends ADBaseImpl implements MaterialTm.Callback ,View.OnClickListener{
    private ViewGroup gdtContainer;
    private final String appId;
    private final String posId;
    private WeakReference<Activity> wrAct;
    private static final String TAG = RenderAdForXM.class.getSimpleName();
    private final MaterialTm tm = new MaterialTm();
    public RenderAdForXM(Activity act, ViewGroup gdtContainer, String appId, String posId) {
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

    // TODO: 2021/12/7 确认UserID WAIT_LIGUO
    private String consumerId = "xxx"; 
    
    @Override
    public void load() {
        final int AD_TIME_OUT = getRequestTimeOutFromExtra();
        Log.e(TAG,"load(),appId=["+appId+"],adId=["+posId+"],超时时间="+AD_TIME_OUT);
        Activity act = wrAct.get();
        if(isValidAct(act)) {
            tm.loadMaterialData(consumerId,posId,this);
        }
    }


    @Override
    public final Adv_Type getAdvType() {
        return Adv_Type.xm;
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
        if(gdtContainer == null){
            Log.e(TAG, "show(),gdtContainer is null");
            return;
        }
        Log.e(TAG, "show()");
    }

    @Override
    public void resume(Object obj) {
        super.resume(obj);
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    private MaterialBean adMb;
    @Override
    public void onSuccess(MaterialBean mb) {
        if(mb == null){
            return;
        }
        this.adMb = mb;
        String imgUrl = mb.materialPath;
        if(AdStringUtils.isEmpty(imgUrl)){
            return;
        }
        Activity act = wrAct.get();
        if(act != null){
            Context ctx = act.getApplicationContext();
            if(ctx != null){
                Glide.with(ctx).load(imgUrl)
                        .listener(new TempRequestListener(this))
                        .preload();
            }
        }
    }

    @Override
    public void onFailure(String code, String errMsg) {

    }

    @Override
    public void onClick(View v) {
        if(adMb == null){
            return;
        }
        XMSdk.click(consumerId,posId,adMb.placeMaterialId,adMb.materialId);
        defaultCallback.onAdClick(ClickModel.getInstance(0,-1,posId,getAdvType()).setAdRespItem(getAdParamItem()));
    }

    private void bindAd(Drawable resource){
        if(adMb == null || resource == null){
            return;
        }
        gdtContainer.removeAllViews();
        View inflateView = LayoutInflater.from(wrAct.get()).inflate(R.layout.layout_adv_for_xm_render, gdtContainer);
        ImageView iv = inflateView.findViewById(R.id.layout_adv_for_xm_render_iv);
        iv.setImageDrawable(resource);
        inflateView.setOnClickListener(this);
        XMSdk.exposure(consumerId,posId,adMb.placeMaterialId,adMb.materialId);
        defaultCallback.onADExposed(PresentModel.getInstance(posId,getAdvType()).setAdRespItem(getAdParamItem()));
    }

    private static class TempRequestListener implements RequestListener<Drawable> {
        private final WeakReference<RenderAdForXM> wr;
        public TempRequestListener(RenderAdForXM self) {
            this.wr = new WeakReference<>(self);
        }

        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            RenderAdForXM self = wr.get();
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
            RenderAdForXM self = wr.get();
            if(self != null && resource != null){
                if(resource instanceof BitmapDrawable){
                    BitmapDrawable bd = (BitmapDrawable)resource;
                    Bitmap bmp = bd.getBitmap();
                    if(bmp == null || bmp.isRecycled()){

                    }else{
                        self.bindAd(resource);
                        self.defaultCallback.onAdPresent(PresentModel.getInstance(self.posId, self.getAdvType()).setAdRespItem(self.getAdParamItem()));
                    }
                }
            }

            return false;
        }
    }
}
