package yixin.com.OpenYiXin;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Map;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
import yixin.com.OpenYiXin.common.Common;
import yixin.com.OpenYiXin.util.HttpClientUtil;
import yixin.com.OpenYiXin.util.ImageUtil;

/**
 * 主类
 * @author 卜祥鹏
 *
 */
public class App {
	
	private static final Logger logger = LoggerFactory.getLogger(App.class);
	
	//webSocket客户端
	private static MsgWebSocketClient client = null;
	
    public static void main( String[] args ){
    	
    	//通过登录流程获取二维码
		logger.info("开始访问易信主网站");
		
		//访问首页，
    	String message = HttpClientUtil.get(Common.YIXINURL);
    	logger.info("获取首页信息完成");
    	
    	//解析返回的html文本信息，获取二维码url
    	String qrCodeUrl = getQrCodeUrlByHtmlText(message);
    	logger.info("获取到登录二维码，地址:{}",qrCodeUrl);
    	
    	//获取监听参数
    	String qrcode = getQrCodeByHtmlText(message);
    	logger.info("获取到了监听所需的参数{}",qrcode);
    	
    	//把二维码保存带本地
    	logger.info("把二维码存到E:/yiXin.jpg");
		ImageUtil.downloadPicture(qrCodeUrl, Common.LOCALYIXINURL);
		
		//监听登录二维码
		logger.info("开始监听用户扫描二维码");
		String url = Common.LISTENERURL+"qrcode="+qrcode+"&ts="+new Date().getTime();
		
		
		//死循环，进行监听用户登录
		String userId = null;
		while (true) {
		    message = HttpClientUtil.get(url);
			Map<String, Object> map =JSON.parseObject(message, Map.class);
			
			if(map.get("code").toString().equals("100")) {
				logger.info("请扫描二维码登录：{}",message);
			}
			
			if(map.get("code").toString().equals("202")) {
				logger.info("扫描成功，等待客户全确认授权:{}",message);
			}
			
			if(map.get("code").toString().equals("200")) {
				logger.info("登录成功:{}",message);
				logger.info("结束监听用户扫描二维码");
				userId = map.get("message").toString();
				break;
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//对userID进行解码
    	userId = encode(userId);
    	logger.info("解码后的用户ID：{}",userId);
    	
    	//设置cookie，为下一步准备参数
    	CookieStore cookieStore = setCookie(userId);
    	
    	//获取sockedID所需要的参数
	    String sockedIdAll = HttpClientUtil.get(Common.GETSOCKEDID+new Date().getTime(),cookieStore);
	    logger.info("sockedIdAll：{}",sockedIdAll);
	    
	    //解析sockedID
	    String sockedId = getSockedId(sockedIdAll);
	    logger.info("sockedId：{}",sockedId);
	    
	    logger.info("开始进行socket连接");
	    try {
			client = new MsgWebSocketClient(Common.YIXIN_SOCKED_URL+sockedId, userId);
			client.setConnectionLostTimeout(0);
			client.connect();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    //获取进行socket时所需要的id、
  	private static String getSockedId(String sockedIdAll) {
  		Integer number = sockedIdAll.indexOf(":");
  		return sockedIdAll.substring(0, number);
  	}
    
    //解码url
  	public static String encode(String url){     
        try {     
             String encodeURL=java.net.URLDecoder.decode(url,"utf-8");   
             return encodeURL;     
        } catch (UnsupportedEncodingException e) {     
             return "Issue while encoding" +e.getMessage();     
        }     
    } 
    
	//处理登录信息工具
	private static String getQrCodeByHtmlText(String message) {
		Integer indexQrCode = message.indexOf("',qrCode:'");
		Integer script = message.indexOf("'};</script>");
		return message.substring(indexQrCode+10, script);
	}
	
	//处理登录信息工具
	private static String getQrCodeUrlByHtmlText(String message) {
		Integer indexQrUrl = message.indexOf("qrUrl:'");
		Integer indexQrCode = message.indexOf("',qrCode:");
		return message.substring(indexQrUrl+7, indexQrCode);
	}
	
	//设置cookie
	private static CookieStore setCookie(String userId) {
		BasicClientCookie cookie = new BasicClientCookie("yxlkid", userId);
	    cookie.setVersion(0);
	    cookie.setDomain("xxx.cn");
	    cookie.setPath("/x");
	    BasicClientCookie cookie1 = new BasicClientCookie("yxlkdeviceid", Common.YXLKDEVICEID);
	    cookie1.setVersion(0);
	    cookie1.setDomain("xxx.cn");
	    cookie1.setPath("/x");
	    CookieStore cookieStore = new BasicCookieStore();
	    cookieStore.addCookie(cookie);
	    cookieStore.addCookie(cookie1);
		return cookieStore;
	}
}
