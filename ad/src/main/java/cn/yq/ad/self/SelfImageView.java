package cn.yq.ad.self;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.meishu.sdk.core.utils.LogUtil;

import java.util.concurrent.atomic.AtomicBoolean;


@SuppressLint("AppCompatCustomView")
public class SelfImageView extends ImageView {
    private final String TAG = SelfImageView.class.getSimpleName();
    public SelfImageView(Context context) {
        super(context);
    }

    public SelfImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SelfImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SelfImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mDestroyed.get()){
            return;
        }
        try {
            Drawable dd = getDrawable();
            if(dd == null){
                return;
            }
            if(dd instanceof BitmapDrawable){
                BitmapDrawable bd = (BitmapDrawable)dd;
                if(!bd.isVisible()){
                    return;
                }
                Bitmap bmp = bd.getBitmap();
                if(bmp == null || bmp.isRecycled() ){
                    return;
                }
            }
            super.onDraw(canvas);
        } catch (Exception e) {
            LogUtil.e(TAG,"onDraw(),errMsg="+e.getMessage());
        }
    }

    private final AtomicBoolean mDestroyed = new AtomicBoolean(false);
    public void destroy(){
        mDestroyed.set(true);
    }
}
