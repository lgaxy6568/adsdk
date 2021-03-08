package cn.yq.ad.tt.config;

import android.content.Context;
import android.util.Log;

import com.bytedance.sdk.openadsdk.TTAdManager;

import java.lang.ref.WeakReference;

/**
 * Created by liguo on 2018/12/3.
 * desc
 */
public class TTUtil {
    private static final String TAG = TTUtil.class.getSimpleName();
    private WeakReference<Context> wrCtx;
    private volatile TTAdManager adManager;
    private TTUtil() {
    }
    private static class TTUFactory{
        private static final TTUtil ins = new TTUtil();
    }

    public static TTUtil get(){
        return TTUFactory.ins;
    }

    public void init(Context ctx){
        if(ctx == null){
            return;
        }
        wrCtx = new WeakReference<>(ctx);
        initAdManager(ctx);
    }

    private void initAdManager(Context ctx){
        if(ctx == null){
            return;
        }
        if(adManager == null){
            synchronized (this){
                if(adManager == null){
                    try {
                        adManager = TTAdManagerHolder.getInstance(ctx);
                        if(adManager != null){
                            Log.d(TAG,"initAdManager(),sdkVersion="+adManager.getSDKVersion());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public TTAdManager getAdManager(){
        initAdManager(wrCtx.get());
        return adManager;
    }

}
