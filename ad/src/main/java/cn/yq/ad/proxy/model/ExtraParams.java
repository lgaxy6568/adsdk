package cn.yq.ad.proxy.model;

import java.io.Serializable;

public class ExtraParams implements Serializable {
    private static final long serialVersionUID = 1L;

    public ExtraParams() {
    }

    private boolean isVip = false;

    public boolean isVip() {
        return isVip;
    }

    public void setVip(boolean vip) {
        isVip = vip;
    }
    private String usrId;

    public String getUsrId() {
        return usrId;
    }

    public void setUsrId(String usrId) {
        this.usrId = usrId;
    }
}
