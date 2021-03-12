package cn.yq.ad.impl;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import cn.yq.ad.Adv_Type;

public class FailModel extends BaseModel<FailModel> {
    private int code;
    private String msg;
    public FailModel(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    /** 单个ID */
    private String adId;

    private String advType;

    public FailModel() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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

    public String toFullMsg(){
        String str = "{\"code\":\"%d\",\"msg\":\"%s\",\"advType\":\"%s\",\"adId\":\"%s\"}";
        return String.format(Locale.getDefault(),str,code,msg,advType,adId);
    }

    public static FailModel toStr(int code, String msg, String adId, Adv_Type at){
        FailModel fm = new FailModel();
        fm.setCode(code);
        fm.setMsg(msg);
        fm.setAdId(adId);
        fm.setAdvType(at.name());
        return fm;
    }

    public Map<String,Object> toMap(){
        Map<String,Object> mmp = new LinkedHashMap<>();
        mmp.put("err_msg",toFullMsg());
        return mmp;
    }

    @Override
    public Adv_Type getAdv_Type(){
        return Adv_Type.valueOf(this.advType);
    }


}
