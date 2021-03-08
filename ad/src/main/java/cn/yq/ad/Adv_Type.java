package cn.yq.ad;

/**
 * Created by liguo on 2018/11/1.
 * desc
 */
public enum Adv_Type {
    gdt(2),bai_du(3),self(1),tt(4),sig_mob(5),fb(6),api_magic_mobile(81),none(-1);

    Adv_Type(int value) {
        this.value = value;
    }

    int value;

    public int getValue() {
        return value;
    }

    public static cn.yq.ad.Adv_Type intValueOf(int value){
        switch (value) {
            case 1:
                return cn.yq.ad.Adv_Type.self;
            case 2:
                return cn.yq.ad.Adv_Type.gdt;
            case 3:
                return cn.yq.ad.Adv_Type.bai_du;
            case 4:
                return cn.yq.ad.Adv_Type.tt;
            case 5:
                return cn.yq.ad.Adv_Type.sig_mob;
            case 81:
                return cn.yq.ad.Adv_Type.api_magic_mobile;
            default:
                return cn.yq.ad.Adv_Type.none;
        }
    }

    public static String getAdSource(cn.yq.ad.Adv_Type type) {
        switch (type) {
            case fb:
                return "4";
            case tt:
                return "2";
            case gdt:
                return "0";
            case bai_du:
                return "1";
            case sig_mob:
                return "5";
            default:
                return "-1";
        }
    }
}
