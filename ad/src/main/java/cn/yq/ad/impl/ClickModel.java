package cn.yq.ad.impl;

import cn.yq.ad.Adv_Type;

/**
 * Created by liguo on 2019/1/8.
 * desc
 */
public class ClickModel extends BaseModel<ClickModel>{
    /** 0:点击 1：跳过 */
    private int from = -1;

    /** 1:下载 2：其它 -1：未知 */
    private int adType = -1;
    private String adId;
    private Object data;

    public Object getData() {
        return data;
    }

    public cn.yq.ad.impl.ClickModel setData(Object data) {
        this.data = data;
        return this;
    }

    @Override
    public String getAdId() {
        return adId;
    }

    public void setAdId(String adID) {
        this.adId = adID;
    }

    public ClickModel(int from, int adType, String adID, String advType) {
        this.from = from;
        this.adType = adType;
        this.adId = adID;
        this.advType = advType;
    }

    public ClickModel() {
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public static ClickModel getInstance(int from, int adType, String adID, Adv_Type at){
        return new ClickModel(from,adType,adID,at.name());
    }

    public int getAdType() {
        return adType;
    }

    public void setAdType(int adType) {
        this.adType = adType;
    }

    private String advType;

    @Override
    public String getAdvType() {
        return advType;
    }

    public void setAdvType(String advType) {
        this.advType = advType;
    }

    @Override
    public Adv_Type getAdv_Type(){
        return Adv_Type.valueOf(this.advType);
    }
}
