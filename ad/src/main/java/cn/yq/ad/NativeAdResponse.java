package cn.yq.ad;


import android.view.View;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NativeAdResponse {

    public NativeAdResponse() {
    }

    private String adId;
    public NativeAdResponse(String imageUrl, String adId,Adv_Type at) {
        this.imageUrl = imageUrl;
        this.adId = adId;
        this.adPlatform = at.getValue();
    }

    private String imageUrl;
    private String icon;
    private String title;
    private String desc;

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getAdvType() {
        return advType;
    }

    public void setAdvType(int advType) {
        this.advType = advType;
    }

    //=========================

    /**
     * 扩展对象
     */
    private Object extra;

    public Object getExtra() {
        return extra;
    }

    public void setExtra(Object extra) {
        this.extra = extra;
    }

    /**
     * LOGO URL
     */
    private String logoUrl;

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    private List<String> imgUrlLst;

    public List<String> getImgUrlLst() {
        return imgUrlLst;
    }

    public void setImgUrlLst(List<String> imgUrlLst) {
        this.imgUrlLst = imgUrlLst;
    }

    /**
     * 穿山甲添加可点击的View
     */
    private transient List<View> viewList;

    public List<View> getViewList() {
        return viewList;
    }

    public void setViewList(List<View> viewList) {
        this.viewList = viewList;
    }

    /** 广告样式 见：ADStyle.java 中的定义 */
    private int advStyle = 0;
    public int getAdvStyle() {
        return advStyle;
    }

    public void setAdvStyle(int advStyle) {
        this.advStyle = advStyle;
    }

    /** Adv_Type.getValue() */
    private int adPlatform;

    public int getAdPlatform() {
        return adPlatform;
    }

    public void setAdPlatform(int adPlatform) {
        this.adPlatform = adPlatform;
    }

    /** 落地页类型： 0：unknown 1：下载类 */
    private int advType;

    public void destroy(){
        extra = null;
        if(viewList != null && viewList.size() > 0){
            viewList.clear();
        }
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    //======================扩展Map==============
    private Map<String, String> extMap;
    public Map<String, String> getExtMap() {
        return extMap;
    }

    public final NativeAdResponse setExtMap(Map<String, String> extMap) {
        if(extMap == null || extMap.size() == 0){
            return this;
        }
        if(this.extMap == null){
            this.extMap = extMap;
        } else{
            this.extMap.putAll(extMap);
        }
        return this;
    }

    public final NativeAdResponse put(final String key, final String value){
        if(key == null || key.trim().length() == 0){
            return this;
        }
        if(value == null || value.trim().length() == 0){
            return this;
        }
        if(this.extMap == null){
            this.extMap = new HashMap<>();
        }
        this.extMap.put(key,value);
        return this;
    }

    public final String get(final String key){
        if(key == null || key.trim().length() == 0){
            return null;
        }
        if(this.extMap == null || this.extMap.size() == 0){
            return null;
        }
        return this.extMap.get(key);
    }
}
