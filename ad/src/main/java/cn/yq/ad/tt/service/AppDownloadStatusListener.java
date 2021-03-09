package cn.yq.ad.tt.service;

import android.content.Context;
import android.util.Log;

import com.bytedance.sdk.openadsdk.TTAppDownloadInfo;
import com.bytedance.sdk.openadsdk.TTGlobalAppDownloadListener;


/**
 * 实现TTGlobalAppDownloadListener接口，实现监听SDK内部下载进度状态回调
 * 如果你不允许SDK内部弹出Notification,可以在此回调中自如弹出Notification
 */

@SuppressWarnings("WeakerAccess")
public class AppDownloadStatusListener implements TTGlobalAppDownloadListener {
    public static final int DOWNLOAD_STATUS_ACTIVE = 1;
    public static final int DOWNLOAD_STATUS_WATING = 2;
    public static final int DOWNLOAD_STATUS_FINISH = 3;
    public static final int DOWNLOAD_STATUS_DELETE = 4;
    public static final int DOWNLOAD_STATUS_FAILED = 5;

    private final Context mContext;

    public AppDownloadStatusListener(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    public void onDownloadActive(TTAppDownloadInfo info) {
//        updateNotification(DOWNLOAD_STATUS_ACTIVE, info);
        Log.d("TTGlobalDownload", "下载中----" + info.getAppName() + "---" + getDownloadPercent(info) + "%");
    }

    @Override
    public void onDownloadPaused(TTAppDownloadInfo info) {
//        updateNotification(DOWNLOAD_STATUS_WATING, info);
        Log.d("TTGlobalDownload", "暂停----" + info.getAppName() + "---" + getDownloadPercent(info) + "%");


    }

    @Override
    public void onDownloadFinished(TTAppDownloadInfo info) {
//        updateNotification(DOWNLOAD_STATUS_FINISH, info);
        Log.d("TTGlobalDownload", "下载完成----" + info.getAppName() + "---" + getDownloadPercent(info) + "%" + "  file: " + info.getFileName());

    }

    @Override
    public void onInstalled(String pkgName, String appName, long id, int status) {
        Log.d("TTGlobalDownload", "安装完成----" + "pkgName: " + pkgName);
    }

    @Override
    public void onDownloadFailed(TTAppDownloadInfo info) {
//        updateNotification(DOWNLOAD_STATUS_FINISH, info);
    }

    private int getDownloadPercent(TTAppDownloadInfo info) {
        if (info == null) {
            return 0;
        }
        double percentD = 0D;
        try {
            percentD = (double) info.getCurrBytes() / (double) info.getTotalBytes();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        int percent = (int) (percentD * 100);
        if (percent < 0) {
            percent = 0;
        }
        return percent;
    }

}
