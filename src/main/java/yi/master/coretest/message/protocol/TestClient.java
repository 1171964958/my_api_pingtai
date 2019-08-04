package yi.master.coretest.message.protocol;

import java.util.Map;

import org.apache.log4j.Logger;

import yi.master.business.testconfig.bean.TestConfig;
import yi.master.constant.MessageKeys;
import yi.master.coretest.message.test.TestMessageScene;

/**
 * 
 * 测试客户端，不同协议的请求通过工厂方法获取
 * @author xuwangcheng
 * @version 1.0.0.0,2017.4.24
 *
 */
public abstract class TestClient {
	
	public static final Logger LOGGER = Logger.getLogger(TestClient.class.getName());
	
	private static final HTTPTestClient httpClient = new HTTPTestClient();
	
	private static final SocketTestClient socketClient = new SocketTestClient();
	
	private static final WebserviceTestClient webserviceClient = new WebserviceTestClient();
	/**
	 * 发送测试请求到指定接口地址
	 * @param requestUrl  请求地址
	 * @param requestMessage 请求报文
	 * @param callParameter  自定义的请求参数,不同类型报文格式，不同请求协议需要不同的配置规则
	 * @param config 用户的测试配置，不会再这里面配置太多内容
	 * @return 测试结果详情 基础的key:<br>
	 * responseMessage-返回报文、返回内容 <br>
	 * headers-请求头，只对于http和https协议<br>
	 * useTime-请求到返回过程耗时  <br>
	 * statusCode-返回码，可以是通用的或者自定义的,默认返回false则表示调用出现系统错误  <br>
	 * mark-出错时的错误记录<br>
	 * 也可以自定义key,但是必须包含以上key,无内容用null或者""替代
	 * 
	 */
	public abstract Map<String, String>  sendRequest(String requestUrl, String requestMessage, Map<String, Object> callParameter, TestConfig config, Object client);
	
	public Map<String, String>  sendRequest(TestMessageScene scene, Object client) {
		return sendRequest(scene.getRequestUrl(), scene.getRequestMessage(), scene.getCallParameter(), scene.getConfig(), client);
	};
	
	/**
	 * 测试该接口地址是否可通
	 * @param requestUrl
	 * @return
	 */
	public abstract boolean testInterface(String requestUrl);
	
	/**
	 * 关闭打开的连接
	 */
	public abstract void closeConnection();
	
	/**
	 * 获取一个全新的测试客户端
	 */
	public abstract Object getTestClient();
	
	/**
	 * 将测试客户端放回池子中
	 */
	public abstract void putBackTestClient(Object procotolClient);
	
	/**
	 * 根据协议类型返回指定的测试客户端
	 * 
	 * @param type 协议类型 目前支持的：http/https webservice socket
	 * @return
	 */
	public static TestClient getTestClientInstance(String type) {
		
		switch (type.toLowerCase()) {
		case MessageKeys.MESSAGE_PROTOCOL_HTTPS:
		case MessageKeys.MESSAGE_PROTOCOL_HTTP:
			return httpClient;
		case MessageKeys.MESSAGE_PROTOCOL_SOCKET:			
			return socketClient;
		case MessageKeys.MESSAGE_PROTOCOL_WEBSERVICE:			
			return webserviceClient;
		default:
			break;
		}		
		return null;
	}
	
}
