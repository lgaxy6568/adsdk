package cn.yq.demo.http;


import java.lang.reflect.Type;

import okhttp3.Response;

public interface HttpResponseHandler {
	
	/**
	 * 将resp经过处理之后，返回resultType类型
	 * @param resp http请求的响应结果
	 * @param resultType 返回的结果类型，与返回类型T是一致的
	 */
	<T> T handResponse(Response resp, Type resultType) throws Exception;
}
