package cn.yq.ad.impl;

import cn.yq.ad.Adv_Type;

/**
 * Created by liguo on 2019/1/8.
 * desc
 */
public class DismissModel extends BaseModel<DismissModel> {

    private Object data;
    private String adId;
    private String advType;
    public DismissModel() {
    }

    public DismissModel(String adId, String adType) {
        this.adId = adId;
        this.advType = adType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    @Override
    public String getAdvType() {
        return advType;
    }

    public void setAdvType(String advType) {
        this.advType = advType;
    }

    /** 0:点击 1：跳过 2：倒计时完成 */
    private int from = -1;

    public int getFrom() {
        return from;
    }

    public DismissModel setFrom(int from) {
        this.from = from;
        return this;
    }

    public static DismissModel newInstance(String adId, Adv_Type at, int from){
        return new DismissModel(adId,at.name()).setFrom(from);
    }

    public static DismissModel newInstance(String adId, Adv_Type at){
        return new DismissModel(adId,at.name());
    }

    @Override
    public Adv_Type getAdv_Type(){
        return Adv_Type.valueOf(this.advType);
    }
}
