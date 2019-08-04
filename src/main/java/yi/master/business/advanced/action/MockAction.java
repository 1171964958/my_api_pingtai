package yi.master.business.advanced.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import yi.master.business.advanced.bean.InterfaceMock;
import yi.master.business.advanced.bean.config.mock.MockRequestValidateConfig;
import yi.master.business.advanced.bean.config.mock.MockResponseConfig;
import yi.master.business.advanced.service.InterfaceMockService;
import yi.master.constant.SystemConsts;
import yi.master.util.PracticalUtils;
import yi.master.util.cache.CacheUtil;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 接口mock处理类<br>
 * 处理入参验证和返回模拟
 * @author xuwangcheng
 * @version 20180612
 *
 */
@Controller
@Scope("prototype") 
public class MockAction extends ActionSupport {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private InterfaceMockService interfaceMockService;
	
	private static final Logger logger = Logger.getLogger(MockAction.class);
	
	private String responseMsg;
	
	private String requestMsg = "";

	public String execute() {
		HttpServletRequest request = ServletActionContext.getRequest();
		String homeUrl = CacheUtil.getSettingValue(SystemConsts.GLOBAL_SETTING_HOME);
		String uri = request.getRequestURI().replace(homeUrl.substring(homeUrl.lastIndexOf("/")), "").substring(5);
		InterfaceMock mock = interfaceMockService.findByMockUrl(uri);
		
		MockRequestValidateConfig config = MockRequestValidateConfig.getInstance(mock.getRequestValidate());
		
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html; charset=UTF-8");
				
		StringBuilder requestMessageB = new StringBuilder();
		Reader reader = null;
		BufferedReader br = null;
		try {
			reader = request.getReader();
			br = new BufferedReader(reader);
			String line = null;
			while((line = br.readLine()) != null) {
				requestMessageB.append(line);
			}			
		} catch (Exception e) {
			
			logger.error(request.getRequestURI() + "获取请求报文失败！", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					logger.warn("IOException", e);
				}
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					logger.warn("IOException", e);
				}
			}
		}
		
		requestMsg = requestMessageB.toString();
		
		responseMsg = config.validate(request, requestMsg);
		MockResponseConfig mockConfig = MockResponseConfig.getInstance(mock.getResponseMock());			
		if ("true".equals(responseMsg)) {								
			responseMsg = mockConfig.generate(response, requestMsg);			
		} else if (StringUtils.isNotBlank(mockConfig.getExampleErrorMsg())) {
			responseMsg = PracticalUtils.replaceGlobalVariable(mockConfig.getExampleErrorMsg().replace("${errorMsg}", responseMsg), null);
		}
		PrintWriter out = null;	
		try {
			out = response.getWriter();
			out.print(responseMsg);
			out.flush();
		} catch (Exception e) {
			
			logger.error("返回出参报文出错", e);
		} finally {
			if (out != null) {
				out.close();
			}
		}
		
		mock.setCallCount(mock.getCallCount() + 1);
		interfaceMockService.edit(mock);
		return null;
	}
	
	public String getResponseMsg() {
		return responseMsg;
	}
	
	public String getRequestMsg() {
		return requestMsg;
	}
}
