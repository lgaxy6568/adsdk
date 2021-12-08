package cn.yq.ad;

/**
 * Created by liguo on 2018/11/1.
 * desc
 */
public enum Adv_Type {
    gdt(2),bai_du(3),self(1),tt(4),sig_mob(5),fb(6),ms(7),api_magic_mobile(81),none(-1);

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
}
