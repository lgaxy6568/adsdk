package cn.yq.ad;

/**
 * Created by liguo on 2018/11/1.
 * desc
 */
public enum Adv_Type {
    gdt(2),bai_du(3),self(1),tt(4),sig_mob(5),fb(6),ms(7),bxm(8),api_magic_mobile(81),none(-1);

    Adv_Type(int value) {
        this.value = value;
    }

    int value;

    public int getValue() {
        return value;
    }

    public static Adv_Type intValueOf(int value){
        switch (value) {
            case 1:
                return Adv_Type.self;
            case 2:
                return Adv_Type.gdt;
            case 8:
                return Adv_Type.bxm;
            case 3:
                return Adv_Type.bai_du;
            case 4:
                return Adv_Type.tt;
            case 5:
                return Adv_Type.sig_mob;
            case 7:
                return Adv_Type.ms;
            case 81:
                return Adv_Type.api_magic_mobile;
            default:
                return Adv_Type.none;
        }
    }

    public static String getAdSource(Adv_Type type) {
        switch (type) {
            case fb:
                return "4";
            case tt:
                return "2";
            case gdt:
                return "0";
            case bxm:
                return "11";
            case bai_du:
                return "1";
            case sig_mob:
                return "5";
            case ms:
                return "10";
            default:
                return "-1";
        }
    }
}
