package cn.yq.ad.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class MyGlideUtils {
    private final Context ctx;
    private MyGlideUtils(Context ctx) {
        this.ctx = ctx.getApplicationContext();
    }

    public static MyGlideUtils get(Context ctx){
        return new MyGlideUtils(ctx);
    }

    public void show(String url, ImageView iv){
        fillRequestBuilder(manager().load(url)).into(iv);
    }

    private <T> RequestBuilder<T> fillRequestBuilder(RequestBuilder<T> builder){
        return builder.diskCacheStrategy(DiskCacheStrategy.NONE);
    }

    private RequestManager manager(){
        return Glide.with(ctx);
    }

}
