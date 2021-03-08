package cn.yq.ad.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import cn.yq.ad.ADCallback;

/**
 * Created by liguo on 2019/1/26.
 * desc
 */
public class ADCallBackImpl implements ADCallback{
    private static final String TAG = cn.yq.ad.impl.ADCallBackImpl.class.getSimpleName();
    private final Set<ADCallback> cbList = new LinkedHashSet<>();

    ADCallBackImpl() {
    }

    public final void addCallBack(ADCallback cb) {
        if(cb != null) {
            if(cbList.contains(cb)){
                return;
            }
            this.cbList.add(cb);
//            Log.e(TAG,"addCallBack(),hasCode="+hashCode()+",cbList.size()="+cbList.size()+",cb="+cb.getClass().getName());
        }
    }

    public final void removeCallBack(ADCallback cb){
        if(cb != null) {
            if(cbList.contains(cb)) {
                this.cbList.remove(cb);
            }
//            Log.e(TAG,"removeCallBack(),hasCode="+hashCode()+",cbList.size()="+cbList.size()+",cb="+cb.getClass().getName());
        }
    }

    public final void removeAll(){
        this.cbList.clear();
//        Log.e(TAG,"removeAll(),hasCode="+hashCode()+",cbList.size()="+cbList.size());
    }

    public List<ADCallback> getCbList() {
        return new ArrayList<>(cbList);
    }

    @Override
    public void onAdPresent(cn.yq.ad.impl.PresentModel pm) {
//        Log.e(TAG,"onAdPresent(),hasCode="+hashCode()+",cbList.size()="+cbList.size());
        if(cbList.size() > 0) {
            for (ADCallback cb : cbList) {
               cb.onAdPresent(pm);
            }
        }
    }

    @Override
    public void onDisLike(cn.yq.ad.impl.PresentModel pm) {
        if(cbList.size() > 0) {
            for (ADCallback cb : cbList) {
                cb.onDisLike(pm);
            }
        }
    }

    @Override
    public void onAdFailed(cn.yq.ad.impl.FailModel fm) {
//        Log.e(TAG,"onAdFailed(),cbList.size()="+cbList.size());
        if(cbList.size() > 0) {
            for (ADCallback cb : cbList) {
                cb.onAdFailed(fm);
            }
        }
    }

    @Override
    public void onAdClick(cn.yq.ad.impl.ClickModel obj) {
//        Log.e(TAG,"onAdClick(),hasCode="+hashCode()+",cbList.size()="+cbList.size());
        if(cbList.size() > 0) {
            for (ADCallback cb : cbList) {
                cb.onAdClick(obj);
            }
        }
    }

    @Override
    public void onAdDismissed(DismissModel dm) {
//        Log.e(TAG,"onAdDismissed(),cbList.size()="+cbList.size());
        if(cbList.size() > 0) {
            for (ADCallback cb : cbList) {
                cb.onAdDismissed(dm);
            }
        }
    }

    @Override
    public void onADExposed(cn.yq.ad.impl.PresentModel pm) {
//        super.onADExposed(pm);
        if(cbList.size() > 0) {
            for (ADCallback cb : cbList) {
                cb.onADExposed(pm);
            }
        }
    }

    @Override
    public void onAdSkip(PresentModel pm) {
        if(cbList.size() > 0) {
            for (ADCallback cb : cbList) {
                cb.onAdSkip(pm);
            }
        }
    }
}
