package yi.master.coretest.message.test.mock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import yi.master.business.advanced.bean.InterfaceMock;
import yi.master.business.advanced.bean.config.mock.MockRequestValidateConfig;
import yi.master.business.advanced.bean.config.mock.MockResponseConfig;
import yi.master.business.advanced.service.InterfaceMockService;
import yi.master.constant.SystemConsts;
import yi.master.util.FrameworkUtil;
import yi.master.util.PracticalUtils;
import yi.master.util.cache.CacheUtil;

/**
 * socket类型的mock服务
 * @author xuwangcheng
 * @version 20180731
 *
 */
public class MockSocketServer {
	
	private static final Logger logger = Logger.getLogger(MockSocketServer.class);
	
	private static final InterfaceMockService mockService = (InterfaceMockService) FrameworkUtil.getSpringBean(InterfaceMockService.class);
	
	private Integer mockId;
	
	private MockRequestValidateConfig validateConfig;
	
	private MockResponseConfig mockConfig;
	
	private ServerSocket socket;
	
	private String errorMsg;

	public MockSocketServer(Integer mockId) throws Exception {
		super();
		this.mockId = mockId;
		
		InterfaceMock interfaceMock = mockService.get(mockId);
		if (interfaceMock == null) {
			throw new Exception("Mock信息不存在!");
		}
		
		validateConfig = MockRequestValidateConfig.getInstance(interfaceMock.getRequestValidate());
		mockConfig = MockResponseConfig.getInstance(interfaceMock.getResponseMock());
		
		boolean flag = true;
		while (flag) {
			try {
				socket = new ServerSocket(PracticalUtils.getRandomNum(65535, 10025));
				flag = false;
			} catch (Exception e) {
				
				logger.error("创建Socket server 出错, 等待重新创建!", e);
			}			
		}
		new Thread(new Runnable() {			
			@Override
			public void run() {
				
				start();
			}
		}).start();
		
		CacheUtil.getSocketServers().put(mockId, this);
	}

	public MockSocketServer() {
		super();
	}
	
	private void start() {
		while (!socket.isClosed()) {
			Socket connection =null; 
			
			try {
				connection = socket.accept();
							
				String requestMessage = readMessageFromClient(connection.getInputStream());
				logger.debug("MockSocketServer " + getMockUrl() + " 接收到数据：" + requestMessage);
				String responseMsg = validateConfig.validate(null, requestMessage);
				if ("true".equals(responseMsg)) {								
					responseMsg = mockConfig.generate(null, requestMessage);			
				} else if (StringUtils.isNotBlank(mockConfig.getExampleErrorMsg())) {
					responseMsg = PracticalUtils.replaceGlobalVariable(mockConfig.getExampleErrorMsg().replace("${errorMsg}", responseMsg), null);
				}
				
				writeMsgToClient(connection.getOutputStream(), responseMsg);
				logger.debug("MockSocketServer " + getMockUrl() + " 回复数据：" + responseMsg);
				connection.close();
			} catch (Exception e) {
				logger.error("Socket Server " + getMockUrl() + " 处理socket请求出错!", e);
			} finally {
				if (connection != null) {
					try {
						connection.close();
					} catch (IOException e) {
						logger.warn("IOException", e);
					}
				}
			}
		}
	}	
	
	public void stop() {
		try {
			if (socket != null) {
				socket.close();
			}	
			CacheUtil.getSocketServers().remove(mockId);
		} catch (IOException e) {
			logger.error("关闭Socket server 失败！", e);
		}
	}
	
	
	/** 
     * 读取客户端信息 
     * @param inputStream 
     */  
    private String readMessageFromClient(InputStream inputStream) throws IOException {  
        Reader reader = new InputStreamReader(inputStream);  
        BufferedReader br=new BufferedReader(reader);  
        String a = null;  
        StringBuilder requestMessage = new StringBuilder();
        while(StringUtils.isNotBlank((a = br.readLine()))){  
        	requestMessage.append(a);
        }
        
        return requestMessage.toString();
    }  
  
    /** 
     * 响应客户端信息 
     * @param outputStream 
     * @param string 
     */  
    private void writeMsgToClient(OutputStream outputStream, String string) throws IOException {  
        Writer writer = new OutputStreamWriter(outputStream);  
        writer.append(string);  
        writer.flush();  
        writer.close();  
    }
	
    public void updateConfig(String settingType, String settingValue) {
    	if ("responseMock".equals(settingType)) {
    		this.mockConfig = MockResponseConfig.getInstance(settingValue);
    	}
    	if ("requestValidate".equals(settingType)) {
    		this.validateConfig = MockRequestValidateConfig.getInstance(settingValue);
    	}
    }
    
	public void setMockId(Integer mockId) {
		this.mockId = mockId;
	}
	
	public Integer getMockId() {
		return mockId;
	}
	
	public ServerSocket getSocket() {
		return socket;
	}
	
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	public String getErrorMsg() {
		return errorMsg;
	}
	
	public String getMockUrl() {
		String ip = "";
		
		try {
			InetAddress addr = InetAddress.getLocalHost();  
	        ip = addr.getHostAddress().toString();
		} catch (Exception e) {
			logger.error("获取服务器本地地址失败,使用配置选项!", e);
			String homeUrl = CacheUtil.getSettingValue(SystemConsts.GLOBAL_SETTING_HOME);
			ip = homeUrl.substring(homeUrl.indexOf("/") + 2, homeUrl.lastIndexOf(":"));
		}
		
		return ip + ":" + socket.getLocalPort();
	}
	
}
