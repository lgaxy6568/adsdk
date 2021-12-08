package cn.yq.ad.xm;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.ViewGroup;

import com.bx.xmsdk.XMSdk;

import cn.yq.ad.ADRunnable;
import cn.yq.ad.Adv_Type;
import cn.yq.ad.impl.BaseADFactoryImpl;
import cn.yq.ad.proxy.AdConfigs;

public class ADFactoryImplByXM extends BaseADFactoryImpl {
    @Override
    public void init(Context ctx, String... args) {
        XMSdk.setDebug(AdConfigs.isDebugModel());
        if(ctx instanceof Application){
            String appId = AdConfigs.getAppIdByType(Adv_Type.xm);
            String arr[] = appId.split("#");
            XMSdk.init((Application) ctx,arr[0],arr[1]);
        }
    }

    @Override
    public ADRunnable createRenderAdForXM(Activity act, String appId, String adId, ViewGroup adContainer) {
        return new RenderAdForXM(act,adContainer,appId,adId);
    }
}
