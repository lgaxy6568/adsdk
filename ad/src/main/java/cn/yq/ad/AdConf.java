package cn.yq.ad;

import java.util.List;

/**
 * Created by liguo on 2018/12/6.
 * desc
 */
public class AdConf {
    private String appId;
    private String adId;
    private List<String> adIdLst;
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
}
