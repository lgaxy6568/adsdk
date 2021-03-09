package cn.yq.ad.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by liguo on 2019/1/28.
 * desc
 * https://blog.csdn.net/csdn_conda/article/details/80097885
 */
public class SizeUtil {

    public static AdSize getScreenSize(Context context){
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        float density = dm.density;
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        AdSize sz = new AdSize(width,height);
        sz.setDensity(density);
        return sz;
    }

    /** dp转px */
    public static int dip2px(Context context, float dpValue) {
        if(dpValue == 0){
            return 0;
        }
        if(context == null){
            return (int)(dpValue * 3);
        }
        try {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        } catch (Exception e) {
            return (int)(dpValue * 3);
        }
    }

    /** px转dp */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
