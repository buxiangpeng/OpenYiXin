package yixin.com.OpenYiXin.util;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * http工具类
 * @author 卜祥鹏
 *
 */
public class HttpClientUtil {
	
	/**
	 * get 请求方法
	 * @param url
	 * @return
	 */
	public static String get(String url) {
		//创建一个连接
		CloseableHttpClient httpCilent2 = HttpClients.createDefault();
	    return requestMain(url, httpCilent2);
	}
	
	/**
	 * get 请求方法,带cookie
	 * @param url
	 * @return
	 */
	public static String get(String url,CookieStore cookieStore) {
		//创建一个连接
		CloseableHttpClient httpCilent2 = HttpClients.custom()
	            .setDefaultCookieStore(cookieStore)//设置Cookie
	            .build();
	    return requestMain(url, httpCilent2);
	}

	private static String requestMain(String url, CloseableHttpClient httpCilent2) {
		HttpGet httpGet2 = new HttpGet(url);
	    httpGet2.setConfig(getGetRequestConfig());
	    //响应信息
	    String srtResult = null;
	    try {
	        HttpResponse httpResponse = httpCilent2.execute(httpGet2);
	        //获得返回的结果
	        srtResult = EntityUtils.toString(httpResponse.getEntity());
            //httpResponse.getStatusLine().getStatusCode() 
	    } catch (IOException e) {
	        e.printStackTrace();
	    }finally {
	        try {
	            httpCilent2.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
		return srtResult;
	}

	/**
	 * 获取get的配置信息
	 * @return
	 */
	private static RequestConfig getGetRequestConfig() {
		RequestConfig requestConfig = RequestConfig.custom()
	            .setConnectTimeout(10000)   //设置连接超时时间
	            .setConnectionRequestTimeout(10000) // 设置请求超时时间
	            .setSocketTimeout(10000)
	            .setRedirectsEnabled(true)//默认允许自动重定向
	            .build();
		return requestConfig;
	}
	
}
