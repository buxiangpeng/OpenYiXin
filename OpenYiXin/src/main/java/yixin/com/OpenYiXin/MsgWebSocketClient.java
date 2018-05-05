package yixin.com.OpenYiXin;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSONObject;

import yixin.com.OpenYiXin.common.Common;

/**
 * 消息处理类
 * @author 卜祥鹏
 *
 */
public class MsgWebSocketClient extends WebSocketClient{
	
	private static final Logger logger = LoggerFactory.getLogger(WebSocketClient.class);
	
	private String userId = null;
	
	private boolean first = true;

	public MsgWebSocketClient(String url,String userId) throws URISyntaxException {  
        super(new URI(url));  
        this.userId= userId;
    }  
  
    @Override  
    public void onOpen(ServerHandshake shake) {  
    	logger.info("connect...");
        for(Iterator<String> it=shake.iterateHttpFields();it.hasNext();) {  
            String key = it.next();  
            System.out.println(key+":"+shake.getFieldValue(key));  
        }  
    }  
  
    @Override  
    public void onMessage(String message) {  
    	logger.info("getInfo："+message);
        //处理消息
        processorMessage(message);
    }

    /**
     * 0::断开
     * 1::连接成功
     * 2::25秒心跳
     * 3::正常消息
     * @param message
     */
	private void processorMessage(String message) {
		if(message.contains("1::")) {
		  //发送鉴权信息
		  logger.info("send: " + getSendInfo());
      	  this.send(getSendInfo());
      	  
        }else if(message.contains("3:::") && first) {
          //发送第二次鉴权信息
          logger.info("send: " + getTwoSendInfo());
      	  this.send(getTwoSendInfo());  
      	  first=false;
      	  
        }else if(message.contains("2::")){
          //正常响应无消息信息
          logger.info("send: 2::");
      	  this.send("2::");  
        }else {
         	logger.info("接收到正常消息:{}",message);
        }
	}  
  
    

	@Override  
    public void onClose(int paramInt, String paramString, boolean paramBoolean) {  
    	 logger.info("close...paramInt:{},paramString:{},paramBoolean:{}",paramInt,paramString,paramBoolean);  
    }  
  
    @Override  
    public void onError(Exception e) {  
    	 logger.info("error"+e);  
    }  
    
    private String getTwoSendInfo() {
    	Map<String, String> map1 = new HashMap<String, String>();
    	map1.put("1", "0");
    	map1.put("2", "0");
    	map1.put("3", "0");
    	map1.put("5", "0");
    	map1.put("10", "0");
    	
    	Map<String, Object> map2 = new HashMap<String, Object>();
    	map2.put("t", "ByteIntMap");
    	map2.put("v", map1);
    	
    	
    	Map<String, Object> map3 = new HashMap<String, Object>();
    	map3.put("t", "LongIntMap");
    	map3.put("v", new HashMap<Object, Object>());
    	
    	List<Map> listMap = new ArrayList<Map>();
    	listMap.add(map2);
    	listMap.add(map3);
    	
    	Map<String, Object> map4 = new HashMap<String, Object>();
    	map4.put("CID", 1);
    	map4.put("SID", 93);
    	map4.put("Q", listMap);
    	return "3:::"+JSONObject.toJSONString(map4);
    }
    
    private String getSendInfo() {
    	Map<String, String> map1 = new HashMap<String, String>();
    	map1.put("t", "string");
    	map1.put("v", userId);
    	
    	Map<String, String> map2 = new HashMap<String, String>();
    	map2.put("9", "80");
    	map2.put("10", "100");
    	map2.put("16", Common.YXLKDEVICEID);
    	map2.put("24", "");
    	Map<String, Object> map3 = new HashMap<String, Object>();
    	map3.put("t", "property");
    	map3.put("v", map2);
    	
    	Map<String, Object> map4 = new HashMap<String, Object>();
    	map4.put("t", "boolean");
    	map4.put("v", true);
    	
    	List<Map> list = new ArrayList<Map>();
    	list.add(map1);
    	list.add(map3);
    	list.add(map4);
    	
    	Map<String, Object> map5 = new HashMap<String, Object>();
    	map5.put("SID", 90);
    	map5.put("CID", 34);
    	map5.put("Q", list);
    	
    	return "3:::"+JSONObject.toJSONString(map5);
    }
	
}
