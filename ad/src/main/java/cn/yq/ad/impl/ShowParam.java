package cn.yq.ad.impl;

public class ShowParam {

    /** 1:正式播放  2：检查是否可以播放（即是否加载成功）*/
    private int mode = 1;

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    /** 0: IDLE  1:SUC  2:FAIL */
    private int status = 0;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ShowParam(int mode) {
        this.mode = mode;
        this.status = 0;
    }
}
