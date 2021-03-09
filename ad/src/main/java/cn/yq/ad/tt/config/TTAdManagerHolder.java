package cn.yq.ad.tt.config;

import android.content.Context;
import android.util.Log;

import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdSdk;

import java.util.concurrent.atomic.AtomicBoolean;

import cn.yq.ad.Adv_Type;
import cn.yq.ad.proxy.AdConfigs;

/**
 * 可以用一个单例来保存TTAdManager实例，在需要初始化sdk的时候调用
 *
 */
public class TTAdManagerHolder {
    private static final String TAG = TTAdManagerHolder.class.getSimpleName();
    private static final AtomicBoolean sInit = new AtomicBoolean(false);
    private static volatile TTAdManager adManager;
    public static TTAdManager getInstance(Context context) {
        if(sInit.get()){
            return adManager;
        }
        if(adManager == null){
            synchronized (TTAdManagerHolder.class){
                if(adManager == null){
                    try {
                        TTAdConfig adConfig = buildAdConfig(context);
                        adManager = TTAdSdk.init(context,adConfig);
                        if(adManager != null) {
                            String sdkVer = adManager.getSDKVersion();
                            Log.e(TAG, "getInstance(),appId=" + adConfig.getAppId()+",appName="+adConfig.getAppName()+",sdkVer="+sdkVer);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        sInit.set(true);
                    }
                }
            }
        }
       return adManager;
    }

    private static TTAdConfig buildAdConfig(Context ctx){
        String appName = "倒数321";
        return new TTAdConfig.Builder()
                .appId(AdConfigs.getAppIdByType(Adv_Type.tt))
                .useTextureView(true) //使用TextureView控件播放视频,默认为SurfaceView,当有SurfaceView冲突的场景，可以使用TextureView
                .appName(appName)
                .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)
                .allowShowNotify(true) //是否允许sdk展示通知栏提示
                .allowShowPageWhenScreenLock(true) //是否在锁屏场景支持展示广告落地页
                //.debug(true) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI, TTAdConstant.NETWORK_STATE_3G) //允许直接下载的网络状态集合
                .supportMultiProcess(true) //是否支持多进程，true支持
                .needClearTaskReset()
                //.httpStack(new MyOkStack3())//自定义网络库，demo中给出了okhttp3版本的样例，其余请自行开发或者咨询工作人员。
                .build();
    }
}
