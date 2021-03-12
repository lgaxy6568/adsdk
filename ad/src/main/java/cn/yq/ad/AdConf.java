package cn.yq.ad;

import java.util.List;

import cn.yq.ad.proxy.model.AdRespItem;

/**
 * Created by liguo on 2018/12/6.
 * desc
 */
public class AdConf {
    private String appId;
    private String adId;
    private List<String> adIdLst;
    private AdRespItem adRespItem;
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public List<String> getAdIdLst() {
        return adIdLst;
    }

    public void setAdIdLst(List<String> adIdLst) {
        this.adIdLst = adIdLst;
    }

    public AdRespItem getAdRespItem() {
        return adRespItem;
    }

    public void setAdRespItem(AdRespItem adRespItem) {
        this.adRespItem = adRespItem;
    }
}
