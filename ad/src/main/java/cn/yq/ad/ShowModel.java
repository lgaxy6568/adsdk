package cn.yq.ad;

public class ShowModel {

    public ShowModel(AdNativeResponse nar) {
        this.nar = nar;
    }

    private AdNativeResponse nar;

    public AdNativeResponse getNar() {
        return nar;
    }

    public void setNar(AdNativeResponse nar) {
        this.nar = nar;
    }
    private Adv_Type adv_type;

    public Adv_Type getAdv_type() {
        return adv_type;
    }

    public void setAdv_type(Adv_Type adv_type) {
        this.adv_type = adv_type;
    }
}
