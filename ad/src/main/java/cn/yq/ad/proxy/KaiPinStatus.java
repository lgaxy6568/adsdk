package cn.yq.ad.proxy;

public class KaiPinStatus {

    /** 1：加载成功 、 2：曝光成功 、 3：加载失败 */
    private int st;

    public KaiPinStatus() {
    }

    public KaiPinStatus(int st) {
        this.st = st;
    }

    public int getSt() {
        return st;
    }

    public void setSt(int st) {
        this.st = st;
    }
}
