package yi.master.interceptor;

import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.struts2.StrutsStatics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import yi.master.business.api.action.BaseApiAction;
import yi.master.business.log.service.LogRecordService;
import yi.master.business.system.bean.OperationInterface;
import yi.master.business.user.bean.User;
import yi.master.constant.SystemConsts;
import yi.master.util.MD5Util;
import yi.master.util.PracticalUtils;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * 提供给外部的api接口调用拦截器
 * @author xuwangcheng
 * @version 1.0.0.0,2018.4.1
 */
@SuppressWarnings("serial")
@Controller
public class CallApiInterceptor extends AbstractInterceptor {
	
	private static Logger logger = Logger.getLogger(CallApiInterceptor.class.getName());
	
	@Autowired
	private LogRecordService recordService;
	
	@Override
	public void destroy() {
	}

	@Override
	public void init() {

	}

	@SuppressWarnings("rawtypes")
	@Override
	public String intercept(ActionInvocation arg0) throws Exception {
		User user = null;
		OperationInterface opInterface = null;
		String callUrl = null; 
		String interceptStatus = "0";
		String callType = "1";
		String userHost = null; 
		String browserAgent = null;
		int validateTime = 0;
		int executeTime = 0;
		String requestParams = null;
		String responseParams = null;
		String mark = null;	
		
		ActionContext actionContext = arg0.getInvocationContext();           
		HttpServletRequest request= (HttpServletRequest) actionContext.get(StrutsStatics.HTTP_REQUEST);  
		browserAgent = request.getHeader("User-Agent");
		userHost = PracticalUtils.getIpAddr(request);
		
		//请求接口路径
		callUrl = "api/" + arg0.getProxy().getActionName();
		String timeTag = MD5Util.code(String.valueOf(System.currentTimeMillis()) + new Random().nextInt(10000));
		Map paramMap = arg0.getInvocationContext().getParameters();	
		requestParams = JSONObject.fromObject(paramMap).toString();
		logger.info("[" + timeTag + "]调用API接口" + callUrl + "\n入参：" + requestParams);
		
		String[] tokens = (String[]) paramMap.get("token");
		if (tokens != null && SystemConsts.REQUEST_ALLOW_TOKEN.equals(tokens[0])) {	
			String result = "";
			try {
				result = arg0.invoke();
			} catch (Exception e) {
				logger.error("系统内部错误,请求失败!", e);
				interceptStatus = "6";
				mark = PracticalUtils.getExceptionAllinformation(e);
				recordService.saveRecord(user, opInterface, callUrl, interceptStatus, callType, userHost, browserAgent,
						validateTime, executeTime, requestParams, responseParams, mark);
				throw e;
			}
			
			BaseApiAction action = (BaseApiAction) arg0.getAction();
			responseParams = action.getReturnInfo().toString();
			logger.info("[" + timeTag + "]api接口" + callUrl + "调用成功.出参\n" + responseParams);	
			recordService.saveRecord(user, opInterface, callUrl, interceptStatus, callType, userHost, browserAgent,
					validateTime, executeTime, requestParams, responseParams, mark);
			return result;
		}
		
		logger.info("[" + timeTag + "]token不存在或者不正确,调用api接口" + callUrl + "失败...");
		interceptStatus = "4";
		recordService.saveRecord(user, opInterface, callUrl, interceptStatus, callType, userHost, browserAgent,
				validateTime, executeTime, requestParams, responseParams, mark);
		return "apiTokenValidateFail";
			
	}
}
