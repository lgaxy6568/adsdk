package cn.yq.demo;

import android.content.Context;

import cn.yq.ad.ADFactory;
import cn.yq.ad.ADUtils;

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
            ADFactory af = ADUtils.getFactoryByGDT();
            if(af != null){
                af.init(mCtx);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ADFactory af = ADUtils.getFactoryByTT();
            if(af != null){
                af.init(mCtx);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
