package cn.yq.ad.proxy.model;

import java.util.Random;

import cn.yq.ad.Adv_Type;
import cn.yq.ad.impl.ADBaseImpl;
import cn.yq.ad.util.ADHelper;
import cn.yq.ad.util.AdStringUtils;

public class AdRespItem implements Comparable<AdRespItem> {

    /**
     * 权重
     */
    private int widget;

    /**
     * 优先级
     */
    private int sort;

    /**
     * SDK类型：SDK|API|Config
     */
    private String type;

    /**
     * 广告合作商KEY：GDT|CSJ，人工配置时为NULL
     */
    private String adPartnerKey;

    /**
     * APPID
     */
    private String adPartnerAppId;

    /**
     * 广告ID
     */
    private String adId;

    /**
     * 人工配置~标题
     */
    private String title;

    /**
     * 人工配置~URL
     */
    private String imageUrl;

    /**
     * 人工配置~跳转的URL
     */
    private String url;

    /**
     * 人工配置~开始时间
     */
    private String fire;

    /**
     * 人工配置~结束时间
     */
    private String expire;

    public AdRespItem() {
    }

    public AdRespItem(int widget, int sort, String type,String adPartnerKey) {
        this.widget = widget;
        this.sort = sort;
        this.type = type;
        this.adPartnerKey = adPartnerKey;
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
        return ADBaseImpl.replaceTrim_R_N(adId);
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
        //数值小的在前面
        if (a < b) {
            return -1;
        } else if (a > b) {
            return 1;
        }
        int a1 = getWeight();
        int b1 = other.getWeight();

        final int ran = new Random().nextInt(a1+b1) + 1;

        int cur_num = 0;
        int[] ws = {a1,b1};
        int result = 0; //只能为1 or -1
        for (int i = 0; i < ws.length; i++) {
            int weight = ws[i];
            cur_num += weight;
            if (cur_num >= ran) {
                if(i == 0){
                    result = -1;
                }else{
                    result = 1;
                }
                break;
            }
        }
        return result;
    }

    public String getAdv_type_name() {
        if (AdConstants.SDK_TYPE_BY_SELF.equalsIgnoreCase(type)) {
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
        if (AdConstants.PARTNER_KEY_BY_BXM.equalsIgnoreCase(adPartnerKey)) {
            return Adv_Type.bxm.name();
        }
        if (AdConstants.PARTNER_KEY_BY_MS.equalsIgnoreCase(adPartnerKey)) {
            return Adv_Type.ms.name();
        }
        return Adv_Type.none.name();
    }

    /**
     * 广告是否无效：[true：表示无效，false：有效]
     * @return
     */
    public boolean isNotValid() {
        boolean valid = false;
        if (AdStringUtils.isNotEmpty(type)) {
            if (type.trim().equalsIgnoreCase(AdConstants.SDK_TYPE_BY_SELF)) {
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
        return ADBaseImpl.replaceTrim_R_N(adPartnerAppId);
    }
}
