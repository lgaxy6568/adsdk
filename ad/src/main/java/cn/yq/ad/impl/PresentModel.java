package cn.yq.ad.impl;

import cn.yq.ad.Adv_Type;

/**
 * Created by liguo on 2018/12/28.
 * desc
 */
public class PresentModel extends BaseModel<cn.yq.ad.impl.PresentModel>{

    private Object data;
    private String adId;
    private String advType;
    public PresentModel() {
    }

    public PresentModel(String adId, String adType) {
        this.adId = adId;
        this.advType = adType;
    }

    public Object getData() {
        return data;
    }

    public cn.yq.ad.impl.PresentModel setData(Object data) {
        this.data = data;
        return this;
    }

    @Override
    public String getAdId() {
        return adId;
    }

    public cn.yq.ad.impl.PresentModel setAdId(String adId) {
        this.adId = adId;
        return this;
    }

    @Override
    public String getAdvType() {
        return advType;
    }

    public void setAdvType(String advType) {
        this.advType = advType;
    }

    public static cn.yq.ad.impl.PresentModel getInstance(String adId, Adv_Type at){
        return new cn.yq.ad.impl.PresentModel(adId,at.name());
    }

    @Override
    public Adv_Type getAdv_Type(){
        return Adv_Type.valueOf(this.advType);
    }
}
