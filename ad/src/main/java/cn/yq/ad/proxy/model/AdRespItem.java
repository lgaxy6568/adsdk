package cn.yq.ad.proxy.model;

import java.util.Random;

import cn.yq.ad.Adv_Type;
import cn.yq.ad.impl.ExtraKey;
import cn.yq.ad.util.ADHelper;
import cn.yq.ad.util.AdStringUtils;

public class AdRespItem implements Comparable<AdRespItem> {
    private int widget;
    private int sort;

    private String type;

    private String adPartnerKey;

    private String adPartnerAppId;

    private String adId;

    private String title;

    private String imageUrl;

    private String url;

    private String fire;

    private String expire;

    public AdRespItem() {
    }

    public AdRespItem(int widget, int sort, String type, String adPartnerKey, String adPartnerAppId, String adId, String title, String imageUrl, String url, String fire, String expire) {
        this.widget = widget;
        this.sort = sort;
        this.type = type;
        this.adPartnerKey = adPartnerKey;
        this.adPartnerAppId = adPartnerAppId;
        this.adId = adId;
        this.title = title;
        this.imageUrl = imageUrl;
        this.url = url;
        this.fire = fire;
        this.expire = expire;
    }

    private int kpSizeType;
    public static final int KP_3 = 3;
    public static final int KP_1 = 1;

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
        return adPartnerAppId;
    }

    public void setAdPartnerAppId(String adPartnerAppId) {
        this.adPartnerAppId = adPartnerAppId;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
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

    public int getKpSizeType() {
        return kpSizeType;
    }

    public void setKpSizeType(int kpSizeType) {
        this.kpSizeType = kpSizeType;
    }

    @Override
    public int compareTo(AdRespItem other) {
        int a = sort;
        int b = other.sort;
        //数值大的在前面
        if (a < b) {
            return 1;
        } else if (a > b) {
            return -1;
        }
        final int ran = new Random().nextInt(100) + 1;
        return (ran % 2 == 0) ? 1 : -1;
    }

    public String getAdv_type_name() {
        if (AdConstants.PARTNER_KEY_BY_SELF.equalsIgnoreCase(type)) {
            return Adv_Type.self.name();
        }
        if (AdStringUtils.isEmpty(adPartnerKey)) {
            return Adv_Type.none.name();
        }
        if (AdConstants.PARTNER_KEY_BY_TT.equalsIgnoreCase(adPartnerKey)) {
            return Adv_Type.tt.name();
        }
        if (AdConstants.PARTNER_KEY_BY_GDT.equalsIgnoreCase(adPartnerKey)) {
            return Adv_Type.gdt.name();
        }
        return Adv_Type.none.name();
    }

    public String getKpSizeTypeDesc() {
        if (kpSizeType == KP_1) {
            return ExtraKey.KP_AD_SIZE_TYPE_VALUE_QUAN_PING;
        } if (kpSizeType == KP_3) {
            return ExtraKey.KP_AD_SIZE_TYPE_VALUE_BAN_PING;
        } return "unknown_" + kpSizeType;
    }

    public boolean isNotValid() {
        boolean valid = false;
        if (AdStringUtils.isNotEmpty(type)) {
            if (type.trim().equalsIgnoreCase(AdConstants.PARTNER_KEY_BY_SELF)) {
                String format = "yyyy-MM-dd HH:mm:ss";
                long now = System.currentTimeMillis();
                long startTime = ADHelper.getMillisByDateStr(fire, format);
                long endTime = ADHelper.getMillisByDateStr(expire, format);
                if (now >= startTime && now <= endTime) {
                    valid = false;
                } else {
                    valid = true;
                }
            }
        }
        return valid;
    }

    public int getWeight() {
        return widget;
    }

    public int getPri(){
        return sort;
    }

    public String getAppId() {
        return adPartnerAppId;
    }
}
