package yi.master.coretest.message.protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import yi.master.business.testconfig.bean.TestConfig;
import yi.master.constant.MessageKeys;
import yi.master.util.PracticalUtils;

public class SocketTestClient extends TestClient {
	
	private static final Logger LOGGER = Logger.getLogger(SocketTestClient.class);
	
	protected SocketTestClient() {
		
	}

	@Override
	public Map<String, String> sendRequest(String requestUrl,
			String requestMessage, Map<String, Object> callParameter, TestConfig config, Object client) {
		
		Map<String, String> returnMap = new HashMap<String, String>();
		
		int connectTimeOut = config.getConnectTimeOut();
		int soTimeOut = config.getReadTimeOut();
		
		if (callParameter != null ) {
			if (PracticalUtils.isNumeric(callParameter.get(MessageKeys.PUBLIC_PARAMETER_CONNECT_TIMEOUT))) {
				connectTimeOut = Integer.parseInt((String) callParameter.get(MessageKeys.PUBLIC_PARAMETER_CONNECT_TIMEOUT));
			}
			
			if (PracticalUtils.isNumeric(callParameter.get(MessageKeys.PUBLIC_PARAMETER_READ_TIMEOUT))) {
				soTimeOut = Integer.parseInt((String) callParameter.get(MessageKeys.PUBLIC_PARAMETER_READ_TIMEOUT));
			}
		}
		
		String[] ipPort = requestUrl.split(":");
		
		long startTime = System.currentTimeMillis();
		String responseMsg = sendSocketMsg(ipPort[0], Integer.parseInt(ipPort[1]), requestMessage, connectTimeOut, soTimeOut);
		long endTime = System.currentTimeMillis();
		
		long useTime = endTime - startTime;
		
		returnMap.put(MessageKeys.RESPONSE_MAP_PARAMETER_MESSAGE, responseMsg);
		returnMap.put(MessageKeys.RESPONSE_MAP_PARAMETER_USE_TIME, String.valueOf(useTime));
		returnMap.put(MessageKeys.RESPONSE_MAP_PARAMETER_STATUS_CODE, "200");	
		returnMap.put(MessageKeys.RESPONSE_MAP_PARAMETER_TEST_MARK, "");
		
		if (responseMsg.startsWith("Send")) {
			returnMap.put(MessageKeys.RESPONSE_MAP_PARAMETER_MESSAGE, "");
			returnMap.put(MessageKeys.RESPONSE_MAP_PARAMETER_TEST_MARK, responseMsg);
			returnMap.put(MessageKeys.RESPONSE_MAP_PARAMETER_STATUS_CODE, "false");			
		}		
		return returnMap;
	}

	@Override
	public boolean testInterface(String requestUrl) {
		
		return false;
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
	
	/************************************************************************************************/		
	private String sendSocketMsg(String ip, int port, String request, int connectimeOut, int soTimeOut) {
		Socket socket = new Socket();
		StringBuilder responseMsg = new StringBuilder("");
		PrintWriter pw = null;
		BufferedReader br = null;
		
		try {
			socket.connect(new InetSocketAddress(ip, port), connectimeOut);
			socket.setSoTimeout(soTimeOut);
			
			//输出流
	        pw = new PrintWriter(socket.getOutputStream());  
	        //输入流  
	        br = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
	        
	        pw.write(request);  
            pw.flush();  
            socket.shutdownOutput(); 
            
            String reply = null;
            
            while (((reply = br.readLine()) != null)) {  
            	responseMsg.append(reply);
	        }           			
		} catch (Exception e) {
			LOGGER.debug("Send Socket msg to [" + ip + ":" + port + "] Fail！", e);
			responseMsg = new StringBuilder("Send Socket msg to [" + ip + ":" + port + "] Fail:" + e.getMessage());
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					LOGGER.warn("IOException", e);
				}  
            try {
				socket.close();
			} catch (IOException e) {
				LOGGER.warn("IOException", e);
			}
		}		
		return responseMsg.toString();
	}

}
