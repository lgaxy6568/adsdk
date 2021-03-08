package cn.yq.ad.proxy.model;

import java.util.List;

public class GetAdsResponseListApiResult {
    private String message;
    private String server;
    private String code;
    private int status;
    private String time;
    private List<GetAdsResponse> data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<GetAdsResponse> getData() {
        return data;
    }

    public void setData(List<GetAdsResponse> data) {
        this.data = data;
    }
}
