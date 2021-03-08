package cn.yq.adsdk;

import android.content.Context;

import cn.yq.ad.ADFactory;
import cn.yq.ad.ADUtil;

/**
 * 初始化广告SDK
 */
public class InitAdFactoryTask implements Runnable {

    private final Context mCtx;

    public InitAdFactoryTask(Context mCtx) {
        this.mCtx = mCtx.getApplicationContext();
    }

    @Override
    public void run() {
        try {
            ADFactory af = ADUtil.getFactoryByGDT();
            if(af != null){
                af.init(mCtx);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ADFactory af = ADUtil.getFactoryByTT();
            if(af != null){
                af.init(mCtx);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
