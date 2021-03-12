package cn.yq.ad.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import cn.yq.ad.Adv_Type;
import cn.yq.ad.proxy.model.AdRespItem;

public abstract class BaseModel<T extends BaseModel> {
    private final Map<String, String> extMap = new LinkedHashMap<>();
    public Map<String, String> getExtMap() {
        return extMap;
    }

    public final T setExtMap(Map<String, String> map) {
        if(map == null || map.size() == 0){
            return (T)this;
        }
        extMap.clear();
        extMap.putAll(map);
        return (T)this;
    }

    public final T put(final String key, final String value){
        if(key == null || key.trim().length() == 0){
            return (T)this;
        }
        if(value == null || value.trim().length() == 0){
            return (T)this;
        }
        this.extMap.put(key,value);
        return (T)this;
    }

    public final String get(final String key){
        if(key == null || key.trim().length() == 0){
            return null;
        }
        if(this.extMap.size() == 0){
            return null;
        }
        return this.extMap.get(key);
    }

    public abstract String getAdId();
    public abstract String getAdvType();
    public abstract Adv_Type getAdv_Type();
    public String getInfo(){
        return "adId="+getAdId()+",adType="+getAdv_Type().name();
    }
    private AdRespItem adRespItem;

    public AdRespItem getAdRespItem() {
        return adRespItem;
    }

    public T setAdRespItem(AdRespItem adRespItem) {
        this.adRespItem = adRespItem;
        return (T)this;
    }
}
