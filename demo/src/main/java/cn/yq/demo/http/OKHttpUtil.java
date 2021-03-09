package cn.yq.demo.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;

import java.lang.reflect.Type;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cn.yq.ad.util.AdGsonUtils;
import cn.yq.ad.util.AdLogUtils;
import cn.yq.ad.util.AdStringUtils;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.platform.Platform;

public class OKHttpUtil {
	private static final String TAG = "OKHttpUtil";
	@SuppressWarnings("unchecked")
	public static <T> T execute(Request request, Type resultType) throws Exception {
		Response response;
		try {
			response = getOkHttpClient().newCall(request).execute();
		} catch (Exception e) {
			AdLogUtils.e(TAG,"execute("+request.url().toString()+")->err:",e);
			throw new Exception(request.url().toString(),e);
		}
		if (resultType == null || resultType == Response.class) {
			return (T) response;
		}
		if (resultType == Void.TYPE) {
			return null;
		}
		if(resultType == String.class){
			return (T)response.body().string();
		}
		if(resultType == Bitmap.class){
			byte[] bytes = response.body().bytes();
			return (T) BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		}
		return AdGsonUtils.getGson().fromJson(response.body().charStream(), resultType);
	}
	
	public static <T> Request buildRequest(String url, HttpMethod method, Map<String,T> params, Map<String, String> headers){
		Request.Builder builder;
		if(method == method.GET){
			builder = buildRequestByGet(url,params);
		}else if(method == method.POST){
			builder = buildRequestByPost(url, params);
		}else{
            throw new RuntimeException(method.name()+" is Supported ?");
        }
		if(headers != null && headers.size() > 0){
			for (String name : headers.keySet()) {
				String e_name = name;

//				try {
//					if(CharsetUtil.isContainChinese(e_name)) {
//						e_name = Uri.encode(name);
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//
				String e_value = headers.get(name);
//				try {
//					if(CharsetUtil.isContainChinese(e_value)) {
//						e_value = Uri.encode(e_value);
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}

				builder.addHeader(e_name,e_value);
			}
		}
		return builder.build();
	}

	private static class OkHttpClientHelper{
		private static final OkHttpClient INSTANCE = new OkHttpClient().newBuilder()
				.followRedirects(true)
				.followSslRedirects(true)
				.sslSocketFactory(createSSLSocketFactory(), Platform.get().platformTrustManager())
				.hostnameVerifier(new TrustAllHostnameVerifier())
				.build();
	}

	public static OkHttpClient getOkHttpClient(){
		System.setProperty("http.keepAlive", "false");
		return OkHttpClientHelper.INSTANCE;
	}

	private static <T> Request.Builder buildRequestByGet(String url, Map<String,T> params){
		return new Request.Builder().url(appendParams(url, params));
	}

	private static final Map<String, String> clsMap = new HashMap<>();
	static {
		Object[] arr = {1,1L,0.5f,5d,(byte)1,"abc",'a',true};
		for (Object o : arr) {
			clsMap.put(o.getClass().getName(),"-");
		}
	}
	public static <T> String appendParams(String url, Map<String,T> params){
		StringBuilder sb = new StringBuilder(url);
		if(params != null && params.size() > 0){
			Uri uri = Uri.parse(url);
			String query = uri.getQuery();
			boolean hasQuery = false;
			if(query != null && query.trim().length() > 0){
				hasQuery=true;
			}
			for (String key : params.keySet()) {
				Object obj = params.get(key);
				if(obj == null) {
					continue;
				}
				String value = "";
				String pkgName = obj.getClass().getName();
				if(obj instanceof String){
					value = obj.toString();
				}else if((obj instanceof List) || (obj instanceof Map)){
					value = AdGsonUtils.getGson().toJson(obj);
				}else if(clsMap.containsKey(pkgName)){
					value = obj.toString();
				}else{
					throw new RuntimeException("unSupport cls :"+pkgName);
				}

				if(AdStringUtils.isEmpty(value) || AdStringUtils.isEmpty(key))
					continue;
				if(hasQuery){
					sb.append("&");
				}else{
					sb.append("?");
					hasQuery=true;
				}
				try {
					value = Uri.encode(value);
				} catch (Exception e) {
					e.printStackTrace();
				}
				sb.append(key).append("=").append(value);
			}
		}
		return sb.toString();
	}
	
	private static <T> Request.Builder buildRequestByPost(final String url, Map<String,T> params){
		return new Request.Builder().url(url).post(getFormRequestBody(params));
	}

	public static <T> RequestBody getFormRequestBody(Map<String,T> params){
		FormBody.Builder bodyBuilder = new FormBody.Builder();
		if(params != null && params.size() > 0){
            String value;
			for (String key : params.keySet()) {
                value = params.get(key).toString();
                if(TextUtils.isEmpty(value)){
                    continue;
                }
                try {
                    bodyBuilder.add(key,value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
		}
		return bodyBuilder.build();
	}

	public static final MediaType MEDIA_TYPE_FORM_JSON = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");//mdiatype 这个需要和服务端保持一致

	private static SSLSocketFactory createSSLSocketFactory() {
		SSLSocketFactory ssf = null;
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, new TrustManager[]{new TrustAllManager()},new SecureRandom());
			ssf = sc.getSocketFactory();
		} catch (Exception e) {
		}
		return ssf;
	}

	private static class TrustAllManager implements X509TrustManager {
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[]{};
		}
	}

	private static class TrustAllHostnameVerifier implements HostnameVerifier {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}
}
