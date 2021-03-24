package cn.yq.ad.proxy.model;

import java.util.ArrayList;
import java.util.List;

import cn.yq.ad.impl.ADBaseImpl;
import cn.yq.ad.util.AdStringUtils;

public class AdResponse {
    private int widget;
    private int sort;

    private String type;

    private String adPartnerKey;

    private String adPartnerAppId;

    private List<String> adPartnerAdId;

    private String title;

    private String imageUrl;

    private String url;

    private String fire;

    private String expire;

    public AdResponse() {
    }

    private AdRespItem toItem(String adId) {
        return new AdRespItem(this.widget, this.sort, this.type, this.adPartnerKey, this.adPartnerAppId, adId, this.title, this.imageUrl, this.url, this.fire, this.expire);
    }
    public List<AdRespItem> toLst(){
        List<AdRespItem> tmpLst = new ArrayList<>();
        if(adPartnerAdId != null && adPartnerAdId.size() > 0){
            for (String s : adPartnerAdId) {
                tmpLst.add(toItem(s));
            }
        }
        if(AdConstants.SDK_TYPE_BY_SELF.equalsIgnoreCase(type)){
            AdRespItem item = toItem("人工配置_"+title);
            if(AdStringUtils.isEmpty(item.getAdPartnerKey())){
                item.setAdPartnerKey(AdConstants.SDK_TYPE_BY_SELF);
            }
            if(AdStringUtils.isEmpty(item.getAdPartnerAppId())){
                item.setAdPartnerAppId("xxx");
            }
            if(AdStringUtils.isEmpty(item.getAdId())){
                item.setAdId("xxx");
            }
            tmpLst.add(item);
        }
        return tmpLst;
    }

    public int getWidget() {
        return widget;
    }

    public void setWidget(int widget) {
        this.widget = widget;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAdPartnerKey() {
        return adPartnerKey;
    }

    public void setAdPartnerKey(String adPartnerKey) {
        this.adPartnerKey = adPartnerKey;
    }

    public String getAdPartnerAppId() {
        return ADBaseImpl.replaceTrim_R_N(adPartnerAppId);
    }

    public void setAdPartnerAppId(String adPartnerAppId) {
        this.adPartnerAppId = adPartnerAppId;
    }

    public List<String> getAdPartnerAdId() {
        return adPartnerAdId;
    }

    public void setAdPartnerAdId(List<String> adPartnerAdId) {
        this.adPartnerAdId = adPartnerAdId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFire() {
        return fire;
    }

    public void setFire(String fire) {
        this.fire = fire;
    }

    public String getExpire() {
        return expire;
    }

    public void setExpire(String expire) {
        this.expire = expire;
    }
}
