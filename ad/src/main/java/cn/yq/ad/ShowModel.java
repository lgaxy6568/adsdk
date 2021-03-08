package cn.yq.ad;

public class ShowModel {

    public ShowModel(cn.yq.ad.NativeAdResponse nar) {
        this.nar = nar;
    }

    private cn.yq.ad.NativeAdResponse nar;

    public cn.yq.ad.NativeAdResponse getNar() {
        return nar;
    }

    public void setNar(cn.yq.ad.NativeAdResponse nar) {
        this.nar = nar;
    }
    private cn.yq.ad.Adv_Type adv_type;

    public cn.yq.ad.Adv_Type getAdv_type() {
        return adv_type;
    }

    public void setAdv_type(cn.yq.ad.Adv_Type adv_type) {
        this.adv_type = adv_type;
    }
}
