package cn.yq.ad;

import android.os.Bundle;
import android.view.View;

import java.util.List;
import java.util.Map;

/**
 * Created by liguo on 2018/10/16.
 * desc
 */
public interface ADRunnable {

    /** 获取一些配置信息 */
    AdConf getCfg();

    /** 产次加载 */
    void load();

    /** 销毁 */
    void destroy();

    /** 设置 */
    void addCallback(ADCallback callback);

    void removeCallBack(ADCallback callback);

    void  removeAll();

    /** 重新加载 */
    void reload();

    /** 发送展示 */
    void show(View view, Object obj);

    /** 客户端回调SDK */
    AdNativeResponse getAdvertEntity(String from, Map<String, String> map);

    /** 客户端回调SDK */
    View getAdvertEntityView(View view, Object obj);

    /** 2：广点通 3：百度 */
    Adv_Type getAdvType();

    List<ADCallback> getCallBackList();

    //=========不太重要的方法============
    void resume(Object obj);
    void pause(Object obj);
    void showTj(ShowModel sm);
    void setExtra(Bundle bd);
    Bundle getExtra();
}
