package yi.master.coretest.message.protocol;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;

import yi.master.business.testconfig.bean.TestConfig;
import yi.master.constant.MessageKeys;
import yi.master.util.PracticalUtils;

public class WebserviceTestClient extends TestClient {
	
	protected WebserviceTestClient() {
		
	}

	@Override
	public Map<String, String> sendRequest(String requestUrl,
			String requestMessage, Map<String, Object> callParameter, TestConfig config, Object client) {
		
		String username = null;
		String password = null;
		String namespace = "";
		String method = "";
		int connectTimeOut = 5000;
		
		if (callParameter != null) {
			username = (String) callParameter.get(MessageKeys.PUBLIC_PARAMETER_USERNAME);
			password = (String) callParameter.get(MessageKeys.PUBLIC_PARAMETER_PASSWORD);
			namespace = (String) callParameter.get(MessageKeys.WEB_SERVICE_PARAMETER_NAMESPACE);
			method = (String) callParameter.get(MessageKeys.PUBLIC_PARAMETER_METHOD);
			connectTimeOut = (int)callParameter.get(MessageKeys.PUBLIC_PARAMETER_CONNECT_TIMEOUT);
		}
		
		String responseMessage = "";
		String useTime = "0";
		String statusCode = "200";
		String mark = "";
		
		try {
			long beginTime = System.currentTimeMillis();
			responseMessage = callService(requestUrl, requestMessage, namespace, method, connectTimeOut, username, password);
			long endTime = System.currentTimeMillis();
			useTime = String.valueOf(endTime - beginTime);			
		} catch (Exception e) {
			
			statusCode = "false";
			mark = "Fail to call web-service url=" + requestUrl + ",namespace=" + namespace + ",method=" + method + "!";
		}
		Map<String, String> returnMap = new HashMap<String, String>();
		returnMap.put("responseMessage", responseMessage);
		returnMap.put("useTime", useTime);
		returnMap.put("statusCode", statusCode);
		returnMap.put("mark", mark);
		return returnMap;
	}

	@Override
	public boolean testInterface(String requestUrl) {
		
		return true;
	}

	@Override
	public void closeConnection() {
				
	}
	
	@Override
	public Object getTestClient() {
		
		return null;
	}

	@Override
	public void putBackTestClient(Object procotolClient) {
		
		
	}
	
	/****************************************************************************************************/
	/**
	 * 使用Axis2调用webservice<br>
	 * 使用RPC方式调用
	 * @param requestUrl
	 * @param request
	 * @param namespace
	 * @param method
	 * @param connectTimeOut
	 * @return
	 * @throws Exception 
	 */
	private String callService (String requestUrl, String request, String namespace, String method, long connectTimeOut, String username, String password) throws Exception {
		try {
			RPCServiceClient client = new RPCServiceClient();
			Options option = client.getOptions();

			// 指定调用的的wsdl地址
			//例如：http://ws.webxml.com.cn/WebServices/ChinaOpenFundWS.asmx?wsdl
			EndpointReference reference = new EndpointReference(requestUrl);
			option.setTo(reference);
			option.setTimeOutInMilliSeconds(connectTimeOut);
			
			if (PracticalUtils.isNormalString(username) && PracticalUtils.isNormalString(password)) {
				option.setUserName(username);
				option.setPassword(password);
			}

			/*
			 * 设置要调用的方法 
			 * http://ws.apache.org/axis2 为默认的（无package的情况）命名空间，
			 * 如果有包名，则为 http://axis2.webservice.elgin.com ,包名倒过来即可 
			 * method为方法名称
			 */
			QName qname = new QName(namespace, method);

			// 调用远程方法,并指定方法参数以及返回值类型
			Object[] result = client.invokeBlocking(qname,
					new Object[] { request }, new Class[] { String.class });
			return result[0].toString();
		} catch (Exception e) {
				
			LOGGER.debug("Fail to call web-service url=" + requestUrl + ",namespace=" + namespace + ",method=" + method + "!", e);
			throw e;
		}
				          
	}	
}
