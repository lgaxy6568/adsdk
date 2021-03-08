package cn.yq.ad.proxy.model;

import java.util.List;

public class GetAdsResponse {
    private String page;
    private String location;
    private int probability;
    private List<AdResponse> ads;

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getProbability() {
        return probability;
    }

    public void setProbability(int probability) {
        this.probability = probability;
    }

    public List<AdResponse> getAds() {
        return ads;
    }

    public void setAds(List<AdResponse> ads) {
        this.ads = ads;
    }
}
