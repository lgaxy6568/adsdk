package cn.yq.ad.util;

import android.util.Log;

import cn.yq.ad.proxy.AdConfigs;

public class AdLogUtils {
	private static boolean debug = AdConfigs.isDebugModel();

	public static void setDebug(boolean debug) {
		AdLogUtils.debug = debug;
	}

	public static void d(String tag, String msg) {
		if(debug) {
			Log.d(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if(debug) {
			Log.w(tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if(debug) {
			Log.i(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if(debug) {
			Log.e(tag, msg);
		}
	}
	public static void e(String tag, String msg,Throwable e) {
		if (debug){
			Log.e(tag, msg, e);
		}
	}
	public static void logMethodCallStacktrace(String tag){
		if(debug){
			RuntimeException re = new RuntimeException("here");
			re.fillInStackTrace();
			Log.e(tag,"Called:",re);
		}
	}
}
